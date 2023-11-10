package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpBuildCodeChanges;
import org.apache.ibatis.annotations.Param;

/**
 * 构建日志Mapper接口
 * 
 * @author lijunqiang
 * @date 2021-10-21
 */
public interface UpBuildCodeChangesMapper 
{
    /**
     * 查询构建日志
     * 
     * @param codeChangeId 构建日志ID
     * @return 构建日志
     */
    public UpBuildCodeChanges selectUpBuildCodeChangesById(Long codeChangeId);

    /**
     * 查询构建日志列表
     * 
     * @param upBuildCodeChanges 构建日志
     * @return 构建日志集合
     */
    public List<UpBuildCodeChanges> selectUpBuildCodeChangesList(UpBuildCodeChanges upBuildCodeChanges);

    /**
     * 新增构建日志
     * 
     * @param upBuildCodeChanges 构建日志
     * @return 结果
     */
    public int insertUpBuildCodeChanges(UpBuildCodeChanges upBuildCodeChanges);

    /**
     * 修改构建日志
     * 
     * @param upBuildCodeChanges 构建日志
     * @return 结果
     */
    public int updateUpBuildCodeChanges(UpBuildCodeChanges upBuildCodeChanges);

    /**
     * 删除构建日志
     * 
     * @param codeChangeId 构建日志ID
     * @return 结果
     */
    public int deleteUpBuildCodeChangesById(Long codeChangeId);

    /**
     * 批量删除构建日志
     * 
     * @param codeChangeIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpBuildCodeChangesByIds(String[] codeChangeIds);

    /**
     * 批量新增构建日志
     *
     * @param codeChangesList 构建日志
     * @return 结果
     */
    int batchInsertUpBuildCodeChanges(@Param("codeChangesList") List<UpBuildCodeChanges> codeChangesList);
}
