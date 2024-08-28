/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package modelengine.fit.serialization.test.person;

import lombok.Data;
import modelengine.fitframework.util.StringUtils;

/**
 * 为单元测试提供人名的信息定义。
 *
 * @author 梁济时
 * @since 2020-11-23
 */
@Data
public class PersonName {
    private String first;
    private String middle;
    private String last;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(this.getFirst())) {
            builder.append(this.getFirst());
        }
        if (StringUtils.isNotBlank(this.getMiddle())) {
            builder.append(' ').append(this.getMiddle());
        }
        if (StringUtils.isNotBlank(this.getLast())) {
            builder.append(' ').append(this.getLast());
        }
        return StringUtils.trim(builder.toString());
    }
}
