import {Plugin} from "@ckeditor/ckeditor5-core";
import ShapeResizeEditing from "./shapeResizeEditing";

/**
 * 调整图形大小组件.
 *
 * @author z00559346 张越.
 */
export default class ShapeResize extends Plugin {
    static get requires() {
        return [ShapeResizeEditing];
    }

    static get pluginName() {
        return 'ShapeResize';
    }
}