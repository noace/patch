package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.SysConfig;
import org.apache.ibatis.annotations.Param;

/**
 * 参数配置 数据层
 * 
 * @author ruoyi
 */
public interface SysConfigMapper
{
    /**
     * 查询参数配置信息
     * 
     * @param config 参数配置信息
     * @return 参数配置信息
     */
    public SysConfig selectConfig(SysConfig config);

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    public List<SysConfig> selectConfigList(SysConfig config);

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数键名
     * @return 参数配置信息
     */
    public SysConfig checkConfigKeyUnique(String configKey);

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    public int insertConfig(SysConfig config);

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    public int updateConfig(SysConfig config);

    /**
     * 删除参数配置
     * 
     * @param configId 参数主键
     * @return 结果
     */
    public int deleteConfigById(Long configId);

    /**
     * 批量删除参数配置
     * 
     * @param configIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteConfigByIds(String[] configIds);

    /**
     * 根据条件查询发包数
     * @return
     */
    public Integer selectPublish(@Param("temp") String value);

    /**
     * 根据条件查询升级数
     * @return
     */
    public Integer selectUpgrades(@Param("temp") String value);

    /**
     * 根据条件查询升级异常数
     * @return
     */
    public Integer selectfailnum(@Param("temp") String value);

    /**
     * 查询当月活跃用户
     * @return
     */
    public Integer selectActiveUser(@Param("temp") String value);

    /**
     * 查询近一个月升级列表
     * @return
     */
    public List<Map> selectOneMonthUpPatchClientList();

    /**
     * 查询近一个月发包列表
     * @return
     */
    public List<Map> selectOneMonthUpPatchList();

    /**
     * 查询近一周发包数
     * @return
     */
    public List<Map> selectUpPatchData();

    /**
     * 查询近一周升级数
     * @return
     */
    public List<Map> selectUpPatchClientData();
}