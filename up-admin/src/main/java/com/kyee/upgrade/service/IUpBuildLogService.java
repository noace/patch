package com.kyee.upgrade.service;

import java.util.List;
import java.util.Map;

import com.kyee.upgrade.domain.UpBuildLog;

/**
 * 构建日志Service接口
 *
 * @author lijunqiang
 * @date 2021-10-20
 */
public interface IUpBuildLogService
{
    /**
     * 查询构建日志
     *
     * @param buildId 构建日志ID
     * @return 构建日志
     */
    public UpBuildLog selectUpBuildLogById(Long buildId);

    /**
     * 查询构建日志列表
     *
     * @param upBuildLog 构建日志
     * @return 构建日志集合
     */
    public List<UpBuildLog> selectUpBuildLogList(UpBuildLog upBuildLog);

    /**
     * 新增构建日志
     *
     * @param upBuildLog 构建日志
     * @return 结果
     */
    public int insertUpBuildLog(UpBuildLog upBuildLog);

    /**
     * 修改构建日志
     *
     * @param upBuildLog 构建日志
     * @return 结果
     */
    public int updateUpBuildLog(UpBuildLog upBuildLog);

    /**
     * 批量删除构建日志
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpBuildLogByIds(String ids);

    /**
     * 删除构建日志信息
     *
     * @param buildId 构建日志ID
     * @return 结果
     */
    public int deleteUpBuildLogById(Long buildId);

    /**
     * 根据补丁ID查询构建日志信息
     *
     * @param patchId 构建日志ID
     * @return List<UpBuildLog> 结果
     */
    List<UpBuildLog> getBuildLogsByPatchId(Long patchId);
}
