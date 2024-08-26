/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.edatamate.repo;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;

import modelengine.fit.waterflow.edatamate.dao.FlowNotificationMapper;
import modelengine.fit.waterflow.edatamate.dao.po.FlowNotificationPo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * @author yangxiangyu
 * @since 2024/8/15
 */
class FlowNotificationRepoImplTest {
    private static FlowNotificationMapper notifyMapper = Mockito.mock(FlowNotificationMapper.class);

    private FlowNotificationRepoImpl flowNotificationRepo;

    @BeforeEach
    void setUp() throws Exception {
        flowNotificationRepo = new FlowNotificationRepoImpl(notifyMapper);
    }

    @Test
    @DisplayName("测试保存FlowNotification成功")
    public void testCreateSuccessful() {
        flowNotificationRepo.create("123", new HashMap<>());

        verify(notifyMapper).create(anyString(), anyString(), anyString(), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("测试查找FlowNotification成功")
    public void testFindAllSuccessful() {
        flowNotificationRepo.findAll();

        verify(notifyMapper).findAll();
    }

    @Test
    @DisplayName("测试删除FlowNotification成功")
    public void testDeleteSuccessful() {
        flowNotificationRepo.delete("123");

        verify(notifyMapper).delete(anyString());
    }

    @Test
    @DisplayName("测试查找下次重试列表成功")
    public void testFindNextRetryListSuccessful() {
        flowNotificationRepo.findNextRetryList(LocalDateTime.now());

        verify(notifyMapper).findNextNotifyList(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("测试更新成功")
    public void testUpdateSuccessful() {
        flowNotificationRepo.update(new FlowNotificationPo("123", "123", "", 1, LocalDateTime.now()));

        verify(notifyMapper).update(anyString(), anyInt(), any(LocalDateTime.class));
    }
}