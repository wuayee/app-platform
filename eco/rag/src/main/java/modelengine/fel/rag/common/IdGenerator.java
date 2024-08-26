/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.common;

import java.util.UUID;

/**
 * 唯一标识生成器。
 * <p>使用UUID随机生成，不保序。</p>
 *
 * @since 2024-05-07
 */
public class IdGenerator {
    /**
     * 获取一个唯一标识。
     *
     * @return 返回生成的唯一标识。
     */
    public static String getId() {
        return UUID.randomUUID().toString();
    }
}
