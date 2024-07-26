/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import static com.huawei.fitframework.util.IoUtils.content;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.test.annotation.MybatisTest;
import com.huawei.fitframework.test.annotation.Sql;
import com.huawei.fitframework.test.domain.db.DatabaseModel;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.app.engine.eval.dto.EvalDataQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDataEntity;
import com.huawei.jade.app.engine.eval.po.EvalDataPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * 表示 {@link EvalDataMapper} 的测试用例。
 *
 * @author 易文渊
 * @since 2024-07-22
 */
@MybatisTest(classes = {EvalDataMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 EvalDataMapper")
public class EvalDataMapperTest {
    @Fit
    private EvalDataMapper evalDataMapper;

    @Test
    @DisplayName("插入数据后，回填主键成功")
    void shouldOkWhenInsert() {
        EvalDataPo evalDataPo = new EvalDataPo();
        evalDataPo.setContent("{}");
        evalDataPo.setCreatedVersion(1L);
        evalDataPo.setDatasetId(1L);
        this.evalDataMapper.insertAll(Collections.singletonList(evalDataPo));
        assertThat(evalDataPo.getId()).isNotEqualTo(null);
    }

    static class QueryTestCaseProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws IOException {
            ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);

            String resourceName = "/test/query_eval_data_test_case.json";
            String jsonContent = content(QueryTestCaseProvider.class, resourceName);

            List<EvalDataQueryTestCaseParam> testCase = serializer.deserialize(jsonContent,
                    TypeUtils.parameterized(List.class, new Type[] {EvalDataQueryTestCaseParam.class}));

            return testCase.stream().map(test -> {
                return Arguments.of(test.getQueryParam(), test.getExpectedSize(), test.getExpectedContent());
            });
        }
    }

    @ParameterizedTest
    @ArgumentsSource(QueryTestCaseProvider.class)
    @DisplayName("分页查询数据成功")
    @Sql(scripts = "sql/insert_data.sql")
    void TestQueryEvalData(EvalDataQueryParam queryParam, int expectedSize, String expectedContent) {
        List<EvalDataEntity> response = this.evalDataMapper.listEvalData(queryParam);
        assertThat(response.get(0).getContent()).isEqualTo(expectedContent);
        assertThat(response.size()).isEqualTo(expectedSize);
        for (EvalDataEntity entity : response) {
            assertThat(entity.getId()).isNotEqualTo(null);
            assertThat(entity.getId()).isGreaterThan(0);
            assertThat(entity.getContent()).isNotNull();
            assertThat(entity.getCreatedAt()).isNotNull();
            assertThat(entity.getUpdatedAt()).isNotNull();
        }
    }

    @Test
    @DisplayName("统计评估数据数量成功")
    @Sql(scripts = "sql/insert_data.sql")
    void TestCountEvalData() {
        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(1L);
        queryParam.setVersion(3L);
        int evalDataCount = this.evalDataMapper.countEvalData(queryParam);

        assertThat(evalDataCount).isEqualTo(5);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("软删除指定数据后，更新过期时间成功")
    void shouldOkWhenSoftDelete() {
        EvalDataPo evalDataPo = new EvalDataPo();
        evalDataPo.setId(1L);
        int effectRows = this.evalDataMapper.updateExpiredVersion(Collections.singletonList(evalDataPo), 2L);
        assertThat(effectRows).isEqualTo(1);
    }

    @Test
    @DisplayName("软删除不存在数据时，更新行数为0")
    void shouldFailWhenSoftDeleteInvalidData() {
        EvalDataPo evalDataPo = new EvalDataPo();
        evalDataPo.setId(1L);
        int effectRows = this.evalDataMapper.updateExpiredVersion(Collections.singletonList(evalDataPo), 2L);
        assertThat(effectRows).isEqualTo(0);
    }
}