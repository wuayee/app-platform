/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.model.tree.support;

import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fitframework.model.tree.Tree;
import com.huawei.fitframework.model.tree.TreeNode;
import com.huawei.fitframework.model.tree.TreeNodeCollection;
import com.huawei.fitframework.util.StringUtils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * 为 {@link Tree} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-08-10
 */
final class DefaultTree implements Tree {
    private final char pathSeparator;
    private final DefaultTreeNodeCollection roots;

    /**
     * 使用路径分隔符创建 {@link DefaultTree} 类的新实例。
     *
     * @param pathSeparator 表示路径分隔符的字符。
     */
    DefaultTree(char pathSeparator) {
        this.pathSeparator = pathSeparator;
        this.roots = new DefaultTreeNodeCollection(this, null);
    }

    @Override
    public char pathSeparator() {
        return this.pathSeparator;
    }

    @Override
    public DefaultTreeNodeCollection roots() {
        return this.roots;
    }

    @Override
    public TreeNode get(String path) {
        if (path == null) {
            return null;
        }
        String[] parts = StringUtils.split(path, this.pathSeparator());
        TreeNode node = this.roots().get(parts[0]);
        for (int i = 1; i < parts.length && node != null; i++) {
            node = node.children().get(parts[i]);
        }
        return node;
    }

    @Override
    public TreeNode getOrCreate(String path) {
        notNull(path, "The path of node cannot be null.");
        String[] parts = StringUtils.split(path, this.pathSeparator());
        TreeNode node = this.roots().getOrCreate(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            node = node.children().getOrCreate(parts[i]);
        }
        return node;
    }

    @Override
    public TreeNode remove(String path) {
        if (path == null) {
            return null;
        }
        String[] parts = StringUtils.split(path, this.pathSeparator());
        TreeNodeCollection nodes = this.roots();
        for (int i = 0; i < parts.length - 1; i++) {
            TreeNode node = nodes.get(parts[i]);
            if (node == null) {
                return null;
            } else {
                nodes = node.children();
            }
        }
        return nodes.remove(parts[parts.length - 1]);
    }

    @Override
    public void bfs(Consumer<TreeNode> consumer) {
        if (consumer == null) {
            return;
        }
        Queue<TreeNode> queue = new LinkedList<>();
        this.roots().forEach(queue::add);
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            consumer.accept(node);
            node.children().forEach(queue::add);
        }
    }

    @Override
    public void dfs(Consumer<TreeNode> consumer) {
        if (consumer == null) {
            return;
        }
        Stack<TreeNode> stack = new Stack<>();
        for (int i = this.roots().size() - 1; i >= 0; i--) {
            stack.push(this.roots().get(i));
        }
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            consumer.accept(node);
            for (int i = node.children().size() - 1; i >= 0; i--) {
                stack.push(node.children().get(i));
            }
        }
    }
}
