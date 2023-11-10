package com.kyee.upgrade.mapper;

import java.util.List;
import java.util.Map;

import com.kyee.upgrade.domain.UpProjectServer;
import com.kyee.upgrade.domain.UpUpgradeRecordClient;
import com.kyee.upgrade.domain.vo.UpUpgradeDataDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 项目应用服务Mapper接口
 * 
 * @author lijunqiang
 * @date 2022-04-12
 */
@Mapper
@Repository
public interface UpProjectServerMapper 
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
     *无升级记录
     * @return
     */
    public Map selectUnUpgrade(@Param("projectServer") UpProjectServer projectServer, @Param("testStatus") List testStatus, @Param("patchStatus") List patchStatus);

    /**
     *无升级记录
     * @return
     */
    Map<String, Object> selectUnExecute(@Param("projectServer") UpUpgradeDataDTO projectServer, @Param("testStatus") String testStatus,
                                                                                @Param("patchStatus") String patchStatus);

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
     * 删除项目应用服务
     * 
     * @param serverId 项目应用服务ID
     * @return 结果
     */
    public int deleteUpProjectServerById(Integer serverId);

    /**
     * 批量删除项目应用服务
     * 
     * @param serverIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpProjectServerByIds(String[] serverIds);


    /**
     * 查询数据库服务列表
     * @return
     */
    List<UpUpgradeDataDTO> getDatabaseList(UpProjectServer server);

    /**
     * 查询补丁包升级记录数据
     * @return
     */
    List<UpUpgradeRecordClient> getUpgradeDataByServerId(Integer serverId);


    /**
     * 修改数据库服务
     *
     * @param dataDTO 数据库服务
     * @return 结果
     */
    int updateDatabaseServer(UpUpgradeDataDTO dataDTO);

}
