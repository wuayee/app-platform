import {Command} from "@ckeditor/ckeditor5-core";

/**
 * 图形删除指令.
 *
 * @author z00559346 张越.
 */
export default class ShapeRemoveCommand extends Command {
    /**
     * @inheritDoc
     * @override
     */
    execute(options) {
        const editor = this.editor;
        const model = editor.model;

        const {isUndoable = true, id, isUndo = false} = options;
        const modelElement = this._getElementById(id);

        // modelElement不存在，说明元素已被删除，直接返回.
        if (!modelElement) {
            return false;
        }

        if (!isUndoable) {
            const batch = model.createBatch({isUndoable: false, isUndo});
            model.enqueueChange(batch, (writer) => {
                writer.remove(modelElement);
            });
        } else {
            model.change(writer => {
                writer.remove(modelElement);
            });
        }

        return true;
    }

    _getElementById(id) {
        const dom = document.getElementById(id);
        if (!dom) {
            return;
        }
        const viewElement = this.editor.editing.view.domConverter.domToView(dom);
        return this.editor.editing.mapper.toModelElement(viewElement);
    }
}