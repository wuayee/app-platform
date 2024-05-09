import {interactDrawer} from "../../../core/drawers/interactDrawer.js";

/**
 * 文档的交互层绘制器.
 *
 * @author z00559346 张越.
 */
export const docInteractDrawer = (graph, page, div) => {
    const self = interactDrawer(graph, page, div);

    /**
     * 为了让滚动条出现，在文档中，交互层需要默认overflow.
     */
    const reset = self.reset;
    self.reset = () => {
        reset.apply(self);
        self.sensor.style.overflow = "visible";
    };

    /**
     * 在文档中，不用鼠标及范围等.
     */
    self.drawSelection = () => {};

    return self;
}