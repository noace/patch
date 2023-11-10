package com.kyee.upgrade.service.impl;
import com.kyee.upgrade.domain.UpProjectServer;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.kyee.upgrade.domain.vo.UpUpgradePatchDTO;
import com.kyee.upgrade.mapper.UpProjectServerMapper;
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
    private UpProjectServerMapper serverMapper;

    @Autowired
    private UpUpgradeRecordClientMapper recordClientMapper;

    /**
     * 查询包含SQL的补丁包列表
     * @param patchDTO
     * @return
     */
    public List<UpPatchClientExtend> getUpgradeSqlList(UpUpgradePatchDTO patchDTO) {

        List<UpPatchClientExtend> clientList = new ArrayList<>();
        UpProjectServer server = serverMapper.selectUpProjectServerById(patchDTO.getServerId());
        if (!Objects.isNull(server)) {
            patchDTO.setSqlFlag("Y");
            if ("测试".equals(server.getServerType())) {
                patchDTO.setServerType("测试");
                patchDTO.setTestStatus(ConstantUtil.UpgradeStatus.NOT_TEST.getValue());
                clientList = recordClientMapper.getUpgradeSqlPatch(patchDTO);
            }
            if ("生产".equals(server.getServerType())) {
                patchDTO.setServerType("生产");
                patchDTO.setPatchStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
                clientList = recordClientMapper.getUpgradeSqlPatch(patchDTO);
            }
        }
        return clientList;
    }
}
