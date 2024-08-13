/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.common.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MybatisTest;
import com.huawei.fitframework.test.annotation.Sql;
import com.huawei.fitframework.transaction.DataAccessException;
import com.huawei.jade.authentication.context.UserContext;
import com.huawei.jade.authentication.context.UserContextHolder;
import com.huawei.jade.common.audit.stub.TestMapper;
import com.huawei.jade.common.audit.stub.TestPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 表示 {@link AuditInterceptor} 的测试集。
 *
 * @author 易文渊
 * @since 2024-08-13
 */
@DisplayName("测试 AuditInterceptor")
@MybatisTest(classes = {TestMapper.class, AuditInterceptor.class})
@Sql(scripts = "sql/test_create_table.sql")
public class AuditInterceptorTest {
    @Fit
    private TestMapper testMapper;

    @Test
    @DisplayName("测试插入单条数据，自动审计生效")
    void shouldOkWhenInsertSingleEntity() {
        UserContext userContext = new UserContext("sky", "", "");
        TestPo testPo = new TestPo();
        UserContextHolder.apply(userContext, () -> testMapper.insert(testPo));
        assertThat(testPo).extracting(TestPo::getCreatedBy, TestPo::getUpdatedBy).containsExactly("sky", "sky");
    }

    @Test
    @DisplayName("测试批量插入数据，自动审计生效")
    void shouldOkWhenInsertCollection() {
        UserContext userContext = new UserContext("sky", "", "");
        TestPo testPo = new TestPo();
        UserContextHolder.apply(userContext, () -> testMapper.insertAll(Collections.singletonList(testPo)));
        assertThat(testPo).extracting(TestPo::getCreatedBy, TestPo::getUpdatedBy).containsExactly("sky", "sky");
    }

    @Test
    @DisplayName("测试多个参数时，自动审计生效")
    void shouldOkWhenInsertMap() {
        UserContext userContext = new UserContext("sky", "", "");
        TestPo testPo = new TestPo();
        UserContextHolder.apply(userContext, () -> testMapper.insertMultiParm(testPo, 1L));
        assertThat(testPo).extracting(TestPo::getCreatedBy, TestPo::getUpdatedBy).containsExactly("sky", "sky");
    }

    @Test
    @DisplayName("测试用户上下文为空时，插入数据失败")
    void shouldFailWhenUserContextMissing() {
        assertThatThrownBy(() -> testMapper.insert(new TestPo())).isInstanceOf(DataAccessException.class);
    }
}