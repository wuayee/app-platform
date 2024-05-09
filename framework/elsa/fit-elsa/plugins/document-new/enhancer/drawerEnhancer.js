const DEFAULT_OUTLINE = "hsl(218,81.8%,56.9%) solid 2px";

/**
 * 增强绘制器相关操作.
 *
 * @param shape 图形对象.
 */
export const enhanceDrawer = (shape) => {
    // 是否和文本一起被选中的标志位.
    shape.isSelectedWithText = false;
    shape.addDetection(["isSelectedWithText"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        shape.drawer.drawBorder();
    });

    const drawer = shape.drawer;

    /**
     * 当和文本一起被选中时，需要设置outline，表明当前图形被选中.
     *
     * @inheritDoc
     * @override
     */
    const drawerBorder = drawer.drawBorder;
    drawer.drawBorder = () => {
        drawerBorder.apply(drawer);
        if (shape.isSelectedWithText) {
            drawer.updateIfChange(drawer.parent.style, "outline", DEFAULT_OUTLINE, "parent_outline");
        } else {
            drawer.updateIfChange(drawer.parent.style, "outline", "", "parent_outline");
        }
    };
}