import {Plugin} from "@ckeditor/ckeditor5-core";
import ShapeCache from "./shapeCache";
import ShapeObserverContext from "./shapeObserverContext";

/**
 * 图形插件删除修改观察者.
 *
 * @author z00559346 张越.
 */
export default class ShapeObserver extends Plugin {
    _shapeCache = new ShapeCache();
    _parentShapeMapping = new ParentShapeMapping();

    /**
     * @inheritDoc
     * @override
     */
    init() {
        const editor = this.editor;

        /**
         * 文档内容发生变化时，需要监听所有shape事件，包括新增、拷贝、删除、修改.
         */
        editor.model.document.on('change:data', (evt, batch) => {
            const isUndo = batch.isUndo;
            const changes = editor.model.document.differ.getChanges();
            const context = new ShapeObserverContext(editor, this._shapeCache, this._parentShapeMapping);
            const rootNames = context.statisticsChanges(changes);
            Promise.resolve().then(() => {
                context.statisticsModification(rootNames);
                context.fireEvents(isUndo);
            });
        }, {priority: 'high'});

        /**
         * 当输入中文时，也需要改变shape的坐标，因此，这里需要对中文输入做出特殊处理.
         */
        editor.editing.view.document.on("beforeinput", (evt, data) => {
            if (data.isComposing && data.inputType === "insertCompositionText") {
                const isUndo = data.isUndo;
                Promise.resolve().then(() => {
                    data.targetRanges && data.targetRanges.filter(r => r).forEach(range => {
                        const rootName = range.start.root.rootName;
                        const context = new ShapeObserverContext(editor, this._shapeCache, this._parentShapeMapping);
                        context.statisticsModification([rootName]);
                        context.fireEvents(isUndo);
                    });
                });
            }
        });
    }

    /**
     * 清除掉缓存数据.
     *
     * @inheritDoc
     * @override
     */
    destroy() {
        super.destroy();
        this._shapeCache.destroy();
        this._parentShapeMapping.destroy();
    }
}

/**
 * 图形与父节点的映射关系.
 *
 * @author z00559346 张越.
 */
class ParentShapeMapping {
    _mapping = new Map();

    /**
     * 添加关联关系.
     *
     * @param rootName 根节点名称.
     * @param parentId 父节点id.
     * @param shapeId 图形id.
     */
    addMapping(rootName, parentId, shapeId) {
        let rootMap = this._mapping.get(rootName);
        if (!rootMap) {
            rootMap = new Map();
            this._mapping.set(rootName, rootMap);
        }

        let shapeIdSet = rootMap.get(parentId);
        if (!shapeIdSet) {
            shapeIdSet = new Set();
            rootMap.set(parentId, shapeIdSet);
        }

        shapeIdSet.add(shapeId);
    }

    /**
     * 删除映射关系.
     *
     * @param rootName 根节点名称.
     * @param parentId 父节点id.
     * @param shapeId 图形id.
     */
    removeMapping(rootName, parentId, shapeId = undefined) {
        let rootMap = this._mapping.get(rootName);
        if (!rootMap) {
            return;
        }

        if (shapeId) {
            const shapeIdSet = rootMap.get(parentId);
            shapeIdSet && shapeIdSet.delete(shapeId);
        } else {
            rootMap.delete(parentId);
        }
    }

    /**
     * 获取图形id集合.
     *
     * @param rootName 根节点名称.
     * @param parentId 父节点id.
     * @return {unknown[]|*[]} 图形id集合.
     */
    getShapeIds(rootName, parentId) {
        const rootMap = this._mapping.get(rootName);
        if (!rootMap) {
            return [];
        }
        const shapeIdSet = rootMap.get(parentId);
        return shapeIdSet ? Array.from(shapeIdSet) : [];
    }

    /**
     * 销毁映射关系.清除数据.
     */
    destroy() {
        this._mapping.clear();
        this._mapping = null;
    }
}