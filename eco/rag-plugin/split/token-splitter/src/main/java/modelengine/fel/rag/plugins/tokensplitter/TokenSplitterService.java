/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.plugins.tokensplitter;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fel.rag.protocol.FlatChunk;
import modelengine.fel.rag.protocol.FlatDocument;
import modelengine.fel.rag.split.DocumentSplitterService;
import modelengine.fel.rag.split.SplitOptions;
import modelengine.fel.rag.split.TokenSplitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Token切分服务的实现。
 *
 * @since 2024-06-03
 */
@Component
public class TokenSplitterService implements DocumentSplitterService {
    @Override
    @Fitable("token-splitter")
    public List<FlatChunk> split(List<FlatDocument> input, SplitOptions options) {
        return new TokenSplitter(options.getTokenSize(), options.getOverlap())
                .split(input.stream()
                        .map(FlatDocument::toDocument)
                        .collect(Collectors.toList()))
                .stream()
                .map(FlatChunk::new)
                .collect(Collectors.toList());
    }
}
