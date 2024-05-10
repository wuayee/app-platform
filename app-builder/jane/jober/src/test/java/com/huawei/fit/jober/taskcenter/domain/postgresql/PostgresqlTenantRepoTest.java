/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.domain.Tenant;
import com.huawei.fit.jane.task.domain.TenantAccessLevel;
import com.huawei.fit.jane.task.domain.TenantMember;
import com.huawei.fit.jane.task.gateway.EmployeeDetailVO;
import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.utils.UserUtil;
import com.huawei.fit.jober.taskcenter.service.TagService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.validation.TenantValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link PostgresqlTenantRepo}对应测试类。
 *
 * @author 陈镕希 c00572808
 * @since 2023-10-12
 */
@ExtendWith(MockitoExtension.class)
class PostgresqlTenantRepoTest {
    private static final String TENANT_ID = "12345678123456781234567812345678";

    private static final String MEMBER_ID = "12345678123456781234567812345678";

    @Mock
    TenantValidator tenantValidator;

    @Mock
    UserUtil userUtil;

    @Mock
    private DynamicSqlExecutor dynamicSqlExecutor;

    @Mock
    private TagService tagService;

    private PostgresqlTenantRepo repo;

    private static Map<String, Object> constructDbQueryResult() {
        Map<String, Object> dbQueryResult = new HashMap<>();
        dbQueryResult.put("id", "a3a7df7e45f44451970a6a8e138382c2");
        dbQueryResult.put("name", "公共空间");
        dbQueryResult.put("description", "默认空间");
        dbQueryResult.put("avatar_id", "");
        dbQueryResult.put("tags", "");
        dbQueryResult.put("created_by", "");
        dbQueryResult.put("created_at", Timestamp.valueOf(Dates.toUtc(LocalDateTime.now())));
        dbQueryResult.put("updated_by", "");
        dbQueryResult.put("updated_at", Timestamp.valueOf(Dates.toUtc(LocalDateTime.now())));
        return dbQueryResult;
    }

    private static Map<String, Object> constructDbQueryMemberResult() {
        Map<String, Object> dbQueryResult = new HashMap<>();
        dbQueryResult.put("id", "87654321876543218765432187654321");
        dbQueryResult.put("tenant_id", "12345678123456781234567812345678");
        dbQueryResult.put("user_id", "globalId1");
        dbQueryResult.put("created_by", "");
        dbQueryResult.put("created_at", Timestamp.valueOf(Dates.toUtc(LocalDateTime.now())));
        return dbQueryResult;
    }

    private static Tenant.Declaration constructTenantDeclaration() {
        return Tenant.Declaration.custom()
                .name("name")
                .description("")
                .avatarId("avatarId")
                .members(Collections.singletonList("张三 00123456"))
                .tags(Arrays.asList("tag1", "tag2"))
                .accessLevel(TenantAccessLevel.PRIVATE)
                .build();
    }

    private static Tenant.Declaration constructTenantDeclarationWithoutMembers() {
        return Tenant.Declaration.custom()
                .name("name")
                .description("")
                .avatarId("avatarId")
                .tags(Arrays.asList("tag1", "tag2"))
                .accessLevel(TenantAccessLevel.PRIVATE)
                .build();
    }

    @BeforeEach
    void before() {
        repo = new PostgresqlTenantRepo(dynamicSqlExecutor, tenantValidator, userUtil, tagService);
    }

    @Nested
    @DisplayName("测试删除租户")
    class TestDeleteTenant {
        @Test
        @DisplayName("测试删除租户")
        void delete() {
            final OperationContext context = OperationContext.custom().operator("张三 00123456").build();
            EmployeeDetailVO employee = new EmployeeDetailVO();
            employee.setGlobalUserId("globalId");
            when(userUtil.getEmployeeDetail(eq("00123456"), eq(null), eq(null), eq(null))).thenReturn(employee);
            mockQueryTenant();
            when(dynamicSqlExecutor.executeUpdate(any(), any())).thenReturn(1);
            when(dynamicSqlExecutor.executeScalar(any(), anyList())).thenAnswer(args -> {
                String sql = args.getArgument(0);
                if (sql.contains("SELECT COUNT(1) FROM \"task\" WHERE \"tenant_id\"")) {
                    return 0;
                } else {
                    return 1;
                }
            });
            // Run the test
            repo.delete("a3a7df7e45f44451970a6a8e138382c2", context);
            // Verify the results
            verify(dynamicSqlExecutor, times(1)).executeUpdate("DELETE FROM \"tenant\" WHERE \"id\" = ?",
                    Collections.singletonList("a3a7df7e45f44451970a6a8e138382c2"));
        }

        @Test
        @DisplayName("测试没权限操作人删除租户")
        void deleteWithoutOperatePermission() {
            final OperationContext context = OperationContext.custom().operator("张三 00123456").build();
            EmployeeDetailVO employee = new EmployeeDetailVO();
            employee.setGlobalUserId("globalId");
            when(userUtil.getEmployeeDetail(eq("00123456"), eq(null), eq(null), eq(null))).thenReturn(employee);
            mockQueryTenant();
            when(dynamicSqlExecutor.executeScalar(any(), anyList())).thenAnswer(args -> {
                String sql = args.getArgument(0);
                return 0;
            });
            // Run the test
            BadRequestException actual = Assertions.assertThrows(BadRequestException.class,
                    () -> repo.delete("a3a7df7e45f44451970a6a8e138382c2", context));
            // Verify the results
            Assertions.assertEquals(ErrorCodes.NO_OPERATE_PERMISSION.getErrorCode(), actual.getCode());
            Assertions.assertEquals(ErrorCodes.NO_OPERATE_PERMISSION.getMessage(), actual.getMessage());
        }
    }

    private void mockQueryTenant() {
        Map<String, Object> dbQueryResult = constructDbQueryResult();
        when(dynamicSqlExecutor.executeQuery(any(), any())).thenReturn(Collections.singletonList(dbQueryResult));
    }

    @Test
    void listTenantIdsByUserId() {

    }

    @Nested
    @DisplayName("测试查找租户")
    class TestRetrieveTenant {
        @Test
        @DisplayName("测试根据id查找租户")
        void retrieveById() {
            final OperationContext context = OperationContext.custom().operator("张三 00123456").build();
            mockQueryTenant();
            // Run the test
            repo.retrieve("a3a7df7e45f44451970a6a8e138382c2", context);
            // Verify the results
            verify(dynamicSqlExecutor, times(1)).executeQuery(
                    "SELECT \"id\", \"name\", \"description\", \"avatar_id\", \"access_level\", \"created_by\", \"created_at\", \"updated_by\", \"updated_at\" FROM \"tenant\" WHERE \"id\" = ?",
                    Collections.singletonList("a3a7df7e45f44451970a6a8e138382c2"));
        }

        @Test
        @DisplayName("测试根据名称查找租户")
        void retrieveByName() {
            final OperationContext context = OperationContext.custom().operator("张三 00123456").build();
            when(dynamicSqlExecutor.executeQuery(any(), any())).thenAnswer(args -> {
                String sql = args.getArgument(0);
                if (sql.contains(
                        "SELECT \"id\", \"name\", \"description\", \"avatar_id\", \"access_level\", \"created_by\", \"created_at\", \"updated_by\", \"updated_at\" FROM \"tenant\" WHERE \"id\" = ?")) {
                    return Collections.emptyList();
                }
                return Collections.singletonList(constructDbQueryResult());
            });
            // Run the test
            repo.retrieve("public", context);
            // Verify the results
            verify(dynamicSqlExecutor, times(1)).executeQuery(
                    "SELECT \"id\", \"name\", \"description\", \"avatar_id\", \"access_level\", \"created_by\", \"created_at\", \"updated_by\", \"updated_at\" FROM \"tenant\" WHERE \"name\" = ?",
                    Collections.singletonList("public"));
        }
    }

    @Test
    @DisplayName("测试按条件筛选租户")
    void list() {
        final OperationContext context = OperationContext.custom().operator("张三 00123456").build();
        mockQueryTenant();
        when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(1);
        // Run the test
        Tenant.Filter tenantFilter = Tenant.Filter.custom()
                .ids(Collections.singletonList("a3a7df7e45f44451970a6a8e138382c2"))
                .accessLevels(Collections.singletonList(TenantAccessLevel.PUBLIC))
                .build();
        repo.list(tenantFilter, 0, 1, context);
        // Verify the results
        verify(dynamicSqlExecutor, times(1)).executeScalar(
                eq("SELECT COUNT(1) FROM \"tenant\" AS \"t\" WHERE 1 = 1 AND \"id\" IN (?) AND \"access_level\" IN (?)"),
                argThat(arg -> arg.size() == 2 && "a3a7df7e45f44451970a6a8e138382c2".equals(arg.get(0))
                        && "PUBLIC".equals(arg.get(1))));
        verify(dynamicSqlExecutor, times(1)).executeQuery(
                eq("SELECT \"t\".\"id\", \"t\".\"name\", \"t\".\"description\", \"t\".\"avatar_id\", \"t\".\"access_level\", \"t\".\"created_by\", \"t\".\"created_at\", \"t\".\"updated_by\", \"t\".\"updated_at\" FROM \"tenant\" AS \"t\" WHERE 1 = 1 AND \"id\" IN (?) AND \"access_level\" IN (?) ORDER BY \"created_at\" OFFSET ? LIMIT ?"),
                argThat(arg -> arg.size() == 4 && "a3a7df7e45f44451970a6a8e138382c2".equals(arg.get(0))
                        && "PUBLIC".equals(arg.get(1)) && arg.get(2).equals(0L) && arg.get(3).equals(1)));
    }

    @Test
    @DisplayName("测试创建租户成员")
    void insertMember() {
        // Setup
        final OperationContext context = OperationContext.custom().operator("赵五 00666666").build();
        EmployeeDetailVO employee1 = new EmployeeDetailVO();
        employee1.setGlobalUserId("globalId1");
        when(userUtil.getEmployeeDetail(eq("00123456"), eq(null), eq(null), eq(null))).thenReturn(employee1);
        EmployeeDetailVO employee2 = new EmployeeDetailVO();
        employee2.setGlobalUserId("globalId2");
        when(userUtil.getEmployeeDetail(eq("00654321"), eq(null), eq(null), eq(null))).thenReturn(employee2);
        EmployeeDetailVO employee3 = new EmployeeDetailVO();
        employee3.setGlobalUserId("globalId3");
        when(userUtil.getEmployeeDetail(eq("00666666"), eq(null), eq(null), eq(null))).thenReturn(employee3);
        when(dynamicSqlExecutor.executeUpdate(any(), any())).thenReturn(1);
        mockQueryTenant();
        mockHasPermitted();
        // Run the test
        repo.insertMembers(TENANT_ID, Arrays.asList("张三 00123456", "李四 00654321"), context);
        // Verify the results
        verify(dynamicSqlExecutor, times(1)).executeUpdate(
                eq("INSERT INTO tenant_member(id, tenant_id, user_id, created_by, created_at)VALUES(?, ?, ?, ?, ?),(?, ?, ?, ?, ?)ON CONFLICT (\"tenant_id\", \"user_id\") DO NOTHING"),
                anyList());
    }

    @Test
    @DisplayName("测试operator是其中一人的情况下，创建租户成员")
    void insertMemberWithSameOperator() {
        // Setup
        final OperationContext context = OperationContext.custom().operator("张三 00123456").build();
        EmployeeDetailVO employee1 = new EmployeeDetailVO();
        employee1.setGlobalUserId("globalId1");
        when(userUtil.getEmployeeDetail(eq("00123456"), eq(null), eq(null), eq(null))).thenReturn(employee1);
        EmployeeDetailVO employee2 = new EmployeeDetailVO();
        employee2.setGlobalUserId("globalId2");
        when(userUtil.getEmployeeDetail(eq("00654321"), eq(null), eq(null), eq(null))).thenReturn(employee2);
        when(dynamicSqlExecutor.executeUpdate(any(), any())).thenReturn(1);
        mockQueryTenant();
        mockHasPermitted();
        // Run the test
        repo.insertMembers(TENANT_ID, Arrays.asList("张三 00123456", "李四 00654321"), context);
        // Verify the results
        verify(dynamicSqlExecutor, times(1)).executeUpdate(
                eq("INSERT INTO tenant_member(id, tenant_id, user_id, created_by, created_at)VALUES(?, ?, ?, ?, ?),(?, ?, ?, ?, ?)ON CONFLICT (\"tenant_id\", \"user_id\") DO NOTHING"),
                anyList());
    }

    @Test
    @DisplayName("测试根据租户删除租户成员")
    void deleteMemberByTenant() {
        // Setup
        final OperationContext context = OperationContext.custom().operator("赵五 00666666").build();
        when(dynamicSqlExecutor.executeUpdate(any(), any())).thenReturn(1);
        EmployeeDetailVO employee = new EmployeeDetailVO();
        employee.setGlobalUserId("globalId");
        // Run the test
        Tenant mockTenant = Mockito.mock(Tenant.class);
        when(mockTenant.id()).thenReturn("a3a7df7e45f44451970a6a8e138382c2");
        when(mockTenant.isPermitted(any(), any(), any())).thenReturn(true);
        repo.deleteMemberByTenant(mockTenant, context);
        // Verify the results
        verify(dynamicSqlExecutor, times(1)).executeUpdate(eq("DELETE FROM \"tenant_member\" WHERE \"tenant_id\" = ?"),
                eq(Collections.singletonList("a3a7df7e45f44451970a6a8e138382c2")));
    }

    @Test
    @DisplayName("测试根据租户查询租户成员")
    void listMemberByTenant() {
        // Setup
        final OperationContext context = OperationContext.custom().operator("赵五 00666666").build();
        when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(1);
        when(dynamicSqlExecutor.executeQuery(any(), any())).thenReturn(Collections.emptyList());
        TenantMember.Filter tenantMemberFilter = TenantMember.Filter.custom()
                .tenantId("12345678123456781234567812345678")
                .build();
        // Run the test
        repo.listMember(tenantMemberFilter, 0, 1, context);
        // Verify the results
        verify(dynamicSqlExecutor, times(1)).executeScalar(
                eq("SELECT COUNT(1) FROM \"tenant_member\" WHERE 1 = 1 AND \"tenant_id\" = ?"),
                argThat(arg -> arg.size() == 1 && "12345678123456781234567812345678".equals(arg.get(0))));
        verify(dynamicSqlExecutor, times(1)).executeQuery(
                eq("SELECT \"id\", \"tenant_id\", \"user_id\", \"created_by\", \"created_at\" FROM \"tenant_member\" WHERE 1 = 1 AND \"tenant_id\" = ? ORDER BY \"created_at\" OFFSET ? LIMIT ?"),
                argThat(arg -> arg.size() == 3 && "12345678123456781234567812345678".equals(arg.get(0))
                        && Objects.equals(0L, arg.get(1)) && Objects.equals(1, arg.get(2))));
    }

    @Test
    @DisplayName("测试根据租户和成员信息查询租户成员")
    void listMemberByTenantAndUser() {
        // Setup
        final OperationContext context = OperationContext.custom().operator("赵五 00666666").build();
        when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(1);
        when(dynamicSqlExecutor.executeQuery(any(), any())).thenReturn(Collections.emptyList());
        EmployeeDetailVO employee = new EmployeeDetailVO();
        employee.setGlobalUserId("globalId1");
        when(userUtil.getEmployeeDetail(eq("00123456"), eq(null), eq(null), eq(null))).thenReturn(employee);
        TenantMember.Filter tenantMemberFilter = TenantMember.Filter.custom()
                .tenantId("12345678123456781234567812345678")
                .userIds(Collections.singletonList("张三 00123456"))
                .build();
        // Run the test
        repo.listMember(tenantMemberFilter, 0, 1, context);
        // Verify the results
        verify(dynamicSqlExecutor, times(1)).executeScalar(
                eq("SELECT COUNT(1) FROM \"tenant_member\" WHERE 1 = 1 AND \"tenant_id\" = ? AND \"user_id\" IN (?)"),
                argThat(arg -> arg.size() == 2 && "12345678123456781234567812345678".equals(arg.get(0))
                        && "globalId1".equals(arg.get(1))));
        verify(dynamicSqlExecutor, times(1)).executeQuery(
                eq("SELECT \"id\", \"tenant_id\", \"user_id\", \"created_by\", \"created_at\" FROM \"tenant_member\" WHERE 1 = 1 AND \"tenant_id\" = ? AND \"user_id\" IN (?) ORDER BY \"created_at\" OFFSET ? LIMIT ?"),
                argThat(arg -> arg.size() == 4 && "12345678123456781234567812345678".equals(arg.get(0))
                        && "globalId1".equals(arg.get(1)) && Objects.equals(0L, arg.get(2)) && Objects.equals(1,
                        arg.get(3))));
    }

    @Test
    @DisplayName("测试根据租户和id查询租户成员")
    void listMemberByTenantAndId() {
        // Setup
        final OperationContext context = OperationContext.custom().operator("赵五 00666666").build();
        Map<String, Object> dbQueryResult = constructDbQueryMemberResult();
        when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(1);
        when(dynamicSqlExecutor.executeQuery(any(), any())).thenReturn(Collections.singletonList(dbQueryResult));
        TenantMember.Filter tenantMemberFilter = TenantMember.Filter.custom()
                .tenantId("12345678123456781234567812345678")
                .ids(Collections.singletonList("87654321876543218765432187654321"))
                .build();
        // Run the test
        repo.listMember(tenantMemberFilter, 0, 1, context);
        // Verify the results
        verify(dynamicSqlExecutor, times(1)).executeScalar(
                eq("SELECT COUNT(1) FROM \"tenant_member\" WHERE 1 = 1 AND \"id\" IN (?) AND \"tenant_id\" = ?"),
                argThat(arg -> arg.size() == 2 && "12345678123456781234567812345678".equals(arg.get(1))
                        && "87654321876543218765432187654321".equals(arg.get(0))));
        verify(dynamicSqlExecutor, times(1)).executeQuery(
                eq("SELECT \"id\", \"tenant_id\", \"user_id\", \"created_by\", \"created_at\" FROM \"tenant_member\" WHERE 1 = 1 AND \"id\" IN (?) AND \"tenant_id\" = ? ORDER BY \"created_at\" OFFSET ? LIMIT ?"),
                argThat(arg -> arg.size() == 4 && "87654321876543218765432187654321".equals(arg.get(0))
                        && "12345678123456781234567812345678".equals(arg.get(1)) && Objects.equals(0L, arg.get(2))
                        && Objects.equals(1, arg.get(3))));
    }

    private void mockHasPermitted() {
        when(dynamicSqlExecutor.executeScalar(
                eq("SELECT COUNT(1) FROM \"tenant_member\" WHERE 1 = 1 AND \"tenant_id\" = ? AND \"user_id\" IN (?)"),
                anyList())).thenReturn(1);
    }

    @Nested
    @DisplayName("测试创建租户")
    class TestCreate {
        @Test
        @DisplayName("测试创建租户带Member")
        void createTenantWithMember() {
            // Setup
            mockQueryTenant();
            mockExecuteScalar();
            final Tenant.Declaration declaration = constructTenantDeclaration();
            this.verifyCreate(declaration);
        }

        private void mockExecuteScalar() {
            when(dynamicSqlExecutor.executeScalar(any(), anyList())).thenAnswer(args -> {
                String sql = args.getArgument(0);
                if (sql.contains(
                        "SELECT COUNT(1) FROM \"tenant_member\" WHERE 1 = 1 AND \"tenant_id\" = ? AND \"user_id\" IN (?)")) {
                    return 1;
                }
                return 0;
            });
        }

        @Test
        @DisplayName("测试创建租户不带有Member")
        void createTenantWithoutMember() {
            // Setup
            mockQueryTenant();
            mockExecuteScalar();
            final Tenant.Declaration declaration = constructTenantDeclarationWithoutMembers();
            this.verifyCreate(declaration);
        }

        private void verifyCreate(Tenant.Declaration declaration) {
            final OperationContext context = OperationContext.custom().operator("张三 00123456").build();
            EmployeeDetailVO employee = new EmployeeDetailVO();
            employee.setGlobalUserId("globalId");
            when(userUtil.getEmployeeDetail(eq("00123456"), eq(null), eq(null), eq(null))).thenReturn(employee);
            when(dynamicSqlExecutor.executeUpdate(any(), any())).thenReturn(1);
            when(tenantValidator.name(eq("name"), any())).thenReturn("name");
            // Run the test
            repo.create(declaration, context);
            // Verify the results
            verify(dynamicSqlExecutor, times(1)).executeUpdate(
                    eq("INSERT INTO tenant(id, name, description, avatar_id, created_by, created_at, updated_by, updated_at, access_level) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)"),
                    anyList());
        }
    }

    @Nested
    @DisplayName("测试选择性修改租户")
    class TestPatch {
        @Test
        @DisplayName("正常修改租户到一个原来没有的名称，修改成功")
        void patchTenant2NonExistentNameThenPatchSuccessfully() {
            // Setup
            final Tenant.Declaration declaration = constructTenantDeclaration();
            final OperationContext context = OperationContext.custom().operator("张三 00123456").build();
            EmployeeDetailVO employee = new EmployeeDetailVO();
            employee.setGlobalUserId("globalId");
            when(userUtil.getEmployeeDetail(eq("00123456"), eq(null), eq(null), eq(null))).thenReturn(employee);
            when(dynamicSqlExecutor.executeUpdate(any(), any())).thenReturn(1);
            when(tenantValidator.name(eq("name"), any())).thenReturn("name");
            mockQueryTenant();
            mockHasPermitted();
            // Run the test
            repo.patch("a3a7df7e45f44451970a6a8e138382c2", declaration, context);
            // Verify the results
            verify(dynamicSqlExecutor, times(1)).executeUpdate(
                    eq("UPDATE \"tenant\" SET \"updated_by\" = ?, \"updated_at\" = ?, \"name\" = ?, \"description\" = ?, \"avatar_id\" = ?, \"access_level\" = ? WHERE \"id\" = ?"),
                    anyList());
            verify(tagService, times(1)).save(eq("TENANT"), argThat(arg -> {
                if (arg.size() != 1) {
                    return false;
                }
                List<String> tags = arg.get("a3a7df7e45f44451970a6a8e138382c2");
                if (tags == null || tags.size() != 2) {
                    return false;
                }
                return tags.get(0).equals("tag1") && tags.get(1).equals("tag2");
            }), same(context));
        }

        @Test
        @DisplayName("正常修改租户到一个原有的名称，修改成功")
        void patchTenantWithSameNameThenPatchSuccessfully() {
            // Setup
            when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(1);
            when(dynamicSqlExecutor.executeQuery(any(), any())).thenReturn(
                    Collections.singletonList(new HashMap<String, Object>() {{
                        put("id", "a3a7df7e45f44451970a6a8e138382c2");
                        put("name", "name");
                        put("description", "12345678123456781234567812345678");
                        put("avatar_id", "avatarId");
                        put("access_level", "accessLevel");
                        put("created_by", "globalId");
                        put("created_at", Timestamp.valueOf(LocalDateTime.now()));
                        put("updated_by", "globalId");
                        put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
                    }}));
            mockQueryTenant();
            patchTenant2NonExistentNameThenPatchSuccessfully();
        }

        @Test
        @DisplayName("正常修改租户到一个已有的名称，修改失败")
        void patchTenantWithExistentNameThenThrowException() {
            // Setup
            final Tenant.Declaration declaration = constructTenantDeclaration();

            final OperationContext context = OperationContext.custom().operator("张三 00123456").build();
            EmployeeDetailVO employee = new EmployeeDetailVO();
            employee.setGlobalUserId("globalId");
            when(userUtil.getEmployeeDetail(eq("00123456"), eq(null), eq(null), eq(null))).thenReturn(employee);
            when(dynamicSqlExecutor.executeScalar(any(), any())).thenReturn(1);
            when(dynamicSqlExecutor.executeQuery(any(), any())).thenAnswer(args -> {
                String sql = args.getArgument(0);
                if (sql.contains(
                        "SELECT \"t\".\"id\", \"t\".\"name\", \"t\".\"description\", \"t\".\"avatar_id\", \"t\".\"access_level\", \"t\".\"created_by\", \"t\".\"created_at\", \"t\".\"updated_by\", \"t\".\"updated_at\" FROM \"tenant\" AS \"t\" WHERE 1 = 1 AND \"name\" IN (?) ORDER BY \"created_at\" OFFSET ? LIMIT ?")) {
                    return Collections.singletonList(new HashMap<String, Object>() {{
                        put("id", "12345678123456781234567812345678");
                        put("name", "name");
                        put("description", "12345678123456781234567812345678");
                        put("avatar_id", "avatarId");
                        put("access_level", TenantAccessLevel.PRIVATE);
                        put("created_by", "globalId");
                        put("created_at", Timestamp.valueOf(LocalDateTime.now()));
                        put("updated_by", "globalId");
                        put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
                    }});
                }
                return Collections.singletonList(constructDbQueryResult());
            });
            when(tenantValidator.name(eq("name"), any())).thenReturn("name");
            mockHasPermitted();
            // Run the test
            BadRequestException actual = Assertions.assertThrows(BadRequestException.class,
                    () -> repo.patch("a3a7df7e45f44451970a6a8e13838666", declaration, context));
            // Verify the results
            Assertions.assertEquals(ErrorCodes.TENANT_IS_EXISTS.getErrorCode(), actual.getCode());
            Assertions.assertEquals(ErrorCodes.TENANT_IS_EXISTS.getMessage(), actual.getMessage());
        }
    }

    @Nested
    @DisplayName("测试批量删除租户成员")
    class TestDeleteMembers {
        @Test
        @DisplayName("删除一个租户成员")
        void deleteOneMember() {
            // Setup
            final OperationContext context = OperationContext.custom().operator("赵五 00666666").build();
            EmployeeDetailVO employee = new EmployeeDetailVO();
            employee.setGlobalUserId("globalId");
            when(userUtil.getEmployeeDetail(eq("00666666"), eq(null), eq(null), eq(null))).thenReturn(employee);
            when(dynamicSqlExecutor.executeUpdate(any(), any())).thenReturn(1);
            mockQueryTenant();
            mockHasPermitted();
            // Run the test
            repo.deleteMembersById(TENANT_ID, Collections.singletonList(MEMBER_ID), context);
            // Verify the results
            verify(dynamicSqlExecutor, times(1)).executeUpdate(
                    eq("DELETE FROM \"tenant_member\" WHERE \"tenant_id\" = ? AND \"id\" IN(?)"),
                    eq(Arrays.asList("12345678123456781234567812345678", "12345678123456781234567812345678")));
        }

        @Test
        @DisplayName("删除多个租户成员")
        void deleteMultiMembers() {
            // Setup
            final OperationContext context = OperationContext.custom().operator("赵五 00666666").build();
            EmployeeDetailVO employee = new EmployeeDetailVO();
            employee.setGlobalUserId("globalId");
            when(userUtil.getEmployeeDetail(eq("00666666"), eq(null), eq(null), eq(null))).thenReturn(employee);
            when(dynamicSqlExecutor.executeUpdate(any(), any())).thenReturn(2);
            mockQueryTenant();
            mockHasPermitted();
            // Run the test
            repo.deleteMembersById(TENANT_ID, Arrays.asList(MEMBER_ID, "87654321876543218765432187654321"), context);
            // Verify the results
            verify(dynamicSqlExecutor, times(1)).executeUpdate(
                    eq("DELETE FROM \"tenant_member\" WHERE \"tenant_id\" = ? AND \"id\" IN(?,?)"),
                    eq(Arrays.asList("12345678123456781234567812345678", "12345678123456781234567812345678",
                            "87654321876543218765432187654321")));
        }
    }
}