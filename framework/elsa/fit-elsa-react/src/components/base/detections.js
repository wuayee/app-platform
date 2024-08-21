/**
 * 添加监听.
 *
 * @param node 节点对象.
 */
export const addDetections = (node) => {
    // 可实现动态替换其中react组件的能力.
    node.addDetection(["componentName"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        node.drawer.unmountReact();
        node.invalidateAlone();
    });
};