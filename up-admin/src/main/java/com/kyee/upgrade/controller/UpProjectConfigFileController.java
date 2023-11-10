package com.kyee.upgrade.controller;

import java.util.List;

import com.github.pagehelper.PageHelper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.kyee.upgrade.domain.UpProjectConfigFile;
import com.kyee.upgrade.service.IUpProjectConfigFileService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目产品Controller
 * 
 * @author lijunqiang
 * @date 2022-06-25
 */
@Controller
@RequestMapping("/upgrade/projectConfigFile")
public class UpProjectConfigFileController extends BaseController
{
    private String prefix = "upgrade/projectConfigFile";

    @Autowired
    private IUpProjectConfigFileService upProjectConfigFileService;

    @RequiresPermissions("upgrade:projectConfigFile:view")
    @GetMapping()
    public String file()
    {
        return prefix + "/projectConfigFile";
    }

    /**
     * 查询项目产品列表
     */
    @RequiresPermissions("upgrade:projectConfigFile:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpProjectConfigFile upProjectConfigFile)
    {
        startPage();
        List<UpProjectConfigFile> list = upProjectConfigFileService.selectUpProjectConfigFileList(upProjectConfigFile);
        return getDataTable(list);
    }

    /**
     * 导出项目产品列表
     */
    @RequiresPermissions("upgrade:projectConfigFile:export")
    @Log(title = "项目产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpProjectConfigFile upProjectConfigFile)
    {
        List<UpProjectConfigFile> list = upProjectConfigFileService.selectUpProjectConfigFileList(upProjectConfigFile);
        ExcelUtil<UpProjectConfigFile> util = new ExcelUtil<UpProjectConfigFile>(UpProjectConfigFile.class);
        return util.exportExcel(list, "项目产品数据");
    }

    /**
     * 新增项目产品
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存项目产品
     */
    @RequiresPermissions("upgrade:projectConfigFile:add")
    @Log(title = "项目产品", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpProjectConfigFile upProjectConfigFile)
    {
        int i;
        try {
            i = upProjectConfigFileService.insertUpProjectConfigFile(upProjectConfigFile);
        } catch (Exception e) {
            return AjaxResult.error("该项目产品中配置文件已存在，请重新选择");
        }
        return toAjax(i);
    }

    /**
     * 修改项目产品
     */
    @GetMapping("/edit/{projectProductId}")
    public String edit(@PathVariable("projectProductId") Integer projectProductId, ModelMap mmap)
    {
        UpProjectConfigFile upProjectConfigFile = upProjectConfigFileService.selectUpProjectConfigFileById(projectProductId);
        mmap.put("upProjectConfigFile", upProjectConfigFile);
        return prefix + "/edit";
    }

    /**
     * 修改保存项目产品
     */
    @RequiresPermissions("upgrade:projectConfigFile:edit")
    @Log(title = "项目产品", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpProjectConfigFile upProjectConfigFile)
    {
        int i;
        try {
            i = upProjectConfigFileService.updateUpProjectConfigFile(upProjectConfigFile);
        } catch (Exception e) {
            return AjaxResult.error("该项目产品中配置文件已存在，请重新选择");
        }
        return toAjax(i);
    }

    /**
     * 删除项目产品
     */
    @RequiresPermissions("upgrade:projectConfigFile:remove")
    @Log(title = "项目产品", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upProjectConfigFileService.deleteUpProjectConfigFileByIds(ids));
    }
}
