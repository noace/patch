package com.kyee.upgrade.domain.vo;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;
import java.util.List;

public class UpUpgradeDataDTO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 服务ID */
    @Excel(name = "服务ID")
    private Integer serverId;

    /** 服务名称 */
    @Excel(name = "服务名称")
    private String serverName;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Integer projectId;

    /** 项目 */
    @Excel(name = "项目ID")
    private String projectName;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Integer productId;

    /** 产品 */
    @Excel(name = "项目ID")
    private String productName;

    /** 服务IP */
    @Excel(name = "服务IP")
    private String serverIp;

    /** 服务端口号/数据库端口 */
    @Excel(name = "服务端口号/数据库端口")
    private Integer serverPort;

    /** 服务文件夹路径/数据库连接方式 */
    @Excel(name = "服务文件夹路径/数据库连接方式")
    private String serverPath;

    /** 服务器状态 */
    @Excel(name = "服务器状态")
    private String serverStatus;

    /** 升级状态 */
    @Excel(name = "升级状态")
    private String upgradeStatus;

    /** 服务器类型 */
    @Excel(name = "服务器类型")
    private String serverType;

    /** 服务类型 */
    @Excel(name = "服务类型")
    private String serviceType;

    /** 最后执行补丁包 */
    @Excel(name = "最后执行补丁包")
    private String lastUpgradePackage;

    /** 最后执行人员 */
    @Excel(name = "最后执行人员")
    private String lastUpgradePerson;

    /** 最后执行时间 */
    @Excel(name = "最后执行时间")
    private Date lastUpgradeTime;

    /** 测试状态 */
    @Excel(name = "测试状态")
    private String testStatus;

    /** 补丁包状态 */
    @Excel(name = "补丁包状态")
    private String patchStatus;

    /** 补丁包状态集合 */
    private List<String> patchStatusList;

    /** 测试状态集合 */
    private List<String> testStatusList;

    /** 升级状态集合 */
    private List<String> upgradeStatusList;

    /** 是否含SQL */
    private String sqlFlag;

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    public String getUpgradeStatus() {
        return upgradeStatus;
    }

    public void setUpgradeStatus(String upgradeStatus) {
        this.upgradeStatus = upgradeStatus;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getLastUpgradePackage() {
        return lastUpgradePackage;
    }

    public void setLastUpgradePackage(String lastUpgradePackage) {
        this.lastUpgradePackage = lastUpgradePackage;
    }

    public String getLastUpgradePerson() {
        return lastUpgradePerson;
    }

    public void setLastUpgradePerson(String lastUpgradePerson) {
        this.lastUpgradePerson = lastUpgradePerson;
    }

    public Date getLastUpgradeTime() {
        return lastUpgradeTime;
    }

    public void setLastUpgradeTime(Date lastUpgradeTime) {
        this.lastUpgradeTime = lastUpgradeTime;
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

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }

    public String getPatchStatus() {
        return patchStatus;
    }

    public void setPatchStatus(String patchStatus) {
        this.patchStatus = patchStatus;
    }

    public List<String> getPatchStatusList() {
        return patchStatusList;
    }

    public void setPatchStatusList(List<String> patchStatusList) {
        this.patchStatusList = patchStatusList;
    }

    public List<String> getTestStatusList() {
        return testStatusList;
    }

    public void setTestStatusList(List<String> testStatusList) {
        this.testStatusList = testStatusList;
    }

    public List<String> getUpgradeStatusList() {
        return upgradeStatusList;
    }

    public void setUpgradeStatusList(List<String> upgradeStatusList) {
        this.upgradeStatusList = upgradeStatusList;
    }

    public String getSqlFlag() {
        return sqlFlag;
    }

    public void setSqlFlag(String sqlFlag) {
        this.sqlFlag = sqlFlag;
    }
}
