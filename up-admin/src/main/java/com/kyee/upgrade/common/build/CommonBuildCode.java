package com.kyee.upgrade.common.build;

import com.kyee.upgrade.common.domain.MavenBuildResult;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.mapper.UpPatchCodelistMapper;
import com.kyee.upgrade.mapper.UpProductMapper;
import com.kyee.upgrade.mapper.UpProjectModuleMapper;
import com.kyee.upgrade.utils.LogUtil;
import com.kyee.upgrade.utils.MavenUtil;
import com.kyee.upgrade.utils.OSValidator;
import com.ruoyi.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kyee.upgrade.common.patch.CommonPatchMethod.recursionSourceFileDir;

/**
 * 描述：编译代码
 * 作者：DangHangHang
 * 时间：2023年06月12日 14:55
 */
@Component
public class CommonBuildCode {
    private static final Logger log = LoggerFactory.getLogger(CommonBuildCode.class);

    @Autowired
    private UpProjectModuleMapper projectModuleMapper;

    @Autowired
    private UpPatchCodelistMapper patchCodelistMapper;

    @Autowired
    private UpProductMapper upProductMapper;

    @Value(value = "${ruoyi.mavenHome}")
    private String mavenHome;

    /**
     * 公共编译代码
     * @param sourceUrl
     * @param processId
     * @param patchId
     * @throws Throwable
     */
    public void commonExecuteBuild(String sourceUrl, String processId, Long patchId, UpPatch patch) throws Throwable{
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
     * @param processId
     * @param patchId 补丁包管理ID
     * @throws Throwable
     */
    public void executeBuild(String sourceUrl, String processId, Long patchId, UpPatch patch) throws Throwable{
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
     * @param processId
     * @param patchId 补丁包管理ID
     */
    public void bootExecuteBuild(String sourceUrl, String processId, Long patchId, UpPatch patch) throws Throwable {
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
     * @param processId
     * @param patchId 补丁包管理
     */
    public void jarExecuteBuild(String sourceUrl, String processId, Long patchId) throws Throwable{

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
     * CLASS编译
     * @param sourceUrl
     * @param processId
     * @param patchId 补丁包管理IDff
     */
    public void classBuild(String sourceUrl, String processId, Long patchId, UpPatch patch) throws Throwable{

        UpProjectModule module = new UpProjectModule();
        module.setProductId(patch.getProductId());
        module.setProjectId(patch.getProjectId());
        // 查询每个产品的模块（和项目有关时再关联项目Id）
        List<UpProjectModule> upProjectModules = projectModuleMapper.getModuleList(module);
        List<UpPatchCodelist> codeList = patchCodelistMapper.selectCodeListByPatchId(patchId);
        UpProduct upProduct = upProductMapper.selectUpProductById(patch.getProductId());
        String productShortName = upProduct.getProductShortName().trim();
        verifySourceCodeRepeat(sourceUrl, patchId, processId, codeList, upProjectModules, productShortName);
        try {
            LogUtil.recordBuildLog("--------------Class项目编译开始--------------", patchId, processId);

            // 先编译his-base和his-base-bean模块，这两个模块为基础模块,再his-boot
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
                        if (!codePath.contains("/src")) {
                            continue;
                        }
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

    /*
     * @description：校验源码文件是否重复，输出到日志中
     * @author：DangHangHang
     * @date：2023-09-15
     */
    private void verifySourceCodeRepeat (String sourceUrl, Long patchId, String processId, List<UpPatchCodelist> codeList, List<UpProjectModule> modules, String shortName) throws Exception {

        // 复制模块中的源码文件到临时目录
        if (!CollectionUtils.isEmpty(modules)) {
            List<String> moduleNames = modules.stream().map(UpProjectModule::getModuleName).distinct().collect(Collectors.toList());
            long startMillis = System.currentTimeMillis();
            LogUtil.recordBuildLog("--------------校验重复文件START--------------", patchId, processId);
            // <模块名，文件路径>
            Map<String, List<String>> map = new HashMap<>();
            for (String name : moduleNames) {
                String modulePath = sourceUrl + File.separator + name + File.separator + "src" + File.separator;
                File pathFile = new File(modulePath);
                List<String> moduleMapList =  new ArrayList<>();
                if (pathFile.exists()) {
                    File[] files = pathFile.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            moduleMapList.addAll(recursionSourceFileDir(file));
                        }
                    }
                }
                map.put(name, moduleMapList);
            }
//            long moduleMillis = startMillis;
            // 重复路径<模块名, 文件名>
            Map<String, String> repeatMap = new HashMap<>();
            // <文件名, 模块名>
            Map<String, String> pathMap = new HashMap<>();
            for (String moduleName : map.keySet()){
                List<String> fileList = map.get(moduleName);
                    for (String moduleFile : fileList) {
                        if (pathMap.containsKey(moduleFile)) {
                            repeatMap.put(moduleName, moduleFile);
                            repeatMap.put(pathMap.get(moduleFile), moduleFile);
                        }
                        pathMap.put(moduleFile, moduleName);
                    }
//                long currentModuleMillis = System.currentTimeMillis();
//                LogUtil.recordBuildLog("--------------校验" + moduleName + "模块重复文件总耗时：" + (currentModuleMillis - moduleMillis) + "ms--------------", patchId, processId);
//                moduleMillis = currentModuleMillis;
            }
            List<String> patchRepeat = new ArrayList<>();
            if (!repeatMap.isEmpty() && CollectionUtils.isNotEmpty(codeList)) {
                for (UpPatchCodelist codelist : codeList) {
                    String codePath = codelist.getCodePath();
                    if (!codePath.contains("/src/")) {
                        continue;
                    }
                    String path = codePath.substring(codePath.lastIndexOf("/src/") + 1);
                    for (String key : repeatMap.keySet()) {
                        String repeatPath = repeatMap.get(key);
                        if (repeatPath.contains("README")) {
                            continue;
                        }
                        if (OSValidator.isWindows()) {
                            repeatPath = repeatPath.replaceAll("\\\\", "/");
                        }
                        if (repeatPath.contains(path)) {
                            LogUtil.recordBuildLog("--------------"+ key + " >>> " + repeatPath +"-------------- ", patchId, processId);
                            patchRepeat.add(path);
                        }
                    }
                }
                LogUtil.recordBuildLog("--------------重复文件总数量：" + repeatMap.size() + "--------------", patchId, processId);
            }
            LogUtil.recordBuildLog("--------------校验重复文件END--------------", patchId, processId);
            long endMillis = System.currentTimeMillis();
            LogUtil.recordBuildLog("--------------校验重复文件总耗时：" + (endMillis-startMillis) + "ms--------------", patchId, processId);

            if (patchRepeat.size() > 0) {
                LogUtil.recordBuildLog("--------------包中重复文件数量：" + patchRepeat.size() + "--------------", patchId, processId);
                throw new Exception("项目源码文件路径有冲突！请处理之后，再生成单任务包！冲突文件，详见日志！！！");
            } else {
                LogUtil.recordBuildLog("--------------源码文件中无重复文件--------------", patchId, processId);
            }
        } else {
            LogUtil.recordBuildLog("--------------项目中无模块--------------", patchId, processId);
        }
    }
}
