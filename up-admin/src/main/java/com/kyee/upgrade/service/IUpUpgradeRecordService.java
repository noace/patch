package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpUpgradeRecord;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 升级管理Service接口
 * 
 * @author lijunqiang
 * @date 2021-06-13
 */
public interface IUpUpgradeRecordService 
{
    /**
     * 查询升级管理
     * 
     * @param upgradeId 升级管理ID
     * @return 升级管理
     */
    public UpUpgradeRecord selectUpUpgradeRecordById(Long upgradeId);

    /**
     * 查询升级管理列表
     * 
     * @param upUpgradeRecord 升级管理
     * @return 升级管理集合
     */
    public List<UpUpgradeRecord> selectUpUpgradeRecordList(UpUpgradeRecord upUpgradeRecord);

    /**
     * 新增升级管理
     * 
     * @param upUpgradeRecord 升级管理
     * @return 结果
     */
    public int insertUpUpgradeRecord(UpUpgradeRecord upUpgradeRecord);

    /**
     * 修改升级管理
     * 
     * @param upUpgradeRecord 升级管理
     * @return 结果
     */
    public int updateUpUpgradeRecord(UpUpgradeRecord upUpgradeRecord);

    /**
     * 批量删除升级管理
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpUpgradeRecordByIds(String ids);

    /**
     * 删除升级管理信息
     * 
     * @param upgradeId 升级管理ID
     * @return 结果
     */
    public int deleteUpUpgradeRecordById(Long upgradeId);
}
