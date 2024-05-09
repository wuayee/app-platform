import {Plugin} from "@ckeditor/ckeditor5-core";
import CustomizedUndoEditing from "./customizedUndoEditing";

/**
 * 用于提醒外部系统，产生了历史记录.
 *
 * @author z00559346 张越.
 */
export default class CustomizedUndo extends Plugin {
    static get pluginName() {
        return 'ElsaUndo';
    }

    static get requires() {
        return [CustomizedUndoEditing];
    }
}