package com.kyee.upgrade.scheduled.job;

import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.mapper.UpPatchMapper;
import com.kyee.upgrade.utils.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 消息通知内容
 */
@Service
public class MessageContentService {

    @Autowired
    private UpPatchMapper patchMapper;

    /**
     * 当日之前已打包状态的包列表
     */
    public int getPatchWithSuccess(String deptId){
        UpPatch upPatch = new UpPatch();
        upPatch.setCreateTime(new Date());
        upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PKG_SUCCESS.getValue());
        return patchMapper.getCountByStatus(upPatch, deptId);
    }

    /**
     * 已打包统计:测试流程统计到测试人员，其他统计到开发人员
     */
    public List<Map<String, String>> getCountByUsername(String deptId){
        UpPatch upPatch = new UpPatch();
        upPatch.setCreateTime(new Date());
        upPatch.setPatchStatus(ConstantUtil.UpPatchStatus.PKG_SUCCESS.getValue());
        return patchMapper.getCountByUsername(upPatch, deptId);
    }

    /**
     * 打包失败状态的包列表(不包含LIS项目)
     */
    public List<Map<String, String>> getPatchWithFailAndBug(String deptId, Integer productId){
        List<String> statusList = new ArrayList<>();
        statusList.add(ConstantUtil.UpPatchStatus.PKG_FAIL.getValue());
        statusList.add(ConstantUtil.UpPatchStatus.ROLLBACK.getValue());
        return patchMapper.getPatchWithFailAndBug(statusList, deptId, productId);
    }
}
