package com.kyee.upgrade.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 升级日志对象 up_upgrade_log_client
 * 
 * @author lijunqiang
 * @date 2022-04-24
 */
public class UpUpgradeLogClient extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Integer logId;

    /** 进程ID */
    @Excel(name = "进程ID")
    private String processId;

    /** 服务ID */
    @Excel(name = "服务ID")
    private Integer serverId;

    /** 补丁ID */
    @Excel(name = "补丁ID")
    private Long patchId;

    /** 升级人员 */
    @Excel(name = "升级人员")
    private String upWorker;

    /** 升级时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "升级时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date upTime;

    /** 日志内容 */
    @Excel(name = "日志内容")
    private String logContent;

    /** 升级前数据库版本 */
    @Excel(name = "升级前数据库版本")
    private String preDbVersion;

    /** 升级前服务版本 */
    @Excel(name = "升级前服务版本")
    private String preServerVersion;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public Long getPatchId() {
        return patchId;
    }

    public void setPatchId(Long patchId) {
        this.patchId = patchId;
    }

    public String getUpWorker() {
        return upWorker;
    }

    public void setUpWorker(String upWorker) {
        this.upWorker = upWorker;
    }

    public Date getUpTime() {
        return upTime;
    }

    public void setUpTime(Date upTime) {
        this.upTime = upTime;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public String getPreDbVersion() {
        return preDbVersion;
    }

    public void setPreDbVersion(String preDbVersion) {
        this.preDbVersion = preDbVersion;
    }

    public String getPreServerVersion() {
        return preServerVersion;
    }

    public void setPreServerVersion(String preServerVersion) {
        this.preServerVersion = preServerVersion;
    }
}
