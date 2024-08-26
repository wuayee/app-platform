/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import modelengine.fit.ohscript.script.parser.nodes.EntityExtensionNode;
import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

import java.util.HashMap;
import java.util.Map;

/**
 * 扩展类型表达式
 *
 * @since 1.0
 */
public class ExtensionTypeExpr extends EntityTypeExpr {
    /**
     * 构造函数
     *
     * @param entity 实体语法节点
     * @param members 成员
     */
    public ExtensionTypeExpr(SyntaxNode entity, Map<String, TypeExpr> members) {
        super(entity, members);
    }

    /**
     * 获取宿主类型表达式
     *
     * @return 宿主类型表达式
     */
    public TypeExpr hostTypeExpr() {
        SyntaxNode host = this.node();
        while (host instanceof EntityExtensionNode) {
            host = ((EntityExtensionNode) host).host();
        }
        return host.typeExpr();
    }

    @Override
    public TypeExpr polish() {
        EntityTypeExpr expr = new ExtensionTypeExpr(this.node(), new HashMap<>());
        return polish(expr);
    }

    @Override
    public Type type() {
        return Type.EXTENSION;
    }
}
