/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.client;

import com.huawei.databus.sdk.message.MessageHeader;
import com.huawei.databus.sdk.tools.Constant;
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
        dispatcherService.submit(() -> {
            this.isRunning = true;
            this.startEventLoop();
        });
    }

    /**
     * 结束分发器运行。通过关闭 socketChannel 来打断阻塞读。
     *
     * @throws IOException 当 socketChannel 非正常关闭。
     */
    public void stop() throws IOException {
        this.isRunning = false;
        this.socketChannel.close();
        this.dispatcherService.shutdownNow();
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

            int bytesRead = 0;
            try {
                bytesRead = socketChannel.read(buffer);
                if (bytesRead == -1) {
                    break;
                }

                buffer.flip();

                // TODO：处理半包和粘包。
                MessageHeader header = MessageHeader.getRootAsMessageHeader(buffer);

                // 读取并打印type和size字段。
                byte type = header.type();
                long seq = header.seq();
                Validation.equals((long) buffer.remaining(),
                        header.size() + Constant.DATABUS_SERVICE_HEADER_SIZE, "Incorrect body payload size");
                logger.info("[startEventLoop] DataBus message received, [size={}, type={}, seq={}]", header.size(),
                        type, seq);

                buffer.position(Constant.DATABUS_SERVICE_HEADER_SIZE);

                // 将消息体拷贝到新的ByteBuffer里。
                ByteBuffer messageBody = ByteBuffer.allocate(buffer.remaining());

                while (buffer.hasRemaining()) {
                    messageBody.put(buffer.get());
                }
                messageBody.flip();

                if (this.replyQueues.containsKey(seq)) {
                    this.replyQueues.get(seq).offer(messageBody);
                } else {
                    logger.error("[startEventLoop] No waiting consumer, [seq={}]", seq);
                }
            } catch (IOException e) {
                // 不退出但是打印日志
                logger.error("[startEventLoop] message receiving exception, [e={}]", e.toString());
            }
        }
    }
}
