package com.kyee.upgrade.utils;

import com.kyee.upgrade.common.domain.GitCheckResult;
import com.kyee.upgrade.common.domain.GitCommitRecord;
import com.kyee.upgrade.domain.UpBuildCodeChanges;
import com.kyee.upgrade.domain.UpBuildCodePath;
import com.kyee.upgrade.domain.UpBuildLog;
import com.kyee.upgrade.domain.UpUpgradeLogClient;
import com.kyee.upgrade.mapper.UpBuildCodeChangesMapper;
import com.kyee.upgrade.mapper.UpBuildCodePathMapper;
import com.kyee.upgrade.mapper.UpBuildLogMapper;
import com.kyee.upgrade.mapper.UpUpgradeLogClientMapper;
import org.eclipse.jgit.diff.DiffEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class LogUtil {

    @Autowired
    private UpBuildLogMapper upBuildLogMapper;

    @Autowired
    private UpBuildCodeChangesMapper upBuildCodeChangesMapper;

    @Autowired
    private UpBuildCodePathMapper upBuildCodePathMapper;
    
    @Autowired
    private UpUpgradeLogClientMapper upUpgradeLogClientMapper;

    private static LogUtil logUtil;

    // 提供日志Util，代码检出日志，构建日志存储到数据库中
    // 代码检出日志
    public static void recordCodeCheckoutLog(GitCheckResult gitCheckResult, Long patchId) throws Exception {
        try {
            List<GitCommitRecord> gitCommitRecordList = gitCheckResult.getGitCommitRecordList();
            // 代码变更记录
            List<UpBuildCodeChanges> codeChangesList = new LinkedList<>();
            // 代码变更路径
            List<UpBuildCodePath> codePathChanges = new LinkedList<>();
            for (GitCommitRecord commit : gitCommitRecordList) {
                UpBuildCodeChanges change = new UpBuildCodeChanges();
                change.setPatchId(patchId);
                change.setCommitId(commit.getCommitId());
                change.setCommitTime(commit.getCommitDate());
                change.setCommitPerson(commit.getCommitPerson());
                change.setCommitMessage(commit.getMessage());
                List<DiffEntry> entityList = commit.getDiffEntityList();
                codeChangesList.add(change);
                // 代码变更路径(保存没有被删除的代码路径)
                List<String> codePathList = entityList.stream()
                        .filter(s -> !DiffEntry.ChangeType.DELETE.equals(s.getChangeType()))
                        .map(DiffEntry::getNewPath)
                        .collect(Collectors.toList());
                List<DiffEntry> noDeleteDiff = entityList.stream().filter(s -> !DiffEntry.ChangeType.DELETE.equals(s.getChangeType())).collect(Collectors.toList());
                // 设置未删除的代码路径
                Map<String, DiffEntry> changeTypeMap = noDeleteDiff.stream().collect(Collectors.toMap(DiffEntry::getNewPath, s -> s));
                for (String newPath : codePathList) {
                    if (changeTypeMap.containsKey(newPath)) {
                        // 代码变更路径
                        UpBuildCodePath codePath = new UpBuildCodePath();
                        codePath.setCommitId(commit.getCommitId());
                        codePath.setCodeChangeType(changeTypeMap.get(newPath).getChangeType().name());
                        codePath.setCodePath(newPath);
                        codePathChanges.add(codePath);
                    }
                }
            }
            logUtil.upBuildCodeChangesMapper.batchInsertUpBuildCodeChanges(codeChangesList);
            logUtil.upBuildCodePathMapper.batchInsertUpBuildCodePath(codePathChanges);
        } catch (Exception e) {
            throw new Exception("保存GIT日志异常");
        }
    }

    // 记录构建日志
    public static void recordBuildLog(String log, Long patchId, String processId) {
        UpBuildLog buildLog = new UpBuildLog();
        buildLog.setBuildLog(log);
        buildLog.setBuildTime(new Date());
        buildLog.setPatchId(patchId);
        buildLog.setProcessId(processId);
        logUtil.upBuildLogMapper.insertUpBuildLog(buildLog);
    }

    // 记录升级日志
    public static void recordUpgradeLog(String log, Long patchId, Integer serverId) {
        UpUpgradeLogClient upgradeLog = new UpUpgradeLogClient();
        upgradeLog.setLogContent(log);
        upgradeLog.setUpTime(new Date());
        upgradeLog.setPatchId(patchId);
        upgradeLog.setServerId(serverId);
        logUtil.upUpgradeLogClientMapper.insertUpUpgradeLogClient(upgradeLog);
    }

    @PostConstruct
    public void init(){
        logUtil = this;
        logUtil.upBuildLogMapper = this.upBuildLogMapper;
        logUtil.upBuildCodeChangesMapper = this.upBuildCodeChangesMapper;
        logUtil.upBuildCodePathMapper = this.upBuildCodePathMapper;
    }
}
