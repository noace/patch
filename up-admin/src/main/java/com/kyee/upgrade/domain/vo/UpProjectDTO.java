package com.kyee.upgrade.domain.vo;

import java.util.List;

public class UpProjectDTO {

    private Integer productId;

    private List<String> idList;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public List<String> getIdList() {
        return idList;
    }

    public void setIdList(List<String> idList) {
        this.idList = idList;
    }
}
