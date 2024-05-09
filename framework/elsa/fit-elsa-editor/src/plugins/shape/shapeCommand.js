import {Command} from "@ckeditor/ckeditor5-core";
import {EXTERNAL_ID_NAME} from "./const";

/**
 * Elsa自定义对应的图形指令.
 *
 * @author z00559346 张越.
 */
export default class ShapeCommand extends Command {
    /**
     * @inheritDoc
     * @override
     */
    execute(options) {
        const editor = this.editor;
        const model = editor.model;

        const {
            isUndoable = true, id = undefined, externalId, size, isUndo = false, selection = undefined
        } = options;

        // 提取属性.
        const attributes = {};
        id ? (attributes.id = id) : (attributes.id = editor.generateId());
        attributes[EXTERNAL_ID_NAME] = externalId;
        attributes.width = size.width;
        attributes.height = size.height;

        if (!isUndoable) {
            // isUndoable 为false，不记录history.
            const batch = model.createBatch({isUndoable: false, isUndo});
            model.enqueueChange(batch, (writer) => {
                this._doInsertShape(options, writer, attributes, selection);
            });
        } else {
            model.change(writer => {
                this._doInsertShape(options, writer, attributes, selection);
            });
        }

        return attributes.id;
    }

    _doInsertShape(options, writer, attributes, selection) {
        const editor = this.editor;
        const model = editor.model;
        const shape = writer.createElement("shape", attributes);

        // 如果position存在，则插入到指定位置，否则插入到光标所在位置.
        if (selection) {
            model.insertContent(shape, selection);
        } else {
            if (options.elementId) {
                const dom = document.getElementById(options.elementId);
                const viewElement = editor.editing.view.domConverter.domToView(dom);
                const modelElement = editor.editing.mapper.toModelElement(viewElement);
                const selection = writer.createSelection(modelElement, "after");
                model.insertContent(shape, selection);
            } else {
                model.insertContent(shape);
            }
        }

        // todo@zhangyue 暂时不选中图形.
        // writer.setSelection(shape, "on");
    }

    /**
     * @inheritDoc
     * @override
     */
    refresh() {
        const model = this.editor.model;
        const selection = model.document.selection;
        this.isEnabled = model.schema.checkChild(selection.focus.parent, "shape");
    }
}