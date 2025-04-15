/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户自定义灵感大全的结构体
 *
 * @author 陈潇文
 * @since 2024-10-19
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InspirationPo {
    private String aippId;
    private String parentId;
    private String categoryId;
    private String inspirationId;
    private String value;
    private String createUser;
}
