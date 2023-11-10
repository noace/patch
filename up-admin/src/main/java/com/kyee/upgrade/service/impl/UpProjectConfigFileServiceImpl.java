package com.kyee.upgrade.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import com.kyee.upgrade.domain.UpProduct;
import com.kyee.upgrade.domain.UpProject;
import com.kyee.upgrade.mapper.UpProductMapper;
import com.kyee.upgrade.mapper.UpProjectMapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpProjectConfigFileMapper;
import com.kyee.upgrade.domain.UpProjectConfigFile;
import com.kyee.upgrade.service.IUpProjectConfigFileService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * 项目产品Service业务层处理
 * 
 * @author lijunqiang
 * @date 2022-06-25
 */
@Service
public class UpProjectConfigFileServiceImpl implements IUpProjectConfigFileService 
{
    @Autowired
    private UpProjectConfigFileMapper upProjectConfigFileMapper;

    @Autowired
    private UpProjectMapper upProjectMapper;

    @Autowired
    private UpProductMapper upProductMapper;

    /**
     * 查询项目产品
     * 
     * @param projectProductId 项目产品ID
     * @return 项目产品
     */
    @Override
    public UpProjectConfigFile selectUpProjectConfigFileById(Integer projectProductId)
    {
        return upProjectConfigFileMapper.selectUpProjectConfigFileById(projectProductId);
    }

    /**
     * 查询项目产品列表
     * 
     * @param upProjectConfigFile 项目产品
     * @return 项目产品
     */
    @Override
    public List<UpProjectConfigFile> selectUpProjectConfigFileList(UpProjectConfigFile upProjectConfigFile)
    {
        List<UpProjectConfigFile> configFiles = upProjectConfigFileMapper.selectUpProjectConfigFileList(upProjectConfigFile);
        //获取project信息
        List<UpProject> projectList = upProjectMapper.selectUpProjectList(new UpProject());
        Map<Integer, String> projectMap = projectList.stream().collect(Collectors.toMap(UpProject::getProjectId, UpProject::getProjectName));
        //获取product信息
        List<UpProduct> productList = upProductMapper.selectUpProductList(new UpProduct());
        Map<Integer, String> productMap = productList.stream().collect(Collectors.toMap(UpProduct::getProductId, UpProduct::getProductName));
        if (!CollectionUtils.isEmpty(configFiles)) {
            for (UpProjectConfigFile configFile : configFiles) {
                configFile.setProductName(productMap.get(configFile.getProductId()));
                configFile.setProjectName(projectMap.get(configFile.getProjectId()));
            }
        }
        return configFiles;
    }

    /**
     * 新增项目产品
     * 
     * @param upProjectConfigFile 项目产品
     * @return 结果
     */
    @Override
    public int insertUpProjectConfigFile(UpProjectConfigFile upProjectConfigFile) throws RuntimeException
    {
        //获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();

        upProjectConfigFile.setCreateTime(DateUtils.getNowDate());
        upProjectConfigFile.setCreateBy(sysUser.getUserName());
        // 校验配置文件名是否有重复
        List<UpProjectConfigFile> configFileList = upProjectConfigFileMapper.selectConfigByProductIdAndProjectId(upProjectConfigFile);
        if (configFileList.size() > 0) {
            throw new RuntimeException("该项目产品中配置文件已存在，请重新选择");
        }

        return upProjectConfigFileMapper.insertUpProjectConfigFile(upProjectConfigFile);
    }

    /**
     * 修改项目产品
     * 
     * @param upProjectConfigFile 项目产品
     * @return 结果
     */
    @Override
    public int updateUpProjectConfigFile(UpProjectConfigFile upProjectConfigFile) throws RuntimeException
    {
        upProjectConfigFile.setUpdateTime(DateUtils.getNowDate());
        // 校验配置文件名是否有重复
        List<UpProjectConfigFile> configFileList = upProjectConfigFileMapper.selectConfigByProductIdAndProjectId(upProjectConfigFile);
        if (configFileList.size() > 0) {
            throw new RuntimeException("该项目产品中配置文件已存在，请重新选择");
        }
        return upProjectConfigFileMapper.updateUpProjectConfigFile(upProjectConfigFile);
    }

    /**
     * 删除项目产品对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectConfigFileByIds(String ids)
    {
        return upProjectConfigFileMapper.deleteUpProjectConfigFileByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除项目产品信息
     * 
     * @param projectProductId 项目产品ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectConfigFileById(Integer projectProductId)
    {
        return upProjectConfigFileMapper.deleteUpProjectConfigFileById(projectProductId);
    }
}
