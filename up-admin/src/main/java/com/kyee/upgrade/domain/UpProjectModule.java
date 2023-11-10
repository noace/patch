package com.kyee.upgrade.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目配置文件对象 up_project_config_file
 * 
 * @author lijunqiang
 * @date 2022-06-25
 */
public class UpProjectModule extends BaseEntity
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

    /** 模块名 */
    @Excel(name = "模块名")
    private String moduleName;

    /** Jar版本号 */
    @Excel(name = "Jar版本号")
    private String jarVersion;

    /** 打包类型 */
    @Excel(name = "打包类型")
    private String packageType;

    /** 序号 */
    @Excel(name = "序号")
    private Integer rank;

    /** 是否基础模块 */
    @Excel(name = "是否基础模块，Y是N否")
    private String baseModule;

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

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getJarVersion() {
        return jarVersion;
    }

    public void setJarVersion(String jarVersion) {
        this.jarVersion = jarVersion;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getBaseModule() {
        return baseModule;
    }

    public void setBaseModule(String baseModule) {
        this.baseModule = baseModule;
    }

    @Override
    public String toString() {
        return "UpProjectModule{" +
                "projectProductId=" + projectProductId +
                ", projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", moduleName='" + moduleName + '\'' +
                '}';
    }
}
