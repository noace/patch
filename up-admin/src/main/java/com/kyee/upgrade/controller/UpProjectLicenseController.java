package com.kyee.upgrade.controller;

import com.kyee.upgrade.domain.License;
import com.kyee.upgrade.domain.UpProject;
import com.kyee.upgrade.service.IUpProjectLicenseService;
import com.kyee.upgrade.service.IUpProjectService;
import com.kyee.upgrade.utils.LicenseUtil;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * 项目授权码生成Controller
 *
 * @author jiaoyun
 * @date 2023-06-19
 */
@Controller
@RequestMapping("/upgrade/projectLicense")
public class UpProjectLicenseController extends BaseController
{
    private String prefix = "upgrade/projectLicense";

    @Autowired
    private IUpProjectLicenseService upProjectLicenseService;

    @RequiresPermissions("upgrade:projectLicense:view")
    @GetMapping()
    public String project()
    {
        return prefix + "/projectLicense";
    }

    /**
     * 生成license授权码
     */
    @PostMapping("/getLicense")
    @ResponseBody
    public AjaxResult getLicense(String bindMac, String serverMac, String allowedHospitalCount, String expireDate) {
        Map<String, Object> map;
        try {
            map = upProjectLicenseService.getLicense(bindMac, serverMac, allowedHospitalCount,expireDate);
        } catch (Exception e) {
            return  AjaxResult.error(e.getMessage());
        }
        return new AjaxResult(AjaxResult.Type.SUCCESS, "生成License成功" ,map);
    }

    /**
     * 加密数据库密码
     */
    @PostMapping("/getEncryptPassword")
    @ResponseBody
    public AjaxResult getEncryptPassword(String password) {
        Map<String, Object> map;
        try {
            map = upProjectLicenseService.getEncryptPassword(password);
        } catch (Exception e) {
            return  AjaxResult.error(e.getMessage());
        }
        return new AjaxResult(AjaxResult.Type.SUCCESS, "加密成功" ,map);
    }
}
