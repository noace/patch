package com.kyee.upgrade.service.impl;

import ch.ethz.ssh2.SCPClient;
import com.alibaba.fastjson.JSONObject;
import com.kyee.feign.IFeignClient;
import com.kyee.upgrade.domain.UpPatchClient;
import com.kyee.upgrade.domain.UpProjectServer;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.kyee.upgrade.mapper.UpPatchClientMapper;
import com.kyee.upgrade.mapper.UpPatchCodelistMapper;
import com.kyee.upgrade.mapper.UpProjectServerMapper;
import com.kyee.upgrade.mapper.UpUpgradeRecordClientMapper;
import com.kyee.upgrade.service.IUpPatchClientService;
import com.kyee.upgrade.utils.*;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 补丁Service业务层处理
 *
 * @author zhanghaoyu
 * @date 2021-10-21
 */
@Service
@Transactional
public class UpPatchClientServiceImpl implements IUpPatchClientService
{
    @Autowired
    private UpPatchClientMapper upPatchClientMapper;

    @Autowired
    private IFeignClient feignClient;
    
    @Autowired
    private UpPatchClientMapper clientMapper;
    
    @Autowired
    private UpUpgradeRecordClientMapper upUpgradeRecordClientMapper;

    @Autowired
    private IUpPatchClientService upPatchClientService;

    /**
     * 查询补丁
     *
     * @param patchId 补丁ID
     * @return 补丁
     */
    @Override
    public UpPatchClient selectUpPatchClientById(Long patchId)
    {
        return upPatchClientMapper.selectUpPatchClientById(patchId);
    }

    /**
     * 查询补丁列表
     *
     * @param upPatchClient 补丁
     * @return 补丁
     */
    @Override
    public List<UpPatchClient> selectUpPatchClientList(UpPatchClient upPatchClient)
    {
        return upPatchClientMapper.selectUpPatchClientList(upPatchClient);
    }

    /**
     * 查询补丁列表，标识第一个包
     *
     * @param upPatchClient 补丁
     * @return 补丁
     */
    @Override
    public List<UpPatchClientExtend> getUpPatchClientList(UpPatchClient upPatchClient)
    {
//        int i = upPatchClientMapper.selectUpPatchClientListCount(upPatchClient);
//        if (upPatchClients.size() <= i) {
//            // 是否为列表第一个
//            upPatchClients.get(0).setFirstPatchFlag(true);
//        }
        List<UpPatchClientExtend> upPatchClientList = upPatchClientMapper.getUpPatchClientList(upPatchClient);
        if (StringUtils.isNotEmpty(upPatchClient.getPatchFileName())) {
            //合包之后，可以使用子包包名查询合包
            List<UpPatchClientExtend> upPatchClientList_son = upPatchClientMapper.getUpPatchListBySonPackage(upPatchClient);
            upPatchClientList.addAll(upPatchClientList_son);
        }
        //获取所有的修复任务的信息
        UpPatchClient reupPatch = new UpPatchClient();
        reupPatch.setBugfixFlag("Y");
        List<UpPatchClient> reUpPatches = upPatchClientMapper.selectUpPatchClient(reupPatch);
        //获取所有已发布的有bug的记录
        UpPatchClient bugUpPatch = new UpPatchClient();
        bugUpPatch.setBugFlag("Y");
        List<UpPatchClient> upPatchesForOne = upPatchClientMapper.selectUpPatchClient(bugUpPatch);
        //key:有bug的记录的id  value:修复bug的记录
        Map<String,List<UpPatchClient>>  oneMap = reUpPatches.stream().filter(s ->s.getBugfixPatch()!=null && !"".equals(s.getBugfixPatch()))
                .collect(Collectors.groupingBy(UpPatchClient::getBugfixPatch));
        //key:合包的id   value:有bug的子包的记录
        Map<String,List<UpPatchClient>>  forOneMap = upPatchesForOne.stream().filter(s ->s.getMergePackageFlag()!=null && !"Y".equals(s.getMergePackageFlag()) && !"N".equals(s.getMergePackageFlag()) )
                .collect(Collectors.groupingBy(UpPatchClient::getMergePackageFlag));
        //key:合包的id   value:修复的子包的记录
        Map<String,List<UpPatchClient>>  reforOneMap = reUpPatches.stream().filter(s ->s.getMergePackageFlag()!=null && !"Y".equals(s.getMergePackageFlag()) && !"N".equals(s.getMergePackageFlag()) )
                .collect(Collectors.groupingBy(UpPatchClient::getMergePackageFlag));
        //key:有bug记录的id  value:有bug的记录
        Map<Long,UpPatchClient>  bugOneMap = upPatchesForOne.stream()
                .collect(Collectors.toMap(UpPatchClient::getPatchId,a->a));
        Map<String, List<UpPatchClient>> finalMap = oneMap;
        Map<Long, UpPatchClient> finalbugOneMap = bugOneMap;
        Map<String, List<UpPatchClient>> finalMapForOneMap = forOneMap;
        Map<String, List<UpPatchClient>> finalMapReforOneMap = reforOneMap;
        upPatchClientList.forEach(item -> {
            if("Y".equals(item.getMergePackageFlag())){ //合包
                List<UpPatchClient> childReUpPatches = finalMapReforOneMap.get("" + item.getPatchId());
                if(childReUpPatches != null && childReUpPatches.size()>0){
                    String bugfixName = "";
                    for(UpPatchClient childUpPatchs : childReUpPatches){
                        String bugfixFlag = childUpPatchs.getBugfixFlag();
                        String bugfixPatch = childUpPatchs.getBugfixPatch();
                        if("Y".equals(bugfixFlag) && bugfixPatch != null && !"".equals(bugfixPatch)){
                            UpPatchClient upPatch1 = finalbugOneMap.get(Long.parseLong(bugfixPatch));
                            if(upPatch1 != null) {
                                if (!"".equals(bugfixName)) {
                                    bugfixName += "<br>";
                                }
                                bugfixName += "【" + upPatch1.getPatchFileName() + "】" + upPatch1.getTopic().replaceAll("\r|\n", "");
                            }
                        }
                    }
                    if(!"".equals(bugfixName)){
                        item.setBugfixName(bugfixName);
                        item.setBugfixFlag("Y");
                    }
                }
                if(!"Y".equals(item.getBugfixFlag() )){
                    item.setBugfixFlag("N");
                }

                List<UpPatchClient> childUpPatches = finalMapForOneMap.get("" + item.getPatchId());
                String itemTopic = item.getTopic();
                item.setMergePackageAllFix("Y");
                if(childUpPatches != null && childUpPatches.size()>0){
                    String repairName = "";
                    String bugName = "";
                    for(UpPatchClient childUpPatchs : childUpPatches){
                        List<UpPatchClient> bugUpPatches = finalMap.get(""+childUpPatchs.getPatchId());
                        if(bugUpPatches != null && bugUpPatches.size()>0) {
                            if(!"Y".equals(item.getBugNoFix())){
                                if( !"".equals(bugName)){
                                    bugName+="，";
                                }
                                bugName += "【" + childUpPatchs.getPatchFileName() + "】" + childUpPatchs.getTopic().replaceAll("\r|\n", "");
                            }

                            for (UpPatchClient upPatchs : bugUpPatches) {
                                if (!"".equals(repairName)) {
                                    repairName += "<br>";
                                }
                                repairName += "【" + upPatchs.getPatchFileName() + "】" + upPatchs.getTopic().replaceAll("\r|\n", "");
                                if(itemTopic.indexOf(upPatchs.getTopic())<0){
                                    item.setMergePackageAllFix("N");
                                }
                            }
                        }else if("Y".equals(childUpPatchs.getBugFlag())){
                            item.setBugNoFix("N");
                            item.setBugDutyName(childUpPatchs.getUpdateBy());
                            bugName += "【" + childUpPatchs.getPatchFileName() + "】" + childUpPatchs.getTopic().replaceAll("\r|\n", "");
                        }
                    }
                    if(!"".equals(repairName)){
                        item.setRepairName(repairName);
                        item.setBugFlag("Y");
                    }
                    if(!"".equals(bugName)){
                        item.setBugName(bugName);
                    }
                }
            }else{ //单包
                List<UpPatchClient> bugUpPatches = finalMap.get(""+item.getPatchId());
                String repairName = "";
                if(bugUpPatches != null && bugUpPatches.size()>0){

                    for(UpPatchClient upPatchs : bugUpPatches){
                        if(!"".equals(repairName)){
                            repairName+="<br>";
                        }
                        repairName+="【"+upPatchs.getPatchFileName()+"】"+upPatchs.getTopic().replaceAll("\r|\n", "");
                    }
                    item.setRepairName(repairName);
                }
                String bugfixFlag = item.getBugfixFlag();
                String bugfixPatch = item.getBugfixPatch();
                if("Y".equals(bugfixFlag) && bugfixPatch != null && !"".equals(bugfixPatch)){
                    UpPatchClient upPatch1 = finalbugOneMap.get(Long.parseLong(bugfixPatch));
                    if(upPatch1 != null){
                        item.setBugfixName("【"+upPatch1.getPatchFileName()+"】"+upPatch1.getTopic().replaceAll("\r|\n", ""));
                    }
                }
            }
        });
        return upPatchClientList;
    }

    /**
     * 新增补丁
     *
     * @param upPatchClient 补丁
     * @return 结果
     */
    @Override
    public int insertUpPatchClient(UpPatchClient upPatchClient)
    {
        return upPatchClientMapper.insertUpPatchClient(upPatchClient);
    }

    /**
     * 修改补丁
     *
     * @param upPatchClient 补丁
     * @return 结果
     */
    @Override
    public int updateUpPatchClient(UpPatchClient upPatchClient)
    {
        return upPatchClientMapper.updateUpPatchClient(upPatchClient);
    }

    /**
     * 删除补丁对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpPatchClientByIds(String ids)
    {
        return upPatchClientMapper.deleteUpPatchClientByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除补丁信息
     *
     * @param patchId 补丁ID
     * @return 结果
     */
    @Override
    public int deleteUpPatchClientById(Long patchId)
    {
        upPatchClientMapper.deleteUpPatchClientChildByParentPatchId(patchId + "");
        // 删除升级记录
        upUpgradeRecordClientMapper.deleteUpUpgradeRecordClientById(patchId);
        return upPatchClientMapper.deleteUpPatchClientById(patchId);
    }

    /**
     * 下载升级文件
     * @param sourceUrl
     * @param uploadUrl
     * @return
     */
    @Override
    public String downloadPatchFile(String sourceUrl, String uploadUrl, String serverUrl) throws Exception {
        return FileUtils.uploadFile(sourceUrl, uploadUrl, serverUrl);
    }

    /**
     * 根据patchFileName判断新增或插入
     * @param upPatchClient
     * @return
     */
    @Override
    public Long addORUpdate(UpPatchClient upPatchClient, String updateBy) throws Exception {
        // 插入端服务器补丁表
        UpPatchClient findPatchClient = new UpPatchClient();
        findPatchClient.setPatchFileName(upPatchClient.getPatchFileName());
        List<UpPatchClient> upPatchClients = selectUpPatchClientList(findPatchClient);
        upPatchClient.setUpBy(updateBy);
        upPatchClient.setUpdateTime(new Date());
        upPatchClient.setAutoUpgradeFlag(upPatchClient.getAutoUpgradeFlag());
        upPatchClient.setBugFlag(upPatchClient.getBugFlag());
        Long patchId = 0L;
        if (null == upPatchClients || upPatchClients.size() == 0) {
            insertUpPatchClient(upPatchClient);
            patchId = upPatchClientMapper.getLastInsertId();
        }else {
            for (UpPatchClient updateUpPatchClient : upPatchClients) {
                patchId = updateUpPatchClient.getPatchId();
                upPatchClient.setPatchId(patchId);
                updateUpPatchClient(upPatchClient);
            }
        }

        return patchId;
    }

    @Override
    public List<UpPatchClient> selectUpPatchListByParentPatchId(Long parentPatchId) {
        List<UpPatchClient> upPatches = upPatchClientMapper.selectUpPatchListByParentPatchId(parentPatchId);
        //获取所有的修复任务的信息
        UpPatchClient reupPatch = new UpPatchClient();
        reupPatch.setBugfixFlag("Y");
        List<UpPatchClient> reUpPatches = upPatchClientMapper.selectUpPatchClient(reupPatch);
        //获取所有已发布的有bug的记录
        UpPatchClient bugUpPatch = new UpPatchClient();
        bugUpPatch.setBugFlag("Y");
        List<UpPatchClient> upPatchesForOne = upPatchClientMapper.selectUpPatchClient(bugUpPatch);
        //key:有bug的记录的id  value:修复bug的记录
        Map<String,List<UpPatchClient>>  oneMap = reUpPatches.stream().filter(s ->s.getBugfixPatch()!=null && !"".equals(s.getBugfixPatch()))
                .collect(Collectors.groupingBy(UpPatchClient::getBugfixPatch));
        //key:有bug记录的id  value:有bug的记录
        Map<Long,UpPatchClient>  bugOneMap = upPatchesForOne.stream()
                .collect(Collectors.toMap(UpPatchClient::getPatchId,a->a));
        Map<String, List<UpPatchClient>> finalMap = oneMap;
        Map<Long, UpPatchClient> finalbugOneMap = bugOneMap;
        upPatches.forEach(item -> {
            List<UpPatchClient> bugUpPatches = finalMap.get(""+item.getPatchId());
            String repairName = "";
            if(bugUpPatches != null && bugUpPatches.size()>0){

                for(UpPatchClient upPatchs : bugUpPatches){
                    if(!"".equals(repairName)){
                        repairName+="<br>";
                    }
                    repairName+="【"+upPatchs.getPatchFileName()+"】"+upPatchs.getTopic().replaceAll("\r|\n", "");
                }
                item.setRepairName(repairName);
            }
            String bugfixFlag = item.getBugfixFlag();
            String bugfixPatch = item.getBugfixPatch();
            if("Y".equals(bugfixFlag) && bugfixPatch != null && !"".equals(bugfixPatch)){
                UpPatchClient upPatch1 = finalbugOneMap.get(Long.parseLong(bugfixPatch));
                if(upPatch1 != null){
                    item.setBugfixName("【"+upPatch1.getPatchFileName()+"】"+upPatch1.getTopic().replaceAll("\r|\n", ""));
                }
            }
        });
        return upPatches;
    }

    /**
     * 修改子补丁
     *
     * @param upPatchClient 补丁
     * @return 结果
     */
    @Override
    public int updateUpPatchClientChild(UpPatchClient upPatchClient)
    {
        return upPatchClientMapper.updateUpPatchClientChild(upPatchClient);
    }

    /**
     * 补丁回退
     *
     * @return 结果
     */
    @Override
    public AjaxResult rollBackUpPatch(Long patchId, String patchFileName, String mergePackageFlag, String isUpgrade)
    {
        String patchState = "";
        String clientPatchState = "";
        if ("Y".equals(isUpgrade)) {
            patchState = ConstantUtil.UpPatchStatus.UPGRADE_ALL.getValue();
            clientPatchState = ConstantUtil.UpgradeStatus.UPGRADE_ALL.getValue();
        } else {
            patchState = ConstantUtil.UpPatchStatus.ROLLBACK.getValue();
            clientPatchState = ConstantUtil.UpPatchStatus.ROLLBACK.getValue();
        }
        UpPatchClient upPatchClient = new UpPatchClient();
        upPatchClient.setPatchId(patchId);
        upPatchClient.setPatchStatus(clientPatchState);

        SysUser sysUser = ShiroUtils.getSysUser();

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("patchFileName", patchFileName);
        paramMap.put("patchStatus", patchState);
        paramMap.put("mergePackageFlag", mergePackageFlag);

        // 推送到云端的补丁状态
        Map<String, Object> patchFileMap = feignClient.updateState(paramMap);
        if(!"0".equals(patchFileMap.get("code")+"")){
            return AjaxResult.error(patchFileMap.get("msg") + "");
        }

        // 更新本地补丁包状态
        updateUpPatchClient(upPatchClient);
        if ("Y".equals(mergePackageFlag)){
            updateUpPatchClientChild(upPatchClient);
        } else if ("N".equals(mergePackageFlag)){
        } else {
            UpPatchClient upPatchClientParent = new UpPatchClient();
            upPatchClientParent.setPatchId(Long.parseLong(mergePackageFlag));
            upPatchClientParent.setPatchStatus(ConstantUtil.UpPatchStatus.ROLLBACK.getValue());
            updateUpPatchClient(upPatchClientParent);
        }

        return AjaxResult.success();
    }

    /**
     * 查询补丁列表名称
     *
     * @param upPatchClient 补丁
     * @return 补丁
     */
    @Override
    public List<String> selectPatchFileName(UpPatchClient upPatchClient)
    {
        return upPatchClientMapper.selectPatchFileName(upPatchClient);
    }

    /**
     * 拉取
     */
    @Override
    public AjaxResult downloadPatchFile(String patchId, String pulledList) {

        SysUser sysUser = ShiroUtils.getSysUser();

        // 根据id获取云补丁包信息
        HashMap<String, Object> param = new HashMap<>();
        param.put("patchId", patchId);
        Map<String, Object> patchMap = feignClient.getUpPatchByPatchId(param);
        if(!"0".equals(patchMap.get("code")+"")){
            return AjaxResult.error(patchMap.get("msg") + "");
        }
        UpPatchClient upPatchClient = JSONObject.parseObject(JSONObject.toJSONString(patchMap.get("data")), UpPatchClient.class);
        if (null == upPatchClient) {
            return AjaxResult.warn("补丁包缺失，请联系管理员！");
        }

        String sourceUrl = upPatchClient.getPatchFileUrl();
        if (null == sourceUrl || sourceUrl.isEmpty()) {
            return AjaxResult.warn("未获取到补丁文件路径，请联系管理员！");
        }

        String compilePaths = (String)patchMap.get("compiles");
        String upgrade = (String)patchMap.get("upgrade");

        // 拉取时利用任务合并时间解决跨包问题 YUNHIS-37875
        HashMap<String, Object> ppParam = new HashMap<>();
        ppParam.put("productId", upPatchClient.getProductId());
        ppParam.put("projectId", upPatchClient.getProjectId());
        ppParam.put("patchId", patchId);
        ppParam.put("pulledList", pulledList);
        Map<String, Object> ppMap = feignClient.pullValidatePatch(ppParam);
        if(!"0".equals(ppMap.get("code")+"")){
            return AjaxResult.error(ppMap.get("msg") + "");
        }
        String tip = ppMap.get("tip") + "";
        if (StringUtils.isNotEmpty(tip)) {
            return AjaxResult.warn(tip);
        }

        // 本地资源路径
        String localPath = RuoYiConfig.getProfile();
        String filePath = StringUtils.substringAfter(sourceUrl, Constants.RESOURCE_PREFIX);
        String prodectPath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        // 文件上传真实路径
        String uploadUrl = localPath + prodectPath;
        // 文件上传虚拟路径，即表内存储路径
        String serverUrl = Constants.RESOURCE_PREFIX + prodectPath;

        try {
            // 从云服务器下载补丁文件到端服务器
            String patchFileUrl = upPatchClientService.downloadPatchFile(sourceUrl, uploadUrl, serverUrl);
            upPatchClient.setPatchFileUrl(patchFileUrl);

            // 编译路径插入UpPatchClient中
            upPatchClient.setCompileList(compilePaths);
            upPatchClient.setAutoUpgradeFlag(upgrade);
            // 插入父包数据
            upPatchClient.setPatchStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());

            // 修改合包数据
            Long parentPatchId = upPatchClientService.addORUpdate(upPatchClient, sysUser.getUserName());
            //若不是合包且为修复任务
            if (upPatchClient.getBugPatchFileName() != null && "N".equals(upPatchClient.getMergePackageFlag())){
                //根据bug包名修改端上的bug_flag字段
                clientMapper.updateBugFlag(upPatchClient.getBugPatchFileName());
                //根据包名修改bugfixPatch字段
                UpPatchClient upPatchClient1 = clientMapper.selectUpPatchClientByName(upPatchClient.getBugPatchFileName());
                if(upPatchClient1 != null && upPatchClient1.getPatchId()!=null && !"".equals(""+upPatchClient1.getPatchId())){
                    clientMapper.updateBugfixPatch(upPatchClient1.getPatchId(),upPatchClient.getPatchFileName());
                }
            }

            // 调用云服务器子包列表接口
            if ("Y".equals(upPatchClient.getMergePackageFlag())) {
                HashMap<String, Object> paramMap = new HashMap<>();
                paramMap.put("patchId", patchId);
                Map<String, Object> patchFileMap = feignClient.getPatchChildList(paramMap);
                if(!"0".equals(patchFileMap.get("code")+"")){
                    return AjaxResult.error(patchFileMap.get("msg") + "");
                }

                // 插入子包数据
                List<UpPatchClient> childUpPatchs = JSONObject.parseArray(
                        JSONObject.toJSONString(patchFileMap.get("data")), UpPatchClient.class);
                // 编译路径
                String childCompilePaths = (String)patchFileMap.get("compiles");
                List<UpPatchClient> patchClients = childUpPatchs.stream().filter(s -> "Y".equals(s.getBugFlag())).collect(Collectors.toList());

                if (patchClients.size()>0) {
                    upPatchClient.setBugFlag("Y");
                    upPatchClientService.addORUpdate(upPatchClient, sysUser.getUserName());
                }

                for (UpPatchClient childUpPatch : childUpPatchs) {
                    childUpPatch.setMergePackageFlag(parentPatchId + "");
                    childUpPatch.setPatchStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
                    childUpPatch.setCompileList(childCompilePaths);

//                    if (childUpPatch.getBugPatchFileName() != null) {
//                        //根据包名修改bugfixPatch字段
//                        UpPatchClient upPatchClient1 = clientMapper.selectUpPatchClientByName(childUpPatch.getBugPatchFileName());
//                        childUpPatch.setBugfixPatch(""+upPatchClient1.getPatchId());
//                    }
                    upPatchClientService.addORUpdate(childUpPatch, sysUser.getUserName());
                    if (childUpPatch.getBugPatchFileName() != null) {
                        //根据bug包名修改端上的bug_flag字段
                        clientMapper.updateBugFlag(childUpPatch.getBugPatchFileName());
                        //根据包名修改bugfixPatch字段
                        UpPatchClient upPatchClient1 = clientMapper.selectUpPatchClientByName(childUpPatch.getBugPatchFileName());
                        if(upPatchClient1 != null && upPatchClient1.getPatchId()!=null && !"".equals(""+upPatchClient1.getPatchId())){
                            clientMapper.updateBugfixPatch(upPatchClient1.getPatchId(),childUpPatch.getBugPatchFileName());
                        }
                    }
                }
                for (UpPatchClient childUpPatch : childUpPatchs) {
                    if (childUpPatch.getBugPatchFileName() != null) {
                        //根据包名修改bugfixPatch字段
                        UpPatchClient upPatchClient1 = clientMapper.selectUpPatchClientByName(childUpPatch.getBugPatchFileName());
                        if(upPatchClient1 != null && upPatchClient1.getPatchId()!=null && !"".equals(""+upPatchClient1.getPatchId())){
                            clientMapper.updateBugfixPatch(upPatchClient1.getPatchId(),childUpPatch.getPatchFileName());
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("下载升级文件失败，请联系管理员！");
        }
        return AjaxResult.success();
    }

    /**
     * 查询未升级且小于当前包编译时间的补丁包列表
     *
     * @return 结果
     */
    @Override
    public AjaxResult validateUpgrade(Long patchId) {
        UpPatchClient patchClient = selectUpPatchClientById(patchId);
        // 查询未升级且小于当前包编译时间的补丁包列表
        patchClient.setPatchStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
        List<UpPatchClient> patchClientList = upPatchClientMapper.getPatchByRepeatValidate(patchClient);

        if (!CollectionUtils.isEmpty(patchClientList)) {
            if (CollectionUtils.isEmpty(patchClientList)) {
                AjaxResult.success();
            }
            Set<String> nameList = new HashSet<>();
            Map<Long, UpPatchClient> idMap = patchClientList.stream().collect(Collectors.toMap(UpPatchClient::getPatchId, s -> s));

//            Map<String, Long> codeMap = patchClientList.stream().collect(Collectors.toMap(UpPatchClient::getCodeList, UpPatchClient::getPatchId));
//            String codeToStr = patchClientList.stream().map(UpPatchClient::getCodeList).collect(Collectors.joining());

            // 待升级的包
            List<String> upgradeCodeList = Arrays.asList(patchClient.getCodeList().replaceAll("\r|\n", "").split(","));
            List<String> codeListPublish = upgradeCodeList.stream().distinct().filter(s -> !s.endsWith("SQL")).collect(Collectors.toList());

            for (UpPatchClient upPatchClient : patchClientList) {
                for (String codePath : codeListPublish) {
                    if (upPatchClient.getCodeList().contains(codePath)) {
                        UpPatchClient up = idMap.get(upPatchClient.getPatchId());
                        String patchFileName = up.getPatchFileName();
                        nameList.add(patchFileName);
                        continue;
                    }
                }
            }
            if (StringUtils.isNotEmpty(nameList)) {
                // 升级提示
                return AjaxResult.error("【请注意】系统检测到该补丁包已携带部分代码，请先升级【" + "," + nameList.toString() + "】，否则将出现升级问题！");
            }
        }
        return AjaxResult.success();
    }
}
