package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpUpgradeLogClient;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 升级日志Mapper接口
 * 
 * @author lijunqiang
 * @date 2022-04-24
 */
@Mapper
@Repository
public interface UpUpgradeLogClientMapper 
{
    /**
     * 查询升级日志
     * 
     * @param logId 升级日志ID
     * @return 升级日志
     */
    UpUpgradeLogClient selectUpUpgradeLogClientById(Long logId);

    /**
     * 查询升级日志列表
     * 
     * @param upUpgradeLogClient 升级日志
     * @return 升级日志集合
     */
    List<UpUpgradeLogClient> selectUpUpgradeLogClientList(UpUpgradeLogClient upUpgradeLogClient);

    /**
     * 新增升级日志
     * 
     * @param upUpgradeLogClient 升级日志
     * @return 结果
     */
    int insertUpUpgradeLogClient(UpUpgradeLogClient upUpgradeLogClient);

    /**
     * 修改升级日志
     * 
     * @param upUpgradeLogClient 升级日志
     * @return 结果
     */
    int updateUpUpgradeLogClient(UpUpgradeLogClient upUpgradeLogClient);

    /**
     * 删除升级日志
     * 
     * @param logId 升级日志ID
     * @return 结果
     */
    int deleteUpUpgradeLogClientById(Long logId);

    /**
     * 批量删除升级日志
     * 
     * @param logIds 需要删除的数据ID
     * @return 结果
     */
    int deleteUpUpgradeLogClientByIds(String[] logIds);

}
