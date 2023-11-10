package com.kyee.feign;

import com.alibaba.fastjson.JSONObject;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.mapper.*;
import com.kyee.upgrade.service.IUpCommandService;
import com.kyee.upgrade.service.IUpPatchService;
import com.kyee.upgrade.service.IUpProjectServerService;
import com.kyee.upgrade.service.IUpUpgradeRecordService;
import com.kyee.upgrade.utils.ConstantUtil;
import com.ruoyi.common.config.ServerConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 跨服务器接口调用服务端
 *
 * @author zhanghaoyu
 * @date 2021-10-21
 */
@RestController
@RequestMapping("/server")
public class FeignServer {

    private static final Logger log = LoggerFactory.getLogger(FeignServer.class);

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private IUpPatchService upPatchService;

    @Autowired
    private IUpUpgradeRecordService upUpgradeRecordService;

    @Autowired
    private IUpProjectServerService projectServerService;

    @Autowired
    private UpPatchCodelistMapper patchCodelistMapper;

    @Autowired
    private IUpCommandService upCommandService;

    @Autowired
    private UpProductMapper upProductMapper;


    @Autowired
    private UpProjectMapper upProjectMapper;

    @Autowired
    private UpProjectProductMapper projectProductMapper;

    @Autowired
    private UpPatchMapper upPatchMapper;

    /**
     * 获取云服务器发布补丁列表
     * @return
     */
    @PostMapping("/getPatchFileList")
    public Map<String, Object> getPatchFileList(@RequestBody HashMap paramMap) {
        UpPatch upPatch = new UpPatch();
        UpPatchClient patchClient = JSONObject.parseObject(JSONObject.toJSONString(paramMap.get("patchClient")), UpPatchClient.class);
        BeanUtils.copyProperties(patchClient, upPatch);

        // 获取合包打包中
        upPatch.setDelFlag("N");
        upPatch.setMergePackageFlag("Y");
        upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PKGING.getValue());
        List<UpPatch> upPatches = upPatchService.selectUpPatchList(upPatch);

        // 获取合包打包失败
        upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PKG_FAIL.getValue());
        upPatches.addAll(upPatchService.selectUpPatchList(upPatch));

        // 获取合包发布
        upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PUBLISHED.getValue());
        upPatches.addAll(upPatchService.selectUpPatchList(upPatch));
        //获取所有的子包
        List<UpPatch> childupPatches = upPatchService.selectUpPatchChildList(upPatches);

        // 获取已发布的单包
        upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PUBLISHED.getValue());
        upPatch.setMergePackageFlag("N");
        List<UpPatch> oneUpPatches = upPatchService.selectUpPatchList(upPatch);
        upPatches.addAll(oneUpPatches);
        if (StringUtils.isNotEmpty(upPatch.getPatchFileName()) ){
            //合包之后，可以使用子包包名查询合包
            List<UpPatch> upPatchList_son = upPatchMapper.getUpPatchListBySonPackage(upPatch);
            upPatches.addAll(upPatchList_son);
        }
        //获取所有的已发布的修复任务的信息
        UpPatch reupPatch = new UpPatch();
        reupPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PUBLISHED.getValue());
        reupPatch.setBugfixFlag("Y");
        List<UpPatch> reUpPatches = upPatchService.selectUpPatchList(reupPatch);
        //获取所有已发布的有bug的记录
        UpPatch bugUpPatch = new UpPatch();
        bugUpPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PUBLISHED.getValue());
        bugUpPatch.setBugFlag("Y");
        List<UpPatch> upPatchesForOne = upPatchService.selectUpPatchList(bugUpPatch);

        //key:合包的id   value:子包的记录
        Map<String,List<UpPatch>>  childMap = childupPatches.stream().filter(s ->s.getMergePackageFlag()!=null && !"Y".equals(s.getMergePackageFlag()) && !"N".equals(s.getMergePackageFlag()) )
                .collect(Collectors.groupingBy(UpPatch::getMergePackageFlag));

        //key:有bug的记录的id  value:修复bug的记录
        Map<String,List<UpPatch>>  oneMap = reUpPatches.stream().filter(s ->s.getBugfixPatch()!=null && !"".equals(s.getBugfixPatch()))
                .collect(Collectors.groupingBy(UpPatch::getBugfixPatch));
        //key:合包的id   value:有bug的子包的记录
        Map<String,List<UpPatch>>  forOneMap = upPatchesForOne.stream().filter(s ->s.getMergePackageFlag()!=null && !"Y".equals(s.getMergePackageFlag()) && !"N".equals(s.getMergePackageFlag()) )
                .collect(Collectors.groupingBy(UpPatch::getMergePackageFlag));
        //key:合包的id   value:修复的子包的记录
        Map<String,List<UpPatch>>  reforOneMap = reUpPatches.stream().filter(s ->s.getMergePackageFlag()!=null && !"Y".equals(s.getMergePackageFlag()) && !"N".equals(s.getMergePackageFlag()) )
                .collect(Collectors.groupingBy(UpPatch::getMergePackageFlag));
        //key:有bug记录的id  value:有bug的记录
        Map<Long,UpPatch>  bugOneMap = upPatchesForOne.stream()
                .collect(Collectors.toMap(UpPatch::getPatchId,a->a));

        // 追加云服务器地址
        Map<String, List<UpPatch>> finalMap = oneMap;
        Map<Long, UpPatch> finalbugOneMap = bugOneMap;
        Map<String, List<UpPatch>> finalMapForOneMap = forOneMap;
        Map<String, List<UpPatch>> finalMapReforOneMap = reforOneMap;
        Map<String, List<UpPatch>> finalChildMap = childMap;
        upPatches.forEach(item -> {
            item.setPatchFileUrl(serverConfig.getUrl() + item.getPatchFileUrl());
            if("Y".equals(item.getMergePackageFlag())){ //合包
                List<UpPatch> childlist = finalChildMap.get("" + item.getPatchId());
                String childPackageName = "";
                if(childlist != null && childlist.size()>0){
                    for(UpPatch upPatch1:childlist){
                        if(!"".equals(childPackageName)){
                            childPackageName+=",";
                        }
                        childPackageName+=upPatch1.getPatchFileName();
                    }
                }
                item.setChildPackageName(childPackageName);
                List<UpPatch> childReUpPatches = finalMapReforOneMap.get("" + item.getPatchId());
                if(childReUpPatches != null && childReUpPatches.size()>0){
                    String bugfixName = "";
                    for(UpPatch childUpPatchs : childReUpPatches){
                        String bugfixFlag = childUpPatchs.getBugfixFlag();
                        String bugfixPatch = childUpPatchs.getBugfixPatch();
                        if("Y".equals(bugfixFlag) && bugfixPatch != null && !"".equals(bugfixPatch)){
                            UpPatch upPatch1 = finalbugOneMap.get(Long.parseLong(bugfixPatch));
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

                List<UpPatch> childUpPatches = finalMapForOneMap.get("" + item.getPatchId());
                String itemTopic = item.getTopic();
                item.setMergePackageAllFix("Y");
                if(childUpPatches != null && childUpPatches.size()>0){
                    String repairName = "";
                    String repairNameNoNum = "";
                    String bugName = "";
                    for(UpPatch childUpPatchs : childUpPatches){
                        List<UpPatch> bugUpPatches = finalMap.get(""+childUpPatchs.getPatchId());
                        if(bugUpPatches != null && bugUpPatches.size()>0) {
                            if(!"Y".equals(item.getBugNoFix())){
                                if( !"".equals(bugName)){
                                    bugName+="，";
                                }
                                bugName += "【" + childUpPatchs.getPatchFileName() + "】" + childUpPatchs.getTopic().replaceAll("\r|\n", "");
                            }

                            for (UpPatch upPatchs : bugUpPatches) {
                                if (!"".equals(repairName)) {
                                    repairName += "<br>";
                                    repairNameNoNum += "<br>";
                                }
                                repairName += "【" + upPatchs.getPatchFileName() + "】" + upPatchs.getTopic().replaceAll("\r|\n", "");
                                repairNameNoNum +=  upPatchs.getPatchFileName();
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
                        item.setRepairNameNoNum(repairNameNoNum);
                        item.setBugFlag("Y");
                    }
                    if(!"".equals(bugName)){
                        item.setBugName(bugName);
                    }
                }
            }else{ //单包
                List<UpPatch> bugUpPatches = finalMap.get(""+item.getPatchId());
                String repairName = "";
                String repairNameNoNum = "";
                if(bugUpPatches != null && bugUpPatches.size()>0){

                    for(UpPatch upPatchs : bugUpPatches){
                        if(!"".equals(repairName)){
                            repairName+="<br>";
                            repairNameNoNum+="<br>";
                        }
                        repairName+="【"+upPatchs.getPatchFileName()+"】"+upPatchs.getTopic().replaceAll("\r|\n", "");
                        repairNameNoNum+=upPatchs.getPatchFileName();
                    }
                    item.setRepairName(repairName);
                    item.setRepairNameNoNum(repairNameNoNum);
                    item.setBugName("【" + item.getPatchFileName() + "】" + item.getTopic().replaceAll("\r|\n", ""));
                }
                String bugfixFlag = item.getBugfixFlag();
                String bugfixPatch = item.getBugfixPatch();
                if("Y".equals(bugfixFlag) && bugfixPatch != null && !"".equals(bugfixPatch)){
                    UpPatch upPatch1 = finalbugOneMap.get(Long.parseLong(bugfixPatch));
                    if(upPatch1 != null){
                        item.setBugfixName("【"+upPatch1.getPatchFileName()+"】"+upPatch1.getTopic().replaceAll("\r|\n", ""));
                    }
                }
            }
        });
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("msg", "success");
        resultMap.put("data", upPatches);
        return resultMap;
    }

    /**
     * 根据id获取补丁信息
     */
    @PostMapping("/getUpPatchByPatchId")
    public Map<String, Object> getUpPatchByPatchId(@RequestBody HashMap paramMap) {
        Long patchId = Long.valueOf(paramMap.get("patchId") + "");
        UpPatch upPatch = upPatchService.selectUpPatchById(patchId);
        //若该包为修复bug包，则查询原bug包包名
        if ("Y".equals(upPatch.getBugfixFlag()) && upPatch.getBugfixPatch() != null) {
            UpPatch bugUpPatch = upPatchService.selectUpPatchById(Long.valueOf(upPatch.getBugfixPatch()));
            upPatch.setBugPatchFileName(bugUpPatch.getPatchFileName());
        }
        // 追加云服务器地址
        upPatch.setPatchFileUrl(serverConfig.getUrl() + upPatch.getPatchFileUrl());

        UpProjectProduct projectProduct = projectProductMapper.getBuildTypeWithPatchId(patchId);
        // 查询编译路径列表，传递给端服务器
        UpPatchCodelist codelist = new UpPatchCodelist();
        codelist.setPatchId(upPatch.getPatchId());
        String compiles = "";
        List<UpPatchCodelist> upPatchCodelists = patchCodelistMapper.selectUpPatchCodelistList(codelist);
        if (!CollectionUtils.isEmpty(upPatchCodelists)) {
            List<String> compileList = upPatchCodelists.stream().map(UpPatchCodelist::getCompilePath).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(compileList)) {
                compiles = StringUtils.join(compileList, ",");
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("msg", "success");
        resultMap.put("data", upPatch);
        resultMap.put("compiles", compiles);
        resultMap.put("upgrade", projectProduct.getAutoUpgradeFlag());
        return resultMap;
    }

    /**
     * 合并包
     */
    @PostMapping("/mergeDownlod")
    public Map<String, Object> mergeDownlod(@RequestBody HashMap paramMap) {
        AjaxResult result = new AjaxResult();
        try {
            result = upPatchService.mergeUpPatch(paramMap.get("ids") + "", paramMap.get("updateBy") + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取云服务器子包列表
     * @return
     */
    @PostMapping("/getPatchChildList")
    public Map<String, Object> getPatchChildList(@RequestBody HashMap paramMap) {
        // 获取子包列表
        List<UpPatch> patchList = upPatchService.selectUpPatchListByParentPatchId(Long.valueOf(paramMap.get("patchId") + ""));
        //遍历子包，若为修复任务，则塞入原bug任务的包名
        for (UpPatch upPatch : patchList){
            if (upPatch.getBugfixPatch() != null) {
                UpPatch bugUpPatch = upPatchService.selectUpPatchById(Long.valueOf(upPatch.getBugfixPatch()));
                upPatch.setBugPatchFileName(bugUpPatch.getPatchFileName());
            }
        }
        //获取所有的已发布的修复任务的信息
        UpPatch reupPatch = new UpPatch();
        reupPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PUBLISHED.getValue());
        reupPatch.setBugfixFlag("Y");
        List<UpPatch> reUpPatches = upPatchService.selectUpPatchList(reupPatch);
        //获取所有已发布的有bug的记录
        UpPatch bugUpPatch = new UpPatch();
        bugUpPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PUBLISHED.getValue());
        bugUpPatch.setBugFlag("Y");
        List<UpPatch> upPatchesForOne = upPatchService.selectUpPatchList(bugUpPatch);

        //key:有bug的记录的id  value:修复bug的记录
        Map<String,List<UpPatch>>  oneMap = reUpPatches.stream().filter(s ->s.getBugfixPatch()!=null && !"".equals(s.getBugfixPatch()))
                .collect(Collectors.groupingBy(UpPatch::getBugfixPatch));
        //key:有bug记录的id  value:有bug的记录
        Map<Long,UpPatch>  bugOneMap = upPatchesForOne.stream()
                .collect(Collectors.toMap(UpPatch::getPatchId,a->a));
        Map<String, List<UpPatch>> finalMap = oneMap;
        Map<Long, UpPatch> finalbugOneMap = bugOneMap;
        patchList.forEach(item -> {
            List<UpPatch> bugUpPatches = finalMap.get(""+item.getPatchId());
            String repairName = "";
            if(bugUpPatches != null && bugUpPatches.size()>0){

                for(UpPatch upPatchs : bugUpPatches){
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
                UpPatch upPatch1 = finalbugOneMap.get(Long.parseLong(bugfixPatch));
                if(upPatch1 != null){
                    item.setBugfixName("【"+upPatch1.getPatchFileName()+"】"+upPatch1.getTopic().replaceAll("\r|\n", ""));
                }
            }
        });
        List<Long> patchIds = new ArrayList<>();
        // 查询编译路径列表，传递给端服务器
        if (!CollectionUtils.isEmpty(patchList)) {
            patchIds = patchList.stream().map(UpPatch::getPatchId).collect(Collectors.toList());
        }
        String compiles = "";
        List<UpPatchCodelist> upPatchCodelists = patchCodelistMapper.selectUpPatchCodeListByPatchIds(patchIds);
        if (!CollectionUtils.isEmpty(upPatchCodelists)) {
            List<String> compileList = upPatchCodelists.stream().map(UpPatchCodelist::getCompilePath).collect(Collectors.toList());
            compiles = StringUtils.join(compileList, ",");
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("msg", "success");
        resultMap.put("data", patchList);
        resultMap.put("compiles", compiles);
        return resultMap;
    }

    /**
     * 取消合并
     * @return
     */
    @PostMapping("/cancleMerge")
    public Map<String, Object> cancleMerge(@RequestBody HashMap paramMap) {
        Long patchId = Long.valueOf(paramMap.get("patchId") + "");

        UpPatch upPatch = new UpPatch();
        upPatch.setPatchId(patchId);
        upPatch.setDelFlag("Y");
        upPatch.setUpdateBy(paramMap.get("updateBy") + "");
        upPatch.setUpdateTime(DateUtils.getNowDate());
        upPatchService.updateUpPatchNoSession(upPatch);

        List<UpPatch> patchList = upPatchService.selectUpPatchListByParentPatchId(patchId);
        patchList.forEach(s -> {
            s.setMergePackageFlag("N");
            upPatchService.updateUpPatchNoSession(s);
        });

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("msg", "success");
        resultMap.put("data", patchList);
        return resultMap;
    }

    /**
     * 推送到云端的补丁状态
     */
    @PostMapping("/updateState")
    public Map<String, Object> updateState(@RequestBody HashMap paramMap) {
        String patchFileName = paramMap.get("patchFileName") + "";
        String patchStatus = paramMap.get("patchStatus") + "";
        String mergePackageFlag = paramMap.get("mergePackageFlag") + "";
        Integer projectId = Integer.parseInt(paramMap.get("projectId") + "");
        Map<String, Object> resultMap = new HashMap<>();

        if ("N".equals(mergePackageFlag)) {
            UpPatch upPatch = new UpPatch();
            upPatch.setPatchFileName(patchFileName);
            upPatch.setPatchStatus(patchStatus);
            upPatch.setUpdateTime(new Date());
            upPatch.setProjectId(projectId);
            upPatchService.updateUpPatchByPatchFileName(upPatch);
            log.info("------------------------非合包推送状态：" + patchFileName + "----------------------");
        }

        // 合包
        if ("Y".equals(mergePackageFlag)) {
            List<Long> patchIdList = upPatchMapper.selectSubUpPatchIdListByPatchFileName(patchFileName, projectId);
            log.info("------------------------合包推送状态：" + patchIdList.toString() + "----------------------");
            if (!CollectionUtils.isEmpty(patchIdList)) {
                upPatchMapper.batchUpdatePatch(patchStatus, patchIdList);
            } else {
                resultMap.put("code", 404);
                resultMap.put("msg", "云端没有找到对应的补丁包,推送补丁包状态失败！");
                return resultMap;
            }
        }

        resultMap.put("code", 0);
        resultMap.put("msg", "success");

        return resultMap;
    }

    /**
     * 导入云服务器升级记录
     * @return
     */
    @PostMapping("/importUpgradeRecord")
    public Map<String, Object> importUpgradeRecord(@RequestBody ArrayList<UpUpgradeRecordClient> dataList) {
        Map<String, Object> resultMap = new HashMap<>();
        if (StringUtils.isNull(dataList) || dataList.size() == 0) {
            resultMap.put("code", 500);
            resultMap.put("msg", "导入数据不能为空！");
            return resultMap;
        }

        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        for (UpUpgradeRecordClient upgradeRecordClient : dataList) {
            UpUpgradeRecord upUpgradeRecord = new UpUpgradeRecord();
            upUpgradeRecord.setUpgradeId(upgradeRecordClient.getUpgradeId());
            upUpgradeRecord.setPatchId(upgradeRecordClient.getPatchId());
            upUpgradeRecord.setServerId(upgradeRecordClient.getServerId());
            upUpgradeRecord.setUpStatus(upgradeRecordClient.getUpStatus());
            upUpgradeRecord.setUpTimes(upgradeRecordClient.getUpTimes());
            try {
                // 验证升级记录是否存在
                UpUpgradeRecord u = upUpgradeRecordService.selectUpUpgradeRecordById(upUpgradeRecord.getUpgradeId());
                if (StringUtils.isNull(u)) {
                    upUpgradeRecordService.insertUpUpgradeRecord(upUpgradeRecord);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、升级记录 " + upUpgradeRecord.getUpgradeId() + " 导入成功!");
                } else {
                    upUpgradeRecordService.updateUpUpgradeRecord(upUpgradeRecord);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、升级记录 " + upUpgradeRecord.getUpgradeId() + " 更新成功!");
                }
            }
            catch (Exception e) {
                failureNum++;
                String msg = "<br/>" + failureNum + "、升级记录 " + upUpgradeRecord.getUpgradeId()+ " 导入失败：";
                failureMsg.append(msg + e.getMessage());
            }
        }
        if (failureNum > 0) {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new BusinessException(failureMsg.toString());
        } else {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }

        resultMap.put("code", 0);
        resultMap.put("msg", successMsg.toString());
        return resultMap;
    }

    /**
     * 查询服务器列表
     * @return
     */
    @PostMapping("/upgradeServers")
    public Map<String, Object> getUpgradeServers(@RequestBody HashMap paramMap) {
        String projectName = String.valueOf(paramMap.get("projectName"));
        String productName = String.valueOf(paramMap.get("productName"));
        // 根据patchId查询项目Id和产品Id
        UpPatch upPatch = new UpPatch();
        upPatch.setProductName(productName);
        upPatch.setProjectName(projectName);
        List<UpPatch> upPatchList = upPatchService.selectUpPatchList(upPatch);
        UpProjectServer server = new UpProjectServer();
        if (!CollectionUtils.isEmpty(upPatchList)) {
            server.setProjectId(upPatchList.get(0).getProjectId());
            server.setProductId(upPatchList.get(0).getProductId());
        }
        List<UpProjectServer> upProjectServers = projectServerService.selectUpProjectServerList(server);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("msg", "success");
        resultMap.put("data", upProjectServers);
        return resultMap;
    }

    /**
     * 合包失败重新打包
     * @return
     */
    @PostMapping("/rePatch")
    public Map<String, Object> rePatch(@RequestBody HashMap paramMap) {
        Long patchId = Long.valueOf(paramMap.get("patchId") + "");

        // 修改补丁包状态
        UpPatch upPatch = new UpPatch();
        upPatch.setPatchId(patchId);
        upPatch.setUpdateBy(paramMap.get("updateBy") + "");
        upPatch.setUpdateTime(DateUtils.getNowDate());
        upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PKGING.getValue());
        upPatchService.updateUpPatchNoSession(upPatch);

        UpCommand command = new UpCommand();
        command.setPatchId(patchId);
        command.setDelFlag("N");
        command.setUpdateBy(paramMap.get("updateBy") + "");
        command.setUpdateTime(DateUtils.getNowDate());
        // 修改命令表
        upCommandService.updateUpCommand(command);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("msg", "success");
        return resultMap;
    }

    /**
     * 获取所有的产品和项目
     * @return
     */
    @PostMapping("/getProductAndProject")
    public Map<String, Object> getProductAndProject() {

        Map<Integer, String> productMap = new HashMap<>();
        Map<Integer, String> projectMap = new HashMap<>();

        UpProduct product = new UpProduct();
        product.setDelFlag("N");
        List<UpProduct> upProductList = upProductMapper.selectUpProductList(product);
        if (!CollectionUtils.isEmpty(upProductList)) {
            productMap = upProductList.stream().collect(Collectors.toMap(UpProduct::getProductId, UpProduct::getProductName));
        }

        UpProject project = new UpProject();
        project.setDelFlag("N");
        List<UpProject> projectList = upProjectMapper.selectUpProjectList(project);
        if (!CollectionUtils.isEmpty(projectList)) {
            projectMap = projectList.stream().collect(Collectors.toMap(UpProject::getProjectId, UpProject::getProjectName));
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("msg", "success");
        resultMap.put("productMap", productMap);
        resultMap.put("projectMap", projectMap);

        return resultMap;
    }
}
