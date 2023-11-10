package com.kyee.upgrade.common.domain;

import org.eclipse.jgit.diff.DiffEntry;

import java.util.Date;
import java.util.List;

public class GitCommitRecord {
    String commitId;
    Date commitDate;
    String commitPerson;
    String message;
    List<DiffEntry> diffEntityList;

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public String getCommitPerson() {
        return commitPerson;
    }

    public void setCommitPerson(String commitPerson) {
        this.commitPerson = commitPerson;
    }

    public List<DiffEntry> getDiffEntityList() {
        return diffEntityList;
    }

    public void setDiffEntityList(List<DiffEntry> diffEntityList) {
        this.diffEntityList = diffEntityList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
