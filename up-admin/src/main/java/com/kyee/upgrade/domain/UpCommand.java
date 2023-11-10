package com.kyee.upgrade.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 待执行命令对象 up_command
 * 
 * @author lijunqiang
 * @date 2021-10-18
 */
public class UpCommand extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 命令ID */
    @Excel(name = "命令ID")
    private Long commandId;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Integer projectId;

    /** 产品ID */
    @Excel(name = "产品ID")
    private Integer productId;

    /** 补丁ID */
    @Excel(name = "补丁ID")
    private Long patchId;

    /** 命令类型 */
    @Excel(name = "命令类型")
    private String commandType;

    /** 命令信息 */
    @Excel(name = "命令信息")
    private String commandInfo;

    public void setCommandId(Long commandId) 
    {
        this.commandId = commandId;
    }

    public Long getCommandId() 
    {
        return commandId;
    }
    public void setProjectId(Integer projectId) 
    {
        this.projectId = projectId;
    }

    public Integer getProjectId() 
    {
        return projectId;
    }
    public void setProductId(Integer productId) 
    {
        this.productId = productId;
    }

    public Integer getProductId() 
    {
        return productId;
    }
    public void setCommandType(String commandType) 
    {
        this.commandType = commandType;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getPatchId() {
        return patchId;
    }

    public void setPatchId(Long patchId) {
        this.patchId = patchId;
    }

    public String getCommandType()
    {
        return commandType;
    }
    public void setCommandInfo(String commandInfo) 
    {
        this.commandInfo = commandInfo;
    }

    public String getCommandInfo() 
    {
        return commandInfo;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("commandId", getCommandId())
            .append("projectId", getProjectId())
            .append("productId", getProductId())
            .append("patchId", getPatchId())
            .append("commandType", getCommandType())
            .append("commandInfo", getCommandInfo())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
