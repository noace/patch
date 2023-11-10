package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpProjectServer;
import com.kyee.upgrade.domain.UpUpgradeLogClient;
import com.kyee.upgrade.domain.vo.UpUpgradeDataDTO;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目应用服务Service接口
 *
 * @author lijunqiang
 * @date 2022-04-12
 */
public interface IUpProjectServerService
{
    /**
     * 查询项目应用服务
     *
     * @param serverId 项目应用服务ID
     * @return 项目应用服务
     */
    public UpProjectServer selectUpProjectServerById(Integer serverId);

    /**
     * 查询项目应用服务列表
     *
     * @param upProjectServer 项目应用服务
     * @return 项目应用服务集合
     */
    public List<UpProjectServer> selectUpProjectServerList(UpProjectServer upProjectServer);

    /**
     * 新增项目应用服务
     *
     * @param upProjectServer 项目应用服务
     * @return 结果
     */
    public int insertUpProjectServer(UpProjectServer upProjectServer);

    /**
     * 修改项目应用服务
     *
     * @param upProjectServer 项目应用服务
     * @return 结果
     */
    public int updateUpProjectServer(UpProjectServer upProjectServer);

    /**
     * 批量删除项目应用服务
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpProjectServerByIds(String ids);

    /**
     * 删除项目应用服务信息
     *
     * @param serverId 项目应用服务ID
     * @return 结果
     */
    public int deleteUpProjectServerById(Integer serverId);

    /**
     * 查询服务器列表
     * @param patchId 补丁Id
     */
    List<UpProjectServer> getUpgradeServers(Long patchId,UpProjectServer upProjectServer);

    /**
     * 启动tomcat服务
     * @param patchId 补丁Id
     * @param serverId 服务Id
     */
    AjaxResult startServer(Long patchId, Integer serverId);

    /**
     * 停止tomcat服务
     * @param patchId 补丁Id
     * @param serverId 服务Id
     */
    AjaxResult stopServer(Long patchId, Integer serverId);

    /**
     * 向目标服务器上传补丁包
     * @param patchId 补丁Id
     * @param serverId 服务Id
     */
    AjaxResult sendPatch(Long patchId, Integer serverId);

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
     * 查询数据库服务信息
     *
     * @return 结果
     */
    List<UpUpgradeDataDTO> getDatabaseList(UpProjectServer server);

//    /**
//     * 升级记录表
//     * @param patchId
//     * @param serverId
//     * @param server
//     */
//    void recordUpgrade(Long patchId, Integer serverId, UpProjectServer server);
}
