package com.kyee.upgrade.utils;

import com.kyee.upgrade.common.domain.GitCheckResult;
import com.kyee.upgrade.common.domain.GitCommitRecord;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.HttpConfig;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class GitUtil {

    public static UsernamePasswordCredentialsProvider getCredentialsProvider(String gituser, String gitpasswd) {
        UsernamePasswordCredentialsProvider credentialsProvider = null;
        if (StringUtils.isNotEmpty(gituser) && StringUtils.isNotEmpty(gitpasswd)) {
            credentialsProvider = new UsernamePasswordCredentialsProvider(gituser, gitpasswd);
        }
        return credentialsProvider;
    }
    public static void doInit(File gitDir) throws GitAPIException {
        if (!gitDir.exists()) {
            gitDir.mkdirs();
        }
        Git git = Git.init().setDirectory(gitDir).setBare(false).call();
    }
    public static void doClone(File gitDir, String gituri, CredentialsProvider credentialsProvider, String branch)
            throws GitAPIException {
        Git.cloneRepository().setURI(gituri)
                .setCredentialsProvider(credentialsProvider)
                .setDirectory(gitDir).setCloneAllBranches(false)
                .setBranch(branch).call().close();
    }
    public static PullResult doPull(File gitDir, CredentialsProvider credentialsProvider) throws IOException,
            GitAPIException {
        Git git = Git.open(gitDir);
        return git.pull().setCredentialsProvider(credentialsProvider).call();
    }
    public static void doAdd(File gitDir, String... files)
            throws GitAPIException, IOException {
        try (Git git = Git.open(gitDir)) {
            AddCommand add = git.add();
            if (ArrayUtils.isEmpty(files)) {
                add.addFilepattern(".");
            } else {
                for (String file : files) {
                    add.addFilepattern(file);
                }
            }
            add.call();
        }
    }
    public static void doCommit(File gitDir, String message) throws GitAPIException, IOException {
        Git git = Git.open(gitDir);
        CommitCommand commit = git.commit().setAll(false).setMessage(message);
        commit.call();
    }
    public static Iterable<PushResult> doPush(File gitDir, CredentialsProvider credentialsProvider) throws IOException, GitAPIException {
        Git git = Git.open(gitDir);
        PushCommand push = git.push().setCredentialsProvider(credentialsProvider);
        return push.call();
    }

    /**
     *
     * <p>
     * Description:判断本地分支名是否存在
     * </p>
     *
     * @param gitDir
     * @param branchName
     * @return
     * @throws GitAPIException
     * @author wgs
     * @date 2019年7月20日 下午2:49:46
     *
     */
    public static boolean branchNameExist(File gitDir, String branchName) throws IOException,GitAPIException {
        Git git = Git.open(gitDir);
        List<Ref> refs = git.branchList().call();
        for (Ref ref : refs) {
            if (ref.getName().contains(branchName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前分支是否存在
     * @param git
     * @param branchName
     * @return
     * @throws IOException
     * @throws GitAPIException
     */
    private static boolean branchNameExist(Git git, String branchName) throws IOException,GitAPIException {
        List<Ref> refs = git.branchList().call();
        for (Ref ref : refs) {
            if (ref.getName().contains(branchName)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * <p>Description:切换分支，并拉取到最新 </p>
     * @param repoDir
     * @param branchName
     * @author wgs
     * @date  2019年7月20日 下午4:11:45
     *
     */
    public static GitCheckResult checkoutAndPull(String repoDir, CredentialsProvider credentialsProvider, String branchName, String repository, String processId, Long patchId) throws Throwable{
        GitCheckResult gitCheckResult = new GitCheckResult();
        try {
            //Repository existingRepo = new FileRepositoryBuilder().setGitDir(new File(repoDir)).build();
            //Git git = new Git(existingRepo);
            //Git git = Git.open(new File(repoDir));
            // 项目不存在，克隆项目
            Git git = getGit(repository, credentialsProvider, repoDir);
            try {
                if (branchNameExist(git, branchName)) {//如果分支在本地已存在，直接checkout即可。
                    git.checkout().setCreateBranch(false).setName(branchName).call();
                    LogUtil.recordBuildLog("--------------切换Git分支为: 【" + branchName + "】--------------", patchId, processId);
                } else {//如果分支在本地不存在，需要创建这个分支，并追踪到远程分支上面。
                    git.checkout().setCreateBranch(true).setName(branchName).setStartPoint("origin/" + branchName).call();
                }

                ObjectId oldHead = git.getRepository().resolve("HEAD^{tree}");


                PullCommand pullCmd = git.pull();//拉取最新的提交
                PullResult pullResult = pullCmd.setCredentialsProvider(credentialsProvider).call();
                MergeResult mergeResult = pullResult.getMergeResult();
                ObjectId[] mergedCommits = mergeResult.getMergedCommits();
                for(ObjectId objectId:mergedCommits){
                    //String commitId = objectId.getName();
                    GitCommitRecord tempGitCommitRecord = getCommitLogList(git,objectId);
                    gitCheckResult.getGitCommitRecordList().add(tempGitCommitRecord);
                }

                ObjectId head = git.getRepository().resolve("HEAD^{tree}");

                ObjectReader reader = git.getRepository().newObjectReader();
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                oldTreeIter.reset(reader, oldHead);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, head);
                List<DiffEntry> diffs= git.diff()
                        .setNewTree(newTreeIter)
                        .setOldTree(oldTreeIter)
                        .call();
                gitCheckResult.setDiffEntryList(diffs);
                gitCheckResult.setResultSuccess(true);
            } finally {
                git.close();
            }
        } catch (Throwable e) {
            gitCheckResult.setResultSuccess(false);
            e.printStackTrace();
            throw new Exception("--------------检出代码失败【repository:" + repository + "】【branch:" + branchName + "】【错误详情：" + e.toString() + "】--------------");
        }
        return gitCheckResult;
    }

    private static GitCommitRecord getCommitLogList(Git git, ObjectId objId) throws Exception {
        GitCommitRecord resultCommitRecord = new GitCommitRecord();
        resultCommitRecord.setCommitId(objId.getName());

        HashMap<String, Object> map = new HashMap<String, Object>();
        // 通过git获取项目库
        Repository repository = git.getRepository();
        // 根据所需要查询的版本号查新ObjectId
        //ObjectId objId = repository.resolve(revision);

        //根据版本号获取指定的详细记录
        Iterable<RevCommit> allCommitsLater = git.log().add(objId).call();
        if (allCommitsLater == null) {
            throw new Exception("该提交版本下无当前查询用户的提交记录");
        }
        Iterator<RevCommit> iter = allCommitsLater.iterator();
        RevCommit commit = iter.next();//提交对象
        Date commitDate = commit.getAuthorIdent().getWhen();//提交时间
        resultCommitRecord.setCommitDate(commitDate);
        String commitPerson = commit.getAuthorIdent().getName();//提交人
        resultCommitRecord.setCommitPerson(commitPerson);
        String message = commit.getFullMessage();//提交日志
        resultCommitRecord.setMessage(message);

        // 给存储库创建一个树的遍历器
        TreeWalk tw = new TreeWalk(repository);
        // 将当前commit的树放入树的遍历器中
        tw.addTree(commit.getTree());

        commit = iter.next();
        if (commit != null) {
            tw.addTree(commit.getTree());
        } else {
            return null;
        }

        tw.setRecursive(true);
        RenameDetector rd = new RenameDetector(repository);
        rd.addAll(DiffEntry.scan(tw));
        //获取到详细记录结果集    在这里就能获取到一个版本号对应的所有提交记录（详情！！！）
        List<DiffEntry> list = rd.compute();
        resultCommitRecord.setDiffEntityList(list);
        return resultCommitRecord;
    }

    private static Git getGit(String uri, CredentialsProvider credentialsProvider, String localDir) throws Exception {
        Git git = null;
        if (new File(localDir).exists() ) {
            git = Git.open(new File(localDir));
        } else {
            git = Git.cloneRepository().setCredentialsProvider(credentialsProvider).setURI(uri)
                    .setDirectory(new File(localDir)).call();
        }
        //设置一下post内存，否则可能会报错Error writing request body to server
        git.getRepository().getConfig().setInt(HttpConfig.HTTP, null, HttpConfig.POST_BUFFER_KEY, 512*1024*1024);
        return git;
    }

    /**
     * 拉取最新分支代码，并切换到指定commit
     * @param repoDir
     * @param credentialsProvider
     * @param branchName 主分支
     * @param repository
     * @param processId
     * @param patchId
     * @param branchId  commitId
     * @throws Throwable
     */
    public static void checkoutAndFetch(String repoDir, CredentialsProvider credentialsProvider, String branchName, String repository, String processId, Long patchId, String branchId) throws Throwable{

        try {
            checkoutBranch(new File(repoDir), branchName);
            LogUtil.recordBuildLog("--------------切换主分支为: 【" + branchName + "】--------------", patchId, processId);
            doPull(new File(repoDir), credentialsProvider);
            LogUtil.recordBuildLog("--------------拉取最新代码完成--------------", patchId, processId);
            checkoutCommitId(new File(repoDir), branchId);
            LogUtil.recordBuildLog("--------------切换CommitId为: 【" + branchId + "】--------------", patchId, processId);

        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception("--------------切换分支Id失败【repository:" + repository + "】【分支Id:" + branchId + "】【错误详情：" + e.toString() + "】--------------");
        }
    }

    /**
     *  切换CommitId
     * @param gitDir
     * @param commitId
     * @throws Exception
     */
    private static void checkoutCommitId(File gitDir, String commitId) throws Exception {
        Git git = Git.open(gitDir);
        git.checkout().setCreateBranch(false).setName(commitId).call();
    }

    /**
     * 切换主分支
     * @param gitDir
     * @param branch
     * @throws Exception
     */
    private static void checkoutBranch(File gitDir, String branch) throws Exception {
        Git git = Git.open(gitDir);
        git.checkout().setCreateBranch(false).setName(branch).call();
    }
}
