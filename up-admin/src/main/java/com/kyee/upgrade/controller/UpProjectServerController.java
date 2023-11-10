package com.kyee.upgrade.controller;


import com.alibaba.fastjson.JSONObject;
import com.kyee.feign.IFeignClient;
import com.kyee.upgrade.domain.UpPatchClient;
import com.kyee.upgrade.domain.UpProjectServer;
import com.kyee.upgrade.domain.UpUpgradeLogClient;
import com.kyee.upgrade.service.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 项目应用服务Controller
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
@Controller
@RequestMapping("/upgrade/projectServer")
public class UpProjectServerController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(UpProjectServerController.class);

    private String prefix = "upgrade/projectServer";

    @Autowired
    private IUpProjectServerService upProjectServerService;

    @Autowired
    private IFeignClient feignClient;

    @RequiresPermissions("upgrade:projectServer:view")
    @GetMapping()
    public String projectServer()
    {
        return prefix + "/projectServer";
    }

    /**
     * 查询项目应用服务列表
     */
    @RequiresPermissions("upgrade:projectServer:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpProjectServer upProjectServer)
    {
        startPage();
        List<UpProjectServer> list = upProjectServerService.selectUpProjectServerList(upProjectServer);
        // 回显产品和项目名
        Map<String, Object> resultMap = feignClient.getProductAndProject();
        Map<Object, String> productMap = JSONObject.parseObject(JSONObject.toJSONString(resultMap.get("productMap")), Map.class);
        Map<Object, String> projectMap = JSONObject.parseObject(JSONObject.toJSONString(resultMap.get("projectMap")), Map.class);
        // 查询所有的产品名和项目名
        for (UpProjectServer server : list) {
            String productName = productMap.get(Integer.toString(server.getProductId()));
            server.setProductName(productName);
            String projectName = projectMap.get(Integer.toString(server.getProjectId()));
            server.setProjectName(projectName);
        }
        return getDataTable(list);
    }

    /**
     * 导出项目应用服务列表
     */
    @RequiresPermissions("upgrade:projectServer:export")
    @Log(title = "项目应用服务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpProjectServer upProjectServer)
    {
        List<UpProjectServer> list = upProjectServerService.selectUpProjectServerList(upProjectServer);
        ExcelUtil<UpProjectServer> util = new ExcelUtil<UpProjectServer>(UpProjectServer.class);
        return util.exportExcel(list, "项目应用服务数据");
    }

    /**
     * 新增项目应用服务
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存项目应用服务
     */
    @RequiresPermissions("upgrade:projectServer:add")
    @Log(title = "项目应用服务", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpProjectServer upProjectServer)
    {
        return toAjax(upProjectServerService.insertUpProjectServer(upProjectServer));
    }

    /**
     * 修改项目应用服务
     */
    @GetMapping("/edit/{serverId}")
    public String edit(@PathVariable("serverId") Integer serverId, ModelMap mmap)
    {
        UpProjectServer upProjectServer = upProjectServerService.selectUpProjectServerById(serverId);
        mmap.put("upProjectServer", upProjectServer);
        return prefix + "/edit";
    }

    /**
     * 修改保存项目应用服务
     */
    @RequiresPermissions("upgrade:projectServer:edit")
    @Log(title = "项目应用服务", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpProjectServer upProjectServer)
    {
        return toAjax(upProjectServerService.updateUpProjectServer(upProjectServer));
    }

    /**
     * 删除项目应用服务
     */
    @RequiresPermissions("upgrade:projectServer:remove")
    @Log(title = "项目应用服务", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upProjectServerService.deleteUpProjectServerByIds(ids));
    }

    /**
     * 查询服务器列表
     */
    @PostMapping("/upgradeServers")
    @ResponseBody
    public TableDataInfo getUpgradeServers(Long patchId,UpProjectServer upProjectServer)  {
        return getDataTable(upProjectServerService.getUpgradeServers(patchId,upProjectServer));
    }

    /**
     * 停止服务
     */
    @PostMapping("/stopServer")
    @ResponseBody
    public AjaxResult stopServer(Long patchId, Integer serverId) {

        return upProjectServerService.stopServer(patchId, serverId);
    }

    /**
     *启动服务
     */
    @PostMapping("/startServer")
    @ResponseBody
    public AjaxResult startServer(Long patchId, Integer serverId) {

        return upProjectServerService.startServer(patchId, serverId);
    }

    /**
     *  补丁包升级
     *
     * @param patchId 补丁包
     */
    @PostMapping("/sendPatch")
    @ResponseBody
    public AjaxResult sendPatch(Long patchId, Integer serverId) {

        return upProjectServerService.sendPatch(patchId, serverId);
    }

    /**
     * 获取升级日志
     */
//    @RequiresPermissions("upgrade:patch:getBuildLog")
    @GetMapping("/getServerLog/{patchId}/{serverId}")
    public String getBuildLog(@PathVariable("patchId") Long patchId, @PathVariable("serverId") Integer serverId, ModelMap mmap) {
        List<UpUpgradeLogClient> serverLogList = upProjectServerService.getServerLogs(patchId, serverId);
        mmap.put("patchId", patchId);
        mmap.put("serverId", serverId);
        mmap.put("logs", serverLogList);
        return prefix + "/serverLog";
    }

    /**
     * 查询最新升级日志列表
     */
    @PostMapping("/getServerLog")
    @ResponseBody
    public List<UpUpgradeLogClient> getBuildLog(Long patchId, Integer serverId) {
        List<UpUpgradeLogClient> buildLogList = upProjectServerService.getServerLogs(patchId, serverId);
        return buildLogList;
    }

    /** 升级回退
     * @param patchId 补丁包Id
     * @return serverId 服务Id
     */
    @PostMapping("/rollbackPatch")
    @ResponseBody
    public AjaxResult rollbackPatch(Long patchId, Integer serverId) {

        return upProjectServerService.rollbackPatch(patchId, serverId);
    }

    /**
     * 手动升级
     */
    @PostMapping("/handUpgrade")
    @ResponseBody
    public AjaxResult handUpgrade(Long patchId, Integer serverId) {

        return upProjectServerService.handUpgrade(patchId, serverId);
    }
}
