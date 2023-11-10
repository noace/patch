package com.kyee.upgrade.cloud.asynpackpatch.impl;

import com.kyee.upgrade.cloud.asynpackpatch.CommitIdExecuteBuild;
import com.kyee.upgrade.cloud.asynpackpatch.IPatchService;
import com.kyee.upgrade.common.domain.GitCheckResult;
import com.kyee.upgrade.common.domain.MavenBuildResult;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.mapper.*;
import com.kyee.upgrade.service.IUpPatchCodelistService;
import com.kyee.upgrade.service.IUpPatchService;
import com.kyee.upgrade.service.IUpProductService;
import com.kyee.upgrade.service.impl.ExecuteSqlRecordService;
import com.kyee.upgrade.utils.*;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.kyee.upgrade.utils.ConstantUtil.GIT_PASSWORD;
import static com.kyee.upgrade.utils.ConstantUtil.GIT_USERNAME;

@Service
public class HisV3PatchServiceImpl implements IPatchService {

    private static final Logger log = LoggerFactory.getLogger(HisV3PatchServiceImpl.class);

    @Value(value = "${ruoyi.profile}")
    private String rootSource;

    @Value(value = "${ruoyi.mavenHome}")
    private String mavenHome;

    @Value(value = "${ruoyi.convertSql}")
    private String convertSql;

    @Value(value = "${ruoyi.detectPath}")
    private String detectPath;

    // 开发库配置
    @Value(value = "${sqlplus.config.hosname}")
    private String hosname;

    @Value(value = "${sqlplus.config.port}")
    private String port;

    @Value(value = "${sqlplus.config.username}")
    private String username;

    @Value(value = "${sqlplus.config.password}")
    private String password;

    @Value(value = "${sqlplus.config.dbname}")
    private String dbname;

    // 标准库11g配置
    @Value(value = "${sqlplus.standardConfig.standardHosname}")
    private String standardHosname;

    @Value(value = "${sqlplus.standardConfig.standardPort}")
    private String standardPort;

    @Value(value = "${sqlplus.standardConfig.standardUsername}")
    private String standardUsername;

    @Value(value = "${sqlplus.standardConfig.standardPassword}")
    private String standardPassword;

    @Value(value = "${sqlplus.standardConfig.standardDbname}")
    private String standardDbname;

    // 标准库12c配置
    @Value(value = "${sqlplus.standard12cConfig.standard12cHosname}")
    private String standard12cHosname;

    @Value(value = "${sqlplus.standard12cConfig.standard12cPort}")
    private String standard12cPort;

    @Value(value = "${sqlplus.standard12cConfig.standard12cUsername}")
    private String standard12cUsername;

    @Value(value = "${sqlplus.standard12cConfig.standard12cPassword}")
    private String standard12cPassword;

    @Value(value = "${sqlplus.standard12cConfig.standard12cDbname}")
    private String standard12cDbname;


    @Autowired
    private UpPatchCodelistMapper patchCodelistMapper;

    @Autowired
    private UpProjectModuleMapper projectModuleMapper;

    @Autowired
    private UpPatchMapper patchMapper;

    @Autowired
    private IUpProductService upProductService;

    @Autowired
    private IUpPatchCodelistService upPatchCodelistService;

    @Autowired
    private IUpPatchService upPatchService;

    @Autowired
    private ExecuteSqlRecordService recordService;

    @Autowired
    private UpProjectProductMapper projectProductMapper;

    @Autowired
    private CommitIdExecuteBuild commitIdExecuteBuild;

    @Autowired
    private UpProductMapper upProductMapper;


    @Autowired
    private UpProjectModuleMapper moduleMapper;

    @Override
    public UpPatch execute(String sourceUrl, String branch, String processId, Long patchId, String repository) throws Throwable {

        // 查询项目的编译方式
        UpProjectProduct result = projectProductMapper.getBuildTypeWithPatchId(patchId);
        UpPatch patch = upPatchService.selectUpPatchById(patchId);

        LogUtil.recordBuildLog("--------------检出代码开始【repository】:【" + repository + "】--------------", patchId, processId);
        if (ConstantUtil.PatchBranch.MASTERBRANCH.getValue().equals(result.getPatchBranch())) {
            executeCheckCode(sourceUrl,branch,processId,patchId, repository);
        } else {
            checkoutCommitId(sourceUrl,branch,processId,patchId, repository, patch.getBranchId());
        }
        LogUtil.recordBuildLog("--------------检出代码成功--------------", patchId, processId);
//        LogUtil.recordCodeCheckoutLog(gitCheckResult, patchId);

        LogUtil.recordBuildLog("--------------编译开始--------------", patchId, processId);
        // 模块编译
        if (ConstantUtil.BuildType.MODULE_BUILD.getValue().equals(result.getBuildType())) {
            JarExecuteBuild(sourceUrl, branch, processId, patchId, patch);
        } else if (ConstantUtil.BuildType.BOOT_BUILD.getValue().equals(result.getBuildType())){
            // Boot编译
            BootExecuteBuild(sourceUrl,branch,processId,patchId, patch);
        } else if (ConstantUtil.BuildType.ALL_BUILD.getValue().equals(result.getBuildType())){
            // 全量编译
            executeBuild(sourceUrl, branch, processId, patchId, patch);
        } else if (ConstantUtil.BuildType.CLASS_BUILD.getValue().equals(result.getBuildType())) {
            ClassBuild(sourceUrl, branch, processId, patchId, patch);
        }
        LogUtil.recordBuildLog("--------------编译成功--------------", patchId, processId);

        LogUtil.recordBuildLog("--------------打包开始--------------", patchId, processId);
        UpPatch tempUpPatch = executePkg(sourceUrl,branch,processId,patchId, result, patch);
        LogUtil.recordBuildLog("--------------打包成功--------------", patchId, processId);

        return tempUpPatch;
    }

    @Override
    public GitCheckResult executeCheckCode(String sourceUrl, String branch, String processId, Long patchId, String repository) throws Throwable{
        // git登录信息
        CredentialsProvider credentialsProvider = GitUtil.getCredentialsProvider(GIT_USERNAME, GIT_PASSWORD);
        GitCheckResult gitCheckResult = GitUtil.checkoutAndPull(sourceUrl, credentialsProvider, branch, repository, processId, patchId);
        return gitCheckResult;
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
     * 公共编译代码
     * @param sourceUrl
     * @param processId
     * @param patchId
     * @throws Throwable
     */
    private void commonExecuteBuild(String sourceUrl, String processId, Long patchId, UpPatch patch) throws Throwable{
        UpProjectModule module = new UpProjectModule();
        module.setProductId(patch.getProductId());
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
     * 全量编译
     * @param sourceUrl
     * @param branch
     * @param processId
     * @param patchId 补丁包管理ID
     * @throws Throwable
     */
    @Override
    public void executeBuild(String sourceUrl, String branch, String processId, Long patchId, UpPatch patch) throws Throwable{
        try {
            commonExecuteBuild(sourceUrl, processId, patchId, patch);
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
     * Boot编译
     * @param sourceUrl
     * @param branch
     * @param processId
     * @param patchId 补丁包管理ID
     */
    @Override
    public void BootExecuteBuild(String sourceUrl, String branch, String processId, Long patchId, UpPatch patch) throws Throwable {
        try {
            LogUtil.recordBuildLog("--------------Boot项目编译开始--------------", patchId, processId);
            commonExecuteBuild(sourceUrl, processId, patchId, patch);
            String hisPom = sourceUrl + File.separator + "his-boot" + File.separator + "pom.xml";
            String command = "clean package";
            LogUtil.recordBuildLog("--------------his-boot mvn clean package 执行--------------", patchId, processId);
            MavenBuildResult cleanPackage = MavenUtil.execute(mavenHome, hisPom, command, patchId, processId);
            if (!cleanPackage.isResultSuccess()) {
                throw new Exception("--------------his-boot mvn clean package 编译失败--------------");
            } else {
                LogUtil.recordBuildLog("--------------Boot项目编译结束--------------", patchId, processId);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("--------------编译失败！【错误详情】：【" + e.toString() + "】--------------");
        }
    }

    /**
     * 模块编译 全量编译
     * @param sourceUrl
     * @param branch
     * @param processId
     * @param patchId 补丁包管理
     */
    @Override
    public void JarExecuteBuild(String sourceUrl, String branch, String processId, Long patchId, UpPatch patch) throws Throwable{

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
     * 模块编译 按需编译
     * @param sourceUrl
     * @param branch
     * @param processId
     * @param patchId 补丁包管理IDff
     */
//    @Override
//    public void JarExecuteBuild(String sourceUrl, String branch, String processId, Long patchId, UpPatch patch) throws Throwable{
//
//        try {
//            LogUtil.recordBuildLog("--------------Jar项目编译开始--------------", patchId, processId);
//
//            // 先编译his-base和his-base-bean模块，这两个模块为基础模块,再his-boot
//            List<UpPatchCodelist> codeList = patchCodelistMapper.selectCodeListByPatchId(patchId);
//            UpProjectModule module = new UpProjectModule();
//            module.setProductId(patch.getProductId());
//            module.setProjectId(patch.getProjectId());
//            // 查询每个产品的模块（和项目有关时再关联项目Id）
//            List<UpProjectModule> upProjectModules = projectModuleMapper.getModuleList(module);
//            if (!CollectionUtils.isEmpty(upProjectModules)) {
//                // 优先编译基础模块
//                List<UpProjectModule> baseModules = upProjectModules.stream().filter(s -> "Y".equals(s.getBaseModule())).collect(Collectors.toList());
//                if(!CollectionUtils.isEmpty(baseModules)) {
//                    for (UpProjectModule baseModule : baseModules) {
//
//                        String name = baseModule.getModuleName();
//                        LogUtil.recordBuildLog("--------------编译基础模块：" + name + "--------------", patchId, processId);
//                        String path = sourceUrl + File.separator + name + File.separator;
//                        File pathFile = new File(path);
//                        String pom = path + "pom.xml";
//                        File pomFile = new File(pom);
//                        if (pathFile.exists() && pomFile.exists()) {
//                            LogUtil.recordBuildLog("--------------" + name + ": mvn clean install 执行--------------", patchId, processId);
//                            String command = "clean install";
//                            MavenBuildResult result = MavenUtil.execute(mavenHome, pom, command, patchId, processId);
//                            if (!result.isResultSuccess()) {
//                                throw new Exception("--------------" + name + ": mvn clean install 执行失败！--------------");
//                            }
//                        }
//                    }
//                }
//                // 已编译的模块
//                List<String> builtModules = new ArrayList<>();
//                for (UpProjectModule mod : upProjectModules) {
//                    String name = mod.getModuleName();
//                    // 代码列表里有该模块的名字则编译否则不编译。his-base和his-boot在之前和之后编译
//                    if ("Y".equals(mod.getBaseModule())) {
//                        continue;
//                    }
//                    for (UpPatchCodelist patchCodelist : codeList) {
//                        String codePath = patchCodelist.getCodePath();
//                        if(StringUtils.isNotEmpty(codePath) && codePath.endsWith(".jar") && codePath.contains(mod.getJarVersion())) {
//                            String sd = "-" + mod.getJarVersion();
//                            String headname = codePath.substring(codePath.lastIndexOf("/") + 1, codePath.indexOf(sd));
//                            if (name.contains(headname)) {
//
//                                // 如果包含/则说明模块有子目录,编译外层pom模块
//                                if (name.contains("/")) {
//                                    name = name.substring(0,name.indexOf("/"));
//                                }
//                                if (builtModules.contains(name)) {
//                                    continue;
//                                }
//
//                                String path = sourceUrl + File.separator + name + File.separator;
//                                File pathFile = new File(path);
//                                String pom = path + "pom.xml";
//                                File pomFile = new File(pom);
//                                if (pathFile.exists() && pomFile.exists()) {
//                                    LogUtil.recordBuildLog("--------------编译业务模块：" + name + "--------------", patchId, processId);
//                                    LogUtil.recordBuildLog("--------------" + name + ": mvn clean install 执行--------------", patchId, processId);
//                                    String command = "clean install";
//                                    MavenBuildResult result = MavenUtil.execute(mavenHome, pom, command, patchId, processId);
//                                    if (!result.isResultSuccess()) {
//                                        throw new Exception("--------------" + name + ": mvn clean install 执行失败！--------------");
//                                    }
//                                    builtModules.add(name);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            String hisPom = sourceUrl + File.separator + "his-boot" + File.separator + "pom.xml";
//            String command = "clean package";
//            LogUtil.recordBuildLog("--------------his-boot mvn clean package 执行--------------", patchId, processId);
//            MavenBuildResult cleanPackage = MavenUtil.execute(mavenHome, hisPom, command, patchId, processId);
//            if (!cleanPackage.isResultSuccess()) {
//                throw new Exception("--------------his-boot mvn clean package 编译失败--------------");
//            } else {
//                LogUtil.recordBuildLog("--------------Jar项目编译结束--------------", patchId, processId);
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//            throw new Exception("--------------Jar项目编译失败！【错误详情】：【" + e.toString() + "】--------------");
//        }
//    }

    /**
     * CLASS编译
     * @param sourceUrl
     * @param branch
     * @param processId
     * @param patchId 补丁包管理IDff
     */
    @Override
    public void ClassBuild(String sourceUrl, String branch, String processId, Long patchId, UpPatch patch) throws Throwable{

        try {
            LogUtil.recordBuildLog("--------------Class项目编译开始--------------", patchId, processId);

            // 先编译his-base和his-base-bean模块，这两个模块为基础模块,再his-boot
            List<UpPatchCodelist> codeList = patchCodelistMapper.selectCodeListByPatchId(patchId);
            UpProduct upProduct = upProductMapper.selectUpProductById(patch.getProductId());
            String productShortName = upProduct.getProductShortName();

            UpProjectModule module = new UpProjectModule();
            module.setProductId(patch.getProductId());
            module.setProjectId(patch.getProjectId());
            // 查询每个产品的模块（和项目有关时再关联项目Id）
            List<UpProjectModule> upProjectModules = projectModuleMapper.getModuleList(module);
            if (!CollectionUtils.isEmpty(upProjectModules)) {
                // 优先编译基础模块
                List<UpProjectModule> baseModules = upProjectModules.stream().filter(s -> "Y".equals(s.getBaseModule())).collect(Collectors.toList());
                if(!CollectionUtils.isEmpty(baseModules)) {
                    for (UpProjectModule baseModule : baseModules) {

                        String name = baseModule.getModuleName();
                        LogUtil.recordBuildLog("--------------编译基础模块：" + name + "--------------", patchId, processId);
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

                for (UpProjectModule mod : upProjectModules) {

                    String name = mod.getModuleName();
                    // 代码列表里有该模块的名字则编译否则不编译。his-base和his-boot在之前和之后编译
                    if ("Y".equals(mod.getBaseModule())) {
                        continue;
                    }
                    for (UpPatchCodelist patchCodelist : codeList) {
                        String codePath = patchCodelist.getCodePath();
                        if(StringUtils.isNotEmpty(codePath) && !codePath.replaceAll("\r|\n", "").startsWith(productShortName + "/src") && !codePath.endsWith(".SQL") && !codePath.endsWith(".sql")) {
                            String headname = codePath.substring(codePath.indexOf("/") + 1, codePath.indexOf("/src"));
                            if (name.contains(headname)) {
                                String path = sourceUrl + File.separator + name + File.separator;
                                File pathFile = new File(path);
                                String pom = path + "pom.xml";
                                File pomFile = new File(pom);
                                if (pathFile.exists() && pomFile.exists()) {

                                    LogUtil.recordBuildLog("--------------编译业务模块：" + name + "--------------", patchId, processId);

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
                }
            }
            String hisPom = sourceUrl + File.separator + "his-boot" + File.separator + "pom.xml";
            String command = "clean package";
            LogUtil.recordBuildLog("--------------his-boot mvn clean package 执行--------------", patchId, processId);
            MavenBuildResult cleanPackage = MavenUtil.execute(mavenHome, hisPom, command, patchId, processId);
            if (!cleanPackage.isResultSuccess()) {
                throw new Exception("--------------his-boot mvn clean package 编译失败--------------");
            } else {
                LogUtil.recordBuildLog("--------------Class项目编译结束--------------", patchId, processId);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("--------------Class项目编译失败！【错误详情】：【" + e.toString() + "】--------------");
        }
    }

    /**
     *
     * @param sourceUrl workspace路径
     * @param branch 分支号
     * @param processId 处理Id
     * @param patchId 补丁包登记Id
     * @return
     */
    @Override
    public UpPatch executePkg(String sourceUrl,String branch,String processId,Long patchId,UpProjectProduct result, UpPatch patch)  throws Throwable{
        try {
            //根据patchId 获取 codelist
            UpPatchCodelist prarmUpPatchCodelist = new UpPatchCodelist();
            prarmUpPatchCodelist.setPatchId(patchId);
            List<UpPatchCodelist> UpPatchCodelistResult = upPatchCodelistService.selectUpPatchCodelistList(prarmUpPatchCodelist);

            checkPatchAndCodeList(patch, UpPatchCodelistResult, processId);

            return createPath(sourceUrl, branch, processId, patch, UpPatchCodelistResult, result);
        }catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("【打包失败】：【" + e.getMessage() + "】--------------");
        }
    }

    private void checkPatchAndCodeList(UpPatch upPatch,List<UpPatchCodelist> upPatchCodelistResult, String processId) throws Exception {
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

    private UpPatch createPath(String sourceUrl,String branch,String processId
            ,UpPatch upPatch,List<UpPatchCodelist> upPatchCodelistResult,UpProjectProduct result) throws Exception{

        UpProduct upProduct = upProductService.selectUpProductById(upPatch.getProductId());
        List<String> codeList = new ArrayList<String>();
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
            codeList.add(tempEntity.getCodePath().replaceAll("\r|\n", ""));
            codeMap.put(tempEntity.getCodeId(), tempEntity.getCodePath().replaceAll("\r|\n", ""));
            LogUtil.recordBuildLog(tempEntity.getCodePath(), upPatch.getPatchId(), processId);
        }
        LogUtil.recordBuildLog("--------------记录代码路径结束--------------", upPatch.getPatchId(), processId);
        rootPath = getSourcePath(upProduct.getProductCode());
        File rootPathFile = new File(rootPath);
        if(!rootPathFile.exists()){
            rootPathFile.mkdirs();
        }

        String[] codeArray = new String[codeList.size()];
        codeList.toArray(codeArray);

        boolean jar = false;
        // 打包根路径名称： upPatchPackage
        String strTempPath = sourceUrl + File.separator + "target" + File.separator + "upPatchPackage" + File.separator;

        // Jar项目根路径名称：his-boot/upPatchPackage
        if (ConstantUtil.BuildType.MODULE_BUILD.getValue().equals(Objects .isNull(result) ? null : result.getBuildType()) || ConstantUtil.BuildType.BOOT_BUILD.getValue().equals(result.getBuildType())) {
            strTempPath = sourceUrl + File.separator + "his-boot" + File.separator + "target" + File.separator + "upPatchPackage" + File.separator;
            jar = true;
        }

        clearAndCreateTempDir(strTempPath);
        tempSourcePath = strTempPath;
        List<String> complierList = new ArrayList<>();

        // 模块编译  // TODO 区分class方式
        if (!ConstantUtil.BuildType.CLASS_BUILD.getValue().equals(result.getBuildType())) {
            complierList = copySourceToTempDir(sourceUrl, tempSourcePath, codeMap, upPatch, processId, mergePackageFlag, jar);
        } else {
            // TODO 复制编译后的路径到目标文件夹下
            complierList = commitIdCopySourceToTempDir(sourceUrl, tempSourcePath, codeMap, upPatch, processId, mergePackageFlag);
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
            afterCopySource(zipFileName, tempSourcePath,comments,topic,complierList, jiraList, configContent);
        }else {
            jiraList.add(upPatch.getJiraNo());
            if (StringUtils.isNotEmpty(demandNo)) {
                zipFileName = "PATCH_" + demandNo.trim() + "_" + jiraTaskNo + "["+ productCode +"][SQL" + sqlFlag + "]_" + buildTime +".ZIP";
            } else {
                zipFileName = "PATCH_" + jiraTaskNo+"["+ productCode +"][SQL" + sqlFlag + "]_" + buildTime +".ZIP";
            }
            afterCopySource(zipFileName, tempSourcePath,comments,topic,complierList, jiraList, configContent);
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
     * CommitId方式-复制模块下的编译文件到临时目录
     * @param sourceUrl
     * @param tempSourcePath
     * @param codeMap
     * @param upPatch
     * @param processId
     * @param mergePackageFlag
     * @param jar
     * @return
     * @throws Exception
     */
    public List<String> commitIdCopySourceToTempDir(String sourceUrl,String tempSourcePath,Map<Long, String> codeMap, UpPatch upPatch, String processId, String mergePackageFlag) throws Exception {

        String localsubPath = "";
        Long patchId = upPatch.getPatchId();
        Integer productId = upPatch.getProductId();
        Integer projectId = upPatch.getProjectId();
        String createBy = upPatch.getCreateBy();
        LogUtil.recordBuildLog("--------------记录编译后的代码路径开始--------------", patchId, processId);
        List<String> complierList = new ArrayList<>();

        // 获取模块名
        UpProjectModule projectModule = new UpProjectModule();
        projectModule.setProductId(productId);
        projectModule.setProjectId(projectId);
        List<UpProjectModule> modules = moduleMapper.selectUpProjectModuleList(projectModule);
        List<String> moduleNames = modules.stream().map(UpProjectModule::getModuleName).collect(Collectors.toList());

        for (Long codeId : codeMap.keySet()) {
            boolean jar = false;
            String tepStr = codeMap.get(codeId);
            tepStr = tepStr.trim().replaceAll("\\\\", "/");
            if (!DotNetToJavaStringHelper.isNullOrEmpty(tepStr)) {
                UpPatchCodelist codelist = new UpPatchCodelist();
                codelist.setCodeId(codeId);
                String projectName = getProjectNameByCodeList(tepStr, patchId, processId);

                if (tepStr.contains("src/main/webapp/")) {
                    jar = true;

                    for (String moduleName : moduleNames) {
                        if (tepStr.contains(moduleName + "/")) {
                            tepStr = tepStr.replace(moduleName + "/","");
                            break;
                        }
                    }
                }

                if (tepStr.startsWith(projectName + "/src")) {
                    jar = true;
                }

                String sourcePath = commitIdExecuteBuild.commitIdReplaceFilePath(tepStr, patchId, processId);
                String path = commitIdExecuteBuild.sourceReplaceFilePath(tepStr, patchId, processId);
                if (tepStr.toLowerCase().endsWith(".java")) {
                    localsubPath = subPath(ConstantUtil.CodeType.CODE);
                    List<String> javaCompilePath = commitIdExecuteBuild.commitIdCopyJavaFile(sourceUrl, tempSourcePath,
                                                                    tepStr,
                                                                    sourcePath,
                                                                    path,
                                                                    projectName,
                                                                    localsubPath,
                                                                    patchId,
                                                                    processId,
                                                                    jar);
                    // 存编译后的codeList对象
                    codelist.setCompilePath(StringUtils.join(javaCompilePath, ","));
                    complierList.addAll(javaCompilePath);
                } else if (tepStr.toLowerCase().endsWith(".sql")) {
                    localsubPath = subPath(ConstantUtil.CodeType.SQL);
                    String sqlCompilePath = copySqlFile(sourceUrl, tempSourcePath, tepStr, projectName, localsubPath,
                                                                    patchId,
                                                                    processId,
                                                                    mergePackageFlag,
                                                                    createBy);
                    codelist.setCompilePath(sqlCompilePath);
                    complierList.add(sqlCompilePath);
                } else {
                    localsubPath = subPath(ConstantUtil.CodeType.CODE);
                    String otherCompilePath = commitIdExecuteBuild.copyCodeFile(sourceUrl, tempSourcePath, tepStr,
                                                                    sourcePath,
                                                                    path,
                                                                    projectName,
                                                                    localsubPath,
                                                                    patchId,
                                                                    processId,
                                                                    jar);
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

    /**
     * 主分支方式-复制编译后的文件到临时目录
     * @param sourceUrl
     * @param tempSourcePath
     * @param codeMap
     * @param upPatch
     * @param processId
     * @param mergePackageFlag
     * @param jar
     * @return
     * @throws Exception
     */
    public List<String> copySourceToTempDir(String sourceUrl,String tempSourcePath,Map<Long, String> codeMap, UpPatch upPatch, String processId, String mergePackageFlag, boolean jar) throws Exception {
        String localsubPath = "";
        Long patchId = upPatch.getPatchId();
        String createBy = upPatch.getCreateBy();
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
                if (tepStr.toLowerCase().endsWith(".java")) {
                    localsubPath = subPath(ConstantUtil.CodeType.CODE);
                    List<String> javaCompilePath = copyJavaFile(
                                                    sourceUrl,
                                                    tempSourcePath,
                                                    tepStr,
                                                    projectName,
                                                    localsubPath,
                                                    patchId,
                                                    processId,
                                                        jar);
                    // 存编译后的codeList对象
                    codelist.setCompilePath(StringUtils.join(javaCompilePath,"," ));
                    complierList.addAll(javaCompilePath);
                } else if (tepStr.toLowerCase().endsWith(".sql")){
                    localsubPath = subPath(ConstantUtil.CodeType.SQL);
                    String sqlCompilePath = copySqlFile(
                                                    sourceUrl, tempSourcePath, tepStr,
                                                    projectName, localsubPath, patchId,
                                                    processId, mergePackageFlag, createBy);
                    codelist.setCompilePath(sqlCompilePath);
                    complierList.add(sqlCompilePath);
                } else {
                    localsubPath = subPath(ConstantUtil.CodeType.CODE);
                    String otherCompilePath = copyCodeFile(
                                                    sourceUrl, tempSourcePath, tepStr,
                                                    projectName, localsubPath, patchId,
                                                    processId,
                                                        jar);
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

            // 暂时这里添加lis
            path = path.replace(  "lis/db/", "db/");
            path = path.replace(  "lis/sql/", "sql/");

            // auth_inter
            path = path.replace(  "auth_inter/SQL/", "SQL/");
        }else if(path.toLowerCase().endsWith(".java")){
            path = path.replace("src/main/java", "WEB-INF/classes");
            path = path.replace("src", "WEB-INF/classes");
        }else{
            if(path.indexOf("WebContent") !=-1){
                path = path.replace("WebContent","");
            }else if(path.indexOf("src/main/webapp/") != -1){
                path = path.replace("src/main/webapp/","");
            }else if(path.indexOf("src/main/resources/") != -1){
                path = path.replace("src/main/resources/","WEB-INF/classes/");
            }else if(path.indexOf("tar.xz") != -1){
                //默认不处理
            }else if(path.indexOf("src/main/java") != -1){
                path = path.replace("src/main/java", "WEB-INF/classes");
            }else{
                LogUtil.recordBuildLog("代码列表路径错误，或者路径有没有考虑到的情况【"+path+"】", patchId, processId);
                throw new Exception("代码列表路径错误，或者路径有没有考虑到的情况");
            }
        }
        return path;
    }

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

    private List<String> copyJavaFile(String sourceUrl,String tempSourcePath,
            String tepStr, String projectName,String localsubPath,Long patchId, String processId, boolean jar) throws Exception {
        String currentDir = "";
        String fileName = tepStr.substring(tepStr.lastIndexOf("/")+1,tepStr.length()).replace(".java", "");
        String localPath =  tempSourcePath + localsubPath ;
        String sourceRootPath = sourceUrl + File.separator + "target" + File.separator + projectName;
        if (jar) {
            sourceRootPath = sourceUrl + File.separator + "his-boot" + File.separator + "target" + File.separator + projectName;
        }

        currentDir = tepStr.substring(0,tepStr.lastIndexOf("/"));
        File currentDirFile = new File(localPath + currentDir);
        if(!currentDirFile.exists()){
            currentDirFile.mkdirs();
        }
        if(sourceRootPath.endsWith(projectName) && currentDir.startsWith(projectName)){
            sourceRootPath = sourceRootPath.substring(0,sourceRootPath.lastIndexOf(projectName));
        }
        String sourceFilePath = sourceRootPath + currentDir;
//        String sourcePath = sourceFilePath.replaceAll("\r|\n", "");
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
                        // his\WEB-INF\classes\com\cpinfo\his\web\inpNurseStation\ExecuteAdviceAction.class

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
            log.error("资源文件不存在。"+tepStr);
            LogUtil.recordBuildLog("资源文件不存在【" + sourceFilePath + "】", patchId, processId);
            throw new Exception("资源文件不存在。"+tepStr);
        }
        return compilePaths;
    }

    public String copySqlFile(String sourceUrl,
                               String tempSourcePath,
                               String tmpStr,
                               String projectName,
                               String localsubPath,
                               Long patchId,
                               String processId,
                               String mergePackageFlag,
                               String createBy
    ) throws Exception {
        String fileName = tmpStr.substring(tmpStr.lastIndexOf("/") + 1);
        String localPath =  tempSourcePath + localsubPath ;
        String foldName = "";
        if(tmpStr.indexOf("0SYS") > -1){
            foldName = "0SYSTEM";
        }
        if(tmpStr.indexOf("0TABLE") > -1 || tmpStr.indexOf("2DATA") > -1 || tmpStr.indexOf("1DATA") > -1){
            foldName = "1TABLE_DATA";
        }
        if(tmpStr.indexOf("1VIEW") > -1 || tmpStr.indexOf("2FUNCTION") > -1 || tmpStr.indexOf("2VIEW_FUNCTION") > -1 || tmpStr.indexOf("2VIEW_FUNC") > -1){
            foldName = "2VIEW_FUNCTION";
        }
        if(tmpStr.indexOf("3PROCEDURE") > -1 || tmpStr.indexOf("4TRIGGER") > -1){
            foldName = "3PROCEDURE_TRIGGER";
        }
        String sourceFilePath = sourceUrl + File.separator + tmpStr;
//        String sourcePath = sourceFilePath.replaceAll("\r|\n", "");
        // G:\workspace3\his_v3\sql/1.YLY/0DDL/3PROCEDURE/201709181503_F_IN_LIST.SQL

        File sourceFile = new File(sourceFilePath);
        if(sourceFile.exists()){
            // SQL检查，执行SQL, 合包跳过检查
            if (!"Y".equals(mergePackageFlag)) {
                String path = "2VIEW_FUNC" + File.separator + "PROJECT";
                if(!sourceFilePath.contains(path)) {

                    detectSql(fileName, sourceFilePath, patchId, processId);
                } else {
                    log.info("略过2VIEW_FUNC/PROJECT目录下的SQL检查");
                    LogUtil.recordBuildLog("--------------略过2VIEW_FUNC/PROJECT目录下的SQL检查--------------", patchId, processId);
                }
                // SQL记录
                recordService.sqlRecordService(patchId, fileName, createBy);
            }

            String realPath = RuoYiConfig.getProfile() + "/SQL/";
            File file = new File(realPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File copySql = new File(realPath + fileName);
            FileUtil.copyFile(sourceFile, copySql,null);
            String destPath = localPath + foldName;
            File destFile = new File(destPath);
            if(!destFile.exists()){
                destFile.mkdirs();
            }
            String destFilePath = destPath + File.separator + fileName;
            File targetFile = new File(destPath + File.separator + fileName);
            FileUtil.copyFile(sourceFile, targetFile,null);

            String compilePath = "";
            if (OSValidator.isWindows()) {
                compilePath = destFilePath.split("upPatchPackage\\\\")[1];
            } else {
                compilePath = destFilePath.split("upPatchPackage/")[1];
            }
            return compilePath;
        }else{
            LogUtil.recordBuildLog("资源文件不存在【" + sourceFilePath + "】", patchId, processId);
            log.error("资源文件不存在。"+tmpStr);
            throw new Exception("资源文件不存在。"+tmpStr);
        }
    }

    /**
     * 名称是否为大写
     * @param name
     * @return
     */
    private static boolean isUpperCase(String name) throws Exception{
        try {
            for(int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (Character.isLowerCase(c)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new Exception("SQL脚本文件名异常,请检查脚本名称：" + name);
        }
    }

    /**
     * 遍历查找END;下一行不为空的值
     * @param num 行数
     * @param sourceFilePath
     * @return
     * @throws IOException
     */
    private void findContent(String num, String sourceFilePath, Long patchId, String processId) throws Exception {

        int number = Integer.parseInt(num);
//      sed -n '106p' convertSql/CONVERT_202211181547_CREATE_PTD_TODO_DISPENSE.SQL
        String nextContent = "";
        int line = number + 1;
        while (StringUtils.isEmpty(nextContent)) {
            String nextScript = "sed -n '" + line + "p' " + sourceFilePath;
            log.info("nextScript：" + nextScript);
            String[] nextLine = {"/bin/sh", "-c", nextScript};
            nextContent = CommandUtil.run(nextLine);
            line++;
        }

        LogUtil.recordBuildLog("--------------当前【END;】后的内容: 【" + nextContent +"】--------------", patchId, processId);
        if(!"/".equals(String.valueOf(nextContent.replaceAll("\n", "").charAt(0)))) {
            throw new Exception("SQL脚本不符合规范【END;】后必须跟结束标记【/】。第" + line + "行，错误。当前【END;】后内容: 【" + nextContent +"】");
        }
    }

    /**
     * 存储过程是否有结束标识'/'
     * @param result
     * @param sourceFilePath
     *
     *
     * "21:END;\n" +
     * "38:END;EXECUTE IMMEDIATE;\n" +
     * "39:END;"
     *
     * "21:END;--END;/"
     *
     * "21:---END;END;/"
     *
     */
    private void endSign(String result, String sourceFilePath, Long patchId, String processId) throws Exception {

        // 校验SQL文件内容，存过必须以/结尾，否则程序等待输入，挂起
        // END;后必须跟/
        LogUtil.recordBuildLog("--------------检测存储过程结束标记开始--------------", patchId, processId);

        log.info("result：" + result);
        LogUtil.recordBuildLog("--------------检测含有END;的结果行--------------", patchId, processId);
        LogUtil.recordBuildLog("--------------" + result + "--------------", patchId, processId);
        String resultStr = result.toUpperCase();

        // END;校验开始
        String[] arrs = resultStr.split("\n");
        // 多行END;
        if (arrs.length > 1) {
            for (String arr : arrs) {
                String[] end = arr.split("END;");
                // 是否被注释
                String[] nums = end[0].split(":");
                if (nums.length > 1) {
                    // 被备注过的 END; 跳过
                    if (nums[1].startsWith("-")) {
                        continue;
                    }
                }
                // 多行中同一行没有其他内容
                if (end.length == 1) {
                    findContent(nums[0], sourceFilePath, patchId, processId);
                // 同一行有其他内容
                } else {
                    if(!"/".equals(String.valueOf(end[1].charAt(0)))) {
                        throw new Exception("SQL脚本不符合规范【END;】后必须跟结束标记【/】。第" + nums[0] + "行错误。当前【END;】后内容: 【" + end[1] +"】");
                    }
                    LogUtil.recordBuildLog("--------------当前【END;】后的内容: 【" + end[1] +"】--------------", patchId, processId);
                }
            }

        // 只有一行END;
        } else {
            String[] content = arrs[0].split("END;");
            // 同一行除了END;还有其他值 "21:END;--END;/"
            // 被备注过的 END; 跳过
            String[] nums = content[0].split(":");
            if (nums.length > 1) {
                // 被备注过的 END; 跳过
                if (nums[1].startsWith("-")) {
                    return;
                }
            }

            if (content.length > 1) {
                char ch = content[1].charAt(0);
                if(!"/".equals(String.valueOf(ch))) {
                    throw new Exception("SQL脚本不符合规范【END;】后必须跟结束标记【/】。第" + nums[0] + "行，错误。当前【END;】后内容: 【" + content[1] +"】");
                }
                LogUtil.recordBuildLog("--------------当前【END; 】后的内容: 【" + content[1] +"】--------------", patchId, processId);
            } else {
                // 同一行只有END;
                findContent(nums[0], sourceFilePath, patchId, processId);
            }
        }
        LogUtil.recordBuildLog("--------------检测存储过程结束标记结束--------------", patchId, processId);
    }

    /**
     * 1.脚本文件名全部大写，后缀全部为'.SQL'
     * 2.文件名前缀精确到分的时间，yyyyMMddHHmm
     * 3.SQL文件必须以exit;结尾
     * 4.SQL文件存放目录是否正确
     *   0DDL
     *      0TABLE 存放DDL脚本;  create, drop, alter, truncate
     *      2VIEW_FUNC 存放视图和函数;
     *      3PROCEDURE 存放存储过程;
     *   1DATA
     *      放置基础数据的脚本，添加数据、修改数据等
     *
     *
     * 检查SQL，执行SQL
     * @param fileName SQL文件名
     * @param sourceFilePath SQL文件路径
     * @param patchId
     * @param processId
     * @throws Exception
     */
    private void detectSql(String fileName, String sourceFilePath, Long patchId, String processId) throws Exception {

        LogUtil.recordBuildLog("--------------检测SQL开始--------------", patchId, processId);
        log.info("--------------检测SQL开始--------------");
        UpPatch upPatch = patchMapper.selectUpPatchById(patchId);
        if (!"Y".equals(upPatch.getThirdPartView())) {
            if (fileName.contains(" ")) {
                log.info("--------------SQL脚本文件名包含空格，请修改后重试: " + fileName +"--------------");
                throw new Exception("SQL脚本文件名包含空格，请修改后重试: " + fileName);
            }

            // 校验SQL文件名称是否正确
            boolean upperCase = isUpperCase(fileName);
            if (!upperCase) {
                log.info("--------------SQL脚本文件名包含小写，请修改后重试: " + fileName +"--------------");
                throw new Exception("SQL脚本文件名包含小写，请修改后重试: " + fileName);
            }

            // 校验文件名时间格式
            if(!(fileName.split("_")[0].length() == 12)) {
                log.info("--------------SQL脚本文件名不符合命名规范【yyyyMMddHHmm_XXX.SQL】，请参考脚本规范后重试：" + fileName +"--------------");
                throw new Exception("SQL脚本文件名不符合命名规范【yyyyMMddHHmm_XXX.SQL】，请参考脚本规范后重试: " + fileName);
            }

            // 校验SQL文件是否存放对应目录
            String[] arr = sourceFilePath.split("/");
            String sqlDir = arr[arr.length-2];
            if (StringUtils.isNotEmpty(sqlDir)) {
                String[] nameArr = fileName.split("_");
                // 存放DDL的目录
                List<String> ddlName = Arrays.asList("CREATE", "DROP", "ALTER", "TRUNCATE");
                if (sqlDir.equals(ConstantUtil.SQLDIR.TABLE.getValue())) {
                    if(!ddlName.contains(nameArr[1])) {
                        throw new Exception("SQL脚本文件存放目录不正确【0TABLE下只能存放DDL语句】或SQL文件名不规范，请参考脚本规范后重试: " + fileName);
                    }
                }

                // 存放视图和函数
                List<String> viewAndFuncName = Arrays.asList("VIEW", "FUNC");
                if (sqlDir.equals(ConstantUtil.SQLDIR.VIEW_FUNC.getValue())) {
                    if(!viewAndFuncName.contains(nameArr[1])) {
                        throw new Exception("SQL脚本文件存放目录不正确【2VIEW_FUNC下只能存放视图和函数语句】或SQL文件名不规范，请参考脚本规范后重试: " + fileName);
                    }
                }

                // 存放存储过程
//            String procedureName = "PROC";
                List<String> names = Arrays.asList("PROC", "PKG", "SEQ", "TRI");
                if (sqlDir.equals(ConstantUtil.SQLDIR.PROCEDURE.getValue())) {
                    if (!names.contains(nameArr[1])) {
                        throw new Exception("SQL脚本文件存放目录不正确【3PROCEDURE下只能存放存储过程语句,序列,包,触发器】或SQL文件名不规范，请参考脚本规范后重试: " + fileName);
                    }
                }

                // 存放基础数据
                List<String> dataName = Arrays.asList("INSERT", "UPDATE", "DELETE");
                if (sqlDir.equals(ConstantUtil.SQLDIR.DATA.getValue())) {
                    if(!dataName.contains(nameArr[1])) {
                        throw new Exception("SQL脚本文件存放目录不正确【1DATA下只能存放基础数据相关语句】或SQL文件名不规范，请参考脚本规范后重试: " + fileName);
                    }
                }
            }

            if (OSValidator.isUnix()) {
                // 校验SQL文件尾规范 必须以exit;结尾，否则sqlplus执行sql脚本之后，程序无法退出。
                String tailContent = "";
                int i = 1;
                while (StringUtils.isEmpty(tailContent)){
                    String content = "tail -n " + i + " "  + sourceFilePath;
                    String[] tailContentScript = {"/bin/sh", "-c", content};
                    tailContent = CommandUtil.run(tailContentScript);
                    i++;
                }
                log.info("--------------SQL脚本文件尾内容：" + tailContent + "--------------", patchId, processId);
                LogUtil.recordBuildLog("--------------SQL脚本文件尾内容: " + tailContent +"--------------", patchId, processId);

                if (!(StringUtils.isNotEmpty(tailContent) && tailContent.toLowerCase().contains("exit;"))) {
                    throw new Exception("SQL脚本不符合规范【文件末尾最后一行必须以exit;结尾】，请参考脚本规范后重试。当前文件尾行: 【" + tailContent+"】");
                }

                // 校验存过END;后是否有/,是否结束(弃用，不太完美)
//                String endPoint = "grep -in 'end;' "  + sourceFilePath;
//                String[] tailContentScript = {"/bin/sh", "-c", endPoint};
//                String result = CommandUtil.run(tailContentScript);
//                if (StringUtils.isNotEmpty(result)) {
//                    endSign(result, sourceFilePath, patchId, processId);
//                }

                // 新方案，强制退出  echo exit | sqlplus HIS_DEV/HIS_DEV@121.4.131.241:52773/orcl @END.SQL

                // SQL文件编码检测
                LogUtil.recordBuildLog("--------------SQL路径: " + sourceFilePath +"--------------", patchId, processId);
                //            String path = ResourceUtils.getFile("classpath:shell/decodeDetect.py").getPath();
                log.info("获取检测脚本路径：" + detectPath);
                String[] detectSql = new String[]{};
                if (OSValidator.isWindows()) {
                    detectSql = new String[]{"cmd", "/c", "python " + detectPath + " " + sourceFilePath};
                }
                if (OSValidator.isUnix()) {
                    detectSql = new String[]{"/bin/sh", "-c", "python " + detectPath + " " + sourceFilePath};
                }
                String message = CommandUtil.run(detectSql);
                log.info("-----------------------------------------SQL脚本: " + "python" + detectPath + " " + sourceFilePath + "-----------------------------------------");
                log.info("-----------------------------------------SQL编码: " + message + "-----------------------------------------");
                if (StringUtils.isNotEmpty(message) && (message.contains(ConstantUtil.EncodeType.GB2312.getValue()) || message.contains(ConstantUtil.EncodeType.ISO_8859_1.getValue()))) {
                    LogUtil.recordBuildLog("--------------SQL编码正确：" + message + "--------------", patchId, processId);
                    log.info("------------------------------------SQL编码正确-----------------------------------------");
                } else {
                    log.info("------------------------------------SQL编码错误-----------------------------------------" + message);
                    throw new Exception("SQL脚本【"+ fileName + "】【" + message + "】编码错误，请修改后重试");
                }

                LogUtil.recordBuildLog("--------------检测SQL结束--------------", patchId, processId);

                // SQL转码
                log.info("------------------------------------转换编码开始-----------------------------------------");
                LogUtil.recordBuildLog("--------------转换SQL编码开始--------------", patchId, processId);
                // iconv -f encoding -t encoding inputfile -o outputfile  转换文件编码
                String name = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1);
                String convertName = "CONVERT_" + name;
                LogUtil.recordBuildLog("--------------转换编码后的SQL名称: " + convertName + "--------------", patchId, processId);
                String convertSqlPath = convertSql + File.separator + convertName;
                String convertSql = "iconv -f GB2312 -t UTF-8 " + sourceFilePath + " -o " + convertSqlPath;
                log.info("--------------转换SQL脚本：" + convertSql + "--------------", patchId, processId);
                String[] convertScript = {"/bin/sh", "-c", convertSql};
                String convertSqlMsg = "";
                try {
                    convertSqlMsg = CommandUtil.run(convertScript);
                    log.info("------------------------------------转换编码成功-----------------------------------------");
                    LogUtil.recordBuildLog("--------------转换SQL编码成功--------------", patchId, processId);
                } catch (Exception e) {
                    log.info("------------------------------------转换编码失败-----------------------------------------" + e.getMessage());
                    throw new Exception("转换SQL编码失败." + convertSqlMsg);
                }
                log.info("------------------------------------转换编码结束-----------------------------------------");
                LogUtil.recordBuildLog("--------------转换SQL编码结束 --------------", patchId, processId);


                // 火神版现场SQL往DEV执行
                // 查询火神版现场
                List<UpProjectProduct> upProjectProducts = projectProductMapper.selectUpProjectProductList(new UpProjectProduct());

                // TODO 多线程优化
                if (!CollectionUtils.isEmpty(upProjectProducts)) {
                    for (UpProjectProduct pp : upProjectProducts) {
                        if(pp.getProjectId().equals(upPatch.getProjectId()) && pp.getProductId().equals(upPatch.getProductId())) {

                            if ("1".equals(pp.getExcuteDevFlag())) {
                                // 向开发库执行SQL脚本
                                executeSQL(convertSqlPath, patchId, processId);
                            }

                            if ("1".equals(pp.getExcuteFlag())) {
                                // 向标准库11g执行SQL脚本
                                executeStandardSQL(convertSqlPath, patchId, processId);

                                // 向标准库12c执行SQL脚本
                                executeStandard12cSQL(convertSqlPath, patchId, processId);
                            } else if("2".equals(pp.getExcuteFlag())){
                                if (sourceFilePath.contains(ConstantUtil.SQLDIR.TABLE.getValue())) {
                                    // 向标准库11g部分执行SQL脚本
                                    executeStandardSQL(convertSqlPath, patchId, processId);

                                    // 向标准库12c部分执行SQL脚本
                                    executeStandard12cSQL(convertSqlPath, patchId, processId);
                                }
                            }
                        }
                    }
                }
            } else {
                LogUtil.recordBuildLog("--------------检测SQL结束--------------", patchId, processId);
            }
        }
    }

    /**
     * 执行开发库SQL
     * @param convertSqlPath 转换编码后的SQL文件名
     * @param patchId
     * @param processId
     * @throws Exception
     */
    private void executeSQL(String convertSqlPath, Long patchId, String processId) throws Exception {

        LogUtil.recordBuildLog("--------------执行开发库SQL开始 --------------", patchId, processId);
        log.info("------------------------------------执行开发库SQL开始-----------------------------------------");
        // 执行SQL
        String excuteScript = "echo exit | sqlplus " + username + "/" + password + "@" + hosname + ":" + port + "/" + dbname + " @" + convertSqlPath;
        log.info("------------------------------------SQL脚本：-----------------------------------------" + excuteScript);
        String[] excuteSql = {"/bin/sh", "-c", excuteScript};
        String excuteSqlMsg = CommandUtil.run(excuteSql);
        if (StringUtils.isNotEmpty(excuteSqlMsg) && (excuteSqlMsg.contains("ORA-") || excuteSqlMsg.contains("SP2-"))) {
            // SQL执行失败
            log.info("------------------------------------执行开发库SQL失败-----------------------------------------" + excuteSqlMsg);

            // TODO sqlplus执行结果格式化输出到页面
            throw new Exception("执行开发库SQL失败,请查看错误详情：" + excuteSqlMsg);
        } else if(StringUtils.isNotEmpty(excuteSqlMsg) && (!excuteSqlMsg.toUpperCase().contains("END!") && !excuteSqlMsg.toUpperCase().contains("END !"))) {
            log.info("------------------------------------执行标准库11gSQL异常，请检查后重试，可能原因：SQL没有【/】,SQL执行未结束-----------------------------------------" + excuteSqlMsg);

            throw new Exception("执行标准库11gSQL异常，请检查后重试，可能原因：SQL没有【/】,SQL执行未结束,请查看错误详情：" + excuteSqlMsg);
        }
        LogUtil.recordBuildLog("执行开发库SQL成功：" + excuteSqlMsg, patchId, processId);
        log.info("------------------------------------执行开发库SQL成功-----------------------------------------");
    }

    /**
     * 执行标准库11gSQL
     * @param convertSqlPath 转换编码后的SQL文件名
     * @param patchId
     * @param processId
     * @throws Exception
     */
    private void executeStandardSQL(String convertSqlPath, Long patchId, String processId) throws Exception {

        LogUtil.recordBuildLog("--------------执行标准库11gSQL开始 --------------", patchId, processId);
        log.info("------------------------------------执行标准库11gSQL开始-----------------------------------------");
        // 执行SQL
        String excuteStandardScript = "echo exit | sqlplus " + standardUsername + "/" + standardPassword + "@" + standardHosname + ":" + standardPort + "/" + standardDbname + " @" + convertSqlPath;
        log.info("------------------------------------SQL脚本：-----------------------------------------" + excuteStandardScript);
        String[] excuteSql = {"/bin/sh", "-c", excuteStandardScript};
        String excuteSqlMsg = CommandUtil.run(excuteSql);
        if (StringUtils.isNotEmpty(excuteSqlMsg) && (excuteSqlMsg.contains("ORA-") || excuteSqlMsg.contains("SP2-"))) {
            // SQL执行失败
            log.info("------------------------------------执行标准库11gSQL失败-----------------------------------------" + excuteSqlMsg);

            throw new Exception("执行标准库11gSQL失败,请查看错误详情：" + excuteSqlMsg);
        } else if(StringUtils.isNotEmpty(excuteSqlMsg) && (!excuteSqlMsg.toUpperCase().contains("END!") && !excuteSqlMsg.toUpperCase().contains("END !"))) {
            log.info("------------------------------------执行标准库11gSQL异常，请检查后重试，可能原因：SQL没有【/】,SQL执行未结束-----------------------------------------" + excuteSqlMsg);

            throw new Exception("执行标准库11gSQL异常，请检查后重试，可能原因：SQL没有【/】,SQL执行未结束,请查看错误详情：" + excuteSqlMsg);
        }
        LogUtil.recordBuildLog("执行标准库11gSQL成功：" + excuteSqlMsg, patchId, processId);
        log.info("------------------------------------执行标准库11gSQL成功-----------------------------------------");
    }

    /**
     * 执行标准库12cSQL
     * @param convertSqlPath 转换编码后的SQL文件名
     * @param patchId
     * @param processId
     * @throws Exception
     */
    private void executeStandard12cSQL(String convertSqlPath, Long patchId, String processId) throws Exception {

        LogUtil.recordBuildLog("--------------执行标准库12cSQL开始 --------------", patchId, processId);
        log.info("------------------------------------执行标准库12cSQL开始-----------------------------------------");
        // 执行SQL
        String excuteStandardScript = "echo exit | sqlplus " + standard12cUsername + "/" + standard12cPassword + "@" + standard12cHosname + ":" + standard12cPort + "/" + standard12cDbname + " @" + convertSqlPath;
        log.info("------------------------------------SQL脚本：-----------------------------------------" + excuteStandardScript);
        String[] excuteSql = {"/bin/sh", "-c", excuteStandardScript};
        String excuteSqlMsg = CommandUtil.run(excuteSql);
        if (StringUtils.isNotEmpty(excuteSqlMsg) && (excuteSqlMsg.contains("ORA-") || excuteSqlMsg.contains("SP2-"))) {
            // SQL执行失败
            log.info("------------------------------------执行标准库12cSQL失败-----------------------------------------" + excuteSqlMsg);

            throw new Exception("执行标准库12cSQL失败,请查看错误详情：" + excuteSqlMsg);
        } else if(StringUtils.isNotEmpty(excuteSqlMsg) && !excuteSqlMsg.toUpperCase().contains("END!") && !excuteSqlMsg.toUpperCase().contains("END !")) {
            log.info("------------------------------------执行标准库11gSQL异常，请检查后重试，可能原因：SQL没有【/】,SQL执行未结束-----------------------------------------" + excuteSqlMsg);

            throw new Exception("执行标准库11gSQL异常，请检查后重试，可能原因：SQL没有【/】,SQL执行未结束,请查看错误详情：" + excuteSqlMsg);
        }
        LogUtil.recordBuildLog("执行标准库12cSQL成功：" + excuteSqlMsg, patchId, processId);
        log.info("------------------------------------执行标准库12cSQL成功-----------------------------------------");
    }

    private String copyCodeFile(
            String sourceUrl, String tempSourcePath,
            String tepStr, String projectName,String localsubPath,Long patchId, String processId, boolean jar) throws Exception {
        String currentDir = tepStr.substring(0,tepStr.lastIndexOf("/"));
        String fileName = tepStr.substring(tepStr.lastIndexOf("/")+1,tepStr.length());
        String localPath =  tempSourcePath + localsubPath;
        String sourceRootPath = sourceUrl + File.separator + "target" + File.separator + projectName;
        if (jar) {
            sourceRootPath = sourceUrl + File.separator + "his-boot" + File.separator + "target" + File.separator + projectName;
        }
        File currentDirFile = new File(localPath + currentDir);
        if(!currentDirFile.exists()){
            currentDirFile.mkdirs();
        }
        if(sourceRootPath.endsWith(projectName) && currentDir.startsWith(projectName)){
            sourceRootPath = sourceRootPath.substring(0,sourceRootPath.lastIndexOf(projectName));
        }
        String sourceFilePath = sourceRootPath + tepStr;
//        String sourcePath = sourceFilePath.replaceAll("\r|\n", "");
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

    private void afterCopySource(String zipFileName, String target, String projectComment,String topic, List<String> codeArray,
                                 List<String> jiraList, String configContent){
        boolean isSuccssCreateReadMe;
        boolean isSuccessWriteReadMe;
        String readMePath;
        readMePath = target + "readMe.txt";
        isSuccssCreateReadMe = createReadMe(readMePath);
        if(!isSuccssCreateReadMe){
            log.error("创建readMe.txt文件失败！");
        }
        isSuccessWriteReadMe = writeTxtFile(zipFileName,projectComment,topic,readMePath,codeArray, jiraList, configContent);
        if(!isSuccessWriteReadMe){
            log.error("写入readMe.txt文件失败！");
            log.error("zipFileName："+zipFileName);
            log.error("projectComment："+projectComment);
        }
    }

    private boolean createReadMe(
            String readMePath) {
        boolean flag = false;
        File filename = new File(readMePath);
        if (!filename.exists()) {
            try {
                filename.createNewFile();
                flag = true;
            } catch (IOException e) {
                flag = false;
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error("创建readMe文件失败",e);
            }
        }
        return flag;
    }

    private boolean writeTxtFile(String packageName, String packageComment,String topic,
                                String readMePath, List<String> codeArray, List<String> jiraList, String configContent) {
        // 先读取原有文件内容，然后进行写入操作
        boolean flag = false;
        StringBuilder sb = new StringBuilder();
        for (String jira : jiraList) {
            sb.append(jira).append("\r\n");
        }
        packageComment = packageComment.replace(",\r\n", "\r\n");
        topic = topic.replace(",\r\n", "\r\n");

        String filein = "单任务包：" + packageName + "\r\n"
                + "--------------------------------" + "\r\n"
                + "该单任务包版本信息："+ "\r\n"
                + topic + "\r\n"
                + "--------------------------------" + "\r\n"
                + "备注："+ "\r\n"
                + packageComment + "\r\n"
                + "--------------------------------" + "\r\n"
                + "任务列表："+ "\r\n"
                + sb.toString() + "\r\n"
                + "--------------------------------" + "\r\n"
                + "修改配置文件内容："+ "\r\n"
                + configContent + "\r\n"
                + "--------------------------------" + "\r\n"
                + "代码列表："+ "\r\n";

        for (String code : codeArray) {
            filein = filein + code + "\r\n";
        }

        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            // 文件路径
            File file = new File(readMePath);
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            // 保存该文件原有的内容
            for (int j = 1; (temp = br.readLine()) != null; j++) {
                buf = buf.append(temp);
                // System.getProperty("line.separator")
                // 行与行之间的分隔符 相当于“\n”
                buf = buf.append(System.getProperty("line.separator"));
            }
            buf.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            flag = true;
        } catch (IOException e1) {
            log.error("写入readme文件失败",e1);
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("写入readme文件失败",e);
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("写入readme文件失败",e);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("写入readme文件失败",e);
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("写入readme文件失败",e);
                }
            }
        }
        return flag;
    }

    private String getSourcePath(String projectCode){
        return rootSource + File.separator + projectCode + File.separator;
    }

}
