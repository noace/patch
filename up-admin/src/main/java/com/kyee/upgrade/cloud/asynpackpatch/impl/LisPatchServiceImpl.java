package com.kyee.upgrade.cloud.asynpackpatch.impl;

import com.kyee.upgrade.cloud.asynpackpatch.IPatchService;
import com.kyee.upgrade.common.build.CommitIdBuildCode;
import com.kyee.upgrade.common.execute.ExecutePatch;
import com.kyee.upgrade.common.patch.CommonPatchMethod;
import com.kyee.upgrade.common.sql.ExecuteCommonSQL;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.mapper.*;
import com.kyee.upgrade.service.IUpPatchCodelistService;
import com.kyee.upgrade.service.impl.ExecuteSqlRecordService;
import com.kyee.upgrade.utils.*;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述：LIS打包实现类
 * 作者：DangHangHang
 * 时间：2023年06月13日 10:22
 */
@Service
public class LisPatchServiceImpl implements IPatchService {
    private static final Logger log = LoggerFactory.getLogger(LisPatchServiceImpl.class);

    @Value(value = "${ruoyi.profile}")
    private String rootSource;

    @Autowired
    private UpProjectProductMapper projectProductMapper;

    @Autowired
    private ExecutePatch executePatch;

    @Autowired
    private IUpPatchCodelistService upPatchCodelistService;

    @Autowired
    private UpProductMapper upProductMapper;

    @Autowired
    private UpPatchCodelistMapper patchCodelistMapper;

    @Autowired
    private UpPatchMapper upPatchMapper;

    @Autowired
    private CommitIdBuildCode commitIdBuildCode;

    @Autowired
    private UpProjectModuleMapper moduleMapper;

    @Autowired
    private ExecuteSqlRecordService recordService;

    @Autowired
    private ExecuteCommonSQL executeCommonSQL;

    @Override
    public UpPatch execute(String sourceUrl, String branch, String processId, Long patchId, String repository) throws Throwable {

        // 查询项目的编译方式
        UpProjectProduct projectProduct = projectProductMapper.getBuildTypeWithPatchId(patchId);
        UpPatch patch = executePatch.executeLis(sourceUrl, branch, processId, patchId, repository, projectProduct);
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

        UpProduct upProduct = upProductMapper.selectUpProductById(upPatch.getProductId());
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
            String fileName = codePath.substring(codePath.lastIndexOf("/") + 1);
            if (!"Y".equals(mergePackageFlag)) {
                // 202301120930_CREATE_CLC_RECIPE_DETAILS_TODO.SQL
                if(fileName.toLowerCase().endsWith(".sql")) {
                    LogUtil.recordBuildLog("--------------校验脚本名规范开始--------------", upPatch.getPatchId(), processId);
                    if (fileName.contains(" ")) {
                        log.info("--------------SQL脚本文件名包含空格，请修改后重试: " + fileName +"--------------");
                        throw new Exception("SQL脚本文件名包含空格，请修改后重试: " + fileName);
                    }

                    // 校验SQL文件名称是否正确
                    boolean upperCase = CommonPatchMethod.isUpperCase(fileName);
                    if (!upperCase) {
                        log.info("--------------SQL脚本文件名包含小写，请修改后重试: " + fileName +"--------------");
                        throw new Exception("SQL脚本文件名包含小写，请修改后重试: " + fileName);
                    }

                    // 校验文件名时间格式
                    if(!(fileName.split("_")[0].length() == 12)) {
                        log.info("--------------SQL脚本文件名不符合命名规范【yyyyMMddHHmm_XXX.SQL】，请参考脚本规范后重试：" + fileName +"--------------");
                        throw new Exception("SQL脚本文件名不符合命名规范【yyyyMMddHHmm_XXX.SQL】，请参考脚本规范后重试: " + fileName);
                    }

                    // 校验脚本文件名，是否是当天日期。年月日
                    String yyyyMM = DateUtils.convertDateToYyyyMM();
                    if (!yyyyMM.equals(fileName.substring(0,6))) {
                        throw new Exception("SQL脚本文件名不符合命名规范【脚本日期的年月必须与打包日期的年月一致，当前日期为： " + yyyyMM + "】，请参考脚本规范后重试: " + fileName);
                    }
                    LogUtil.recordBuildLog("--------------校验脚本名规范结束--------------", upPatch.getPatchId(), processId);
                }
            }
            codeMap.put(tempEntity.getCodeId(), codePath);
            LogUtil.recordBuildLog(tempEntity.getCodePath(), upPatch.getPatchId(), processId);
        }
        LogUtil.recordBuildLog("--------------记录代码路径结束--------------", upPatch.getPatchId(), processId);

        rootPath = getSourcePath(upProduct.getProductCode());
        File rootPathFile = new File(rootPath);
        if(!rootPathFile.exists()){
            rootPathFile.mkdirs();
        }

        // 打包根路径名称： his-boot/upPatchPackage
        String strTempPath = sourceUrl + File.separator + "lis-boot" + File.separator + "target" + File.separator + "upPatchPackage" + File.separator;
        CommonPatchMethod.clearAndCreateTempDir(strTempPath);
        tempSourcePath = strTempPath;
        List<String> complierList;

        // 模块编译
        if (!ConstantUtil.BuildType.CLASS_BUILD.getValue().equals(result.getBuildType())) {
            complierList = copySourceToTempDir(sourceUrl, tempSourcePath, codeMap, upPatch, processId, mergePackageFlag);
        } else {
            complierList = commitIdCopySourceToTempDir(sourceUrl, tempSourcePath, codeMap, upPatch, processId, mergePackageFlag);
        }

        // 获取补丁包配置文件内容
        String configContent;
        if ("Y".equals(upPatch.getConfigFlag())) {
            configContent = upPatch.getConfigList();
        } else {
            configContent = "无";
        }

        if (ConstantUtil.PatchBranch.COMMITID.getValue().equals(patchBranch)) {
            buildTime = mergeTime.replaceAll(":|-", "").replace(" ", "");
        }

        // 生成版本号
        String version = CommonPatchMethod.createVersion(result.getUpgradeVersion());

        // TODO 合包判断逻辑
        List<String> jiraList = new ArrayList<>();
        if ("Y".equals(mergePackageFlag)) {
            if (StringUtils.isNotEmpty(demandNo)) {
                zipFileName = "PATCH_" + demandNo.trim() + "_" + jiraTaskNo + "["+ productCode +"][SQL" + sqlFlag + "][M]_V" + version + "_" + buildTime +".ZIP";
            } else {
                zipFileName = "PATCH_" + jiraTaskNo+"["+ productCode +"][SQL" + sqlFlag + "][M]_V" + version + "_" + buildTime +".ZIP";
            }
            // 查询任务列表
            List<UpPatch> upPatchList = upPatchMapper.selectUpPatchListByParentPatchId(upPatch.getPatchId());
            jiraList = upPatchList.stream().map(UpPatch::getJiraNo).collect(Collectors.toList());
            CommonPatchMethod.afterCopySource(zipFileName, tempSourcePath,comments,topic,complierList, jiraList, configContent);
        }else {
            jiraList.add(upPatch.getJiraNo());
            if (StringUtils.isNotEmpty(demandNo)) {
                zipFileName = "PATCH_" + demandNo.trim() + "_" + jiraTaskNo + "["+ productCode +"][SQL" + sqlFlag + "]_V" + version + "_" + buildTime +".ZIP";
            } else {
                zipFileName = "PATCH_" + jiraTaskNo+"["+ productCode +"][SQL" + sqlFlag + "]_V" + version + "_" + buildTime +".ZIP";
            }
            CommonPatchMethod.afterCopySource(zipFileName, tempSourcePath,comments,topic,complierList, jiraList, configContent);
        }

        // 生成版本号xml
        String xmlPath = tempSourcePath + "code" + File.separator + productShortName + File.separator;
        CommonPatchMethod.createXml(zipFileName, xmlPath, version);
        upPatch.setUpgradeVersion(version);

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
     * 描述：LIS代码路径替换
     * 作者：DangHangHang
     * 时间：2023年06月13日 10:52
     */
    private String codePathReplace(String path, Long patchId, String processId) throws Exception{
        if(path.toLowerCase().endsWith(".sql")){
            // 暂时这里添加lis
            path = path.replace(  "lis/db/", "db/");
            path = path.replace(  "lis/sql/", "sql/");
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

    /**
     * 主分支方式-复制编译后的文件到临时目录
     * @param sourceUrl
     * @param tempSourcePath
     * @param codeMap
     * @param upPatch
     * @param processId
     * @param mergePackageFlag
     * @return
     * @throws Exception
     */
    public List<String> copySourceToTempDir(String sourceUrl,String tempSourcePath,Map<Long, String> codeMap, UpPatch upPatch, String processId, String mergePackageFlag) throws Exception {
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
                String projectName = CommonPatchMethod.getProjectNameByCodeList(tepStr, patchId, processId);
                tepStr = codePathReplace(tepStr, patchId, processId);
                if (tepStr.toLowerCase().endsWith(".java")) {
                    localsubPath = CommonPatchMethod.subPath(ConstantUtil.CodeType.CODE);
                    List<String> javaCompilePath = copyJavaFile(
                            sourceUrl,
                            tempSourcePath,
                            tepStr,
                            projectName,
                            localsubPath,
                            patchId,
                            processId);
                    // 存编译后的codeList对象
                    codelist.setCompilePath(StringUtils.join(javaCompilePath,"," ));
                    complierList.addAll(javaCompilePath);
                } else if (tepStr.toLowerCase().endsWith(".sql")){
                    localsubPath = CommonPatchMethod.subPath(ConstantUtil.CodeType.SQL);
                    String sqlCompilePath = copySqlFile(sourceUrl,
                            tempSourcePath,
                            tepStr,
                            localsubPath,
                            patchId,
                            processId,
                            mergePackageFlag,
                            createBy);
                    codelist.setCompilePath(sqlCompilePath);
                    complierList.add(sqlCompilePath);
                } else {
                    localsubPath = CommonPatchMethod.subPath(ConstantUtil.CodeType.CODE);
                    String otherCompilePath = copyCodeFile(
                            sourceUrl,
                            tempSourcePath,
                            tepStr,
                            projectName,
                            localsubPath,
                            patchId,
                            processId);
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
     * CommitId方式-复制模块下的编译文件到临时目录
     * @param sourceUrl
     * @param tempSourcePath
     * @param codeMap
     * @param upPatch
     * @param processId
     * @param mergePackageFlag
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
                String projectName = CommonPatchMethod.getProjectNameByCodeList(tepStr, patchId, processId);

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

                String sourcePath = commitIdBuildCode.commitIdReplaceFilePath(tepStr, patchId, processId);
                String path = commitIdBuildCode.sourceReplaceFilePath(tepStr, patchId, processId);
                if (tepStr.toLowerCase().endsWith(".java")) {
                    localsubPath = CommonPatchMethod.subPath(ConstantUtil.CodeType.CODE);
                    List<String> javaCompilePath = commitIdBuildCode.commitIdCopyJavaFile(sourceUrl, tempSourcePath,
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
                    localsubPath = CommonPatchMethod.subPath(ConstantUtil.CodeType.SQL);
                    String sqlCompilePath = copySqlFile(sourceUrl,
                            tempSourcePath,
                            tepStr,
                            localsubPath,
                            patchId,
                            processId,
                            mergePackageFlag,
                            createBy);
                    codelist.setCompilePath(sqlCompilePath);
                    complierList.add(sqlCompilePath);
                } else {
                    localsubPath = CommonPatchMethod.subPath(ConstantUtil.CodeType.CODE);
                    String otherCompilePath = commitIdBuildCode.copyCodeFile(sourceUrl, tempSourcePath, tepStr,
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
     * 描述：补丁包生成路径
     * 作者：DangHangHang
     * 时间：2023年06月13日 10:42
     */
    public String getSourcePath(String projectCode){
        return rootSource + File.separator + projectCode + File.separator;
    }

    /**
     * 描述：复制Java文件到临时目录
     * 作者：DangHangHang
     * 时间：2023年06月13日 10:57
     */
    private List<String> copyJavaFile(String sourceUrl,String tempSourcePath,
                                      String tepStr, String projectName,String localsubPath,Long patchId, String processId) throws Exception {
        String currentDir = "";
        String fileName = tepStr.substring(tepStr.lastIndexOf("/")+1).replace(".java", "");
        String localPath =  tempSourcePath + localsubPath ;
        String sourceRootPath = sourceUrl + File.separator + "lis-boot" + File.separator + "target" + File.separator + projectName;

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

    /**
     * 描述：复制SQL到临时目录
     * 作者：DangHangHang
     * 时间：2023年06月13日 11:15
     */
    public String copySqlFile(String sourceUrl,
                              String tempSourcePath,
                              String tmpStr,
                              String localsubPath,
                              Long patchId,
                              String processId,
                              String mergePackageFlag,
                              String createBy
    ) throws Exception {
        String fileName = tmpStr.substring(tmpStr.lastIndexOf("/") + 1);
        String localPath =  tempSourcePath + localsubPath ;
        String foldName = "";
        if(tmpStr.contains("0SYS")){
            foldName = "0SYSTEM";
        }
        if(tmpStr.contains("0TABLE") || tmpStr.contains("2DATA") || tmpStr.contains("1DATA")){
            foldName = "1TABLE_DATA";
        }
        if(tmpStr.contains("1VIEW") || tmpStr.contains("2FUNCTION") || tmpStr.contains("2VIEW_FUNCTION") || tmpStr.contains("2VIEW_FUNC")){
            foldName = "2VIEW_FUNCTION";
        }
        if(tmpStr.contains("3PROCEDURE") || tmpStr.contains("4TRIGGER")){
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

                    executeCommonSQL.detectSql(fileName, sourceFilePath, patchId, processId);
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
     * copy other code file
     * @param sourceUrl
     * @param tempSourcePath
     * @param tepStr
     * @param projectName
     * @param localsubPath
     * @param patchId
     * @param processId
     * @return
     * @throws Exception
     */
    private String copyCodeFile(String sourceUrl, String tempSourcePath, String tepStr, String projectName,
                                String localsubPath,Long patchId, String processId) throws Exception {
        String currentDir = tepStr.substring(0,tepStr.lastIndexOf("/"));
        String fileName = tepStr.substring(tepStr.lastIndexOf("/")+1,tepStr.length());
        String localPath =  tempSourcePath + localsubPath;
        String sourceRootPath = sourceUrl + File.separator + "lis-boot" + File.separator + "target" + File.separator + projectName;
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
}
