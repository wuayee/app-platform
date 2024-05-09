import {Plugin} from "@ckeditor/ckeditor5-core";
import AbbreviationEditing from "./AbbreviationEditing";
import AbbreviationUI from "./AbbreviationUI";

export default class Abbreviation extends Plugin {
    static get requires() {
        return [AbbreviationEditing, AbbreviationUI];
    }
}