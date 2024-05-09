import {Plugin} from "@ckeditor/ckeditor5-core";
import ShapeEditing from "./shapeEditing";
import ShapeChineseResolver from "./shapeChineseResolver";
import ShapeObserver from "./shapeObserver";

/**
 * Elsa自定义对应的图形插件.
 *
 * @author z00559346 张越.
 */
export default class Shape extends Plugin {
    static get requires() {
        return [ShapeEditing,
            ShapeChineseResolver,
            ShapeObserver];
    }
}