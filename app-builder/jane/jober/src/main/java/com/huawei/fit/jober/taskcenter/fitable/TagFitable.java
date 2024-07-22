/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.waterflow.biz.task.TagService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;

import java.util.List;
import java.util.Map;

/**
 * TagService的实现
 *
 * @author songyongtan
 * @since 2024/7/15
 */
@Component
public class TagFitable implements TagService {
    private com.huawei.fit.jober.taskcenter.service.TagService tagService;

    public TagFitable(com.huawei.fit.jober.taskcenter.service.TagService tagService) {
        this.tagService = tagService;
    }

    @Override
    @Fitable(id = "f65d41b8425b407ab7e7ff8129293def")
    public void add(String objectType, String objectId, String tag, OperationContext context) {
        this.tagService.add(objectType, objectId, tag, context);
    }

    @Override
    @Fitable(id = "11c17b23d6b04693a5a54e15a1394a5f")
    public void save(String objectType, String objectId, List<String> tags, OperationContext context) {
        this.tagService.save(objectType, objectId, tags, context);
    }

    @Override
    @Fitable(id = "bf31d1f71deb434c98e08d0c91ecc35c")
    public void save(String objectType, Map<String, List<String>> tags, OperationContext context) {
        this.tagService.save(objectType, tags, context);
    }

    @Override
    @Fitable(id = "72c325fff2c74d5caeaec49c2168d917")
    public void remove(String objectType, String objectId, String tag, OperationContext context) {
        this.tagService.remove(objectType, objectId, tag, context);
    }

    @Override
    @Fitable(id = "ef7cc310b4574101b80a74f18fa28196")
    public List<String> list(String objectType, String objectId, OperationContext context) {
        return this.tagService.list(objectType, objectId, context);
    }

    @Override
    @Fitable(id = "9ef8295620f341978236e977c0bacd89")
    public Map<String, List<String>> list(String objectType, List<String> objectIds, OperationContext context) {
        return this.tagService.list(objectType, objectIds, context);
    }

    @Override
    @Fitable(id = "e92cf608a3724a4aa8eb918e5e6852c2")
    public List<String> list(String objectType, List<String> tags) {
        return this.tagService.list(objectType, tags);
    }
}
