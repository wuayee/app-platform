import {SHAPE_IN_DOCUMENT_MODE} from "../common/const.js";
import Utils from "../common/Utils.js";
import {getRotatedCoordinate} from "../../../common/util.js";
import {layoutCommand} from "../../../core/commands.js";

/**
 * 增强图形和文本中图形元素的纠缠事件.
 *
 * @param shape 图形对象.
 * @param docSection 文档.
 */
export const enhanceEntanglement = (shape, docSection) => {
    if (!docSection) {
        return;
    }

    shape.serializedFields.batchAdd("externalId");

    /**
     * 当删除图形时，若图形时嵌入态，则删除对应的纠缠图形.
     *
     * @override
     */
    const remove = shape.remove;
    shape.remove = (source) => {
        if (shape.isEmbedded()) {
            shape.removeEntanglementShape();
        }
        return remove.apply(shape, [source]);
    }

    /**
     * 旋转时，需要resize文本中的shape元素.
     *
     * @inheritDoc
     * @override
     */
    const initConnectors = shape.initConnectors;
    shape.initConnectors = () => {
        initConnectors.call(shape);
        if(shape.isType("line") || shape.isType("docPen")) {
            return;
        }
        const c8 = shape.connectors[8];
        const moving = c8.moving;
        c8.moving = (deltaX, deltaY, x, y) => {
            moving.apply(c8, [deltaX, deltaY, x, y]);
            if (shape.isEmbedded()) {
                // 求出最大最小的x和y，算出新的高度和宽度，从而resize嵌入图形.
                const maxAndMin = getMaxAndMin(shape.get("rotateDegree") * Math.PI / 180, elementPosition.x, elementPosition.y);
                resizeEntanglementShape(maxAndMin.maxX - maxAndMin.minX, maxAndMin.maxY - maxAndMin.minY);
            }
        }
    };

    const getMaxAndMin = (degree, outerX, outerY) => {
        const x = outerX ? outerX : shape.x;
        const y = outerY ? outerY : shape.y;
        const westNorthPoint = {x: x, y: y};
        const westSouthPoint = {x: x, y: y + shape.height};
        const eastNorthPoint = {x: x + shape.width, y: y};
        const eastSouthPoint = {x: x + shape.width, y: y + shape.height};
        const centerPoint = {x: x + (shape.width / 2), y: y + (shape.height / 2)};

        const rotateWestNorthPoint = getRotatedCoordinate(westNorthPoint.x, westNorthPoint.y, centerPoint.x, centerPoint.y, degree);
        const rotateWestSouthPoint = getRotatedCoordinate(westSouthPoint.x, westSouthPoint.y, centerPoint.x, centerPoint.y, degree);
        const rotateEastNorthPoint = getRotatedCoordinate(eastNorthPoint.x, eastNorthPoint.y, centerPoint.x, centerPoint.y, degree);
        const rotateEastSouthPoint = getRotatedCoordinate(eastSouthPoint.x, eastSouthPoint.y, centerPoint.x, centerPoint.y, degree);

        const maxX = Math.max(rotateWestNorthPoint.x, rotateWestSouthPoint.x, rotateEastNorthPoint.x, rotateEastSouthPoint.x);
        const minX = Math.min(rotateWestNorthPoint.x, rotateWestSouthPoint.x, rotateEastNorthPoint.x, rotateEastSouthPoint.x);
        const maxY = Math.max(rotateWestNorthPoint.y, rotateWestSouthPoint.y, rotateEastNorthPoint.y, rotateEastSouthPoint.y);
        const minY = Math.min(rotateWestNorthPoint.y, rotateWestSouthPoint.y, rotateEastNorthPoint.y, rotateEastSouthPoint.y);
        return {
            maxX: parseFloat(maxX.toFixed(2)),
            minX: parseFloat(minX.toFixed(2)),
            maxY: parseFloat(maxY.toFixed(2)),
            minY: parseFloat(minY.toFixed(2))
        };
    }

    /**
     * resize图形时，如果是嵌入态，则需要resize对应的文本shape.
     *
     * @inheritDoc
     * @override
     */
    const resize = shape.resize;
    shape.resize = (width, height) => {
        resize.apply(shape, [width, height]);
        if (!shape.isEmbedded()) {
            return;
        }
        resizeEntanglementShape(width, height);
    };

    const resizeEntanglementShape = (width, height) => {
        docSection.drawer.getEditor().format("resizeShape", {
            id: shape.externalId, width: width + "px", height: height + "px", isUndoable: false
        }, false);
    }

    /**
     * 如果是嵌入态，通过文本shape元素调整elsa中shape的位置.
     *
     * @param element
     */
    let elementPosition = {x: 0, y: 0};
    shape.adjust = (position) => {
        if (!shape.isEmbedded()) {
            return;
        }

        elementPosition = position;

        // 通过mouseContext获取拖拽之前的图形的position
        // 若mouseContext不存在，则将shape当前的x、y作为prePosition.
        const getPrePosition = () => {
            const contextShapes = shape.page.mouseActions.mouseContext.shapes;
            if (!contextShapes) {
                return {x: shape.x, y: shape.y};
            }

            const contextShape = contextShapes.find(s => s.shape === shape);
            if (!contextShape) {
                return {x: shape.x, y: shape.y};
            }

            return {x: contextShape.x.preValue, y: contextShape.y.preValue};
        }

        const prePosition = getPrePosition();
        const commandData = {
            shape, x: {preValue: prePosition.x, value: position.x}, y: {preValue: prePosition.y, value: position.y}
        };
        if (shape.get("rotateDegree") !== 0) {
            // 如果旋转角度不等于0，则需要计算图形的坐标，使图形在文本shape的范围内.
            const maxAndMin = getMaxAndMin(shape.get("rotateDegree") * Math.PI / 180, position.x, position.y);
            let dx = position.x - maxAndMin.minX;
            let dy = position.y - maxAndMin.minY;
            const newX = parseFloat((position.x + dx).toFixed(2));
            const newY = parseFloat((position.y + dy).toFixed(2));
            commandData.x.value = newX;
            commandData.y.value = newY;
            shape.moveTo(newX, newY);
        } else {
            shape.moveTo(position.x, position.y);
        }
        return commandData;
    };

    /**
     * 使图形嵌入文本中.
     */
    shape.embed = () => {
        if (shape.isEmbedded() || isUndo(shape)) {
            return;
        }
        const externalId = shape.insertEntanglementShape();
        shape.page.moveIndexBottom(shape);
        layoutCommand(shape.page, [{shape, externalId, mode: SHAPE_IN_DOCUMENT_MODE.EMBED}]).execute(shape.page);
    };

    /**
     * 使图形悬浮于文本上.
     */
    shape.suspension = () => {
        if (!shape.isEmbedded() || shape.inDragging || isUndo(shape)) {
            return;
        }
        shape.removeEntanglementShape();
        layoutCommand(shape.page, [{
            shape, externalId: null, mode: SHAPE_IN_DOCUMENT_MODE.SUSPENSION
        }]).execute(shape.page);
        shape.page.moveIndexTop(shape);
    };

    /**
     * 插入纠缠图形.
     * 如果graph正处于撤销或重做的场景下，不进行处理，交给编辑器进行处理。
     */
    shape.insertEntanglementShape = () => {
        if (shape.graph.inRedo || shape.graph.inUndo) {
            return;
        }

        const eventPosition = Utils.toEventPosition(shape.x, shape.y, shape.page);
        const editor = docSection.drawer.getEditor();

        const param = {};
        param.externalId = shape.id;
        param.size = {width: shape.width + "px", height: shape.height + "px"};
        param.selection = docSection.drawer.withPointerEvents(() => editor.positionToSelection(eventPosition));
        return editor.format("shape", param, false);
    };

    /**
     * 删除纠缠图形.
     * 如果graph正处于撤销或重做的场景下，不进行处理，交给编辑器进行处理。
     */
    shape.removeEntanglementShape = () => {
        if (shape.graph.inRedo || shape.graph.inUndo) {
            return;
        }
        const editor = docSection.drawer.getEditor();
        editor.format("removeShape", {id: shape.externalId}, false);
    };

    const isUndo = (shape) => {
        return shape.graph.inUndo || shape.graph.inRedo;
    }

    /**
     * @maliya 2023.6.9 临时方案，为鸿蒙演示
     * 文档中批注功能，要求：批注的笔记可以跟随文字自适应变化，先只实现一根直线，不考虑圆
     */
    shape.createReferWithText = () => {
        const referId = `refer_text_${shape.id}`;
        const startPos = Utils.toEventPosition(shape.x, shape.y, shape.page);
        const endPos = Utils.toEventPosition(shape.x+shape.width, shape.y+shape.height, shape.page);
        const editor = docSection.drawer.getEditor();
        const ckEditor = editor.editor;
        const model = ckEditor.model;

        model.schema.extend('$text', { allowAttributes: 'highlight' });
        ckEditor.conversion.for( 'downcast' ).attributeToElement( {
            model: 'highlight',
            view: ( modelAttributeValue, { writer } ) => {
                return writer.createAttributeElement( 'span', { class: 'highlight', id: referId, style:`display:inline-block` }, { priority: 5 } );
            }
        } );

        model.change(writer => {
            const p1 = docSection.drawer.withPointerEvents(() => editor.positionToSelection(startPos));
            const p2 = docSection.drawer.withPointerEvents(() => editor.positionToSelection(endPos));
            const range = model.createRange(p1.getFirstPosition(), p2.getLastPosition());

            writer.setAttribute('highlight', true, range);

            setTimeout(() => {
                const element = document.querySelector(`#${referId}`);
                const resizeObserver = new ResizeObserver(entries => {
                    for (let entry of entries) {
                        shape.width = entry.contentRect.width;
                        shape.invalidateAlone();
                    }
                });
                resizeObserver.observe(element);
            })
        });
    }

}