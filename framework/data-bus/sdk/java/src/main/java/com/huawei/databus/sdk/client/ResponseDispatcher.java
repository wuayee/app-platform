/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client;

import com.huawei.databus.sdk.message.ErrorMessageResponse;
import com.huawei.databus.sdk.message.MessageHeader;
import com.huawei.databus.sdk.message.MessageType;
import com.huawei.databus.sdk.tools.Constant;
import com.huawei.databus.sdk.tools.DataBusUtils;
import com.huawei.fitframework.inspection.Validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 负责监听并解析来自服务器的信息，然后将信息分派到不同的队列里。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
class ResponseDispatcher {
    private static final Logger logger = LogManager.getLogger(ResponseDispatcher.class);

    private final Map<Long, BlockingQueue<ByteBuffer>> replyQueues;
    private final SocketChannel socketChannel;
    private volatile boolean isRunning;

    /**
     * 单线程池，只有一个长任务监听信息。
     */
    private final ExecutorService dispatcherService = new ThreadPoolExecutor(1,
            1,
            60,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(1),
            new ThreadPoolExecutor.AbortPolicy());

    public ResponseDispatcher(Map<Long, BlockingQueue<ByteBuffer>> replyQueues, SocketChannel socketChannel) {
        this.replyQueues = replyQueues;
        this.socketChannel = socketChannel;
        this.isRunning = false;
    }

    /**
     * 启动分发器任务。
     */
    public void start() {
        this.isRunning = true;
        dispatcherService.submit(() -> {
            this.startEventLoop();
        });
    }

    /**
     * 返回分发器当前状态。
     *
     * @return 表示分发器是否在工作的 {@code boolean}。
     */
    public boolean isRunning() {
        return this.isRunning;
    }

    private void startEventLoop() {
        ByteBuffer buffer = ByteBuffer.allocate(Constant.BUFFER_SIZE);
        while (this.isRunning) {
            buffer.clear();

            int bytesRead;
            try {
                bytesRead = socketChannel.read(buffer);
                if (bytesRead == -1) {
                    logger.error("[startEventLoop] Disconnected, Broken pipe.");
                    this.shutdownGracefully();
                    return;
                }

                buffer.flip();
                ByteBuffer messageBytes = buffer;
                // 使用循环处理粘包。
                while (messageBytes.hasRemaining()) {
                    // TODO：处理半包。
                    MessageHeader header = MessageHeader.getRootAsMessageHeader(messageBytes);
                    byte type = header.type();
                    long seq = header.seq();
                    int curPacketSize = (int) header.size() + Constant.DATABUS_SERVICE_HEADER_SIZE;

                    // 只在剩余字节过少时抛出异常。
                    Validation.greaterThanOrEquals(messageBytes.remaining(), curPacketSize, "Too few bytes.");
                    logger.info(
                            "[startEventLoop] DataBus message received, [total size={}, body size={}, type={}, seq={}]",
                            messageBytes.remaining(), header.size(), type, seq);

                    messageBytes.position(Constant.DATABUS_SERVICE_HEADER_SIZE);

                    // 将消息体拷贝到新的ByteBuffer里。
                    ByteBuffer messageBody = DataBusUtils.copyFromByteBuffer(messageBytes, (int) header.size());
                    messageBytes = DataBusUtils.copyFromByteBuffer(messageBytes, messageBytes.remaining());

                    this.deliverMessage(seq, type, messageBody);
                }
            } catch (Exception e) {
                // 异常意味着连接问题或者编程错误，此时应该退出
                logger.error("[startEventLoop] message receiving exception.", e);
                this.shutdownGracefully();
                return;
            }
        }
    }

    /**
     * 结束分发器运行。通过关闭 socketChannel 来打断阻塞读。
     */
    void shutdownGracefully() {
        this.isRunning = false;
        this.dispatcherService.shutdownNow();

        for (Map.Entry<Long, BlockingQueue<ByteBuffer>> entry : replyQueues.entrySet()) {
            // 对每一个等待的请求发送空缓冲区，强制其退出。
            this.replyQueues.get(entry.getKey()).offer(ByteBuffer.allocate(0));
        }

        try {
            this.socketChannel.close();
        } catch (IOException ex) {
            logger.error("[startEventLoop] closing socket receiving exception.", ex);
        }
    }

    private void deliverMessage(long seq, byte type, ByteBuffer messageBody) {
        if (this.replyQueues.containsKey(seq)) {
            // 打印错误信息并返回空缓冲区
            if (type == MessageType.Error) {
                ErrorMessageResponse response = ErrorMessageResponse.getRootAsErrorMessageResponse(messageBody);
                logger.error("[deliverMessage] Error message received, [seq={}, error={}]", seq, response.errorType());
                this.replyQueues.get(seq).offer(ByteBuffer.allocate(0));
            } else {
                this.replyQueues.get(seq).offer(messageBody);
            }
        } else {
            logger.error("[deliverMessage] No waiting consumer, [seq={}]", seq);
        }
    }
}
