import TreeNode from "./TreeNode";

/**
 * 定义heading相关行为及操作，简化代码.
 *
 * @author z00559346 张越.
 */
export default class Heading extends TreeNode {
    constructor(id, block) {
        super();
        this._id = id;
        this._block = block;
    }

    /**
     * 获取level.
     *
     * @return {number} 级别.
     */
    getLevel() {
        return parseInt(this._block.name.replace("heading", ""));
    }

    /**
     * 标题在编辑器中的位置.
     *
     * @return {*} 下标位置.
     */
    getIndex() {
        return this._block.getPath()[0];
    }

    /**
     * 判断当前对象是否还是一个heading.
     *
     * @return {boolean} true/false.
     */
    isValid() {
        return this._block.name.startsWith("heading") && this._id === this._block._attrs.get("id");
    }

    /**
     * 获取元素id.
     *
     * @return {V} id.
     */
    getId() {
        return this._id;
    }

    /**
     * 返回CK原生的元素对象.
     *
     * @return {*} ck原生element对象.
     */
    getElement() {
        return this._block;
    }

    /**
     * 判断两个 {@link #Heading} 是否相等，就是判断他们持有的block是否是同一个.
     *
     * @param heading 其他heading对象.
     * @return {boolean} true/false
     */
    equals(heading) {
        return this._block === heading.getElement();
    }

    /**
     * 判断heading是否是某个类型.
     *
     * @param name 类型名称.
     * @return {boolean} true/false.
     */
    is(name) {
        return this._block.name === name;
    }

    /**
     * 获取标题序号.
     *
     * @return {string} 标题的序号.
     */
    getSerialNo() {
        return this.getPath().map(index => index + 1).join(".");
    }

    /**
     * 获取当前标题树中的最大index.
     *
     * @return {*} 最大的index.
     */
    getMaxIndex() {
        let maxIndex = this.getIndex();
        let children = this.getChildren();
        while (children.length > 0) {
            const maxChild = children[children.length - 1];
            maxIndex = maxChild.getIndex();
            children = maxChild.getChildren();
        }
        return maxIndex;
    }
}