package com.kyee.upgrade.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 项目应用服务对象 up_project_server
 * 
 * @author lijunqiang
 * @date 2022-04-12
 */
public class UpProjectServer extends BaseEntity
{
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

    /** 项目名 */
    @Excel(name = "项目名")
    private String projectName;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Integer productId;

    /** 产品名 */
    @Excel(name = "产品名")
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
    
    /** tomcat路径 */
    @Excel(name = "tomcat路径")
    private String tomcatPath;

    /** 项目部署名称 */
    @Excel(name = "项目部署名称")
    private String deployName;

    /** OS类型 */
    @Excel(name = "OS类型")
    private String osType;

    /** ssh端口 */
    @Excel(name = "ssh端口")
    private Integer sshPort;

    /** ssh用户名/数据库用户名 */
    @Excel(name = "ssh用户名/数据库用户名")
    private String sshUser;

    /** ssh密码/数据库密码 */
    @Excel(name = "ssh密码/数据库密码")
    private String sshPassword;

    /** 服务器类型 */
    @Excel(name = "服务器类型")
    private String serverType;

    /** 服务类型 */
    @Excel(name = "服务类型")
    private String serviceType;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 最后升级包 */
    @Excel(name = "最后升级包")
    private String lastUpgradePackage;

    /** 最后升级人员 */
    @Excel(name = "最后升级人员")
    private String lastUpgradePerson;

    /** 最后升级时间 */
    @Excel(name = "最后升级时间")
    private Date lastUpgradeTime;

    /** 升级次数 */
    @Excel(name = "升级次数")
    private Integer upTimes;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public String getTomcatPath() {
        return tomcatPath;
    }

    public void setTomcatPath(String tomcatPath) {
        this.tomcatPath = tomcatPath;
    }

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getSshUser() {
        return sshUser;
    }

    public void setSshUser(String sshUser) {
        this.sshUser = sshUser;
    }

    public String getSshPassword() {
        return sshPassword;
    }

    public void setSshPassword(String sshPassword) {
        this.sshPassword = sshPassword;
    }

    public String getDeployName() {
        return deployName;
    }

    public void setDeployName(String deployName) {
        this.deployName = deployName;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getUpTimes() {
        return upTimes;
    }

    public void setUpTimes(Integer upTimes) {
        this.upTimes = upTimes;
    }

    @Override
    public String toString() {
        return "UpProjectServer{" +
                "serverId=" + serverId +
                ", serverName='" + serverName + '\'' +
                ", projectId=" + projectId +
                ", productId=" + productId +
                ", serverIp='" + serverIp + '\'' +
                ", serverPort=" + serverPort +
                ", serverPath='" + serverPath + '\'' +
                ", serverStatus='" + serverStatus + '\'' +
                ", upgradeStatus='" + upgradeStatus + '\'' +
                ", tomcatPath='" + tomcatPath + '\'' +
                ", deployName='" + deployName + '\'' +
                ", osType='" + osType + '\'' +
                ", sshPort=" + sshPort +
                ", sshUser='" + sshUser + '\'' +
                ", sshPassword='" + sshPassword + '\'' +
                ", serverType='" + serverType + '\'' +
                ", serviceType='" + serviceType + '\'' +
                '}';
    }
}
