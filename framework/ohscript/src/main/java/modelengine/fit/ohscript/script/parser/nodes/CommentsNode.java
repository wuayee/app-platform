/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * comments in ohscript is significant for AI function
 * comments will give LLM information to determine what code to run next
 * huizi 2023
 *
 * @since 1.0
 */
public class CommentsNode extends NonTerminalNode {
    public CommentsNode() {
        super(NonTerminal.COMMENT_STATEMENT);
    }

    @Override
    public void optimizeBeta() {
    }
}
