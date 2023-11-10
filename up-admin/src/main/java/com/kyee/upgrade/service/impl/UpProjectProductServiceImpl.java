package com.kyee.upgrade.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.kyee.upgrade.domain.UpProduct;
import com.kyee.upgrade.domain.UpProject;
import com.kyee.upgrade.domain.UpProjectPerson;
import com.kyee.upgrade.mapper.UpProductMapper;
import com.kyee.upgrade.mapper.UpProjectMapper;
import com.kyee.upgrade.service.IUpProjectPersonService;
import com.kyee.upgrade.service.UpHelperService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpProjectProductMapper;
import com.kyee.upgrade.domain.UpProjectProduct;
import com.kyee.upgrade.service.IUpProjectProductService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.util.Assert;

/**
 * 项目产品Service业务层处理
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
@Service
public class UpProjectProductServiceImpl implements IUpProjectProductService
{
    @Autowired
    private UpProjectProductMapper upProjectProductMapper;

    @Autowired
    private UpProjectMapper upProjectMapper;

    @Autowired
    private UpProductMapper upProductMapper;

    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private IUpProjectPersonService projectPersonService;

    @Autowired
    private UpHelperService upHelperService;

    /**
     * 查询项目产品
     *
     * @param projectProductId 项目产品ID
     * @return 项目产品
     */
    @Override
    public UpProjectProduct selectUpProjectProductById(Integer projectProductId)
    {
        return upProjectProductMapper.selectUpProjectProductById(projectProductId);
    }

    /**
     * 查询项目产品列表
     *
     * @param upProjectProduct 项目产品
     * @return 项目产品
     */
    @Override
    public List<UpProjectProduct> selectUpProjectProductList(UpProjectProduct upProjectProduct)
    {
        return upProjectProductMapper.selectUpProjectProductList(upProjectProduct);
    }

    /**
     * 新增项目产品
     *
     * @param upProjectProduct 项目产品
     * @return 结果
     */
    @Override
    public int insertUpProjectProduct(UpProjectProduct upProjectProduct) {
        //获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();
        UpProjectProduct projectAndProductInfo = getProjectAndProductInfo(upProjectProduct);
        projectAndProductInfo.setCreateTime(DateUtils.getNowDate());
        projectAndProductInfo.setCreateBy(sysUser.getUserName());
        // 插入前，进行唯一性校验，避免数据库抛异常
        // 为以后扩展删除接口时考虑：判断重复时，增加未删除条件
        List<UpProjectProduct> projectProductList = upProjectProductMapper.selectByProductIdAndProjectId(projectAndProductInfo);
        if (projectProductList.size() > 0) {
            throw new RuntimeException("该项目产品已存在，请重新选择。");
        }
        List<SysUser> users = upHelperService.getSysUserList();
        String projectPrincipals = projectAndProductInfo.getProjectPrincipalId();
        //根据人员id查询人员名称
        if (projectPrincipals != null) {
            //用户id用逗号隔开
            String[] split = projectPrincipals.split(",");
            //用户集合
            List<String> finalProjectPrincipals = new ArrayList<>();
            for (String s : split) {
                for (SysUser user : users) {
                    if (s.equals(user.getUserId()+"")) {
                        //符合条件的用户放到用户的列表中
                        finalProjectPrincipals.add(user.getUserName());
                        break;
                    }
                }
            }
            //为项目产品负责人赋值，用户名也用逗号隔开
            projectAndProductInfo.setProjectPrincipal(String.join(",",finalProjectPrincipals));
        }

        int i = upProjectProductMapper.insertUpProjectProduct(projectAndProductInfo);
        //获取 插入id
        Long projectProductId = upProjectProductMapper.getLastInsertId();
        Long[] productPrincipalIds = upProjectProduct.getProductPrincipalIds();
        for (Long productPrincipalId : productPrincipalIds) {
            SysUser user = iSysUserService.selectUserById(productPrincipalId);
            UpProjectPerson person = new UpProjectPerson();
            person.setProjectProductId(projectProductId.intValue());
            person.setUserId(productPrincipalId);
            person.setUserName(user.getUserName());
            person.setCreateBy(sysUser.getUserName());
            person.setCreateTime(DateUtils.getNowDate());
            projectPersonService.insertUpProjectPerson(person);
        }
        return i;
    }

    /**
     * 修改项目产品
     *
     * @param upProjectProduct 项目产品
     * @return 结果
     */
    @Override
    public int updateUpProjectProduct(UpProjectProduct upProjectProduct)
    {
        //获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();
        UpProjectProduct projectAndProductInfo = getProjectAndProductInfo(upProjectProduct);
        projectAndProductInfo.setUpdateTime(DateUtils.getNowDate());
        projectAndProductInfo.setUpdateBy(sysUser.getUserName());
        Long[] productPrincipalIds = upProjectProduct.getProductPrincipalIds();
        projectPersonService.deleteUpProjectPersonById(upProjectProduct.getProjectProductId());
        int i = 0;
        for (Long productPrincipalId : productPrincipalIds) {
            UpProjectPerson person = new UpProjectPerson();
            person.setProjectProductId(upProjectProduct.getProjectProductId());
            person.setUserId(productPrincipalId);
            person.setCreateBy(sysUser.getUserName());
            person.setCreateTime(DateUtils.getNowDate());
            i = projectPersonService.insertUpProjectPerson(person);
        }
        List<SysUser> users = upHelperService.getSysUserList();
        String projectPrincipals = projectAndProductInfo.getProjectPrincipalId();
        //根据人员id查询人员名称
        if (projectPrincipals != null) {
            //用户id用逗号隔开
            String[] split = projectPrincipals.split(",");
            //用户集合
            List<String> finalProjectPrincipals = new ArrayList<>();
            for (String s : split) {
                for (SysUser user : users) {
                    if (s.equals(user.getUserId()+"")) {
                        //符合条件的用户放到用户的列表中
                        finalProjectPrincipals.add(user.getUserName());
                        break;
                    }
                }
            }
            //为项目产品负责人赋值，用户名也用逗号隔开
            projectAndProductInfo.setProjectPrincipal(String.join(",",finalProjectPrincipals));
        }
        return upProjectProductMapper.updateUpProjectProduct(projectAndProductInfo);
    }

    /**
     * 获取项目产品对象
     * @param upProjectProduct 项目产品对象
     * @return UpProjectProduct
     */
    private UpProjectProduct getProjectAndProductInfo(UpProjectProduct upProjectProduct) {
        Assert.notNull(upProjectProduct.getProductId(), "产品Id不能为空");
        Assert.notNull(upProjectProduct.getProjectId(), "项目Id不能为空");

        //获取project信息
        Integer projectId = upProjectProduct.getProjectId();
        UpProject upProject = upProjectMapper.selectUpProjectById(projectId);
        //获取product信息
        Integer productId = upProjectProduct.getProductId();
        UpProduct upProduct = upProductMapper.selectUpProductById(productId);
        upProjectProduct.setProductName(upProduct.getProductName());
        upProjectProduct.setProjectName(upProject.getProjectName());
        return upProjectProduct;
    }

    /**
     * 删除项目产品对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectProductByIds(String ids)
    {
        return upProjectProductMapper.deleteUpProjectProductByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除项目产品信息
     *
     * @param projectProductId 项目产品ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectProductById(Integer projectProductId)
    {
        return upProjectProductMapper.deleteUpProjectProductById(projectProductId);
    }

    @Override
    public List<UpProjectProduct> selectByProductIdAndProjectId(UpProjectProduct upProjectProduct) {
        return upProjectProductMapper.selectByProductIdAndProjectId(upProjectProduct);
    }
}
