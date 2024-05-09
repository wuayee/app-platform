import {Plugin} from "@ckeditor/ckeditor5-core";
import {ChangeUtils} from "../../utils/ChangeUtils";

/**
 * 用于给元素添加id属性.
 *
 * @author z00559346 张越.
 */
export default class IDPlugin extends Plugin {
    /**
     * rootName和id集合的映射关系.
     *
     * @member {Map<string, Set>}
     * @private
     */
    _rootIdSetMap = new Map();

    static get pluginName() {
        return 'IDPlugin';
    }

    init() {
        const editor = this.editor;

        /**
         * 添假model中id到view中id的映射.
         */
        editor.conversion.attributeToAttribute({model: "id", view: "id"});

        /**
         * 重写checkAttribute方法.转换时，会通过allowAttributes校验是否允许该属性.
         * 为了不修改每个元素的schema，这里统一进行处理.
         *
         * @param context 上下文.
         * @param attributeName 属性名称.
         * @return {boolean|*} true/false.
         */
        const checkAttribute = editor.model.schema.checkAttribute;
        editor.model.schema.checkAttribute = (context, attributeName) => {
            if (attributeName === "id") {
                return true;
            }
            return checkAttribute.apply(editor.model.schema, [context, attributeName]);
        }

        /**
         * 对新增的元素分配id.
         */
        editor.model.document.on("change:data", (eventInfo, batch) => {
            if (batch.isUndo) {
                return;
            }
            const changes = editor.model.document.differ.getChanges();
            changes.filter(change => this._isElementInsertChange(change)).forEach(change => {
                const item = change.position.nodeAfter;
                const rootName = ChangeUtils.getRootName(change);
                const idSet = this._getIdSet(rootName);
                this._handleId(item, idSet);
            });
        }, {priority: "highest"}); // highest代表最先执行.
    }

    // 忽略id的元素.
    IGNORE_ID_ELEMENTS = new Set().add("softBreak");

    /**
     * 为item分配id，并且遍历其子孙元素，也进行id分配.
     *
     * @param item 元素对象.
     * @param idSet
     * @private
     */
    _handleId(item, idSet) {
        // 文本不配被分配id.
        if (!item.name || item.name === "$text") {
            return;
        }

        let id = item.getAttribute("id");
        if (this.IGNORE_ID_ELEMENTS.has(item.name)) {
            id && idSet.add(id);
            return;
        }

        // 如果id不存在，或者id已经被分配过了，则说明需要重新给新增的元素生成id.
        if (!id || idSet.has(id)) {
            id = this.editor.generateId();
            this.editor.model.change(writer => writer.setAttribute("id", id, item));
        }

        idSet.add(id);

        // 遍历子孙元素.
        Array.from(item.getChildren()).forEach(c => this._handleId(c, idSet));
    }

    _getIdSet(rootName) {
        let idSet = this._rootIdSetMap.get(rootName);
        if (!idSet) {
            idSet = new Set();
            this._rootIdSetMap.set(rootName, idSet);
        }
        return idSet;
    }

    _isElementInsertChange(change) {
        return change.type === "insert" && change.position.nodeAfter && change.position.nodeAfter.name;
    }

    /**
     * 清除所有的id数据.
     *
     * @param rootName 根节点名称.
     */
    clearIds(rootName) {
        const idSet = this._rootIdSetMap.get(rootName);
        idSet && idSet.clear();
    }

    /**
     * @inheritDoc
     * @override
     */
    destroy() {
        super.destroy();
        this._rootIdSetMap.clear();
        this._rootIdSetMap = null;
    }
}