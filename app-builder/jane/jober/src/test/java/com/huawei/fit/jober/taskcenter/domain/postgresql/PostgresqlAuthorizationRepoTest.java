/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.postgresql;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.task.domain.Authorization;
import modelengine.fit.jane.task.util.Dates;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.validation.AuthorizationValidator;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.ServerInternalException;
import modelengine.fit.jober.common.exceptions.NotFoundException;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@DisplayName("测试 PostgresqlAuthorizationRepo")
class PostgresqlAuthorizationRepoTest {
    private static final String ID = "ebb433ec5d094e72a8f54ef624a4ea68";

    private static final String SYSTEM = "FIT Lab";

    private static final String USER_ID = "12345678";

    private static final String TOKEN = "hello token";

    private static final Long EXPIRATION = 200L;

    private static final String OPERATOR = "Jane";

    private static final Authorization.Declaration DECLARATION = Authorization.Declaration.custom()
            .system(SYSTEM).user(USER_ID).token(TOKEN).expiration(EXPIRATION).build();

    private static final Map<String, Object> ROW;

    private static final OperationContext CONTEXT = OperationContext.custom().operator(OPERATOR).build();

    private static final ClassLoader LOADER = PostgresqlAuthorizationRepoTest.class.getClassLoader();

    private static final String INSERT_SQL;

    private static final String PATCH_SQL;

    private static final String DELETE_SQL;

    private static final String RETRIEVE_SQL;

    private static final String RETRIEVE_SYSTEM_USER_SQL;

    private static final String COUNT_SQL;

    private static final String LIST_SQL;

    static {
        LocalDateTime now = Dates.toUtc(LocalDateTime.now());
        ROW = MapBuilder.<String, Object>get()
                .put("id", ID)
                .put("system", SYSTEM)
                .put("user_id", USER_ID)
                .put("token", TOKEN)
                .put("created_by", OPERATOR)
                .put("created_at", Timestamp.valueOf(now))
                .put("updated_by", OPERATOR)
                .put("updated_at", Timestamp.valueOf(now))
                .build();
        try {
            INSERT_SQL = IoUtils.content(LOADER, "sql/authorization-insert.sql");
            PATCH_SQL = IoUtils.content(LOADER, "sql/authorization-patch.sql");
            DELETE_SQL = IoUtils.content(LOADER, "sql/authorization-delete.sql");
            RETRIEVE_SQL = IoUtils.content(LOADER, "sql/authorization-retrieve.sql");
            COUNT_SQL = IoUtils.content(LOADER, "sql/authorization-count.sql");
            LIST_SQL = IoUtils.content(LOADER, "sql/authorization-list.sql");
            RETRIEVE_SYSTEM_USER_SQL = IoUtils.content(LOADER, "sql/authorization-retrieve-system-user.sql");
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private PostgresqlAuthorizationRepo repo;

    private DynamicSqlExecutor executor;

    private AuthorizationValidator validator;

    @BeforeEach
    void setup() {
        this.executor = mock(DynamicSqlExecutor.class);
        this.validator = mock(AuthorizationValidator.class);
        this.repo = new PostgresqlAuthorizationRepo(this.executor, this.validator);
    }

    private void mockValidator() {
        when(this.validator.id(any())).thenAnswer(args -> args.getArguments()[0]);
        when(this.validator.system(any())).thenAnswer(args -> args.getArguments()[0]);
        when(this.validator.user(any())).thenAnswer(args -> args.getArguments()[0]);
        when(this.validator.token(any())).thenAnswer(args -> args.getArguments()[0]);
        when(this.validator.expiration(any())).thenAnswer(args -> args.getArguments()[0]);
    }

    @Test
    @DisplayName("创建三方系统授权")
    void should_return_new_created_authorization() {
        this.mockValidator();
        when(this.executor.executeUpdate(any(), any())).thenReturn(1);
        Authorization authorization = this.repo.create(DECLARATION, CONTEXT);
        assertTrue(expectAuthorization(authorization, Function.identity()));
        verify(this.executor, times(1)).executeUpdate(eq(INSERT_SQL), argThat(args -> {
            if (args.size() != 9) {
                return false;
            }
            Authorization data = Authorization.custom().id(cast(args.get(0))).system(cast(args.get(1)))
                    .user(cast(args.get(2))).token(cast(args.get(3))).expiration(cast(args.get(4)))
                    .creator(cast(args.get(5))).creationTime(cast(args.get(6))).lastModifier(cast(args.get(7)))
                    .lastModificationTime(cast(args.get(8))).build();
            return expectAuthorization(data, Dates::toUtc);
        }));
    }

    @Test
    @DisplayName("修改三方系统授权")
    void should_patch_authorization() {
        this.mockValidator();
        when(this.executor.executeUpdate(any(), any())).thenReturn(1);
        this.repo.patch(ID, DECLARATION, CONTEXT);
        verify(this.executor, times(1)).executeUpdate(eq(PATCH_SQL), argThat(args -> {
            if (args.size() != 7) {
                return false;
            }
            Authorization data = Authorization.custom().system(cast(args.get(0))).user(cast(args.get(1)))
                    .token(cast(args.get(2))).expiration(cast(args.get(3))).creator(cast(args.get(4)))
                    .creationTime(cast(args.get(5))).lastModifier(cast(args.get(4)))
                    .lastModificationTime(cast(args.get(5))).id(cast(args.get(6)))
                    .build();
            return expectAuthorization(data, Dates::toUtc);
        }));
    }

    @Test
    @DisplayName("删除三方系统授权")
    void should_delete_authorization() {
        when(this.validator.id(any())).thenAnswer(args -> args.getArguments()[0]);
        when(this.executor.executeUpdate(any(), any())).thenReturn(1);
        this.repo.delete(ID, CONTEXT);
        verify(this.executor, times(1)).executeUpdate(eq(DELETE_SQL),
                argThat(args -> args.size() == 1 && Objects.equals(ID, args.get(0))));
    }

    @Test
    @DisplayName("检索第三方系统授权")
    void should_return_authorization_with_specific_id() {
        when(this.validator.id(any())).thenAnswer(args -> args.getArguments()[0]);
        when(this.executor.executeQuery(any(), any())).thenReturn(Collections.singletonList(ROW));
        Authorization authorization = this.repo.retrieve(ID, CONTEXT);
        expectAuthorization(authorization, Function.identity());
        verify(this.executor, times(1)).executeQuery(eq(RETRIEVE_SQL),
                argThat(args -> args.size() == 1 && Objects.equals(ID, args.get(0))));
    }

    @Test
    @DisplayName("根据系统，用户检索第三方系统授权")
    void should_return_authorization_with_system_user() {
        when(this.executor.executeQuery(any(), any())).thenReturn(Collections.singletonList(ROW));
        Authorization authorization = this.repo.retrieveSystemUser(SYSTEM, USER_ID, CONTEXT);
        expectAuthorization(authorization, Function.identity());
        verify(this.executor, times(1)).executeQuery(eq(RETRIEVE_SYSTEM_USER_SQL),
                argThat(args -> args.size() == 2 && Objects.equals(SYSTEM, args.get(0)) && Objects.equals(USER_ID,
                        args.get(1))));
    }

    @Test
    @DisplayName("分页查询三方系统授权")
    void should_return_authorizations() {
        when(this.executor.executeQuery(any(), any())).thenReturn(Collections.singletonList(ROW));
        when(this.executor.executeScalar(any(), any())).thenReturn(1);
        Authorization.Filter filter = Authorization.Filter.custom()
                .ids(Collections.singletonList(ID))
                .systems(Collections.singletonList(SYSTEM))
                .users(Collections.singletonList(USER_ID))
                .build();
        RangedResultSet<Authorization> results = this.repo.list(filter, 0, 100, CONTEXT);
        assertEquals(0, results.getRange().getOffset());
        assertEquals(100, results.getRange().getLimit());
        assertEquals(1, results.getRange().getTotal());
        assertEquals(1, results.getResults().size());
        expectAuthorization(results.getResults().get(0), Function.identity());
        verify(this.executor, times(1)).executeScalar(eq(COUNT_SQL), argThat(args -> args.size() == 3
                && Objects.equals(ID, args.get(0)) && Objects.equals(USER_ID, args.get(1))
                && Objects.equals("%" + SYSTEM + "%", args.get(2))));
        verify(this.executor, times(1)).executeQuery(eq(LIST_SQL), argThat(args -> args.size() == 5
                && Objects.equals(ID, args.get(0)) && Objects.equals(USER_ID, args.get(1))
                && Objects.equals("%" + SYSTEM + "%", args.get(2)) && Objects.equals(0L, args.get(3))
                && Objects.equals(100, args.get(4))));
    }

    @Test
    @DisplayName("当插入数据库失败时，抛出异常")
    void should_throw_when_create_but_no_affected_rows() {
        this.mockValidator();
        when(this.executor.executeUpdate(any(), any())).thenReturn(0);
        ServerInternalException ex = assertThrows(ServerInternalException.class,
                () -> this.repo.create(DECLARATION, CONTEXT));
        assertEquals("Failed to insert authorization into database.", ex.getMessage());
    }

    @Test
    @DisplayName("当更新数据库失败时，抛出异常")
    void should_throw_when_patch_but_no_affected_rows() {
        this.mockValidator();
        when(this.executor.executeUpdate(any(), any())).thenReturn(0);
        NotFoundException ex = assertThrows(NotFoundException.class, () -> this.repo.patch(ID, DECLARATION, CONTEXT));
        assertEquals(ErrorCodes.AUTHORIZATION_NOT_FOUND.getErrorCode(), ex.getCode());
    }

    @Test
    @DisplayName("当删除数据库失败时，抛出异常")
    void should_throw_when_delete_but_no_affected_rows() {
        when(this.validator.id(any())).thenAnswer(args -> args.getArguments()[0]);
        when(this.executor.executeUpdate(any(), any())).thenReturn(0);
        NotFoundException ex = assertThrows(NotFoundException.class, () -> this.repo.delete(ID, CONTEXT));
        assertEquals(ErrorCodes.AUTHORIZATION_NOT_FOUND.getErrorCode(), ex.getCode());
    }

    private static boolean expectAuthorization(Authorization authorization,
            Function<LocalDateTime, LocalDateTime> datetimeMapper) {
        LocalDateTime now = datetimeMapper.apply(LocalDateTime.now());
        return Entities.isId(authorization.id())
                && Objects.equals(SYSTEM, authorization.system())
                && Objects.equals(USER_ID, authorization.user())
                && Objects.equals(TOKEN, authorization.token())
                && Objects.equals(EXPIRATION, authorization.expiration())
                && Objects.equals(OPERATOR, authorization.creator())
                && !authorization.creationTime().isAfter(now)
                && Objects.equals(OPERATOR, authorization.lastModifier())
                && !authorization.lastModificationTime().isAfter(now);
    }
}