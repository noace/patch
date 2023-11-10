package com.kyee.upgrade.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpProduct;
import com.kyee.upgrade.domain.UpProjectConfigFile;
import com.kyee.upgrade.mapper.UpProductMapper;
import com.kyee.upgrade.mapper.UpProjectConfigFileMapper;
import com.kyee.upgrade.utils.FileUtil;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpPatchCodelistMapper;
import com.kyee.upgrade.domain.UpPatchCodelist;
import com.kyee.upgrade.service.IUpPatchCodelistService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 补丁包代码列Service业务层处理
 *
 * @author lijunqiang
 * @date 2021-09-23
 */
@Service
public class UpPatchCodelistServiceImpl implements IUpPatchCodelistService
{
    @Autowired
    private UpPatchCodelistMapper upPatchCodelistMapper;

    @Autowired
    private UpProjectConfigFileMapper configFileMapper;

    /**
     * 查询补丁包代码列
     *
     * @param codeId 补丁包代码列ID
     * @return 补丁包代码列
     */
    @Override
    public UpPatchCodelist selectUpPatchCodelistById(Long codeId)
    {
        return upPatchCodelistMapper.selectUpPatchCodelistById(codeId);
    }

    /**
     * 查询补丁包代码列列表
     *
     * @param upPatchCodelist 补丁包代码列
     * @return 补丁包代码列
     */
    @Override
    public List<UpPatchCodelist> selectUpPatchCodelistList(UpPatchCodelist upPatchCodelist)
    {
        return upPatchCodelistMapper.selectUpPatchCodelistList(upPatchCodelist);
    }

    /**
     * 新增补丁包代码列
     *
     * @param upPatchCodelist 补丁包代码列
     * @return 结果
     */
    @Override
    public int insertUpPatchCodelist(UpPatchCodelist upPatchCodelist)
    {
        upPatchCodelist.setCreateTime(DateUtils.getNowDate());
        return upPatchCodelistMapper.insertUpPatchCodelist(upPatchCodelist);
    }

    /**
     * 修改补丁包代码列
     *
     * @param upPatchCodelist 补丁包代码列
     * @return 结果
     */
    @Override
    public int updateUpPatchCodelist(UpPatchCodelist upPatchCodelist)
    {
        upPatchCodelist.setUpdateTime(DateUtils.getNowDate());
        return upPatchCodelistMapper.updateUpPatchCodelist(upPatchCodelist);
    }

    /**
     * 删除补丁包代码列对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpPatchCodelistByIds(String ids)
    {
        return upPatchCodelistMapper.deleteUpPatchCodelistByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除补丁包代码列信息
     *
     * @param codeId 补丁包代码列ID
     * @return 结果
     */
    @Override
    public int deleteUpPatchCodelistById(Long codeId)
    {
        return upPatchCodelistMapper.deleteUpPatchCodelistById(codeId);
    }

    @Override
    public List<UpPatchCodelist> selectUpPatchCodeListByPatchIds(List<Long> patchIds) {

        return upPatchCodelistMapper.selectUpPatchCodeListByPatchIds(patchIds);
    }

    /**
     * 根据补丁信息插入补丁code列表
     *
     * @param upPatch
     * @return 结果
     */
    @Override
    public void insertUpBuildCodePathByUpPatch(UpPatch upPatch)
    {
        //获取product信息productShortName
        // Integer productId = upPatch.getProductId();
        // UpProduct upProduct = upProductMapper.selectUpProductById(productId);

        // 获取配置文件表中不进行打包的文件名
        UpProjectConfigFile configFile = new UpProjectConfigFile();
        configFile.setProjectId(upPatch.getProjectId());
        configFile.setProductId(upPatch.getProductId());
        List<UpProjectConfigFile> configFileList = configFileMapper.selectUpProjectConfigFileList(configFile);
        List<String> configNameList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(configFileList)) {
            configNameList = configFileList.stream().map(UpProjectConfigFile::getConfigFileName).collect(Collectors.toList());
        }

        //插入插入补丁code列表
        String[] codePathArray = upPatch.getCodeList().split(",");
        for(int i = 0;i< codePathArray.length;i++){
            UpPatchCodelist upPatchCodelist = new UpPatchCodelist();
            // 代码路径拼接产品项目名
//            String codePath = upProduct.getProductShortName() + "/" + codePathArray[i];
            String codePath = codePathArray[i];
            String code = codePath.substring(codePath.lastIndexOf("/") + 1);
            // 跳过不打包的配置文件和空格
            if (configNameList.contains(code) || StringUtils.isEmpty(code)) {
                continue;
            }
            upPatchCodelist.setCodePath(codePathArray[i]);
            // TODO 添加编译后的路径为空格，编译时修改
            upPatchCodelist.setCompilePath(" ");
            upPatchCodelist.setFileType(FileUtil.getFileType(codePathArray[i]).getValue());
            upPatchCodelist.setPatchId(upPatch.getPatchId());
            upPatchCodelist.setProductId(upPatch.getProductId());
            upPatchCodelist.setProjectId(upPatch.getProjectId());
            upPatchCodelist.setCreateBy(upPatch.getCreateBy());
            upPatchCodelist.setUpdateBy(upPatch.getUpdateBy());
            upPatchCodelist.setCreateTime(new Date());
            upPatchCodelist.setUpdateTime(new Date());
            upPatchCodelistMapper.insertUpPatchCodelist(upPatchCodelist);
        }
    }
}
