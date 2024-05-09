/**
 * {@code DiffItem} 的操作类.{@see #Differ#getChanges()}.
 *
 * @author z00559346 张越.
 */
export class ChangeUtils {
    /**
     * 获取root名称.
     *
     * @param change Change对象.
     * @return {string} 名称字符串.
     */
    static getRootName(change) {
        if (change.position) {
            return change.position.root.rootName;
        }
        return change.range.start.root.rootName;
    }
}