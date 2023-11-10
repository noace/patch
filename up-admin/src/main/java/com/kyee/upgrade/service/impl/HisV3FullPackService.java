package com.kyee.upgrade.service.impl;

import com.kyee.upgrade.common.domain.GitCheckResult;
import com.kyee.upgrade.common.domain.MavenBuildResult;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.mapper.*;
import com.kyee.upgrade.utils.*;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.kyee.upgrade.utils.ConstantUtil.GIT_PASSWORD;
import static com.kyee.upgrade.utils.ConstantUtil.GIT_USERNAME;

@Service
public class HisV3FullPackService {
    private static final Logger log = LoggerFactory.getLogger(HisV3FullPackService.class);

    @Autowired
    private UpProductMapper productMapper;

    @Autowired
    private UpCommandMapper commandMapper;

    @Autowired
    private UpPatchMapper patchMapper;

    @Autowired
    private UpProjectModuleMapper projectModuleMapper;

    @Autowired
    private UpProjectProductMapper ppMapper;

    @Value(value = "${ruoyi.profile}")
    private String rootSource;

    @Value(value = "${ruoyi.mavenHome}")
    private String mavenHome;


    /**
     * HIS全量包打包
     * 开发端：
     * 0.点击全量包按钮 -> 命令表插入全量包指令 -> 定时器扫描全量包指令 -> 全量包打包开始
     * 1.拉取项目代码
     * 2.编译打包HIS
     * 3.全量包打包结束
     * 升级端：
     * 1.备份配置文件
     * 2.升级全量包
     */
    @Transactional
    public void hisV3FullPack(UpPatch patch) {

        // 获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();
        patch.setCreateBy(sysUser.getUserName());
        patch.setUpdateBy(sysUser.getUserName());
        patch.setCreateTime(new Date());
        patch.setUpdateByUserId(sysUser.getUserId());
        patch.setJiraNo("全量包");
        patch.setTopic("");

        // 状态改为打包中
        patch.setPatchStatus(ConstantUtil.UpPatchStatus.PKGING.getValue());
        // 插入登记补丁数据
        patchMapper.insertUpPatch(patch);
        // 获取插入id
        Long patchId = patchMapper.getLastInsertId();
        patch.setPatchId(patchId);
        // 命令表
        fullPackCommand(patch);
    }

    /**
     * 插入版本包命令
     * @param patch
     */
    private void fullPackCommand(UpPatch patch) {
        UpCommand upCommand = new UpCommand();
        upCommand.setCommandInfo(ConstantUtil.UpPatchStatus.REGISTERED.name());
        upCommand.setCommandType(ConstantUtil.PatchType.RELEASE.name());
        upCommand.setProductId(patch.getProductId());
        upCommand.setProjectId(patch.getProjectId());
        upCommand.setPatchId(patch.getPatchId());
        upCommand.setCreateBy(patch.getCreateBy());
        upCommand.setUpdateBy(patch.getUpdateBy());
        upCommand.setCreateTime(new Date());
        upCommand.setUpdateTime(new Date());
        upCommand.setDelFlag("N");
        //插入构建队列
        commandMapper.insertUpCommand(upCommand);
    }

    /**
     * 执行版本包打包
     * @param sourceUrl
     * @param branch
     * @param processId
     * @param patchId
     * @param repository
     * @return
     * @throws Throwable
     */
    public UpPatch fullPack(String sourceUrl, String branch, String processId, Long patchId, String repository) throws Throwable {

        // 查询项目的编译方式
        UpProjectProduct projectProduct = ppMapper.getBuildTypeWithPatchId(patchId);

        LogUtil.recordBuildLog("--------------检出代码开始【repository】:【" + repository + "】--------------", patchId, processId);
        executeCheckCode(sourceUrl,branch,processId,patchId, repository);
        LogUtil.recordBuildLog("--------------检出代码成功--------------", patchId, processId);

        LogUtil.recordBuildLog("--------------编译开始--------------", patchId, processId);
        // 全量编译
        if (ConstantUtil.BuildType.ALL_BUILD.getValue().equals(projectProduct.getBuildType())) {
            executeBuild(sourceUrl, processId, patchId, projectProduct);
        // 模块编译
        } else if (ConstantUtil.BuildType.MODULE_BUILD.getValue().equals(projectProduct.getBuildType())) {
            JarExecuteBuild(sourceUrl, processId, patchId);
        // Class编译
        } else if (ConstantUtil.BuildType.CLASS_BUILD.getValue().equals(projectProduct.getBuildType())) {
            JarExecuteBuild(sourceUrl, processId, patchId);
        }
        LogUtil.recordBuildLog("--------------编译成功--------------", patchId, processId);

        LogUtil.recordBuildLog("--------------完整包打包开始--------------", patchId, processId);
        UpPatch tempUpPatch;
        if (ConstantUtil.BuildType.MODULE_BUILD.getValue().equals(projectProduct.getBuildType())) {
            tempUpPatch = executePkgClass(sourceUrl, patchId, processId, projectProduct);
        } else {
            tempUpPatch = executePkg(sourceUrl, patchId, processId, projectProduct);
        }
        LogUtil.recordBuildLog("--------------完整包打包成功--------------", patchId, processId);

        return tempUpPatch;
    }

    /**
     * 全量编译
     * @param sourceUrl
     * @param processId
     * @param patchId
     * @param projectProduct
     * @throws Exception
     */
    public void executeBuild(String sourceUrl,String processId, Long patchId, UpProjectProduct projectProduct) throws Exception {
        try {
            commonExecuteBuild(sourceUrl, processId, patchId, projectProduct);
            LogUtil.recordBuildLog("--------------mvn clean package 执行--------------", patchId, processId);
            String hisPom = sourceUrl + File.separator + "pom.xml";
            String command = "clean package";
            MavenBuildResult cleanPackage = MavenUtil.execute(mavenHome, hisPom, command, patchId, processId);
            if (!cleanPackage.isResultSuccess()) {
                throw new Exception("--------------mvn clean package 执行失败！--------------");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("--------------编译失败！【错误详情】：【" + e.toString() + "】--------------");
        }
    }

    /**
     * 模块编译 全量编译
     * @param sourceUrl
     * @param processId
     * @param patchId 补丁包管理
     */
    public void JarExecuteBuild(String sourceUrl, String processId, Long patchId) throws Throwable{

        try {
            LogUtil.recordBuildLog("--------------Jar项目编译开始--------------", patchId, processId);

            String clean = sourceUrl + File.separator  + "pom.xml";
            String cleanCommand = "clean";
            LogUtil.recordBuildLog("--------------mvn clean 执行中--------------", patchId, processId);
            MavenBuildResult cleanPackage = MavenUtil.execute(mavenHome, clean, cleanCommand, patchId, processId);
            if (!cleanPackage.isResultSuccess()) {
                throw new Exception("--------------mvn clean 编译失败--------------");
            }

            String install = sourceUrl + File.separator  + "pom.xml";
            String installCommand = "install";
            LogUtil.recordBuildLog("--------------mvn install 执行中--------------", patchId, processId);
            MavenBuildResult installPackage = MavenUtil.execute(mavenHome, install, installCommand, patchId, processId);
            if (!installPackage.isResultSuccess()) {
                throw new Exception("--------------mvn install 编译失败--------------");
            } else {
                LogUtil.recordBuildLog("--------------Jar项目编译结束--------------", patchId, processId);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("--------------Jar项目编译失败！【错误详情】：【" + e.toString() + "】--------------");
        }
    }

    /**
     * 执行打包
     * @param sourceUrl
     * @return
     * @throws Exception
     */
    public UpPatch executePkg(String sourceUrl,Long patchId, String processId, UpProjectProduct projectProduct) throws Exception {

        Integer productId = projectProduct.getProductId();
        UpProduct upProduct = productMapper.selectUpProductById(productId);
        UpPatch upPatch = patchMapper.selectUpPatchById(patchId);

        String productCode = upProduct.getProductCode().trim();
        String shortName = upProduct.getProductShortName().trim();
        Date date = new Date();
        String buildTime = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String rootPath = getSourcePath(upProduct.getProductCode());
        File rootPathFile = new File(rootPath);
        if(!rootPathFile.exists()){
            rootPathFile.mkdirs();
        }

        LogUtil.recordBuildLog("--------------处理源码文件开始：--------------", patchId, processId);

        // 打包临时目录： upPatchPackage
        String strTempPath = sourceUrl + File.separator + "target" + File.separator + "upPatchPackage" + File.separator;
        // 打包编译目录
        String packPath = "";

        // Jar项目根路径名称：his-boot/upPatchPackage
        if (ConstantUtil.BuildType.MODULE_BUILD.getValue().equals(projectProduct.getBuildType())) {
            packPath = sourceUrl + File.separator + "his-boot" + File.separator + "target" + File.separator + shortName + File.separator;
        } else if (ConstantUtil.BuildType.ALL_BUILD.getValue().equals(projectProduct.getBuildType())) {
            packPath = sourceUrl + File.separator + File.separator + "target" + File.separator + shortName + File.separator;
        }
        clearAndCreateTempDir(strTempPath);

        // 复制全量包文件到临时目录
        org.aspectj.util.FileUtil.copyFile(new File(packPath), new File(strTempPath + "code" + File.separator + shortName + File.separator));
        LogUtil.recordBuildLog("--------------处理源码文件结束：--------------", patchId, processId);

        LogUtil.recordBuildLog("--------------处理脚本文件开始：--------------", patchId, processId);
        String hisSourceDir = sourceUrl + File.separator + "db" + File.separator;
        File dir = null;
        if (new File(hisSourceDir).exists()) {
            clearAndCreateTempDir(strTempPath + "sql" + File.separator + "1TABLE_DATA");
            clearAndCreateTempDir(strTempPath + "sql" + File.separator + "2VIEW_FUNCTION");
            clearAndCreateTempDir(strTempPath + "sql" + File.separator + "3PROCEDURE_TRIGGER");

            dir = new File(hisSourceDir);
        } else {
            LogUtil.recordBuildLog("--------------脚本文件夹不存在：--------------" + hisSourceDir, patchId, processId);
        }

        if (dir != null) {
            // 遍历源文件夹
            try {
                recursionDir(dir, strTempPath);
            } catch (Exception e) {
                throw new Exception("遍历脚本文件夹失败！");
            }
        }
        LogUtil.recordBuildLog("--------------处理脚本文件结束：--------------", patchId, processId);


        // hisv3_FULL_4.0.4_20230525165649.ZIP
        String zipFileName = productCode + "_FULL_3.0.0_" + buildTime +".ZIP";

        File zipFile = new File(rootPath + zipFileName);
        boolean isSuccessZipFile = ZipFileUtil.ZipFiles(zipFile,new File(strTempPath).listFiles());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(!isSuccessZipFile){
            throw new Exception("文件压缩失败【"+zipFileName+"】");
        }else{
            upPatch.setPatchFileName(zipFileName);
            // 数据库资源地址
            String downloadPath = Constants.RESOURCE_PREFIX + "/" + upProduct.getProductCode() +  "/" + zipFileName;
            upPatch.setPatchFileUrl(downloadPath);
            upPatch.setBuildTime(sdf.format(date));
        }
        return upPatch;
    }

    /**
     * 执行打包Class
     * @param sourceUrl
     * @return
     * @throws Exception
     */
    public UpPatch executePkgClass(String sourceUrl,Long patchId, String processId, UpProjectProduct projectProduct) throws Exception {

        Integer productId = projectProduct.getProductId();
        Integer projectId = projectProduct.getProjectId();
        UpProduct upProduct = productMapper.selectUpProductById(productId);
        UpPatch upPatch = patchMapper.selectUpPatchById(patchId);

        String productCode = upProduct.getProductCode().trim();
        String shortName = upProduct.getProductShortName().trim();
        Date date = new Date();
        String buildTime = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String rootPath = getSourcePath(upProduct.getProductCode());
        File rootPathFile = new File(rootPath);
        if(!rootPathFile.exists()){
            rootPathFile.mkdirs();
        }
        // 打包临时目录： upPatchPackage
        String strTempPath = sourceUrl + File.separator + "target" + File.separator + "upPatchPackage" + File.separator;
        // 打包编译目录
        String packPath = "";

        // Jar项目根路径名称：his-boot/upPatchPackage
        if (ConstantUtil.BuildType.MODULE_BUILD.getValue().equals(projectProduct.getBuildType())) {
            packPath = sourceUrl + File.separator + "his-boot" + File.separator + "target" + File.separator + shortName + File.separator;
        } else if (ConstantUtil.BuildType.ALL_BUILD.getValue().equals(projectProduct.getBuildType())) {
            packPath = sourceUrl + File.separator + File.separator + "target" + File.separator + shortName + File.separator;
        }

        if (!new File(packPath).exists()) {
            throw new Exception("项目编译根路径不存在！" + packPath);
        }

        clearAndCreateTempDir(strTempPath);

        LogUtil.recordBuildLog("--------------复制项目根编译文件开始--------------", patchId, processId);
        // 复制全量包文件到临时目录
        org.aspectj.util.FileUtil.copyFile(new File(packPath), new File(strTempPath + "code" + File.separator + shortName + File.separator));

        LogUtil.recordBuildLog("--------------复制项目根编译文件结束--------------", patchId, processId);

        // 复制模块jar包的class文件到临时目录
        UpProjectModule module = new UpProjectModule();
        module.setProductId(productId);
        module.setProjectId(projectId);
        module.setPackageType("jar");
        List<UpProjectModule> upProjectModules = projectModuleMapper.getModuleList(module);
        if (CollectionUtils.isEmpty(upProjectModules)) {
            throw new Exception("项目模块为空，请检查该项目是否含有模块！");
        }

        String jarVersion = upProjectModules.get(0).getJarVersion();
        List<String> moduleNames = upProjectModules.stream().map(UpProjectModule::getModuleName).distinct().collect(Collectors.toList());

        log.info("---------------------------------------------------校验Jar重复文件START-------------------------------------------------------------");
        LogUtil.recordBuildLog("--------------校验Jar重复文件START--------------", patchId, processId);
        // 校验生成完整包时，jar包拆分成class之后，是否和his-boot下的路径重复，避免覆盖
        // <模块名，重复路径>
        Map<String, List<Map<String, Long>>> map = new HashMap<>();

        // 重复路径
        List<String> repeatPath = new ArrayList<>();

        // 全部文件路径
//        List<String> list = new ArrayList<>();

        List<Map<String, Long>> mapList =  new ArrayList<>();

        // his-boot编译目录
        String bootDir =  sourceUrl + File.separator + "his-boot" + File.separator + "target" + File.separator + shortName + File.separator;

        log.info("---------------------------------------------------复制his-boot编译文件开始-------------------------------------------------------------");
        File bootPathFile = new File(bootDir);
        if (bootPathFile.exists()) {
            File[] files = bootPathFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    mapList.addAll(recursionFileDir(file));
                }
                map.put("his-boot", mapList);
            }
            log.info("---------------------------------------------------复制his-boot编译文件结束-------------------------------------------------------------");
        }

        for (String name : moduleNames) {
            String modulePath = sourceUrl + File.separator + name + File.separator + "target" + File.separator + "classes" + File.separator;
            File pathFile = new File(modulePath);
            // 复制模块文件到临时目录
//            List<String> fileList = new ArrayList<>();

            List<Map<String, Long>> moduleMapList =  new ArrayList<>();

            if (pathFile.exists()) {
                File[] files = pathFile.listFiles();
                if (files != null) {
                    for (File file : files) {
                        moduleMapList.addAll(recursionFileDir(file));
                    }
                }
            } else {
                throw new Exception("找不到模块：" + name);
            }

            // 遍历模块中的文件路径
            /*for (String filePath : fileList) {
                if (repeatPath.contains(filePath)) {
                    path.add(name + "==" + filePath);
                }
                repeatPath.add(filePath);
            }*/
            map.put(name, moduleMapList);
        }

        // <文件名, 模块名>
        Map<String, String> pathMap = new HashMap<>();
        Map<String, Long> longMap = new HashMap<>();
        for (String moduleName : map.keySet()){
            List<Map<String, Long>> fileList = map.get(moduleName);
            for (Map<String, Long> bytesMap : fileList) {
                for (String moduleFile : bytesMap.keySet()) {
                    if (pathMap.containsKey(moduleFile)) {
                        repeatPath.add(moduleName + " >>> " + moduleFile + "-------->>>--------" + bytesMap.get(moduleFile));
                        repeatPath.add(pathMap.get(moduleFile) + " >>> " + moduleFile + "-------->>>--------" + longMap.get(moduleFile));
                    }
                    pathMap.put(moduleFile, moduleName);
                    longMap.put(moduleFile, bytesMap.get(moduleFile));
                }
            }
        }

        if (!repeatPath.isEmpty()) {
            log.info("---------------------------------------------------收集重复文件列表开始-------------------------------------------------------------");
            LogUtil.recordBuildLog("--------------收集重复文件列表开始--------------", patchId, processId);
            log.info(repeatPath.toString());

            for (String path : repeatPath) {
                LogUtil.recordBuildLog(path, patchId, processId);
            }

            log.info("--------------------------------------------------------------------------------------------------------------------------");
            LogUtil.recordBuildLog("------------------------------------", patchId, processId);
            log.info("重复文件总数量：" + repeatPath.size());
            LogUtil.recordBuildLog("重复文件总数量：" + repeatPath.size(), patchId, processId);
            log.info("---------------------------------------------------收集重复文件列表结束-------------------------------------------------------------");
            LogUtil.recordBuildLog("--------------收集重复文件列表结束--------------", patchId, processId);
        } else {
            log.info("---------------------------------------------------没有收集到文件-------------------------------------------------------------");
            LogUtil.recordBuildLog("--------------没有收集到文件--------------", patchId, processId);
        }

        log.info("---------------------------------------------------校验Jar重复文件END-------------------------------------------------------------");
        LogUtil.recordBuildLog("--------------校验Jar重复文件END--------------", patchId, processId);

        if (repeatPath.size() > 0) {
            throw new Exception("Jar项目编译文件拆分后与根项目编译文件路径有冲突！请处理之后，再生成完整包！冲突文件，详见日志！！");
        }

        LogUtil.recordBuildLog("--------------复制项目模块编译文件开始--------------", patchId, processId);
        // 仅Jar模块
        for (String name : moduleNames) {
            String modulePath = sourceUrl + File.separator + name + File.separator + "target" + File.separator + "classes" + File.separator;
            File pathFile = new File(modulePath);
            // 复制模块文件到临时目录
            if (pathFile.exists()) {
                org.aspectj.util.FileUtil.copyFile(new File(modulePath), new File(strTempPath + "code" + File.separator + shortName + File.separator + "WEB-INF" + File.separator +"classes" + File.separator));
                LogUtil.recordBuildLog("--------------复制模块文件【"+ modulePath +"】" + "【" + name + "】--------------", patchId, processId);
            } else {
                throw new Exception("找不到模块：" + name);
            }
        }

        LogUtil.recordBuildLog("--------------复制项目模块编译文件结束--------------", patchId, processId);

        LogUtil.recordBuildLog("--------------复制脚本开始--------------", patchId, processId);
        String hisSourceDir = sourceUrl + File.separator + "db" + File.separator;
        File dir = null;
        if (new File(hisSourceDir).exists()) {
            clearAndCreateTempDir(strTempPath + "sql" + File.separator + "1TABLE_DATA");
            clearAndCreateTempDir(strTempPath + "sql" + File.separator + "2VIEW_FUNCTION");
            clearAndCreateTempDir(strTempPath + "sql" + File.separator + "3PROCEDURE_TRIGGER");
            dir = new File(hisSourceDir);
        } else {
            LogUtil.recordBuildLog("--------------脚本文件夹不存在：--------------" + hisSourceDir, patchId, processId);
        }

        if (dir != null) {
            // 遍历源文件夹
            try {
                recursionDir(dir, strTempPath);
            } catch (Exception e) {
                throw new Exception("遍历脚本文件夹失败！");
            }
        }
        LogUtil.recordBuildLog("--------------复制脚本结束--------------", patchId, processId);

        LogUtil.recordBuildLog("--------------删除模块jar开始--------------", patchId, processId);
        if (!CollectionUtils.isEmpty(moduleNames)) {
            // his/src/main/webapp/WEB-INF/lib/his-boot-3.0.0-master.jar
            for (String name : moduleNames) {
                if (name.contains("/")) {
                    name = name.substring(name.lastIndexOf("/") + 1);
                }
                String path = strTempPath + "code" + File.separator + shortName + File.separator + "WEB-INF" + File.separator + "lib" + File.separator + name + "-" + jarVersion + ".jar";
                File file = new File(path);
                if (file.exists()) {
                    boolean b = FileUtil.delFile(file);
                    if (!b) {
                        throw new Exception("删除模块jar文件失败！" + path);
                    } else {
                        LogUtil.recordBuildLog("--------------已删除jar:" + path + "--------------", patchId, processId);
                    }
                } else {
                    throw new Exception("模块jar文件找不到！" + path);
                }
            }
        }
        LogUtil.recordBuildLog("--------------删除模块jar结束--------------", patchId, processId);

        // hisv3_FULL_4.0.4_20230525165649.ZIP
        String zipFileName = productCode + "_FULL_3.0.0_" + buildTime +".ZIP";

        File zipFile = new File(rootPath + zipFileName);
        boolean isSuccessZipFile = ZipFileUtil.ZipFiles(zipFile,new File(strTempPath).listFiles());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(!isSuccessZipFile){
            throw new Exception("文件压缩失败【"+zipFileName+"】");
        }else{
            upPatch.setPatchFileName(zipFileName);
            // 数据库资源地址
            String downloadPath = Constants.RESOURCE_PREFIX + "/" + upProduct.getProductCode() +  "/" + zipFileName;
            upPatch.setPatchFileUrl(downloadPath);
            upPatch.setBuildTime(sdf.format(date));
        }
        return upPatch;
    }

    /**
     * 获取资源路径
     * @param projectCode
     * @return
     */
    private String getSourcePath(String projectCode){
        return rootSource + File.separator + projectCode + File.separator;
    }

    /**
     * 递归遍历文件夹
     * @param dir
     * @param strTempPath
     * @throws Exception
     */
    private static void recursionDir(File dir, String strTempPath) throws Exception {
        File targetDir = new File(strTempPath + "sql" + File.separator);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    recursionDir(file, strTempPath);
                } else {
                    if (!targetDir.exists()) {
                        boolean mkdirs = targetDir.mkdirs();
                        if (!mkdirs) {
                            throw new Exception("创建文件夹失败！");
                        }
                    }
                    String tmpStr = file.getPath();
                    if(tmpStr.contains("0TABLE") || tmpStr.contains("2DATA") || tmpStr.contains("1DATA")){
                        org.aspectj.util.FileUtil.copyFile(new File(tmpStr), new File(strTempPath + "sql" + File.separator + "1TABLE_DATA"));
                    }
                    if(tmpStr.contains("1VIEW") || tmpStr.contains("2FUNCTION") || tmpStr.contains("2VIEW_FUNCTION") || tmpStr.contains("2VIEW_FUNC")){
                        org.aspectj.util.FileUtil.copyFile(new File(tmpStr), new File(strTempPath + "sql" + File.separator + "2VIEW_FUNCTION"));
                    }
                    if(tmpStr.contains("3PROCEDURE") || tmpStr.contains("4TRIGGER")){
                        org.aspectj.util.FileUtil.copyFile(new File(tmpStr), new File(strTempPath + "sql" + File.separator + "3PROCEDURE_TRIGGER"));
                    }
                }
            }
        }
    }

    /**
     * 递归遍历文件夹
     * @param dir
     * @throws Exception
     */
    private static List<Map<String, Long>> recursionFileDir(File dir) {
//        List<String> fileList = new ArrayList<>();
        List<Map<String, Long>> mapList = new ArrayList<>();

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    mapList.addAll(recursionFileDir(file));
                } else {
                    Map<String, Long> map = new HashMap<>();
                    String path = file.getPath();
                    if (!path.contains("classes")) {
                        continue;
                    }
                    String subPath = path.substring(path.indexOf("classes" + File.separator) + 8);
//                    fileList.add(subPath + "\n");
                    long length = file.length();
                    map.put(subPath, length);
                    mapList.add(map);
                }
            }
        }
        return mapList;
    }

    /**
     * 检出CommitId
     * @param sourceUrl
     * @param branch 主分支
     * @param processId
     * @param patchId
     * @param repository
     * @param branchId 分支commitId
     */
    private void checkoutCommitId(String sourceUrl, String branch, String processId, Long patchId, String repository, String branchId) throws Throwable {

        CredentialsProvider credentialsProvider = GitUtil.getCredentialsProvider(GIT_USERNAME, GIT_PASSWORD);
        GitUtil.checkoutAndFetch(sourceUrl, credentialsProvider, branch, repository, processId, patchId, branchId);
    }

    /**
     * 创建临时目录
     * @param strTempPath
     * @return
     */
    private boolean clearAndCreateTempDir(String strTempPath){
        boolean result = false;
        File tempPathFile = new File(strTempPath);
        if(tempPathFile.exists()){
            FileUtil.delFile(tempPathFile);
            result = true;
        }else{
            tempPathFile.mkdirs();
            result = true;
        }
        return result;
    }

    /**
     * 检出代码
     * @param sourceUrl
     * @param branch
     * @param processId
     * @param patchId
     * @param repository
     * @return
     * @throws Throwable
     */
    public GitCheckResult executeCheckCode(String sourceUrl, String branch, String processId, Long patchId, String repository) throws Throwable {
        // git登录信息
        CredentialsProvider credentialsProvider = GitUtil.getCredentialsProvider(GIT_USERNAME, GIT_PASSWORD);
        GitCheckResult gitCheckResult = GitUtil.checkoutAndPull(sourceUrl, credentialsProvider, branch, repository, processId, patchId);
        return gitCheckResult;
    }

    /**
     * 公共编译代码
     * @param sourceUrl
     * @param processId
     * @param patchId
     * @throws Throwable
     */
    private void commonExecuteBuild(String sourceUrl, String processId, Long patchId, UpProjectProduct projectProduct) throws Throwable{
        UpProjectModule module = new UpProjectModule();
        module.setProductId(projectProduct.getProductId());
        module.setProjectId(projectProduct.getProjectId());
        // 查询每个产品的模块（和项目有关时再关联项目Id）
        List<UpProjectModule> upProjectModules = projectModuleMapper.getModuleList(module);
        if (!CollectionUtils.isEmpty(upProjectModules)) {
            List<String> moduleNames = upProjectModules.stream().map(UpProjectModule::getModuleName).distinct().collect(Collectors.toList());
            for (String name : moduleNames) {
                String path = sourceUrl + File.separator + name + File.separator;
                File pathFile = new File(path);
                String pom = path + "pom.xml";
                File pomFile = new File(pom);
                if (pathFile.exists() && pomFile.exists()) {
                    LogUtil.recordBuildLog("--------------" + name + ": mvn clean install 执行--------------", patchId, processId);
                    String command = "clean install";
                    MavenBuildResult result = MavenUtil.execute(mavenHome, pom, command, patchId, processId);
                    if (!result.isResultSuccess()) {
                        throw new Exception("--------------" + name + ": mvn clean install 执行失败！--------------");
                    }
                }
            }
        }
    }

    /**
     * 父目录
     * @param codeType
     * @return
     * @throws Exception
     */
    private String subPath(ConstantUtil.CodeType codeType) throws Exception{
        String result = "";
        if(codeType.isCode()){
            result = "code" + File.separator;
        }else if(codeType.isSql()){
            result = "sql" + File.separator;
        }else{
            throw new Exception("文件类型未知");
        }
        return result;
    }
}
