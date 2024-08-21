/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.IntegrationTest;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.Spy;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;
import com.huawei.jade.app.engine.eval.dto.EvalDataCreateDto;
import com.huawei.jade.app.engine.eval.dto.EvalDataQueryParam;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetCreateDto;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetQueryParam;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetUpdateDto;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.app.engine.eval.manager.EvalDataValidator;
import com.huawei.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import com.huawei.jade.app.engine.eval.mapper.EvalDataMapper;
import com.huawei.jade.app.engine.eval.mapper.EvalDatasetMapper;
import com.huawei.jade.app.engine.eval.service.EvalDataService;
import com.huawei.jade.app.engine.eval.service.EvalDatasetService;
import com.huawei.jade.app.engine.schema.SchemaValidator;
import com.huawei.jade.app.engine.uid.UidGenerator;
import com.huawei.jade.common.vo.PageVo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示评估数据集的集成测试用例集。
 *
 * @author 何嘉斌
 * @since 2024-08-05
 */
@IntegrationTest(scanPackages = "com.huawei.jade.app.engine.eval")
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("评估数据集集成测试")
public class EvalDatasetIntegrationTest {
    @Fit
    private MockMvc mockMvc;

    @Fit
    private EvalDatasetService evalDatasetService;

    @Fit
    private EvalDataService evalDataService;

    @Spy
    private EvalDatasetMapper datasetMapper;

    @Spy
    private EvalDataMapper evalDataMapper;

    @Spy
    private EvalDataValidator dataValidator;

    @Spy
    private EvalDatasetVersionManager evalDatasetVersionManager;

    @Mock
    private UidGenerator versionGenerator;

    @Mock
    private SchemaValidator schemaValidator;

    private HttpClassicClientResponse<?> response;

    @BeforeEach
    void setUp() {
        doNothing().when(this.schemaValidator).validate(anyString(), anyList());
        when(this.versionGenerator.getUid()).thenReturn(1L, 2L, 3L, 4L, 5L, 6L);
        when(this.datasetMapper.getSchema(anyLong())).thenReturn("");
    }

    @AfterEach
    void teardown() throws IOException {
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("创建评估数据集接口成功后，插入新数据成功")
    void shouldOkWhenCreateEvalDataset() throws IOException {
        // 创建评估数据集。
        EvalDatasetCreateDto evalDatasetCreateDto = new EvalDatasetCreateDto();
        evalDatasetCreateDto.setName("ds1");
        evalDatasetCreateDto.setDescription("Test dataset");
        evalDatasetCreateDto.setContents(Collections.singletonList("{}"));
        evalDatasetCreateDto.setSchema("{}");
        evalDatasetCreateDto.setAppId("1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/dataset").jsonEntity(evalDatasetCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        this.response.close();

        // 查询数据集数量为 1。
        EvalDatasetQueryParam datasetQueryParam = new EvalDatasetQueryParam();
        datasetQueryParam.setAppId("1");
        datasetQueryParam.setPageIndex(1);
        datasetQueryParam.setPageSize(2);

        List<EvalDatasetEntity> datasetEntities = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetEntities.size()).isEqualTo(1);

        // 查询评估数据集元数据。
        EvalDatasetEntity entity = datasetEntities.get(0);
        assertThat(entity).extracting(EvalDatasetEntity::getId,
                EvalDatasetEntity::getName,
                EvalDatasetEntity::getDescription,
                EvalDatasetEntity::getCreatedBy,
                EvalDatasetEntity::getUpdatedBy).containsExactly(1L, "ds1", "Test dataset", "system", "system");

        // 查询随数据集创建插入数据量为 1。
        EvalDataQueryParam dataQueryParam = new EvalDataQueryParam();
        dataQueryParam.setDatasetId(1L);
        dataQueryParam.setVersion(1L);
        assertThat(this.evalDataMapper.countEvalData(dataQueryParam)).isEqualTo(1);

        // 插入
        EvalDataCreateDto evalDataCreateDto = new EvalDataCreateDto();
        evalDataCreateDto.setDatasetId(1L);
        evalDataCreateDto.setContents(Collections.singletonList("{}"));

        requestBuilder =
                MockMvcRequestBuilders.post("/eval/data").jsonEntity(evalDataCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);

        // 查询插入新数据后，数据量为 2。
        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(1L);
        queryParam.setVersion(5L);
        assertThat(this.evalDataMapper.countEvalData(queryParam)).isEqualTo(2);
    }

    @Test
    @DisplayName("创建评估数据集接口失败")
    void shouldNotOkWhenCreateEvalDatasetWithoutApplicationId() {
        EvalDatasetCreateDto evalDatasetCreateDto = new EvalDatasetCreateDto();
        evalDatasetCreateDto.setName("ds1");
        evalDatasetCreateDto.setDescription("Test dataset");
        evalDatasetCreateDto.setContents(Collections.singletonList("{}"));
        evalDatasetCreateDto.setSchema("{}");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/dataset").jsonEntity(evalDatasetCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("删除评估数据集接口成功")
    void shouldOkWhenDeleteEvalDataset() {
        EvalDatasetQueryParam datasetQueryParam = new EvalDatasetQueryParam();
        datasetQueryParam.setAppId("1");
        datasetQueryParam.setPageIndex(1);
        datasetQueryParam.setPageSize(10);

        List<EvalDatasetEntity> datasetEntities = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetEntities.size()).isEqualTo(3);

        MockRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/eval/dataset")
                .param("datasetIds", Arrays.asList("1", "2"))
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);

        datasetQueryParam = new EvalDatasetQueryParam();
        datasetQueryParam.setAppId("1");
        datasetQueryParam.setPageIndex(1);
        datasetQueryParam.setPageSize(10);

        datasetEntities = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetEntities.size()).isEqualTo(1);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("修改数据集信息接口成功")
    void shouldOkWhenUpdateDataset() {
        EvalDatasetUpdateDto updateDto = new EvalDatasetUpdateDto();
        updateDto.setId(1L);
        updateDto.setName("name1");
        updateDto.setDescription("desc1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.put("/eval/dataset").jsonEntity(updateDto).responseType(Void.class);

        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);

        EvalDatasetQueryParam datasetQueryParam = new EvalDatasetQueryParam();
        datasetQueryParam.setAppId("1");
        datasetQueryParam.setPageIndex(1);
        datasetQueryParam.setPageSize(5);

        List<EvalDatasetEntity> datasetEntities = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetEntities.size()).isEqualTo(3);

        EvalDatasetEntity entity = datasetEntities.get(0);
        assertThat(entity).extracting(EvalDatasetEntity::getId,
                EvalDatasetEntity::getName,
                EvalDatasetEntity::getDescription).containsExactly(1L, "name1", "desc1");
    }

    @Test
    @Sql(scripts = "sql/test_create_dataset.sql")
    @DisplayName("分页查询评估数据集接口成功")
    void shouldOkWhenQueryEvalDataset() {
        for (int i = 0; i < 3; i++) {
            MockRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/eval/dataset")
                    .param("appId", "123456")
                    .param("pageIndex", String.valueOf(i + 1))
                    .param("pageSize", "1")
                    .responseType(TypeUtils.parameterized(PageVo.class, new Type[] {EvalDatasetEntity.class}));

            this.response = this.mockMvc.perform(requestBuilder);
            assertThat(this.response.statusCode()).isEqualTo(200);
            assertThat(this.response.objectEntity()).isPresent();

            PageVo<EvalDatasetEntity> target = ObjectUtils.cast(this.response.objectEntity().get().object());

            EvalDatasetEntity entity = target.getItems().get(0);
            assertThat(entity).extracting(EvalDatasetEntity::getSchema,
                            EvalDatasetEntity::getId,
                            EvalDatasetEntity::getName,
                            EvalDatasetEntity::getDescription,
                            EvalDatasetEntity::getCreatedBy,
                            EvalDatasetEntity::getUpdatedBy)
                    .containsExactly(null,
                            i + 1L,
                            StringUtils.format("name{0}", i + 1),
                            StringUtils.format("desc{0}", i + 1),
                            StringUtils.format("Sky{0}", i + 1),
                            StringUtils.format("Fang{0}", i + 1));
        }
    }

    @Test
    @DisplayName("评估数据集增删改查成功")
    void shouldOkWhenCrudEvalDataset() throws IOException {
        // 创建评估数据集。
        EvalDatasetCreateDto evalDatasetCreateDto = new EvalDatasetCreateDto();
        evalDatasetCreateDto.setName("ds1");
        evalDatasetCreateDto.setDescription("Test dataset");
        evalDatasetCreateDto.setContents(Collections.singletonList("{}"));
        evalDatasetCreateDto.setSchema("{}");
        evalDatasetCreateDto.setAppId("1");

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/eval/dataset").jsonEntity(evalDatasetCreateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        this.response.close();

        // 查询数据集数量为 1。
        EvalDatasetQueryParam datasetQueryParam = new EvalDatasetQueryParam();
        datasetQueryParam.setAppId("1");
        datasetQueryParam.setPageIndex(1);
        datasetQueryParam.setPageSize(10);

        List<EvalDatasetEntity> datasetEntities = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetEntities.size()).isEqualTo(1);

        EvalDatasetEntity entity = datasetEntities.get(0);
        assertThat(entity).extracting(EvalDatasetEntity::getId,
                EvalDatasetEntity::getName,
                EvalDatasetEntity::getDescription).containsExactly(1L, "ds1", "Test dataset");

        // 更新数据集元数据。
        EvalDatasetUpdateDto updateDto = new EvalDatasetUpdateDto();
        updateDto.setId(1L);
        updateDto.setName("name1");
        updateDto.setDescription("desc1");

        requestBuilder = MockMvcRequestBuilders.put("/eval/dataset").jsonEntity(updateDto).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        this.response.close();

        datasetEntities = this.datasetMapper.listEvalDataset(datasetQueryParam);
        assertThat(datasetEntities.size()).isEqualTo(1);
        entity = datasetEntities.get(0);
        assertThat(entity).extracting(EvalDatasetEntity::getId,
                EvalDatasetEntity::getName,
                EvalDatasetEntity::getDescription).containsExactly(1L, "name1", "desc1");

        // 删除评估数据集。
        requestBuilder = MockMvcRequestBuilders.delete("/eval/dataset")
                .param("datasetIds", Arrays.asList("1", "2"))
                .responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
        assertThat(this.datasetMapper.listEvalDataset(datasetQueryParam).size()).isEqualTo(0);
    }
}
