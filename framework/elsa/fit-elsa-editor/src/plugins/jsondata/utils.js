/**
 * 获取dom元素的矩形.
 * 1、包含margin.计算时，坐标及宽高等会加上margin距离.
 *
 * @param dom dom元素.
 * @returns {{top: number, left: number, bottom: number, width: number, x: number, y: number, right: number, height: number}}
 */
export function getDomClientRect(dom) {
    const rect = dom.getBoundingClientRect();
    const computedStyle = getComputedStyle(dom);
    const marginTop = parseFloat(computedStyle.marginTop.replace("px", ""));
    const marginBottom = parseFloat(computedStyle.marginBottom.replace("px", ""));
    const marginLeft = parseFloat(computedStyle.marginLeft.replace("px", ""));
    const marginRight = parseFloat(computedStyle.marginRight.replace("px", ""));

    return {
        bottom: rect.bottom + marginBottom,
        height: rect.height + marginTop + marginBottom,
        left: rect.left - marginLeft,
        right: rect.right + marginRight,
        top: rect.top - marginTop,
        width: rect.width + marginLeft + marginRight,
        x: rect.left - marginLeft,
        y: rect.top - marginTop
    }
}

/**
 * 获取dom的坐标.
 *
 * @param dom dom对象.
 * @return {{x: number, y: number}} 坐标对象.
 */
export function getPositionByDom(dom) {
    const rect = getDomClientRect(dom);
    return {
        x: parseFloat(rect.x.toFixed(2)), y: parseFloat(rect.y.toFixed(2))
    };
}

/**
 * 通过dom的唯一标记获取其坐标.
 *
 * @param id 唯一标识.
 * @return {null|{x: number, y: number}} 返回null或坐标对象.
 */
export function getPositionByDomId(id) {
    const dom = document.getElementById(id);
    if (!dom) {
        return null;
    }
    return getPositionByDom(dom);
}