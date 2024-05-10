/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import java.util.Map;

/**
 * 表示实时刷新的数据源。
 *
 * @author 梁济时 l00815032
 * @since 2023-11-21
 */
public class RefreshInTimeSourceEntity extends SourceEntity {
    private Map<String, Object> metadata;

    private String createFitableId;

    private String patchFitableId;

    private String deleteFitableId;

    private String retrieveFitableId;

    private String listFitableId;

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getCreateFitableId() {
        return createFitableId;
    }

    public void setCreateFitableId(String createFitableId) {
        this.createFitableId = createFitableId;
    }

    public String getPatchFitableId() {
        return patchFitableId;
    }

    public void setPatchFitableId(String patchFitableId) {
        this.patchFitableId = patchFitableId;
    }

    public String getDeleteFitableId() {
        return deleteFitableId;
    }

    public void setDeleteFitableId(String deleteFitableId) {
        this.deleteFitableId = deleteFitableId;
    }

    public String getRetrieveFitableId() {
        return retrieveFitableId;
    }

    public void setRetrieveFitableId(String retrieveFitableId) {
        this.retrieveFitableId = retrieveFitableId;
    }

    public String getListFitableId() {
        return listFitableId;
    }

    public void setListFitableId(String listFitableId) {
        this.listFitableId = listFitableId;
    }
}
