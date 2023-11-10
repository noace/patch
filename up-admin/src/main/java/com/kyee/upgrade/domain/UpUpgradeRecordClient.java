package com.kyee.upgrade.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 升级管理对象 up_upgrade_record_client
 * 
 * @author zhanghaoyu
 * @date 2021-10-21
 */
public class  UpUpgradeRecordClient extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long upgradeId;

    /** 服务ID */
    @Excel(name = "服务ID")
    private Integer serverId;

    /** 服务名 */
    @Excel(name = "服务名")
    private String serverName;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Integer projectId;

    /** 项目 */
    @Excel(name = "项目")
    private String projectName;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Integer productId;

    /** 产品 */
    @Excel(name = "产品")
    private String productName;

    /** 补丁ID */
    @Excel(name = "补丁ID")
    private Long patchId;

    /** 补丁文件名 */
    @Excel(name = "补丁文件名")
    private String patchFileName;

    /** 升级状态 */
    @Excel(name = "升级状态")
    private String upStatus;

    /** 升级次数 */
    @Excel(name = "升级次数")
    private Integer upTimes;

    /** 最后升级人员 */
    @Excel(name = "最后升级人员")
    private String lastUpWorker;

    /** 最后升级时间 */
    @Excel(name = "最后升级时间")
    private Date lastUpTime;

    public void setUpgradeId(Long upgradeId) 
    {
        this.upgradeId = upgradeId;
    }

    public Long getUpgradeId() 
    {
        return upgradeId;
    }
    public void setServerId(Integer serverId) 
    {
        this.serverId = serverId;
    }

    public Integer getServerId() 
    {
        return serverId;
    }
    public void setPatchId(Long patchId) 
    {
        this.patchId = patchId;
    }

    public Long getPatchId() 
    {
        return patchId;
    }
    public void setUpStatus(String upStatus) 
    {
        this.upStatus = upStatus;
    }

    public String getUpStatus() 
    {
        return upStatus;
    }
    public void setUpTimes(Integer upTimes) 
    {
        this.upTimes = upTimes;
    }

    public Integer getUpTimes() 
    {
        return upTimes;
    }
    public void setLastUpWorker(String lastUpWorker) 
    {
        this.lastUpWorker = lastUpWorker;
    }

    public String getLastUpWorker() 
    {
        return lastUpWorker;
    }
    public void setLastUpTime(Date lastUpTime) 
    {
        this.lastUpTime = lastUpTime;
    }

    public Date getLastUpTime() 
    {
        return lastUpTime;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getPatchFileName() {
        return patchFileName;
    }

    public void setPatchFileName(String patchFileName) {
        this.patchFileName = patchFileName;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "UpUpgradeRecordClient{" +
                "upgradeId=" + upgradeId +
                ", serverId=" + serverId +
                ", serverName=" + serverName +
                ", patchId=" + patchId +
                ", patchFileName=" + patchFileName +
                ", upStatus='" + upStatus + '\'' +
                ", upTimes=" + upTimes +
                ", lastUpWorker='" + lastUpWorker + '\'' +
                ", lastUpTime=" + lastUpTime +
                '}';
    }
}
