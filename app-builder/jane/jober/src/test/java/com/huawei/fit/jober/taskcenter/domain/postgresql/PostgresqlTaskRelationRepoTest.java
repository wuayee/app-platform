/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static com.huawei.fit.jober.common.ErrorCodes.FILTER_IS_EMPTY;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.huawei.fit.jane.task.domain.TaskRelation;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.validation.TaskRelationValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link PostgresqlTaskRelationRepo} 对应测试类。
 *
 * @author 陈镕希 c00572808
 * @since 2024-02-06
 */
@ExtendWith(MockitoExtension.class)
class PostgresqlTaskRelationRepoTest {
    private PostgresqlTaskRelationRepo postgresqlTaskRelationRepo;

    @Mock
    private DynamicSqlExecutor executor;

    @Mock
    TaskRelationValidator validator;

    @BeforeEach
    void before() {
        postgresqlTaskRelationRepo = new PostgresqlTaskRelationRepo(executor, validator);
    }

    @Test
    @DisplayName("when filter is empty, then throw BadRequestException")
    void whenFilterIsEmptyThenThrowBadRequestException() {
        TaskRelation.Filter filter = TaskRelation.Filter.custom().build();
        BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> postgresqlTaskRelationRepo.list(filter, 0L, 10, null));
        Assertions.assertEquals(FILTER_IS_EMPTY.getErrorCode(), badRequestException.getCode());
    }
}