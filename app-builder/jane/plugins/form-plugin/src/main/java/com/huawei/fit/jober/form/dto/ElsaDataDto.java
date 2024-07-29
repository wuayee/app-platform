/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.form.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

import java.util.List;

/**
 * elsa数据
 *
 * @author s00664640
 * @since 2024/4/10
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElsaDataDto {
    List<ElsaPage> pages;

    /**
     * elsa页面数据
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class ElsaPage {
        List<ElsaShape> shapes;
    }

    /**
     * elsa shape数据
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class ElsaShape {
        List<ElsaShapeMeta> meta;
    }

    /**
     * elsa shape元数据
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class ElsaShapeMeta {
        Integer length;
        String key;
        String name;
        String type;
    }
}
