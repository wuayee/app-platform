/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.ohscript.script.parser.nodes.async;

import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fit.ohscript.script.parser.nodes.NonTerminalNode;

/**
 * lock代码块节点
 *
 * @since 1.0
 */
public class LockBlockNode extends NonTerminalNode {
    public LockBlockNode() {
        super(NonTerminal.LOCK_BLOCK);
    }
}
