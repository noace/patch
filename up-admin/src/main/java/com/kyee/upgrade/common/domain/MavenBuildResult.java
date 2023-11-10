package com.kyee.upgrade.common.domain;

import java.util.List;

public class MavenBuildResult {
    private boolean resultSuccess;
    private List<String> logArray;

    public boolean isResultSuccess() {
        return resultSuccess;
    }

    public void setResultSuccess(boolean resultSuccess) {
        this.resultSuccess = resultSuccess;
    }

    public List<String> getLogArray() {
        return logArray;
    }

    public void setLogArray(List<String> logArray) {
        this.logArray = logArray;
    }
}
