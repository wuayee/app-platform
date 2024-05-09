import {Plugin} from "@ckeditor/ckeditor5-core";
import ElsaHeadingEditing from "./elsaHeadingEditing";
import {HeadingUI} from "@ckeditor/ckeditor5-heading";
import ElsaHeadingTreeManager from "./elsaHeadingTreeManager";
import ElsaHeadingDelete from "./elsaHeadingDelete";
import "../../../theme/heading/heading.css"

/**
 * Elsa自定义标题插件.
 *
 * @author z00559346 张越.
 */
export default class ElsaHeading extends Plugin {
    static get requires() {
        return [ElsaHeadingEditing, HeadingUI, ElsaHeadingTreeManager, ElsaHeadingDelete];
    }

    static get pluginName() {
        return 'elsaHeading';
    }
}