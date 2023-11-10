package com.kyee.upgrade.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kyee.feign.IFeignClient;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.kyee.upgrade.mapper.UpPatchClientMapper;
import com.kyee.upgrade.service.*;
import com.kyee.upgrade.utils.ConstantUtil;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.CxSelect;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 端服务器补丁包管理Controller
 *
 * @author zhanghoayu
 * @date 2021-10-21
 */
@Controller
@RequestMapping("/upgrade/patchClient")
public class UpPatchClientController extends BaseController
{
    private String prefix = "upgrade/patchClient";

    @Autowired
    private IFeignClient feignClient;

    @Autowired
    private IUpPatchClientService upPatchClientService;

    @Autowired
    private IUpUpgradeRecordClientService upUpgradeRecordClientService;

    @Autowired
    private IUpProjectProductService upProjectProductService;

    @Autowired
    private IUpProductService upProductService;

    @Autowired
    private UpPatchClientMapper clientMapper;

    @RequiresPermissions("upgrade:patchClient:view")
    @GetMapping()
    public String patchClient(ModelMap mmap)
    {
        SysUser sysUser = ShiroUtils.getSysUser();
        // 获取产品项目级联列表
        List<CxSelect> productList = upProductService.selectProjectAndProduct(sysUser.getUserId());
        mmap.put("projectAndProduct", JSON.toJSON(productList));
        return prefix + "/patchClient";
    }

    /**
     * 查询补丁列表
     */
    @RequiresPermissions("upgrade:patchClient:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UpPatchClient upPatchClient)
    {
        Integer pageNum = startPage();
        List<UpPatchClientExtend> upPatchClientList = upPatchClientService.getUpPatchClientList(upPatchClient);
        if (CollectionUtils.isEmpty(upPatchClientList)) {
            return getDataTable(upPatchClientList);
        }
        for (UpPatchClientExtend patchClient : upPatchClientList) {
            patchClient.setPageNum(pageNum);
            // 查询补丁包升级记录，如果都为成功，则为全部升级，否则为部分升级
        }
        upPatchClientList.get(0).setFirstPatchFlag(true);
        upPatchClientList.get(upPatchClientList.size()-1).setLastPatchFlag(true);
        return getDataTable(upPatchClientList);
    }

    /**
     * 导出补丁列表
     */
    @RequiresPermissions("upgrade:patchClient:export")
    @Log(title = "补丁", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UpPatchClient upPatchClient)
    {
        List<UpPatchClient> list = upPatchClientService.selectUpPatchClientList(upPatchClient);
        ExcelUtil<UpPatchClient> util = new ExcelUtil<UpPatchClient>(UpPatchClient.class);
        return util.exportExcel(list, "补丁数据");
    }

    /**
     * 新增补丁
     */
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存补丁
     */
    @RequiresPermissions("upgrade:patchClient:add")
    @Log(title = "补丁", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UpPatchClient upPatchClient)
    {
        return toAjax(upPatchClientService.insertUpPatchClient(upPatchClient));
    }

    /**
     * 删除补丁
     */
    @RequiresPermissions("upgrade:patchClient:remove")
    @Log(title = "补丁", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(upPatchClientService.deleteUpPatchClientById(Long.valueOf(ids)));
    }

    /**
     * 跳转云服务器补丁列表
     */
    @GetMapping("/downloadControl")
//    @RequiresPermissions("upgrade:patchClient:downloadControl")
    @Log(title = "补丁", businessType = BusinessType.DOWNLOAD)
    public String downloadControl(ModelMap mmap)
    {
        SysUser sysUser = ShiroUtils.getSysUser();
        // 获取产品项目级联列表
        List<CxSelect> productList = upProductService.selectProjectAndProduct(sysUser.getUserId());
        mmap.put("projectAndProduct", JSON.toJSON(productList));

        return prefix + "/downloadControl";
    }

    /**
     * 查询云服务器发布补丁列表
     */
//    @RequiresPermissions("upgrade:patchClient:getPatchFileList")
    @PostMapping("/getPatchFileList")
    @ResponseBody
    public TableDataInfo getPatchFileList(UpPatchClient upPatchClient) throws Exception {
//        startPage();
        HashMap<Object, Object> paramMap = new HashMap<>();
        paramMap.put("patchClient", upPatchClient);

        // 调用云服务器发布补丁列表接口
        Map<String, Object> patchFileMap = feignClient.getPatchFileList(paramMap);
        if(!"0".equals(patchFileMap.get("code")+"")){
            TableDataInfo rspData = new TableDataInfo();
            rspData.setCode(1);
            rspData.setMsg(patchFileMap.get("msg")+"");
            return rspData;
        }

        List<UpPatchClient> upPatchClientList = JSONObject.parseArray(
                JSONObject.toJSONString(patchFileMap.get("data")), UpPatchClient.class);

//        List<UpPatchClient> pulledPatchList = upPatchClientService.selectUpPatchClientList(upPatchClient);
//        List<String> patchFileNames = pulledPatchList.stream().map(UpPatchClient::getPatchFileName)
//                                                              .collect(Collectors.toList());
        // SQL优化
        List<String> patchFileNames = upPatchClientService.selectPatchFileName(upPatchClient);

        List<Long> pulledList = new ArrayList<>();
        for (UpPatchClient patchClient : upPatchClientList) {
            if (patchFileNames.contains(patchClient.getPatchFileName())) {
                patchClient.setPulledFlag("Y");
                pulledList.add(patchClient.getPatchId());
            } else {
                patchClient.setPulledFlag("N");
            }
        }
        // 是否拉取筛选
        if (StringUtils.isNotEmpty(upPatchClient.getPulledFlag())) {
            upPatchClientList = upPatchClientList.stream().filter(
                    s -> upPatchClient.getPulledFlag().equals(s.getPulledFlag())).collect(Collectors.toList());
        }

        // 按编译时间排序
        upPatchClientList.sort(Comparator.comparing(UpPatchClient::getBuildTime, Comparator.nullsLast(String::compareTo)));
//        upPatchClientList.sort(Comparator.comparing(UpPatchClient::getPatchStatus).reversed());

        upPatchClientList.forEach(s -> s.setPulledList(pulledList));
        return getDataTable(upPatchClientList);
    }

    /**
     * 合并云服务器发布补丁包
     */
    @PostMapping("/mergeDownlod")
    @ResponseBody
    public AjaxResult mergeDownlod(String ids) throws Exception {
        SysUser sysUser = ShiroUtils.getSysUser();

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("ids", ids);
        paramMap.put("updateBy", sysUser.getUserName());

        // 调用云服务器发布补丁列表接口
        Map<String, Object> patchFileMap = feignClient.mergeDownlod(paramMap);
        if(!"0".equals(patchFileMap.get("code")+"")){
            return AjaxResult.error(patchFileMap.get("msg") + "");
        }

        return AjaxResult.success();
    }

    /**
     * 取消合并云服务器发布补丁包
     */
    @PostMapping("/cancleMerge")
    @ResponseBody
    public AjaxResult cancleMerge(Long id) throws Exception {
        SysUser sysUser = ShiroUtils.getSysUser();

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("patchId", id);
        paramMap.put("updateBy", sysUser.getUserName());

        // 调用取消云服务器发布补丁列表接口
        Map<String, Object> patchFileMap = feignClient.cancleMerge(paramMap);
        if(!"0".equals(patchFileMap.get("code")+"")){
            return AjaxResult.error(patchFileMap.get("msg") + "");
        }

        return AjaxResult.success();
    }

    /**
     * 弹出合并子包列表页面
     */
    @GetMapping("/jiraList/{client}/{patchId}")
    public String jiraList(@PathVariable("client") boolean client, @PathVariable("patchId") Long patchId, ModelMap mmap) {
        mmap.put("client", client);
        mmap.put("patchId", patchId);
        return prefix + "/jiraList";
    }

    /**
     * 查询子包列表
     */
    @PostMapping("/jiraList")
    @ResponseBody
    public TableDataInfo jiraList(boolean client, Long patchId) {
//        startPage();
        List<UpPatchClient> patchList = null;
        if (client) {
            // 获取本地子包列表
            patchList = upPatchClientService.selectUpPatchListByParentPatchId(patchId);
        } else {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("patchId", patchId);

            // 调用云服务器子包列表接口
            Map<String, Object> patchFileMap = feignClient.getPatchChildList(paramMap);
            if(!"0".equals(patchFileMap.get("code")+"")){
                TableDataInfo rspData = new TableDataInfo();
                rspData.setCode(1);
                rspData.setMsg(patchFileMap.get("msg")+"");
                return rspData;
            }

            patchList = (List<UpPatchClient>) patchFileMap.get("data");
        }

        return getDataTable(patchList);
    }

    /**
     * 拉取补丁包
     */
//    @RequiresPermissions("upgrade:patchClient:downloadPatchFile")
    @Log(title = "升级管理", businessType = BusinessType.DOWNLOAD)
    @PostMapping( "/downloadPatchFile")
    @ResponseBody
    public AjaxResult downloadPatchFile(String patchId, String pulledList) {

        return upPatchClientService.downloadPatchFile(patchId, pulledList);
    }

    /**
     * 升级补丁包跳转
     */
    @GetMapping("/edit/{patchId}")
    public String edit(@PathVariable("patchId") Long patchId, ModelMap mmap)
    {
        UpPatchClient upPatchClient = upPatchClientService.selectUpPatchClientById(patchId);
        mmap.put("upPatchClient", upPatchClient);
        return prefix + "/upgrade";
    }

    /**
     * 升级补丁包
     */
    @RequiresPermissions("upgrade:patchClient:edit")
    @Log(title = "补丁", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UpUpgradeRecordClient upUpgradeRecordClient)
    {
        SysUser user =(SysUser) SecurityUtils.getSubject().getPrincipal();

        // 根据服务id和补丁id查找升级记录
        List<UpUpgradeRecordClient> upUpgradeRecordClients = upUpgradeRecordClientService.selectUpUpgradeRecordClientList(upUpgradeRecordClient);

        int updateResult = 0;

        // 新增升级记录
        if (null == upUpgradeRecordClients || upUpgradeRecordClients.size() == 0) {
            upUpgradeRecordClient.setUpStatus(ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue());
            upUpgradeRecordClient.setUpTimes(1);
            upUpgradeRecordClient.setLastUpWorker(user.getUserName());
            upUpgradeRecordClient.setLastUpTime(DateUtils.getNowDate());
            updateResult = upUpgradeRecordClientService.insertUpUpgradeRecordClient(upUpgradeRecordClient);
        }
        // 更新升级记录
        else {
            for (UpUpgradeRecordClient findUpUpgradeRecordClient : upUpgradeRecordClients) {
                upUpgradeRecordClient.setUpgradeId(findUpUpgradeRecordClient.getUpgradeId());
                upUpgradeRecordClient.setLastUpWorker(user.getUserName());
                upUpgradeRecordClient.setLastUpTime(DateUtils.getNowDate());
                upUpgradeRecordClient.setUpStatus(ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue());
                upUpgradeRecordClient.setUpTimes(findUpUpgradeRecordClient.getUpTimes()+1);
                updateResult = upUpgradeRecordClientService.updateUpUpgradeRecordClient(upUpgradeRecordClient);
            }
        }

        return toAjax(updateResult);
    }

    /**
     * 上传补丁包跳转
     */
    @GetMapping("/uploadControl")
    @RequiresPermissions("upgrade:patchClient:uploadControl")
    @Log(title = "升级管理", businessType = BusinessType.UPLOAD)
    public String uploadControl()
    {
        return prefix + "/uploadControl";
    }

    /**
     * 上传补丁包
     */
//    @RequiresPermissions("upgrade:patchClient:uploadPatchFile")
    @Log(title = "升级管理", businessType = BusinessType.UPLOAD)
    @GetMapping( "/uploadPatchFile")
    @ResponseBody
    public AjaxResult uploadPatchFile(UpPatchClient upPatchClient) throws Exception {
        Long userId = ShiroUtils.getUserId();

        String fileName = upPatchClient.getPatchFileName();

        if (null == fileName || fileName.isEmpty()) {
            return AjaxResult.warn("补丁文件获取失败，请联系管理员！");
        }

        String productCode = "";
        // 截取文件名携带信息并存储
        try {
            productCode = fileName.substring(fileName.indexOf("[") + 1, fileName.indexOf("]"));
            String sqlFlag = fileName.substring(fileName.indexOf("SQL") + 3,fileName.indexOf("SQL") + 4);
            String buildTime = fileName.substring(fileName.lastIndexOf("_") + 1 ,fileName.lastIndexOf("."));

            UpProduct upProduct = new UpProduct();
            upProduct.setProductCode(productCode);
            List<UpProduct> upProducts = upProductService.selectUpProductList(upProduct);

            UpProjectProduct upProjectProduct = new UpProjectProduct();
            upProjectProduct.setProductPrincipalId(userId);
            upProjectProduct.setProductId(upProducts.get(0).getProductId());
            List<UpProjectProduct> upProjectProducts = upProjectProductService.selectUpProjectProductList(upProjectProduct);
            List<String> projectNames = upProjectProducts.stream().map(UpProjectProduct::getProjectName).collect(Collectors.toList());

            upPatchClient.setProductId(upProducts.get(0).getProductId());
            upPatchClient.setProductName(upProducts.get(0).getProductName());
            upPatchClient.setProjectId(upProjectProducts.get(0).getProjectId());
            upPatchClient.setProjectName(projectNames.get(0));
            upPatchClient.setSqlFlag(sqlFlag);
            upPatchClient.setBuildTime(buildTime);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.warn("补丁文件名不规范，无法存储携带信息，请检查！");
        }

        // 存储上传人信息
        SysUser user =(SysUser) SecurityUtils.getSubject().getPrincipal();
        upPatchClient.setUpBy(user.getUserName());

        // 存储数据库资源地址
        String fileUrl = Constants.RESOURCE_PREFIX + "/" + productCode + "/" + fileName;
        upPatchClient.setPatchFileUrl(fileUrl);

        // 插入端服务器补丁表
        UpPatchClient findPatchClient = new UpPatchClient();
        findPatchClient.setPatchFileName(upPatchClient.getPatchFileName());
        List<UpPatchClient> upPatchClients = upPatchClientService.selectUpPatchClientList(findPatchClient);
        if (null == upPatchClients || upPatchClients.size() == 0) {
            upPatchClientService.insertUpPatchClient(upPatchClient);
        }else {
            for (UpPatchClient updateUpPatchClient : upPatchClients) {
                upPatchClient.setPatchId(updateUpPatchClient.getPatchId());
                upPatchClientService.updateUpPatchClient(upPatchClient);
            }
        }

        return AjaxResult.success();
    }

    /**
     * 临时升级
     */
    @PostMapping( "/tempUpgrade")
    @ResponseBody
    public AjaxResult tempUpgrade(Long patchId, String patchFileName, String mergePackageFlag, String isUpgrade)
    {
        return upPatchClientService.rollBackUpPatch(patchId, patchFileName, mergePackageFlag, isUpgrade);
    }

    /**
     * 跳转服务器管理页面
     */
    @GetMapping( "/upgradeManageServer/{patchId}")
    public String upgradeManageServer(@PathVariable("patchId") Long patchId, ModelMap mmap)  {

        UpPatchClient upPatchClient = upPatchClientService.selectUpPatchClientById(patchId);
        mmap.put("upPatchClient", upPatchClient);
        return prefix + "/upgradeServer";
    }

    /**
     * 合包失败重新打包
     */
    @PostMapping("/rePatch")
    @ResponseBody
    public AjaxResult rePatch(String patchId) {
        SysUser sysUser = ShiroUtils.getSysUser();

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("patchId", patchId);
        paramMap.put("updateBy", sysUser.getUserName());

        // 调用取消云服务器发布补丁列表接口
        Map<String, Object> patchFileMap = feignClient.rePatch(paramMap);
        if(!"0".equals(patchFileMap.get("code")+"")){
            return AjaxResult.error(patchFileMap.get("msg") + "");
        }

        return AjaxResult.success();
    }

    /**
     * 升级时校验重复文件
     */
    @PostMapping( "/validateUpgrade")
    @ResponseBody
    public AjaxResult validateUpgrade(Long patchId)
    {
        return upPatchClientService.validateUpgrade(patchId);
    }
}
