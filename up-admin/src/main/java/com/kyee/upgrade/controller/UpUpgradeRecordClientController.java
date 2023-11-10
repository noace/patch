package com.kyee.upgrade.controller;

import com.kyee.feign.IFeignClient;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.kyee.upgrade.domain.vo.UpUpgradeDataDTO;
import com.kyee.upgrade.service.IUpPatchClientService;
import com.kyee.upgrade.service.IUpProjectServerService;
import com.kyee.upgrade.service.IUpUpgradeRecordClientService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.*;

/**
 * 端服务器升级管理Controller
 * 
 * @author zhanghaoyu
 * @date 2021-10-21
 */
@Controller
@RequestMapping("/upgrade/uprecordClient")
public class UpUpgradeRecordClientController extends BaseController
{
    private String prefix = "upgrade/uprecordClient";

    @Autowired
    private IUpUpgradeRecordClientService upUpgradeRecordClientService;

    @Autowired
    private IFeignClient feignClient;

    @Autowired
    private IUpProjectServerService projectServerService;

    @Autowired
    private IUpPatchClientService upPatchClientService;

    @RequiresPermissions("upgrade:uprecordClient:view")
    @GetMapping()
    public String uprecordClient()
    {
        return prefix + "/uprecordClient";
    }

    /**
     * 查询升级管理列表
     */
    @RequiresPermissions("upgrade:uprecordClient:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpProjectServer upProjectServer) throws Exception {
        startPage();
        List<UpProjectServer> list = upUpgradeRecordClientService.selectUpUpgradeRecordClientList(upProjectServer);
        return getDataTable(list);
    }

    /**
     * 导出升级管理列表
     */
    @RequiresPermissions("upgrade:uprecordClient:export")
    @Log(title = "升级管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpUpgradeRecordClient upUpgradeRecordClient)
    {
        List<UpUpgradeRecordClient> list = upUpgradeRecordClientService.selectUpUpgradeRecordClientList(upUpgradeRecordClient);
        ExcelUtil<UpUpgradeRecordClient> util = new ExcelUtil<UpUpgradeRecordClient>(UpUpgradeRecordClient.class);
        return util.exportExcel(list, "升级管理数据");
    }

    /**
     * 新增升级管理
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存升级管理
     */
    @RequiresPermissions("upgrade:uprecordClient:add")
    @Log(title = "升级管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpUpgradeRecordClient upUpgradeRecordClient)
    {
        return toAjax(upUpgradeRecordClientService.insertUpUpgradeRecordClient(upUpgradeRecordClient));
    }

    /**
     * 修改升级管理
     */
    @GetMapping("/edit/{upgradeId}")
    public String edit(@PathVariable("upgradeId") Long upgradeId, ModelMap mmap)
    {
        UpUpgradeRecordClient upUpgradeRecordClient = upUpgradeRecordClientService.selectUpUpgradeRecordClientById(upgradeId);
        mmap.put("upUpgradeRecordClient", upUpgradeRecordClient);
        return prefix + "/edit";
    }

    /**
     * 修改保存升级管理
     */
    @RequiresPermissions("upgrade:uprecordClient:edit")
    @Log(title = "升级管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpUpgradeRecordClient upUpgradeRecordClient)
    {
        return toAjax(upUpgradeRecordClientService.updateUpUpgradeRecordClient(upUpgradeRecordClient));
    }

    /**
     * 删除升级管理
     */
    @RequiresPermissions("upgrade:uprecordClient:remove")
    @Log(title = "升级管理", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upUpgradeRecordClientService.deleteUpUpgradeRecordClientByIds(ids));
    }

    /**
     * 下载模板
     */
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate()
    {
        ExcelUtil<UpUpgradeRecordClient> util = new ExcelUtil<UpUpgradeRecordClient>(UpUpgradeRecordClient.class);
        return util.importTemplateExcel("升级记录");
    }

    /**
     * 导入数据
     */
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<UpUpgradeRecordClient> util = new ExcelUtil<UpUpgradeRecordClient>(UpUpgradeRecordClient.class);
        ArrayList<UpUpgradeRecordClient> dataList = (ArrayList<UpUpgradeRecordClient>) util.importExcel(file.getInputStream());
        // 调用云服务器发布补丁列表接口
        Map<String, Object> resultMap = feignClient.importUpgradeRecord(dataList);

        if(resultMap.get("code").equals("200")) {
            return AjaxResult.success(resultMap.get("msg"));
        }else {
            return AjaxResult.error((String) resultMap.get("msg"));
        }
    }

    /**
     * 跳转补丁包管理页面
     */
    @GetMapping("/upgrade/{serverId}/{productId}/{projectId}")
    public String sqlClient(@PathVariable("serverId") String serverId,@PathVariable("productId") String productId,
                            @PathVariable("projectId") String projectId, ModelMap map) {
        UpProjectServer server = projectServerService.selectUpProjectServerById(Integer.parseInt(serverId));
        map.put("serverId", serverId);
        map.put("serverType", server.getServerType());
        map.put("projectId", projectId);
        map.put("productId", productId);
        return prefix + "/upgrade";
    }

    /**
     * 查询补丁包升级包列表
     */
    @PostMapping("/upgrade/list")
    @ResponseBody
    public TableDataInfo upgradePatchList(UpUpgradeDataDTO dataVo) {
        List<UpPatchClientExtend> upgradePatchList;
        if ("N".equals(dataVo.getUpgradeRecordFlag())) {
            upgradePatchList = upUpgradeRecordClientService.getUpgradePatchList(dataVo);
        } else {
            startPage();
            upgradePatchList = upUpgradeRecordClientService.getHistoryUpgradePatchList(dataVo);
        }

        if (CollectionUtils.isNotEmpty(upgradePatchList)) {
            upgradePatchList.get(0).setFirstPatchFlag(true);
        }
        return getDataTable(upgradePatchList);
    }

    /**
     * 停止服务
     */
    @PostMapping("/stopServer")
    @ResponseBody
    public AjaxResult stopServer(Integer serverId) {

        return upUpgradeRecordClientService.stopServer(serverId);
    }

    /**
     *启动服务
     */
    @PostMapping("/startServer")
    @ResponseBody
    public AjaxResult startServer(Integer serverId, String projectId, String productId) {

        AjaxResult result;
        try {
            result = upUpgradeRecordClientService.startServer(serverId, Integer.valueOf(projectId), Integer.valueOf(productId));
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return result;
    }

    /**
     *  补丁包升级
     *
     * @param patchId 补丁包
     */
    @PostMapping("/sendPatch")
    @ResponseBody
    public AjaxResult sendPatch(Long patchId, Integer serverId, String serverType) {

        return upUpgradeRecordClientService.sendPatch(patchId, serverId, serverType);
    }

    /**
     * 获取升级日志
     */
    @GetMapping("/getServerLog/{patchId}/{serverId}")
    public String getBuildLog(@PathVariable("patchId") Long patchId, @PathVariable("serverId") Integer serverId, ModelMap mmap) {
        List<UpUpgradeLogClient> serverLogList = upUpgradeRecordClientService.getServerLogs(patchId, serverId);
        mmap.put("patchId", patchId);
        mmap.put("serverId", serverId);
        mmap.put("logs", serverLogList);
        return prefix + "/serverLog";
    }

    /** 升级回退
     * @param patchId 补丁包Id
     * @return serverId 服务Id
     */
    @PostMapping("/rollbackPatch")
    @ResponseBody
    public AjaxResult rollbackPatch(Long patchId, Integer serverId) {

        return upUpgradeRecordClientService.rollbackPatch(patchId, serverId);
    }

    /**
     * 手动升级
     */
    @PostMapping("/handUpgrade")
    @ResponseBody
    public AjaxResult handUpgrade(Long patchId, Integer serverId) {

        return upUpgradeRecordClientService.handUpgrade(patchId, serverId);
    }

    /**
     * 弹出合并子包列表页面
     */
    @GetMapping("/jiraList/{client}/{patchId}")
    public String jiraList(@PathVariable("client") boolean client, @PathVariable("patchId") Long patchId, ModelMap mmap) {
        mmap.put("client", client);
        mmap.put("patchId", patchId);
        return prefix + "/jiraList";
    }

    /**
     * 查询子包列表
     */
    @PostMapping("/jiraList")
    @ResponseBody
    public TableDataInfo jiraList(boolean client, Long patchId) {
        List<UpPatchClient> patchList = null;
        if (client) {
            // 获取本地子包列表
            patchList = upPatchClientService.selectUpPatchListByParentPatchId(patchId);
        } else {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("patchId", patchId);

            // 调用云服务器子包列表接口
            Map<String, Object> patchFileMap = feignClient.getPatchChildList(paramMap);
            if(!"0".equals(patchFileMap.get("code")+"")){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(1);
                rspData.setMsg(patchFileMap.get("msg")+"");
                return rspData;
            }

            patchList = (List<UpPatchClient>) patchFileMap.get("data");
        }

        return getDataTable(patchList);
    }


    /**
     *  获取服务器状态
     * @param serverId
     */
    @PostMapping("/getServerStatus")
    @ResponseBody
    public String getServerStatus(Integer serverId) {

        UpProjectServer server = upUpgradeRecordClientService.getServerStatus(serverId);
        if (Objects.isNull(server)) {
            return "";
        }
        return server.getServerStatus();
    }

    /**
     * 升级时校验重复文件
     */
    @PostMapping( "/validateUpgrade")
    @ResponseBody
    public String validateUpgrade(Long patchId, String serverType, Integer serverId)
    {
        return upUpgradeRecordClientService.validateUpgrade(patchId, serverType, serverId);
    }
}
