package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpWorkspaceSource;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 工作空间Mapper接口
 * 
 * @author lijunqiang
 * @date 2021-10-18
 */
@Mapper
@Repository
public interface UpWorkspaceSourceMapper 
{
    /**
     * 查询工作空间
     * 
     * @param sourceId 工作空间ID
     * @return 工作空间
     */
    public UpWorkspaceSource selectUpWorkspaceSourceById(Long sourceId);

    /**
     * 根据产品id查询未占用的工作空间列表，按创建时间排序取第一行
     *
     * @param productId
     * @return 工作空间集合
     */
    public UpWorkspaceSource selectUpWorkspaceSourceOld(Integer productId);
    
    /**
     * 查询工作空间列表
     * 
     * @param upWorkspaceSource 工作空间
     * @return 工作空间集合
     */
    public List<UpWorkspaceSource> selectUpWorkspaceSourceList(UpWorkspaceSource upWorkspaceSource);

    /**
     * 新增工作空间
     * 
     * @param upWorkspaceSource 工作空间
     * @return 结果
     */
    public int insertUpWorkspaceSource(UpWorkspaceSource upWorkspaceSource);

    /**
     * 修改工作空间
     * 
     * @param upWorkspaceSource 工作空间
     * @return 结果
     */
    public int updateUpWorkspaceSource(UpWorkspaceSource upWorkspaceSource);

    /**
     * 删除工作空间
     * 
     * @param sourceId 工作空间ID
     * @return 结果
     */
    public int deleteUpWorkspaceSourceById(Long sourceId);

    /**
     * 批量删除工作空间
     * 
     * @param sourceIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpWorkspaceSourceByIds(String[] sourceIds);
}
