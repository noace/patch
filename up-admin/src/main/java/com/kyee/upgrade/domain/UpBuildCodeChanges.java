package com.kyee.upgrade.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 构建日志对象 up_build_code_changes
 * 
 * @author lijunqiang
 * @date 2021-10-21
 */
public class UpBuildCodeChanges extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 变更记录Id */
    @Excel(name = "变更记录Id")
    private Long codeChangeId;

    /** 补丁包Id */
    @Excel(name = "补丁包Id")
    private Long patchId;

    /** 提交Id */
    @Excel(name = "提交Id")
    private String commitId;

    /** 提交时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "提交时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date commitTime;

    /** 提交人 */
    @Excel(name = "提交人")
    private String commitPerson;

    /** 提交日志 */
    @Excel(name = "提交日志")
    private String commitMessage;

    /** 代码路径 */
    @Excel(name = "代码路径")
    private String codePath;

    /** 操作类型 */
    @Excel(name = "操作类型")
    private String changeType;

    public void setCodeChangeId(Long codeChangeId) 
    {
        this.codeChangeId = codeChangeId;
    }

    public Long getCodeChangeId() 
    {
        return codeChangeId;
    }

    public Long getPatchId() {
        return patchId;
    }

    public void setPatchId(Long patchId) {
        this.patchId = patchId;
    }

    public void setCommitId(String commitId)
    {
        this.commitId = commitId;
    }

    public String getCommitId() 
    {
        return commitId;
    }
    public void setCommitTime(Date commitTime) 
    {
        this.commitTime = commitTime;
    }

    public Date getCommitTime() 
    {
        return commitTime;
    }
    public void setCommitPerson(String commitPerson) 
    {
        this.commitPerson = commitPerson;
    }

    public String getCommitPerson() 
    {
        return commitPerson;
    }
    public void setCommitMessage(String commitMessage) 
    {
        this.commitMessage = commitMessage;
    }

    public String getCommitMessage() 
    {
        return commitMessage;
    }
    public void setCodePath(String codePath) 
    {
        this.codePath = codePath;
    }

    public String getCodePath() 
    {
        return codePath;
    }
    public void setChangeType(String changeType) 
    {
        this.changeType = changeType;
    }

    public String getChangeType() 
    {
        return changeType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("codeChangeId", getCodeChangeId())
            .append("commitId", getCommitId())
            .append("commitTime", getCommitTime())
            .append("commitPerson", getCommitPerson())
            .append("commitMessage", getCommitMessage())
            .append("delFlag", getDelFlag())
            .append("codePath", getCodePath())
            .append("remark", getRemark())
            .append("changeType", getChangeType())
            .toString();
    }
}
