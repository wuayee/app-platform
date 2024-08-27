/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.errors;

/**
 * This class is used to throw an exception when the grammar syntax is incorrect.
 *
 * @since 1.0
 */
public class GrammarSyntaxException extends IllegalArgumentException {
    public GrammarSyntaxException(String info) {
        super(info);
    }
}
