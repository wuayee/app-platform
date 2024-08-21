/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.data.repository.support;

import static modelengine.fitframework.util.ObjectUtils.cast;

import com.huawei.databus.sdk.api.DataBusClient;
import com.huawei.databus.sdk.message.ErrorType;
import com.huawei.databus.sdk.message.PermissionType;
import com.huawei.databus.sdk.support.GetMetaDataRequest;
import com.huawei.databus.sdk.support.GetMetaDataResult;
import com.huawei.databus.sdk.support.MemoryIoRequest;
import com.huawei.databus.sdk.support.MemoryIoResult;
import com.huawei.databus.sdk.support.OpenConnectionResult;
import com.huawei.databus.sdk.support.ReleaseMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryRequest;
import com.huawei.databus.sdk.support.SharedMemoryResult;
import modelengine.fit.data.repository.entity.Metadata;
import modelengine.fit.data.repository.entity.MetadataType;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * 使用 DataBus 进行数据传输。
 *
 * @author 王成 w00863339
 * @since 2024/5/30
 */
public enum DataBusRepository {
    INSTANCE;

    private static final int MIN_RECONNECT_DELAY_MILLIS = 5 * 1000;
    private static final int MAX_RECONNECT_DELAY_MILLIS = 320 * 1000;
    private static final Logger log = Logger.get(DataBusRepository.class);

    private final DataBusClient dataBusClient;
    private int reconnectDelay;
    private long nextReconnectTimeMillis;
    private boolean isDataBusEnabled;
    private String host;
    private int port;

    DataBusRepository() {
        this.dataBusClient = DataBusClient.getClient();
    }

    /**
     * 打开通向指定地址和端口的 DataBus 连接。
     *
     * @param host 表示 DataBus 服务地址的 {@code String}。
     * @param port 表示 DataBus 服务端口的 {@code int}。
     * @param isDataBusEnabled 表示 DataBus 服务是否被启动的 {@code boolean}。
     */

    public void init(String host, int port, boolean isDataBusEnabled) {
        this.host = host;
        this.port = port;
        // 仅在Linux系统下使用 DataBus
        this.isDataBusEnabled = isDataBusEnabled && System.getProperty("os.name").toLowerCase().contains("linux");
        if (this.isDataBusEnabled) {
            this.open();
        }
    }

    /**
     * 将指定键的数据从 DataBus 中读出。
     *
     * @param id 表示数据键的 {@code String}。
     * @return 表示数据的 {@code byte[]}。
     */
    public Optional<byte[]> get(String id) {
        if (!checkRunningWithRetry()) {
            return Optional.empty();
        }

        log.info("Using databus to retrieve data. [id={}]", id);
        MemoryIoRequest request = MemoryIoRequest.custom()
                .userKey(id)
                .memoryOffset(0)
                .permissionType(PermissionType.Read)
                .build();
        MemoryIoResult memoryIoResult = this.dataBusClient.readOnce(request);
        if (memoryIoResult.isSuccess()) {
            log.info("Read data using dataBus, [size={}]", memoryIoResult.bytes().length);
            return Optional.of(memoryIoResult.bytes());
        }

        this.processDisconnected(memoryIoResult.errorType());
        log.error("Retrieving data from DataBus service failed. [id={}, reason={}]", id, memoryIoResult.errorType());
        return Optional.empty();
    }

    /**
     * 将指定键的数据和可选的用户元数据存入 DataBus。如果调用前 DataBus 主服务没有连接，本方法会尝试重连。
     *
     * @param id 表示数据键的 {@code String}。
     * @param data 表示数据的 {@code Object}。
     * @return boolean 表示存储是否成功。
     */
    public boolean save(String id, Object data) {
        if (!checkRunningWithRetry()) {
            return false;
        }

        log.info("Store data using dataBus, [id={}]", id);
        byte[] userDataBytes = new byte[1];
        byte[] dataBytes;
        // 判断数据类型并确定元数据
        if (data instanceof byte[]) {
            dataBytes = cast(data);
            userDataBytes[0] = MetadataType.BYTES.id();
        } else {
            String dataStr = cast(data);
            dataBytes = dataStr.getBytes(StandardCharsets.UTF_8);
            userDataBytes[0] = MetadataType.STRING.id();
        }

        // 申请内存。
        SharedMemoryRequest request = SharedMemoryRequest.custom()
                .userKey(id)
                .size(dataBytes.length)
                .build();
        SharedMemoryResult applyResult = dataBusClient.sharedMalloc(request);
        if (!applyResult.isSuccess()) {
            this.processDisconnected(applyResult.errorType());
            log.error("Apply memory from DataBus service failed. id={}, reason={}", id, applyResult.errorType());
            return false;
        }
        // 将数据和用户元数据写入 DataBus。
        MemoryIoRequest ioRequest = MemoryIoRequest.custom()
                .userKey(id)
                .bytes(dataBytes)
                .memoryOffset(0)
                .permissionType(PermissionType.Write)
                .isOperatingUserData(true)
                .userData(userDataBytes)
                .build();
        MemoryIoResult memoryIoResult = this.dataBusClient.writeOnce(ioRequest);
        if (!memoryIoResult.isSuccess()) {
            this.processDisconnected(applyResult.errorType());
            log.error("Writing data from DataBus service failed. id={}, reason={}", id, memoryIoResult.errorType());
            return false;
        }
        return true;
    }

    /**
     * 将指定键对应的内存的元数据从 DataBus 中读出。
     *
     * @param id 表示数据键的 {@code String}。
     * @return 表示元数据的 {@link Optional}{@code <}{@link Metadata}{@code >}。
     */
    public Optional<Metadata> getMetadata(String id) {
        if (!this.checkRunningWithRetry()) {
            return Optional.empty();
        }
        log.info("Using databus to retrieve metadata. [id={}]", id);
        GetMetaDataRequest request = GetMetaDataRequest.custom().userKey(id).build();
        GetMetaDataResult result = this.dataBusClient.readMetaData(request);
        if (result.isSuccess() && result.userData().length == 1) {
            Metadata metadata = new Metadata();
            switch (MetadataType.fromId(result.userData()[0])) {
                case BYTES:
                    metadata.setType(MetadataType.BYTES.code());
                    break;
                case STRING:
                    metadata.setType(MetadataType.STRING.code());
                    break;
                default:
                    throw new IllegalStateException(StringUtils.format("Unsupported type. [cacheId={0}]", id));
            }
            metadata.setLength((int) result.size());
            return Optional.of(metadata);
        } else {
            this.processDisconnected(result.errorType());
            log.error("Retrieving metadata from DataBus service failed. [id={}, reason={}]", id, result.errorType());
        }
        return Optional.empty();
    }

    /**
     * 将指定键的内存块删除。
     *
     * @param id 表示数据键的 {@code String}。
     */
    public void delete(String id) {
        if (!this.checkRunningWithRetry()) {
            return;
        }
        log.info("Release data using dataBus. [id={}]", id);
        ReleaseMemoryRequest releaseMemoryRequest = new ReleaseMemoryRequest.Builder().userKey(id).build();
        dataBusClient.sharedFree(releaseMemoryRequest);
    }

    private boolean open() {
        try {
            OpenConnectionResult result = this.dataBusClient.open(InetAddress.getByName(this.host), this.port);
            if (!result.isSuccess()) {
                this.setDisconnectedState();
                log.warn("DataBus init failed [host={}, port={}, reason={}]", this.host, this.port, result.errorType());
                return false;
            }
            this.setConnectedState();
            log.info("DataBus init succeeded. [host={}, port={}]", this.host, this.port);
            return true;
        } catch (UnknownHostException e) {
            this.setDisconnectedState();
            log.warn("DataBus init failed, [exception={}]", e.toString());
            return false;
        }
    }

    /**
     * 增加重连延迟，每次在原有的延迟上加倍。重连延迟有上限和下限。
     */
    private void setDisconnectedState() {
        if (this.reconnectDelay < MIN_RECONNECT_DELAY_MILLIS) {
            this.reconnectDelay = MIN_RECONNECT_DELAY_MILLIS;
        } else {
            this.reconnectDelay *= 2;
            if (this.reconnectDelay > MAX_RECONNECT_DELAY_MILLIS) {
                this.reconnectDelay = MAX_RECONNECT_DELAY_MILLIS;
            }
        }
        this.nextReconnectTimeMillis = System.currentTimeMillis() + this.reconnectDelay;
        log.info("DataBus Disconnected. [time={}, delay={}]", this.nextReconnectTimeMillis, this.reconnectDelay);
    }

    /**
     * 重置连接状态。
     */
    private void setConnectedState() {
        this.reconnectDelay = 0;
        this.nextReconnectTimeMillis = 0L;
    }

    /**
     * 返回 DataBus 服务是否可用。
     *
     * @return 表示 DataBus 是否可用的 {@code boolean}。
     */
    boolean isRunning() {
        return this.isDataBusEnabled && this.dataBusClient.isConnected();
    }

    /**
     * 返回 DataBus 服务是否可用。如果不可用，则在重连冷却到期后进行重试。
     *
     * @return 表示 DataBus 是否可用的 {@code boolean}
     */
    public boolean checkRunningWithRetry() {
        if (this.isRunning()) {
            return true;
        }

        if (System.currentTimeMillis() < this.nextReconnectTimeMillis) {
            return false;
        }

        return this.open();
    }

    private void processDisconnected(byte errorType) {
        if (errorType == ErrorType.NotConnectedToDataBus && this.isRunning()) {
            this.setDisconnectedState();
        }
    }
}
