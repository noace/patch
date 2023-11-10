package com.kyee.upgrade.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 补丁对象 up_patch_client
 *
 * @author zhanghaoyu
 * @date 2021-10-21
 */
public class UpPatchClient extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 补丁ID */
    private Long patchId;

    /** 产品id */
    private Integer productId;

    /** 项目id */
    private Integer projectId;

    /** 产品名 */
    @Excel(name = "产品名")
    private String productName;

    /** 项目名 */
    @Excel(name = "项目名")
    private String projectName;

    /** 补丁文件名 */
    @Excel(name = "补丁文件名")
    private String patchFileName;

    /** 补丁文件URL */
    @Excel(name = "补丁文件URL")
    private String patchFileUrl;

    /** 该补丁是否含SQL脚本 */
    @Excel(name = "该补丁是否含SQL脚本")
    private String sqlFlag;

    /** 上传人 */
    @Excel(name = "上传人")
    private String upBy;

    /** 补丁包构建时间 */
    @Excel(name = "补丁包构建时间")
    private String buildTime;

    /** 补丁状态 */
    @Excel(name = "补丁状态")
    private String patchStatus;

    /** 是否是合包 */
    @Excel(name = "子包父ID")
    private String mergePackageFlag;

    /** 修改配置文件标识 */
    @Excel(name = "修改配置文件标识")
    private String configFlag;

    /** 修改配置文件标识 */
    @Excel(name = "修改配置文件内容")
    private String configList;

    /** JIRA任务号 */
    @Excel(name = "任务号")
    private String jiraNo;

    /** 主题 */
    @Excel(name = "主题")
    private String topic;

    /** 代码列表 */
    @Excel(name = "代码列表")
    private String codeList;

    /** 真实代码列表 */
    @Excel(name = "真实代码列表")
    private String realCodeList;

    /** 编译列表 */
    @Excel(name = "编译列表")
    private String compileList;

    /** 是否拉取包标识 */
    @Excel(name = "是否拉取包标识")
    private String pulledFlag;

    /** 是否列表第一个标识 */
    @Excel(name = "是否列表第一个标识")
    private boolean firstPatchFlag;

    /** 补丁包类型 */
    @Excel(name = "补丁包类型")
    private String patchType;

    /** 一键升级 */
    @Excel(name = "一键升级")
    private String autoUpgradeFlag;

    /** 加急包 */
    @Excel(name = "加急包")
    private String expedited;

    /** 任务类型 */
    @Excel(name = "任务类型")
    private String taskType;

    /** 需求号 */
    @Excel(name = "需求号")
    private String demandNo;

    /** 需求名 */
    @Excel(name = "需求名")
    private String demandName;

    /** 该补丁是否有bug */
    private String bugFlag;

    /** 该补丁是否用于修复bug */
    private String bugfixFlag;

    /** 修复哪个补丁号的bug */
    private String bugfixPatch;

    /** 修复哪个补丁号的bug的名称 */
    private String bugfixName;
    /** 修复哪个补丁号的bug的任务号 */
    private String bugfixJirano;
    /** 修复的任务号 */
    private String repairName;
    /** 修复的任务不带任务号 */
    private String repairNameNoNum;
    /** 合包中的bug包任务 */
    private String bugName;
    /** 合包中的bug包任务责任人 */
    private String bugDutyName;
    /** 合包中的bug包任务是否有bug还未修复 */
    private String bugNoFix;

    /** 合包中如果有bug任务，bug任务的修复任务是否全在该合包中 */
    private String mergePackageAllFix;

    /** 修复的原bug包包名 */
    private String bugPatchFileName;

    /** 所有子包的包名 */
    private String childPackageName;

    /** 测试状态 */
    private String testStatus;

    /** 已拉取的patchId*/
    private List<Long> pulledList;

    public String getBugPatchFileName() {
        return bugPatchFileName;
    }

    public void setBugPatchFileName(String bugPatchFileName) {
        this.bugPatchFileName = bugPatchFileName;
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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getPatchStatus() {
        return patchStatus;
    }

    public void setPatchStatus(String patchStatus) {
        this.patchStatus = patchStatus;
    }

    public String getMergePackageFlag() {
        return mergePackageFlag;
    }

    public void setMergePackageFlag(String mergePackageFlag) {
        this.mergePackageFlag = mergePackageFlag;
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

    public String getJiraNo() {
        return jiraNo;
    }

    public void setJiraNo(String jiraNo) {
        this.jiraNo = jiraNo;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCodeList() {
        return codeList;
    }

    public void setCodeList(String codeList) {
        this.codeList = codeList;
    }

    public void setPatchId(Long patchId)
    {
        this.patchId = patchId;
    }

    public Long getPatchId()
    {
        return patchId;
    }
    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductName()
    {
        return productName;
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
    public void setUpBy(String upBy)
    {
        this.upBy = upBy;
    }

    public String getUpBy()
    {
        return upBy;
    }
    public void setBuildTime(String buildTime)
    {
        this.buildTime = buildTime;
    }

    public String getBuildTime()
    {
        return buildTime;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPulledFlag() {
        return pulledFlag;
    }

    public void setPulledFlag(String pulledFlag) {
        this.pulledFlag = pulledFlag;
    }

    public boolean isFirstPatchFlag() {
        return firstPatchFlag;
    }

    public void setFirstPatchFlag(boolean firstPatchFlag) {
        this.firstPatchFlag = firstPatchFlag;
    }

    public String getCompileList() {
        return compileList;
    }

    public void setCompileList(String compileList) {
        this.compileList = compileList;
    }

    public String getPatchType() {
        return patchType;
    }

    public void setPatchType(String patchType) {
        this.patchType = patchType;
    }

    public String getRealCodeList() {
        return realCodeList;
    }

    public void setRealCodeList(String realCodeList) {
        this.realCodeList = realCodeList;
    }

    public String getAutoUpgradeFlag() {
        return autoUpgradeFlag;
    }

    public void setAutoUpgradeFlag(String autoUpgradeFlag) {
        this.autoUpgradeFlag = autoUpgradeFlag;
    }

    public String getExpedited() {
        return expedited;
    }
    public void setExpedited(String expedited) {
        this.expedited = expedited;
    }
    public String getBugFlag() {
        return bugFlag;
    }

    public void setBugFlag(String bugFlag) {
        this.bugFlag = bugFlag;
    }

    public String getBugfixFlag() {
        return bugfixFlag;
    }

    public void setBugfixFlag(String bugfixFlag) {
        this.bugfixFlag = bugfixFlag;
    }

    public String getBugfixPatch() {
        return bugfixPatch;
    }

    public void setBugfixPatch(String bugfixPatch) {
        this.bugfixPatch = bugfixPatch;
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

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public List<Long> getPulledList() {
        return pulledList;
    }

    public void setPulledList(List<Long> pulledList) {
        this.pulledList = pulledList;
    }

    @Override
    public String toString() {
        return "UpPatchClient{" +
                "patchId=" + patchId +
                ", productName='" + productName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", patchFileName='" + patchFileName + '\'' +
                ", patchFileUrl='" + patchFileUrl + '\'' +
                ", sqlFlag='" + sqlFlag + '\'' +
                ", upBy='" + upBy + '\'' +
                ", buildTime='" + buildTime + '\'' +
                ", patchStatus='" + patchStatus + '\'' +
                ", mergePackageFlag='" + mergePackageFlag + '\'' +
                ", configFlag='" + configFlag + '\'' +
                ", configList='" + configList + '\'' +
                ", jiraNo='" + jiraNo + '\'' +
                ", topic='" + topic + '\'' +
                ", codeList='" + codeList + '\'' +
                ", compileList='" + compileList + '\'' +
                ", expedited='" + expedited + '\'' +
                '}';
    }
}
