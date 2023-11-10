package com.kyee.upgrade.controller;

import java.util.List;
import com.alibaba.fastjson.JSON;
import com.kyee.upgrade.service.IUpProductService;
import com.ruoyi.common.core.domain.CxSelect;
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
import com.kyee.upgrade.domain.UpPatchSql;
import com.kyee.upgrade.service.IUpPatchSqlService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 执行SQLController
 * 
 * @author lijunqiang
 * @date 2022-10-20
 */
@Controller
@RequestMapping("/upgrade/patchSql")
public class UpPatchSqlController extends BaseController
{
    private String prefix = "upgrade/patchSql";

    @Autowired
    private IUpPatchSqlService upPatchSqlService;

    @Autowired
    private IUpProductService upProductService;

    @RequiresPermissions("upgrade:patchSql:view")
    @GetMapping()
    public String sql(ModelMap mmap)
    {
        // 获取产品项目级联列表
        List<CxSelect> productList = upProductService.selectProjectAndProduct(null);
        mmap.put("projectAndProduct", JSON.toJSON(productList));
        return prefix + "/sql";
    }

    /**
     * 查询执行SQL列表
     */
    @RequiresPermissions("upgrade:patchSql:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpPatchSql upPatchSql)
    {
        Integer pageNum = startPage();
        List<UpPatchSql> list = upPatchSqlService.selectUpPatchSqlList(upPatchSql);
        return getDataTable(list);
    }

    /**
     * 导出执行SQL列表
     */
    @RequiresPermissions("upgrade:patchSql:export")
    @Log(title = "执行SQL", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpPatchSql upPatchSql)
    {
        List<UpPatchSql> list = upPatchSqlService.selectUpPatchSqlList(upPatchSql);
        ExcelUtil<UpPatchSql> util = new ExcelUtil<UpPatchSql>(UpPatchSql.class);
        return util.exportExcel(list, "执行SQL数据");
    }

    /**
     * 新增执行SQL
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存执行SQL
     */
    @RequiresPermissions("upgrade:patchSql:add")
    @Log(title = "执行SQL", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpPatchSql upPatchSql)
    {
        return toAjax(upPatchSqlService.insertUpPatchSql(upPatchSql));
    }

    /**
     * 修改执行SQL
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        UpPatchSql upPatchSql = upPatchSqlService.selectUpPatchSqlById(id);
        mmap.put("upPatchSql", upPatchSql);
        return prefix + "/edit";
    }

    /**
     * 修改保存执行SQL
     */
    @RequiresPermissions("upgrade:patchSql:edit")
    @Log(title = "执行SQL", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpPatchSql upPatchSql)
    {
        return toAjax(upPatchSqlService.updateUpPatchSql(upPatchSql));
    }

    /**
     * 删除执行SQL
     */
    @RequiresPermissions("upgrade:patchSql:remove")
    @Log(title = "执行SQL", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upPatchSqlService.deleteUpPatchSqlByIds(ids));
    }

    /**
     * 批量下载SQL
     */
    @RequiresPermissions("upgrade:patchSql:download")
    @Log(title = "批量下载SQL", businessType = BusinessType.DOWNLOAD)
    @PostMapping( "/batchDownload")
    @ResponseBody
    public String batchDownload(String ids) throws Exception {
        return upPatchSqlService.batchSelectSqlList(ids);
    }
}
