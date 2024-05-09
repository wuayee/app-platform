import {offsetPosition} from "../../../common/util.js";

/**
 * 工具类.
 *
 * @author z00559346 张越.
 */
export default class Utils {
    /**
     * 将shape的坐标还原成事件坐标.
     *
     * @param shapeX shape的横坐标.
     * @param shapeY shape的纵坐标.
     * @param page 页面对象.
     * @return {{x: *, y: *}}
     */
    static toEventPosition(shapeX, shapeY, page) {
        const os = offsetPosition(page);
        const x = (shapeX + page.x) * page.scaleX + os.x;
        const y = (shapeY + page.y) * page.scaleY + os.y;
        return {x, y};
    }

    /**
     * 判断shape是否处于拖拽中，包括shape的连接点和region.
     *
     * @param shape 图形对象.
     * @return {boolean|*|boolean|boolean} true/false.
     */
    static isDragging = (shape) => {
        return shape.inDragging || shape.mousedownConnector !== null || (shape.mousedownRegion && shape.mousedownRegion.draggable);
    }
}