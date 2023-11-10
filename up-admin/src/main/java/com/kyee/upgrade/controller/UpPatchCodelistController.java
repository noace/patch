package com.kyee.upgrade.controller;

import com.kyee.upgrade.domain.UpPatchCodelist;
import com.kyee.upgrade.service.IUpPatchCodelistService;
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
 * 补丁包代码列Controller
 * 
 * @author lijunqiang
 * @date 2021-09-23
 */
@Controller
@RequestMapping("/upgrade/codelist")
public class UpPatchCodelistController extends BaseController
{
    private String prefix = "upgrade/codelist";

    @Autowired
    private IUpPatchCodelistService upPatchCodelistService;

    @RequiresPermissions("upgrade:codelist:view")
    @GetMapping()
    public String codelist()
    {
        return prefix + "/codelist";
    }

    /**
     * 查询补丁包代码列列表
     */
    @RequiresPermissions("upgrade:codelist:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpPatchCodelist upPatchCodelist)
    {
        startPage();
        List<UpPatchCodelist> list = upPatchCodelistService.selectUpPatchCodelistList(upPatchCodelist);
        return getDataTable(list);
    }

    /**
     * 导出补丁包代码列列表
     */
    @RequiresPermissions("upgrade:codelist:export")
    @Log(title = "补丁包代码列", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpPatchCodelist upPatchCodelist)
    {
        List<UpPatchCodelist> list = upPatchCodelistService.selectUpPatchCodelistList(upPatchCodelist);
        ExcelUtil<UpPatchCodelist> util = new ExcelUtil<UpPatchCodelist>(UpPatchCodelist.class);
        return util.exportExcel(list, "补丁包代码列数据");
    }

    /**
     * 新增补丁包代码列
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存补丁包代码列
     */
    @RequiresPermissions("upgrade:codelist:add")
    @Log(title = "补丁包代码列", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpPatchCodelist upPatchCodelist)
    {
        return toAjax(upPatchCodelistService.insertUpPatchCodelist(upPatchCodelist));
    }

    /**
     * 修改补丁包代码列
     */
    @GetMapping("/edit/{codeId}")
    public String edit(@PathVariable("codeId") Long codeId, ModelMap mmap)
    {
        UpPatchCodelist upPatchCodelist = upPatchCodelistService.selectUpPatchCodelistById(codeId);
        mmap.put("upPatchCodelist", upPatchCodelist);
        return prefix + "/edit";
    }

    /**
     * 修改保存补丁包代码列
     */
    @RequiresPermissions("upgrade:codelist:edit")
    @Log(title = "补丁包代码列", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpPatchCodelist upPatchCodelist)
    {
        return toAjax(upPatchCodelistService.updateUpPatchCodelist(upPatchCodelist));
    }

    /**
     * 删除补丁包代码列
     */
    @RequiresPermissions("upgrade:codelist:remove")
    @Log(title = "补丁包代码列", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upPatchCodelistService.deleteUpPatchCodelistByIds(ids));
    }
}
