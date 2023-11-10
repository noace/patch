package com.kyee.upgrade.service;

import java.util.List;

import com.kyee.upgrade.domain.UpPatchClient;
import com.kyee.upgrade.domain.UpProjectServer;
import com.kyee.upgrade.domain.UpUpgradeLogClient;
import com.kyee.upgrade.domain.UpUpgradeRecordClient;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.kyee.upgrade.domain.vo.UpUpgradeDataDTO;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 升级管理Service接口
 * 
 * @author zhanghaoyu
 * @date 2021-10-21
 */
public interface IUpUpgradeRecordClientService 
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
     * 查询升级管理列表(新)
     *
     * @param upProjectServer 升级管理
     * @return 升级管理集合
     */
    public List<UpProjectServer> selectUpUpgradeRecordClientList(UpProjectServer upProjectServer) throws Exception;

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
     * 批量删除升级管理
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpUpgradeRecordClientByIds(String ids);

    /**
     * 删除升级管理信息
     * 
     * @param upgradeId 升级管理ID
     * @return 结果
     */
    public int deleteUpUpgradeRecordClientById(Long upgradeId);

    /**
     * 查询服务器包列表
     * @param dataVo
     * @return
     */
    List<UpPatchClientExtend> getUpgradePatchList(UpUpgradeDataDTO dataVo);

    /**
     * 查询历史服务器包列表
     * @param dataVo
     * @return
     */
    List<UpPatchClientExtend> getHistoryUpgradePatchList(UpUpgradeDataDTO dataVo);

    /**
     * 启动tomcat服务
     * @param serverId 服务Id
     */
    AjaxResult startServer(Integer serverId, Integer projectId, Integer productId) throws Exception;

    /**
     * 停止tomcat服务
     * @param serverId 服务Id
     */
    AjaxResult stopServer(Integer serverId);

    /**
     * 向目标服务器上传补丁包
     * @param patchId 补丁Id
     * @param serverId 服务Id
     * @param serverType 服务类型
     */
    AjaxResult sendPatch(Long patchId, Integer serverId, String serverType);

    /**
     * 查看升级日志
     * @param patchId 补丁Id
     * @param serverId 服务Id
     */
    List<UpUpgradeLogClient> getServerLogs(Long patchId, Integer serverId);

    /** 升级回退
     * @param patchId 补丁包Id
     * @return serverId 服务Id
     */
    AjaxResult rollbackPatch(Long patchId, Integer serverId);

    /** 手动升级
     * @param patchId 补丁包Id
     * @return serverId 服务Id
     */
    AjaxResult handUpgrade(Long patchId, Integer serverId);

    /**
     * 记录升级记录
     * @param patchId
     * @param upStatus
     * @param server
     * @param patchClient
     */
    void recordUpgrade(Long patchId, String upStatus, UpProjectServer server, UpPatchClient patchClient);

    /**
     * 获取服务器状态
     * @param serverId
     */
    UpProjectServer getServerStatus(Integer serverId);

    /**
     * 升级时校验重复文件
     * @param patchId
     * @return
     */
    String validateUpgrade(Long patchId, String serverType, Integer serverId);

}
