import {getPositionByDomId} from "../jsondata/utils";
import {ChangeUtils} from "../../utils/ChangeUtils";
import {EXTERNAL_ID_NAME} from "./const";

/**
 * 图形观察事件上下文对象.
 *
 * @author z00559346 张越.
 */
export default class ShapeObserverContext {
    _insertShapes = new Map();
    _copyShapes = new Map();
    _removeShapes = new Map();
    _modifyShapes = new Map();

    constructor(editor, shapeCache, parentShapeMapping) {
        this._editor = editor;
        this._shapeCache = shapeCache;
        this._parentShapeMapping = parentShapeMapping;
    }

    /**
     * 添加插入shape.
     *
     * @param rootName 根节点名称.
     * @param shapeId 图形id.
     * @param data 图形数据.
     */
    addInsertShape(rootName, shapeId, data) {
        let shapeMap = this._insertShapes.get(rootName);
        if (!shapeMap) {
            shapeMap = new Map();
            this._insertShapes.set(rootName, shapeMap);
        }
        shapeMap.set(shapeId, data);
    }

    /**
     * 添加拷贝shape.
     *
     * @param rootName 根节点名称.
     * @param shapeId 图形id.
     * @param data 图形数据.
     */
    addCopyShape(rootName, shapeId, data) {
        let shapeMap = this._copyShapes.get(rootName);
        if (!shapeMap) {
            shapeMap = new Map();
            this._copyShapes.set(rootName, shapeMap);
        }
        shapeMap.set(shapeId, data);
    }

    /**
     * 添加删除shape.
     *
     * @param rootName 根节点名称.
     * @param shapeId 图形id.
     */
    addRemovedShape(rootName, shapeId) {
        let shapeIdSet = this._removeShapes.get(rootName);
        if (!shapeIdSet) {
            shapeIdSet = new Set();
            this._removeShapes.set(rootName, shapeIdSet);
        }
        shapeIdSet.add(shapeId);
    }

    /**
     * 删除 已删除的shape的id.
     *
     * @param rootName 根节点id.
     * @param shapeId 图形id.
     */
    deleteRemovedShape(rootName, shapeId) {
        let shapeIdSet = this._removeShapes.get(rootName);
        if (!shapeIdSet) {
            return;
        }
        shapeIdSet.delete(shapeId);
    }

    /**
     * 添加拷贝shape.
     *
     * @param rootName 根节点名称.
     * @param shapeId 图形id.
     * @param data 图形数据.
     */
    addModifyShape(rootName, shapeId, data) {
        let shapeMap = this._modifyShapes.get(rootName);
        if (!shapeMap) {
            shapeMap = new Map();
            this._modifyShapes.set(rootName, shapeMap);
        }
        shapeMap.set(shapeId, data);
    }

    /**
     * 在插入的图形集合中是否存在该shapeId.
     *
     * @param rootName 根节点名称.
     * @param shapeId 图形id.
     * @return {any} true/false.
     */
    hasInsertShape(rootName, shapeId) {
        const shapeMap = this._insertShapes.get(rootName);
        return shapeMap && shapeMap.has(shapeId);
    }

    /**
     * 在拷贝的图形集合中是否存在该shapeId.
     *
     * @param rootName 根节点名称.
     * @param shapeId 图形id.
     * @return {any} true/false.
     */
    hasCopyShape(rootName, shapeId) {
        const shapeMap = this._copyShapes.get(rootName);
        return shapeMap && shapeMap.has(shapeId);
    }

    /**
     * 触发所有事件.
     */
    fireEvents(isUndo) {
        this._insertShapes.forEach((value, rootName) => {
            const shapes = Array.from(value.values());
            shapes.forEach(s => this._setPosition(s.item));
            this._editor.fire(rootName + ":shape:add", {shapes, isUndo});
        });

        // 考虑将删除事件变成同步，因为该事件不依赖于异步机制获取dom数据.
        this._removeShapes.forEach((value, rootName) => {
            this._editor.fire(rootName + ":shape:remove", {shapes: Array.from(value), isUndo});
        });

        this._copyShapes.forEach((value, rootName) => {
            const shapes = Array.from(value.values());
            shapes.forEach(s => this._setPosition(s.item));
            this._editor.fire(rootName + ":shape:copy", {shapes, isUndo});
        });

        this._modifyShapes.forEach((value, rootName) => {
            this._editor.fire(rootName + ":shape:modify", {shapes: Array.from(value.values()), isUndo});
        });
    }

    _setPosition = (itemJson) => {
        const position = getPositionByDomId(itemJson.attributes.id);
        itemJson.x = position.x;
        itemJson.y = position.y;
    }

    /**
     * 根基change列表统计插入、拷贝、删除等信息.
     *
     * @param changes 变更列表.
     * @return {Set<any>} rootName集合.
     */
    statisticsChanges(changes) {
        const rootNames = new Set();
        changes.sort((a, b) => a.type === b.type ? 0 : (a.type === "remove" ? -1 : 0))
            .forEach(change => {
                rootNames.add(ChangeUtils.getRootName(change));
                if (!change.name || change.name === "$text") {
                    return;
                }

                if (change.type === "insert") {
                    if (change.name === "shape") {
                        this._handleShapeInsertion(change.position.nodeAfter);
                    } else {
                        const parent = change.position.nodeAfter;
                        const children = Array.from(parent.getChildren());
                        children.filter(c => c.name === "shape")
                            .forEach(c => this._handleShapeInsertion(c));
                    }
                } else if (change.type === "remove") {
                    this._handleShapeDeletion(change);
                }
            });
        return rootNames;
    }

    _handleShapeInsertion(item) {
        const externalId = item.getAttribute(EXTERNAL_ID_NAME);
        const rootName = item.root.rootName;
        const data = item.toJSON();

        // 如果存在，则是拷贝；否则，就是新增.
        if (this._shapeCache.has(rootName, externalId)) {
            const editor = this._editor;
            const newId = editor.generateId();
            editor.model.change(writer => writer.setAttribute(EXTERNAL_ID_NAME, newId, item));
            data.attributes[EXTERNAL_ID_NAME] = newId;
            this._shapeCache.set(rootName, newId, data);
            this._parentShapeMapping.addMapping(rootName, item.parent.getAttribute("id"), newId);
            this.addCopyShape(rootName, newId, {item: data, newId, oldId: externalId});
        } else {
            this._shapeCache.set(rootName, externalId, data);
            this._parentShapeMapping.addMapping(rootName, item.parent.getAttribute("id"), externalId);
            this.addInsertShape(rootName, externalId, {item: data, shapeId: externalId});

            // 在removeShapes中存在，说明之前被删除了；这里又进行了插入，因此需要将其从删除结合中排除.
            this.deleteRemovedShape(rootName, externalId);
        }
    }

    _handleShapeDeletion(change) {
        const rootName = change.position.root.rootName;
        const parentId = change.name === "shape" ? change.position.parent.getAttribute("id") : change.attributes.get("id");
        const shapeIds = change.name === "shape" ? [change.attributes.get(EXTERNAL_ID_NAME)] : this._parentShapeMapping.getShapeIds(rootName, parentId);
        this._parentShapeMapping.removeMapping(rootName, parentId);
        shapeIds.forEach(shapeId => {
            this._shapeCache.remove(rootName, shapeId);
            this.addRemovedShape(rootName, shapeId);
        });
    }

    /**
     * 统计修改信息.
     *
     * @param rootNames 根节点集合.
     */
    statisticsModification(rootNames) {
        rootNames.forEach(rootName => {
            this._shapeCache.forEach(rootName, (value, key) => {
                if (this.hasInsertShape(rootName, key) || this.hasCopyShape(rootName, key)) {
                    return;
                }
                const position = getPositionByDomId(value.attributes.id);
                const dom = document.getElementById(value.attributes.id);
                const domRect = dom.getBoundingClientRect();

                // 当前的矩形.
                const currentRect = new Rect(position.x, position.y, domRect.width, domRect.height);

                // 之前的矩形.
                const prevRect = new Rect(value.x, value.y, value.width, value.height);
                if (!prevRect.equals(currentRect)) {
                    this.addModifyShape(rootName, key, {...value, ...position});

                    // 修改cache中的值.
                    value.x = position.x;
                    value.y = position.y;
                    value.width = domRect.width;
                    value.height = domRect.height;
                }
            });
        });
    }
}

/**
 * 矩形.
 *
 * @author z00559346 张越.
 */
class Rect {
    _x;
    _y;
    _width;
    _height;

    constructor(x, y, width, height) {
        this._x = x;
        this._y = y;
        this._width = width;
        this._height = height;
    }

    /**
     * 判断两个矩形是否相等.只有当坐标一致，宽高一致才是相等的.
     *
     * @param rect 其他矩形对象.
     * @return {boolean} true/false.
     */
    equals(rect) {
        if (rect === this) {
            return true;
        }

        return this._x === rect.x && this._y === rect.y && this._width === rect.width && this._height === rect.height;
    }

    get x() {
        return this._x;
    }

    get y() {
        return this._y;
    }

    get width() {
        return this._width;
    }

    get height() {
        return this._height;
    }
}