/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MybatisTest;
import com.huawei.fitframework.test.annotation.Sql;
import com.huawei.fitframework.test.domain.db.DataBaseModelEnum;
import com.huawei.jade.app.engine.eval.po.EvalDataPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 表示 {@link EvalDataMapper} 的测试用例。
 *
 * @author 易文渊
 * @since 2024-07-22
 */
@MybatisTest(classes = {EvalDataMapper.class}, model = DataBaseModelEnum.POSTGRESQL)
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