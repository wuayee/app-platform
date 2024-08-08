/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors;

import com.huawei.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import com.huawei.fitframework.util.ObjectUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * name 提取器.
 *
 * @author 张越 z00559346
 * @since 2024-08-05
 */
public class NameExtractor implements ValueExtractor {
    @Override
    public Object extract(AttributesData attributesData) {
        Object text = attributesData.getData().get("text");
        if (text instanceof String) {
            return text;
        }
        String textHtml = ObjectUtils.cast(attributesData.getData().get("textInnerHtml"));
        String regex = "<p[^>]*>(.*?)</p>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(textHtml);
        StringBuilder concatenatedText = new StringBuilder();
        while (matcher.find()) {
            String tagContent = matcher.group(1);
            concatenatedText.append(tagContent);
        }
        return concatenatedText.toString();
    }
}
