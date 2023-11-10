package com.kyee.upgrade.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpUpgradeLogClientMapper;
import com.kyee.upgrade.domain.UpUpgradeLogClient;
import com.kyee.upgrade.service.IUpUpgradeLogClientService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.transaction.annotation.Transactional;

/**
 * 升级日志Service业务层处理
 * 
 * @author lijunqiang
 * @date 2022-04-24
 */
@Service
@Transactional
public class UpUpgradeLogClientServiceImpl implements IUpUpgradeLogClientService 
{
    @Autowired
    private UpUpgradeLogClientMapper upUpgradeLogClientMapper;

    /**
     * 查询升级日志
     * 
     * @param logId 升级日志ID
     * @return 升级日志
     */
    @Override
    public UpUpgradeLogClient selectUpUpgradeLogClientById(Long logId)
    {
        return upUpgradeLogClientMapper.selectUpUpgradeLogClientById(logId);
    }

    /**
     * 查询升级日志列表
     * 
     * @param upUpgradeLogClient 升级日志
     * @return 升级日志
     */
    @Override
    public List<UpUpgradeLogClient> selectUpUpgradeLogClientList(UpUpgradeLogClient upUpgradeLogClient)
    {
        return upUpgradeLogClientMapper.selectUpUpgradeLogClientList(upUpgradeLogClient);
    }

    /**
     * 新增升级日志
     * 
     * @param upUpgradeLogClient 升级日志
     * @return 结果
     */
    @Override
    public int insertUpUpgradeLogClient(UpUpgradeLogClient upUpgradeLogClient)
    {
        upUpgradeLogClient.setCreateTime(DateUtils.getNowDate());
        return upUpgradeLogClientMapper.insertUpUpgradeLogClient(upUpgradeLogClient);
    }

    /**
     * 修改升级日志
     * 
     * @param upUpgradeLogClient 升级日志
     * @return 结果
     */
    @Override
    public int updateUpUpgradeLogClient(UpUpgradeLogClient upUpgradeLogClient)
    {
        upUpgradeLogClient.setUpdateTime(DateUtils.getNowDate());
        return upUpgradeLogClientMapper.updateUpUpgradeLogClient(upUpgradeLogClient);
    }

    /**
     * 删除升级日志对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpUpgradeLogClientByIds(String ids)
    {
        return upUpgradeLogClientMapper.deleteUpUpgradeLogClientByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除升级日志信息
     * 
     * @param logId 升级日志ID
     * @return 结果
     */
    @Override
    public int deleteUpUpgradeLogClientById(Long logId)
    {
        return upUpgradeLogClientMapper.deleteUpUpgradeLogClientById(logId);
    }
}
