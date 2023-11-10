package com.kyee.upgrade.common.domain;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.ArrayList;
import java.util.List;

public class GitCheckResult {
    private boolean resultSuccess;
    private List<GitCommitRecord> gitCommitRecordList = new ArrayList<GitCommitRecord>();
    private List<DiffEntry> diffEntryList;

    public boolean isResultSuccess() {
        return resultSuccess;
    }

    public void setResultSuccess(boolean resultSuccess) {
        this.resultSuccess = resultSuccess;
    }

    public List<GitCommitRecord> getGitCommitRecordList() {
        return gitCommitRecordList;
    }

    public void setGitCommitRecordList(List<GitCommitRecord> gitCommitRecordList) {
        this.gitCommitRecordList = gitCommitRecordList;
    }

    public List<DiffEntry> getDiffEntryList() {
        return diffEntryList;
    }

    public void setDiffEntryList(List<DiffEntry> diffEntryList) {
        this.diffEntryList = diffEntryList;
    }
}
