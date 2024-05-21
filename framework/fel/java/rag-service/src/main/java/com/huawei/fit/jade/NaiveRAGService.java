package com.huawei.fit.jade;

import com.huawei.fitframework.annotation.Genericable;
import java.util.List;

public interface NaiveRAGService {
    @Genericable(id = "com.huawei.fit.fel.rag.pattern.naiveRAGService")
    String process(Integer topK, List<String> collectionName, String question);
}