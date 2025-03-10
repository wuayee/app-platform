/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.serialization.ObjectSerializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

class MapSerializerTest {
    private ObjectSerializer objectSerializer;

    private MapSerializer mapSerializer;

    @BeforeEach
    void setup() {
        this.objectSerializer = mock(ObjectSerializer.class);
        this.mapSerializer = new DefaultMapSerializer(this.objectSerializer);
    }

    @Test
    void should_serialize() {
        String expected = "{}";
        when(this.objectSerializer.serialize(Collections.emptyMap(), StandardCharsets.UTF_8))
                .thenReturn(expected.getBytes(StandardCharsets.UTF_8));
        String actual = this.mapSerializer.serialize(Collections.emptyMap());
        assertEquals(expected, actual);
    }
}
