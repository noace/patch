package com.kyee.upgrade.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 补丁包管理对象 up_patch
 *
 * @author lijunqiang
 * @date 2021-06-12
 */
public class UpPatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 补丁ID */
    private Long patchId;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Integer projectId;

    /** 项目名 */
    @Excel(name = "项目名")
    private String projectName;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Integer productId;

    /** 产品名 */
    @Excel(name = "产品名")
    private String productName;

    /** JIRA任务号 */
    @Excel(name = "任务号")
    private String jiraNo;

    /** 任务名 */
    @Excel(name = "任务名")
    private String topic;

    /** 补丁文件名 */
    @Excel(name = "补丁文件名")
    private String patchFileName;

    /** 补丁文件URL */
    @Excel(name = "补丁文件URL")
    private String patchFileUrl;

    /** 该补丁是否含SQL脚本 */
    @Excel(name = "该补丁是否含SQL脚本")
    private String sqlFlag;

    /** 补丁状态 */
    @Excel(name = "补丁状态")
    private String patchStatus;

    /** 任务列表 */
    @Excel(name = "任务列表")
    private String taskList;

    /** 代码列表 */
    @Excel(name = "代码列表")
    private String codeList;

    /** 真实代码列表 */
    @Excel(name = "真实代码列表")
    private String realCodeList;

    /** 该补丁是否有bug */
    @Excel(name = "该补丁是否有bug")
    private String bugFlag;

    /** 该补丁是否用于修复bug */
    @Excel(name = "该补丁是否用于修复bug")
    private String bugfixFlag;

    /** 修复哪个补丁号的bug */
    @Excel(name = "修复哪个补丁号的bug")
    private String bugfixPatch;

    /** 构建时间 */
    @Excel(name = "构建时间")
    private String buildTime;

    /** 责任人 */
    @Excel(name = "创建责任人")
    private String createBy;

    /** 责任人 */
    @Excel(name = "更新责任人")
    private String updateBy;

    /** bug分类 */
    @Excel(name = "bug分类")
    private String bugType;

    /** bug级别 */
    @Excel(name = "bug级别")
    private String bugLevel;

    /** bug描述 */
    @Excel(name = "bug描述")
    private String bugRemark;

    /** 是否是合包 */
    @Excel(name = "子包父ID")
    private String mergePackageFlag;

    /** 修改配置文件标识 */
    @Excel(name = "修改配置文件标识")
    private String configFlag;

    /** 配置文件列表 */
    @Excel(name = "修改配置文件内容")
    private String configList;

    /** 补丁包类型 */
    @Excel(name = "补丁包类型")
    private String patchType;

    /** 提交序号 */
    @Excel(name = "提交序号")
    private String commitId;

    /** 发布人 */
    @Excel(name = "发布人")
    private String publicBy;

    /** 锁定Id */
    @Excel(name = "锁定Id")
    private String lockId;

    /** BUG次数 */
    @Excel(name = "BUG次数")
    private Integer bugCount;

    /** 是否测试 */
    @Excel(name = "是否测试,Y是N否")
    private Boolean testFlag;

    /** 是否可编辑 */
    @Excel(name = "是否可编辑,Y是N否")
    private Boolean editFlag;

    /** 是否可删除 */
    @Excel(name = "是否可删除,Y是N否")
    private Boolean deleteFlag;

    /** 是否测试角色 */
    @Excel(name = "是否测试角色,Y是N否")
    private Boolean testRoleFlag;

    /** 是否开发角色 */
    @Excel(name = "是否开发角色,Y是N否")
    private Boolean devRoleFlag;

    /** 是否第三方视图 */
    @Excel(name = "是否第三方视图,Y是N否")
    private String thirdPartView;

    /** 分支Id */
    @Excel(name = "分支Id")
    private String branchId;

    /** 需求号 */
    @Excel(name = "需求号")
    private String demandNo;

    /** 删除的代码路径 */
    @Excel(name = "删除的代码路径")
    private String deletePath;

    /** 移动的代码路径 */
    @Excel(name = "移动的代码路径")
    private String movePath;

    /** 需求名 */
    @Excel(name = "需求名")
    private String demandName;
    /** 修复的bug任务的名称 */
    private String bugfixName;
    /** 修复的bug任务的任务号 */
    private String bugfixJirano;
    /** 修复的任务 */
    private String repairName;
    /** 修复的任务不带任务号 */
    private String repairNameNoNum;
    /** 合包中的bug包任务 */
    private String bugName;
    /** 合包中的bug包任务责任人 */
    private String bugDutyName;
    /** 合包中的bug包任务是否有bug还未修复 */
    private String bugNoFix;

    /** 修复的原bug包包名 */
    private String bugPatchFileName;

    /** 合包中如果有bug任务，bug任务的修复任务是否全在该合包中 */
    private String mergePackageAllFix;

    /** 所有子包的包名 */
    private String childPackageName;

    /** 任务类型 */
    @Excel(name = "任务类型")
    private String taskType;

    /** 加急包 */
    @Excel(name = "加急包")
    private String expedited;

    /** 合并时间 */
    @Excel(name = "合并时间")
    private String mergeTime;

    /** 更新责任人的id */
    private Long updateByUserId;

    public String getTaskType() {
        return taskType;
    }

    public String getDemandNo() {
        return demandNo;
    }

    public void setDemandNo(String demandNo) {
        this.demandNo = demandNo;
    }

    public String getDemandName() {
        return demandName;
    }

    public void setDemandName(String demandName) {
        this.demandName = demandName;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getBugPatchFileName() {
        return bugPatchFileName;
    }

    public void setBugPatchFileName(String bugPatchFileName) {
        this.bugPatchFileName = bugPatchFileName;
    }

    @Override
    public String getUpdateBy() {
        return updateBy;
    }

    @Override
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public void setPatchId(Long patchId)
    {
        this.patchId = patchId;
    }

    public Long getPatchId()
    {
        return patchId;
    }
    public void setProductId(Integer productId)
    {
        this.productId = productId;
    }

    public Integer getProductId()
    {
        return productId;
    }
    public void setProjectId(Integer projectId)
    {
        this.projectId = projectId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getProjectId()
    {
        return projectId;
    }
    public void setPatchFileName(String patchFileName)
    {
        this.patchFileName = patchFileName;
    }

    public String getPatchFileName()
    {
        return patchFileName;
    }
    public void setPatchFileUrl(String patchFileUrl)
    {
        this.patchFileUrl = patchFileUrl;
    }

    public String getPatchFileUrl()
    {
        return patchFileUrl;
    }
    public void setSqlFlag(String sqlFlag)
    {
        this.sqlFlag = sqlFlag;
    }

    public String getSqlFlag()
    {
        return sqlFlag;
    }
    public void setPatchStatus(String patchStatus)
    {
        this.patchStatus = patchStatus;
    }

    public String getPatchStatus()
    {
        return patchStatus;
    }
    public void setTaskList(String taskList)
    {
        this.taskList = taskList;
    }

    public String getTaskList()
    {
        return taskList;
    }
    public void setBugFlag(String bugFlag)
    {
        this.bugFlag = bugFlag;
    }

    public String getCodeList() {
        return codeList;
    }

    public void setCodeList(String codeList) {
        this.codeList = codeList;
    }

    public String getBugFlag()
    {
        return bugFlag;
    }
    public void setBugfixFlag(String bugfixFlag)
    {
        this.bugfixFlag = bugfixFlag;
    }

    public String getBugfixFlag()
    {
        return bugfixFlag;
    }
    public void setBugfixPatch(String bugfixPatch)
    {
        this.bugfixPatch = bugfixPatch;
    }

    public String getBugfixPatch()
    {
        return bugfixPatch;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }

    public void setBugType(String bugType)
    {
        this.bugType = bugType;
    }

    public String getBugType()
    {
        return bugType;
    }

    public String getBugLevel() {
        return bugLevel;
    }

    public void setBugLevel(String bugLevel) {
        this.bugLevel = bugLevel;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getJiraNo() {
        return jiraNo;
    }

    public void setJiraNo(String jiraNo) {
        this.jiraNo = jiraNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBugRemark() {
        return bugRemark;
    }

    public void setBugRemark(String bugRemark) {
        this.bugRemark = bugRemark;
    }

    public String getMergePackageFlag() {
        return mergePackageFlag;
    }

    public void setMergePackageFlag(String mergePackageFlag) {
        this.mergePackageFlag = mergePackageFlag;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getConfigFlag() {
        return configFlag;
    }

    public void setConfigFlag(String configFlag) {
        this.configFlag = configFlag;
    }

    public String getConfigList() {
        return configList;
    }

    public void setConfigList(String configList) {
        this.configList = configList;
    }

    public String getPatchType() {
        return patchType;
    }

    public void setPatchType(String patchType) {
        this.patchType = patchType;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getPublicBy() {
        return publicBy;
    }

    public void setPublicBy(String publicBy) {
        this.publicBy = publicBy;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }

    public Integer getBugCount() {
        return bugCount;
    }

    public void setBugCount(Integer bugCount) {
        this.bugCount = bugCount;
    }

    public Boolean getTestFlag() {
        return testFlag;
    }

    public void setTestFlag(Boolean testFlag) {
        this.testFlag = testFlag;
    }

    public Boolean getEditFlag() {
        return editFlag;
    }

    public void setEditFlag(Boolean editFlag) {
        this.editFlag = editFlag;
    }

    public Boolean getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Boolean deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public Boolean getTestRoleFlag() {
        return testRoleFlag;
    }

    public void setTestRoleFlag(Boolean testRoleFlag) {
        this.testRoleFlag = testRoleFlag;
    }

    public String getThirdPartView() {
        return thirdPartView;
    }

    public void setThirdPartView(String thirdPartView) {
        this.thirdPartView = thirdPartView;
    }

    public String getRealCodeList() {
        return realCodeList;
    }

    public void setRealCodeList(String realCodeList) {
        this.realCodeList = realCodeList;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public Boolean getDevRoleFlag() {
        return devRoleFlag;
    }

    public void setDevRoleFlag(Boolean devRoleFlag) {
        this.devRoleFlag = devRoleFlag;
    }

    public String getBugfixName() {
        return bugfixName;
    }

    public void setBugfixName(String bugfixName) {
        this.bugfixName = bugfixName;
    }

    public String getBugfixJirano() {
        return bugfixJirano;
    }

    public void setBugfixJirano(String bugfixJirano) {
        this.bugfixJirano = bugfixJirano;
    }

    public String getRepairName() {
        return repairName;
    }

    public void setRepairName(String repairName) {
        this.repairName = repairName;
    }

    public String getBugName() {
        return bugName;
    }

    public void setBugName(String bugName) {
        this.bugName = bugName;
    }

    public String getBugDutyName() {
        return bugDutyName;
    }

    public void setBugDutyName(String bugDutyName) {
        this.bugDutyName = bugDutyName;
    }

    public String getBugNoFix() {
        return bugNoFix;
    }

    public void setBugNoFix(String bugNoFix) {
        this.bugNoFix = bugNoFix;
    }

    public String getMergePackageAllFix() {
        return mergePackageAllFix;
    }

    public void setMergePackageAllFix(String mergePackageAllFix) {
        this.mergePackageAllFix = mergePackageAllFix;
    }

    public String getExpedited() {
        return expedited;
    }
    public void setExpedited(String expedited) {
        this.expedited = expedited;
    }
    public String getRepairNameNoNum() {
        return repairNameNoNum;
    }

    public void setRepairNameNoNum(String repairNameNoNum) {
        this.repairNameNoNum = repairNameNoNum;
    }

    public String getChildPackageName() {
        return childPackageName;
    }

    public void setChildPackageName(String childPackageName) {
        this.childPackageName = childPackageName;
    }

    public String getMergeTime() {
        return mergeTime;
    }

    public void setMergeTime(String mergeTime) {
        this.mergeTime = mergeTime;
    }

    public Long getUpdateByUserId() {
        return updateByUserId;
    }

    public void setUpdateByUserId(Long updateByUserId) {
        this.updateByUserId = updateByUserId;
    }

    public String getDeletePath() {
        return deletePath;
    }

    public void setDeletePath(String deletePath) {
        this.deletePath = deletePath;
    }

    public String getMovePath() {
        return movePath;
    }

    public void setMovePath(String movePath) {
        this.movePath = movePath;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("patchId", getPatchId())
            .append("projectId", getProjectId())
            .append("projectName", getProjectName())
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("jiraNo", getJiraNo())
            .append("patchFileName", getPatchFileName())
            .append("patchFileUrl", getPatchFileUrl())
            .append("sqlFlag", getSqlFlag())
            .append("patchStatus", getPatchStatus())
            .append("taskList", getTaskList())
            .append("bugFlag", getBugFlag())
            .append("bugfixFlag", getBugfixFlag())
            .append("bugfixPatch", getBugfixPatch())
            .append("buildTime", getBuildTime())
            .append("bugType", getBugType())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .append("expedited", getExpedited())
            .toString();
    }
}
