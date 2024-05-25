/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.client.http.websocket;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.client.Request;
import com.huawei.fit.client.Response;
import com.huawei.fit.http.websocket.CloseReason;
import com.huawei.fit.http.websocket.Session;
import com.huawei.fit.http.websocket.client.WebSocketClassicListener;
import com.huawei.fit.serialization.MessageSerializer;
import com.huawei.fit.serialization.http.websocket.FailMessageContentUtils;
import com.huawei.fit.serialization.http.websocket.RequestMessageContentUtils;
import com.huawei.fit.serialization.http.websocket.ResponseMessageContentUtils;
import com.huawei.fit.serialization.http.websocket.StreamMessageType;
import com.huawei.fit.serialization.http.websocket.WebSocketUtils;
import com.huawei.fit.serialization.http.websocket.WebSocketWorkerObserver;
import com.huawei.fit.serialization.util.MessageSerializerUtils;
import com.huawei.fit.serialization.util.PublisherCategory;
import com.huawei.fitframework.broker.ExceptionInfo;
import com.huawei.fitframework.broker.FitExceptionCreator;
import com.huawei.fitframework.exception.ClientException;
import com.huawei.fitframework.flowable.Emitter;
import com.huawei.fitframework.flowable.Publisher;
import com.huawei.fitframework.flowable.choir.FlexibleEmitterChoir;
import com.huawei.fitframework.flowable.solo.FlexibleEmitterSolo;
import com.huawei.fitframework.flowable.util.worker.Worker;
import com.huawei.fitframework.flowable.util.worker.WorkerObserver;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.serialization.ResponseMetadata;
import com.huawei.fitframework.serialization.TagLengthValues;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.LockUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 表示流式调用的调用器。
 *
 * @author 何天放 h00679269
 * @since 2024-05-08
 */
public class WebsocketInvoker implements WebSocketClassicListener, WebSocketInvokeRequester {
    private static final Logger log = Logger.get(WebsocketInvoker.class);
    private static final int RETURN_INDEX = -1;

    private final BeanContainer container;
    private final String genericableId;
    private final String fitableId;
    private final MessageSerializer messageSerializer;
    private final Type returnType;
    private final Object signal = LockUtils.newSynchronizedLock();
    private final LazyLoader<FitExceptionCreator> exceptionCreatorLoader = new LazyLoader<>(this::loadExceptionCreator);
    private final Map<Integer, Worker<Object>> workers = new HashMap<>();
    private final Map<Integer, BiConsumer<Session, TagLengthValues>> handlers = new HashMap<>();
    private Type returnElementType;
    private Emitter<Object> emitter;
    private Response response;

    WebsocketInvoker(BeanContainer container, Request request) {
        notNull(request, "The request cannot be null.");
        this.container = notNull(container, "The bean container cannot be null.");
        this.genericableId = request.metadata().genericableId();
        this.fitableId = request.metadata().fitableId();
        this.returnType = request.returnType();
        this.messageSerializer =
                MessageSerializerUtils.getMessageSerializer(this.container, request.metadata().dataFormat())
                        .orElseThrow(() -> new IllegalStateException(StringUtils.format(
                                "MessageSerializer required but not found. [format={0}]",
                                request.metadata().dataFormat())));
        this.handlers.put(StreamMessageType.RESPONSE.code(), this::doResponseMessageHandler);
        this.handlers.put(StreamMessageType.CONSUME.code(), this::doConsumeMessageHandler);
        this.handlers.put(StreamMessageType.COMPLETE.code(), this::doCompleteMessageHandler);
        this.handlers.put(StreamMessageType.FAIL.code(), this::doFailMessageHandler);
        this.handlers.put(StreamMessageType.REQUEST_ELEMENT.code(), this::doRequestElementHandler);
        this.handlers.put(StreamMessageType.CANCEL.code(), this::doCancelMessageHandler);
        this.handlers.put(StreamMessageType.UNKNOWN.code(), this::doUnknownMessageHandler);
    }

    @Override
    public void request(Session session, Request request) {
        Object[] convertedArguments = this.convertPublisherTypeArguments(session, request.data(), request.dataTypes());
        this.sendRequestMessage(session, request, convertedArguments);
    }

    @Override
    public Response waitAndgetResponse() throws InterruptedException {
        synchronized (this.signal) {
            this.signal.wait();
        }
        return this.response;
    }

    @Override
    public void onMessage(Session session, byte[] message) {
        TagLengthValues tlvs = TagLengthValues.deserialize(message);
        int type = WebSocketUtils.getType(tlvs);
        this.handlers.get(type).accept(session, tlvs);
    }

    @Override
    public void onClose(Session session, int code, String reason) {
        if (code != CloseReason.NORMAL_CLOSURE.getCode()) {
            throw new ClientException(StringUtils.format(
                    "Websocket connection was closed unexpectedly. [code={0}, reason={1}]",
                    code,
                    reason));
        }
    }

    @Override
    public void onOpen(Session session) {}

    @Override
    public void onMessage(Session session, String message) {}

    @Override
    public void onError(Session session, Throwable cause) {
        throw new ClientException(StringUtils.format(
                "Error occurred in websocket connection. [genericableId={0}, fitableId={1}]",
                this.genericableId,
                this.fitableId), cause);
    }

    private void doConsumeMessageHandler(Session session, TagLengthValues message) {
        byte[] content = WebSocketUtils.getContent(message);
        this.emitter.emit(this.messageSerializer.deserializeResponse(this.returnElementType, content));
    }

    private void doCompleteMessageHandler(Session session, TagLengthValues message) {
        this.emitter.complete();
    }

    private void doFailMessageHandler(Session session, TagLengthValues message) {
        byte[] content = WebSocketUtils.getContent(message);
        TagLengthValues failMessageContent = TagLengthValues.deserialize(content);
        ExceptionInfo exceptionInfo = ExceptionInfo.create(this.genericableId,
                this.fitableId,
                FailMessageContentUtils.getCode(failMessageContent),
                FailMessageContentUtils.getMessage(failMessageContent),
                FailMessageContentUtils.getExceptionProperties(failMessageContent));
        this.emitter.fail(this.exceptionCreatorLoader.get().buildException(exceptionInfo));
    }

    private void doRequestElementHandler(Session session, TagLengthValues message) {
        int index = WebSocketUtils.getIndex(message);
        byte[] content = WebSocketUtils.getContent(message);
        long requestCount = Long.parseLong(new String(content, StandardCharsets.UTF_8));
        this.workers.get(index).request(requestCount);
    }

    private void doCancelMessageHandler(Session session, TagLengthValues message) {
        int index = WebSocketUtils.getIndex(message);
        this.workers.get(index).cancel();
    }

    private void doUnknownMessageHandler(Session session, TagLengthValues message) {
        log.warn(StringUtils.format("Cannot handle message with unknown type."));
    }

    private void doResponseMessageHandler(Session session, TagLengthValues message) {
        byte[] content = WebSocketUtils.getContent(message);
        TagLengthValues responseMessageContent = TagLengthValues.deserialize(content);
        byte[] entity = ResponseMessageContentUtils.getEntity(responseMessageContent);
        int format = ResponseMessageContentUtils.getDataFormat(responseMessageContent);
        int errorCode = ResponseMessageContentUtils.getCode(responseMessageContent);
        String errorMessage = ResponseMessageContentUtils.getMessage(responseMessageContent);
        TagLengthValues extensions = ResponseMessageContentUtils.getExtensions(responseMessageContent);
        ResponseMetadata responseMetadata = ResponseMetadata.custom()
                .dataFormat(format)
                .code(errorCode)
                .message(errorMessage)
                .tagValues(extensions)
                .build();
        Object result = getConvertedReturnValue(session, errorCode, entity);
        this.response = Response.create(responseMetadata, result);
        synchronized (this.signal) {
            this.signal.notifyAll();
        }
    }

    private Object[] convertPublisherTypeArguments(Session session, Object[] arguments, Type[] argumentTypes) {
        Object[] convertedArguments = new Object[arguments.length];
        for (int index = 0; index < arguments.length; index++) {
            if (PublisherCategory.fromType(argumentTypes[index]) == PublisherCategory.NON_PUBLISHER) {
                convertedArguments[index] = arguments[index];
                continue;
            }
            Type argumentElementType = getPublisherDataType(argumentTypes[index]);
            WorkerObserver<Object> observer = new WebSocketWorkerObserver(session,
                    this.messageSerializer,
                    argumentElementType,
                    index,
                    (argumentSession, argumentIndex) -> {});
            Publisher<Object> publisher = ObjectUtils.cast(Validation.isInstanceOf(arguments[index],
                    Publisher.class,
                    StringUtils.format("The argument type is not Publisher. [index={0}, type={1}]",
                            index,
                            arguments[index].getClass())));
            Worker<Object> worker = Worker.create(observer, publisher, index);
            this.workers.put(index, worker);
            worker.run();
            convertedArguments[index] = null;
        }
        return convertedArguments;
    }

    private Object getConvertedReturnValue(Session session, int errorCode, byte[] entity) {
        Object result = null;
        if (errorCode == ResponseMetadata.CODE_OK) {
            PublisherCategory category = PublisherCategory.fromType(returnType);
            if (category == PublisherCategory.NON_PUBLISHER) {
                result = this.messageSerializer.deserializeResponse(this.returnType, entity);
            } else {
                this.returnElementType = getPublisherDataType(this.returnType);
                this.emitter = Emitter.create();
                Consumer<Long> requestElementHandler = value -> this.sendRequestElementMessage(session, value);
                Runnable cancelHandler = () -> {
                    this.sendCancelMessage(session);
                };
                if (category == PublisherCategory.CHOIR) {
                    result =
                            new FlexibleEmitterChoir<>(() -> emitter, null, null, requestElementHandler, cancelHandler);
                } else {
                    result = new FlexibleEmitterSolo<>(() -> emitter, null, null, requestElementHandler, cancelHandler);
                }
            }
        }
        return result;
    }

    private void sendRequestMessage(Session session, Request request, Object[] convertedArguments) {
        byte[] entity = this.messageSerializer.serializeRequest(request.dataTypes(), convertedArguments);
        TagLengthValues requestMessageContent = TagLengthValues.create();
        RequestMessageContentUtils.setDataFormat(requestMessageContent, request.metadata().dataFormat());
        RequestMessageContentUtils.setGenericableVersion(requestMessageContent,
                request.metadata().genericableVersion());
        RequestMessageContentUtils.setExtensions(requestMessageContent, request.metadata().tagValues());
        RequestMessageContentUtils.setEntity(requestMessageContent, entity);
        TagLengthValues requestMessage = TagLengthValues.create();
        WebSocketUtils.setType(requestMessage, StreamMessageType.REQUEST.code());
        WebSocketUtils.setContent(requestMessage, requestMessageContent.serialize());
        session.send(requestMessage.serialize());
    }

    private void sendRequestElementMessage(Session session, long count) {
        TagLengthValues requestMessage = TagLengthValues.create();
        WebSocketUtils.setIndex(requestMessage, RETURN_INDEX);
        WebSocketUtils.setType(requestMessage, StreamMessageType.REQUEST_ELEMENT.code());
        WebSocketUtils.setContent(requestMessage, Long.toString(count).getBytes(StandardCharsets.UTF_8));
        session.send(requestMessage.serialize());
    }

    private void sendCancelMessage(Session session) {
        TagLengthValues cancelMessage = TagLengthValues.create();
        WebSocketUtils.setIndex(cancelMessage, RETURN_INDEX);
        WebSocketUtils.setType(cancelMessage, StreamMessageType.CANCEL.code());
        session.send(cancelMessage.serialize());
    }

    private static Type getPublisherDataType(Type type) {
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Cannot get data type which type is not parameterized type.");
        }
        return ((ParameterizedType) type).getActualTypeArguments()[0];
    }

    private FitExceptionCreator loadExceptionCreator() {
        return container.lookup(FitExceptionCreator.class)
                .map(BeanFactory::<FitExceptionCreator>get)
                .orElseThrow(() -> new IllegalStateException("No fit exception creator."));
    }
}
