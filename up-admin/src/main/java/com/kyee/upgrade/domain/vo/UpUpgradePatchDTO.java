package com.kyee.upgrade.domain.vo;

import com.ruoyi.common.core.domain.BaseEntity;

import java.util.List;

public class UpUpgradePatchDTO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 产品id */
    private Integer productId;

    /** 项目id */
    private Integer projectId;

    /** 产品名 */
    private String productName;

    /** 项目名 */
    private String projectName;

    /** 该补丁是否含SQL脚本 */
    private String sqlFlag;

    /** 补丁状态 */
    private String patchStatus;

    /** 是否是合包 */
    private String mergePackageFlag;

    /** 是否拉取包标识 */
    private String pulledFlag;

    /** 是否列表第一个标识 */
    private boolean firstPatchFlag;

    /** 补丁包类型 */
    private String patchType;

    /** 一键升级 */
    private String autoUpgradeFlag;

    /** 测试状态 */
    private String testStatus;

    /** 服务ID */
    private Integer serverId;

    /** 服务类型 */
    private String serverType;

    /** 是否显示历史升级记录 */
    private String upgradeRecordFlag;

    /** 补丁包状态集合 */
    private List<String> patchStatusList;

    /** 测试状态集合 */
    private List<String> testStatusList;

    /** 补丁ID */
    private Long patchId;

    /** 补丁包构建时间 */
    private String buildTime;

    public String getUpgradeRecordFlag() {
        return upgradeRecordFlag;
    }

    public void setUpgradeRecordFlag(String upgradeRecordFlag) {
        this.upgradeRecordFlag = upgradeRecordFlag;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getSqlFlag() {
        return sqlFlag;
    }

    public void setSqlFlag(String sqlFlag) {
        this.sqlFlag = sqlFlag;
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

    public String getPatchType() {
        return patchType;
    }

    public void setPatchType(String patchType) {
        this.patchType = patchType;
    }

    public String getAutoUpgradeFlag() {
        return autoUpgradeFlag;
    }

    public void setAutoUpgradeFlag(String autoUpgradeFlag) {
        this.autoUpgradeFlag = autoUpgradeFlag;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public List<String> getPatchStatusList() {
        return patchStatusList;
    }

    public void setPatchStatusList(List<String> patchStatusList) {
        this.patchStatusList = patchStatusList;
    }

    public List<String> getTestStatusList() {
        return testStatusList;
    }

    public void setTestStatusList(List<String> testStatusList) {
        this.testStatusList = testStatusList;
    }

    public Long getPatchId() {
        return patchId;
    }

    public void setPatchId(Long patchId) {
        this.patchId = patchId;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(String buildTime) {
        this.buildTime = buildTime;
    }
}
