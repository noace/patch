package com.kyee.upgrade.service.impl;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kyee.upgrade.common.domain.MessageText;
import com.kyee.upgrade.common.domain.TencentMessage;
import com.kyee.upgrade.domain.vo.UpPatchCodeListPo;
import com.kyee.upgrade.domain.vo.UpProjectDTO;
import com.kyee.upgrade.scheduled.job.TencentMessageJob;
import com.kyee.upgrade.service.IUpProductService;
import com.kyee.upgrade.utils.CommonUtils;
import com.kyee.upgrade.utils.GitFilePath;
import com.ruoyi.common.core.domain.CxSelect;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import org.apache.commons.collections.MapUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.mapper.*;
import com.kyee.upgrade.service.IUpCommandService;
import com.kyee.upgrade.utils.ConstantUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.service.IUpPatchService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 补丁包管理Service业务层处理
 *
 * @author lijunqiang
 * @date 2021-06-12
 */
@Service
public class UpPatchServiceImpl implements IUpPatchService
{
    private static final Logger log = LoggerFactory.getLogger(UpPatchServiceImpl.class);

    // 企业微信通知KEY
    @Value(value = "${tencentMsg.hisKey}")
    private String hisKey;

    @Value(value = "${tencentMsg.projectKey}")
    private String projectKey;

    @Value(value = "${tencentMsg.productKey}")
    private String productKey;

    @Autowired
    private UpPatchMapper upPatchMapper;

    @Autowired
    private UpPatchCodelistMapper upPatchCodelistMapper;

    @Autowired
    private UpCommandMapper upCommandMapper;

    @Autowired
    private IUpCommandService upCommandService;

    @Autowired
    private UpPatchCodelistServiceImpl upPatchCodelistService;

    @Autowired
    private UpBuildLogMapper upBuildLogMapper;

    @Autowired
    private UpProjectConfigFileMapper configFileMapper;

    @Autowired
    private UpPatchSqlMapper sqlMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private IUpPatchService upPatchService;

    @Autowired
    private UpProjectProductMapper projectProductMapper;

    @Autowired
    private TencentMessageJob tencentMessageJob;

    @Autowired
    private IUpProductService upProductService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UpProductMapper productMapper;

    @Autowired
    private UpProjectModuleMapper moduleMapper;

    @Autowired
    private UpProjectMapper upProjectMapper;

    @Autowired
    private UpProjectProductMapper upProjectProductMapper;

    @Value(value = "${ruoyi.mavenHome}")
    private String mavenHome;

    @Value(value = "${ruoyi.workspacePath}")
    private String workspacePath;

    /**
     * 补丁登记
     * @param upPatch 登记信息
     * @return
     */
    @Override
    @Transactional
    public synchronized String registPatch(UpPatch upPatch) {



        // 补充补丁登记信息
        setRegistPatchParam(upPatch);

        //插入登记补丁数据
        upPatchMapper.insertUpPatch(upPatch);

        //获取 插入id
        Long patchId = upPatchMapper.getLastInsertId();
        upPatch.setPatchId(patchId);

        //插入插入补丁code列表
        upPatchCodelistService.insertUpBuildCodePathByUpPatch(upPatch);

        //生成信息到构建队列
        UpCommand upCommand = setUpCommandParam(upPatch);

        //插入构建队列
        upCommandMapper.insertUpCommand(upCommand);
        return patchId + "";
    }

    /*
     * @description：校验重复文件
     * @author：DangHangHang
     * @date：2023-09-21
     */
    @Override
    public Map<String, String> validatePatchMergeTime(UpPatch upPatch) {
        Map<String, String> map = new HashMap<>();
        if (ConstantUtil.ProjectProductEnum.LIS.getValue().equals(upPatch.getProductId())) {
            return map;
        }
        // 查询项目的编译方式
        UpProjectProduct pp = projectProductMapper.getUpProjectProductById(upPatch.getProductId(), upPatch.getProjectId());
        // 打包时利用任务合并时间解决跨包问题  YUNHIS-40285
        if (ConstantUtil.PatchBranch.COMMITID.getValue().equals(pp.getPatchBranch())) {
            String projectPrincipal = pp.getProjectPrincipal();
            List<String> projectPrincipals = Arrays.asList(projectPrincipal.split(","));
            map = validatePatchMergeTime(upPatch, projectPrincipals.get(0));
        }
        return map;
    }

    /**
     * 校验登记补丁包的合并时间是否小于已发布和已升级的
     */
    private Map<String, String> validatePatchMergeTime(UpPatch upPatch, String projectPrincipal) {

        Map<String, String> map = new HashMap<>();
        Integer productId = upPatch.getProductId();
        Integer projectId = upPatch.getProjectId();
        String mergeTime = upPatch.getMergeTime();
        // 查询当前已打包的包
        UpPatch patch = new UpPatch();
        patch.setProjectId(projectId);
        patch.setProductId(productId);
        // 已发布和已升级的包列表
        if (StringUtils.isEmpty(mergeTime)) {
            throw new BusinessException("补丁包合并时间为空，请检查！");
        }
        patch.setMergeTime(mergeTime);
        List<String> statusList = Arrays.asList(ConstantUtil.UpPatchStatus.PUBLISHED.getValue(), ConstantUtil.UpPatchStatus.UPGRADE_ALL.getValue());
        List<UpPatch> patchList = upPatchMapper.getPatchListByStatusList(patch, statusList);
        if (!CollectionUtils.isEmpty(patchList)) {
            Map<String, List<UpPatch>> statusMap = patchList.stream().collect(Collectors.groupingBy(UpPatch::getPatchStatus));
            List<UpPatch> publishPatchList = statusMap.get(ConstantUtil.UpPatchStatus.PUBLISHED.getValue());
            List<UpPatch> upgradePatchList = statusMap.get(ConstantUtil.UpPatchStatus.UPGRADE_ALL.getValue());
            String publishMsg = commonGetCodeListByIds(publishPatchList, productId, projectId, projectPrincipal, "publish", upPatch);
            String upgradeMsg = commonGetCodeListByIds(upgradePatchList, productId, projectId, projectPrincipal, "upgrade", upPatch);
            map.put("publish", publishMsg);
            map.put("upgrade", upgradeMsg);
            return map;
        }
        return map;
    }

    /**
     * 查询是否包含指定包代码列表
     * @param patchList
     * @param productId
     * @param projectId
     * @param projectPrincipal
     * @param type
     */
    private String commonGetCodeListByIds(List<UpPatch> patchList,Integer productId, Integer projectId, String projectPrincipal, String type, UpPatch upPatch) {

        if (CollectionUtils.isEmpty(patchList)) {
            return "";
        }

        SysUser sysUser = ShiroUtils.getSysUser();
        String loginName = sysUser.getLoginName();
        String userName = sysUser.getUserName();
        List<Long> ids = patchList.stream().map(UpPatch::getPatchId).collect(Collectors.toList());
        Map<Long, UpPatch> idMap = patchList.stream().collect(Collectors.toMap(UpPatch::getPatchId, s -> s));

        List<UpPatchCodeListPo> codeList = upPatchCodelistMapper.getCodePathWithIds(ids, productId, projectId);
        codeList.forEach(s -> s.setCodePath(s.getCodePath().replaceAll("\r|\n", "")));

        Set<String> codeListPublish = new HashSet<>();
        List<String> registerCodeList = patchList.stream().map(UpPatch::getCodeList).collect(Collectors.toList());
        for (String code : registerCodeList) {
            codeListPublish.addAll(Arrays.asList(code.replaceAll("\r|\n", "").split(",")));
        }

        UpProject project = upProjectMapper.selectUpProjectById(projectId);
        String projectName = "";
        if (!Objects.isNull(project)) {
            projectName = project.getProjectName();
        }

        Set<String> publishNameList = new HashSet<>();
        Set<String> upgradeNameList = new HashSet<>();
        String commitId = upPatch.getCommitId();
        String mergeTime = upPatch.getMergeTime();

        if (!CollectionUtils.isEmpty(codeList)) {
            String deptId = sysUserMapper.getDeptByLoginName(loginName);
            TencentMessage textMsg = new TencentMessage();
            textMsg.setMsgtype("text");
            MessageText textText = new MessageText();
            textText.setMentioned_list(Arrays.asList(loginName));
            for (UpPatchCodeListPo codeListPo : codeList) {
                if (codeListPublish.contains(codeListPo.getCodePath())) {
                    // 发送企业微信
                    UpPatch patch = idMap.get(codeListPo.getPatchId());
                    String patchFileName = patch.getPatchFileName();
                    if ("publish".equals(type)) {
                        publishNameList.add(patchFileName);
                    }

                    if ("upgrade".equals(type)) {
                        upgradeNameList.add(patchFileName);
                    }
                }
            }
            if ("publish".equals(type) && StringUtils.isNotEmpty(publishNameList)) {

                String publishMsg = "【警告】由于您合并分支后未及时打包，系统检测到有其他补丁包携带你的部分代码【已发布】至【" + projectName + "】现场！\n" +
                        "提交序号：" + commitId + "，合并时间："+ mergeTime + "\n" +
                        "请立刻按照以下流程处理：\n" +
                        "1、立刻通知升级负责人【" + projectPrincipal + "】不要升级【" + StringUtils.join(publishNameList, ",\n") +"】;\n" +
                        "2、打包完成后请及时发布，并通知升级负责人先升级您的补丁包！\n" +
                        "【" + userName + "】收到请回复（处理完成请回复！）";
                textText.setContent(publishMsg);
                textMsg.setText(textText);
                if (StringUtils.isNotEmpty(deptId) && ConstantUtil.DEPTID.HISDEPTID.getValue().equals(deptId)) {
                    tencentMessageJob.sendMessage(textMsg, hisKey);
                    return publishMsg;
                } else {
                    return publishMsg;
                }
            }

            if ("upgrade".equals(type) && StringUtils.isNotEmpty(upgradeNameList)) {
                String upgradeMsg = "【警告】由于您合并分支后未及时打包，系统检测到有其他补丁包【" + StringUtils.join(upgradeNameList, ",\n") + "】已携带你的部分代码发布至【" + projectName + "】现场，且【已经升级】！\n" +
                        "提交序号：" + commitId + "，合并时间："+ mergeTime + "\n" +
                        "请立刻按照以下流程处理：\n" +
                        "1、立刻联系升级负责人【" + projectPrincipal + "】排查此任务相关功能是否出现问题;\n" +
                        "2、若出现问题，请立刻解决后重新提交合并请求，然后使用【重新合并的提交序号】编辑打包并及时发布;\n" +
                        "3、若未出现问题，请重新提交合并请求，然后使用【重新合并的提交序号】编辑打包并及时发布。\n" +
                        "【" + userName + "】收到请回复（处理完成请回复！）";
                textText.setContent(upgradeMsg);
                textMsg.setText(textText);
                if (StringUtils.isNotEmpty(deptId) && ConstantUtil.DEPTID.HISDEPTID.getValue().equals(deptId)) {
                    tencentMessageJob.sendMessage(textMsg, hisKey);
                    return upgradeMsg;
                } else {
                    return upgradeMsg;
                }
            }
        }
        return "";
    }

    /**
     * 补丁修改
     *
     * @param upPatch 补丁包管理
     * @return 结果
     */
    @Override
    public int updateUpPatch(UpPatch upPatch){
        // 补充补丁登记信息
        setRegistPatchParam(upPatch);

        // 修改登记补丁数据
        upPatchMapper.updateUpPatch(upPatch);

        // 删除补丁code列表
        upPatchCodelistMapper.deleteUpPatchCodelistByPathId(upPatch.getPatchId());

        // 插入补丁code列表
        upPatchCodelistService.insertUpBuildCodePathByUpPatch(upPatch);

        //生成信息到构建队列
//        UpCommand upCommand = setUpCommandParam(upPatch);
        UpCommand upCommand = new UpCommand();
        upCommand.setUpdateBy(upPatch.getUpdateBy());
        upCommand.setUpdateTime(DateUtils.getNowDate());
        upCommand.setDelFlag("N");
        upCommand.setPatchId(upPatch.getPatchId());
        upCommand.setProductId(upPatch.getProductId());
        upCommand.setProjectId(upPatch.getProjectId());

        // 修改时，删除历史补丁包日志，为了让【打包中】状态，查看日志显示最新的日志
        List<UpBuildLog> buildLogs = upBuildLogMapper.getBuildLogsByPatchId(upPatch.getPatchId());
        if (!CollectionUtils.isEmpty(buildLogs)) {
            List<Long> buildList = buildLogs.stream().map(UpBuildLog::getBuildId).collect(Collectors.toList());
            /*Long[] array =new Long[buildList.size()];
            Long[] buildArray = buildList.toArray(array);*/
            upBuildLogMapper.deleteLogByIds(buildList);
        }

        //更新并重启构建队列
        return upCommandService.updateUpCommand(upCommand);
    }

    /**
     * 补丁合并
     *
     * @param
     * @return 结果
     */
    @Override
    public AjaxResult mergeUpPatch(String ids, String updateBy) {
        // 检查待合包是否存在
        List<UpPatch> upPatchs = selectUpPatchListByIds(ids);
        if (null == upPatchs) {
            return AjaxResult.error("补丁包获取失败，请联系管理员！");
        }

        List<String> mergeFlags = upPatchs.stream().map(UpPatch::getMergePackageFlag).collect(Collectors.toList());
        if (mergeFlags.contains("Y")) {
            return AjaxResult.warn("已合并的补丁包不能再合并，请重新选择！");
        }

        // 检查要合并的包是否是同一个产品
        long productCount = upPatchs.stream().map(UpPatch::getProductId).distinct().count();
        if (productCount > 1) {
            return AjaxResult.warn("请选择相同产品的包进行合并！");
        }

        // 检查要合并的包是否是同一个项目
        long projectCount = upPatchs.stream().map(UpPatch::getProjectId).distinct().count();
        if (projectCount > 1) {
            return AjaxResult.warn("请选择相同项目的包进行合并！");
        }

        // 校验是否有打包失败的包被发布
        List<UpPatch> failPatch = upPatchs.stream().filter(s -> StringUtils.isEmpty(s.getBuildTime())).collect(Collectors.toList());
        if (failPatch.size() > 0) {
            return AjaxResult.warn("合并的包中包含打包失败的包，请联系管理员！");
        }

        // 检查要合并的包是否是已发布状态
        for (UpPatch upPatch : upPatchs) {
            if (!ConstantUtil.UpPatchStatus.PUBLISHED.getValue().equals(upPatch.getPatchStatus())) {
                return AjaxResult.warn("请选择已发布的补丁包进行合并！");
            }
        }

        String branchId = "";
        String mergeTime = "";
        UpProjectProduct pp = projectProductMapper.getUpProjectProductById(upPatchs.get(0).getProductId(), upPatchs.get(0).getProjectId());
        // 合并包使用最新子包的分支Id打包
        if (!Objects.isNull(pp) && ConstantUtil.PatchBranch.COMMITID.getValue().equals(pp.getPatchBranch())) {
            branchId = upPatchs.stream().sorted(Comparator.comparing(UpPatch::getMergeTime).reversed()).collect(Collectors.toList()).get(0).getBranchId();
            log.info("合并包的分支Id为：", branchId);
            if (StringUtils.isEmpty(branchId)) {
                return AjaxResult.warn("合并包没有分支Id，请联系管理员排查");
            }
            mergeTime = upPatchs.stream().sorted(Comparator.comparing(UpPatch::getMergeTime).reversed()).collect(Collectors.toList()).get(0).getMergeTime();
            log.info("合并包的合并时间为：", mergeTime);
        }

        // 组装合并包数据
        UpPatch upPatch = setMergeUpPatchParam(upPatchs, updateBy, branchId, mergeTime);

        // 补丁登记
        String parentPatchId = "";
        try {
            parentPatchId = registPatch(upPatch);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("补丁包代码列表中有不能打包的配置文件信息");
        }
        // 子包合并标记插入登记id
        for (UpPatch patch : upPatchs) {
            patch.setMergePackageFlag(parentPatchId);
            updateUpPatchNoSession(patch);
        }

        return AjaxResult.success();
    }

    /**
     * 组装合并包数据
     *
     * @param upPatchs
     * @param updateBy
     * @param branchId 分支Id
     * @return 结果
     */
    private UpPatch setMergeUpPatchParam(List<UpPatch> upPatchs, String updateBy, String branchId, String mergeTime)
    {
        UpPatch upPatch = new UpPatch();

        // 取第一条补丁数据的产品、项目、任务名作为合并数据
        UpPatch firstUpPatch = upPatchs.get(0);
        upPatch.setProductId(firstUpPatch.getProductId());
        upPatch.setProjectId(firstUpPatch.getProjectId());
        upPatch.setJiraNo(firstUpPatch.getJiraNo());
        upPatch.setDemandNo(firstUpPatch.getDemandNo());//需求号取第一个任务的需求号

        // 合并主题、备注、代码列表、配置列表、配置标记、bug标记、需求名
        StringBuilder topic = new StringBuilder();
        StringBuilder remark = new StringBuilder();
        StringBuilder codeList = new StringBuilder();
        StringBuilder configList = new StringBuilder();
        StringBuilder demandName = new StringBuilder();
        String configFlag = "N";
        String bugfixFlag = "N";
        int row  = 0;
        boolean isExpedited = false;
        int count = 0;
        for (UpPatch tempUpPatch : upPatchs) {
            if("Y".equals(tempUpPatch.getExpedited())){
                isExpedited = true;
            }
            // 合并主题
            topic.append(tempUpPatch.getTopic());
            topic.append("\r\n");

            //合并需求名
            if (tempUpPatch.getDemandNo() != null && tempUpPatch.getDemandName() != null) {
                if (count <= 0) {
                    upPatch.setDemandNo(tempUpPatch.getDemandNo());//需求号取第一个有需求的任务的需求号
                    count++;
                }
                demandName.append(tempUpPatch.getDemandName().trim());
                demandName.append("\r\n");
            }


            // 合并备注
            if (StringUtils.isNotEmpty(tempUpPatch.getRemark())) {
                remark.append(tempUpPatch.getRemark());
                remark.append(",\r\n");
            }

            // 合并代码列表
            String code = tempUpPatch.getCodeList().replace("\r\n", "");
            codeList.append(code);
            if (row < upPatchs.size() -1) {
                codeList.append(",");
                codeList.append("\r\n");
            }

            // 合并配置列表
            configList.append(tempUpPatch.getConfigList());
            configList.append("\r\n");

            // 合并配置标记
            if ("Y".equals(tempUpPatch.getConfigFlag())) {
                configFlag = "Y";
            }

            // 合并bug标记
            if ("Y".equals(tempUpPatch.getBugfixFlag())) {
                bugfixFlag = "Y";
            }
            row++;
        }
        upPatch.setTopic(topic.toString());
        upPatch.setDemandName(demandName.toString());
        upPatch.setRemark(remark.toString());
        upPatch.setCodeList(codeList.toString());
        upPatch.setConfigList(configList.toString());
        upPatch.setConfigFlag(configFlag);
        upPatch.setBugfixFlag(bugfixFlag);
        upPatch.setBranchId(branchId);
        upPatch.setMergeTime(mergeTime);

        // 合并标记、责任人
        upPatch.setMergePackageFlag("Y");
        upPatch.setCreateBy(updateBy);
        upPatch.setUpdateBy(updateBy);
        upPatch.setExpedited(isExpedited ? "Y" : "N");
        return upPatch;
    }

    /**
     * 补充补丁登记信息
     */
    private void setRegistPatchParam(UpPatch upPatch) {
        //获取时间信息
        upPatch.setCreateTime(new Date());
        upPatch.setUpdateTime(new Date());
        if (StringUtils.isNotEmpty(upPatch.getDemandNo())) {
            upPatch.setDemandNo(upPatch.getDemandNo().trim());
        }

        //生成sql标志
        String codeList = upPatch.getCodeList();

        // 检查代码里是否有配置文件
        codeListHasConfigFile(codeList, upPatch.getProductId(), upPatch.getProjectId());

        if(codeListHavaSqlFile(codeList)){
            upPatch.setSqlFlag(ConstantUtil.SqlFlag.SQL.getValue());
        }else{
            upPatch.setSqlFlag(ConstantUtil.SqlFlag.NOT_SQL.getValue());
        }

        // 状态改为打包中
        upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PKGING.getValue());
    }

    /**
     * 组装构建队列
     */
    private UpCommand setUpCommandParam(UpPatch upPatch) {
        UpCommand upCommand = new UpCommand();
        upCommand.setCommandInfo(ConstantUtil.UpPatchStatus.REGISTERED.name());
        upCommand.setCommandType("");
        upCommand.setProductId(upPatch.getProductId());
        upCommand.setProjectId(upPatch.getProjectId());
        upCommand.setPatchId(upPatch.getPatchId());
        upCommand.setCreateBy(upPatch.getCreateBy());
        upCommand.setUpdateBy(upPatch.getUpdateBy());
        upCommand.setCreateTime(new Date());
        upCommand.setUpdateTime(new Date());
        upCommand.setDelFlag("N");

        return upCommand;
    }

    /**
     * 检查代码里边中是否包含sql
     */
    private boolean codeListHavaSqlFile(String codeList){
        boolean result = false;
        if(codeList != null){
            String[] codePathArray = codeList.split(",");
            for(int i = 0;i< codePathArray.length;i++){
                if(codePathArray[i].toLowerCase().endsWith(".sql")){
                    result = true;
                    break;
                }
            }
        }else{
            result = false;
        }
        return result;
    }

    /**
     * 检查代码里是否有配置文件
     * @return
     */
    private void codeListHasConfigFile(String codeList, Integer productId, Integer projectId){
        if(codeList != null){
            String[] codePathArray = codeList.split(",");
            List<String> pathList = new ArrayList<>();
            // 查询不能打进包里的配置文件(和项目无关)
            List<UpProjectConfigFile> configFiles = configFileMapper.selectUpProjectConfigFileList(new UpProjectConfigFile());
            List<String> configNames = configFiles.stream().filter(s -> productId.equals(s.getProductId()))
                                                        .map(UpProjectConfigFile::getConfigFileName)
                                                        .collect(Collectors.toList());
            for (String codepath : codePathArray) {
                String path = codepath.substring(codepath.lastIndexOf("/") + 1).replaceAll("\r|\n", "");
                if (configNames.contains(path)) {
                    pathList.add(path);
                }
            }
            if (!CollectionUtils.isEmpty(pathList)) {
                throw new RuntimeException("代码列表里不能包含" + pathList.toString() + "文件，请在修改配置中说明");
            }
        }
    }

    /**
     * 查询补丁包管理
     *
     * @param patchId 补丁包管理ID
     * @return 补丁包管理
     */
    @Override
    public UpPatch selectUpPatchById(Long patchId)
    {
        return upPatchMapper.selectUpPatchById(patchId);
    }

    /**
     * 查询补丁包管理列表
     *
     * @param upPatch 补丁包管理
     * @return 补丁包管理
     */
    @Override
    public List<UpPatch> selectUpPatchList(UpPatch upPatch)
    {
        return upPatchMapper.selectUpPatchList(upPatch);
    }
    /**
     * 查询补丁包管理列表
     *
     * @return 补丁包管理
     */
    @Override
    public List<UpPatch> selectUpPatchChildList(List<UpPatch> upPatches)
    {
        return upPatches==null||upPatches.size()==0 ? new ArrayList<>():upPatchMapper.selectUpPatchChildList(upPatches);
    }

    /**
     * 查询补丁包管理列表
     *
     * @param upPatch 补丁包管理
     * @return 补丁包管理
     */
    @Override
    public List<UpPatch> getUpPatchList(UpPatch upPatch)
    {

        // 打包中 -> 打包失败 -> 已回退 -> 已锁定 -> 已打包  -> 已发布 -> 未升级 -> 已升级 （编译时间）
        List<UpPatch> upPatchList = upPatchMapper.getUpPatchList(upPatch);
        //获取所有的修复任务的信息
        UpPatch reupPatch = new UpPatch();
        reupPatch.setBugfixFlag("Y");
        List<UpPatch> reUpPatches = upPatchService.selectUpPatchList(reupPatch);
        //获取所有已发布的有bug的记录
        UpPatch bugUpPatch = new UpPatch();
        bugUpPatch.setBugFlag("Y");
        List<UpPatch> upPatchesForOne = upPatchService.selectUpPatchList(bugUpPatch);
        //key:有bug的记录的id  value:修复bug的记录
        Map<String,List<UpPatch>>  oneMap = reUpPatches.stream().filter(s ->s.getBugfixPatch()!=null && !"".equals(s.getBugfixPatch()))
                .collect(Collectors.groupingBy(UpPatch::getBugfixPatch));
        //key:有bug记录的id  value:有bug的记录
        Map<Long,UpPatch>  bugOneMap = upPatchesForOne.stream()
                .collect(Collectors.toMap(UpPatch::getPatchId,a->a));
        Map<String, List<UpPatch>> finalMap = oneMap;
        Map<Long, UpPatch> finalbugOneMap = bugOneMap;
        upPatchList.forEach(item -> {
            List<UpPatch> bugUpPatches = finalMap.get(""+item.getPatchId());
            String repairName = "";
            if(bugUpPatches != null && bugUpPatches.size()>0){

                for(UpPatch upPatchs : bugUpPatches){
                    if(!"".equals(repairName)){
                        repairName+="<br>";
                    }
                    repairName+="【"+upPatchs.getPatchFileName()+"】"+upPatchs.getTopic().replaceAll("\r|\n", "");
                }
                item.setRepairName(repairName);
            }
            String bugfixFlag = item.getBugfixFlag();
            String bugfixPatch = item.getBugfixPatch();
            if("Y".equals(bugfixFlag) && bugfixPatch != null && !"".equals(bugfixPatch)){
                UpPatch upPatch1 = finalbugOneMap.get(Long.parseLong(bugfixPatch));
                if(upPatch1 != null){
                    item.setBugfixName("【"+upPatch1.getPatchFileName()+"】"+upPatch1.getTopic().replaceAll("\r|\n", ""));
                }
            }
        });




        // 查询用户角色
        List<SysRole> roles = ShiroUtils.getSysUser().getRoles();
        List<Long> roleIds = roles.stream().map(SysRole::getRoleId).collect(Collectors.toList());
        UpProjectProduct upProjectProduct = new UpProjectProduct();
        upProjectProduct.setTestFlag("Y");
        List<UpProjectProduct> ppList = projectProductMapper.selectUpProjectProductList(upProjectProduct);
        for (UpPatch patch : upPatchList) {
            for (UpProjectProduct pp : ppList) {

                // 当前用户是测试角色和管理员角色可以删除
                if(roleIds.contains(4L) || roleIds.contains(1L) || roleIds.contains(5L)) {
                    patch.setTestRoleFlag(true);
                } else {
                    patch.setTestRoleFlag(false);
                }
                if(patch.getProjectId().equals(pp.getProjectId()) && patch.getProductId().equals(pp.getProductId())
                        && "Y".equals(pp.getTestFlag())) {
                    // 项目是否测试
                    patch.setTestFlag(true);
                    // 是否可编辑
                    patch.setEditFlag(false);
                    // 是否可删除
                    patch.setDeleteFlag(false);
                    break;
                } else {
                    patch.setTestFlag(false);
                    patch.setEditFlag(true);
                    patch.setDeleteFlag(true);
                }
            }
            // 当前用户是开发角色和管理员角色可以在特定状态下发现bug
            if(roleIds.contains(5L) || roleIds.contains(2L)) {
                patch.setDevRoleFlag(true);
            } else {
                patch.setDevRoleFlag(false);
            }
        }

        return upPatchList;
    }

    /**
     * 新增补丁包管理
     *
     * @param upPatch 补丁包管理
     * @return 结果
     */
    @Override
    public int insertUpPatch(UpPatch upPatch)
    {
        upPatch.setCreateBy(ShiroUtils.getSysUser().getUserName());
        upPatch.setCreateTime(DateUtils.getNowDate());
        upPatch.setUpdateBy(upPatch.getCreateBy());
        upPatch.setUpdateTime(DateUtils.getNowDate());
        return upPatchMapper.insertUpPatch(upPatch);
    }

    @Override
    public int updateUpPatchNoSession(UpPatch upPatch)
    {
        return upPatchMapper.updateUpPatch(upPatch);
    }

    @Override
    public int updateUpPatchByPatchFileName(UpPatch upPatch)
    {
        return upPatchMapper.updateUpPatchByPatchFileName(upPatch);
    }

    /**
     * 删除补丁包管理对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpPatchByIds(String ids)
    {
        String[] idList = Convert.toStrArray(ids);
        //获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();
        // 删除up_patch_codelist表中关联的信息
        upPatchCodelistMapper.deleteUpPatchCodelistByPatchIds(idList, sysUser.getUserName());
        upCommandMapper.deleteUpCommandByPatchIdsAndLoginName(idList, sysUser.getUserName());

        // 删除SQL记录表
        sqlMapper.deleteUpPatchSqlByIds(idList, sysUser.getUserName());
        return upPatchMapper.deleteUpPatchByIdsAndLoginName(idList, sysUser.getUserName());
    }

    /**
     * 删除补丁包管理信息
     *
     * @param patchId 补丁包管理ID
     * @return 结果
     */
    @Override
    public int deleteUpPatchById(Long patchId)
    {
        return upPatchMapper.deleteUpPatchById(patchId);
    }

    /**
     * 发布补丁包
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public String publish(String ids) throws Exception {
        String[] idArray =  Convert.toStrArray(ids);
        List<String> idList = Arrays.asList(idArray);
        List<UpPatch> upPatches = upPatchMapper.selectUpPatchListByIds(idList);
        List<String> patchStatus = upPatches.stream().map(UpPatch::getPatchStatus).collect(Collectors.toList());

        int patchSuccessSize = upPatches.stream().map(UpPatch::getPatchStatus).filter(s -> ConstantUtil.UpPatchStatus.PKG_SUCCESS.getValue().equals(s)).collect(Collectors.toList()).size();
        if (patchStatus.size() != patchSuccessSize) {
            return "请勾选已打包的数据！";
        }

        // 已锁定的包禁止发布
        if (patchStatus.contains(ConstantUtil.UpPatchStatus.LOCKED.getValue())) {
            return "已锁定的包禁止发布，待解锁后发布";
        }

        // 判断用户角色
        SysUser sysUser = ShiroUtils.getSysUser();
        List<SysUserRole> roleList = userRoleMapper.selectUserRoleByUserId(sysUser.getUserId());
        List<Long> roleIds = roleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        Set<String> msgList = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.YYYY_MM_DD_HH_MM_SS);

        List<Long> patchIds = new ArrayList<>();
        // 判断项目，产品是否需要测试
        for (UpPatch upPatch : upPatches) {
            Integer projectId = upPatch.getProjectId();
            Integer productId = upPatch.getProductId();
            String buildTimeStr = upPatch.getBuildTime();

            UpProjectProduct pp = new UpProjectProduct();
            pp.setProjectId(projectId);
            pp.setProductId(productId);
            List<UpProjectProduct> upProjectProducts = projectProductMapper.selectUpProjectProductList(pp);
            UpProjectProduct projectProduct = upProjectProducts.get(0);
            if("Y".equals(projectProduct.getTestFlag()) && !roleIds.contains(4L)) {
                return "【" + projectProduct.getProjectName() + "】【" + projectProduct.getProductName() + "】需要【测试】发布，请重新选择！";
            }

            // commitId方式打包， 发布时校验前面未发布的包有没有重复文件  YUNHIS-39817
            if (ConstantUtil.PatchBranch.COMMITID.getValue().equals(projectProduct.getPatchBranch())) {
                // 查询当前已打包的包
                UpPatch patch = new UpPatch();
                patch.setPatchStatus(ConstantUtil.UpPatchStatus.PKG_SUCCESS.getValue());
                patch.setProjectId(projectId);
                patch.setProductId(productId);
                List<UpPatch> patchSuccessful = upPatchMapper.getPatchListWithoutPatchId(patch, idList);
                if (!CollectionUtils.isEmpty(patchSuccessful)) {
                    List<UpPatch> patches = patchSuccessful.stream().filter(s -> !upPatch.getPatchId().equals(s.getPatchId())).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(patches)) {
                        long buildTime;
                        long patchBuildTime;
                        try {
                            buildTime = sdf.parse(buildTimeStr).getTime();
                            for (UpPatch up : patchSuccessful) {
                                // 筛选出编译时间再发布时间之前的包
                                String successBuildTime = up.getBuildTime();
                                patchBuildTime = sdf.parse(successBuildTime).getTime();
                                if (patchBuildTime < buildTime) {
                                    patchIds.add(up.getPatchId());
                                }
                            }
                        } catch (Exception e) {
                            throw new Exception("日期格式化失败！");
                        }

                        if (!CollectionUtils.isEmpty(patchIds)) {
                            // 未发布的包中有比当前要发布包的编译时间之前的包，查询代码列表是否含有重复文件
                            List<UpPatchCodeListPo> patchCodeList = upPatchCodelistMapper.getCodePathWithPublish(patchIds, productId, projectId, ConstantUtil.UpPatchStatus.PKG_SUCCESS.getValue());
                            patchCodeList.forEach(s -> s.setCodePath(s.getCodePath().replaceAll("\r|\n", "")));
                            // 待发布的代码列表
                            List<UpPatchCodelist> codeLists = upPatchCodelistMapper.selectCodeListByPatchId(upPatch.getPatchId());
                            codeLists.forEach(s -> s.setCodePath(s.getCodePath().replaceAll("\r|\n", "")));
                            List<String> codeListPublish = codeLists.stream()
                                    .distinct()
                                    .filter(s -> !"SQL".equals(s.getFileType()))
                                    .map(UpPatchCodelist::getCodePath)
                                    .collect(Collectors.toList());
                            codeListPublish.forEach(s -> s.replaceAll("\r|\n", ""));
                            if (!CollectionUtils.isEmpty(patchCodeList)) {
                                for (UpPatchCodeListPo codeListPo : patchCodeList) {
                                    if (codeListPublish.contains(codeListPo.getCodePath())) {
                                        String msg = "请注意！系统检测到有编译时间小于当前包的补丁包【" + codeListPo.getPatchFileName() + "】与当前包的文件列表有重复，且尚未发布，请先通知【" + codeListPo.getUpdateBy() + "】发布，否则将引发跨包升级问题！";
                                        msgList.add(msg);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!CollectionUtils.isEmpty(msgList)) {
            return String.join(",", msgList);
        }

        for(UpPatch upPatch : upPatches){
            //加急包发布通知项目产品负责人
            if ("Y".equals(upPatch.getExpedited())) {
                Map<String, String> patchInfo = upPatchMapper.getBugTaskName(Long.toString(upPatch.getPatchId()));
                Map<String, String> productCharge = upPatchMapper.getProductChargeName(upPatch.getProjectId(), upPatch.getProductId());
                String projectPrincipalId = MapUtils.isNotEmpty(productCharge) ? productCharge.get("id") : null;
                List<Map> users = new ArrayList<>();
                if(StringUtils.isNotEmpty(projectPrincipalId)){
                    users = sysUserMapper.selectLoginName(Arrays.asList(projectPrincipalId.split(",")));
                }
                List<String> jobNos = new ArrayList<>();
                List<String> names = new ArrayList<>();
                users.forEach(map -> {
                    jobNos.add((String) map.get("login_name"));
                    names.add((String) map.get("user_name"));
                });

                tencentMessageJob.sendExpeditedUpgradeMassage(patchInfo.get("productname"), patchInfo.get("projectname"), upPatch.getUpdateBy(), upPatch.getPatchFileName(), upPatch.getJiraNo(), upPatch.getTopic(), jobNos, names);
            }
            upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PUBLISHED.getValue());
            upPatch.setPublicBy(sysUser.getUserName());
            upPatch.setUpdateTime(new Date());
            upPatchMapper.updateUpPatch(upPatch);
        }
        return "true";
    }

    @Override
    public List<UpPatch> selectUpPatchListByIds(String ids) {
        String[] idStr = ids.split(",");
        List<String> idList = Arrays.asList(idStr);
        List<UpPatch> upPatches = upPatchMapper.selectUpPatchListByIds(idList);
        return upPatches;
    }

    @Override
    public List<UpPatch> selectUpPatchListByParentPatchId(Long parentPatchId) {
        List<UpPatch> upPatches = upPatchMapper.selectUpPatchListByParentPatchId(parentPatchId);
        return upPatches;
    }

    @Override
    public List<UpPatch> selectUpPatchListByPatchFileName(String patchFileName) {
        List<UpPatch> upPatches = upPatchMapper.selectUpPatchListByPatchFileName(patchFileName);
        return upPatches;
    }

    /**
     * 修改子补丁
     *
     * @param upPatch 补丁
     * @return 结果
     */
    @Override
    public int updateUpPatchClientChild(UpPatch upPatch)
    {
        return upPatchMapper.updateUpPatchClientChild(upPatch);
    }

    /**
     * 发现Bug 查询范围：当天的包
     * @param patchId
     * @return
     */
    @Override
    public void findBug(Long patchId) {

        SysUser sysUser = ShiroUtils.getSysUser();
        // 当前补丁包退回状态
        // 查询相关文件代码列表的补丁包，打锁定状态
        UpPatch up = upPatchMapper.selectUpPatchById(patchId);
        //开发、测试 已锁定状态(77)点击【发现BUG】按钮弹出提示
        if ("77".equals(up.getPatchStatus())){
            //查询被锁定的包
            UpPatch lockup =upPatchMapper.selectUpPatchById(Long.parseLong(up.getLockId()));
            throw new BusinessException( "该补丁包由于文件列表与已发现BUG的包【"+lockup.getPatchFileName()+"】有关联，故状态已锁定。请联系"+lockup.getPublicBy()+"，等待其修复后再执行操作");
        }
        UpPatchCodelist code = new UpPatchCodelist();
        code.setPatchId(patchId);
        List<UpPatchCodelist> codeList = upPatchCodelistMapper.selectUpPatchCodelistList(code);
        List<String> codePathList = new ArrayList<>();
        for (UpPatchCodelist patchCode : codeList) {
            String path = patchCode.getCodePath().replaceAll("\r|\n", "");
            codePathList.add(path);
        }

        // 当天补丁包中有关联bug文件的包
        UpPatch patch = new UpPatch();
        patch.setCreateTime(new Date());
        patch.setPatchStatus(ConstantUtil.UpPatchStatus.PKG_SUCCESS.getValue());
        patch.setProductId(up.getProductId());
        patch.setProjectId(up.getProjectId());
        List<UpPatch> upPatches = upPatchMapper.selectUpPatchList(patch);

        List<String> idList = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        if(!CollectionUtils.isEmpty(upPatches)){
            List<Long> pathIds = upPatches.stream().map(UpPatch::getPatchId).collect(Collectors.toList());
            List<UpPatchCodelist> upPatchCodeList = upPatchCodelistMapper.selectUpPatchCodeListByPatchIds(pathIds);
            for (UpPatchCodelist patchCodeList : upPatchCodeList) {
                String codePath = patchCodeList.getCodePath().replaceAll("\r|\n", "");
                if(codePathList.contains(codePath)) {
                    Long id = patchCodeList.getPatchId();
                    if(id.equals(patchId)) {
                        continue;
                    }
                    idList.add(String.valueOf(patchCodeList.getPatchId()));
                }
            }

            if(!CollectionUtils.isEmpty(idList)) {
                ids = idList.stream().distinct().collect(Collectors.toList());
                List<UpPatch> patches = upPatchMapper.selectUpPatchListByIds(ids);
                if (!CollectionUtils.isEmpty(patches)) {
                    patches.forEach(s -> {
                        s.setPatchStatus(ConstantUtil.UpPatchStatus.LOCKED.getValue());
                        s.setPublicBy(sysUser.getUserName());
                        s.setUpdateTime(new Date());
                    });
                    for (UpPatch ch : patches) {
                        upPatchMapper.updateUpPatch(ch);
                    }
                }
            }
        }

        UpPatch upPatch = new UpPatch();
        upPatch.setPatchId(patchId);
        upPatch.setPublicBy(sysUser.getUserName());
        upPatch.setUpdateTime(new Date());
        //若为已打包状态，发现bug后状态更改为已回退。其余不变
        if ("20".equals(up.getPatchStatus())){
            upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.ROLLBACK.getValue());
        }
        upPatch.setBugFlag("Y");
        Integer bugCount = up.getBugCount();
        upPatch.setBugCount(++bugCount);
        if (!CollectionUtils.isEmpty(ids)) {
            String lockIds = String.join(",", ids);
            upPatch.setLockId(lockIds);
        }
        upPatchMapper.updateUpPatch(upPatch);

//        List<SysRole> roles = sysUser.getRoles();
//        Map<String, String> info = upPatchMapper.getMessageInfo(patchId);
//        //获取项目负责人userid
//        Map<String, String> productCharge = upPatchMapper.getProductChargeName(up.getProjectId(), up.getProductId());
//        List<String> jobnoList = new ArrayList<>();
//        if (productCharge != null) {
//            String[] userids = productCharge.get("id").split(",");
//            for (String id : userids) {
//                //获取项目产品负责人工号
//                String chargeJobNo = upPatchMapper.getChargeJobNo(id);
//                jobnoList.add(chargeJobNo);
//            }
//        }
//        TencentMessage textMsg = new TencentMessage();
//        MessageText textText = new MessageText();
//        textMsg.setMsgtype("text");
//        textMsg.setText(textText);
//        for (SysRole sysRole : roles) {
//            //判断该用户角色
//            if ("tester".equals(sysRole.getRoleKey())) {
//                //测试发现bug通知研发
//                textText.setContent("补丁包【" + info.get("name") + "】发现BUG，请" + info.get("updateBy") + "按照BUG包流程及时处理。\n");
//                textText.setMentioned_list(Arrays.asList(info.get("id")));
//                tencentMessageJob.sendMessage(textMsg, hisKey);
//            }
//        }
    }

    /**
     * 修复Bug
     * @param upPatch
     * @return
     */
    @Override
    public void repairPatch(UpPatch upPatch) throws Exception {
        //获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();
        upPatch.setCreateBy(sysUser.getUserName());
        upPatch.setUpdateBy(sysUser.getUserName());
        upPatch.setUpdateByUserId(sysUser.getUserId());
        upPatch.setBugfixFlag("Y");
        try {
            upPatchService.registPatch(upPatch);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /**
     * 获取补丁包登记参数
     * @return
     */
    @Override
    public Map<String, Object> getRegistPatchParams() {

        Map<String, Object> map = new HashMap<>();
/*        // 获取产品项目级联列表
        List<CxSelect> productList = upProductService.selectProjectAndProduct(null);
        map.put("projectAndProduct", productList);*/

        List<CxSelect> productList = new ArrayList<>();

        // 获取产品项目级联列表
        List<UpProduct> upProducts = upProductService.selectUpProductList(new UpProduct());
        if (StringUtils.isEmpty(upProducts)) {
            map.put("projectAndProduct", productList);
            return map;
        }

        for (UpProduct upProduct : upProducts) {

            // 查询所有子项目
            List<String> idList = new ArrayList<>();
            Integer productId = upProduct.getProductId();
            List<UpProjectProduct> ppList = upProjectProductMapper.getSubProjectByProductId(productId);
            if (!CollectionUtils.isEmpty(ppList)) {
                List<String> subIdList = ppList.stream().map(UpProjectProduct::getSubProjectId).collect(Collectors.toList());
                subIdList.forEach(s -> {
                    String[] ids = s.split(",");
                    idList.addAll(Arrays.asList(ids));
                });
            }

            UpProjectDTO projectDTO = new UpProjectDTO();
            projectDTO.setIdList(idList);
            projectDTO.setProductId(productId);
            List<UpProjectProduct> upProjectProducts = projectProductMapper.getByProductId(projectDTO);

            if (StringUtils.isNotEmpty(upProjectProducts)) {
                List<CxSelect> projectList = new ArrayList<>();
                for (UpProjectProduct projectProduct : upProjectProducts) {
                    // 项目下拉框对象
                    CxSelect cxSelectUpProject = new CxSelect();
                    // 项目下拉name
                    cxSelectUpProject.setN(projectProduct.getProjectName());
                    // 项目下拉value
                    cxSelectUpProject.setV(projectProduct.getProjectId() + "");
                    // 项目下列列表
                    projectList.add(cxSelectUpProject);
                }
                // 产品下拉框对象
                CxSelect cxSelectUpProduct = new CxSelect();
                // 产品下拉name
                cxSelectUpProduct.setN(upProduct.getProductName());
                // 产品下拉value
                cxSelectUpProduct.setV(productId + "");
                // 产品联动的项目列表
                cxSelectUpProduct.setS(projectList);
                productList.add(cxSelectUpProduct);
            }
        }
        map.put("projectAndProduct", productList);
        return map;
    }

    /**
     * 查询打包分支
     * @param productId
     * @param projectId
     * @return
     */
    @Override
    public String searchPatchBranch(Integer productId, Integer projectId) {

        UpProjectProduct projectProduct = projectProductMapper.getUpProjectProductById(productId, projectId);
        if (!Objects.isNull(projectProduct)) {
            return projectProduct.getPatchBranch();
        }
        return "";
    }

    /**
     * 根据提交序号查询代码列表
     * @param commitId
     */
    private List<Map<String, Object>> getCodeListByCommitId(String commitId, String codeListPath) throws Exception {

        String[] ids = commitId.trim().split("#");
        List<Map<String, Object>> valuesList = new ArrayList<>();
        for (String id : ids) {
            if(!StringUtils.isEmpty(id)) {
                List<Map<String, Object>> mapList;
                String[] cmds = new String[]{"curl", "-u", "zhanghaoyu:960808aAa", "-XGET", codeListPath + id + "/changes?start=0&limit=1000", "-H", "X-Requested-By:sdc"};
                GitFilePath gitFilePath = new GitFilePath();
                String resultStr = gitFilePath.execCurl(cmds);
                if (!StringUtils.isEmpty(resultStr)) {
                    Map<String, Object> resultMap = CommonUtils.JsonUtil.json2Map(resultStr);
                    mapList = (List)resultMap.get("values");
                    if (!org.apache.commons.collections.CollectionUtils.isEmpty(mapList)) {
                        valuesList.addAll(mapList);
                    } else {
                        throw new Exception("提交序号【" + id +"】查询失败，请重新查询");
                    }
                }
            }
        }
        return valuesList;
    }

    /**
     * 根据提交序号查询合并CommitId
     * @param commitId
     */
    private Map<String, Object> getMergeIdByCommitId(String commitId, String codeListPath) throws Exception {

        String[] ids = commitId.trim().split("#");
        Map<String, Map<String, Object>> map = new HashMap<>();
        List<String> times = new ArrayList<>();
        for (String id : ids) {
            if(!StringUtils.isEmpty(id)) {
                String[] cmds = new String[]{"curl", "-u", "zhanghaoyu:960808aAa", "-XGET", codeListPath + id + "/activities?start=0&limit=50&avatarSize=64&markup=true", "-H", "X-Requested-By:sdc"};
                GitFilePath gitFilePath = new GitFilePath();
                String resultStr = gitFilePath.execCurl(cmds);
                if (!StringUtils.isEmpty(resultStr)) {

                    JSONObject jsonObject = JSON.parseObject(resultStr);
                    JSONArray values = jsonObject.getJSONArray("values");
                    Map<String, Object> paramMap = new HashMap<>();

                    for (Object value : values) {
                        JSONObject value1 = (JSONObject)JSON.toJSON(value);
                        if ("MERGED".equals(value1.get("action") + "")) {
                            JSONObject commit = value1.getJSONObject("commit");
                            if (commit == null) {
                                throw new Exception("根据提交序号没有找到合并请求的commitId，请检查！");
                            }
                            String mergeId = commit.getString("id");

                            Date authorTime = commit.getDate("authorTimestamp");
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String mergeTime = df.format(authorTime);

//                            String message = commit.getString("message").replaceAll("：", ":");
//                            if (message.contains("任务:") && message.contains("说明:") && message.contains("作者:")) {
//                                String taskId = message.substring(message.indexOf("任务:") + 3, message.indexOf("说明:")).trim();
//                                String taskName = message.substring(message.indexOf("说明:") + 3, message.indexOf("作者:")).trim();
//                                paramMap.put("taskId", taskId);
//                                paramMap.put("taskName", taskName);
//                            }
                            String msg = commit.getString("message");
                            if (msg.contains("【")) {
                                String taskId = msg.substring(msg.indexOf(":\n") + 2, msg.indexOf("【")).trim();
                                String taskName = msg.substring(msg.indexOf("【"));
                                paramMap.put("taskId", taskId);
                                paramMap.put("taskName", taskName);
                            } else {
                                paramMap.put("taskId", "");
                                paramMap.put("taskName", "");
                            }

                            paramMap.put("commitId", mergeId);
                            paramMap.put("mergeTime", mergeTime);

                            map.put(mergeTime, paramMap);
                            times.add(mergeTime);
                            break;
                        }
                    }

                    if (paramMap.isEmpty()) {
                        throw new Exception("提交序号的合并请求尚未合并，请检查！");
                    }
                }
            }
        }
        String time = times.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList()).get(0);
        return map.get(time);
    }

    /**
     * 根据提交序号查询代码列表
     * @param commitId 提交id
     * @param productId 产品id
     * @param projectId 项目id
     * @return
     *
     *
     *
     * HIS分模块代码结构： 2023/05/10
     *
     * pom为Jar类型的模块,src/main/webapp下的资源不打到Jar包里
     * pom不为Jar类型的模块，所有资源都不打到Jar包里
     *
     */
    @Override
    public Map<String, Object> searchCode(String commitId, Integer productId, Integer projectId) throws Exception {

        if (StringUtils.isEmpty(commitId)) {
            return new HashMap<>();
        }


        // 查询项目是否包含子项目, 项目Id取第一个子项目Id
        if (productId != 7) {
            UpProjectProduct projectProduct = projectProductMapper.getUpProjectProductById(productId, projectId);
            if (StringUtils.isNotEmpty(projectProduct.getSubProjectId())) {
                String subProjectId = projectProduct.getSubProjectId();
                String[] subProjectIds = subProjectId.split(",");
                projectId = Integer.parseInt(subProjectIds[0]);
            }
        }

        // lis项目查询第一个
        if (productId == 7) {
            UpProjectProduct upProjectProduct = new UpProjectProduct();
            upProjectProduct.setProductId(7);
            List<UpProjectProduct> upProjectProducts = projectProductMapper.selectByProductIdAndProjectId(upProjectProduct);
            if (!CollectionUtils.isEmpty(upProjectProducts)) {
                projectId = upProjectProducts.get(0).getProjectId();
            }
        }

        String codeListPath = "";
        ConstantUtil.CodeResp[] values = ConstantUtil.CodeResp.values();
        for (ConstantUtil.CodeResp value : values) {
            if(value.getKey() == productId){
                codeListPath = value.getValue();
                break;
            }
        }

        List<Map<String, Object>> valuesList = getCodeListByCommitId(commitId, codeListPath);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(valuesList)) {
            return new HashMap<>();
        }
        // 查询项目的编译方式
        UpProjectProduct projectProduct = projectProductMapper.getUpProjectProductById(productId, projectId);

        // 获取产品
        List<UpProduct> products = productMapper.selectUpProductList(new UpProduct());
        Map<Integer, String> productMap = products.stream().collect(Collectors.toMap(UpProduct::getProductId, UpProduct::getProductShortName));

        // 获取模块名
        UpProjectModule projectModule = new UpProjectModule();
        projectModule.setProductId(productId);
        projectModule.setProjectId(projectId);
        List<UpProjectModule> modules = moduleMapper.selectUpProjectModuleList(projectModule);

        List<String> addOrUpdatePath = new ArrayList<>();
        Set<String> delete = new HashSet<>();
        Set<String> move = new HashSet<>();
        String pathHeader = "his";

        if (!productMap.isEmpty()) {
            for(Integer id : productMap.keySet()){
                if (productId == id) {
                    pathHeader = productMap.get(productId);
                    break;
                }
            }
        }

        // 真实代码路径
        int j=0;
        List<String> realPath = new ArrayList<>();
        // 移动类型文件 源文件jar
        Set<String> srcPathJarNames = new HashSet<>();
        for (Map<String, Object> map : valuesList) {
            String type = map.get("type") + "";
            Map<String, Object> pathMap = (Map)map.get("path");
            String jiraKey = pathMap.get("toString") + "";

            String realpath = "";
            if (j == valuesList.size() - 1) {
                realpath = jiraKey;
            } else {
                realpath = jiraKey + ",\n";
            }
            j++;

            // 移动类型的文件，判断源文件是不是模块，以及模块是不是jar类型
            String srcPath = "";
            if ("MOVE".equals(type)) {
                // 源路径
                Map<String, Object> srcPathMap = (Map)map.get("srcPath");
                srcPath = srcPathMap.get("toString") + "";
            }

            if (!"DELETE".equals(type)) {
                // 代码规范校验时，新增文件进行严格校验
                if ("ADD".equals(type)) {
                    realPath.add("ADD_" + realpath);
                } else {
                    realPath.add(realpath);
                }
            }

            // 如果是移动类型的文件，源文件不是jar类型，需要指定删除文件；如果是jar类型且编译路径没有该jar路径，则需要填入改路径
            // SQL和pom文件的移动，不做限制
            if ("MOVE".equals(type) && !srcPath.toUpperCase().endsWith("SQL") && !srcPath.contains("pom")) {
                if (srcPath.contains("/src")) {
                    // 待加入编译路径的jar
                    String srcPathHeadName = srcPath.substring(0,srcPath.indexOf("/src"));
                    srcPathJarNames.add(srcPathHeadName);
                } else {
                    move.add(srcPath + ",\n");
                }
            }

            // 如果是删除文件类型，且删除文件不在jar包里，需指定删除文件
            if ("DELETE".equals(type) && (realpath.startsWith("src") || realpath.contains("webapp"))) {
                delete.add(realpath);
            }


            if (!Objects.isNull(projectProduct) && !(ConstantUtil.BuildType.CLASS_BUILD.getValue().equals(projectProduct.getBuildType()) && !jiraKey.contains("-view/"))) {
                // src/main/webapp下的静态资源不在jar包里
                if (jiraKey.contains("src/main/webapp")) {
                    jiraKey = jiraKey.substring(jiraKey.indexOf("src"));
                }
//                if (!jiraKey.startsWith("src") && !jiraKey.substring(jiraKey.lastIndexOf(".") + 1).toLowerCase().endsWith("sql") && !jiraKey.contains("pom") && !jiraKey.endsWith("iml") && !jiraKey.contains("README")) {
                    if (!CollectionUtils.isEmpty(modules) && jiraKey.contains("/src")) {
                        String headName = jiraKey.substring(0,jiraKey.indexOf("/src"));
                        for (UpProjectModule module : modules) {
                            String moduleName = module.getModuleName();
                            // 文件路径是jar类型，且模块名匹配
                            if ("jar".equals(module.getPackageType())) {

                                if (moduleName.equals(headName)) {
                                    if(moduleName.split("/").length > 1) {
                                        moduleName = moduleName.substring(moduleName.lastIndexOf("/") + 1);
                                    }
                                    jiraKey = "src/main/webapp/WEB-INF/lib/" + moduleName + "-" + module.getJarVersion() + ".jar";
                                    break;
                                }
                            } else {
                                jiraKey = jiraKey.substring(jiraKey.indexOf("src"));
                            }
                        }
                    }
//                }
            }
            if (!"DELETE".equals(type)) {
                addOrUpdatePath.add(jiraKey);
            }
        }

        List<String> codePathList = new ArrayList<>();
        // 移动类型，源文件为jar路径不为空
        if (!CollectionUtils.isEmpty(srcPathJarNames) && !ConstantUtil.BuildType.CLASS_BUILD.getValue().equals(projectProduct.getBuildType())) {
            Map<String, String> moduleMap = modules.stream().filter(s -> "jar".equals(s.getPackageType()))
                    .collect(Collectors.toMap(UpProjectModule::getModuleName, UpProjectModule::getPackageType));

            for (String srcPathJarName : srcPathJarNames) {
                if (!addOrUpdatePath.contains(srcPathJarName) && !CollectionUtils.isEmpty(modules)) {
                    // 判断模块是不是jar类型
                    String packageType = moduleMap.get(srcPathJarName);
                    if(srcPathJarName.split("/").length > 1) {
                        srcPathJarName = srcPathJarName.substring(srcPathJarName.lastIndexOf("/") + 1);
                    }
                    if (StringUtils.isNotEmpty(packageType)) {
                        addOrUpdatePath.add("src/main/webapp/WEB-INF/lib/" + srcPathJarName + "-" + modules.get(0).getJarVersion() + ".jar");
                    }
                }
            }
        }
        List<String> addPathList = addOrUpdatePath.stream().distinct().collect(Collectors.toList());
        for (int i=0; i<addPathList.size(); i++) {
            String path = addPathList.get(i);
            String codePath;
            StringBuilder sb = new StringBuilder().append(pathHeader).append("/").append(path);
            // 处理代码路径中的产品头和换行
            if(i == addPathList.size()-1) {
                codePath = sb.toString();
            } else {
                codePath = sb.append(",\n").toString();
            }
            codePathList.add(codePath);
        }

        // 返回真实路径和替换后的路径
        Map<String, Object> map = new HashMap<>();

        // 获取任务号，任务名，合并CommitId
        Map<String, Object> mergeMap = getMergeIdByCommitId(commitId , codeListPath);

        // commitId打包方式，校验分支合并时间是否超过两小时
        String patchBranch = projectProduct.getPatchBranch();
        if (ConstantUtil.PatchBranch.COMMITID.getValue().equals(patchBranch)) {
            map.put("timeout", false);
            String mergeTime = mergeMap.get("mergeTime").toString().replaceAll(":|-", "").replaceAll(" ", "");
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            String twoDateAgo = df.format(calendar.getTime());
            if (Long.parseLong(twoDateAgo) > Long.parseLong(mergeTime)) {
                map.put("timeout", true);
            }

            map.put("commitId", mergeMap.get("commitId") + "");
            map.put("mergeTime", mergeMap.get("mergeTime") + "");
        }
        map.put("taskId", mergeMap.get("taskId") + "");
        map.put("taskName", mergeMap.get("taskName") + "");
        map.put("real", realPath);
        map.put("replace", codePathList);
        map.put("delete", delete);
        map.put("move", move);
        return map;
    }


    /**
     * 根据提交序号查询代码列表 (新版定时器打包)
     * @param commitId 提交id
     * @param productId 产品id
     * @param projectId 项目id
     * @return
     */
    @Override
    public Map<String, Object> searchCodeByHisScheduled(String commitId, Integer productId, Integer projectId) throws Exception {

        if (StringUtils.isEmpty(commitId)) {
            return new HashMap<>();
        }

        String codeListPath = "";
        ConstantUtil.CodeResp[] values = ConstantUtil.CodeResp.values();
        for (ConstantUtil.CodeResp value : values) {
            if(value.getKey() == productId){
                codeListPath = value.getValue();
                break;
            }
        }

        List<Map<String, Object>> valuesList = getCodeListByCommitId(commitId, codeListPath);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(valuesList)) {
            return new HashMap<>();
        }
        // 查询项目的编译方式
        UpProjectProduct projectProduct = projectProductMapper.getUpProjectProductById(productId, projectId);

        /*// 获取产品
        List<UpProduct> products = productMapper.selectUpProductList(new UpProduct());
        Map<Integer, String> productMap = products.stream().collect(Collectors.toMap(UpProduct::getProductId, UpProduct::getProductShortName));*/

        // 获取模块名
        UpProjectModule projectModule = new UpProjectModule();
        projectModule.setProductId(productId);
        projectModule.setProjectId(projectId);
        List<UpProjectModule> modules = moduleMapper.selectUpProjectModuleList(projectModule);

        List<String> warNameList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(modules)) {
            warNameList = modules.stream().filter(s -> "war".equals(s.getPackageType())).map(UpProjectModule::getModuleName).collect(Collectors.toList());
        }

        List<String> addOrUpdatePath = new ArrayList<>();
        Set<String> delete = new HashSet<>();
        Set<String> move = new HashSet<>();
        String pathHeader = "his";

        /*if (!productMap.isEmpty()) {
            for(Integer id : productMap.keySet()){
                if (productId == id) {
                    pathHeader = productMap.get(productId);
                    break;
                }
            }
        }*/

        // 真实代码路径
        int j=0;
        List<String> realPath = new ArrayList<>();
        // 移动类型文件 源文件jar
        Set<String> srcPathJarNames = new HashSet<>();
        for (Map<String, Object> map : valuesList) {
            String type = map.get("type") + "";
            Map<String, Object> pathMap = (Map)map.get("path");
            String jiraKey = pathMap.get("toString") + "";

            String realpath = "";
            if (j == valuesList.size() - 1) {
                realpath = jiraKey;
            } else {
                realpath = jiraKey + ",\n";
            }
            j++;

            // 移动类型的文件，判断源文件是不是模块，以及模块是不是jar类型
            String srcPath = "";
            if ("MOVE".equals(type)) {
                // 源路径
                Map<String, Object> srcPathMap = (Map)map.get("srcPath");
                srcPath = srcPathMap.get("toString") + "";
            }

            if (!"DELETE".equals(type)) {
                // 代码规范校验时，新增文件进行严格校验
                if ("ADD".equals(type)) {
                    realPath.add("ADD_" + realpath);
                } else {
                    realPath.add(realpath);
                }
            }

            // 如果是移动类型的文件，源文件不是jar类型，需要指定删除文件；如果是jar类型且编译路径没有该jar路径，则需要填入改路径
            // SQL和pom文件的移动，不做限制
            if ("MOVE".equals(type) && !srcPath.toUpperCase().endsWith("SQL") && !srcPath.contains("pom")) {
                if (srcPath.contains("/src")) {
                    // 待加入编译路径的jar
                    String srcPathHeadName = srcPath.substring(0,srcPath.indexOf("/src"));
                    srcPathJarNames.add(srcPathHeadName);
                } else {
                    move.add(srcPath + ",\n");
                }
            }

            // 如果是删除文件类型，且删除文件不在jar包里，需指定删除文件
            if ("DELETE".equals(type) && (realpath.startsWith("src") || realpath.contains("webapp"))) {
                delete.add(realpath);
            }

            // Jar模块路径集合
            if (!Objects.isNull(projectProduct) && !ConstantUtil.BuildType.CLASS_BUILD.getValue().equals(projectProduct.getBuildType())) {
                // TODO War包模块都引用了Jar模块,需要War包每个模块lib下都要引入该Jar
                if (!CollectionUtils.isEmpty(modules) && jiraKey.contains("/src")) {
                    String headName = jiraKey.substring(0,jiraKey.indexOf("/src"));
                    for (UpProjectModule module : modules) {
                        List<String> jarPath = new ArrayList<>();
                        String moduleName = module.getModuleName();
                        // 模块名匹配
                        if ("jar".equals(module.getPackageType()) && moduleName.equals(headName)  && !warNameList.isEmpty()) {
                            if(moduleName.split("/").length > 1) {
                                moduleName = moduleName.substring(moduleName.lastIndexOf("/") + 1);
                            }
                            for (String warName : warNameList) {
                                String path = warName + "/src/main/webapp/WEB-INF/lib/" + moduleName + "-" + module.getJarVersion() + ".jar";
                                if (jarPath.contains(path)) {
                                    continue;
                                }
                                jarPath.add(path);
                            }
                            addOrUpdatePath.addAll(jarPath);
                        } else  {
                            if (jiraKey.startsWith(moduleName)) {
                                if (!"DELETE".equals(type)) {
                                    addOrUpdatePath.add(jiraKey);
                                }
                            }
                        }
                    }
                } else {
                    if (!"DELETE".equals(type)) {
                        addOrUpdatePath.add(jiraKey);
                    }
                }
            }
        }

        List<String> codePathList = new ArrayList<>();
        // 移动类型，源文件为jar路径不为空
        if (!CollectionUtils.isEmpty(srcPathJarNames)) {
            Map<String, String> moduleMap = modules.stream().filter(s -> "jar".equals(s.getPackageType()))
                    .collect(Collectors.toMap(UpProjectModule::getModuleName, UpProjectModule::getPackageType));

            for (String srcPathJarName : srcPathJarNames) {
                if (!addOrUpdatePath.contains(srcPathJarName) && !CollectionUtils.isEmpty(modules)) {
                    // 判断模块是不是jar类型
                    String packageType = moduleMap.get(srcPathJarName);
                    if(srcPathJarName.split("/").length > 1) {
                        srcPathJarName = srcPathJarName.substring(srcPathJarName.lastIndexOf("/") + 1);
                    }
                    if (StringUtils.isNotEmpty(packageType)) {
                        addOrUpdatePath.add("src/main/webapp/WEB-INF/lib/" + srcPathJarName + "-" + modules.get(0).getJarVersion() + ".jar");
                    }
                }
            }
        }
        List<String> addPathList = addOrUpdatePath.stream().distinct().collect(Collectors.toList());
        for (int i=0; i<addPathList.size(); i++) {
            String path = addPathList.get(i);
            String codePath;
            StringBuilder sb = new StringBuilder().append(path);
            // 处理代码路径中的产品头和换行
            if(i == addPathList.size()-1) {
                codePath = sb.toString();
            } else {
                codePath = sb.append(",\n").toString();
            }
            codePathList.add(codePath);
        }

        // 返回真实路径和替换后的路径
        Map<String, Object> map = new HashMap<>();

        // 获取任务号，任务名，合并CommitId
        Map<String, Object> mergeMap = getMergeIdByCommitId(commitId , codeListPath);
        // commitId打包方式，校验分支合并时间是否超过两小时
        String patchBranch = projectProduct.getPatchBranch();
        if (ConstantUtil.PatchBranch.COMMITID.getValue().equals(patchBranch)) {
            String mergeTime = mergeMap.get("mergeTime").toString().replaceAll(":|-", "").replaceAll(" ", "");
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -1);
            String twoDateAgo = df.format(calendar.getTime());
            if (Long.parseLong(twoDateAgo) > Long.parseLong(mergeTime)) {
                throw new Exception("提交序号的合并请求已过期，请重新提交合并请求！");
            }

            map.put("commitId", mergeMap.get("commitId") + "");
            map.put("mergeTime", mergeMap.get("mergeTime") + "");
            map.put("taskId", mergeMap.get("taskId") + "");
        }

        map.put("taskId", mergeMap.get("taskId") + "");
        map.put("taskName", mergeMap.get("taskName") + "");

        map.put("real", realPath);
        map.put("replace", codePathList);
        map.put("delete", delete);
        map.put("move", move);
        return map;
    }

    /**
     * 解析pom文件获取jar包版本(目前先手动维护版本号)
     * @return String 版本号
     */
    @Deprecated
    private String JarversionWithPom() throws Exception{

//        UpProjectModule module = new UpProjectModule();
//        module.setProductId(1);
//        module.setProjectId(28);
//        // 查询每个产品的模块（和项目有关时再关联项目Id）
//        List<UpProjectModule> upProjectModules = projectModuleMapper.getModuleList(module);
//        if (!CollectionUtils.isEmpty(upProjectModules)) {
//            List<String> moduleNames = upProjectModules.stream().map(UpProjectModule::getModuleName).distinct().collect(Collectors.toList());
//            for (String name : moduleNames) {
//                String path = workspacePath + File.separator + name + File.separator;
//                File pathFile = new File(path);
//                String pom = path + "pom.xml";
//                File pomFile = new File(pom);
//                if (pathFile.exists() && pomFile.exists()) {
//
//                }
//            }
//        }

        // his-parent pom文件路径
        String pomPath = workspacePath + File.separator + "his-parent" + File.separator + "pom.xml";
        File pathFile = new File(pomPath);
        if (!pathFile.exists()) {
            throw new Exception("his-parent模块中pom文件不存在：" + pomPath);
        }
        SAXReader sax = new SAXReader();
        String version = "";
        try {
            // DOM4J
            Document document = sax.read(new File(pomPath));
            Element root = document.getRootElement();
            //遍历当前元素(在此是根元素)的子元素
            Map<String, String> map = new HashMap<>();
            Iterator<Element> itr = root.elementIterator();
            while (itr.hasNext()){
                Element element = itr.next();
                //获取当前元素的名字
                String name = element.getName();
                if ("properties".equals(name)) {
                    String revision = element.elementText("revision");
                    String changelist = element.elementText("changelist");
                    if (StringUtils.isEmpty(revision)) {
                        throw new Exception("revision版本号为空,请检查his-parent模块中,pom文件属性properties是否定义版本号");
                    }
                    version = revision;
                    if (!StringUtils.isEmpty(changelist)) {
                        version = revision + changelist;
                    }
                    log.info("版本号revision为: " + revision + "changelist为: " + changelist);
                }
            }
        } catch (Exception e) {
            log.info("获取模块版本号错误" + e.getMessage());
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 根据任务号或需求号查询补丁包
     * @param jiraNo
     * @return
     */
    @Override
    public List<Map> searchPatchs(String jiraNo, String demandNo, Integer productId, Integer projectId) {
        return upPatchMapper.searchPatchs(jiraNo,demandNo,productId,projectId);
    }

    /**
     * 设置加急状态
     * @param ids
     * @param status
     */
    @Override
    public void setExpeditedStatus(String ids, String status) {
        String[] idStr = ids.split(",");
        List<String> idList = Arrays.asList(idStr);
        upPatchMapper.setExpeditedStatus(idList,status);
    }

    /**
     * 已发布未升级的包则通知负责人
     */
    @Override
    public void sendExpeditedUpgradeMassage(String upPatchStatus, String projectIdStr, String productIdStr, String jiraNoStr, String topicStr, String productNameStr, String projectNameStr, String developerStr, String patchFileNameStr) {
        String[] patchStatus = upPatchStatus.split(",");
        String[] projectIds = projectIdStr.split(",");
        String[] productIds = productIdStr.split(",");
        String[] productNames = productNameStr.split(",");
        String[] projectNames = projectNameStr.split(",");
        String[] developers = developerStr.split(",");
        String[] patchFileNames = patchFileNameStr.split(",");
        String[] jiraNos = jiraNoStr.split(",");
        String[] topics = topicStr.split(",");
        for (int i = 0; i < productIds.length; i++) {
            //已打包状态不需要通知企业微信项目产品负责人
            if("20".equals(patchStatus[i])){
                continue;
            }
            String projectId = projectIds[i];
            String productId = productIds[i];
            UpProjectProduct projectProduct = projectProductMapper.getUpProjectProductById(Integer.valueOf(productId), Integer.valueOf(projectId));
            String projectPrincipalId = projectProduct.getProjectPrincipalId();
            List<Map> users = new ArrayList<>();
            if(StringUtils.isNotEmpty(projectPrincipalId)){
                users = sysUserMapper.selectLoginName(Arrays.asList(projectPrincipalId.split(",")));
            }
            List<String> jobNos = new ArrayList<>();
            List<String> names = new ArrayList<>();
            users.forEach(map -> {
                jobNos.add((String) map.get("login_name"));
                names.add((String) map.get("user_name"));
            });
            tencentMessageJob.sendExpeditedUpgradeMassage(productNames[i], projectNames[i], developers[i], patchFileNames[i], jiraNos[i], topics[i],jobNos,names);
        }
    }
}
