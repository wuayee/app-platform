/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import static modelengine.fitframework.util.ObjectUtils.cast;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jane.task.util.PaginationResult;
import com.huawei.fit.jober.entity.InstanceCategoryChanged;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@link AutomaticConvolutionImpl}对应测试类。
 *
 * @author 姚江
 * @since 2023-11-01 13:48
 */
@ExtendWith(MockitoExtension.class)
public class AutomaticConvolutionImplTest {
    private static final String ID_KEY = "id";

    private static final String PARENT_KEY = "decomposed_from";

    private static final String TASK_ID = "ab8919b6320d44aaa8dd60d75a5bb147";

    private static final String INSTANCE_ID = "1387c2bf05014f8e804f92c879353b34";

    private static final String PARENT_INSTANCE_ID = "9019dc355fbb4c72a1108bda9b5cd0b8";

    private static final String CUSTOM_PARENT_ID = "39fc82481ec84931805cbc9ee3045cb9";

    private TaskService taskService;

    private TaskInstance.Repo repo;

    private AutomaticConvolutionImpl automaticConvolution;

    String tenantId = "testId";

    String operator = "TEST.";

    String operatorIp = "192.168.0.11";

    @BeforeEach
    void before() {
        this.taskService = mock(TaskService.class);
        this.repo = mock(TaskInstance.Repo.class);
        this.automaticConvolution = new AutomaticConvolutionImpl(this.taskService, this.repo);
    }

    @Test
    void test() {
        InstanceCategoryChanged change = new InstanceCategoryChanged();
        change.setTaskId(TASK_ID);
        change.setInstanceId(INSTANCE_ID);
        change.setNewCategory("已完成");

        TaskEntity task = new TaskEntity();
        task.setId(TASK_ID);
        task.setProperties(Arrays.asList(
                mockProperty("id", Collections.emptyMap()),
                mockProperty("decomposed_from", Collections.emptyMap()),
                mockProperty("status", Collections.singletonMap("complete", "已完成"))
        ));
        when(this.taskService.retrieve(eq(TASK_ID), any())).thenReturn(task);

        TaskInstance instance = mock(TaskInstance.class);
        when(instance.info()).thenReturn(MapBuilder.<String, Object>get()
                .put(PARENT_KEY, CUSTOM_PARENT_ID)
                .put("status", "complete")
                .build());
        when(instance.categories()).thenReturn(Collections.singletonList("已完成"));
        TaskInstance parent = mock(TaskInstance.class);
        when(parent.id()).thenReturn(PARENT_INSTANCE_ID);

        when(this.repo.retrieve(same(task), eq(INSTANCE_ID), eq(false), any())).thenReturn(instance);
        PagedResultSet<TaskInstance> instances = cast(mock(PagedResultSet.class));
        when(instances.pagination()).thenReturn(PaginationResult.create(0, 200, 1));
        when(instances.results()).thenReturn(Collections.singletonList(instance));
        when(this.repo.list(same(task), argThat(arg -> matchFilter(arg, PARENT_KEY, CUSTOM_PARENT_ID)),
                argThat(arg -> matchPagination(arg, 0, 200)), anyList(), any(), any())).thenReturn(instances);

        PagedResultSet<TaskInstance> parents = cast(mock(PagedResultSet.class));
        when(parents.results()).thenReturn(Collections.singletonList(parent));
        when(this.repo.list(same(task), argThat(arg -> matchFilter(arg, ID_KEY, CUSTOM_PARENT_ID)),
                argThat(arg -> matchPagination(arg, 0, 1)), anyList(), any(), any())).thenReturn(parents);

        this.automaticConvolution.process(Collections.singletonList(change));

        verify(this.repo, times(1)).patch(same(task), eq(PARENT_INSTANCE_ID), argThat(arg -> !arg.typeId().defined()
                && !arg.sourceId().defined() && !arg.tags().defined() && arg.info().defined()
                && arg.info().get().size() == 1 && Objects.equals(arg.info().get().get("status"), "complete")), any());
    }

    private static boolean matchFilter(TaskInstance.Filter filter, String key, String value) {
        List<String> values = filter.infos().get(key);
        if (CollectionUtils.isEmpty(values)) {
            return false;
        }
        return Objects.equals(values.get(0), value);
    }

    private static boolean matchPagination(Pagination pagination, long offset, int limit) {
        return pagination.offset() == offset && pagination.limit() == limit;
    }

    private static TaskProperty mockProperty(String name, Map<String, String> categoryMappings) {
        List<PropertyCategory> categories = categoryMappings.entrySet().stream()
                .map(entry -> new PropertyCategory(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return TaskProperty.custom().name(name).categories(categories).build();
    }
}
