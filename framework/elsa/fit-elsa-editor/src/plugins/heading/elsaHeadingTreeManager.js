import {Plugin} from "@ckeditor/ckeditor5-core";
import ElsaHeadingChangeEmitter from "./elsaHeadingChangeEmitter";
import Heading from "./domain/heading";
import TreeNode from "./domain/TreeNode";
import {ChangeUtils} from "../../utils/ChangeUtils";

/**
 * heading管理器，监听heading的增删事件，调整heading树.
 *
 * @author z00559346 张越.
 */
export default class ElsaHeadingTreeManager extends Plugin {
    /**
     * 记录root和标题树之间的映射关系.
     *
     * @member {Map<string, HeadingTree>}
     * @private
     */
    _headingTreeMap = new Map();

    static get requires() {
        return [ElsaHeadingChangeEmitter];
    }

    static get pluginName() {
        return 'ElsaHeadingTreeManager';
    }

    init() {
        const elsaHeadingChangeEmitter = this.editor.plugins.get("ElsaHeadingChangeEmitter");
        elsaHeadingChangeEmitter.on("heading:change", (evtInfo, changes) => {
            // 记录发生过修改的headingTree.用于后续的统一遍历.
            const changeHeadingTrees = new Set();
            changes.forEach(change => {
                const headingTree = this._getHeadingTree(ChangeUtils.getRootName(change));
                switch (change.type) {
                    case "insert":
                        this._insert(change, headingTree);
                        break;
                    case "remove":
                        this._remove(change, headingTree);
                        break;
                    default:
                        throw new Error("ElsaHeadingTreeManager#init: invalidate change type[" + change.type + "]");
                }
                changeHeadingTrees.add(headingTree);
            });

            this.editor.model.change(writer => {
                changeHeadingTrees.forEach(ht => {
                    ht.traverse(heading => {
                        // 1、重新设置heading的model的serialNo属性.
                        writer.setAttribute("serialNo", heading.getSerialNo(), heading.getElement());

                        // 2、若heading的层级和当前在headingTree中的层级不匹配，需要进行rename.
                        if (heading.getPath().length !== heading.getLevel()) {
                            writer.rename(heading.getElement(), "heading" + heading.getPath().length);
                        }
                    });
                });
            });
        }, {priority: "highest"});
    }

    /**
     * 根据rootName获取对应的标题树.
     *
     * @param rootName 根接待名称.
     * @return {HeadingTree} 标题树对象.
     * @private
     */
    _getHeadingTree(rootName) {
        let headingTree = this._headingTreeMap.get(rootName);
        if (!headingTree) {
            headingTree = new HeadingTree();
            this._headingTreeMap.set(rootName, headingTree);
        }
        return headingTree;
    }

    _insert(change, headingTree) {
        const heading = new Heading(change.attributes.get("id"), change.position.nodeAfter);
        headingTree.insertHeading(heading);
    }

    _remove(change, headingTree) {
        const id = change.attributes.get("id");
        headingTree.removeHeading(id);
    }

    /**
     * 根据rootName和headingId获取标题.
     *
     * @param rootName 根节点名称.
     * @param headingId 标题id.
     * @return {Heading} 标题对象.
     */
    getHeadingById(rootName, headingId) {
        const headingTree = this._headingTreeMap.get(rootName);
        if (!headingTree) {
            throw new Error("ElsaHeadingTreeManager#getHeadingById: heading tree of [" + rootName + "] is not exists");
        }
        return headingTree._idHeadingMapping.get(headingId);
    }

    /**
     * @inheritDoc
     * @override
     */
    destroy() {
        super.destroy();
        this._headingTreeMap.clear();
        this._headingTreeMap = null;
    }
}

/**
 * 标题树，用于管理标题.
 *
 * @author z00559346 张越.
 */
class HeadingTree extends TreeNode {
    /**
     * id和标题的映射关系.主要用于删除操作.
     *
     * @member {Map<string, Heading>}
     * @private
     */
    _idHeadingMapping = new Map();

    /**
     * 插入heading.
     *
     * @param heading 标题元素.
     */
    insertHeading(heading) {
        this._insertHeading(heading, this, 1);
        this._idHeadingMapping.set(heading.getId(), heading);
    }

    _insertHeading(heading, parent, level) {
        const headings = parent.getChildren();
        if (headings.length === 0) {
            parent.appendChild(heading);
            return;
        }

        let index = headings.length - 1;
        for (let i = 0; i < headings.length; i++) {
            if (headings[i].getIndex() > heading.getIndex()) {
                index = i - 1;
                break;
            }
        }

        if (level < heading.getLevel()) {
            if (index < 0) {
                // 如果index小于0(拷贝时有可能直接将一个4级标题，拷贝到1级标题下).
                // 此时，直接将该标题插入到开始位置.
                parent.insertChild(heading, 0);
            } else {
                // 1、如果当前的level层级比待插入heading的level层级小，则进入下一层判断是否可以插入.
                // 1.1、这里需要将heading插入到前一个节点中.
                this._insertHeading(heading, headings[index], level + 1);
            }
        } else if (level === heading.getLevel()) {
            // 2、如果当前的level层级和待插入heading的level层级相等，则进行插入处理
            // 2.1、插入节点.
            parent.insertChild(heading, index + 1);

            // 2.2、将前一个节点下面index比待插入heading的index大的子孙(不仅包括孩子，还包括孙子等)移动到新插入节点下
            if (index >= 0) {
                this._moveChildren(headings[index], heading);
            }
        }
    }

    _moveChildren(prevHeading, heading) {
        const stack = [...prevHeading.getChildren()];
        while (stack.length !== 0) {
            const current = stack.shift();
            if (current.getIndex() > heading.getIndex()) {
                current.getParent().removeChildByIndex(current.getTreeIndex());
                heading.appendChild(current);
            } else if (current.getMaxIndex() > heading.getIndex()) {
                current.getChildren().reverse().forEach(c => stack.unshift(c));
            } else {
                // 直接处理下一个元素.
            }
        }
    }

    /**
     * 通过id删除heading.
     *
     * @param id 唯一标识.
     */
    removeHeading(id) {
        const heading = this._idHeadingMapping.get(id);
        if (heading === null || heading === undefined) {
            return;
        }

        const previous = heading.getPrevious();
        if (previous) {
            // 1、如果存在前继节点，删除该节点后，将其子节点修改为前面兄弟节点的儿子.
            heading.getChildren().forEach(c => previous.appendChild(c));
        } else {
            // 2、如果为0，说明是第一个节点，删除该节点时，需要将其子节点设置为其父节点的子节点.
            heading.getParent().insertChildren(heading.getChildren(), 0);
        }

        // 删除该节点.
        heading.getParent().removeChildByIndex(heading.getTreeIndex());
        this._idHeadingMapping.delete(id);
    }

    /**
     * 遍历整棵树.
     *
     * @param action 遍历时的操作.
     */
    traverse(action) {
        this._traverse(this, action);
    }

    _traverse(node, action) {
        node.getChildren().forEach(c => {
            action(c);
            this._traverse(c, action);
        });
    }
}