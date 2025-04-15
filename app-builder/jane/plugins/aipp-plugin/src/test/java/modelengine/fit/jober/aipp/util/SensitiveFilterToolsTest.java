/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.jober.aipp.util;

import modelengine.fit.jober.aipp.util.SensitiveFilterTools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link SensitiveFilterTools}的单元测试类
 *
 * @author 姚江
 * @since 2024-09-10
 */
@ExtendWith(MockitoExtension.class)
public class SensitiveFilterToolsTest {
    private SensitiveFilterTools tool;
    private List<SensitiveFilterTools.SensitiveReplaceEntity> entities = new ArrayList<>();

    @BeforeEach
    void setup() {
        this.entities.add(new SensitiveFilterTools.SensitiveReplaceEntity("\"timeStamp\":\"([^\"]*)\"",
                "\"timeStamp\":\"***\"", null));
        this.tool = new SensitiveFilterTools(this.entities);
    }

    @Test
    @DisplayName("测试正常场景，\"timeStamp\":\"2024-09-10T08:32:52.919+00:00\"")
    void testSuccessTimeStamp() {
        String test = "\"timeStamp\":\"2024-09-10T08:32:52.919+00:00\"";
        String except = "\"timeStamp\":\"***\"";
        String actual = tool.filterString(test);
        Assertions.assertEquals(except, actual);
    }

    @Test
    @DisplayName("测试正常场景，\"timeStamp\":\"2024-09-10 08:32:52\"")
    void testSuccessTimeStampWithBlack() {
        String test = "\"timeStamp\":\"2024-09-10 08:32:52\"";
        String except = "\"timeStamp\":\"***\"";
        String actual = tool.filterString(test);
        Assertions.assertEquals(except, actual);
    }
}
