/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import com.huawei.fit.http.HttpMessage;
import com.huawei.fit.http.entity.support.DefaultWritableBinaryEntity;
import com.huawei.fit.http.protocol.ServerResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link WritableBinaryEntity} 的单元测试。
 *
 * @author bWX1068551
 * @since 2023-02-22
 */
@DisplayName("测试 WritableBinaryEntity 类")
class WritableBinaryEntityTest {
    private final byte[] writeBytes = new byte[] {1, 2};
    private WritableBinaryEntity writableBinaryEntity;
    private ServerResponse serverResponse;

    @BeforeEach
    void setup() {
        this.serverResponse = mock(ServerResponse.class);
        final HttpMessage httpMessage = mock(HttpMessage.class);
        this.writableBinaryEntity = new DefaultWritableBinaryEntity(httpMessage, this.serverResponse);
    }

    @Test
    @DisplayName("向 Http 消息体中写入数据成功")
    void shouldReturnWriteSuccessfully() {
        assertDoesNotThrow(() -> this.writableBinaryEntity.write(this.writeBytes));
        assertDoesNotThrow(() -> this.writableBinaryEntity.flush());
    }
}
