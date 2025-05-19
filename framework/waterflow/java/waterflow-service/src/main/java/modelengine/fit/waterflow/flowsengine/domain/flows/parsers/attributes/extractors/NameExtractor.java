/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.extractors;

import modelengine.fit.waterflow.flowsengine.domain.flows.parsers.attributes.objects.AttributesData;
import modelengine.fitframework.util.ObjectUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * name 提取器.
 *
 * @author 张越
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
