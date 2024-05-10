/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.form.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElsaDataDto {
    List<ElsaPage> pages;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    static public class ElsaPage {
        List<ElsaShape> shapes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    static public class ElsaShape {
        List<ElsaShapeMeta> meta;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    static public class ElsaShapeMeta {
        Integer length;
        String key;
        String name;
        String type;
    }
}
