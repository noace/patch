package com.kyee.upgrade.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 构建日志对象 up_build_log
 * 
 * @author lijunqiang
 * @date 2021-10-20
 */
public class UpBuildLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 构建Id */
    private Long buildId;

    /** 补丁包Id */
    @Excel(name = "补丁包Id")
    private Long patchId;

    /** 标识某次构建 */
    @Excel(name = "进程Id")
    private String processId;

    /** 构建类型 */
    @Excel(name = "构建类型")
    private String buildType;

    /** 构建日志 */
    @Excel(name = "构建日志")
    private String buildLog;

    /** 构建人员 */
    @Excel(name = "构建人员")
    private String buildWorker;

    /** 构建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "构建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date buildTime;

    public void setBuildId(Long buildId) 
    {
        this.buildId = buildId;
    }

    public Long getBuildId() 
    {
        return buildId;
    }
    public void setBuildLog(String buildLog) 
    {
        this.buildLog = buildLog;
    }

    public Long getPatchId() {
        return patchId;
    }

    public void setPatchId(Long patchId) {
        this.patchId = patchId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getBuildLog()
    {
        return buildLog;
    }
    public void setBuildWorker(String buildWorker) 
    {
        this.buildWorker = buildWorker;
    }

    public String getBuildWorker() 
    {
        return buildWorker;
    }
    public void setBuildTime(Date buildTime) 
    {
        this.buildTime = buildTime;
    }

    public Date getBuildTime() 
    {
        return buildTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("buildId", getBuildId())
            .append("patchId", getPatchId())
            .append("processId", getProcessId())
            .append("buildType", getBuildType())
            .append("buildLog", getBuildLog())
            .append("buildWorker", getBuildWorker())
            .append("buildTime", getBuildTime())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
