/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.fixture;

import java.util.List;

/**
 * human
 * 测试类
 *
 * @since 1.0
 */
public interface Human {
    /**
     * 获取人的年龄
     *
     * @return 返回人的年龄
     */
    Integer getAge();

    /**
     * 获取人的名字
     *
     * @param firstName 人的名字
     * @return 返回人的名字
     */
    String getName(String firstName);

    /**
     * 添加朋友
     *
     * @param friends 朋友列表
     * @return 返回添加后的朋友列表
     */
    List<Human> makeFriends(List<Human> friends);

    /**
     * 获取朋友
     *
     * @return 返回朋友
     */
    Human getFriend();
}