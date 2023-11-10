package com.kyee.upgrade.controller.fullpack.his;

import com.alibaba.fastjson.JSON;
import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.service.IUpPatchService;
import com.kyee.upgrade.service.impl.HisV3FullPackService;
import com.ruoyi.common.core.domain.AjaxResult;
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
    public AjaxResult hisV3FullPack(UpPatch patch) {

        if (patch.getProductId() == 7) {
            return AjaxResult.warn("期待后续支持LIS完整包功能.");
        }
        hisV3FullPackService.hisV3FullPack(patch);
        return AjaxResult.success();
    }


}
