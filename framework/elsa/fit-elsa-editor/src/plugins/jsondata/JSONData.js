import {Plugin} from "@ckeditor/ckeditor5-core";
import {getDomClientRect} from "./utils";

/**
 * Json数据插，用于将ckeditor相关的数据结构转换成自定义json格式.
 *
 * @author z00559346 张越.
 */
export default class JSONData extends Plugin {
    static get pluginName() {
        return 'JSONData';
    }

    init() {
        const editor = this.editor;

        /**
         * 将json字符串转换成ck的数据格式.
         *
         * @param data json数据.
         */
        editor.data.init = data => {
            editor.model.enqueueChange({isUndoable: false}, writer => {
                for (const rootName of Object.keys(data)) {
                    if (!Array.isArray(data[rootName])) {
                        throw new Error("JSONData#init: root data must be an array.");
                    }
                    const modelRoot = editor.model.document.getRoot(rootName);
                    append(writer, modelRoot, data[rootName]);
                }
            });
        };

        /**
         * 将ck的数据结构转换成json.
         *
         * @param modelElementOrFragment ck数据结构.
         * @returns {*[]} json数据.
         */
        editor.data.stringify = modelElementOrFragment => {
            return Array.from(modelElementOrFragment.getChildren()).map(child => this._traverse(child));
        };
    }

    _traverse(modelElement) {
        if (!modelElement.is("element") || modelElement.name === "softBreak") {
            return modelElement.toJSON();
        }

        const json = this._toJSON(modelElement);
        const children = Array.from(modelElement.getChildren());
        if (children.length > 0) {
            json.children = children.map(child => this._traverse(child));
        }
        return json;
    }

    _toJSON(modelElement) {
        const elementJSON = modelElement.toJSON();

        // 给header添加level字段.
        if (elementJSON.name.startsWith("heading")) {
            elementJSON.level = parseInt(elementJSON.name.replace("heading", ""));
        }

        const domNode = this._toDom(modelElement);
        if (!domNode) {
            return elementJSON;
        }
        const rect = getDomClientRect(domNode);
        elementJSON.x = parseFloat(rect.x.toFixed(2));
        elementJSON.y = parseFloat(rect.y.toFixed(2));
        elementJSON.width = parseFloat(rect.width.toFixed(2));
        elementJSON.height = parseFloat(rect.height.toFixed(2));
        return elementJSON;
    }

    /**
     * 获取 modelElement 对应的 dom 对象.
     *
     * @param modelElement 模型元素.
     * @returns {Node|DocumentFragment} dom节点.
     * @private
     */
    _toDom(modelElement) {
        const editor = this.editor;
        const editing = editor.editing;
        const viewElement = editing.mapper.toViewElement(modelElement);
        return viewElement ? editing.view.domConverter.viewToDom(viewElement) : null;
    }
}

const append = (writer, parentElement, childrenData = []) => {
    for (const child of childrenData) {
        if (!child.name) {
            writer.appendText(child.data, child.attributes, parentElement);
        } else {
            const childElement = writer.createElement(child.name, child.attributes);
            writer.append(childElement, parentElement);
            append(writer, childElement, child.children);
        }
    }
}