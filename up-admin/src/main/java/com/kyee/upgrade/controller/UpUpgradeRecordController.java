package com.kyee.upgrade.controller;

import com.kyee.upgrade.domain.UpUpgradeRecord;
import com.kyee.upgrade.service.IUpUpgradeRecordService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 云服务器升级管理Controller
 * 
 * @author lijunqiang
 * @date 2021-06-13
 */
@Controller
@RequestMapping("/upgrade/uprecord")
public class UpUpgradeRecordController extends BaseController
{
    private String prefix = "upgrade/uprecord";

    @Autowired
    private IUpUpgradeRecordService upUpgradeRecordService;

    @RequiresPermissions("upgrade:uprecord:view")
    @GetMapping()
    public String uprecord(ModelMap mmap)
    {
        mmap.put("upgradeEnabled", RuoYiConfig.isUpgradeEnabled());
        return prefix + "/uprecord";
    }

    /**
     * 查询升级管理列表
     */
    @RequiresPermissions("upgrade:uprecord:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpUpgradeRecord upUpgradeRecord)
    {
        startPage();
        List<UpUpgradeRecord> list = upUpgradeRecordService.selectUpUpgradeRecordList(upUpgradeRecord);
        return getDataTable(list);
    }

    /**
     * 导出升级管理列表
     */
    @RequiresPermissions("upgrade:uprecord:export")
    @Log(title = "升级管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpUpgradeRecord upUpgradeRecord)
    {
        List<UpUpgradeRecord> list = upUpgradeRecordService.selectUpUpgradeRecordList(upUpgradeRecord);
        ExcelUtil<UpUpgradeRecord> util = new ExcelUtil<UpUpgradeRecord>(UpUpgradeRecord.class);
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
    @RequiresPermissions("upgrade:uprecord:add")
    @Log(title = "升级管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpUpgradeRecord upUpgradeRecord)
    {
        return toAjax(upUpgradeRecordService.insertUpUpgradeRecord(upUpgradeRecord));
    }

    /**
     * 修改升级管理
     */
    @GetMapping("/edit/{upgradeId}")
    public String edit(@PathVariable("upgradeId") Long upgradeId, ModelMap mmap)
    {
        UpUpgradeRecord upUpgradeRecord = upUpgradeRecordService.selectUpUpgradeRecordById(upgradeId);
        mmap.put("upUpgradeRecord", upUpgradeRecord);
        return prefix + "/edit";
    }

    /**
     * 修改保存升级管理
     */
    @RequiresPermissions("upgrade:uprecord:edit")
    @Log(title = "升级管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpUpgradeRecord upUpgradeRecord)
    {
        return toAjax(upUpgradeRecordService.updateUpUpgradeRecord(upUpgradeRecord));
    }

    /**
     * 删除升级管理
     */
    @RequiresPermissions("upgrade:uprecord:remove")
    @Log(title = "升级管理", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upUpgradeRecordService.deleteUpUpgradeRecordByIds(ids));
    }
}
