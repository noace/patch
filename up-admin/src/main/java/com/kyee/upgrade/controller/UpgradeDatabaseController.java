package com.kyee.upgrade.controller;

import com.kyee.upgrade.domain.UpPatchClient;
import com.kyee.upgrade.domain.UpProjectServer;
import com.kyee.upgrade.domain.UpUpgradeLogClient;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.kyee.upgrade.domain.vo.UpUpgradeDataDTO;
import com.kyee.upgrade.domain.vo.UpUpgradePatchDTO;
import com.kyee.upgrade.service.IUpPatchClientService;
import com.kyee.upgrade.service.IUpProjectServerService;
import com.kyee.upgrade.service.impl.SqlPatchListService;
import com.kyee.upgrade.service.impl.UpProjectServerServiceImpl;
import com.kyee.upgrade.service.impl.UpgradeDatabaseService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * 数据库管理列表
 */
@Controller
@RequestMapping("/upgrade/database")
public class UpgradeDatabaseController extends BaseController {

    private String prefix = "upgrade/database";

    @Autowired
    private UpProjectServerServiceImpl serverService;

    @Autowired
    private SqlPatchListService sqlPatchListService;

    @Autowired
    private UpgradeDatabaseService databaseService;

    @Autowired
    private IUpProjectServerService projectServerService;

    @Autowired
    private IUpPatchClientService patchClientService;
    /**
     * 跳转数据库管理页面
     */
    @RequiresPermissions("upgrade:database:view")
    @GetMapping()
    public String executeSQL(ModelMap mmap) {
        return prefix + "/database";
    }

    /**
     * 查询数据库列表
     */
    @RequiresPermissions("upgrade:database:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpProjectServer server) throws ParseException {

        List<UpUpgradeDataDTO> databaseList = serverService.getDatabaseList(server);
        return getDataTable(databaseList);
    }

    /**
     * 跳转补丁包SQL管理页面
     */
    @GetMapping("/executeSql/{serverId}/{productId}/{projectId}")
    public String sqlClient(@PathVariable("serverId") String serverId,@PathVariable("productId") String productId,
                            @PathVariable("projectId") String projectId, ModelMap map) {
        UpProjectServer server = projectServerService.selectUpProjectServerById(Integer.parseInt(serverId));
        map.put("serverId", serverId);
        map.put("projectId", projectId);
        map.put("productId", productId);
        map.put("serverType", server.getServerType());
        return prefix + "/sqlClient";
    }


    /**
     * 查询数据库列表
     */
    @PostMapping("/sqlPatch/list")
    @ResponseBody
    public TableDataInfo sqlPatchList(UpUpgradePatchDTO patchDTO) {
        List<UpPatchClientExtend> patchClientList;
        if ("N".equals(patchDTO.getUpgradeRecordFlag())) {
            patchClientList = sqlPatchListService.getUpgradeSqlList(patchDTO);
        } else {
            startPage();
            patchClientList = sqlPatchListService.getHistoryUpgradeSqlList(patchDTO);
        }
        if (!CollectionUtils.isEmpty(patchClientList)) {
            patchClientList.get(0).setFirstPatchFlag(true);
        }
        return getDataTable(patchClientList);
    }

    /**
     * 执行数据库
     */
    @PostMapping("/execute")
    @ResponseBody
    public AjaxResult execute(Long patchId, Integer serverId) {

        // 执行数据库
        try {
            databaseService.executeSQL(patchId, serverId);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.success("执行成功！");
    }

    /**
     * 获取升级日志
     * @param patchId
     * @param serverId
     * @param mmap
     * @return
     */
    @GetMapping("/getExecuteLog/{patchId}/{serverId}")
    public String getBuildLog(@PathVariable("patchId") Long patchId, @PathVariable("serverId") String serverId, ModelMap mmap) {
        List<UpUpgradeLogClient> serverLogList = databaseService.getServerLogs(patchId, Integer.valueOf(serverId));
        mmap.put("patchId", patchId);
        mmap.put("serverId", serverId);
        mmap.put("logs", serverLogList);
        return prefix + "/serverLog";
    }

    /**
     * 手动执行
     */
    @PostMapping("/handExecute")
    @ResponseBody
    public AjaxResult handExecute(Integer serverId, Long patchId) {

        return databaseService.handExecute(serverId, patchId);
    }

    /**
     * 弹出合并子包列表页面
     */
    @GetMapping("/jiraList/{patchId}")
    public String jiraList(@PathVariable("patchId") Long patchId, ModelMap mmap) {
        mmap.put("patchId", patchId);
        return prefix + "/jiraList";
    }

    /**
     * 查询子包列表
     */
    @PostMapping("/jiraList")
    @ResponseBody
    public TableDataInfo jiraList(Long patchId) {
        List<UpPatchClient> patchList = patchClientService.selectUpPatchListByParentPatchId(patchId);
        return getDataTable(patchList);
    }
}
