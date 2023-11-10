package com.kyee.upgrade.controller;

import com.alibaba.fastjson.JSON;
import com.kyee.upgrade.domain.UpBuildLog;
import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpProject;
import com.kyee.upgrade.domain.UpProjectProduct;
import com.kyee.upgrade.mapper.UpPatchMapper;
import com.kyee.upgrade.mapper.UpProjectMapper;
import com.kyee.upgrade.mapper.UpProjectProductMapper;
import com.kyee.upgrade.service.IUpBuildLogService;
import com.kyee.upgrade.service.IUpPatchService;
import com.kyee.upgrade.service.IUpProductService;
import com.kyee.upgrade.utils.ConstantUtil;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.CxSelect;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.service.ISysDictDataService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 云服务器补丁包管理Controller
 *
 * @author lijunqiang
 * @date 2021-06-12
 */
@Controller
@RequestMapping("/upgrade/patch")
public class UpPatchController extends BaseController {
    private String prefix = "upgrade/patch";

    @Autowired
    private IUpPatchService upPatchService;

    @Autowired
    private IUpProductService upProductService;

    @Autowired
    private IUpBuildLogService upBuildLogService;

    @Autowired
    private UpPatchMapper upPatchMapper;

    @Autowired
    private UpProjectProductMapper projectProductMapper;

    @RequiresPermissions("upgrade:patch:view")
    @GetMapping()
    public String patch(ModelMap mmap) {
        // 获取产品项目级联列表
        List<CxSelect> productList = upProductService.selectProjectAndProduct(null);
        mmap.put("projectAndProduct", JSON.toJSON(productList));

        return prefix + "/patch";
    }

    /**
     * 查询补丁包管理列表
     */
    @RequiresPermissions("upgrade:patch:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpPatch upPatch) {
        startPage();
        List<UpPatch> list = upPatchService.getUpPatchList(upPatch);
        return getDataTable(list);
    }

    /**
     * 导出补丁包管理列表
     */
    @RequiresPermissions("upgrade:patch:export")
    @Log(title = "补丁包管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpPatch upPatch) {
        List<UpPatch> list = upPatchService.selectUpPatchList(upPatch);
        ExcelUtil<UpPatch> util = new ExcelUtil<UpPatch>(UpPatch.class);
        return util.exportExcel(list, "补丁包管理数据");
    }

    /**
     * 新增补丁包管理
     */
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    /**
     * 新增保存补丁包管理
     */
    @RequiresPermissions("upgrade:patch:add")
    @Log(title = "补丁包管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpPatch upPatch) {
        return toAjax(upPatchService.insertUpPatch(upPatch));
    }

    /**
     * 补丁包管理详情
     */
    @GetMapping("/detail/{patchId}")
    public String detail(@PathVariable("patchId") Long patchId, ModelMap mmap) {
        UpPatch upPatch = upPatchService.selectUpPatchById(patchId);
        mmap.put("upPatch", upPatch);
        return prefix + "/detail";
    }

    /**
     * 登记补丁包管理
     */
    @GetMapping("/regist")
    public String regist(ModelMap mmap) {

        Map<String, Object> paramsMap = upPatchService.getRegistPatchParams();
        Object pp = paramsMap.get("projectAndProduct");
        mmap.put("projectAndProduct", JSON.toJSON(pp));

        return prefix + "/regist";
    }

    /**
     * 检查补丁包是否重复登记
     */
    @RequiresPermissions("upgrade:patch:regist")
    @PostMapping("/checkRepeatRegist")
    @ResponseBody
    public AjaxResult checkRepeatRegist(UpPatch upPatch) {
        //查询任务号是否已存在
        List<Map> existsUpPatchs = upPatchService.searchPatchs(upPatch.getJiraNo(),upPatch.getDemandNo(),upPatch.getProductId(),upPatch.getProjectId());
        String message = "";
        if (!CollectionUtils.isEmpty(existsUpPatchs)) {
            StringBuilder sb = new StringBuilder();
            for (Map existsUpPatch : existsUpPatchs) {
                sb.append((upPatch.getJiraNo().equals(existsUpPatch.get("jira_no")) ? ("任务号【" + existsUpPatch.get("jira_no")) : ("需求号【" + existsUpPatch.get("demand_no"))) + "】在当前项目已打过包，补丁名称【" + existsUpPatch.get("topic") + "】，补丁状态【" + existsUpPatch.get("dict_label") + "】，责任人【" + existsUpPatch.get("update_by") + "】，编译时间【" + existsUpPatch.get("build_time") + "】。<br/>");
            }
            sb.append("请确定是否继续登记？");
            message = sb.toString();
        }
        return StringUtils.isNotEmpty(message) ? AjaxResult.warn(message) : AjaxResult.success("");
    }


    /**
     * 检查补丁包是否重复登记
     */
    @PostMapping("/validatePatchMergeTime")
    @ResponseBody
    public AjaxResult validatePatchMergeTime(UpPatch upPatch) {

        Map<String, String> map = upPatchService.validatePatchMergeTime(upPatch);
        return AjaxResult.success(map);
    }

    /**
     * 登记补丁包管理
     */
    @RequiresPermissions("upgrade:patch:regist")
    @Log(title = "补丁包管理", businessType = BusinessType.INSERT)
    @PostMapping("/regist")
    @ResponseBody
    public AjaxResult registPatch(UpPatch upPatch) {

        String[] subProjectIds = {};
        Integer productId = upPatch.getProductId();
        if (upPatch.getProjectId() != null) {
            UpProjectProduct projectProduct = projectProductMapper.getUpProjectProductById(productId, upPatch.getProjectId());
            if (StringUtils.isNotEmpty(projectProduct.getSubProjectId())) {
                String subProjectId = projectProduct.getSubProjectId();
                subProjectIds = subProjectId.split(",");
            }
        }

        //获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();
        upPatch.setCreateBy(sysUser.getUserName());
        upPatch.setUpdateBy(sysUser.getUserName());
        upPatch.setUpdateByUserId(sysUser.getUserId());
        try {
            if(ConstantUtil.ProjectProductEnum.LIS.getValue().equals(productId)) {

                UpProjectProduct upProjectProduct = new UpProjectProduct();
                upProjectProduct.setProductId(ConstantUtil.ProjectProductEnum.LIS.getValue());
                List<UpProjectProduct> pps = projectProductMapper.selectByProductIdAndProjectId(upProjectProduct);
                for (UpProjectProduct pp : pps) {
                    upPatch.setProjectId(pp.getProjectId());
                    upPatchService.registPatch(upPatch);
                }
                // 包含子项目
            } else if (subProjectIds.length != 0) {
                for (String projectId : subProjectIds) {
                    upPatch.setProductId(productId);
                    upPatch.setProjectId(Integer.parseInt(projectId));
                    upPatchService.registPatch(upPatch);
                }
            } else {
                upPatchService.registPatch(upPatch);
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.success();
    }

    /**
     * 修改补丁包管理
     */
    @GetMapping("/edit/{patchId}")
    public String edit(@PathVariable("patchId") Long patchId, ModelMap mmap) {
        UpPatch upPatch = upPatchService.selectUpPatchById(patchId);
        mmap.put("upPatch", upPatch);

        // 获取产品项目级联列表
        List<CxSelect> productList = upProductService.selectProjectAndProduct(null);
        mmap.put("projectAndProduct", JSON.toJSON(productList));

        return prefix + "/edit";
    }

    /**
     * 修改保存补丁包管理
     */
    @RequiresPermissions("upgrade:patch:edit")
    @Log(title = "补丁包管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpPatch upPatch) {
        //获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();
        upPatch.setUpdateBy(sysUser.getUserName());
        upPatch.setUpdateByUserId(sysUser.getUserId());
        try {
            upPatchService.updateUpPatch(upPatch);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.success("修改成功，开始重新打包");
    }

    /**
     * 删除补丁包管理
     */
    @RequiresPermissions("upgrade:patch:remove")
    @Log(title = "补丁包管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String id) {
        return toAjax(upPatchService.deleteUpPatchByIds(id));
    }

    /**
     * 发布补丁包管理
     */
    @RequiresPermissions("upgrade:patch:publish")
    @PostMapping("/publish")
    @ResponseBody
    public AjaxResult publish(String ids) {
        String publish = null;
        try {
            publish = upPatchService.publish(ids);
            if (!"true".equals(publish)) {
                return AjaxResult.warn(publish);
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.success();
    }

    /**
     * 合并补丁包
     */
    @RequiresPermissions("upgrade:patch:mergeDownlod")
    @Log(title = "补丁包管理", businessType = BusinessType.DOWNLOAD)
    @PostMapping("/mergeDownlod")
    @ResponseBody
    public AjaxResult mergeDownlod(String ids) {
        AjaxResult result = new AjaxResult();
        // 登记要合并的补丁包
        String updateBy = ShiroUtils.getSysUser().getUserName();
        try {
            result = upPatchService.mergeUpPatch(ids, updateBy);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return result;
    }

    /**
     * 取消发布补丁包管理
     */
    @RequiresPermissions("upgrade:patch:canclePublish")
    @PostMapping("/canclePublish")
    @ResponseBody
    public AjaxResult canclePublish(String ids) {
        UpPatch upPatch = upPatchService.selectUpPatchById(Long.valueOf(ids));
        upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PKG_SUCCESS.getValue());
//        SysUser sysUser = ShiroUtils.getSysUser();
//        upPatch.setUpdateBy(sysUser.getUserName());
        upPatch.setUpdateTime(DateUtils.getNowDate());
        upPatchService.updateUpPatchNoSession(upPatch);
        return AjaxResult.success();
    }


    /**
     * 取消合并补丁包
     */
    @RequiresPermissions("upgrade:patch:cancleMerge")
    @PostMapping("/cancleMerge")
    @ResponseBody
    public AjaxResult cancleMerge(Long id) {
        SysUser sysUser = ShiroUtils.getSysUser();
        UpPatch upPatch = new UpPatch();
        upPatch.setPatchId(id);
        upPatch.setDelFlag("Y");
        upPatch.setUpdateBy(sysUser.getUserName());
        upPatch.setUpdateTime(DateUtils.getNowDate());
        upPatchService.updateUpPatchNoSession(upPatch);

        List<UpPatch> patchList = upPatchService.selectUpPatchListByParentPatchId(id);
        patchList.forEach(s -> {
            s.setMergePackageFlag("N");
            upPatchService.updateUpPatchNoSession(s);
        });

        return AjaxResult.success();
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
        List<UpPatch> patchList = upPatchService.selectUpPatchListByParentPatchId(patchId);
        return getDataTable(patchList);
    }

    /**
     * 获取补丁包打包日志
     */
    @RequiresPermissions("upgrade:patch:getBuildLog")
    @GetMapping("/getBuildLog/{patchId}")
    public String getBuildLog(@PathVariable("patchId") Long patchId, ModelMap mmap) {
        List<UpBuildLog> buildLogList = upBuildLogService.getBuildLogsByPatchId(patchId);
        mmap.put("patchId", patchId);
        mmap.put("logs", buildLogList);
        return prefix + "/buildlog";
    }

    /**
     * 查询补丁包最新编译日志列表
     */
    @PostMapping("/getBuildLog")
    @ResponseBody
    public List<UpBuildLog> getBuildLog(Long patchId) {
        return upBuildLogService.getBuildLogsByPatchId(patchId);
    }

    /**
     * 根据提交序号查询代码列表
     */
    @PostMapping("/searchCode")
    @ResponseBody
    public AjaxResult searchCode(String commitId, Integer productId, Integer projectId) {

        Map<String, Object> map;
        try {

            // 新版定时器项目
            if (productId == 10) {
                map = upPatchService.searchCodeByHisScheduled(commitId, productId, projectId);
            } else {
                map = upPatchService.searchCode(commitId, productId, projectId);
            }
        } catch (Exception e) {
           return  AjaxResult.error(e.getMessage());
        }
        return new AjaxResult(AjaxResult.Type.SUCCESS, "查询成功" ,map);
    }

    /**
     * 发现Bug
     */
    @RequiresPermissions("upgrade:patch:findBug")
    @PostMapping("/findBug/{patchId}")
    @ResponseBody
    public AjaxResult findBug(@PathVariable("patchId") Long patchId) throws Exception {
        upPatchService.findBug(patchId);
        return AjaxResult.success();
    }

    /**
     * 修复Bug
     */
    @RequiresPermissions("upgrade:patch:repairPatch")
    @GetMapping("/repairPatch/{bugPatchId}")
    public String repairPatch(ModelMap mmap, @PathVariable("bugPatchId") String bugPatchId) {
        // 获取产品项目级联列表
        List<CxSelect> productList = upProductService.selectProjectAndProduct(null);
        UpPatch upPatch = upPatchMapper.selectUpPatchById(Long.parseLong(bugPatchId));
        mmap.put("projectAndProduct", JSON.toJSON(productList));
        mmap.put("bugfixPatch", bugPatchId);
        mmap.put("lockId", upPatch.getLockId());
        mmap.put("projectId", upPatch.getProjectId());
        mmap.put("productId", upPatch.getProductId());
        mmap.put("bugCount", 0);
        return prefix + "/repair";
    }

    /**
     * 修复Bug
     */
    @RequiresPermissions("upgrade:patch:repairPatch")
    @Log(title = "补丁包管理", businessType = BusinessType.INSERT)
    @PostMapping("/repair")
    @ResponseBody
    public AjaxResult repairPatch(UpPatch upPatch) {
        try {
            upPatchService.repairPatch(upPatch);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.success();
    }

    /**
     * 根据提交序号查询代码列表
     */
    @PostMapping("/searchPatchBranch")
    @ResponseBody
    public String searchPatchBranch(Integer productId, Integer projectId) {

        return upPatchService.searchPatchBranch(productId, projectId);
    }

    /**
     * 设置加急包状态
     * 已升级未发布补丁包加急需要给项目产品责任人发送企业微信消息
     */
    @RequiresPermissions("upgrade:patch:expedited")
    @PostMapping("/setExpedited")
    @ResponseBody
    public AjaxResult setExpedited(@RequestParam  Map<String,String> data) {
        upPatchService.setExpeditedStatus(data.get("ids"),data.get("status"));
        String upPatchStatus = data.get("upPatchStatus");
        if("Y".equals(data.get("status"))){
            String projectIds = data.get("projectIds");
            String productIds = data.get("productIds");
            String jiraNos = data.get("jiraNos");
            String topics = data.get("topics");
            String productNames = data.get("productNames");
            String projectNames = data.get("projectNames");
            String developer = data.get("developer");
            String patchFileName = data.get("patchFileName");
            upPatchService.sendExpeditedUpgradeMassage(upPatchStatus,projectIds,productIds,jiraNos,topics,productNames,projectNames,developer,patchFileName);
        }
        return AjaxResult.success("Y".equals(data.get("status"))?"加急成功，已打包未发布的补丁包发布后将通知项目产品负责人，已发布未升级的补丁包已通知项目产品负责人！":"取消加急成功！");
    }
}
