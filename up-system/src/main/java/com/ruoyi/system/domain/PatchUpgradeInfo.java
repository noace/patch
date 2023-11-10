package com.ruoyi.system.domain;


import java.math.BigInteger;

/**
 * @description：补丁包升级信息
 * @author：DangHangHang
 * @date：2023-07-31
 */
public class PatchUpgradeInfo {

    private BigInteger projectId;

    private String projectName;

    private String lastUpgradeTime;

    private Long oneMonthPublish;

    private Integer oneMonthUpgrade;

    private String patchFileName;

    public BigInteger getProjectId() {
        return projectId;
    }

    public void setProjectId(BigInteger projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getLastUpgradeTime() {
        return lastUpgradeTime;
    }

    public void setLastUpgradeTime(String lastUpgradeTime) {
        this.lastUpgradeTime = lastUpgradeTime;
    }

    public Long getOneMonthPublish() {
        return oneMonthPublish;
    }

    public void setOneMonthPublish(Long oneMonthPublish) {
        this.oneMonthPublish = oneMonthPublish;
    }

    public Integer getOneMonthUpgrade() {
        return oneMonthUpgrade;
    }

    public void setOneMonthUpgrade(Integer oneMonthUpgrade) {
        this.oneMonthUpgrade = oneMonthUpgrade;
    }

    public String getPatchFileName() {
        return patchFileName;
    }

    public void setPatchFileName(String patchFileName) {
        this.patchFileName = patchFileName;
    }
}
