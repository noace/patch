package com.kyee.upgrade.mapper;

import java.util.List;

import com.kyee.upgrade.domain.UpPatchClient;
import com.kyee.upgrade.domain.UpUpgradeRecordClient;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.kyee.upgrade.domain.vo.UpUpgradeDataDTO;
import com.kyee.upgrade.domain.vo.UpUpgradePatchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 升级管理Mapper接口
 * 
 * @author zhanghaoyu
 * @date 2021-10-21
 */
@Mapper
@Repository
public interface UpUpgradeRecordClientMapper 
{
    /**
     * 查询升级管理
     * 
     * @param upgradeId 升级管理ID
     * @return 升级管理
     */
    public UpUpgradeRecordClient selectUpUpgradeRecordClientById(Long upgradeId);

    /**
     * 查询升级管理列表
     * 
     * @param upUpgradeRecordClient 升级管理
     * @return 升级管理集合
     */
    public List<UpUpgradeRecordClient> selectUpUpgradeRecordClientList(UpUpgradeRecordClient upUpgradeRecordClient);

    /**
     * 查询补丁包升级记录
     * @param patchId
     * @param serverIds
     * @return
     */
    List<UpUpgradeRecordClient> getUpgradeRecordClientList(@Param("patchId") Long patchId, @Param("serverIds") List<Integer> serverIds);

    /**
     * 查询服务器最后升级记录
     * @param patchDTO
     * @return
     */
    List<UpUpgradeRecordClient> getLastUpgradeRecordList(UpUpgradeDataDTO patchDTO);

    /**
     * 查询补丁包升级记录
     * @param patchId
     * @param serverId
     * @return
     */
    UpUpgradeRecordClient getUpgradeRecordClient(@Param("patchId") Long patchId, @Param("serverId") Integer serverId);

    /**
     * 新增升级管理
     * 
     * @param upUpgradeRecordClient 升级管理
     * @return 结果
     */
    public int insertUpUpgradeRecordClient(UpUpgradeRecordClient upUpgradeRecordClient);

    /**
     * 修改升级管理
     * 
     * @param upUpgradeRecordClient 升级管理
     * @return 结果
     */
    public int updateUpUpgradeRecordClient(UpUpgradeRecordClient upUpgradeRecordClient);

    /**
     * 删除升级管理
     * 
     * @param patchId 升级管理ID
     * @return 结果
     */
    public int deleteUpUpgradeRecordClientById(Long patchId);

    /**
     * 批量删除升级管理
     * 
     * @param patchIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpUpgradeRecordClientByIds(String[] patchIds);

    /**
     * 查询执行脚本补丁包列表
     * @param patchDTO
     * @return
     */
    List<UpPatchClientExtend> getUpgradeSqlList(UpUpgradePatchDTO patchDTO);

    /**
     * 查询SQL升级记录
     * @param patchDTO
     * @return
     */
    List<UpPatchClientExtend> getUpgradeSqlPatch(UpUpgradePatchDTO patchDTO);

    /**
     * 查询补丁包升级记录
     * @param patchDTO
     * @return
     */
    List<UpPatchClientExtend> getUpgradeRecordPatch(UpUpgradeDataDTO patchDTO);

    /**
     * 部分升级的且升级记录状态为回退成功、升级失败、回退失败）
     * @param patchDTO
     * @return
     */
    List<UpPatchClientExtend> getPartUpgradePatch(UpUpgradeDataDTO patchDTO);

    /**
     * 获取启动服务待修改状态的补丁包
     * @param patchDTO
     * @return
     */
    List<UpPatchClientExtend> getStartServerPatch(UpUpgradeDataDTO patchDTO);

}
