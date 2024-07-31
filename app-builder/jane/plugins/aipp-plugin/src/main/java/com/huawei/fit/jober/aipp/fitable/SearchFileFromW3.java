/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.fitable;

import com.huawei.fit.jober.FlowableService;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.dto.xiaohai.SearchW3Item;
import com.huawei.fit.jober.aipp.dto.xiaohai.SearchW3RespDto;
import com.huawei.fit.jober.aipp.service.AippLogService;
import com.huawei.fit.jober.aipp.util.DataUtils;
import com.huawei.fit.jober.aipp.util.HttpUtils;
import com.huawei.fit.jober.aipp.util.JsonUtils;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 从W3搜索文件
 *
 * @author s00664640
 * @since 2024/05/10
 */
@Component
@Slf4j
public class SearchFileFromW3 implements FlowableService {
    private final AippLogService aippLogService;
    private final String searchW3Url;

    public SearchFileFromW3(AippLogService aippLogService, @Value("${xiaohai.search_w3_url}") String searchW3Url) {
        this.aippLogService = aippLogService;
        this.searchW3Url = searchW3Url;
    }

    private void validationQueryContent(String queryContent, List<Map<String, Object>> flowData) {
        if (StringUtils.isNotBlank(queryContent)) {
            return;
        }
        String msg = "很抱歉！检索内容不能为空，请输入您关注的内容";
        this.aippLogService.insertErrorLog(msg, flowData);

        throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR, "queryContent is empty.");
    }

    /**
     * 调用小海2.0接口，到W3搜索数据
     *
     * @param flowData 流程执行上下文数据
     * @return flowData
     */
    @Fitable("com.huawei.fit.jober.aipp.fitable.SearchFileFromW3")
    @Override
    public List<Map<String, Object>> handleTask(List<Map<String, Object>> flowData) {
        Map<String, Object> businessData = DataUtils.getBusiness(flowData);
        log.debug("SearchFileFromW3 businessData {}", businessData);

        String queryContent = ObjectUtils.cast(businessData.get(AippConst.BS_SEARCH_W3_QUERY_CONTENT));
        validationQueryContent(queryContent, flowData);
        String queryTopKStr = ObjectUtils.cast(businessData.getOrDefault(AippConst.BS_SEARCH_W3_QUERY_TOP_K, "3"));

        String msg = "好的，根据您的需求，我决定先从网络上搜索相关信息";
        this.aippLogService.insertMsgLog(msg, flowData);
        HttpGet httpGet = null;
        try (CloseableHttpResponse response = HttpUtils.execute(httpGet = createHttpGet(queryContent, queryTopKStr))) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException(String.format(Locale.ROOT,
                        "send http fail. url=%s result=%d",
                        httpGet.getURI(),
                        response.getStatusLine().getStatusCode()));
            }
            String resp = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            log.info("search w3 resp:{}", resp);
            SearchW3RespDto respDto = JsonUtils.parseObject(resp, SearchW3RespDto.class);
            this.aippLogService.insertMsgLog(searchW3ItemListToLog(respDto.getData()), flowData);
            businessData.put(AippConst.BS_SEARCH_W3_QUERY_RESULT, w3QueryResultToString(respDto.getData()));
        } catch (IOException | ParseException e) {
            throw new JobberException(ErrorCodes.UN_EXCEPTED_ERROR,
                    String.format(Locale.ROOT,
                            "SearchFileFromW3 failed error=%s, stack: %s",
                            e.getMessage(),
                            Arrays.toString(e.getStackTrace())));
        }
        return flowData;
    }

    private HttpGet createHttpGet(String queryContent, String queryTopKStr) throws IOException {
        try {
            int queryTopK = Integer.parseInt(queryTopKStr);
            log.info("queryContent={} queryTopK={}", queryContent, queryTopK);
            return new HttpGet(String.format(Locale.ROOT,
                    searchW3Url,
                    URLEncoder.encode(queryContent, "utf-8"),
                    queryTopK));
        } catch (NumberFormatException | IllegalFormatException | UnsupportedEncodingException e) {
            throw new IOException(e);
        }
    }

    private String w3QueryResultToString(List<SearchW3Item> data) {
        StringBuilder sb = new StringBuilder();
        data.forEach(item -> sb.append(item.getDocText()).append("\n"));
        return data.isEmpty() ? "" : sb.substring(0, sb.length() - 1);
    }

    private String searchW3ItemListToLog(List<SearchW3Item> data) {
        StringBuilder sb = new StringBuilder("您好！以下是搜索到的相关信息:\n");
        for (int i = 0; i < data.size(); i++) {
            SearchW3Item item = data.get(i);
            sb.append(String.format(Locale.ROOT, "%d. %s [%s]", (i + 1), item.getDocTitle(), item.getDocUrl()))
                    .append("\n");
        }
        return data.isEmpty() ? "没有搜索到相关信息" : sb.substring(0, sb.length() - 1);
    }
}
