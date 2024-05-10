/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import java.util.List;

/**
 * 调用IData接口代理类。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-03
 */
public interface PersonService {
    /**
     * 通过关键字查询用户信息。
     *
     * @param keyword 关键字。
     * @param uid uid。
     * @return {@link EmployeeVO} 的列表。
     */
    List<EmployeeVO> searchEmployeeInfo(String keyword, String uid);

    /**
     * 通过指定信息查询对应用户详情信息。
     *
     * @param employeeNumber 表示用户纯工号的 {@link String}。 e.g. 00123456
     * @param uid 表示用户短工号的 {@link String}。 e.g. z00123456
     * @param uuid 表示用户uuid的 {@link String}。 e.g. uuid~YzAwNTcyODA4
     * @param globalUserId 表示用户全局id的 {@link String}。 e.g. 167783891071488
     * @return 表示用户详情信息的 {@link EmployeeDetailVO}。
     */
    EmployeeDetailVO getPersonInfo(String employeeNumber, String uid, String uuid, String globalUserId);
}
