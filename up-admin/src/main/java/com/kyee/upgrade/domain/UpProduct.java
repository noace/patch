package com.kyee.upgrade.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 产品对象 up_product
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
public class UpProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 产品ID */
    private Integer productId;

    /** 产品名 */
    @Excel(name = "产品名")
    private String productName;

    /** 产品项目名 */
    @Excel(name = "产品项目名")
    private String productShortName;

    /** 产品编码 */
    @Excel(name = "产品编码")
    private String productCode;

    /** 产品仓库 */
    @Excel(name = "产品仓库")
    private String productRepository;

    /** 产品备注 */
    @Excel(name = "产品备注")
    private String memo;

    public void setProductId(Integer productId)
    {
        this.productId = productId;
    }

    public Integer getProductId()
    {
        return productId;
    }
    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getProductName()
    {
        return productName;
    }

    public String getProductShortName() {
        return productShortName;
    }

    public void setProductShortName(String productShortName) {
        this.productShortName = productShortName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setProductRepository(String productRepository)
    {
        this.productRepository = productRepository;
    }

    public String getProductRepository()
    {
        return productRepository;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("productId", getProductId())
                .append("productShortName", getProductShortName())
                .append("productName", getProductName())
                .append("productCode", getProductCode())
                .append("createBy", getCreateBy())
                .append("productRepository", getProductRepository())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .append("remark", getRemark())
                .toString();
    }
}
