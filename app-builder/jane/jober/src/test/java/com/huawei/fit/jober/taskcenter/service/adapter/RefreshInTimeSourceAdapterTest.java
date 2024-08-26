/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.adapter;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.dao.po.SourceObject;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.domain.RefreshInTimeSourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceType;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.MapSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.IoUtils;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@DisplayName("测试 RefreshInTimeSourceAdapter")
class RefreshInTimeSourceAdapterTest {
    private static final String SOURCE_ID = "beed175b19d04949bba9c90c45bb8383";

    private static final String TASK_ID = "cfc1439dc363469aa4fa7d7431364080";

    private static final String SOURCE_NAME = "demo-source";

    private static final String SOURCE_APP = "demo-app";

    private static final String SOURCE_TYPE = "REFRESH_IN_TIME";

    private static final String OPERATOR = "demo-operator";

    private static final String OPERATOR_IP = "localhost";

    private static final String TENANT_ID = "demo-tenant";

    private static final Map<String, Object> METADATA_MAP = MapBuilder.<String, Object>get()
            .put("project", "hello")
            .build();

    private static final String METADATA_JSON = "{\"project\":\"hello\"}";

    private static final String CREATE_FITABLE_ID = "aa65abcec1064edb88b3657e4aeb9b62";

    private static final String PATCH_FITABLE_ID = "5c53eea2077246929a34570d9dc40ab3";

    private static final String DELETE_FITABLE_ID = "06207ee0392d412890a587f62dad7db3";

    private static final String RETRIEVE_FITABLE_ID = "7de934a4f9d542abbfb1ecbcd972f120";

    private static final String LIST_FITABLE_ID = "190fc8d07f724702bf705487b313071f";

    private static final Map<String, Object> ROW = MapBuilder.<String, Object>get()
            .put("id", SOURCE_ID)
            .put("metadata", METADATA_JSON)
            .put("create_fitable_id", CREATE_FITABLE_ID)
            .put("patch_fitable_id", PATCH_FITABLE_ID)
            .put("delete_fitable_id", DELETE_FITABLE_ID)
            .put("retrieve_fitable_id", RETRIEVE_FITABLE_ID)
            .put("list_fitable_id", LIST_FITABLE_ID)
            .build();

    private static final SourceObject SOURCE_OBJECT = new SourceObject(SOURCE_ID, TASK_ID, SOURCE_NAME,
            SOURCE_APP, SOURCE_TYPE);

    private static final OperationContext CONTEXT = OperationContext.custom()
            .operator(OPERATOR).operatorIp(OPERATOR_IP).tenantId(TENANT_ID).build();

    private static final SourceDeclaration DECLARATION;

    private static final String INSERT_SQL;

    private static final String UPDATE_SQL;

    private static final String DELETE_SQL;

    private static final String RETRIEVE_SQL;

    private static final String LIST_SQL;

    static {
        DECLARATION = new SourceDeclaration();
        DECLARATION.setName(UndefinableValue.defined(SOURCE_NAME));
        DECLARATION.setApp(UndefinableValue.defined(SOURCE_APP));
        DECLARATION.setType(UndefinableValue.defined(SOURCE_TYPE));
        DECLARATION.setMetadata(UndefinableValue.defined(METADATA_MAP));
        DECLARATION.setCreateFitableId(UndefinableValue.defined(CREATE_FITABLE_ID));
        DECLARATION.setPatchFitableId(UndefinableValue.defined(PATCH_FITABLE_ID));
        DECLARATION.setDeleteFitableId(UndefinableValue.defined(DELETE_FITABLE_ID));
        DECLARATION.setRetrieveFitableId(UndefinableValue.defined(RETRIEVE_FITABLE_ID));
        DECLARATION.setListFitableId(UndefinableValue.defined(LIST_FITABLE_ID));

        try {
            INSERT_SQL = IoUtils.content(RefreshInTimeSourceAdapterTest.class.getClassLoader(),
                    "sql/insert-source-refresh.sql");
            UPDATE_SQL = IoUtils.content(RefreshInTimeSourceAdapterTest.class.getClassLoader(),
                    "sql/update-source-refresh.sql");
            DELETE_SQL = IoUtils.content(RefreshInTimeSourceAdapterTest.class.getClassLoader(),
                    "sql/delete-source-refresh.sql");
            RETRIEVE_SQL = IoUtils.content(RefreshInTimeSourceAdapterTest.class.getClassLoader(),
                    "sql/retrieve-source-refresh.sql");
            LIST_SQL = IoUtils.content(RefreshInTimeSourceAdapterTest.class.getClassLoader(),
                    "sql/list-source-refresh.sql");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private DynamicSqlExecutor executor;

    private MapSerializer mapSerializer;

    private RefreshInTimeSourceAdapter adapter;

    @BeforeEach
    void setup() {
        this.executor = mock(DynamicSqlExecutor.class);
        this.mapSerializer = mock(MapSerializer.class);
        this.adapter = new RefreshInTimeSourceAdapter(this.executor, this.mapSerializer);
    }

    private void mockSerializeMethod() {
        when(this.mapSerializer.serialize(any())).thenAnswer(args -> {
            Map<String, Object> values = cast(args.getArgument(0));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(values);
        });
    }

    private void mockDeserializeMethod() {
        when(this.mapSerializer.deserialize(any())).thenAnswer(args -> {
            String json = cast(args.getArgument(0));
            ObjectMapper mapper = new ObjectMapper();
            return cast(mapper.readValue(json, Map.class));
        });
    }

    private static void assertSourceEntity(RefreshInTimeSourceEntity actual) {
        assertEquals(SOURCE_ID, actual.getId());
        assertEquals(SOURCE_NAME, actual.getName());
        assertEquals(SOURCE_APP, actual.getApp());
        assertEquals(SourceType.REFRESH_IN_TIME, actual.getType());
        assertTrue(actual.getMetadata().size() == 1 && Objects.equals(actual.getMetadata().get("project"), "hello"));
        assertEquals(CREATE_FITABLE_ID, actual.getCreateFitableId());
        assertEquals(PATCH_FITABLE_ID, actual.getPatchFitableId());
        assertEquals(DELETE_FITABLE_ID, actual.getDeleteFitableId());
        assertEquals(RETRIEVE_FITABLE_ID, actual.getRetrieveFitableId());
        assertEquals(LIST_FITABLE_ID, actual.getListFitableId());
    }

    @Nested
    @DisplayName("测试创建数据源")
    class CreateTest {
        @Test
        @DisplayName("输入合理，创建成功")
        void should_return_created_entity() {
            when(executor.executeUpdate(any(), any())).thenReturn(1);
            mockSerializeMethod();
            mockDeserializeMethod();
            SourceEntity entity = adapter.createExtension(SOURCE_OBJECT, DECLARATION, CONTEXT);
            RefreshInTimeSourceEntity actual = assertInstanceOf(RefreshInTimeSourceEntity.class, entity);
            assertSourceEntity(actual);
            verify(executor, times(1)).executeUpdate(eq(INSERT_SQL), argThat(this::matchInsertArgs));
        }

        private boolean matchInsertArgs(List<?> args) {
            return CollectionUtils.equals(args, Arrays.asList(SOURCE_ID, METADATA_JSON, CREATE_FITABLE_ID,
                    PATCH_FITABLE_ID, DELETE_FITABLE_ID, RETRIEVE_FITABLE_ID, LIST_FITABLE_ID));
        }
    }

    @Nested
    @DisplayName("测试修改数据源")
    class PatchTest {
        @Test
        @DisplayName("输入合理，修改成功")
        void should_execute_update_sql() {
            when(executor.executeUpdate(any(), any())).thenReturn(1);
            mockSerializeMethod();
            adapter.patchExtension(SOURCE_OBJECT, DECLARATION, CONTEXT);
            List<Object> expectedArgs = Arrays.asList(METADATA_JSON, CREATE_FITABLE_ID, PATCH_FITABLE_ID,
                    DELETE_FITABLE_ID, RETRIEVE_FITABLE_ID, LIST_FITABLE_ID, SOURCE_ID);
            verify(executor, times(1)).executeUpdate(eq(UPDATE_SQL),
                    argThat(args -> CollectionUtils.equals(args, expectedArgs)));
        }
    }

    @Nested
    @DisplayName("测试删除数据源")
    class DeleteTest {
        @Test
        @DisplayName("输入合理，删除成功")
        void should_execute_delete_sql() {
            when(executor.executeUpdate(any(), any())).thenReturn(1);
            adapter.deleteExtension(SOURCE_ID, CONTEXT);
            verify(executor, times(1)).executeUpdate(eq(DELETE_SQL),
                    argThat(args -> CollectionUtils.equals(args, Collections.singletonList(SOURCE_ID))));
        }
    }

    @Nested
    @DisplayName("测试检索数据源")
    class RetrieveTest {
        @Test
        @DisplayName("输入合理，检索成功")
        void should_return_entity() {
            when(executor.executeQuery(any(), any())).thenReturn(Collections.singletonList(ROW));
            mockDeserializeMethod();
            SourceEntity entity = adapter.retrieveExtension(SOURCE_OBJECT, CONTEXT);
            RefreshInTimeSourceEntity actual = assertInstanceOf(RefreshInTimeSourceEntity.class, entity);
            assertSourceEntity(actual);
            verify(executor, times(1)).executeQuery(eq(RETRIEVE_SQL),
                    argThat(args -> CollectionUtils.equals(args, Collections.singletonList(SOURCE_ID))));
        }
    }

    @Nested
    @DisplayName("测试查询数据源")
    class ListTest {
        @Test
        @DisplayName("输入合理，查询成功")
        void should_return_entities() {
            when(executor.executeQuery(any(), any())).thenReturn(Collections.singletonList(ROW));
            mockDeserializeMethod();
            Map<String, List<SourceEntity>> grouped = adapter
                    .listExtension(Collections.singletonList(SOURCE_OBJECT), CONTEXT);
            assertEquals(1, grouped.size());
            assertTrue(grouped.containsKey(TASK_ID));
            List<SourceEntity> entities = grouped.get(TASK_ID);
            assertEquals(1, entities.size());
            RefreshInTimeSourceEntity actual = assertInstanceOf(RefreshInTimeSourceEntity.class, entities.get(0));
            assertSourceEntity(actual);
            verify(executor, times(1)).executeQuery(eq(LIST_SQL),
                    argThat(args -> CollectionUtils.equals(args, Collections.singletonList(SOURCE_ID))));
        }
    }
}