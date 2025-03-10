/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.document.extractor;

import modelengine.fel.core.chat.ChatMessage;
import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.support.ChatMessages;
import modelengine.fel.core.chat.support.HumanMessage;
import modelengine.fit.jade.aipp.document.code.DocumentExtractRetCode;
import modelengine.fit.jade.aipp.document.exception.DocumentExtractException;
import modelengine.fit.jober.aipp.service.OperatorService.FileType;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.resource.web.Media;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 图片提取工具。
 *
 * @author 马朝阳
 * @since 2024-12-12
 */
@Component
public class ImageExtractor implements BaseExtractor {
    private static final Logger LOG = Logger.get(ImageExtractor.class);

    private final ChatModel chatModel;

    public ImageExtractor(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 表示圖片内容提取接口。
     *
     * @param fileUrl 文件链接。
     * @param context 文件提取额外参数。
     * @return 表示文件内容的 {@link String}。
     */
    @Override
    public String extract(String fileUrl, Map<String, Object> context) {
        if (MapUtils.isEmpty(context) || !context.containsKey("prompt")) {
            LOG.error("There is no key of prompt when extract prompt, fileUrl:{0}", fileUrl);
            throw new DocumentExtractException(DocumentExtractRetCode.EMPTY_EXTRACT_PARAM, "prompt");
        }
        try {
            String prompt = ObjectUtils.cast(context.get("prompt"));
            if (StringUtils.isEmpty(prompt)) {
                prompt = "请描述一下图片。";
            }
            ChatMessages chatMessages =
                    ChatMessages.from(Arrays.asList(new HumanMessage(prompt),
                            new HumanMessage(StringUtils.EMPTY,
                                    Collections.singletonList(new Media(new URL(fileUrl))))));
            ChatOption option = ChatOption.custom().model("Qwen2-VL").stream(false).build();
            List<ChatMessage> messages = chatModel.generate(chatMessages, option).blockAll();
            if (CollectionUtils.isEmpty(messages)) {
                LOG.error("chat model response is empty.");
                return StringUtils.EMPTY;
            }
            String ans = messages.get(0).text();
            LOG.info("question={} ans={}", ObjectUtils.<String>cast(chatMessages.messages().get(0).text()), ans);
            return ans;
        } catch (MalformedURLException e) {
            throw new DocumentExtractException(DocumentExtractRetCode.WRONG_FILE_URL, fileUrl);
        }
    }

    @Override
    public FileType type() {
        return FileType.IMAGE;
    }
}
