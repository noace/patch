package com.kyee.upgrade.common.clone;

import com.kyee.upgrade.common.domain.GitCheckResult;
import com.kyee.upgrade.utils.GitUtil;
import org.eclipse.jgit.transport.CredentialsProvider;

import static com.kyee.upgrade.utils.ConstantUtil.GIT_PASSWORD;
import static com.kyee.upgrade.utils.ConstantUtil.GIT_USERNAME;

/**
 * 描述：克隆代码通用类
 * 作者：DangHangHang
 * 时间：2023年06月12日 14:31
 */
public class CloneCode {

    /**
     * 描述：克隆代码
     * 作者：DangHangHang
     * 时间：2023年06月12日 14:31
     */
    public static GitCheckResult executeCheckCode(String sourceUrl, String branch, String processId, Long patchId, String repository) throws Throwable{
        // git登录信息
        CredentialsProvider credentialsProvider = GitUtil.getCredentialsProvider(GIT_USERNAME, GIT_PASSWORD);
        return GitUtil.checkoutAndPull(sourceUrl, credentialsProvider, branch, repository, processId, patchId);
    }

    /**
     * commitId检出代码
     * @param sourceUrl
     * @param branch 主分支
     * @param processId
     * @param patchId
     * @param repository
     * @param branchId 分支commitId
     */
    public static void checkoutCommitId(String sourceUrl, String branch, String processId, Long patchId, String repository, String branchId) throws Throwable {

        CredentialsProvider credentialsProvider = GitUtil.getCredentialsProvider(GIT_USERNAME, GIT_PASSWORD);
        GitUtil.checkoutAndFetch(sourceUrl, credentialsProvider, branch, repository, processId, patchId, branchId);
    }
}
