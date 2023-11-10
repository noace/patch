package com.kyee.upgrade.service.impl;

import com.kyee.feign.IFeignClient;
import com.kyee.upgrade.domain.UpPatchClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PatchRetryService {

    private static final Logger log = LoggerFactory.getLogger(PatchRetryService.class);

    @Autowired
    private IFeignClient feignClient;

    /**
     * 同步云端补丁包状态
     * @param patchClient 端补丁包
     * @return
     * @throws Exception
     *
     * value 何种异常需要重试
     * maxAttempts 最大重试次数
     * delay 重试延迟时间
     * multiplier 上一次延迟时间为本次的倍数
     */
    @Retryable(value = Exception.class, maxAttempts = 20, backoff = @Backoff(delay = 10000, multiplier = 2))
    @Async
    public void syncCloudPatchStatus(UpPatchClient patchClient) throws Exception {

        log.info("------------------------Retry接口执行START----------------------");

        String patchFileName = patchClient.getPatchFileName();
        String patchStatus = patchClient.getPatchStatus();
        String mergePackageFlag = patchClient.getMergePackageFlag();
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("patchFileName", patchFileName);
        paramMap.put("patchStatus", patchStatus);
        paramMap.put("mergePackageFlag", mergePackageFlag);
        paramMap.put("projectId", patchClient.getProjectId());

        // 推送到云端的补丁状态
        Map<String, Object> patchFileMap = feignClient.updateState(paramMap);
        if (!"0".equals(patchFileMap.get("code")+"")) {
            throw new Exception(patchFileMap.get("msg") + "");
        }
        log.info("------------------------Retry接口执行END----------------------");
    }

    /**
     * 最大重试次数下，失败后的回调
     * @param e
     */
    @Recover
    public void recover (Exception e, UpPatchClient patchClient) {

        log.info("同步云端补丁包状态失败,已达到最大重试次数！" + patchClient.getPatchId());
    }
}
