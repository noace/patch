package com.kyee.upgrade.controller;

import com.alibaba.fastjson.JSON;
import com.kyee.upgrade.domain.UpProjectModule;
import com.kyee.upgrade.service.IUpProductService;
import com.kyee.upgrade.service.IUpProjectModuleService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.CxSelect;
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
 * 模块名Controller
 * 
 * @author lijunqiang
 * @date 2022-06-25
 */
@Controller
@RequestMapping("/upgrade/projectModule")
public class UpProjectModuleController extends BaseController
{
    private String prefix = "upgrade/projectModule";

    @Autowired
    private IUpProjectModuleService upProjectModuleService;

    @Autowired
    private IUpProductService upProductService;

    @RequiresPermissions("upgrade:projectModule:view")
    @GetMapping()
    public String file(ModelMap mmap)
    {
        // 获取产品项目级联列表
        List<CxSelect> productList = upProductService.selectProjectAndProduct(null);
        mmap.put("projectAndProduct", JSON.toJSON(productList));
        return prefix + "/projectModule";
    }

    /**
     * 查询模块名列表
     */
    @RequiresPermissions("upgrade:projectModule:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpProjectModule upProjectModule)
    {
        startPage();
        List<UpProjectModule> list = upProjectModuleService.selectUpProjectModuleList(upProjectModule);
        return getDataTable(list);
    }

    /**
     * 新增模块名
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存模块名
     */
    @RequiresPermissions("upgrade:projectModule:add")
    @Log(title = "模块名", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpProjectModule upProjectModule)
    {
        int i;
        try {
            i = upProjectModuleService.insertUpProjectModule(upProjectModule);
        } catch (Exception e) {
            return AjaxResult.error("新增模块名出错");
        }
        return toAjax(i);
    }

    /**
     * 修改模块名
     */
    @GetMapping("/edit/{projectProductId}")
    public String edit(@PathVariable("projectProductId") Integer projectProductId, ModelMap mmap)
    {
        UpProjectModule upProjectModule = upProjectModuleService.selectUpProjectModuleById(projectProductId);
        mmap.put("upProjectModule", upProjectModule);
        return prefix + "/edit";
    }

    /**
     * 修改保存模块名
     */
    @RequiresPermissions("upgrade:projectModule:edit")
    @Log(title = "模块名", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpProjectModule upProjectModule)
    {
        int i;
        try {
            i = upProjectModuleService.updateUpProjectModule(upProjectModule);
        } catch (Exception e) {
            return AjaxResult.error("修改模块名出错");
        }
        return toAjax(i);
    }

    /**
     * 删除模块名
     */
    @RequiresPermissions("upgrade:projectModule:remove")
    @Log(title = "模块名", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upProjectModuleService.deleteUpProjectModuleByIds(ids));
    }
}
