package com.kyee.upgrade.controller;

import com.kyee.upgrade.domain.UpCommand;
import com.kyee.upgrade.service.IUpCommandService;
import com.ruoyi.common.annotation.Log;
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
 * 待执行命令Controller
 * 
 * @author lijunqiang
 * @date 2021-10-18
 */
@Controller
@RequestMapping("/upgrade/command")
public class UpCommandController extends BaseController
{
    private String prefix = "upgrade/command";

    @Autowired
    private IUpCommandService upCommandService;

    @RequiresPermissions("upgrade:command:view")
    @GetMapping()
    public String command()
    {
        return prefix + "/command";
    }

    /**
     * 查询待执行命令列表
     */
    @RequiresPermissions("upgrade:command:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpCommand upCommand)
    {
        startPage();
        List<UpCommand> list = upCommandService.selectUpCommandList(upCommand);
        return getDataTable(list);
    }

    /**
     * 导出待执行命令列表
     */
    @RequiresPermissions("upgrade:command:export")
    @Log(title = "待执行命令", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpCommand upCommand)
    {
        List<UpCommand> list = upCommandService.selectUpCommandList(upCommand);
        ExcelUtil<UpCommand> util = new ExcelUtil<UpCommand>(UpCommand.class);
        return util.exportExcel(list, "待执行命令数据");
    }

    /**
     * 新增待执行命令
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存待执行命令
     */
    @RequiresPermissions("upgrade:command:add")
    @Log(title = "待执行命令", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpCommand upCommand)
    {
        return toAjax(upCommandService.insertUpCommand(upCommand));
    }

    /**
     * 修改待执行命令
     */
    @GetMapping("/edit/{commandId}")
    public String edit(@PathVariable("commandId") Long commandId, ModelMap mmap)
    {
        UpCommand upCommand = upCommandService.selectUpCommandById(commandId);
        mmap.put("upCommand", upCommand);
        return prefix + "/edit";
    }

    /**
     * 修改保存待执行命令
     */
    @RequiresPermissions("upgrade:command:edit")
    @Log(title = "待执行命令", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpCommand upCommand)
    {
        return toAjax(upCommandService.updateUpCommand(upCommand));
    }

    /**
     * 删除待执行命令
     */
    @RequiresPermissions("upgrade:command:remove")
    @Log(title = "待执行命令", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upCommandService.deleteUpCommandByIds(ids));
    }
}
