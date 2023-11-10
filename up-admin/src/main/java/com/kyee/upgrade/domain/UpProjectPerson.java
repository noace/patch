package com.kyee.upgrade.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目人员对象 up_project_person
 * 
 * @author lijunqiang
 * @date 2022-03-16
 */
public class UpProjectPerson extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 项目产品ID */
    private Integer projectProductId;

    /** 人员工号 */
    private Long userId;

    /** 人员工号 */
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setProjectProductId(Integer projectProductId)
    {
        this.projectProductId = projectProductId;
    }

    public Integer getProjectProductId() 
    {
        return projectProductId;
    }
    public void setUserId(Long userId) 
    {
        this.userId = userId;
    }

    public Long getUserId() 
    {
        return userId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("projectProductId", getProjectProductId())
            .append("userId", getUserId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
