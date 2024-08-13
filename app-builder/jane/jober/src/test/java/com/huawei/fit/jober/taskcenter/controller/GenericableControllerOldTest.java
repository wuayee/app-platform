/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.huawei.fit.jober.common.model.JoberResponse;
import com.huawei.fit.jober.common.model.TextStringValue;
import com.huawei.fit.service.RegistryService;
import com.huawei.fit.service.entity.FitableInfo;
import com.huawei.fit.service.entity.FitableMeta;
import com.huawei.fit.service.entity.FitableMetaInstance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link GenericableControllerOld} 对应测试类。
 *
 * @author 陈镕希
 * @since 2023-06-15
 */
@ExtendWith(MockitoExtension.class)
class GenericableControllerOldTest {
    @Mock
    RegistryService registryService;

    private GenericableControllerOld genericableController;

    @BeforeEach
    void before() {
        genericableController = new GenericableControllerOld(registryService);
    }

    @Nested
    @DisplayName("测试获取Genericable实现信息")
    class TestGetGenericableImplementInfo {
        @Test
        @DisplayName("只有一个实现别名，只显示系统实现别名")
        void givenOnlyAliasThenDisplaySystemAlias() {
            // given
            FitableMetaInstance fitableMetaInstance = new FitableMetaInstance();
            FitableMeta fitableMeta = new FitableMeta();
            FitableInfo fitableInfo = new FitableInfo();
            fitableInfo.setFitableId("fitableId");
            fitableMeta.setFitable(fitableInfo);
            fitableMeta.setAliases(Collections.singletonList("$Fit$dtsServiceImpl"));
            fitableMetaInstance.setMeta(fitableMeta);
            when(registryService.queryFitableMetas(any())).thenReturn(Collections.singletonList(fitableMetaInstance));
            // when
            JoberResponse<List<TextStringValue>> actual = genericableController.getGenericableImplementInfo(
                    "genericableId");
            // then
            List<TextStringValue> actualData = actual.getData();
            Assertions.assertEquals(1, actualData.size());
            Assertions.assertEquals("$Fit$dtsServiceImpl", actualData.get(0).getText());
            Assertions.assertEquals("fitableId", actualData.get(0).getValue());
        }

        @Test
        @DisplayName("有两个实现别名，去除系统实现别名")
        void givenTwoAliasThenRemoveSystemAlias() {
            // given
            FitableMetaInstance fitableMetaInstance = new FitableMetaInstance();
            FitableMeta fitableMeta = new FitableMeta();
            FitableInfo fitableInfo = new FitableInfo();
            fitableInfo.setFitableId("fitableId");
            fitableMeta.setFitable(fitableInfo);
            fitableMeta.setAliases(Arrays.asList("$Fit$dtsServiceImpl", "dts-defect"));
            fitableMetaInstance.setMeta(fitableMeta);
            when(registryService.queryFitableMetas(any())).thenReturn(Collections.singletonList(fitableMetaInstance));
            // when
            JoberResponse<List<TextStringValue>> actual = genericableController.getGenericableImplementInfo(
                    "genericableId");
            // then
            List<TextStringValue> actualData = actual.getData();
            Assertions.assertEquals(1, actualData.size());
            Assertions.assertEquals("dts-defect", actualData.get(0).getText());
            Assertions.assertEquals("fitableId", actualData.get(0).getValue());
        }

        @Test
        @DisplayName("有多个实现别名，去除系统实现别名")
        void givenMultiAliasThenRemoveSystemAlias() {
            // given
            FitableMetaInstance fitableMetaInstance = new FitableMetaInstance();
            FitableMeta fitableMeta = new FitableMeta();
            FitableInfo fitableInfo = new FitableInfo();
            fitableInfo.setFitableId("fitableId");
            fitableMeta.setFitable(fitableInfo);
            fitableMeta.setAliases(Arrays.asList("$Fit$dtsServiceImpl", "dts-defect", "libing-requirement"));
            fitableMetaInstance.setMeta(fitableMeta);
            when(registryService.queryFitableMetas(any())).thenReturn(Collections.singletonList(fitableMetaInstance));
            // when
            JoberResponse<List<TextStringValue>> actual = genericableController.getGenericableImplementInfo(
                    "genericableId");
            // then
            List<TextStringValue> actualData = actual.getData();
            Assertions.assertEquals(1, actualData.size());
            Assertions.assertEquals("dts-defect, libing-requirement", actualData.get(0).getText());
            Assertions.assertEquals("fitableId", actualData.get(0).getValue());
        }
    }
}