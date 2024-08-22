/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.model.openai.client;

import modelengine.fel.chat.protocol.FlatChatMessage;
import modelengine.fel.model.openai.entity.chat.OpenAiChatCompletionResponse;
import modelengine.fel.model.openai.utils.OpenAiMessageUtils;
import modelengine.fitframework.flowable.Emitter;
import modelengine.fitframework.inspection.Validation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 处理 OpenAI 流式响应的回调。
 *
 * @author 张庭怿
 * @since 2024-5-16
 */
public class ChatStreamCallback implements Callback<ResponseBody> {
    private static final String RESPONSE_BODY_DATA_PREFIX = "data:";

    private static final String END_FLAG = "[DONE]";

    private final Emitter<FlatChatMessage> emitter;

    public ChatStreamCallback(Emitter<FlatChatMessage> emitter) {
        this.emitter = Validation.notNull(emitter, "The emitter cannot be null.");
    }

    private void emitData(BufferedReader reader) throws IOException {
        String line;
        String data = null;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(RESPONSE_BODY_DATA_PREFIX)) {
                data = line.substring(RESPONSE_BODY_DATA_PREFIX.length()).trim();
            } else if (line.isEmpty() && data != null) {
                if (END_FLAG.equals(data)) {
                    return;
                }

                OpenAiChatCompletionResponse response =
                        OpenAiMessageUtils.OBJECT_MAPPER.readValue(data, OpenAiChatCompletionResponse.class);
                if (response == null) {
                    throw new IllegalArgumentException("Failed to parse response from: " + data);
                }

                emitter.emit(OpenAiMessageUtils.buildFelAiMessage(response));
                data = null;
            } else {
                throw new IllegalArgumentException("Unrecognized data format: " + line);
            }
        }
    }

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        if (!response.isSuccessful()) {
            StringBuilder errMsg = new StringBuilder(response.message());
            try {
                errMsg.append(response.errorBody() == null ? "" : ": " + response.errorBody().string());
            } catch (IOException e) {
                // 此异常与模型响应错误无关，所以暂不处理。
            }
            this.onFailure(call, new IllegalStateException(errMsg.toString()));
            return;
        }
        try (ResponseBody body = Validation.notNull(response.body(), "The stream response body is null");
            InputStream in = body.byteStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            emitData(reader);
            emitter.complete();
        } catch (IllegalArgumentException | IOException exception) {
            this.onFailure(call, exception);
        }
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
        emitter.fail(new IllegalStateException(throwable));
    }
}