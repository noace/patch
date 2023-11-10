package com.kyee.upgrade.controller;


import com.kyee.upgrade.cloud.asynpackpatch.IPatchService;
import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpProjectProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/upgrade/patchhis")
public class HisV3PatchController {
    @Autowired
    private IPatchService hisV3PatchServiceImpl;

    @GetMapping("/executePkg")
    public void executePkg() {
        String sourceUrl = "D:/work/workspace/his_v3";
        String branch = "project/v1.5.40_yangxi";
        String processId = "123";
        Long patchId = new Long(18);
        UpProjectProduct result = new UpProjectProduct();
        UpPatch patch = new UpPatch();
        try{
            hisV3PatchServiceImpl.executePkg(sourceUrl,branch,processId,patchId, result, patch);
        }catch (Throwable e){
            e.printStackTrace();
        }

    }
}
