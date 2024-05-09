import {Plugin} from "@ckeditor/ckeditor5-core";
import SimpleBoxEditing from "./SimpleBoxEditing";
import SimpleBoxUI from "./SimpleBoxUI";

export default class SimpleBox extends Plugin {
    static get requires() {
        return [SimpleBoxEditing, SimpleBoxUI];
    }
}