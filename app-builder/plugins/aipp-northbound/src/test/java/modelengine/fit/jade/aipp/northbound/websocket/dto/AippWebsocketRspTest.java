/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.northbound.websocket.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link AippWebsocketRsp} 的测试类。
 *
 * @author 曹嘉美
 * @since 2024-12-17
 */
class AippWebsocketRspTest {
    @Test
    @DisplayName("测试构造 AippWebsocketRsp。")
    void testAippWebsocketRspFields() {
        AippWebsocketRsp aippWebsocketRsp = AippWebsocketRsp.builder()
                .requestId("request123")
                .code(200)
                .msg("Success")
                .data("Some data")
                .isCompleted(true)
                .build();

        assertThat(aippWebsocketRsp.getRequestId()).isEqualTo("request123");
        assertThat(aippWebsocketRsp.getCode()).isEqualTo(200);
        assertThat(aippWebsocketRsp.getMsg()).isEqualTo("Success");
        assertThat(aippWebsocketRsp.getData()).isEqualTo("Some data");
        assertThat(aippWebsocketRsp.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("测试空字段。")
    void testEmptyFields() {
        AippWebsocketRsp emptyResponse = new AippWebsocketRsp();
        assertThat(emptyResponse.getRequestId()).isNull();
        assertThat(emptyResponse.getCode()).isEqualTo(0);
        assertThat(emptyResponse.getMsg()).isNull();
        assertThat(emptyResponse.getData()).isNull();
        assertThat(emptyResponse.isCompleted()).isFalse();
    }
}
