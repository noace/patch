package com.kyee.upgrade.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 执行SQL对象 up_patch_sql
 * 
 * @author lijunqiang
 * @date 2022-10-20
 */
public class UpPatchSql extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 补丁ID */
    @Excel(name = "补丁ID")
    private Long patchId;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Integer projectId;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Integer productId;

    /** 项目名称 */
    @Excel(name = "项目名称")
    private String projectName;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 补丁包名称 */
    @Excel(name = "补丁包名称")
    private String patchFileName;

    /** sql名称 */
    @Excel(name = "sql名称")
    private String sqlName;

    /** sql路径 */
    @Excel(name = "sql路径")
    private String sqlPath;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setPatchId(Long patchId) 
    {
        this.patchId = patchId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPatchFileName() {
        return patchFileName;
    }

    public void setPatchFileName(String patchFileName) {
        this.patchFileName = patchFileName;
    }

    public Long getPatchId()
    {
        return patchId;
    }
    public void setSqlName(String sqlName) 
    {
        this.sqlName = sqlName;
    }

    public String getSqlName() 
    {
        return sqlName;
    }
    public void setSqlPath(String sqlPath) 
    {
        this.sqlPath = sqlPath;
    }

    public String getSqlPath() 
    {
        return sqlPath;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("patchId", getPatchId())
            .append("sqlName", getSqlName())
            .append("sqlPath", getSqlPath())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}
