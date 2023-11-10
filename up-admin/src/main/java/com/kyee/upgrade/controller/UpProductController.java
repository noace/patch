package com.kyee.upgrade.controller;

import com.kyee.upgrade.domain.UpProduct;
import com.kyee.upgrade.service.IUpProductService;
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
 * 产品Controller
 * 
 * @author lijunqiang
 * @date 2021-06-10
 */
@Controller
@RequestMapping("/upgrade/product")
public class UpProductController extends BaseController
{
    private String prefix = "upgrade/product";

    @Autowired
    private IUpProductService upProductService;

    @RequiresPermissions("upgrade:product:view")
    @GetMapping()
    public String product()
    {
        return prefix + "/product";
    }

    /**
     * 查询产品列表
     */
    @RequiresPermissions("upgrade:product:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpProduct upProduct)
    {
        startPage();
        List<UpProduct> list = upProductService.selectUpProductList(upProduct);
        return getDataTable(list);
    }

    /**
     * 导出产品列表
     */
    @RequiresPermissions("upgrade:product:export")
    @Log(title = "产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpProduct upProduct)
    {
        List<UpProduct> list = upProductService.selectUpProductList(upProduct);
        ExcelUtil<UpProduct> util = new ExcelUtil<UpProduct>(UpProduct.class);
        return util.exportExcel(list, "产品数据");
    }

    /**
     * 新增产品
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存产品
     */
    @RequiresPermissions("upgrade:product:add")
    @Log(title = "产品", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpProduct upProduct)
    {
        return toAjax(upProductService.insertUpProduct(upProduct));
    }

    /**
     * 修改产品
     */
    @GetMapping("/edit/{productId}")
    public String edit(@PathVariable("productId") Integer productId, ModelMap mmap)
    {
        UpProduct upProduct = upProductService.selectUpProductById(productId);
        mmap.put("upProduct", upProduct);
        return prefix + "/edit";
    }

    /**
     * 修改保存产品
     */
    @RequiresPermissions("upgrade:product:edit")
    @Log(title = "产品", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpProduct upProduct)
    {
        return toAjax(upProductService.updateUpProduct(upProduct));
    }

    /**
     * 删除产品
     */
    @RequiresPermissions("upgrade:product:remove")
    @Log(title = "产品", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upProductService.deleteUpProductByIds(ids));
    }
}
