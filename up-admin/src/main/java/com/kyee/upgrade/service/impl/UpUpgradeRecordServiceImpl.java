package com.kyee.upgrade.service.impl;

import java.util.List;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpUpgradeRecordMapper;
import com.kyee.upgrade.domain.UpUpgradeRecord;
import com.kyee.upgrade.service.IUpUpgradeRecordService;
import com.ruoyi.common.core.text.Convert;

/**
 * 升级管理Service业务层处理
 * 
 * @author lijunqiang
 * @date 2021-06-13
 */
@Service
public class UpUpgradeRecordServiceImpl implements IUpUpgradeRecordService
{
    @Autowired
    private UpUpgradeRecordMapper upUpgradeRecordMapper;

    /**
     * 查询升级管理
     * 
     * @param upgradeId 升级管理ID
     * @return 升级管理
     */
    @Override
    public UpUpgradeRecord selectUpUpgradeRecordById(Long upgradeId)
    {
        return upUpgradeRecordMapper.selectUpUpgradeRecordById(upgradeId);
    }

    /**
     * 查询升级管理列表
     * 
     * @param upUpgradeRecord 升级管理
     * @return 升级管理
     */
    @Override
    public List<UpUpgradeRecord> selectUpUpgradeRecordList(UpUpgradeRecord upUpgradeRecord)
    {
        return upUpgradeRecordMapper.selectUpUpgradeRecordList(upUpgradeRecord);
    }

    /**
     * 新增升级管理
     * 
     * @param upUpgradeRecord 升级管理
     * @return 结果
     */
    @Override
    public int insertUpUpgradeRecord(UpUpgradeRecord upUpgradeRecord)
    {
        upUpgradeRecord.setCreateTime(DateUtils.getNowDate());
        return upUpgradeRecordMapper.insertUpUpgradeRecord(upUpgradeRecord);
    }

    /**
     * 修改升级管理
     * 
     * @param upUpgradeRecord 升级管理
     * @return 结果
     */
    @Override
    public int updateUpUpgradeRecord(UpUpgradeRecord upUpgradeRecord)
    {
        upUpgradeRecord.setUpdateTime(DateUtils.getNowDate());
        return upUpgradeRecordMapper.updateUpUpgradeRecord(upUpgradeRecord);
    }

    /**
     * 删除升级管理对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpUpgradeRecordByIds(String ids)
    {
        return upUpgradeRecordMapper.deleteUpUpgradeRecordByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除升级管理信息
     * 
     * @param upgradeId 升级管理ID
     * @return 结果
     */
    @Override
    public int deleteUpUpgradeRecordById(Long upgradeId)
    {
        return upUpgradeRecordMapper.deleteUpUpgradeRecordById(upgradeId);
    }
}
