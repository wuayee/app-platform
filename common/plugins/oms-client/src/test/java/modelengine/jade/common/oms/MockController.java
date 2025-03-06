/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.jade.common.oms;

import static modelengine.fitframework.util.ObjectUtils.cast;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.entity.NamedEntity;
import modelengine.fit.http.entity.PartitionedEntity;
import modelengine.fit.http.entity.support.DefaultNamedEntity;
import modelengine.fitframework.annotation.Component;
import modelengine.jade.oms.response.ResultVo;

import java.util.List;

/**
 * 表示模拟的接口类。
 *
 * @author 李金绪
 * @since 2024-11-30
 */
@Component
public class MockController {
    /**
     * 获取文件列表。
     *
     * @param receivedFiles 表示上传的文件的 {@link PartitionedEntity}。
     * @return 返回文件列表的 {@link DefaultNamedEntity}。
     */
    @PostMapping("/mock/upload")
    public ResultVo<String> upload(PartitionedEntity receivedFiles) {
        List<NamedEntity> entities = cast(receivedFiles.entities());
        StringBuilder sb = new StringBuilder();
        for (NamedEntity entity : entities) {
            if (entity.isText()) {
                sb.append(entity.asText().content()).append(";");
            }
            if (entity.isFile()) {
                sb.append(entity.asFile().filename()).append(";");
            }
        }
        return new ResultVo<>(sb.toString());
    }
}
