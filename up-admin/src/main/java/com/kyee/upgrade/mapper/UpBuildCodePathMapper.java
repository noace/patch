package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpBuildCodePath;
import org.apache.ibatis.annotations.Param;

/**
 * 代码变更路径Mapper接口
 * 
 * @author lijunqiang
 * @date 2021-10-22
 */
public interface UpBuildCodePathMapper 
{
    /**
     * 查询代码变更路径
     * 
     * @param codePathId 代码变更路径ID
     * @return 代码变更路径
     */
    public UpBuildCodePath selectUpBuildCodePathById(Long codePathId);

    /**
     * 查询代码变更路径列表
     * 
     * @param upBuildCodePath 代码变更路径
     * @return 代码变更路径集合
     */
    public List<UpBuildCodePath> selectUpBuildCodePathList(UpBuildCodePath upBuildCodePath);

    /**
     * 新增代码变更路径
     * 
     * @param upBuildCodePath 代码变更路径
     * @return 结果
     */
    public int insertUpBuildCodePath(UpBuildCodePath upBuildCodePath);

    /**
     * 修改代码变更路径
     * 
     * @param upBuildCodePath 代码变更路径
     * @return 结果
     */
    public int updateUpBuildCodePath(UpBuildCodePath upBuildCodePath);

    /**
     * 删除代码变更路径
     * 
     * @param codePathId 代码变更路径ID
     * @return 结果
     */
    public int deleteUpBuildCodePathById(Long codePathId);

    /**
     * 批量删除代码变更路径
     * 
     * @param codePathIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpBuildCodePathByIds(String[] codePathIds);

    /**
     * 批量新增代码变更路径
     * @param upBuildCodePathList
     */
    void batchInsertUpBuildCodePath(@Param("upBuildCodePathList") List<UpBuildCodePath> upBuildCodePathList);
}
