/**
 * 获取范围内的所有文本.
 *
 * @param range 范围.
 * @returns {unknown} 范围内的文本字符串.
 */
export default function getRangeText(range) {
    return Array.from(range.getItems()).reduce((rangeText, node) => {
        if (!(node.is('text') || node.is('textProxy'))) {
            return rangeText;
        }
        return rangeText + node.data;
    }, "");
}