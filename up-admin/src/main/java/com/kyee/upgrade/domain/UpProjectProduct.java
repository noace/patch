package com.kyee.upgrade.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * 项目产品对象 up_project_product
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
public class UpProjectProduct extends BaseEntity
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

    /** 产品负责人Id */
    @Excel(name = "产品负责人Id")
    private Long productPrincipalId;

    /** 升级负责人 */
    @Excel(name = "升级负责人")
    private String productPrincipal;

    /** 项目代码分支 */
    @Excel(name = "项目代码分支")
    private String sourceBranchName;

    /** 分支类型 */
    @Excel(name = "分支类型")
    private String branchType;

    /** 数据库版本 */
    @Excel(name = "数据库版本")
    private String dbVersion;

    /** 最小服务版本 */
    @Excel(name = "最小服务版本")
    private String minServerVersion;

    /** 最大服务版本 */
    @Excel(name = "最大服务版本")
    private String maxServerVersion;

    /** 最后升级时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "最后升级时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastUpgradeTime;

    /** 产品负责人集合 */
    private Long[] productPrincipalIds;

    /** 是否测试 */
    @Excel(name = "是否测试")
    private String testFlag;

    /** 是否自动升级 */
    @Excel(name = "是否自动升级")
    private String autoUpgradeFlag;

    /** 是否执行标准库 */
    @Excel(name = "是否执行标准库, 1：全部执行，2：部分执行，0：不执行")
    private String excuteFlag;

    /** 是否执行开发库 */
    @Excel(name = "是否执行开发库, 1：全部执行，2：部分执行，0：不执行")
    private String excuteDevFlag;

    /** 编译方式 */
    @Excel(name = "编译方式：全量编译1，部分编译2")
    private String buildType;

    /** 打包分支 */
    @Excel(name = "打包分支：CommitID，主分支")
    private String patchBranch;

    /** 升级版本号 */
    @Excel(name = "升级版本号")
    private String upgradeVersion;

    /** 项目经理*/
    @Excel(name = "项目经理")
    private String projectPrincipal;

    /** 项目产品负责人id*/
    @Excel(name = "项目产品负责人Id")
    private String projectPrincipalId;

    /** 测试负责人*/
    @Excel(name = "测试负责人")
    private String testPrincipal;

    /** 子项目Id*/
    @Excel(name = "子项目Id")
    private String subProjectId;

    /** 子项目名称*/
    @Excel(name = "子项目名称")
    private String SubProjectName;

    /** 是否选中 */
    private boolean flag;

    /** 打包序号 */
    private Integer patchRank;

    public String getProjectPrincipalId() {
        return projectPrincipalId;
    }

    public void setProjectPrincipalId(String projectPrincipalId) {
        this.projectPrincipalId = projectPrincipalId;
    }

    public String getProjectPrincipal() {
        return projectPrincipal;
    }

    public void setProjectPrincipal(String projectPrincipal) {
        this.projectPrincipal = projectPrincipal;
    }

    public void setProjectProductId(Integer projectProductId)
    {
        this.projectProductId = projectProductId;
    }

    public Integer getProjectProductId()
    {
        return projectProductId;
    }
    public void setProjectId(Integer projectId)
    {
        this.projectId = projectId;
    }

    public Integer getProjectId()
    {
        return projectId;
    }
    public void setProductId(Integer productId)
    {
        this.productId = productId;
    }

    public Integer getProductId()
    {
        return productId;
    }

    public Long getProductPrincipalId() {
        return productPrincipalId;
    }

    public void setProductPrincipalId(Long productPrincipalId) {
        this.productPrincipalId = productPrincipalId;
    }

    public String getProductPrincipal() {
        return productPrincipal;
    }

    public void setProductPrincipal(String productPrincipal) {
        this.productPrincipal = productPrincipal;
    }

    public void setSourceBranchName(String sourceBranchName)
    {
        this.sourceBranchName = sourceBranchName;
    }

    public String getSourceBranchName()
    {
        return sourceBranchName;
    }
    public void setDbVersion(String dbVersion)
    {
        this.dbVersion = dbVersion;
    }

    public String getDbVersion()
    {
        return dbVersion;
    }
    public void setMinServerVersion(String minServerVersion)
    {
        this.minServerVersion = minServerVersion;
    }

    public String getMinServerVersion()
    {
        return minServerVersion;
    }
    public void setMaxServerVersion(String maxServerVersion)
    {
        this.maxServerVersion = maxServerVersion;
    }

    public String getMaxServerVersion()
    {
        return maxServerVersion;
    }
    public void setLastUpgradeTime(Date lastUpgradeTime)
    {
        this.lastUpgradeTime = lastUpgradeTime;
    }

    public Date getLastUpgradeTime()
    {
        return lastUpgradeTime;
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

    public Long[] getProductPrincipalIds() {
        return productPrincipalIds;
    }

    public void setProductPrincipalIds(Long[] productPrincipalIds) {
        this.productPrincipalIds = productPrincipalIds;
    }

    public String getBranchType() {
        return branchType;
    }

    public void setBranchType(String branchType) {
        this.branchType = branchType;
    }

    public String getTestFlag() {
        return testFlag;
    }

    public void setTestFlag(String testFlag) {
        this.testFlag = testFlag;
    }

    public String getAutoUpgradeFlag() {
        return autoUpgradeFlag;
    }

    public void setAutoUpgradeFlag(String autoUpgradeFlag) {
        this.autoUpgradeFlag = autoUpgradeFlag;
    }

    public String getExcuteFlag() {
        return excuteFlag;
    }

    public void setExcuteFlag(String excuteFlag) {
        this.excuteFlag = excuteFlag;
    }

    public String getExcuteDevFlag() {
        return excuteDevFlag;
    }

    public void setExcuteDevFlag(String excuteDevFlag) {
        this.excuteDevFlag = excuteDevFlag;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getPatchBranch() {
        return patchBranch;
    }

    public void setPatchBranch(String patchBranch) {
        this.patchBranch = patchBranch;
    }

    public String getTestPrincipal() {
        return testPrincipal;
    }

    public void setTestPrincipal(String testPrincipal) {
        this.testPrincipal = testPrincipal;
    }

    public String getUpgradeVersion() {
        return upgradeVersion;
    }

    public void setUpgradeVersion(String upgradeVersion) {
        this.upgradeVersion = upgradeVersion;
    }

    public String getSubProjectId() {
        return subProjectId;
    }

    public void setSubProjectId(String subProjectId) {
        this.subProjectId = subProjectId;
    }

    public String getSubProjectName() {
        return SubProjectName;
    }

    public void setSubProjectName(String subProjectName) {
        SubProjectName = subProjectName;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Integer getPatchRank() {
        return patchRank;
    }

    public void setPatchRank(Integer patchRank) {
        this.patchRank = patchRank;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("projectProductId", getProjectProductId())
            .append("projectId", getProjectId())
            .append("projectName", getProjectName())
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("sourceBranchName", getSourceBranchName())
            .append("dbVersion", getDbVersion())
            .append("minServerVersion", getMinServerVersion())
            .append("maxServerVersion", getMaxServerVersion())
            .append("lastUpgradeTime", getLastUpgradeTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
