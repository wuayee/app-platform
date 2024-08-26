/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.broker.Target;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PodAffinityInvokerFilter对应测试类
 *
 * @author yangxiangyu
 * @since 2024/8/9
 */
class PodAffinityInvokerFilterTest {
    @InjectMocks
    private PodAffinityInvokerFilter podAffinityInvokerFilter;

    private AutoCloseable mockitoCloseable;

    @BeforeEach
    void setUp() throws Exception {
        mockitoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockitoCloseable.close();
    }

    @Test
    void testFilterWithEmptyList() {
        List<Target> toFilterTargets = new ArrayList<>();
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("taskInstanceId", "123");

        List<Target> result = podAffinityInvokerFilter.filter(null, "localWorkerId", toFilterTargets, extensions);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testFilterWithNonEmptyList() {
        Target target1 = mock(Target.class);
        Target target2 = mock(Target.class);
        when(target1.workerId()).thenReturn("worker1");
        when(target2.workerId()).thenReturn("worker2");

        List<Target> toFilterTargets = Arrays.asList(target1, target2);
        Map<String, Object> extensions = new HashMap<>();
        extensions.put("taskInstanceId", "123");

        List<Target> result = podAffinityInvokerFilter.filter(null, "localWorkerId", toFilterTargets, extensions);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testFilterWithTaskInstanceIdAbsent() {
        Target target1 = mock(Target.class);
        Target target2 = mock(Target.class);
        when(target1.workerId()).thenReturn("worker1");
        when(target2.workerId()).thenReturn("worker2");

        List<Target> toFilterTargets = Arrays.asList(target1, target2);
        Map<String, Object> extensions = new HashMap<>();

        List<Target> result = podAffinityInvokerFilter.filter(null, "localWorkerId", toFilterTargets, extensions);

        Assertions.assertEquals(1, result.size());
    }
}