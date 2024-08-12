/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.tools;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.databus.sdk.message.MessageType;
import com.huawei.databus.sdk.message.PermissionType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * 表示 {@link DataBusUtils} 的单元测试。
 *
 * @author 王成
 * @since 2024-03-22
 */
@DisplayName("测试 DataBusUtils 类")
class DataBusUtilsTest {
    @Test
    @DisplayName("测试权限对比函数")
    void shouldReturnPermissionComparison() {
        assertThat(DataBusUtils.comparePermission(PermissionType.Write, PermissionType.Read)).isGreaterThan(0);
        assertThat(DataBusUtils.comparePermission(PermissionType.Write, PermissionType.Write)).isEqualTo(0);
        assertThat(DataBusUtils.comparePermission(PermissionType.None, PermissionType.Read)).isLessThan(0);
        assertThat(DataBusUtils.comparePermission(PermissionType.Write, PermissionType.None)).isGreaterThan(0);
    }

    @Test
    @DisplayName("测试生成消息头与预期值相同")
    void headerShouldEqualToGivenValue() {
        ByteBuffer expected = ByteBuffer.wrap(new byte[]{16, 0, 0, 0, 0, 0, 10, 0, 16, 0, 15, 0, 8, 0, 4, 0, 10,
                0, 0, 0, 1, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 33});
        ByteBuffer actual = DataBusUtils.buildMessageHeader(MessageType.ApplyPermission, 100, 1);
        assertThat(isEqualByteBuffer(actual, expected)).isTrue();
    }

    private static boolean isEqualByteBuffer(ByteBuffer buffer1, ByteBuffer buffer2) {
        int limit1 = buffer1.limit();
        int position1 = buffer1.position();

        int position2 = buffer2.position();

        if (buffer1.remaining() != buffer2.remaining()) {
            return false;
        }

        for (int i = position1; i < limit1; i++) {
            if (buffer1.get(i) != buffer2.get(i + position2 - position1)) {
                return false;
            }
        }
        return true;
    }
}
