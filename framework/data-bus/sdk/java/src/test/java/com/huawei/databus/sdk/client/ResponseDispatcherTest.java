/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.databus.sdk.message.ApplyPermissionMessageResponse;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.databus.sdk.message.MessageType;
import com.huawei.databus.sdk.tools.DataBusUtils;
import com.huawei.databus.sdk.tools.SeqGenerator;

import com.google.flatbuffers.FlatBufferBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 表示 {@link ResponseDispatcher} 的单元测试。
 *
 * @author 王成 w00863339
 * @since 2024-07-02
 */
@DisplayName("测试 ResponseDispatcher 类")
class ResponseDispatcherTest {
    @Test
    @DisplayName("测试分发器优雅关闭逻辑正确运行")
    void shouldPerformShutdownGracefully() throws IOException {
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        Map<Long, BlockingQueue<ByteBuffer>> replyQueues = new HashMap<>();
        BlockingQueue<ByteBuffer> queue = new ArrayBlockingQueue<>(1);
        replyQueues.put(1L, queue);
        ResponseDispatcher responseDispatcher = new ResponseDispatcher(replyQueues, socketChannelMock);

        assertThat(replyQueues.get(1L).size()).isEqualTo(0);
        responseDispatcher.shutdownGracefully();
        // 验证所有挂起接收方都获取0字节长度的 ByteBuffer。
        assertThat(replyQueues.get(1L).size()).isEqualTo(1);
        assertThat(Objects.requireNonNull(replyQueues.get(1L).poll()).remaining()).isEqualTo(0);
        Mockito.verify(socketChannelMock, Mockito.atLeastOnce()).close();
    }

    @Test
    @DisplayName("测试分发器返回正确")
    void shouldReturnCorrectReply() throws IOException, InterruptedException {
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        final long seq = SeqGenerator.getInstance().getNextNumber();
        final ByteBuffer messageBody = getSampleMessage();
        final ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ApplyMemory,
                messageBody.remaining(), seq);
        Mockito.doAnswer(invocation -> {
            ByteBuffer buffer = invocation.getArgument(0);
            // 写入一个整包。
            copyFromByteBuffer(messageHeaderBuffer, buffer);
            copyFromByteBuffer(messageBody, buffer);
            return buffer.position();
        }).when(socketChannelMock).read(Mockito.any(ByteBuffer.class));

        Map<Long, BlockingQueue<ByteBuffer>> replyQueues = new HashMap<>();
        BlockingQueue<ByteBuffer> queue = new ArrayBlockingQueue<>(1);
        replyQueues.put(seq, queue);
        ResponseDispatcher responseDispatcher = new ResponseDispatcher(replyQueues, socketChannelMock);

        assertThat(replyQueues.get(seq).size()).isEqualTo(0);
        responseDispatcher.start();
        ByteBuffer reply = Objects.requireNonNull(replyQueues.get(seq).take());
        assertThat(isEqualByteBuffer(reply, getSampleMessage())).isTrue();
    }

    @Test
    @DisplayName("测试粘包时分发器返回正确")
    void shouldReturnCorrectReplyOnStickyPacket() throws IOException, InterruptedException {
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        final long seq1 = SeqGenerator.getInstance().getNextNumber();
        final ByteBuffer messageBody1 = getSampleMessage();
        final ByteBuffer messageHeaderBuffer1 = DataBusUtils.buildMessageHeader(MessageType.ApplyMemory,
                messageBody1.remaining(), seq1);
        final long seq2 = SeqGenerator.getInstance().getNextNumber();
        final ByteBuffer messageBody2 = getSampleMessage();
        final ByteBuffer messageHeaderBuffer2 = DataBusUtils.buildMessageHeader(MessageType.ApplyMemory,
                messageBody2.remaining(), seq2);
        Mockito.doAnswer(invocation -> {
            ByteBuffer buffer = invocation.getArgument(0);
            // 写入一个包含两个整包的粘包。
            copyFromByteBuffer(messageHeaderBuffer1, buffer);
            copyFromByteBuffer(messageBody1, buffer);
            copyFromByteBuffer(messageHeaderBuffer2, buffer);
            copyFromByteBuffer(messageBody2, buffer);
            return buffer.position();
        }).when(socketChannelMock).read(Mockito.any(ByteBuffer.class));

        Map<Long, BlockingQueue<ByteBuffer>> replyQueues = new HashMap<>();
        BlockingQueue<ByteBuffer> queue1 = new ArrayBlockingQueue<>(1);
        replyQueues.put(seq1, queue1);
        BlockingQueue<ByteBuffer> queue2 = new ArrayBlockingQueue<>(1);
        replyQueues.put(seq2, queue2);
        ResponseDispatcher responseDispatcher = new ResponseDispatcher(replyQueues, socketChannelMock);

        assertThat(replyQueues.get(seq1).size()).isEqualTo(0);
        assertThat(replyQueues.get(seq2).size()).isEqualTo(0);
        responseDispatcher.start();
        ByteBuffer reply1 = Objects.requireNonNull(replyQueues.get(seq1).take());
        assertThat(isEqualByteBuffer(reply1, getSampleMessage())).isTrue();
        ByteBuffer reply2 = Objects.requireNonNull(replyQueues.get(seq2).take());
        assertThat(isEqualByteBuffer(reply2, getSampleMessage())).isTrue();
    }

    @Test
    @DisplayName("测试半包时分发器返回0长度缓冲区")
    void shouldReturnCorrectReplyOnShortPacket() throws IOException, InterruptedException {
        SocketChannel socketChannelMock = Mockito.mock(SocketChannel.class);
        final long seq = SeqGenerator.getInstance().getNextNumber();
        final ByteBuffer messageBody = getSampleMessage();
        final ByteBuffer messageHeaderBuffer = DataBusUtils.buildMessageHeader(MessageType.ApplyMemory,
                messageBody.remaining(), seq);
        Mockito.doAnswer(invocation -> {
            ByteBuffer buffer = invocation.getArgument(0);
            // 写入一个不含消息体的半包。
            copyFromByteBuffer(messageHeaderBuffer, buffer);
            return buffer.position();
        }).when(socketChannelMock).read(Mockito.any(ByteBuffer.class));

        Map<Long, BlockingQueue<ByteBuffer>> replyQueues = new HashMap<>();
        BlockingQueue<ByteBuffer> queue = new ArrayBlockingQueue<>(1);
        replyQueues.put(seq, queue);
        ResponseDispatcher responseDispatcher = new ResponseDispatcher(replyQueues, socketChannelMock);

        assertThat(replyQueues.get(seq).size()).isEqualTo(0);
        responseDispatcher.start();
        assertThat(Objects.requireNonNull(replyQueues.get(seq).take()).remaining()).isEqualTo(0);
        Mockito.verify(socketChannelMock, Mockito.never()).close();
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

    private static ByteBuffer getSampleMessage() {
        FlatBufferBuilder bodyBuilder = new FlatBufferBuilder();
        ApplyPermissionMessageResponse.startApplyPermissionMessageResponse(bodyBuilder);
        ApplyPermissionMessageResponse.addErrorType(bodyBuilder, ErrorType.None);
        ApplyPermissionMessageResponse.addGranted(bodyBuilder, true);
        ApplyPermissionMessageResponse.addMemoryKey(bodyBuilder, 1);
        ApplyPermissionMessageResponse.addMemorySize(bodyBuilder, 100L);
        int messageOffset = ApplyPermissionMessageResponse.endApplyPermissionMessageResponse(bodyBuilder);
        bodyBuilder.finish(messageOffset);
        return bodyBuilder.dataBuffer();
    }

    private static void copyFromByteBuffer(ByteBuffer source, ByteBuffer destination) {
        int total = source.remaining();
        for (int i = 0; i < total; i++) {
            destination.put(source.get());
        }
    }
}