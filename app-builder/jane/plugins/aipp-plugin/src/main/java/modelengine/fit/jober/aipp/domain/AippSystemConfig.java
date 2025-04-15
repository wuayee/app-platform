/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统配置领域对象.
 *
 * @author 张越
 * @since 2024-11-30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Slf4j
public class AippSystemConfig extends BaseDomain {
    private Long id;
    private String configKey;
    private String configValue;
    private String configGroup;
    private String configParent;
    private JSONObject json;

    /**
     * 获取value中的string属性.
     *
     * @param key 键值.
     * @return 属性值.
     */
    public String getValueAttributeString(String key) {
        if (this.json == null) {
            this.json = JSON.parseObject(this.configValue);
        }
        return this.json.getString(key);
    }
}
