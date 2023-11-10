package com.kyee.upgrade.cloud.asynpackpatch;

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
    UpPatch execute(String sourceUrl,String branch,String processId,Long patchId, String repository)  throws Throwable ;

    /**
     * 打包执行入口
     *
     * @param patchId 补丁包管理ID
     * @return 补丁包管理
     */
    UpPatch executePkg(String sourceUrl,String branch,String processId,Long patchId, UpProjectProduct result, UpPatch patch) throws Throwable;
}
