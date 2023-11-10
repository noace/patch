package com.kyee.upgrade.service.impl;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.kyee.upgrade.domain.UpProduct;
import com.kyee.upgrade.domain.UpProject;
import com.kyee.upgrade.domain.UpProjectConfigFile;
import com.kyee.upgrade.mapper.UpProductMapper;
import com.kyee.upgrade.mapper.UpProjectMapper;
import com.kyee.upgrade.utils.FileUtil;
import com.kyee.upgrade.utils.ZipFileUtil;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.framework.web.exception.GlobalExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpPatchSqlMapper;
import com.kyee.upgrade.domain.UpPatchSql;
import com.kyee.upgrade.service.IUpPatchSqlService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * 执行SQLService业务层处理
 * 
 * @author lijunqiang
 * @date 2022-10-20
 */
@Service
public class UpPatchSqlServiceImpl implements IUpPatchSqlService
{
    private static final Logger log = LoggerFactory.getLogger(UpPatchSqlServiceImpl.class);

    @Autowired
    private UpPatchSqlMapper upPatchSqlMapper;

    @Autowired
    private UpProjectMapper upProjectMapper;

    @Autowired
    private UpProductMapper upProductMapper;

    /**
     * 查询执行SQL
     * 
     * @param id 执行SQLID
     * @return 执行SQL
     */
    @Override
    public UpPatchSql selectUpPatchSqlById(Long id)
    {
        return upPatchSqlMapper.selectUpPatchSqlById(id);
    }

    /**
     * 查询执行SQL列表
     * 
     * @param upPatchSql 执行SQL
     * @return 执行SQL
     */
    @Override
    public List<UpPatchSql> selectUpPatchSqlList(UpPatchSql upPatchSql)
    {

        List<UpPatchSql> sqlList = upPatchSqlMapper.selectUpPatchSqlList(upPatchSql);
        //获取project信息
        List<UpProject> projectList = upProjectMapper.selectUpProjectList(new UpProject());
        Map<Integer, String> projectMap = projectList.stream().collect(Collectors.toMap(UpProject::getProjectId, UpProject::getProjectName));
        //获取product信息
        List<UpProduct> productList = upProductMapper.selectUpProductList(new UpProduct());
        Map<Integer, String> productMap = productList.stream().collect(Collectors.toMap(UpProduct::getProductId, UpProduct::getProductName));
        if (!CollectionUtils.isEmpty(sqlList)) {
            for (UpPatchSql patchSql : sqlList) {
                patchSql.setProductName(productMap.get(patchSql.getProductId()));
                patchSql.setProjectName(projectMap.get(patchSql.getProjectId()));
            }
        }
        return sqlList;
    }

    /**
     * 新增执行SQL
     * 
     * @param upPatchSql 执行SQL
     * @return 结果
     */
    @Override
    public int insertUpPatchSql(UpPatchSql upPatchSql)
    {
        upPatchSql.setCreateTime(DateUtils.getNowDate());
        return upPatchSqlMapper.insertUpPatchSql(upPatchSql);
    }

    /**
     * 修改执行SQL
     * 
     * @param upPatchSql 执行SQL
     * @return 结果
     */
    @Override
    public int updateUpPatchSql(UpPatchSql upPatchSql)
    {
        return upPatchSqlMapper.updateUpPatchSql(upPatchSql);
    }

    /**
     * 删除执行SQL对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpPatchSqlByIds(String ids)
    {
        SysUser sysUser = ShiroUtils.getSysUser();
        return upPatchSqlMapper.deleteUpPatchSqlByIds(Convert.toStrArray(ids), sysUser.getUserName());
    }

    /**
     * 删除执行SQL信息
     * 
     * @param id 执行SQLID
     * @return 结果
     */
    @Override
    public int deleteUpPatchSqlById(Long id)
    {
        return upPatchSqlMapper.deleteUpPatchSqlById(id);
    }

    /**
     * 批量查询执行SQL列表
     *
     * @param ids 执行SQLID
     */
    @Override
    public String batchSelectSqlList(String ids) throws Exception {

        String[] idArr = ids.split(",");
        List<String> idList = Arrays.asList(idArr);

        // 查询到的Sql路径文件列表复制到要压缩的临时路径下，压缩，下载
        List<UpPatchSql> upPatchSqls = upPatchSqlMapper.batchSelectSqlList(idList);
        List<String> sqlPathList = upPatchSqls.stream().map(UpPatchSql::getSqlPath).collect(Collectors.toList());

        // 存放批量SQL文件，待压缩目录
        String zipPath = RuoYiConfig.getProfile() + "/ZipSQL/";
        // 压缩文件目录
        String zipDir = RuoYiConfig.getProfile() + "/SQL/ZIP/";
        File zipDirFile = new File(zipDir);
        if (!zipDirFile.exists()) {
            zipDirFile.mkdirs();
        }
        for (String path : sqlPathList) {
            File file = new File(zipPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            String sqlName = path.substring(path.lastIndexOf("/") + 1);
            String sourcePath = RuoYiConfig.getProfile() + "/SQL/" + sqlName;

            String targetPath = zipPath + sqlName;
            FileUtil.copyFile(new File(sourcePath), new File(targetPath),null);
        }

        Date date = new Date();
        String createTime = new SimpleDateFormat("yyyyMMddHHmmss").format(date);

        String zipName = createTime + "_SQL.ZIP";
        String downloadPath = zipDir + zipName;
        boolean isSuccessZipFile = ZipFileUtil.ZipFiles(new File(downloadPath),new File(zipPath).listFiles());

        if(!isSuccessZipFile){
            throw new Exception("文件压缩失败【"+zipName+"】");
        } else {
            // 删除上次被压缩文件
            FileUtil.delFile(new File(zipPath));
        }
        return Constants.RESOURCE_PREFIX + "/SQL/ZIP/" + zipName;
    }
}
