package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpBuildLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 构建日志Mapper接口
 *
 * @author lijunqiang
 * @date 2021-10-20
 */
@Mapper
@Repository
public interface UpBuildLogMapper
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
     * 删除构建日志
     *
     * @param buildId 构建日志ID
     * @return 结果
     */
    public int deleteUpBuildLogById(Long buildId);

    /**
     * 批量删除构建日志
     *
     * @param buildIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpBuildLogByIds(Long[] buildIds);

    /**
     * 根据补丁ID查询构建日志信息
     *
     * @param patchId 构建日志ID
     * @return List<UpBuildLog> 结果
     */
    List<UpBuildLog> getBuildLogsByPatchId(Long patchId);

    /**
     * 批量删除构建日志(真删)
     *
     * @param buildIds 需要删除的数据ID
     * @return 结果
     */
    int deleteLogByIds(@Param("buildIds") List<Long> buildIds);
}
