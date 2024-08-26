/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.edatamate.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * FlowNotificationQueue持久化对象
 *
 * @author yangxiangyu
 * @since 2024/5/27
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FlowNotificationPo {
    /**
     * 重试唯一id标识
     */
    private String id;

    /**
     * 调用的fitableId
     */
    private String fitableId;

    /**
     * 通知参数
     */
    private String data;

    /**
     * 通知次数
     */
    private int notifyCount;

    /**
     * 下次通知时间
     */
    private LocalDateTime nextNotifyTime;
}
