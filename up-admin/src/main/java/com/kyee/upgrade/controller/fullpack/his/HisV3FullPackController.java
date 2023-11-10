package com.kyee.upgrade.controller.fullpack.his;

import com.alibaba.fastjson.JSON;
import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpProjectProduct;
import com.kyee.upgrade.mapper.UpProjectProductMapper;
import com.kyee.upgrade.service.IUpPatchService;
import com.kyee.upgrade.service.impl.HisV3FullPackService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/upgrade/patch")
public class HisV3FullPackController {

    private String prefix = "upgrade/patch";

    @Autowired
    private HisV3FullPackService hisV3FullPackService;

    @Autowired
    private IUpPatchService patchService;

    @Autowired
    private UpProjectProductMapper projectProductMapper;

    /**
     * 全量包打包
     */
    @GetMapping("/fullpack")
    public String fullPack(ModelMap mmap) {

        Map<String, Object> paramsMap = patchService.getRegistPatchParams();
        Object pp = paramsMap.get("projectAndProduct");
        mmap.put("projectAndProduct", JSON.toJSON(pp));

        return prefix + "/fullpack";
    }

    /**
     *  HISV3全量包
     * @return
     */
    @RequiresPermissions("upgrade:patch:fullpack")
    @PostMapping("/fullPack")
    @ResponseBody
    public AjaxResult hisV3FullPack(UpPatch upPatch) {

        if (upPatch.getProductId() == 7) {
            return AjaxResult.warn("期待后续支持LIS完整包功能.");
        }
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
            // 包含子项目
            if (subProjectIds.length != 0) {
                for (String projectId : subProjectIds) {
                    upPatch.setProductId(productId);
                    upPatch.setProjectId(Integer.parseInt(projectId));
                    hisV3FullPackService.hisV3FullPack(upPatch);
                }
            } else {
                hisV3FullPackService.hisV3FullPack(upPatch);
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }

        return AjaxResult.success();
    }


}
