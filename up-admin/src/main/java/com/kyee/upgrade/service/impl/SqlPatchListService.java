package com.kyee.upgrade.service.impl;
import com.google.common.collect.Lists;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.kyee.upgrade.domain.vo.UpUpgradePatchDTO;
import com.kyee.upgrade.mapper.UpUpgradeRecordClientMapper;
import com.kyee.upgrade.utils.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SqlPatchListService {

    @Autowired
    private UpUpgradeRecordClientMapper recordClientMapper;

    /**
     * 查询包含SQL的补丁包列表
     * @param patchDTO
     * @return
     */
    public List<UpPatchClientExtend> getUpgradeSqlList(UpUpgradePatchDTO patchDTO) {

        List<UpPatchClientExtend> clientList = new ArrayList<>();
        patchDTO.setSqlFlag("Y");
        if ("测试".equals(patchDTO.getServerType())) {
            patchDTO.setServerType("测试");
            patchDTO.setTestStatus(ConstantUtil.UpgradeStatus.NOT_TEST.getValue());
            clientList = recordClientMapper.getUpgradeSqlPatch(patchDTO);
        }
        if ("生产".equals(patchDTO.getServerType())) {
            patchDTO.setServerType("生产");
            patchDTO.setPatchStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
            clientList = recordClientMapper.getUpgradeSqlPatch(patchDTO);
        }
        return clientList;
    }

    /**
     * 查询历史包含SQL的补丁包列表
     * @param patchDTO
     * @return
     */
    public List<UpPatchClientExtend> getHistoryUpgradeSqlList(UpUpgradePatchDTO patchDTO) {

        List<UpPatchClientExtend> clientList = new ArrayList<>();
        patchDTO.setSqlFlag("Y");
        if ("测试".equals(patchDTO.getServerType())) {
            patchDTO.setServerType("测试");
            patchDTO.setTestStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.SQL_TESTED.getValue(),
                                                        ConstantUtil.UpgradeStatus.TEST_UPGRADE.getValue()));
            clientList = recordClientMapper.getHistoryUpgradeSqlPatch(patchDTO);
        }
        if ("生产".equals(patchDTO.getServerType())) {
            patchDTO.setServerType("生产");
            patchDTO.setPatchStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.SQL_EXECUTED.getValue(),
                                                        ConstantUtil.UpgradeStatus.UPGRADE_PART.getValue(),
                                                        ConstantUtil.UpgradeStatus.UPGRADE_ALL.getValue()));
            clientList = recordClientMapper.getHistoryUpgradeSqlPatch(patchDTO);
        }
        return clientList;
    }
}
