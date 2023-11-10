package com.kyee.upgrade.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 待执行命令对象 up_workspace_source
 * 
 * @author lijunqiang
 * @date 2021-10-18
 */
public class UpWorkspaceSource extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 资源ID */
    private Long sourceId;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Integer productId;

    /** 资源状态（0未使用，1使用中） */
    @Excel(name = "资源状态", readConverterExp = "0=未使用，1使用中")
    private Integer sourceState;

    /** 资源路径 */
    @Excel(name = "资源路径")
    private String sourceUrl;

    public void setSourceId(Long sourceId) 
    {
        this.sourceId = sourceId;
    }

    public Long getSourceId() 
    {
        return sourceId;
    }
    public void setProductId(Integer productId) 
    {
        this.productId = productId;
    }

    public Integer getProductId() 
    {
        return productId;
    }
    public void setSourceState(Integer sourceState) 
    {
        this.sourceState = sourceState;
    }

    public Integer getSourceState() 
    {
        return sourceState;
    }
    public void setSourceUrl(String sourceUrl) 
    {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceUrl() 
    {
        return sourceUrl;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("sourceId", getSourceId())
            .append("productId", getProductId())
            .append("sourceState", getSourceState())
            .append("sourceUrl", getSourceUrl())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
