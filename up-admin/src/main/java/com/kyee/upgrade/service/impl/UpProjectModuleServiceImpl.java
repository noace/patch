package com.kyee.upgrade.service.impl;

import com.kyee.upgrade.domain.UpProduct;
import com.kyee.upgrade.domain.UpProject;
import com.kyee.upgrade.domain.UpProjectModule;
import com.kyee.upgrade.mapper.UpProductMapper;
import com.kyee.upgrade.mapper.UpProjectModuleMapper;
import com.kyee.upgrade.mapper.UpProjectMapper;
import com.kyee.upgrade.service.IUpProjectModuleService;
import com.kyee.upgrade.service.IUpProjectModuleService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 项目产品Service业务层处理
 * 
 * @author lijunqiang
 * @date 2022-06-25
 */
@Service
public class UpProjectModuleServiceImpl implements IUpProjectModuleService
{
    @Autowired
    private UpProjectModuleMapper upProjectModuleMapper;

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
    public UpProjectModule selectUpProjectModuleById(Integer projectProductId)
    {
        return upProjectModuleMapper.selectUpProjectModuleById(projectProductId);
    }

    /**
     * 查询项目产品列表
     * 
     * @param upProjectModule 项目产品
     * @return 项目产品
     */
    @Override
    public List<UpProjectModule> selectUpProjectModuleList(UpProjectModule upProjectModule)
    {

        List<UpProjectModule> configFiles = upProjectModuleMapper.selectUpProjectModuleList(upProjectModule);
        //获取project信息
        List<UpProject> projectList = upProjectMapper.selectUpProjectList(new UpProject());
        Map<Integer, String> projectMap = projectList.stream().collect(Collectors.toMap(UpProject::getProjectId, UpProject::getProjectName));
        //获取product信息
        List<UpProduct> productList = upProductMapper.selectUpProductList(new UpProduct());
        Map<Integer, String> productMap = productList.stream().collect(Collectors.toMap(UpProduct::getProductId, UpProduct::getProductName));

        if (!CollectionUtils.isEmpty(configFiles)) {
            for (UpProjectModule configFile : configFiles) {
                configFile.setProductName(productMap.get(configFile.getProductId()));
                configFile.setProjectName(projectMap.get(configFile.getProjectId()));
            }
        }
        return configFiles;
    }

    /**
     * 新增项目产品
     * 
     * @param upProjectModule 项目产品
     * @return 结果
     */
    @Override
    public int insertUpProjectModule(UpProjectModule upProjectModule) throws Exception
    {
        //获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();

        upProjectModule.setCreateTime(DateUtils.getNowDate());
        upProjectModule.setCreateBy(sysUser.getUserName());
        // 新增校验模块名是否有重复
//        List<UpProjectModule> configFileList = upProjectModuleMapper.selectConfigByProductIdAndProjectId(upProjectModule);
//        if (configFileList.size() > 0) {
//            throw new Exception("该产品模块名已存在，请重新输入");
//        }

        return upProjectModuleMapper.insertUpProjectModule(upProjectModule);
    }

    /**
     * 修改项目产品
     * 
     * @param upProjectModule 项目产品
     * @return 结果
     */
    @Override
    public int updateUpProjectModule(UpProjectModule upProjectModule) throws Exception {
        upProjectModule.setUpdateTime(DateUtils.getNowDate());
//        List<UpProjectModule> configFileList = upProjectModuleMapper.selectConfigByProductIdAndProjectId(upProjectModule);
//        if (configFileList.size() > 0) {
//            throw new Exception("该产品模块名已存在，请重新输入");
//        }
        return upProjectModuleMapper.updateUpProjectModule(upProjectModule);
    }

    /**
     * 删除项目产品对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectModuleByIds(String ids)
    {
        return upProjectModuleMapper.deleteUpProjectModuleByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除项目产品信息
     * 
     * @param projectProductId 项目产品ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectModuleById(Integer projectProductId)
    {
        return upProjectModuleMapper.deleteUpProjectModuleById(projectProductId);
    }
}
