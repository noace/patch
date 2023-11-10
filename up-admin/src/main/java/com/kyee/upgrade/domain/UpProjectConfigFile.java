package com.kyee.upgrade.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目配置文件对象 up_project_config_file
 * 
 * @author lijunqiang
 * @date 2022-06-25
 */
public class UpProjectConfigFile extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 项目产品ID */
    private Integer projectProductId;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Integer projectId;

    /** 项目名 */
    @Excel(name = "项目名")
    private String projectName;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Integer productId;

    /** 产品名 */
    @Excel(name = "产品名")
    private String productName;

    /** 不打包的配置文件名 */
    @Excel(name = "不打包的配置文件名")
    private String configFileName;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getProjectProductId() {
        return projectProductId;
    }

    public void setProjectProductId(Integer projectProductId) {
        this.projectProductId = projectProductId;
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

    public String getConfigFileName() {
        return configFileName;
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    @Override
    public String toString() {
        return "UpProjectConfigFile{" +
                "projectProductId=" + projectProductId +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", configFileName='" + configFileName + '\'' +
                '}';
    }
}
