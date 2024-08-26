/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import modelengine.fel.rag.Chunk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于Fitable调用的文本块适配类。
 *
 * @since 2024-06-03
 */
@Getter
@AllArgsConstructor
public class FlatChunk {
    private String id;
    @Setter
    private String content;
    @Setter
    private List<String> rowContent;
    @Setter
    private String sourceId;
    @Setter
    private List<Float> embedding;
    @Setter
    private Double score;

    /**
     * 利用Chunk构造 {@link FlatChunk} 实例。
     *
     * @param chunk 表示文本块的{@link Chunk}。
     */
    public FlatChunk(Chunk chunk) {
        this.id = chunk.getId();
        this.content = chunk.getContent();
        this.rowContent = chunk.getRowContent();

        Object object1 = chunk.getMetadata().get("sourceId");
        if (object1 != null && object1 instanceof String) {
            this.sourceId = (String) object1;
        }
        Object object2 = chunk.getMetadata().get("embedding");
        if (object2 != null && object2 instanceof List<?>) {
            this.embedding = (List<Float>) object2; // FORCE to float
        }
        Object object3 = chunk.getMetadata().get("score");
        if (object3 != null && object3 instanceof Double) {
            this.score = (Double) object3;
        }
    }

    /**
     * 将自身转换为Chunk。
     *
     * @return 返回转换后的文本块。
     */
    public Chunk toChunk() {
        Map<String, Object> metadata = new HashMap<>();

        if (sourceId != null) {
            metadata.put("sourceId", sourceId);
        }
        if (embedding != null) {
            metadata.put("embedding", embedding);
        }
        if (score != null) {
            metadata.put("score", score);
        }

        Chunk chunk = new Chunk(id, content, metadata, sourceId);
        chunk.setRowContent(rowContent);

        return chunk;
    }
}
