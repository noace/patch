package com.kyee.upgrade.domain.vo;

import com.kyee.upgrade.domain.UpPatchClient;

/**
 * 端补丁包Vo
 */
public class UpPatchClientExtend extends UpPatchClient {

    /** 当前记录起始索引 */
    private Integer pageNum;

    /** 升级状态 */
    private String upStatus;

    /** 服务类型 */
    private String serverType;

    /** 是否列表最后一个标识 */
    private boolean lastPatchFlag;
    
    public Integer getPageNum() {
        return pageNum;
    }
    
    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getUpStatus() {
        return upStatus;
    }

    public void setUpStatus(String upStatus) {
        this.upStatus = upStatus;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public boolean isLastPatchFlag() {
        return lastPatchFlag;
    }

    public void setLastPatchFlag(boolean lastPatchFlag) {
        this.lastPatchFlag = lastPatchFlag;
    }
}
