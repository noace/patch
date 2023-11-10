package com.kyee.upgrade.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpProjectPersonMapper;
import com.kyee.upgrade.domain.UpProjectPerson;
import com.kyee.upgrade.service.IUpProjectPersonService;
import com.ruoyi.common.core.text.Convert;

/**
 * 项目人员Service业务层处理
 * 
 * @author lijunqiang
 * @date 2022-03-16
 */
@Service
public class UpProjectPersonServiceImpl implements IUpProjectPersonService 
{
    @Autowired
    private UpProjectPersonMapper upProjectPersonMapper;

    /**
     * 查询项目人员
     * 
     * @param projectProductId 项目人员ID
     * @return 项目人员
     */
    @Override
    public List<UpProjectPerson> selectUpProjectPersonById(Integer projectProductId)
    {
        return upProjectPersonMapper.selectUpProjectPersonById(projectProductId);
    }

    /**
     * 查询项目人员
     *
     * @param userId 项目人员ID
     * @return 项目人员
     */
    @Override
    public List<UpProjectPerson> selectUpProjectPersonByUserId(Long userId)
    {
        return upProjectPersonMapper.selectUpProjectPersonByUserId(userId);
    }

    /**
     * 查询项目人员列表
     * 
     * @param upProjectPerson 项目人员
     * @return 项目人员
     */
    @Override
    public List<UpProjectPerson> selectUpProjectPersonList(UpProjectPerson upProjectPerson)
    {
        return upProjectPersonMapper.selectUpProjectPersonList(upProjectPerson);
    }

    /**
     * 新增项目人员
     * 
     * @param upProjectPerson 项目人员
     * @return 结果
     */
    @Override
    public int insertUpProjectPerson(UpProjectPerson upProjectPerson)
    {
        upProjectPerson.setCreateTime(DateUtils.getNowDate());
        return upProjectPersonMapper.insertUpProjectPerson(upProjectPerson);
    }

    /**
     * 修改项目人员
     * 
     * @param upProjectPerson 项目人员
     * @return 结果
     */
    @Override
    public int updateUpProjectPerson(UpProjectPerson upProjectPerson)
    {
        upProjectPerson.setUpdateTime(DateUtils.getNowDate());
        return upProjectPersonMapper.updateUpProjectPerson(upProjectPerson);
    }

    /**
     * 删除项目人员对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectPersonByIds(String ids)
    {
        return upProjectPersonMapper.deleteUpProjectPersonByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除项目人员信息
     * 
     * @param projectProductId 项目人员ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectPersonById(Integer projectProductId)
    {
        return upProjectPersonMapper.deleteUpProjectPersonById(projectProductId);
    }
}
