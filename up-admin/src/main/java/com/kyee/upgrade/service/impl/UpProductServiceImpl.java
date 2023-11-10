package com.kyee.upgrade.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.kyee.upgrade.domain.UpProjectPerson;
import com.kyee.upgrade.domain.UpProjectProduct;
import com.kyee.upgrade.service.IUpProjectPersonService;
import com.kyee.upgrade.service.IUpProjectProductService;
import com.ruoyi.common.core.domain.CxSelect;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpProductMapper;
import com.kyee.upgrade.domain.UpProduct;
import com.kyee.upgrade.service.IUpProductService;
import com.ruoyi.common.core.text.Convert;

/**
 * 产品Service业务层处理
 *
 * @author lijunqiang
 * @date 2021-06-10
 */
@Service
public class UpProductServiceImpl implements IUpProductService
{
    @Autowired
    private UpProductMapper upProductMapper;

    @Autowired
    private IUpProjectProductService upProjectProductService;

    @Autowired
    private IUpProductService upProductService;

    @Autowired
    private IUpProjectPersonService upProjectPersonService;
    /**
     * 查询产品
     *
     * @param productId 产品ID
     * @return 产品
     */
    @Override
    public UpProduct selectUpProductById(Integer productId)
    {
        return upProductMapper.selectUpProductById(productId);
    }

    /**
     * 查询产品列表
     *
     * @param upProduct 产品
     * @return 产品
     */
    @Override
    public List<UpProduct> selectUpProductList(UpProduct upProduct)
    {
        return upProductMapper.selectUpProductList(upProduct);
    }

    /**
     * 查询产品列表
     *
     * @param userId 用户Id
     * @return 产品
     */
    @Override
    public List<UpProduct> selectUpProductListByName(String userId)
    {
        return upProductMapper.selectUpProductListByName(userId);
    }

    /**
     * 新增产品
     *
     * @param upProduct 产品
     * @return 结果
     */
    @Override
    public int insertUpProduct(UpProduct upProduct)
    {
        //获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();
        upProduct.setCreateTime(DateUtils.getNowDate());
        upProduct.setCreateBy(sysUser.getUserName());
        return upProductMapper.insertUpProduct(upProduct);
    }

    /**
     * 修改产品
     *
     * @param upProduct 产品
     * @return 结果
     */
    @Override
    public int updateUpProduct(UpProduct upProduct)
    {
        //获取当前用户信息
        SysUser sysUser = ShiroUtils.getSysUser();
        upProduct.setUpdateTime(DateUtils.getNowDate());
        upProduct.setUpdateBy(sysUser.getUserName());
        return upProductMapper.updateUpProduct(upProduct);
    }

    /**
     * 删除产品对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpProductByIds(String ids)
    {
        return upProductMapper.deleteUpProductByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除产品信息
     *
     * @param productId 产品ID
     * @return 结果
     */
    @Override
    public int deleteUpProductById(Integer productId)
    {
        return upProductMapper.deleteUpProductById(productId);
    }

    /**
     * 获取产品项目级联列表
     *
     * @return 结果
     */
    @Override
    public List<CxSelect> selectProjectAndProduct(Long userId){
        List<CxSelect> productList = new ArrayList<CxSelect>();

        // 获取产品项目级联列表
        List<UpProduct> upProducts = upProductService.selectUpProductList(new UpProduct());

        // 获取当前用户绑定的项目产品Id
        List<Integer> projectProductIds = new ArrayList<>();
        if (null != userId) {
            projectProductIds = upProjectPersonService.selectUpProjectPersonByUserId(userId).stream().map(UpProjectPerson::getProjectProductId).collect(Collectors.toList());
        }

        if (null != upProducts) {
            for (UpProduct upProduct : upProducts) {
                UpProjectProduct upProjectProduct = new UpProjectProduct();
                upProjectProduct.setProductId(upProduct.getProductId());
                List<UpProjectProduct> upProjectProducts = upProjectProductService.selectByProductIdAndProjectId(upProjectProduct);
                // 过滤当前用户绑定的项目产品
                if (null != userId) {
                    List<Integer> finalProjectProductIds = projectProductIds;
                    upProjectProducts = upProjectProducts.stream().filter(
                            s -> finalProjectProductIds.contains(s.getProjectProductId())
                    ).collect(Collectors.toList());
                }

                if (null != upProjectProducts && upProjectProducts.size() != 0) {
                    List<CxSelect> projectList = new ArrayList<>();
                    for (UpProjectProduct projectProduct : upProjectProducts) {
                        // 项目下拉框对象
                        CxSelect cxSelectUpProject = new CxSelect();
                        // 项目下拉name
                        cxSelectUpProject.setN(projectProduct.getProjectName());
                        // 项目下拉value
                        cxSelectUpProject.setV(projectProduct.getProjectId() + "");
                        // 项目下列列表
                        projectList.add(cxSelectUpProject);
                    }
                    // 产品下拉框对象
                    CxSelect cxSelectUpProduct = new CxSelect();
                    // 产品下拉name
                    cxSelectUpProduct.setN(upProduct.getProductName());
                    // 产品下拉value
                    cxSelectUpProduct.setV(upProduct.getProductId() + "");
                    // 产品联动的项目列表
                    cxSelectUpProduct.setS(projectList);
                    productList.add(cxSelectUpProduct);
                }
            }
        }

        return productList;
    }
}
