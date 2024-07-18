/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser;

import com.huawei.fit.ohscript.script.lexer.Terminal;
import com.huawei.fit.ohscript.script.lexer.Token;
import com.huawei.fit.ohscript.util.Tool;

import java.util.HashMap;
import java.util.Map;

/**
 * 预测表
 *
 * @since 1.0
 */
public class PredictTable {
    private final Map<Terminal, Map<Grammar, Production>> table = new HashMap<>();

    /**
     * 构建预测表
     *
     * @param grammars 语法表
     */
    public PredictTable(Grammars grammars) {
        grammars.grammars().forEach(grammar -> grammar.productions().forEach(production -> {
            if (production.isEmpty()) {
                grammar.follow().forEach(symbol -> addMapping(grammar, production, symbol));
            }
            production.first().forEach(symbol -> addMapping(grammar, production, symbol));
        }));
    }

    private void addMapping(Grammar grammar, Production production, Terminal symbol) {
        Map<Grammar, Production> firsts = table.computeIfAbsent(symbol, k -> new HashMap<>());
        firsts.put(grammar, production);
    }

    /**
     * 匹配产生式
     *
     * @param token 符号
     * @param grammar 语法
     * @param line 行数，用于指导报错
     * @return 匹配到的产生式
     */
    public Production match(Token token, Grammar grammar, String line) {
        Terminal terminal = token.tokenType();
        Map<Grammar, Production> productions = table.get(terminal);
        String lineSeparator = System.lineSeparator();
        if (productions == null) {
            Tool.grammarError(
                    "`" + terminal.text() + "` is not found in predict table" + lineSeparator + line);
        }
        Production production = productions.get(grammar);
        if (production == null) {
            StringBuilder sb = new StringBuilder();
            for (Production p : productions.values()) {
                sb.append(p.display() + lineSeparator);
            }
            Tool.grammarError("token: " + terminal.name() + ":" + token.lexeme()
                    + " doesn't match any ohScript grammar definition at line: " + token.line() + ", position from "
                    + token.start() + " to " + token.end() + lineSeparator + line
                    + lineSeparator + " the possible productions should be matched would be:" + sb);
        }
        return production;
    }
}
