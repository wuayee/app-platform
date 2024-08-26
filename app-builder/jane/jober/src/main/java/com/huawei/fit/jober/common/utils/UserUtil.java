/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.utils;

import com.huawei.fit.jane.task.gateway.EmployeeDetailVO;
import com.huawei.fit.jane.task.gateway.PersonService;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.StringUtils;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 为人员提供相关工具方法。
 *
 * @author 陈镕希
 * @since 2023-10-07
 */
@Component
@RequiredArgsConstructor
public class UserUtil {
    private static final Logger log = Logger.get(UserUtil.class);

    private final PersonService personService;

    private final LoadingCache<String, EmployeeDetailVO> employeeNumberUserCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(12))
            .refreshAfterWrite(Duration.ofHours(6))
            .build(this::getEmployeeDetailByEmployeeNumber);

    private final LoadingCache<String, EmployeeDetailVO> uidUserCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(12))
            .refreshAfterWrite(Duration.ofHours(6))
            .build(this::getEmployeeDetailByUid);

    private final LoadingCache<String, EmployeeDetailVO> uuidUserCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(12))
            .refreshAfterWrite(Duration.ofHours(6))
            .build(this::getEmployeeDetailByUuid);

    private final LoadingCache<String, EmployeeDetailVO> globalUserIdUserCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofHours(12))
            .refreshAfterWrite(Duration.ofHours(6))
            .build(this::getEmployeeDetailByGlobalUserId);

    private static boolean isAppId(EmployeeDetailVO employeeDetail) {
        return StringUtils.equals(employeeDetail.getEmployeeNumber(), employeeDetail.getGlobalUserId());
    }

    /**
     * getEmployeeDetail
     *
     * @param employeeNumber employeeNumber
     * @param uid uid
     * @param uuid uuid
     * @param globalUserId globalUserId
     * @return EmployeeDetailVO
     */
    public EmployeeDetailVO getEmployeeDetail(String employeeNumber, String uid, String uuid, String globalUserId) {
        Optional<String> optionalAppid = this.findAppid(Arrays.asList(employeeNumber, uid, uuid, globalUserId));
        if (optionalAppid.isPresent()) {
            String appid = optionalAppid.get();
            EmployeeDetailVO employee = new EmployeeDetailVO();
            employee.setEmployeeNumber(appid);
            employee.setUid(appid);
            employee.setUuid(appid);
            employee.setGlobalUserId(appid);
            return employee;
        }
        if (StringUtils.isNotBlank(employeeNumber)) {
            return employeeNumberUserCache.get(employeeNumber);
        }
        if (StringUtils.isNotBlank(uid)) {
            return uidUserCache.get(uid);
        }
        if (StringUtils.isNotBlank(uuid)) {
            return uuidUserCache.get(uuid);
        }
        if (StringUtils.isNotBlank(globalUserId)) {
            return globalUserIdUserCache.get(globalUserId);
        }
        log.error("All input param of getEmployeeDetail is blank.");
        throw new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "All input param of getEmployeeDetail");
    }

    /**
     * getUserName
     *
     * @param employeeNumber employeeNumber
     * @param uid uid
     * @param uuid uuid
     * @param globalUserId globalUserId
     * @return user name
     */
    public String getUserName(String employeeNumber, String uid, String uuid, String globalUserId) {
        EmployeeDetailVO employeeDetail = this.getEmployeeDetail(employeeNumber, uid, uuid, globalUserId);
        if (isAppId(employeeDetail)) {
            return employeeDetail.getEmployeeNumber();
        }
        return employeeDetail.getLastName() + " " + employeeDetail.getEmployeeNumber();
    }

    private Optional<String> findAppid(List<String> judgeStringList) {
        // 当前appid需要在应用中心创建，都为com.huawei开头，
        // 但存量appid并没有一个统一格式，这块若后续有特殊的appid调用，需要进行支持。建议后续移至配置文件中，进行appid列表的配置
        return judgeStringList.stream()
                .filter(StringUtils::isNotBlank)
                .filter(string -> string.contains("com.huawei"))
                .findFirst();
    }

    private EmployeeDetailVO getEmployeeDetailByEmployeeNumber(String employeeNumber) {
        return personService.getPersonInfo(employeeNumber, null, null, null);
    }

    private EmployeeDetailVO getEmployeeDetailByUid(String uid) {
        return personService.getPersonInfo(null, uid, null, null);
    }

    private EmployeeDetailVO getEmployeeDetailByUuid(String uuid) {
        return personService.getPersonInfo(null, null, uuid, null);
    }

    private EmployeeDetailVO getEmployeeDetailByGlobalUserId(String globalUserId) {
        return personService.getPersonInfo(null, null, null, globalUserId);
    }
}
