package com.kyee.upgrade.domain.vo;

import com.ruoyi.common.core.domain.BaseEntity;

public class UpPatchCodeListPo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 补丁ID */
    private Long patchId;

    /** 补丁文件名 */
    private String patchFileName;

    /** 代码路径 */
    private String codePath;

    /** 文件类型,java文件：JAVA,SQL文件：SQL,其他文件：OTHER */
    private String fileType;

    public Long getPatchId() {
        return patchId;
    }

    public void setPatchId(Long patchId) {
        this.patchId = patchId;
    }

    public String getPatchFileName() {
        return patchFileName;
    }

    public void setPatchFileName(String patchFileName) {
        this.patchFileName = patchFileName;
    }

    public String getCodePath() {
        return codePath;
    }

    public void setCodePath(String codePath) {
        this.codePath = codePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
