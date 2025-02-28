/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.telemetry.aop.stub;

/**
 * 用于测试对象解析操作的测试类。
 *
 * @author 马朝阳
 * @since 2024-08-01
 */
public class CarverSpanObjectParse {
    /**
     * 内部类，用于测试解析操作。
     */
    public static class Outer {
        private final Inner k1;

        public Outer(Inner k1) {
            this.k1 = k1;
        }

        public Inner getK1() {
            return this.k1;
        }

        @Override
        public String toString() {
            return "{k1=" + k1.toString() + "}";
        }
    }

    /**
     * 内部类，用于测试解析操作。
     */
    public static class Inner {
        private final String k2;

        public Inner(String k2) {
            this.k2 = k2;
        }

        public String getK2() {
            return this.k2;
        }

        @Override
        public String toString() {
            return "{k2=" + k2 + "}";
        }
    }
}
