package com.kyee.upgrade.cloud.asynpackpatch.impl;

import com.kyee.upgrade.cloud.asynpackpatch.IPatchService;
import com.kyee.upgrade.common.execute.ExecutePatch;
import com.kyee.upgrade.common.patch.CommonPatchMethod;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.mapper.UpPatchCodelistMapper;
import com.kyee.upgrade.mapper.UpProjectModuleMapper;
import com.kyee.upgrade.mapper.UpProjectProductMapper;
import com.kyee.upgrade.service.IUpPatchCodelistService;
import com.kyee.upgrade.service.IUpPatchService;
import com.kyee.upgrade.service.IUpProductService;
import com.kyee.upgrade.utils.*;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述：新定时器项目打包
 * 作者：DangHangHang
 * 时间：2023年06月15日 10:33
 */
@Service
public class HisScheduledServiceImpl implements IPatchService {

    private static final Logger log = LoggerFactory.getLogger(HisScheduledServiceImpl.class);

    @Value(value = "${ruoyi.profile}")
    private String rootSource;

    @Resource
    private ExecutePatch executePatch;

    @Resource
    private IUpPatchService upPatchService;

    @Resource
    private IUpProductService upProductService;

    @Resource
    private UpProjectProductMapper projectProductMapper;

    @Resource
    private IUpPatchCodelistService upPatchCodelistService;

    @Autowired
    private UpPatchCodelistMapper patchCodelistMapper;

    @Autowired
    private UpProjectModuleMapper moduleMapper;

    @Override
    public UpPatch execute(String sourceUrl, String branch, String processId, Long patchId, String repository) throws Throwable {
        // 查询项目的编译方式
        UpProjectProduct projectProduct = projectProductMapper.getBuildTypeWithPatchId(patchId);
        UpPatch patch = executePatch.executeHis(sourceUrl, branch, processId, patchId, repository, projectProduct);
        LogUtil.recordBuildLog("--------------打包开始--------------", patchId, processId);
        UpPatch tempUpPatch = executePkg(sourceUrl,branch,processId,patchId, projectProduct, patch);
        LogUtil.recordBuildLog("--------------打包成功--------------", patchId, processId);

        return tempUpPatch;
    }

    @Override
    public UpPatch executePkg(String sourceUrl, String branch, String processId, Long patchId, UpProjectProduct result, UpPatch patch) throws Throwable {
        try {
            //根据patchId 获取 codelist
            UpPatchCodelist prarmUpPatchCodelist = new UpPatchCodelist();
            prarmUpPatchCodelist.setPatchId(patchId);
            List<UpPatchCodelist> UpPatchCodelistResult = upPatchCodelistService.selectUpPatchCodelistList(prarmUpPatchCodelist);

            checkPatchAndCodeList(patch, UpPatchCodelistResult);

            return createPath(sourceUrl, processId, patch, UpPatchCodelistResult, result);
        }catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("【打包失败】：【" + e.getMessage() + "】--------------");
        }
    }

    private void checkPatchAndCodeList(UpPatch upPatch,List<UpPatchCodelist> upPatchCodelistResult) throws Exception {
        if(upPatch != null){
            if(upPatchCodelistResult != null && upPatchCodelistResult.size() > 0){
                List<String> codePathList = upPatchCodelistResult.stream().map(UpPatchCodelist::getCodePath).collect(Collectors.toList());
                codePathList.removeAll(Collections.singleton(null));
                if(!(CollectionUtils.isNotEmpty(codePathList) && codePathList.size() > 0)){
                    throw new Exception("代码列表为空,打包异常结束");
                }
            }else{
                throw new Exception("代码列表为空,打包异常结束");
            }
        }else{
            throw new Exception("无法根据patchId查到到包信息, 打包异常结束");
        }
    }

    private UpPatch createPath(String sourceUrl,String processId
            ,UpPatch upPatch,List<UpPatchCodelist> upPatchCodelistResult,UpProjectProduct result) throws Exception{

        UpProduct upProduct = upProductService.selectUpProductById(upPatch.getProductId());
        Map<Long, String> codeMap = new HashMap<>();
        String taskNo = upPatch.getJiraNo();
        // 处理掉任务号空格，防止打包后包名带空格
        String jiraTaskNo = taskNo.trim();
        String comments = upPatch.getRemark();
        String topic = upPatch.getTopic();
        String mergeTime = upPatch.getMergeTime();
        String patchBranch = result.getPatchBranch();
        String demandNo = upPatch.getDemandNo();

        String productShortName = upProduct.getProductShortName();
        String upProductCode = upProduct.getProductCode();
        // 处理掉产品编码空格
        String productCode = upProductCode.trim();
        Date date = new Date();
        String buildTime = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
        String zipFileName = "";
        String tempSourcePath = "";
        String rootPath = "";
        String zipFilePath = "";
        boolean isSuccessZipFile = false;
        String sqlFlag = upPatch.getSqlFlag();
        String mergePackageFlag = upPatch.getMergePackageFlag();

        LogUtil.recordBuildLog("--------------记录代码路径开始--------------", upPatch.getPatchId(), processId);
        for(UpPatchCodelist tempEntity: upPatchCodelistResult){
            String codePath = tempEntity.getCodePath().replaceAll("\r|\n", "");
            codeMap.put(tempEntity.getCodeId(), codePath);
            LogUtil.recordBuildLog(tempEntity.getCodePath(), upPatch.getPatchId(), processId);
        }
        LogUtil.recordBuildLog("--------------记录代码路径结束--------------", upPatch.getPatchId(), processId);

        rootPath = getSourcePath(upProduct.getProductCode());
        File rootPathFile = new File(rootPath);
        if(!rootPathFile.exists()){
            rootPathFile.mkdirs();
        }

        // 打包根路径名称： upPatchPackage
        String strTempPath = sourceUrl + File.separator + "target" + File.separator + "upPatchPackage" + File.separator;

        // 从模块配置页面获取项目虚拟目录
        UpProjectModule projectModule = new UpProjectModule();
        projectModule.setProductId(upPatch.getProductId());
        projectModule.setProjectId(upPatch.getProjectId());
        projectModule.setPackageType("war");
        List<UpProjectModule> modules = moduleMapper.selectUpProjectModuleList(projectModule);
        List<String> virtualPathList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(modules)) {
            for (UpProjectModule module : modules) {
                String moduleName = module.getModuleName();
                /*if (moduleName.contains("/")) {
                    String virtualPath = moduleName.substring(moduleName.lastIndexOf("/") + 1);
                    virtualPathList.add(virtualPath);
                }*/
                virtualPathList.add(moduleName);
            }
        }

        CommonPatchMethod.clearAndCreateTempDir(strTempPath);
        tempSourcePath = strTempPath;
        List<String> complierList;

        // 模块编译  // TODO 区分class方式
        if (!ConstantUtil.BuildType.CLASS_BUILD.getValue().equals(result.getBuildType())) {
            complierList = copySourceToTempDir(sourceUrl, tempSourcePath, codeMap, upPatch, processId, virtualPathList);
        } else {
            throw new Exception("新版定时器无需使用Class编译方式，请重新配置编译类型！");
        }

        // 获取补丁包配置文件内容
        String configContent;
        if ("Y".equals(upPatch.getConfigFlag())) {
            configContent = upPatch.getConfigList();
        } else {
            configContent = "无";
        }

        // TODO CommitId打包方式，编译时间取合并时间
        if (ConstantUtil.PatchBranch.COMMITID.getValue().equals(patchBranch)) {
            buildTime = mergeTime.replaceAll(":|-", "").replace(" ", "");
        }

        // TODO 合包判断逻辑
        List<String> jiraList = new ArrayList<>();
        if ("Y".equals(mergePackageFlag)) {
            if (StringUtils.isNotEmpty(demandNo)) {
                zipFileName = "PATCH_" + demandNo.trim() + "_" + jiraTaskNo + "["+ productCode +"][SQL" + sqlFlag + "][M]_" + buildTime +".ZIP";
            } else {
                zipFileName = "PATCH_" + jiraTaskNo+"["+ productCode +"][SQL" + sqlFlag + "][M]_" + buildTime +".ZIP";
            }
            // 查询任务列表
            List<UpPatch> upPatchList = upPatchService.selectUpPatchListByParentPatchId(upPatch.getPatchId());
            jiraList = upPatchList.stream().map(UpPatch::getJiraNo).collect(Collectors.toList());
            CommonPatchMethod.afterCopySource(zipFileName, tempSourcePath,comments,topic,complierList, jiraList, configContent);
        }else {
            jiraList.add(upPatch.getJiraNo());
            if (StringUtils.isNotEmpty(demandNo)) {
                zipFileName = "PATCH_" + demandNo.trim() + "_" + jiraTaskNo + "["+ productCode +"][SQL" + sqlFlag + "]_" + buildTime +".ZIP";
            } else {
                zipFileName = "PATCH_" + jiraTaskNo+"["+ productCode +"][SQL" + sqlFlag + "]_" + buildTime +".ZIP";
            }
            CommonPatchMethod.afterCopySource(zipFileName, tempSourcePath,comments,topic,complierList, jiraList, configContent);
        }

        zipFilePath = rootPath + zipFileName;
        File zipFile = new File(zipFilePath);

        isSuccessZipFile = ZipFileUtil.ZipFiles(zipFile,new File(tempSourcePath).listFiles());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(!isSuccessZipFile){
            throw new Exception("文件压缩失败【"+zipFileName+"】");
        }else{
            upPatch.setPatchFileName(zipFileName);
            // 数据库资源地址
            String downloadPath = Constants.RESOURCE_PREFIX + "/" + upProduct.getProductCode() +  "/" + zipFileName;
            upPatch.setPatchFileUrl(downloadPath);
            upPatch.setBuildTime(sdf.format(date));
            if (ConstantUtil.PatchBranch.COMMITID.getValue().equals(patchBranch)) {
                upPatch.setBuildTime(mergeTime);
            }
        }
        return upPatch;
    }


    /**
     * 主分支方式-复制编译后的文件到临时目录
     * @param sourceUrl
     * @param tempSourcePath
     * @param codeMap
     * @param upPatch
     * @param processId
     * @param virtualPathList
     * @return
     * @throws Exception
     */
    public List<String> copySourceToTempDir(String sourceUrl,String tempSourcePath,Map<Long, String> codeMap, UpPatch upPatch, String processId, List<String> virtualPathList) throws Exception {
        String localsubPath = "";
        Long patchId = upPatch.getPatchId();
        LogUtil.recordBuildLog("--------------记录编译后的代码路径开始--------------", patchId, processId);
        List<String> complierList = new ArrayList<>();

        for (Long codeId : codeMap.keySet()) {
            String tepStr = codeMap.get(codeId);
            tepStr = tepStr.trim().replaceAll("\\\\", "/");
            if(!DotNetToJavaStringHelper.isNullOrEmpty(tepStr)){
                UpPatchCodelist codelist = new UpPatchCodelist();
                codelist.setCodeId(codeId);
                String projectName = getProjectNameByCodeList(tepStr, patchId, processId);
                tepStr = codePathReplace(tepStr, patchId, processId);

                String virtualPath = "";
                for (String path : virtualPathList) {
                    if (tepStr.contains(path)) {
                        virtualPath = path;
                    }
                }

                if (tepStr.toLowerCase().endsWith(".java")) {
                    localsubPath = CommonPatchMethod.subPath(ConstantUtil.CodeType.CODE);
                    List<String> javaCompilePath = copyJavaFile(
                                                    sourceUrl,
                                                    tempSourcePath,
                                                    tepStr,
                                                    projectName,
                                                    localsubPath,
                                                    patchId,
                                                    processId,
                                                    virtualPath);
                    // 存编译后的codeList对象
                    codelist.setCompilePath(StringUtils.join(javaCompilePath,"," ));
                    complierList.addAll(javaCompilePath);
                } else {
                    localsubPath = CommonPatchMethod.subPath(ConstantUtil.CodeType.CODE);
                    String otherCompilePath = copyCodeFile(
                                                    sourceUrl,
                                                    tempSourcePath,
                                                    tepStr,
                                                    projectName,
                                                    localsubPath,
                                                    patchId,
                                                    processId,
                                                    virtualPath);
                    codelist.setCompilePath(otherCompilePath);
                    complierList.add(otherCompilePath);
                }
                // 修改编译后的路径
                patchCodelistMapper.updateUpPatchCodelistByCodeId(codelist);
                LogUtil.recordBuildLog(codelist.getCompilePath(), patchId, processId);
            }
        }
        LogUtil.recordBuildLog("--------------记录编译后的代码路径结束--------------", patchId, processId);
        return complierList;
    }

    private String getProjectNameByCodeList(String codePath, Long patchId, String processId) throws Exception{
        String reuslt = "";
        if(!DotNetToJavaStringHelper.isNullOrEmpty(codePath)){
            codePath = codePath.replaceAll("\\\\", "/");
            if(codePath.indexOf("/") > 0){
                reuslt = codePath.substring(0,codePath.indexOf("/"));
            }else{
                LogUtil.recordBuildLog("代码路径分隔符错误！【"+codePath+"】", patchId, processId);
                throw new Exception("代码路径分隔符错误！"+codePath);
            }
        }else{
            LogUtil.recordBuildLog("代码路径错误！【"+codePath+"】", patchId, processId);
            throw new Exception("代码路径错误！");
        }
        return reuslt.trim();
    }

    private String codePathReplace(String path, Long patchId, String processId) throws Exception{
        //hisv3
        if(path.toLowerCase().endsWith(".sql")){
            path = path.replace(  "his/db/", "db/");
            path = path.replace(  "his/sql/", "sql/");
            // auth_inter
            path = path.replace(  "auth_inter/SQL/", "SQL/");
        }else if(path.toLowerCase().endsWith(".java")){
            path = path.replace("src/main/java", "WEB-INF/classes");
            path = path.replace("src", "WEB-INF/classes");
        }else{
            if(path.contains("WebContent")){
                path = path.replace("WebContent","");
            }else if(path.contains("src/main/webapp/")){
                path = path.replace("src/main/webapp/","");
            }else if(path.contains("src/main/resources/")){
                path = path.replace("src/main/resources/","WEB-INF/classes/");
            }else if(path.contains("tar.xz")){
                //默认不处理
            }else if(path.contains("src/main/java")){
                path = path.replace("src/main/java", "WEB-INF/classes");
            }else{
                LogUtil.recordBuildLog("代码列表路径错误，或者路径有没有考虑到的情况【"+path+"】", patchId, processId);
                throw new Exception("代码列表路径错误，或者路径有没有考虑到的情况");
            }
        }
        return path;
    }

    // code/模块名/代码路径

    private List<String> copyJavaFile(String sourceUrl,String tempSourcePath, String tepStr, String projectName,
                                      String localsubPath,Long patchId, String processId, String virtualPath) throws Exception {
        /*String fileName = tepStr.substring(tepStr.lastIndexOf("/")+1).replace(".java", "");

        // 打包主目录 uppatch/code/
        String localPath =  tempSourcePath + localsubPath ;

        // 编译后的主目录 target/
        String buildVirtualPath = virtualPath.replaceAll("-", "_");
        String sourceRootPath = sourceUrl + File.separator + virtualPath + File.separator + "target" + File.separator + buildVirtualPath;

        // 创建打包文件目录
        String currentDir = tepStr.substring(tepStr.indexOf(projectName + "/"), tepStr.lastIndexOf("/"));
        File currentDirFile = new File(localPath + currentDir);
        if(!currentDirFile.exists()){
            currentDirFile.mkdirs();
        }

        if(currentDir.startsWith(projectName)){
//            sourceRootPath = sourceRootPath.substring(0,sourceRootPath.lastIndexOf(buildVirtualPath));
            currentDir = currentDir.substring(currentDir.indexOf(projectName + "/") + projectName.length());
        }

        // 编译后的文件目录
        String sourceFilePath = sourceRootPath + currentDir;*/
        // 编译后的主目录 target/
        String buildVirtualPath = virtualPath.replaceAll("-", "_");

        String currentDir = buildVirtualPath + tepStr.substring(tepStr.indexOf(projectName + "/") + projectName.length(), tepStr.lastIndexOf("/"));
        String fileName = tepStr.substring(tepStr.lastIndexOf("/") + 1).replace(".java", "");
        String localPath =  tempSourcePath + localsubPath;

        File currentDirFile = new File(localPath + currentDir);
        if(!currentDirFile.exists()){
            currentDirFile.mkdirs();
        }

        String sourceRootPath = sourceUrl + File.separator + virtualPath + File.separator + "target" + File.separator + buildVirtualPath;
        String sourceFilePath = sourceRootPath + currentDir.substring(currentDir.indexOf(buildVirtualPath + "/") + buildVirtualPath.length());

        // 编译后的文件路径
        List<String> compilePaths = new ArrayList<>();
        File file = new File(sourceFilePath);
        if(file.exists()){
            for(File tempFile : file.listFiles()){
                if(tempFile.isFile()){
                    String tempFileName = tempFile.getName();
                    String start = "";
                    String endChar = "";
                    tempFileName = tempFileName.replaceAll("\\\\", "/");
                    if(tempFileName.indexOf("/") != -1){
                        start = "[/]";
                        endChar = "/";
                    }else{
                        start = "";
                    }
                    String compilePath = "";
                    if(tempFileName.endsWith(endChar + fileName + ".class")){
                        //类
                        String destFilePath = currentDirFile.getAbsolutePath() + File.separator + tempFileName;
                        File destFile = new File(destFilePath);
                        FileUtil.copyFile(tempFile, destFile, null);

                        if (OSValidator.isWindows()) {
                            compilePath = destFilePath.split("upPatchPackage\\\\")[1];
                        } else {
                            compilePath = destFilePath.split("upPatchPackage/")[1];
                        }
                        compilePaths.add(compilePath);
                    }else if(tempFileName.matches(".*"+ start +fileName+"[$].*"+".class")){
                        //内部类
                        String destFilePath = currentDirFile.getAbsolutePath() + File.separator + tempFileName;
                        File destFile = new File(destFilePath);
                        FileUtil.copyFile(tempFile, destFile, null);
                        if (OSValidator.isWindows()) {
                            compilePath = destFilePath.split("upPatchPackage\\\\")[1];
                        } else {
                            compilePath = destFilePath.split("upPatchPackage/")[1];
                        }

                        compilePaths.add(compilePath);
                    }
                }
            }
        }else{
            log.error("资源文件不存在："+file.getPath());
            LogUtil.recordBuildLog("资源文件不存在【" + sourceFilePath + "】", patchId, processId);
            throw new Exception("资源文件不存在："+file.getPath());
        }
        return compilePaths;
    }

    private String copyCodeFile(String sourceUrl, String tempSourcePath, String tepStr, String projectName,
                                String localsubPath,Long patchId, String processId, String virtualPath) throws Exception {
        // 编译后的主目录 target/
        String buildVirtualPath = virtualPath.replaceAll("-", "_");

        String currentDir = buildVirtualPath + tepStr.substring(tepStr.indexOf(projectName + "/") + projectName.length(), tepStr.lastIndexOf("/"));
        String fileName = tepStr.substring(tepStr.lastIndexOf("/")+1);
        String localPath =  tempSourcePath + localsubPath;

        File currentDirFile = new File(localPath + currentDir);
        if(!currentDirFile.exists()){
            currentDirFile.mkdirs();
        }

        String sourceRootPath = sourceUrl + File.separator + virtualPath + File.separator + "target" + File.separator + buildVirtualPath;
//        if(sourceRootPath.endsWith(buildVirtualPath) && currentDir.startsWith(buildVirtualPath)){
//            sourceRootPath = sourceRootPath.substring(0,sourceRootPath.lastIndexOf(buildVirtualPath));
//        }
        String sourceFilePath = sourceRootPath + tepStr.substring(tepStr.indexOf(projectName + "/") + projectName.length());
        File sourceFile = new File(sourceFilePath);
        if(sourceFile.exists()){
            String destFilePath = currentDirFile.getAbsolutePath() + File.separator + fileName;
            File destFile = new File(destFilePath);
            FileUtil.copyFile(sourceFile, destFile, null);

            String compilePath = "";
            if (OSValidator.isWindows()) {
                compilePath = destFilePath.split("upPatchPackage\\\\")[1];
            } else {
                compilePath = destFilePath.split("upPatchPackage/")[1];
            }
            return compilePath;

        }else{
            LogUtil.recordBuildLog("资源文件不存在【" + sourceFilePath + "】", patchId, processId);
            log.error("资源文件不存在。"+tepStr);
            throw new Exception("资源文件不存在。"+tepStr);
        }
    }

    private String getSourcePath(String projectCode){
        return rootSource + File.separator + projectCode + File.separator;
    }
}
