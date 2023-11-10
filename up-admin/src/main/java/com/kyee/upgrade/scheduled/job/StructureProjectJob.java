package com.kyee.upgrade.scheduled.job;

import com.kyee.upgrade.cloud.asynpackpatch.IPatchService;
import com.kyee.upgrade.cloud.asynpackpatch.impl.HisScheduledServiceImpl;
import com.kyee.upgrade.cloud.asynpackpatch.impl.LisPatchServiceImpl;
import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpProjectProduct;
import com.kyee.upgrade.mapper.UpPatchMapper;
import com.kyee.upgrade.mapper.UpProjectProductMapper;
import com.kyee.upgrade.service.IUpPatchService;
import com.kyee.upgrade.service.impl.HisV3FullPackService;
import com.kyee.upgrade.utils.ConstantUtil;
import com.kyee.upgrade.utils.LogUtil;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 检出分支构建项目
 *
 * @author zhanghaoyu
 * @date 2021-10-19
 */
@Service
public class StructureProjectJob {

    @Autowired
    private IUpPatchService upPatchService;

    @Autowired
    private IPatchService hisV3PatchServiceImpl;

    @Autowired
    private UpPatchMapper upPatchMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UpProjectProductMapper projectProductMapper;

    @Autowired
    private TencentMessageJob tencentMessageJob;

    @Autowired
    private HisV3FullPackService hisV3FullPackService;

    @Autowired
    private LisPatchServiceImpl lisPatchService;

    @Autowired
    private HisScheduledServiceImpl hisScheduledService;

    public void executeCommand(Map<String, Object> paramMap) throws Throwable {
        String sourceUrl = paramMap.get("sourceUrl") + "";
        String branch = paramMap.get("branch") + "";
        String commandInfo = paramMap.get("commandInfo") + "";
        String commandType = paramMap.get("commandType") + "";
        long patchId = (long) paramMap.get("patchId");
        String processId = String.valueOf(paramMap.get("processId"));
        String repository = paramMap.get("repository") + "";
        int productId = (int)paramMap.get("productId");

//        TencentMessage textMsg = new TencentMessage();
//        MessageText textText = new MessageText();
        UpPatch patch = new UpPatch();
        if ("REGISTERED".equals(commandInfo)) {
            LogUtil.recordBuildLog(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>KYEE UPPATCH<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", patchId, processId);
            LogUtil.recordBuildLog("--------------登记开始【repository:" + repository + "】【branch:" + branch + "】--------------", patchId, processId);
            try {
                if (ConstantUtil.PatchType.RELEASE.name().equals(commandType)) {

                    // 版本包打包开始
                    patch = hisV3FullPackService.fullPack(sourceUrl, branch, processId, patchId, repository);
                    patch.setPatchStatus(ConstantUtil.UpPatchStatus.UPGRADE_ALL.getValue());
                    LogUtil.recordBuildLog("全量包打包成功！", patchId, processId);
                } else {
                    // LIS
                    if (7 == productId) {
                        patch = lisPatchService.execute(sourceUrl, branch, processId, patchId, repository);
                    } else if (10 == productId) {
                        patch = hisScheduledService.execute(sourceUrl, branch, processId, patchId, repository);
                    } else {
                        // HIS AUTH_INTER
                        patch = hisV3PatchServiceImpl.execute(sourceUrl, branch, processId, patchId, repository);
                    }

                    // 合并包打包完成，状态改为已发布
                    if ("Y".equals(patch.getMergePackageFlag())) {
                        patch.setPatchStatus(ConstantUtil.UpPatchStatus.PUBLISHED.getValue());
                        LogUtil.recordBuildLog("单任务合并包打包成功！", patchId, processId);

                        // 更新版本号
                        UpProjectProduct pp = new UpProjectProduct();
                        pp.setProductId(productId);
                        pp.setProjectId(patch.getProjectId());
                        pp.setUpgradeVersion(patch.getUpgradeVersion());
                        projectProductMapper.updateByProductIdAndProjectId(pp);
                    } else {
                        patch.setPatchStatus(ConstantUtil.UpPatchStatus.PKG_SUCCESS.getValue());
                        LogUtil.recordBuildLog("单任务包打包成功！", patchId, processId);
                        publish(patch,patchId,processId);
                        // BUG包打包成功
                        repairPatch(patch, patchId, processId);

                        // 更新版本号
                        UpProjectProduct pp = new UpProjectProduct();
                        pp.setProductId(productId);
                        pp.setProjectId(patch.getProjectId());
                        pp.setUpgradeVersion(patch.getUpgradeVersion());
                        projectProductMapper.updateByProductIdAndProjectId(pp);
                    }
                }
            } catch (Throwable e) {
                patch.setPatchId(patchId);
                patch.setPatchStatus(ConstantUtil.UpPatchStatus.PKG_FAIL.getValue());
                LogUtil.recordBuildLog(e.toString(), patchId, processId);

//                Map<String,String> map = upPatchMapper.getMessageInfo(patchId);
//                if (!"Y".equals(map.get("merge"))) {
//                    // 打包失败通知
//                    textText.setContent("JIRA任务号【" + map.get("jiraNo") +"】打包失败，请及时处理。\n");
//                    textText.setMentioned_list(Arrays.asList(map.get("id")));
//                    textMsg.setMsgtype("text");
//                    textMsg.setText(textText);
//                    tencentMessageJob.sendMessage(textMsg);
//                }
                throw new Exception(e);
            } finally {
                upPatchService.updateUpPatchNoSession(patch);
                LogUtil.recordBuildLog("包状态变更" + patch.getPatchStatus() + "，打包结束。", patchId, processId);
                LogUtil.recordBuildLog(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>KYEE UPPATCH<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<", patchId, processId);
            }
        }
    }


    /**
     * 自动发布，自动发布不影响原本的打包程序
     * @param patch
     */
    private void publish(UpPatch patch,long patchId,String processId){
        try{
            Long updateByUserId = patch.getUpdateByUserId();
            SysUser sysUser = sysUserMapper.selectUserById(updateByUserId);
            if(sysUser == null || !"Y".equals(sysUser.getAutoPublish())){ //个人设置里面没有开启自动打包
                return;
            }
            // 已锁定的包禁止发布
            if (ConstantUtil.UpPatchStatus.LOCKED.getValue().equals(patch.getPatchStatus())) {
                return ;
            }

            List<SysUserRole> roleList = userRoleMapper.selectUserRoleByUserId(sysUser.getUserId());
            List<Long> roleIds = roleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            UpProjectProduct pp = new UpProjectProduct();
            pp.setProjectId(patch.getProjectId());
            pp.setProductId(patch.getProductId());
            List<UpProjectProduct> upProjectProducts = projectProductMapper.selectUpProjectProductList(pp);
            if("Y".equals(upProjectProducts.get(0).getTestFlag()) && !roleIds.contains(4L)) { //需要测试发包
                return ;
            }
            //加急包发布通知项目产品负责人
            if ("Y".equals(patch.getExpedited())) {
                Map<String, String> patchInfo = upPatchMapper.getBugTaskName(Long.toString(patch.getPatchId()));
                Map<String, String> productCharge = upPatchMapper.getProductChargeName(patch.getProjectId(), patch.getProductId());
                String projectPrincipalId = MapUtils.isNotEmpty(productCharge) ? productCharge.get("id") : null;
                List<Map> users = new ArrayList<>();
                if(StringUtils.isNotEmpty(projectPrincipalId)){
                    users = sysUserMapper.selectLoginName(Arrays.asList(projectPrincipalId.split(",")));
                }
                List<String> jobNos = new ArrayList<>();
                List<String> names = new ArrayList<>();
                users.forEach(map -> {
                    jobNos.add((String) map.get("login_name"));
                    names.add((String) map.get("user_name"));
                });

                tencentMessageJob.sendExpeditedUpgradeMassage(patchInfo.get("productname"), patchInfo.get("projectname"), patch.getUpdateBy(), patch.getPatchFileName(), patch.getJiraNo(), patch.getTopic(), jobNos, names);
            }
            patch.setPatchStatus(ConstantUtil.UpPatchStatus.PUBLISHED.getValue()); // 发布
            patch.setPublicBy(sysUser.getUserName());
        }catch (Exception e){
            LogUtil.recordBuildLog("打包时自动发布失败", patchId, processId);
        }
    }

    /**
     * BUG包打包成功，修改相应的回退包和锁定包状态
     * @param patch
     * @param patchId
     * @param processId
     */
    private void repairPatch(UpPatch patch, Long patchId, String processId) {
        // 修复Bug包

        String bugfixPatch = patch.getBugfixPatch();
        if(StringUtils.isNotEmpty(bugfixPatch)) {
            UpPatch up = upPatchMapper.selectUpPatchById(Long.parseLong(bugfixPatch));
            if (!Objects.isNull(up) && ConstantUtil.UpPatchStatus.ROLLBACK.getValue().equals(up.getPatchStatus())) {
                UpPatch bugPatch = new UpPatch();
                bugPatch.setPatchId(Long.parseLong(bugfixPatch));
                bugPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PKG_SUCCESS.getValue());
                bugPatch.setUpdateTime(new Date());
                upPatchService.updateUpPatchNoSession(bugPatch);
                LogUtil.recordBuildLog("--------------修复补丁包Id:" + bugfixPatch + "--------------", patchId, processId);
            }
        }

        // 查询是否含有关联的锁定包，有则改为已打包状态
        String lockId = patch.getLockId();
        if (StringUtils.isNotEmpty(lockId)) {
            String[] lockIdArr = lockId.split(",");
            for (String id : lockIdArr) {
                UpPatch ch = new UpPatch();
                ch.setPatchId(Long.parseLong(id));
                ch.setUpdateTime(new Date());
                ch.setPatchStatus(ConstantUtil.UpPatchStatus.PKG_SUCCESS.getValue());
                upPatchMapper.updateUpPatch(ch);
            }
            LogUtil.recordBuildLog("--------------修复BUG关联包Id:" + lockId + "--------------", patchId, processId);
        }
    }
}
