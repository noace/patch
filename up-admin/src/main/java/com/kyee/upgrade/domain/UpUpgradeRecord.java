package com.kyee.upgrade.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 升级管理对象 up_upgrade_record
 * 
 * @author lijunqiang
 * @date 2021-06-13
 */
public class UpUpgradeRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long upgradeId;

    /** 服务ID */
    @Excel(name = "服务ID")
    private Integer serverId;

    /** 补丁ID */
    @Excel(name = "补丁ID")
    private Long patchId;

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
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后升级时间", width = 30, dateFormat = "yyyy-MM-dd")
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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("upgradeId", getUpgradeId())
            .append("serverId", getServerId())
            .append("patchId", getPatchId())
            .append("upStatus", getUpStatus())
            .append("upTimes", getUpTimes())
            .append("lastUpWorker", getLastUpWorker())
            .append("lastUpTime", getLastUpTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
