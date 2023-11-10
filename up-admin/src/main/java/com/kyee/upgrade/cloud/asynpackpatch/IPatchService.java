package com.kyee.upgrade.cloud.asynpackpatch;

import com.kyee.upgrade.common.domain.GitCheckResult;
import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpProjectProduct;

public interface IPatchService {

    /**
     * 总入口
     * @param sourceUrl
     * @param branch
     * @param processId
     * @param patchId
     * @return
     */
    public UpPatch execute(String sourceUrl,String branch,String processId,Long patchId, String repository)  throws Throwable ;

    /**
     * 检出代码入口
     *
     * @param patchId 补丁包管理ID
     * @return 补丁包管理
     */
    public GitCheckResult executeCheckCode(String sourceUrl, String branch, String processId, Long patchId, String repository) throws Throwable;


    /**
     * 编译入口
     *
     * @param patchId 补丁包管理ID
     * @return 补丁包管理
     */
    public void executeBuild(String sourceUrl,String branch,String processId,Long patchId, UpPatch patch) throws Throwable;


    /**
     * 打包执行入口
     *
     * @param patchId 补丁包管理ID
     * @return 补丁包管理
     */
    public UpPatch executePkg(String sourceUrl,String branch,String processId,Long patchId, UpProjectProduct result, UpPatch patch) throws Throwable;

    /**
     * Jar包编译入口
     *
     * @param patchId 补丁包管理ID
     * @return 补丁包管理
     */
    void JarExecuteBuild(String sourceUrl,String branch,String processId,Long patchId, UpPatch patch) throws Throwable;

    /**
     * Boot编译入口
     *
     * @param patchId 补丁包管理ID
     * @return 补丁包管理
     */
    void BootExecuteBuild(String sourceUrl,String branch,String processId,Long patchId, UpPatch patch) throws Throwable;

    /**
     * Class编译入口
     * @param sourceUrl
     * @param branch
     * @param processId
     * @param patchId
     * @param patch
     * @throws Throwable
     */
    void ClassBuild(String sourceUrl, String branch, String processId, Long patchId, UpPatch patch) throws Throwable;
}
