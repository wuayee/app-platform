/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.http.server.handler;

/**
 * 表示 Http 报文中的来源位置。
 *
 * @author 季聿阶
 * @since 2023-08-28
 */
public enum Source {
    QUERY {
        @Override
        public boolean isInBody() {
            return false;
        }
    },

    HEADER {
        @Override
        public boolean isInBody() {
            return false;
        }
    },

    COOKIE {
        @Override
        public boolean isInBody() {
            return false;
        }
    },

    PATH {
        @Override
        public boolean isInBody() {
            return false;
        }
    },

    BODY {
        @Override
        public boolean isInBody() {
            return true;
        }
    },

    FORM {
        @Override
        public boolean isInBody() {
            return true;
        }
    };

    /**
     * 判断当前位置是否属于消息体。
     *
     * @return 如果属于，返回 {@code true}，否则，返回 {@code false}。
     */
    public abstract boolean isInBody();
}
