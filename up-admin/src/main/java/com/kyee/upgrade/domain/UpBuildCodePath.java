package com.kyee.upgrade.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 代码变更路径对象 up_build_code_path
 * 
 * @author lijunqiang
 * @date 2021-10-22
 */
public class UpBuildCodePath extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 代码路径Id */
    private Long codePathId;

    /** 代码提交Id */
    @Excel(name = "代码提交Id")
    private String commitId;

    /** 代码路径 */
    @Excel(name = "代码路径")
    private String codePath;

    /** 代码变更类型 */
    @Excel(name = "代码变更类型")
    private String codeChangeType;

    public void setCodePathId(Long codePathId) 
    {
        this.codePathId = codePathId;
    }

    public Long getCodePathId() 
    {
        return codePathId;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public void setCodePath(String codePath)
    {
        this.codePath = codePath;
    }

    public String getCodePath() 
    {
        return codePath;
    }
    public void setCodeChangeType(String codeChangeType) 
    {
        this.codeChangeType = codeChangeType;
    }

    public String getCodeChangeType() 
    {
        return codeChangeType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("codePathId", getCodePathId())
            .append("commitId", getCommitId())
            .append("codePath", getCodePath())
            .append("codeChangeType", getCodeChangeType())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
