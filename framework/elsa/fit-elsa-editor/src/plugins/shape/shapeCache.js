/**
 * 图形缓存.
 * 通过两层map来进行存在.
 * 1、第一层map用于存储root和其下面的图形关系.
 * 2、第二层map用于存储shapeId和编辑器中图形之间的关系.
 *
 * @author z00559346 张越.
 */
export default class ShapeCache {
    _cache = new Map();

    /**
     * 设置缓存.
     *
     * @param rootName 根节点名称.
     * @param shapeId 图形id.
     * @param data 图形数据.
     */
    set(rootName, shapeId, data) {
        let shapeMap = this._cache.get(rootName);
        if (!shapeMap) {
            shapeMap = new Map();
            this._cache.set(rootName, shapeMap);
        }
        shapeMap.set(shapeId, data);
    }

    /**
     * 删除图形数据.
     *
     * @param rootName 根节点名称.
     * @param shapeId 图形id.
     */
    remove(rootName, shapeId) {
        const shapeMap = this._cache.get(rootName);
        if (!shapeMap) {
            return;
        }
        shapeMap.delete(shapeId);
    }

    /**
     * 是否存在某个图形.
     *
     * @param rootName 根节点名称.
     * @param shapeId 图形id.
     * @return {any} true/false.
     */
    has(rootName, shapeId) {
        const shapeMap = this._cache.get(rootName);
        return shapeMap && shapeMap.has(shapeId);
    }

    /**
     * 获取图形数据.
     *
     * @param rootName 根节点名称.
     * @param shapeId 图形id.
     * @return {*|null} 图形数据或null.
     */
    get(rootName, shapeId) {
        const shapeMap = this._cache.get(rootName);
        return shapeMap ? shapeMap.get(shapeId) : null;
    }

    /**
     * 遍历某个根节点下的所有图形数据.
     *
     * @param rootName 根节点名称.
     * @param action 处理操作.
     */
    forEach(rootName, action) {
        const shapeMap = this._cache.get(rootName);
        if (!shapeMap) {
            return;
        }
        shapeMap.forEach((value, key, map) => action(value, key, map));
    }

    /**
     * 销毁缓存.
     */
    destroy() {
        this._cache.clear();
        this._cache = null;
    }
}