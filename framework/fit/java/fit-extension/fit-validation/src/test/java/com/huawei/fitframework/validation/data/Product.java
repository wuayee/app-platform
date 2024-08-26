/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.validation.data;

import com.huawei.fitframework.validation.constraints.NotBlank;
import com.huawei.fitframework.validation.constraints.Positive;

/**
 * 表示产品的数据类。
 *
 * @author 吕博文
 * @since 2024-08-02
 */
public class Product {
    @NotBlank(message = "产品名不能为空")
    private String name;

    @Positive(message = "产品价格必须为正")
    private Double price;

    @Positive(message = "产品数量必须为正")
    private Integer quantity;

    @NotBlank(message = "产品类别不能为空")
    private String category;

    /**
     * Product 默认构造函数。
     *
     */
    public Product() {}

    public Product(String name, Double price, Integer quantity, String category) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }
}
