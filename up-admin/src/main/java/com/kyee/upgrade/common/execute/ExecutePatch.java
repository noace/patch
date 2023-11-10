package com.kyee.upgrade.common.execute;

import com.kyee.upgrade.common.build.CommonBuildCode;
import com.kyee.upgrade.common.clone.CloneCode;
import com.kyee.upgrade.common.review.CodeReviewToolkit;
import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpProjectProduct;
import com.kyee.upgrade.mapper.UpPatchMapper;
import com.kyee.upgrade.service.IUpPatchService;
import com.kyee.upgrade.utils.ConstantUtil;
import com.kyee.upgrade.utils.LogUtil;
import com.kyee.upgrade.utils.OSValidator;
import com.ruoyi.common.config.RuoYiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * 描述：执行打包
 * 作者：DangHangHang
 * 时间：2023年06月12日 18:17
 */
@Component
public class ExecutePatch {

    @Value(value = "${ruoyi.workspacePath}")
    private String workspacePath;

    @Autowired
    private UpPatchMapper upPatchMapper;

    @Autowired
    private CommonBuildCode commonBuildCode;

    @Autowired
    private IUpPatchService upPatchService;

    @Resource
    private CodeReviewToolkit codeReviewToolkit;

    /**
     * 描述：HIS打包支持
     * 作者：DangHangHang
     * 时间：2023年06月12日 18:19
     */
    public UpPatch executeHis(String sourceUrl, String branch, String processId, Long patchId, String repository, UpProjectProduct result) throws Throwable {
        UpPatch patch = upPatchService.selectUpPatchById(patchId);

        LogUtil.recordBuildLog("--------------检出代码开始【repository】:【" + repository + "】--------------", patchId, processId);
        if (ConstantUtil.PatchBranch.MASTERBRANCH.getValue().equals(result.getPatchBranch())) {
            CloneCode.executeCheckCode(sourceUrl,branch,processId,patchId, repository);
        } else {
            CloneCode.checkoutCommitId(sourceUrl,branch,processId,patchId, repository, patch.getBranchId());
        }
        LogUtil.recordBuildLog("--------------检出代码成功--------------", patchId, processId);

        if (!"Y".equals(patch.getMergePackageFlag()) && OSValidator.isUnix()) {
            codeReviewToolkit.codeReview(patch, sourceUrl, patchId, processId);
        }

        LogUtil.recordBuildLog("--------------编译开始--------------", patchId, processId);
        // 模块编译
        if (ConstantUtil.BuildType.MODULE_BUILD.getValue().equals(result.getBuildType())) {
            commonBuildCode.jarExecuteBuild(sourceUrl, processId, patchId);
        } else if (ConstantUtil.BuildType.BOOT_BUILD.getValue().equals(result.getBuildType())){
            // Boot编译
            commonBuildCode.bootExecuteBuild(sourceUrl, processId,patchId, patch);
        } else if (ConstantUtil.BuildType.ALL_BUILD.getValue().equals(result.getBuildType())){
            // 全量编译
            commonBuildCode.executeBuild(sourceUrl, processId, patchId, patch);
        } else if (ConstantUtil.BuildType.CLASS_BUILD.getValue().equals(result.getBuildType())) {
//            commonBuildCode.classBuild(sourceUrl, processId, patchId, patch);
            commonBuildCode.jarExecuteBuild(sourceUrl, processId, patchId);
        }
        LogUtil.recordBuildLog("--------------编译成功--------------", patchId, processId);
        return patch;
    }

    /**
     * 描述：LIS打包支持
     * 作者：DangHangHang
     * 时间：2023年06月12日 18:18
     */
    public UpPatch executeLis(String sourceUrl, String branch, String processId, Long patchId, String repository, UpProjectProduct result) throws Throwable {
        UpPatch patch = upPatchMapper.selectUpPatchById(patchId);
        // 检出HIS_V5代码
        String hisSourceUrl = RuoYiConfig.getWorkspacePath() + File.separator + "workspace_his_v5_lis" + File.separator + "his_v5";
        String hisRepository = "http://s.kyee.com.cn/scm/yly/his_v5.git";
        String hisBranch = "project/dev/his_to_lis";
        LogUtil.recordBuildLog("--------------检出HIS_V5代码开始--------------", patchId, processId);
        LogUtil.recordBuildLog("--------------仓库:【"+hisRepository+"】--------------", patchId, processId);
        LogUtil.recordBuildLog("--------------分支:【"+hisBranch+"】--------------", patchId, processId);
        CloneCode.executeCheckCode(hisSourceUrl,hisBranch,processId,patchId, hisRepository);
        LogUtil.recordBuildLog("--------------检出HIS_V5代码成功--------------", patchId, processId);

        // 检出LIS代码
        LogUtil.recordBuildLog("--------------检出LIS代码开始:【" + repository + "】--------------", patchId, processId);
        if (ConstantUtil.PatchBranch.MASTERBRANCH.getValue().equals(result.getPatchBranch())) {
            CloneCode.executeCheckCode(sourceUrl,branch,processId,patchId, repository);
        } else {
            CloneCode.checkoutCommitId(sourceUrl,branch,processId,patchId, repository, patch.getBranchId());
        }
        LogUtil.recordBuildLog("--------------检出LIS代码成功--------------", patchId, processId);

        LogUtil.recordBuildLog("--------------编译开始--------------", patchId, processId);
        if (ConstantUtil.BuildType.MODULE_BUILD.getValue().equals(result.getBuildType())) {
            commonBuildCode.jarExecuteBuild(sourceUrl, processId, patchId);
        } else if (ConstantUtil.BuildType.ALL_BUILD.getValue().equals(result.getBuildType())){
            // 全量编译
            commonBuildCode.executeBuild(sourceUrl, processId, patchId, patch);
        }
        LogUtil.recordBuildLog("--------------编译成功--------------", patchId, processId);
        return patch;
    }
}
