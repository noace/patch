package com.kyee.upgrade.service;

import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.mapper.UpProjectProductMapper;
import com.ruoyi.common.core.domain.entity.SysDictType;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.mapper.SysDictTypeMapper;
import com.ruoyi.system.service.ISysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 补丁管理需要的公共方法
 *
 * @author lijunqiang
 */
@Service("upHelper")
public class UpHelperService
{
    private static final Logger log = LoggerFactory.getLogger(UpHelperService.class);

    @Autowired
    private IUpProductService upProductService;

    @Autowired
    private IUpProjectService upProjectService;

    @Autowired
    private IUpProjectServerService upProjectServerService;

    @Autowired
    private IUpPatchClientService upPatchClientService;

    @Autowired
    private ISysUserService iSysUserService;

    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private IUpProjectModuleService upProjectModuleService;

    /**
     * 返回产品列表
     *
     * @return 产品列表
     */
    public List<UpProduct> getProductList()
    {
        UpProduct search = new UpProduct();
        return upProductService.selectUpProductList(search);
    }
    /**
     * 返回项目列表
     *
     * @return 项目列表
     */
    public List<UpProject> getProjectList()
    {
        UpProject search = new UpProject();
        return upProjectService.selectUpProjectList(search);
    }

    /**
     * 返回服务列表
     *
     * @return 补丁服务列表
     */
    public List<UpProjectServer> getServerList()
    {
        UpProjectServer search = new UpProjectServer();
        return upProjectServerService.selectUpProjectServerList(search);
    }

    /**
     * 返回补丁列表
     *
     * @return 补丁列表
     */
    public List<UpPatchClient> getPatchList()
    {
        UpPatchClient search = new UpPatchClient();
        return upPatchClientService.selectUpPatchClientList(search);
    }

    /**
     * 返回产品列表
     *
     * @param productName 产品名称
     * @return 产品列表
     */
    public List<UpProduct> getProductList(String productName)
    {
        UpProduct search = new UpProduct();
        search.setProductName(productName);
        return upProductService.selectUpProductList(search);
    }

    /**
     * 根据用户名称返回产品列表
     *
     * @param userId 用户Id
     * @return 产品列表
     */
    public List<UpProduct> getProductListByName(String userId)
    {
        return upProductService.selectUpProductListByName(userId);
    }

    /**
     * 返回项目列表
     *
     * @param projectName 项目名称
     * @return 项目列表
     */
    public List<UpProject> getProjectList(String projectName)
    {
        UpProject search = new UpProject();
        search.setProjectName(projectName);
        return upProjectService.selectUpProjectList(search);
    }

    /**
     * 根据用户名称返回项目列表
     *
     * @param userId 项目Id
     * @return 项目列表
     */
    public List<UpProject> getProjectListByName(String userId)
    {
        return upProjectService.selectUpProjectListByName(userId);
    }

    /**
     * 返回服务列表
     *
     * @return 补丁服务列表
     */
    public List<UpProjectServer> getServerList(String serverName)
    {
        UpProjectServer search = new UpProjectServer();
        search.setServerName(serverName);
        return upProjectServerService.selectUpProjectServerList(search);
    }

    /**
     * 返回补丁列表
     *
     * @return 补丁列表
     */
    public List<UpPatchClient> getPatchList(String patchFileName)
    {
        UpPatchClient search = new UpPatchClient();
        search.setPatchFileName(patchFileName);
        return upPatchClientService.selectUpPatchClientList(search);
    }

    /**
     * 返回人员列表
     *
     * @return 人员列表
     */
    public List<SysUser> getSysUserList() {
        SysUser sysUser = new SysUser();
        return iSysUserService.selectUserList(sysUser);
    }

    /**
     * 获取分支类型
     * @return
     */
    public List<String> getBranchTypeList() {
        SysDictType dictType = new SysDictType();
        dictType.setDictType("patch_branch_type");
        dictType.setStatus("0");
        List<SysDictType> dictTypes = dictTypeMapper.selectDictTypeList(dictType);
        List<String> dict = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dictTypes)) {
            dict = dictTypes.stream().map(SysDictType::getDictName).collect(Collectors.toList());
        }
        return dict;
    }

    /**
     * 返回产品列表
     *
     * @return 产品列表
     */
    public List<UpProjectModule> selectUpProjectModuleList()
    {
        UpProjectModule module = new UpProjectModule();
        return upProjectModuleService.selectUpProjectModuleList(module);
    }
}
