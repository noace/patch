package com.ruoyi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 全局配置类
 * 
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "ruoyi")
public class RuoYiConfig
{
    /** 项目名称 */
    private static String name;

    /** 版本 */
    private static String version;

    /** 版权年份 */
    private static String copyrightYear;

    /** 实例演示开关 */
    private static boolean demoEnabled;

    /** 上传路径 */
    private static String profile;

    /** 云服务器及端口 */
    private static String yunServerUrl;

    /** 获取地址开关 */
    private static boolean addressEnabled;

    /** 升级工具端开关 */
    private static boolean upgradeEnabled;

    /** 云服务器workspace地址 */
    private static String workspacePath;

    /** 升级端备份路径 */
    private static String backupPath;

    public static String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        RuoYiConfig.name = name;
    }

    public static String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        RuoYiConfig.version = version;
    }

    public static String getCopyrightYear()
    {
        return copyrightYear;
    }

    public void setCopyrightYear(String copyrightYear)
    {
        RuoYiConfig.copyrightYear = copyrightYear;
    }

    public static boolean isUpgradeEnabled()
    {
        return upgradeEnabled;
    }

    public void setUpgradeEnabled(boolean upgradeEnabled)
    {
        RuoYiConfig.upgradeEnabled = upgradeEnabled;
    }

    public static boolean isDemoEnabled()
    {
        return demoEnabled;
    }

    public void setDemoEnabled(boolean demoEnabled)
    {
        RuoYiConfig.demoEnabled = demoEnabled;
    }

    public static String getProfile()
    {
        return profile;
    }

    public static String getYunServerUrl()
    {
        return yunServerUrl;
    }
    public void setYunServerUrl(String yunServerUrl)
    {
        RuoYiConfig.yunServerUrl = yunServerUrl;
    }

    public void setProfile(String profile)
    {
        RuoYiConfig.profile = profile;
    }

    public static boolean isAddressEnabled()
    {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled)
    {
        RuoYiConfig.addressEnabled = addressEnabled;
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }

    public static String getWorkspacePath() {
        return workspacePath;
    }

    public void setWorkspacePath(String workspacePath) {
        RuoYiConfig.workspacePath = workspacePath;
    }

    /** 升级端备份路径 */
    public static String getBackupPath() {
        return backupPath;
    }

    public void setBackupPath(String backupPath) {
        RuoYiConfig.backupPath = backupPath;
    }
}
