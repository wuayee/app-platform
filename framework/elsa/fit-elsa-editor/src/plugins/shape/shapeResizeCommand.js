import {Plugin} from "@ckeditor/ckeditor5-core";

/**
 * 调整图形大小指令.
 *
 * @author z00559346 张越.
 */
export default class ShapeResizeCommand extends Plugin {
    /**
     * @inheritDoc
     * @override
     */
    refresh() {
        const editor = this.editor;
        const element = this._getSelectedShapeElement(editor.model.document.selection);
        this.isEnabled = !!element;
        if (!element || (!element.hasAttribute("width") && !element.hasAttribute("height"))) {
            this.value = null;
        } else {
            this.value = {
                width: element.getAttribute("width"), height: element.getAttribute("height")
            };
        }
    }

    /**
     * @inheritDoc
     * @override
     */
    execute(options) {
        const editor = this.editor;
        const model = editor.model;
        const {isUndoable = true, id, width, height} = options;
        const shapeElement = id ? this._getSelectedShapeElementById(id) : this._getSelectedShapeElement(model.document.selection);

        this.value = {width: width, height: height};

        if (shapeElement) {
            if (!isUndoable) {
                const batch = model.createBatch({isUndoable: false});
                model.enqueueChange(batch, (writer) => {
                    writer.setAttribute("width", width, shapeElement);
                    writer.setAttribute("height", height, shapeElement);
                });
            } else {
                model.change(writer => {
                    writer.setAttribute("width", width, shapeElement);
                    writer.setAttribute("height", height, shapeElement);
                });
            }
        }
    }

    _getSelectedShapeElementById(id) {
        const dom = document.getElementById(id);
        if (!dom) {
            return;
        }
        const viewElement = this.editor.editing.view.domConverter.domToView(dom);
        return this.editor.editing.mapper.toModelElement(viewElement);
    }

    _getSelectedShapeElement(selection) {
        const selectedElement = selection.getSelectedElement();
        return this._isShape(selectedElement) ? selectedElement : null;
    }

    _isShape(element) {
        return !!element && element.is("element", "shape");
    }
}