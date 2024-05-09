import {SHAPE_IN_DOCUMENT_MODE} from "../common/const.js";
import Utils from "../common/Utils.js";
import {layoutCommand} from "../../../core/commands.js";

/**
 * 增强拖拽事件.
 *
 * @param shape 图形对象.
 * @param docSection 文档.
 */
export const enhanceDraggable = (shape, docSection) => {
    if (!docSection) {
        return;
    }

    shape.mode = SHAPE_IN_DOCUMENT_MODE.SUSPENSION;
    shape.serializedFields.batchAdd("mode");
    let isDragging = false;

    /**
     * 处于拖拽状态不能删除.
     *
     * @param source 源头.
     */
    const remove = shape.remove;
    shape.remove = (source) => {
        if (isDragging) {
            return [];
        }
        return remove.apply(shape, [source]);
    };

    /**
     * 拖动图形时，修改marker的选区.
     *
     * @override
     */
    const dragging = shape.dragging;
    shape.dragging = (target) => {
        dragging.apply(shape, [target]);
        if (!shape.isEmbedded()) {
            return;
        }

        if (!isDragging) {
            shape.removeEntanglementShape();
            isDragging = true;
            docSection.drawer.enableTextPointerEvents();
        }

        // 还原坐标，将shape左上角的x、y坐标还原成浏览器原生坐标.
        const position = Utils.toEventPosition(shape.x, shape.y, shape.page);

        // 修改DragDrop的marker坐标.
        updateDragDropMarker(position.x, position.y);
    };

    /**
     * 拖动结束后，删除marker.
     *
     * @override
     */
    const endDrag = shape.endDrag;
    shape.endDrag = (target, position) => {
        endDrag.apply(shape, [target]);
        if (!shape.isEmbedded()) {
            return;
        }

        // 删除DragDrop的marker.
        removeDragDropMarker();
        if (isDragging) {
            isDragging = false;
            docSection.drawer.disableTextPointerEvents();
        }
        const externalId = shape.insertEntanglementShape();
        layoutCommand(shape.page, [{shape, externalId}]).execute(shape.page);

        /**
         * 如果触发了插入图形的操作，则将context中的command删除掉，在mouseUp之后不再生成position命令.
         * 具体查看 {@link bindMouseActions#onMouseUp} 方法.
         */
        delete position.context.command;
    };

    shape.isEmbedded = () => {
        return shape.mode && shape.mode === SHAPE_IN_DOCUMENT_MODE.EMBED;
    }

    const updateDragDropMarker = (x, y) => {
        const editor = docSection.drawer.getEditor();
        editor && editor.updateDragDropMarker(x, y);
    };

    const removeDragDropMarker = () => {
        const editor = docSection.drawer.getEditor();
        editor && editor.removeDragDropMarker();
    }
}