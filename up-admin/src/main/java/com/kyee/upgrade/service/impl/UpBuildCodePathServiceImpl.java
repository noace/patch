package com.kyee.upgrade.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpBuildCodePathMapper;
import com.kyee.upgrade.domain.UpBuildCodePath;
import com.kyee.upgrade.service.IUpBuildCodePathService;
import com.ruoyi.common.core.text.Convert;

/**
 * 代码变更路径Service业务层处理
 * 
 * @author lijunqiang
 * @date 2021-10-22
 */
@Service
public class UpBuildCodePathServiceImpl implements IUpBuildCodePathService 
{
    @Autowired
    private UpBuildCodePathMapper upBuildCodePathMapper;

    /**
     * 查询代码变更路径
     * 
     * @param codePathId 代码变更路径ID
     * @return 代码变更路径
     */
    @Override
    public UpBuildCodePath selectUpBuildCodePathById(Long codePathId)
    {
        return upBuildCodePathMapper.selectUpBuildCodePathById(codePathId);
    }

    /**
     * 查询代码变更路径列表
     * 
     * @param upBuildCodePath 代码变更路径
     * @return 代码变更路径
     */
    @Override
    public List<UpBuildCodePath> selectUpBuildCodePathList(UpBuildCodePath upBuildCodePath)
    {
        return upBuildCodePathMapper.selectUpBuildCodePathList(upBuildCodePath);
    }

    /**
     * 新增代码变更路径
     * 
     * @param upBuildCodePath 代码变更路径
     * @return 结果
     */
    @Override
    public int insertUpBuildCodePath(UpBuildCodePath upBuildCodePath)
    {
        return upBuildCodePathMapper.insertUpBuildCodePath(upBuildCodePath);
    }

    /**
     * 修改代码变更路径
     * 
     * @param upBuildCodePath 代码变更路径
     * @return 结果
     */
    @Override
    public int updateUpBuildCodePath(UpBuildCodePath upBuildCodePath)
    {
        return upBuildCodePathMapper.updateUpBuildCodePath(upBuildCodePath);
    }

    /**
     * 删除代码变更路径对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpBuildCodePathByIds(String ids)
    {
        return upBuildCodePathMapper.deleteUpBuildCodePathByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除代码变更路径信息
     * 
     * @param codePathId 代码变更路径ID
     * @return 结果
     */
    @Override
    public int deleteUpBuildCodePathById(Long codePathId)
    {
        return upBuildCodePathMapper.deleteUpBuildCodePathById(codePathId);
    }
}
