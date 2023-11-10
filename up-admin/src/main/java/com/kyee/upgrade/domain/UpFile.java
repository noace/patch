package com.kyee.upgrade.domain;

import com.ruoyi.common.annotation.Excel;

/**
 * 升级文件对象 UpUpgradeRecordFile
 * 
 * @author zhanghoayu
 * @date 2021-10-20
 */
public class UpFile
{
    private static final long serialVersionUID = 1L;

    /** 补丁文件名 */
    @Excel(name = "文件名")
    private String fileName;

    /** 补丁文件URL */
    @Excel(name = "文件URL")
    private String fileServerUrl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getfileServerUrl() {
        return fileServerUrl;
    }

    public void setfileServerUrl(String fileServerUrl) {
        this.fileServerUrl = fileServerUrl;
    }

    @Override
    public String toString() {
        return "UpFile{" +
                "fileName='" + fileName + '\'' +
                ", fileServerUrl='" + fileServerUrl + '\'' +
                '}';
    }
}
