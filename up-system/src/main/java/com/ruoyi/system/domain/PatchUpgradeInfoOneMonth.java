package com.ruoyi.system.domain;

import java.math.BigInteger;

/**
 * @description：补丁包升级信息
 * @author：zhangyuanrui
 * @date：2023-09-06
 */
public class PatchUpgradeInfoOneMonth {

    /** 补丁编号 */
    private BigInteger projectId;

    /** 项目名称 */
    private String projectName;

    /** 项目最后一次升级时间 */
    private String lastUpgradeTime;

    /** 项目最近一个月发包数 */
    private Long oneMonthPublish;

    /** 项目最近一个月升级数 */
    private Long oneMonthUpgrade;

    /** 发包任务号 */
    private String jiraNo;

    /** 升级任务号 */
    private String jiraNoUp;

    /** 项目最近一次发包时间 */
    private String newUpdateTime;

    public String getJiraNoUp() {
        return jiraNoUp;
    }

    public void setJiraNoUp(String jiraNoUp) {
        this.jiraNoUp = jiraNoUp;
    }

    public String getNewUpdateTime() {
        return newUpdateTime;
    }

    public void setNewUpdateTime(String newUpdateTime) {
        this.newUpdateTime = newUpdateTime;
    }

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

    public Long getOneMonthUpgrade() {
        return oneMonthUpgrade;
    }

    public void setOneMonthUpgrade(Long oneMonthUpgrade) {
        this.oneMonthUpgrade = oneMonthUpgrade;
    }

    public String getJiraNo() {
        return jiraNo;
    }

    public void setJiraNo(String jiraNo) {
        this.jiraNo = jiraNo;
    }

}
