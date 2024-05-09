import {containerDrawer} from "../../../core/drawers/containerDrawer.js";

/**
 * docFrame绘制器.
 *
 * @author z00559346 张越.
 */
export const docFrameDrawer = (shape, div, x, y) => {
    const self = containerDrawer(shape, div, x, y);

    /**
     * 适配滚动条，需将overflow设置为默认值.
     *
     * @override
     */
    const containerResize = self.containerResize;
    self.containerResize = (width, height) => {
        containerResize.apply(self, [width, height]);
        self.container.style.overflow = "visible";
        self.container.style.height = "100%";
    }

    return self;
}