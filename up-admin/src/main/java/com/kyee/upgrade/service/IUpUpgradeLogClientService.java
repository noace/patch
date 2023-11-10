package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpUpgradeLogClient;

/**
 * 升级日志Service接口
 * 
 * @author lijunqiang
 * @date 2022-04-24
 */
public interface IUpUpgradeLogClientService 
{
    /**
     * 查询升级日志
     * 
     * @param logId 升级日志ID
     * @return 升级日志
     */
    public UpUpgradeLogClient selectUpUpgradeLogClientById(Long logId);

    /**
     * 查询升级日志列表
     * 
     * @param upUpgradeLogClient 升级日志
     * @return 升级日志集合
     */
    public List<UpUpgradeLogClient> selectUpUpgradeLogClientList(UpUpgradeLogClient upUpgradeLogClient);

    /**
     * 新增升级日志
     * 
     * @param upUpgradeLogClient 升级日志
     * @return 结果
     */
    public int insertUpUpgradeLogClient(UpUpgradeLogClient upUpgradeLogClient);

    /**
     * 修改升级日志
     * 
     * @param upUpgradeLogClient 升级日志
     * @return 结果
     */
    public int updateUpUpgradeLogClient(UpUpgradeLogClient upUpgradeLogClient);

    /**
     * 批量删除升级日志
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpUpgradeLogClientByIds(String ids);

    /**
     * 删除升级日志信息
     * 
     * @param logId 升级日志ID
     * @return 结果
     */
    public int deleteUpUpgradeLogClientById(Long logId);
}
