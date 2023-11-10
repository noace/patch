package com.kyee.upgrade.domain.vo;

public class RegitstPatchInfo {
    private Integer projectId;
    private Integer productId;
    private String jiraNo;
    private String topic;
    private String bugfixFlag;
    private String remark;
    private String codeList;
    private String mergePackageFlag;
    private String updateBy;

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
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

    public String getJiraNo() {
        return jiraNo;
    }

    public void setJiraNo(String jiraNo) {
        this.jiraNo = jiraNo;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getBugfixFlag() {
        return bugfixFlag;
    }

    public void setBugfixFlag(String bugfixFlag) {
        this.bugfixFlag = bugfixFlag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCodeList() {
        return codeList;
    }

    public void setCodeList(String codeList) {
        this.codeList = codeList;
    }

    public String getMergePackageFlag() {
        return mergePackageFlag;
    }

    public void setMergePackageFlag(String mergePackageFlag) {
        this.mergePackageFlag = mergePackageFlag;
    }
}
