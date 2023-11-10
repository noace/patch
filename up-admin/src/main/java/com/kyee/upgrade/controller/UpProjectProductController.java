package com.kyee.upgrade.controller;

import com.alibaba.fastjson.JSON;
import com.kyee.upgrade.domain.UpProject;
import com.kyee.upgrade.domain.UpProjectPerson;
import com.kyee.upgrade.domain.UpProjectProduct;
import com.kyee.upgrade.mapper.UpProjectMapper;
import com.kyee.upgrade.mapper.UpProjectProductMapper;
import com.kyee.upgrade.service.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.CxSelect;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目产品Controller
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
@Controller
@RequestMapping("/upgrade/projectProduct")
public class UpProjectProductController extends BaseController
{
    private String prefix = "upgrade/projectProduct";

    @Autowired
    private IUpProjectProductService upProjectProductService;

    @Autowired
    private IUpProjectPersonService upProjectPersonService;

    @Autowired
    private UpHelperService upHelperService;

    @Autowired
    private IUpProductService upProductService;

    @Autowired
    private UpProjectMapper upProjectMapper;

    @Autowired
    private UpProjectProductMapper upProjectProductMapper;

    @RequiresPermissions("upgrade:projectProduct:view")
    @GetMapping()
    public String projectProduct(ModelMap mmap)
    {
        // 获取产品项目级联列表
        List<CxSelect> productList = upProductService.selectProjectAndProduct(null);
        mmap.put("projectAndProduct", JSON.toJSON(productList));
        return prefix + "/projectProduct";
    }

    /**
     * 查询项目产品列表
     */
    @RequiresPermissions("upgrade:projectProduct:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpProjectProduct upProjectProduct) throws Exception {
        startPage();
        List<UpProjectProduct> upProjectProducts = upProjectProductService.selectUpProjectProductList(upProjectProduct);
        for (UpProjectProduct projectProduct : upProjectProducts) {
            List<UpProjectPerson> upProjectPersons = upProjectPersonService.selectUpProjectPersonById(projectProduct.getProjectProductId());
            List<String> usernames = upProjectPersons.stream().map(UpProjectPerson::getUserName).collect(Collectors.toList());
            projectProduct.setProductPrincipal(String.join(",", usernames));

            String subProjectId = projectProduct.getSubProjectId();
            if (StringUtils.isNotEmpty(subProjectId)) {
                String[] projectIds = subProjectId.split(",");
                List<UpProject> upProjects = upProjectMapper.selectUpProjectListByIds(Arrays.asList(projectIds));
                List<String> projectName = upProjects.stream().map(UpProject::getProjectName).collect(Collectors.toList());
                projectProduct.setSubProjectName(StringUtils.join(projectName, ","));
            }
        }
        return getDataTable(upProjectProducts);
    }

    /**
     * 导出项目产品列表
     */
    @RequiresPermissions("upgrade:projectProduct:export")
    @Log(title = "项目产品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpProjectProduct upProjectProduct) throws Exception {
        List<UpProjectProduct> list = upProjectProductService.selectUpProjectProductList(upProjectProduct);
        ExcelUtil<UpProjectProduct> util = new ExcelUtil<UpProjectProduct>(UpProjectProduct.class);
        return util.exportExcel(list, "项目产品数据");
    }

    /**
     * 新增项目产品
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        List<SysUser> users = upHelperService.getSysUserList();
        // 筛选测试负责人
        List<SysUser> testPrincipals = users.stream().filter(s -> s.getDeptId() != null && 105 == s.getDeptId()).collect(Collectors.toList());
        mmap.put("users", upHelperService.getSysUserList());
        mmap.put("testPrincipals", testPrincipals);//测试负责人
        return prefix + "/add";
    }

    /**
     * 新增保存项目产品
     */
    @RequiresPermissions("upgrade:projectProduct:add")
    @Log(title = "项目产品", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpProjectProduct upProjectProduct) {
        return toAjax(upProjectProductService.insertUpProjectProduct(upProjectProduct));
    }

    /**
     * 修改项目产品
     */
    @GetMapping("/edit/{projectProductId}")
    public String edit(@PathVariable("projectProductId") Integer projectProductId, ModelMap mmap)
    {
        UpProjectProduct upProjectProduct = upProjectProductService.selectUpProjectProductById(projectProductId);
        mmap.put("upProjectProduct", upProjectProduct);
        List<UpProjectPerson> upProjectPersons = upProjectPersonService.selectUpProjectPersonById(upProjectProduct.getProjectProductId());
        List<SysUser> users = upHelperService.getSysUserList();
        for (SysUser user : users) {
            for (UpProjectPerson upProjectPerson : upProjectPersons) {
                if (user.getUserId().longValue() == upProjectPerson.getUserId().longValue()) {
                    user.setFlag(true);
                    break;
                }
            }
        }

        List<SysUser> projectUsers = upHelperService.getSysUserList();
        //项目经理
        String projectPrincipals = upProjectProduct.getProjectPrincipalId();
        //根据人员id查询人员名称
        if (projectPrincipals != null) {
            String[] split = projectPrincipals.split(",");
            for (String s : split) {
                for (SysUser user : projectUsers) {
                    if (s.equals(user.getUserId()+"")) {
                        //符合条件的用户放到用户的列表中
                        user.setFlag(true);
                        break;
                    }
                }
            }
        }

        // 筛选测试负责人
        List<SysUser> testPrincipals = users.stream().filter(s -> s.getDeptId() != null && 105 == s.getDeptId()).collect(Collectors.toList());

        List<UpProject> ppList = upProjectMapper.selectUpProjectList(new UpProject());
        String subProjectId = upProjectProduct.getSubProjectId();
        if (StringUtils.isNotEmpty(subProjectId)) {
            String[] split = subProjectId.split(",");
            for (String s : split) {
                for (UpProject pp : ppList) {
                    if (s.equals(pp.getProjectId() + "")) {
                        pp.setFlag(true);
                        break;
                    }
                }
            }
        }
        mmap.put("ppList", ppList);
        mmap.put("users", users);//升级负责人
        mmap.put("projectUsers", projectUsers);//项目经理
        mmap.put("testPrincipal", upProjectProduct.getTestPrincipal());//测试负责人
        mmap.put("testPrincipals", testPrincipals);//测试负责人下拉
        return prefix + "/edit";
    }

    /**
     * 修改保存项目产品
     */
    @RequiresPermissions("upgrade:projectProduct:edit")
    @Log(title = "项目产品", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpProjectProduct upProjectProduct)
    {
        return toAjax(upProjectProductService.updateUpProjectProduct(upProjectProduct));
    }

    /**
     * 删除项目产品
     */
    @RequiresPermissions("upgrade:projectProduct:remove")
    @Log(title = "项目产品", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upProjectProductService.deleteUpProjectProductByIds(ids));
    }

    /**
     * 修改项目经理
     */
    @RequiresPermissions("upgrade:projectProduct:modify")
    @GetMapping("/modify/{projectProductId}")
    public String modify(@PathVariable("projectProductId") Integer projectProductId, ModelMap mmap)
    {
        UpProjectProduct upProjectProduct = upProjectProductService.selectUpProjectProductById(projectProductId);
        mmap.put("upProjectProduct", upProjectProduct);
        List<UpProjectPerson> upProjectPersons = upProjectPersonService.selectUpProjectPersonById(upProjectProduct.getProjectProductId());
        List<SysUser> users = upHelperService.getSysUserList();
        for (SysUser user : users) {
            for (UpProjectPerson upProjectPerson : upProjectPersons) {
                if (user.getUserId().longValue() == upProjectPerson.getUserId().longValue()) {
                    user.setFlag(true);
                    break;
                }
            }
        }

        List<SysUser> projectUsers = upHelperService.getSysUserList();
        //项目经理
        String projectPrincipals = upProjectProduct.getProjectPrincipalId();
        //根据人员id查询人员名称
        if (projectPrincipals != null) {
            String[] split = projectPrincipals.split(",");
            for (String s : split) {
                for (SysUser user : projectUsers) {
                    if (s.equals(user.getUserId()+"")) {
                        //符合条件的用户放到用户的列表中
                        user.setFlag(true);
                        break;
                    }
                }
            }
        }

        mmap.put("users", users);//升级负责人
        mmap.put("projectUsers", projectUsers);//项目经理
        return prefix + "/modify";
    }

    /**
     * 修改保存项目经理
     */
    @RequiresPermissions("upgrade:projectProduct:modify")
    @Log(title = "项目产品", businessType = BusinessType.UPDATE)
    @PostMapping("/modify")
    @ResponseBody
    public AjaxResult modifySave(UpProjectProduct upProjectProduct)
    {
        return toAjax(upProjectProductService.updateUpProjectProduct(upProjectProduct));
    }
}
