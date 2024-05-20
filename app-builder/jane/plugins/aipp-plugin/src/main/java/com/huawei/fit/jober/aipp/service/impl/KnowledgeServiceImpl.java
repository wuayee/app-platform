/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jober.aipp.common.HttpUtils;
import com.huawei.fit.jober.aipp.common.JsonUtils;
import com.huawei.fit.jober.aipp.common.PageResponse;
import com.huawei.fit.jober.aipp.condition.KnowledgeQueryCondition;
import com.huawei.fit.jober.aipp.condition.PaginationCondition;
import com.huawei.fit.jober.aipp.dto.KnowledgeDetailDto;
import com.huawei.fit.jober.aipp.service.KnowledgeService;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 知识库服务接口实现
 *
 * @author h00804153
 * @since 2024-04-23
 */
@Component
public class KnowledgeServiceImpl implements KnowledgeService {
    private static final Logger log = Logger.get(KnowledgeServiceImpl.class);
    private final String queryListUrl;

    public KnowledgeServiceImpl(@Value("${eDataMate.query_list_url}") String queryListUrl) {
        this.queryListUrl = queryListUrl;
    }

    /**
     * 查询知识库列表
     *
     * @param cond 过滤条件
     * @param page 分页
     * @return 知识库详细信息
     */
    @Override
    public PageResponse<KnowledgeDetailDto> listKnowledge(KnowledgeQueryCondition cond, PaginationCondition page)
            throws IOException {
        log.info("listKnowledge cond{} page{}", cond, page);
        Map<String, Object> data = queryListEDataMate(cond, page);
        List<Map<String, Object>> records = ObjectUtils.cast(data.get("records"));
        List<KnowledgeDetailDto> result = records.stream()
                .map(map -> new KnowledgeDetailDto(this.safelyConvertToLongType(ObjectUtils.cast(map.get("id"))),
                        ObjectUtils.cast(map.get("name")),
                        ObjectUtils.cast(map.get("description"))))
                .collect(Collectors.toList());
        return new PageResponse<>(this.safelyConvertToLongType(ObjectUtils.cast(data.get("total"))), null, result);
    }

    private Map<String, Object> queryListEDataMate(KnowledgeQueryCondition cond, PaginationCondition page)
            throws IOException {
        HttpPost httpPost = new HttpPost(this.queryListUrl);
        httpPost.setEntity(new StringEntity(this.buildQueryListEBody(cond, page), ContentType.APPLICATION_JSON));
        try (CloseableHttpResponse response = HttpUtils.execute(httpPost)) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IOException(String.format(Locale.ROOT,
                        "send http fail. url=%s result=%d",
                        httpPost.getURI(),
                        response.getStatusLine().getStatusCode()));
            }
            String respContent = EntityUtils.toString(response.getEntity());
            Map<String, Object> respObj = JsonUtils.parseObject(respContent);
            int code = (int) respObj.get("code");
            if (code != 0) {
                throw new IOException(String.format(Locale.ROOT,
                        "knowledgeService queryListEDataMate fail. code=%d msg=%s",
                        code,
                        respObj.get("msg")));
            }
            return ObjectUtils.cast(respObj.get("data"));
        } catch (IOException e) {
            throw new IOException(String.format(Locale.ROOT,
                    "knowledgeService queryListEDataMate fail: %s",
                    e.getMessage()));
        }
    }

    private String buildQueryListEBody(KnowledgeQueryCondition cond, PaginationCondition page) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(cond.getName())) {
            map.put("name", cond.getName());
        }
        map.put("pageNo", page.getPageNum());
        map.put("pageSize", page.getPageSize());
        return JsonUtils.toJsonString(map);
    }

    private Long safelyConvertToLongType(Number value) {
        return value.longValue();
    }
}
