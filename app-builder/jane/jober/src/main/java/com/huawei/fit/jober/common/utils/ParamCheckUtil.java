/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.utils;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_INVALID;

import com.huawei.fit.http.QueryCollection;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fitframework.log.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ParamCheckUtil
 *
 * @author 孙怡菲 s00664640
 * @since 2023-07-27
 */
public class ParamCheckUtil {
    private static final Logger log = Logger.get(ParamCheckUtil.class);

    /**
     * checkTags
     *
     * @param tags tags
     */
    public static void checkTags(List<String> tags) {
        if (tags.isEmpty()) {
            log.error("Query param has no tag.");
            throw new JobberParamException(INPUT_PARAM_IS_EMPTY, "tag");
        }
    }

    /**
     * checkUsers
     *
     * @param owners owners
     * @param appliers appliers
     */
    public static void checkUsers(List<String> owners, List<String> appliers) {
        if (owners.isEmpty() && appliers.isEmpty() || !owners.isEmpty() && !appliers.isEmpty()) {
            log.error("Cannot query both or neither user role.");
            throw new JobberParamException(INPUT_PARAM_IS_INVALID, "created_by or owner");
        }
    }

    /**
     * decodeListParam
     *
     * @param queryCollection queryCollection
     * @param key key
     * @return List<String>
     */
    public static List<String> decodeListParam(QueryCollection queryCollection, String key) {
        List<String> paramList = queryCollection.all(key).stream().map(a -> {
            try {
                return URLDecoder.decode(a, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.warn("unsupported encoding for string {}", a);
                return null;
            }
        }).collect(Collectors.toList());
        return paramList.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * decodeParam
     *
     * @param queryCollection queryCollection
     * @param key key
     * @return String
     */
    public static String decodeParam(QueryCollection queryCollection, String key) {
        return queryCollection.first(key).map(a -> {
            try {
                return URLDecoder.decode(a, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.warn("unsupported encoding for string {}", a);
                return null;
            }
        }).orElseThrow(() -> new JobberParamException(INPUT_PARAM_IS_EMPTY, key));
    }

    /**
     * 校验分页参数
     *
     * @param offset 分页偏移值
     * @param limit 分页大小
     */
    public static void checkPagination(long offset, int limit) {
        if (offset < 0L) {
            log.error("The offset of pagination out of range. Input offset is {}", offset);
            throw new BadRequestException(ErrorCodes.PAGINATION_OFFSET_INVALID);
        }
        if (limit < 0 || limit > 1000) {
            log.error("The limit of pagination out of range. Input limit is {}", limit);
            throw new BadRequestException(ErrorCodes.PAGINATION_LIMIT_INVALID);
        }
    }
}
