/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.support;

import static com.huawei.fit.http.server.handler.MockHttpClassicServerRequest.HEADER_KEY;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.server.handler.MockHttpClassicServerRequest;
import com.huawei.fit.http.server.handler.PropertyValueMapper;
import com.huawei.fit.http.server.support.DefaultHttpClassicServerRequest;
import com.huawei.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 表示 {@link MultiSourcePropertyValueMapper } 的单元测试。
 *
 * @author 邬涨财 w00575064
 * @since 2024-02-17
 */
public class MultiSourcePropertyValueMapperTest {
    @Test
    @DisplayName("将 Http 请求和响应通过规则映射成为 Map")
    void givenMapWhenDestinationNameNotEmptyThenReturnMap() {
        final MockHttpClassicServerRequest serverRequest = new MockHttpClassicServerRequest();
        final DefaultHttpClassicServerRequest request = serverRequest.getRequest();
        List<SourceFetcherInfo> sourceFetcherInfos =
                Collections.singletonList(new SourceFetcherInfo(new HeaderFetcher(HEADER_KEY), "a.b", false));
        PropertyValueMapper mapper = new MultiSourcePropertyValueMapper(sourceFetcherInfos);
        final Object value = mapper.map(request, null);
        Map<String, Map> map = ObjectUtils.cast(value);
        assertThat(map).containsKey("a");
    }
}
