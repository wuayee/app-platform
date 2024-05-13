/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fit.server.http.websocket;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.server.DoHttpHandlerException;
import com.huawei.fit.http.websocket.Session;
import com.huawei.fit.http.websocket.annotation.BinaryMessage;
import com.huawei.fit.http.websocket.annotation.OnClose;
import com.huawei.fit.http.websocket.annotation.OnMessage;
import com.huawei.fit.http.websocket.annotation.OnOpen;
import com.huawei.fit.http.websocket.annotation.WebSocketEndpoint;
import com.huawei.fit.serialization.http.FailMessageContentUtils;
import com.huawei.fit.serialization.http.RequestMessageContentUtils;
import com.huawei.fit.serialization.http.ResponseMessageContentUtils;
import com.huawei.fit.serialization.http.StreamMessageType;
import com.huawei.fit.serialization.http.WebSocketUtils;
import com.huawei.fit.serialization.util.MessageSerializerUtils;
import com.huawei.fit.serialization.util.PublisherCategory;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.broker.ExceptionInfo;
import com.huawei.fitframework.broker.FitExceptionCreator;
import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.Genericable;
import com.huawei.fitframework.broker.LocalGenericableRepository;
import com.huawei.fitframework.broker.server.Dispatcher;
import com.huawei.fitframework.broker.server.Response;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.Solo;
import com.huawei.fitframework.flowable.util.worker.Worker;
import com.huawei.fitframework.flowable.util.worker.WorkerObserver;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.RequestMetadata;
import com.huawei.fitframework.serialization.ResponseMetadata;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.serialization.Version;
import com.huawei.fitframework.serialization.tlv.TlvUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 表示处理 FIT WebSocket 通信请求的控制器。
 *
 * @author 何天放 h00679269
 * @since 2024-05-06
 */
@WebSocketEndpoint(path = "/fit/{genericableId}/{fitableId}")
@Component
public class FitWebSocketController {
    private static final Logger log = Logger.get(FitWebSocketController.class);
    private static final int RETURN_INDEX = -1;

    private final BeanContainer container;
    private final Dispatcher dispatcher;
    private final LocalGenericableRepository repository;
    private final FitExceptionCreator exceptionCreator;
    private final Map<String, ConfigurableWebSocketServerContext> contexts = new ConcurrentHashMap<>();
    private final Map<Integer, BiConsumer<Session, TagLengthValues>> handlers = new HashMap<>();

    FitWebSocketController(BeanContainer container, Dispatcher dispatcher, LocalGenericableRepository repository,
            FitExceptionCreator exceptionCreator) {
        this.container = notNull(container, "The container cannot be null.");
        this.dispatcher = notNull(dispatcher, "The dispatcher cannot be null.");
        this.repository = notNull(repository, "The repository cannot be null.");
        this.exceptionCreator = notNull(exceptionCreator, "The exception creator cannot be null.");
        this.handlers.put(StreamMessageType.REQUEST.code(), this::doRequestMessageHandler);
        this.handlers.put(StreamMessageType.CONSUME.code(), this::doConsumeMessageHandler);
        this.handlers.put(StreamMessageType.COMPLETE.code(), this::doCompleteMessageHandler);
        this.handlers.put(StreamMessageType.FAIL.code(), this::doFailMessageHandler);
        this.handlers.put(StreamMessageType.REQUEST_ELEMENT.code(), this::doRequestElementHandler);
        this.handlers.put(StreamMessageType.CANCEL.code(), this::doCancelMessageHandler);
        this.handlers.put(StreamMessageType.UNKNOWN.code(), this::doUnknownMessageHandler);
    }

    /**
     * 当一个新的 WebSocket 会话打开时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param genericableId 表示泛服务唯一标识的 {@link String}。
     * @param fitableId 表示泛服务实现唯一标识的 {@link String}。
     */
    @OnOpen
    public void onOpen(Session session, @PathVariable("genericableId") String genericableId,
            @PathVariable("fitableId") String fitableId) {
        this.contexts.put(session.getId(), ConfigurableWebSocketServerContext.create(genericableId, fitableId));
    }

    /**
     * 当收到 WebSocket 二进制消息时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     * @param message 表示收到的 WebSocket 二进制消息的 {@link String}。
     */
    @OnMessage
    public void onMessage(Session session, @BinaryMessage byte[] message) {
        TagLengthValues tlvs = TagLengthValues.deserialize(message);
        int type = WebSocketUtils.getType(tlvs);
        this.handlers.get(type).accept(session, tlvs);
    }

    /**
     * 当 WebSocket 会话关闭时的处理方法。
     *
     * @param session 表示 WebSocket 会话的 {@link Session}。
     */
    @OnClose
    public void onClose(Session session) {
        log.warn(StringUtils.format("WebSocket connection closed by client. [code={0}, reason={1}]",
                session.getCloseCode(),
                session.getCloseReason()));
        this.contexts.remove(session.getId());
    }

    private void doConsumeMessageHandler(Session session, TagLengthValues message) {
        int index = WebSocketUtils.getIndex(message);
        byte[] content = WebSocketUtils.getContent(message);
        ConfigurableWebSocketServerContext context = this.contexts.get(session.getId());
        context.emitters()
                .get(index)
                .emit(context.messageSerializer()
                        .deserializeResponse(context.publisherArgumentElementTypes().get(index), content));
    }

    private void doCompleteMessageHandler(Session session, TagLengthValues message) {
        int index = WebSocketUtils.getIndex(message);
        this.contexts.get(session.getId()).emitters().get(index).complete();
        this.tryCloseConnection(session, index);
    }

    private void doFailMessageHandler(Session session, TagLengthValues message) {
        int index = WebSocketUtils.getIndex(message);
        byte[] content = WebSocketUtils.getContent(message);
        TagLengthValues failMessageContent = TagLengthValues.deserialize(content);
        ExceptionInfo exceptionInfo = ExceptionInfo.create(this.contexts.get(session.getId()).genericableId(),
                this.contexts.get(session.getId()).fitableId(),
                FailMessageContentUtils.getCode(failMessageContent),
                FailMessageContentUtils.getMessage(failMessageContent),
                TlvUtils.getExceptionProperties(failMessageContent));
        this.contexts.get(session.getId())
                .emitters()
                .get(index)
                .fail(this.exceptionCreator.buildException(exceptionInfo));
        this.tryCloseConnection(session, index);
    }

    private void doRequestElementHandler(Session session, TagLengthValues message) {
        byte[] content = WebSocketUtils.getContent(message);
        long requestElementCount = Long.parseLong(new String(content, StandardCharsets.UTF_8));
        this.contexts.get(session.getId()).worker().request(requestElementCount);
    }

    private void doCancelMessageHandler(Session session, TagLengthValues message) {
        this.contexts.get(session.getId()).worker().cancel();
        this.tryCloseConnection(session, RETURN_INDEX);
    }

    private void doUnknownMessageHandler(Session session, TagLengthValues message) {
        log.warn(StringUtils.format("Cannot handle message with unknown type."));
    }

    private void doRequestMessageHandler(Session session, TagLengthValues message) {
        ConfigurableWebSocketServerContext context = this.contexts.get(session.getId());
        byte[] content = WebSocketUtils.getContent(message);
        TagLengthValues requestMessageContent = TagLengthValues.deserialize(content);
        byte[] entity = RequestMessageContentUtils.getEntity(requestMessageContent);
        context.format(RequestMessageContentUtils.getDataFormat(requestMessageContent));
        context.genericableVersion(RequestMessageContentUtils.getGenericableVersion(requestMessageContent));
        context.extensions(RequestMessageContentUtils.getExtensions(requestMessageContent));
        context.messageSerializer(MessageSerializerUtils.getMessageSerializer(this.container, context.format())
                .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                        "MessageSerializer required but not found. [format={0}]",
                        context.format()))));
        Object[] parameters = this.getDeserializedParameters(session, entity);
        Response response = this.executeMethod(session, parameters);
        Object result = this.getConvertedReturnValue(session, response, context);
        this.sendResponseMessage(session, response, result);
        if (response.metadata().code() != ResponseMetadata.CODE_OK) {
            session.close();
        }
    }

    private Object[] getDeserializedParameters(Session session, byte[] dataBytes) {
        Type[] argumentTypes = Stream.of(this.getMethod(session).getParameters())
                .map(Parameter::getParameterizedType)
                .toArray(Type[]::new);
        Object[] arguments =
                this.contexts.get(session.getId()).messageSerializer().deserializeRequest(argumentTypes, dataBytes);
        this.initPublisherTypeArguments(session, arguments, argumentTypes);
        return arguments;
    }

    private void initPublisherTypeArguments(Session session, Object[] arguments, Type[] argumentTypes) {
        ConfigurableWebSocketServerContext context = this.contexts.get(session.getId());
        for (int index = 0; index < argumentTypes.length; index++) {
            PublisherCategory category = PublisherCategory.fromType(argumentTypes[index]);
            if (category == PublisherCategory.NON_PUBLISHER) {
                continue;
            }
            Emitter<Object> emitter = Emitter.create();
            context.publisherArgumentElementTypes().put(index, getPublisherDataType(argumentTypes[index]));
            context.emitters().put(index, emitter);
            context.publisherFinishedTags().put(index, false);
            int finalIndex = index;
            Consumer<Long> requestElementHandler = value -> this.sendRequestElementMessage(session, finalIndex, value);
            Runnable cancelHandler = () -> {
                this.sendCancelMessage(session, finalIndex);
                this.tryCloseConnection(session, finalIndex);
            };
            if (category == PublisherCategory.CHOIR) {
                arguments[index] = Choir.fromEmitter(emitter, requestElementHandler, cancelHandler);
            } else {
                arguments[index] = Solo.fromEmitter(emitter, requestElementHandler, cancelHandler);
            }
        }
    }

    private Response executeMethod(Session session, Object[] parameters) {
        ConfigurableWebSocketServerContext context = this.contexts.get(session.getId());
        RequestMetadata metadata = RequestMetadata.custom()
                .dataFormat(context.format())
                .genericableId(context.genericableId())
                .genericableVersion(context.genericableVersion())
                .fitableId(context.fitableId())
                .fitableVersion(Version.builder(FitableMetadata.DEFAULT_VERSION).build())
                .tagValues(context.extensions())
                .build();
        return this.dispatcher.dispatch(metadata, parameters);
    }

    private Object getConvertedReturnValue(Session session, Response response,
            ConfigurableWebSocketServerContext context) {
        Type returnType = this.getMethod(session).getGenericReturnType();
        if (response.metadata().code() != ResponseMetadata.CODE_OK
                || PublisherCategory.fromType(returnType) == PublisherCategory.NON_PUBLISHER) {
            return response.data();
        }
        context.publisherFinishedTags().put(RETURN_INDEX, false);
        Type returnElementType = getPublisherDataType(returnType);
        WorkerObserver<Object> observer = new WebSocketWorkerObserver(session,
                context.messageSerializer(),
                returnElementType,
                RETURN_INDEX,
                this::tryCloseConnection);
        context.worker(Worker.create(observer, convertToPublisher(response.data()), RETURN_INDEX, 0));
        context.worker().run();
        return null;
    }

    private void sendRequestElementMessage(Session session, int index, long count) {
        TagLengthValues requestMessage = TagLengthValues.create();
        WebSocketUtils.setIndex(requestMessage, index);
        WebSocketUtils.setType(requestMessage, StreamMessageType.REQUEST_ELEMENT.code());
        WebSocketUtils.setContent(requestMessage, Long.toString(count).getBytes(StandardCharsets.UTF_8));
        session.send(requestMessage.serialize());
    }

    private void sendCancelMessage(Session session, int index) {
        TagLengthValues cancelMessage = TagLengthValues.create();
        WebSocketUtils.setIndex(cancelMessage, index);
        WebSocketUtils.setType(cancelMessage, StreamMessageType.CANCEL.code());
        session.send(cancelMessage.serialize());
    }

    private void sendResponseMessage(Session session, Response response, Object result) {
        Type returnType = this.getMethod(session).getGenericReturnType();
        TagLengthValues responseMessageContent = TagLengthValues.create();
        ResponseMessageContentUtils.setDataFormat(responseMessageContent, response.metadata().dataFormat());
        ResponseMessageContentUtils.setCode(responseMessageContent, response.metadata().code());
        ResponseMessageContentUtils.setMessage(responseMessageContent, response.metadata().message());
        ResponseMessageContentUtils.setExtensions(responseMessageContent, response.metadata().tagValues());
        ResponseMessageContentUtils.setEntity(responseMessageContent,
                this.contexts.get(session.getId()).messageSerializer().serializeResponse(returnType, result));
        TagLengthValues responseMessage = TagLengthValues.create();
        WebSocketUtils.setType(responseMessage, StreamMessageType.RESPONSE.code());
        WebSocketUtils.setContent(responseMessage, responseMessageContent.serialize());
        session.send(responseMessage.serialize());
    }

    private Method getMethod(Session session) {
        ConfigurableWebSocketServerContext context = this.contexts.get(session.getId());
        Genericable genericable = this.repository.get(context.genericableId(), context.genericableVersion().toString())
                .orElseThrow(() -> new DoHttpHandlerException(StringUtils.format(
                        "No genericable. [genericableId={0}, genericableVersion={1}]",
                        context.genericableId(),
                        context.genericableVersion())));
        Method method = genericable.method().method();
        notNull(method, "The genericable method cannot be null. [genericableId={0}]", genericable.id());
        return method;
    }

    private void tryCloseConnection(Session session, int index) {
        this.contexts.get(session.getId()).publisherFinishedTags().put(index, true);
        for (Map.Entry<Integer, Boolean> entry : this.contexts.get(session.getId())
                .publisherFinishedTags()
                .entrySet()) {
            if (!entry.getValue()) {
                return;
            }
        }
        if (!this.contexts.get(session.getId()).finished().compareAndSet(false, true)) {
            return;
        }
        session.close();
        this.contexts.remove(session.getId());
    }

    private static Type getPublisherDataType(Type type) {
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Cannot get data type which type is not parameterized type.");
        }
        return ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    private static <T> T convertToPublisher(Object obj) {
        if (!(obj instanceof Publisher)) {
            throw new IllegalArgumentException("Cannot convert object which type is not publisher type.");
        }
        return ObjectUtils.cast(obj);
    }
}
