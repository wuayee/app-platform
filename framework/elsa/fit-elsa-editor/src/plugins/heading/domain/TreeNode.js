/**
 * 定义树中节点的行为.
 *
 * @author z00559346 张越.
 */
export default class TreeNode {
    _children = [];

    /**
     * 获取孩子节点.
     *
     * @return {[]} 孩子节点.
     */
    getChildren() {
        return this._children;
    }

    /**
     * 获取前一个节点.
     *
     * @return {null|*} 前一个节点.
     */
    getPrevious() {
        if (this._treeIndex === 0) {
            return null;
        }
        return this._parent.getChildren()[this._treeIndex - 1];
    }

    /**
     * 获取父节点.
     *
     * @return {*}
     */
    getParent() {
        return this._parent;
    }

    /**
     * 添加子节点.
     *
     * @param child 子节点.
     */
    appendChild(child) {
        this._children.push(child);
        child.setParent(this);
        child.setTreeIndex(this._children.length - 1);
    }

    /**
     * 在指定位置插入子节点.
     *
     * @param child 子节点.
     * @param index 指定的下标.
     */
    insertChild(child, index) {
        if (index >= this._children.length) {
            this.appendChild(child);
            return;
        }

        this._children.splice(index, 0, child);
        child.setTreeIndex(index);
        child.setParent(this);

        // 调整插入位置之后的兄弟节点.
        for (let i = index + 1; i < this._children.length; i++) {
            this._children[i].setTreeIndex(i);
        }
    }

    /**
     * 批量插入子节点.
     *
     * @param children 子节点数组.
     * @param index 插入的下标位置.
     */
    insertChildren(children, index) {
        if (!Array.isArray(children)) {
            throw new Error("TreeNode#insertChildren: children must be an array.");
        }

        children.reverse().forEach(c => {
            this._children.unshift(c);
            c.setParent(this);
        });
        for (let i = index; i < this._children.length; i++) {
            this._children[i].setTreeIndex(i);
        }
    }

    /**
     * 按下表删除孩子节点.
     *
     * @param index 下标.
     */
    removeChildByIndex(index) {
        this._children.splice(index, 1);
        for (let i = index; i < this._children.length; i++) {
            this._children[i].setTreeIndex(i);
        }
    }

    /**
     * 设置节点在树中的index.
     *
     * @param treeIndex 树中的index.
     */
    setTreeIndex(treeIndex) {
        this._treeIndex = treeIndex;
    }

    /**
     * 获取在树中的index.
     *
     * @return {*} 下标.
     */
    getTreeIndex() {
        return this._treeIndex;
    }

    /**
     * 设置parent.
     *
     * @param parent 父节点.
     */
    setParent(parent) {
        this._parent = parent;
    }

    /**
     * 获取节点在树中的路径.
     *
     * @return {*[]} 路径数组.
     */
    getPath() {
        const path = [];
        let node = this;
        while (node._parent) {
            path.unshift(node._treeIndex);
            node = node._parent;
        }
        return path;
    }
}