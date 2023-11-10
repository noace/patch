package com.kyee.upgrade.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 补丁包代码列对象 up_patch_codelist
 * 
 * @author lijunqiang
 * @date 2021-09-23
 */
public class UpPatchCodelist extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 代码ID */
    private Long codeId;

    /** 补丁ID */
    @Excel(name = "补丁ID")
    private Long patchId;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Integer productId;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Integer projectId;

    /** 代码路径 */
    @Excel(name = "代码路径")
    private String codePath;

    /** 编译路径 */
    @Excel(name = "编译路径")
    private String compilePath;

    /** 文件类型,java文件：JAVA,SQL文件：SQL,其他文件：OTHER */
    @Excel(name = "文件类型,java文件：JAVA,SQL文件：SQL,其他文件：OTHER")
    private String fileType;

    public void setCodeId(Long codeId) 
    {
        this.codeId = codeId;
    }

    public Long getCodeId() 
    {
        return codeId;
    }
    public void setPatchId(Long patchId) 
    {
        this.patchId = patchId;
    }

    public Long getPatchId() 
    {
        return patchId;
    }
    public void setProductId(Integer productId) 
    {
        this.productId = productId;
    }

    public Integer getProductId() 
    {
        return productId;
    }
    public void setProjectId(Integer projectId) 
    {
        this.projectId = projectId;
    }

    public Integer getProjectId() 
    {
        return projectId;
    }
    public void setCodePath(String codePath) 
    {
        this.codePath = codePath;
    }

    public String getCodePath() 
    {
        return codePath;
    }
    public void setFileType(String fileType) 
    {
        this.fileType = fileType;
    }

    public String getFileType() 
    {
        return fileType;
    }

    public String getCompilePath() {
        return compilePath;
    }

    public void setCompilePath(String compilePath) {
        this.compilePath = compilePath;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("codeId", getCodeId())
            .append("patchId", getPatchId())
            .append("productId", getProductId())
            .append("projectId", getProjectId())
            .append("codePath", getCodePath())
            .append("compilePath", getCompilePath())
            .append("fileType", getFileType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
