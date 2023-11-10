package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpUpgradeRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 升级管理Mapper接口
 * 
 * @author lijunqiang
 * @date 2021-06-13
 */
@Mapper
public interface UpUpgradeRecordMapper 
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
     * 删除升级管理
     * 
     * @param upgradeId 升级管理ID
     * @return 结果
     */
    public int deleteUpUpgradeRecordById(Long upgradeId);

    /**
     * 批量删除升级管理
     * 
     * @param upgradeIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpUpgradeRecordByIds(String[] upgradeIds);
}
