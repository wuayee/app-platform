import {Plugin} from "@ckeditor/ckeditor5-core";
import {toWidget, viewToModelPositionOutsideModelElement, Widget} from "@ckeditor/ckeditor5-widget";
import "../../../theme/shape/shape.css";
import ShapeCommand from "./shapeCommand";
import {EXTERNAL_ID_NAME} from "./const";
import ShapeRemoveCommand from "./shapeRemoveCommand";

/**
 * 图形编辑组件.
 *
 * @author z00559346 张越.
 */
export default class ShapeEditing extends Plugin {
    static get requires() {
        return [Widget];
    }

    /**
     * @inheritDoc
     * @override
     */
    init() {
        this._defineSchema();
        this._defineConverters();

        // 所有和shape相关的命令.
        this.editor.commands.add('shape', new ShapeCommand(this.editor));
        this.editor.commands.add('removeShape', new ShapeRemoveCommand(this.editor));

        // 让shape在编辑器也能让selection识别到.
        this.editor.editing.mapper.on('viewToModelPosition',
            viewToModelPositionOutsideModelElement(this.editor.model, viewElement => viewElement.hasClass("shape")));
    }

    _defineSchema() {
        const schema = this.editor.model.schema;
        schema.register("shape", {
            allowWhere: "$text",
            isInline: true,
            isObject: true,
            allowAttributesOf: "$text",
            allowAttributes: ["id", EXTERNAL_ID_NAME, "width", "height"]
        });
    }

    _defineConverters() {
        const conversion = this.editor.conversion;

        conversion.for("upcast").elementToElement({
            view: {name: "span", classes: ["shape-external"]}, model: (viewElement, {writer: modelWriter}) => {
                const attributes = {};
                attributes.id = viewElement.getAttribute("id");
                attributes[EXTERNAL_ID_NAME] = viewElement.getAttribute(EXTERNAL_ID_NAME);
                attributes.width = viewElement.getAttribute("width");
                attributes.height = viewElement.getAttribute("height");
                return modelWriter.createElement("shape", attributes);
            }
        });

        conversion.for("editingDowncast").elementToElement({
            model: "shape", view: (modelItem, {writer: viewWriter}) => {
                const widgetElement = this._createShapeView(modelItem, viewWriter);
                return toWidget(widgetElement, viewWriter);
            }
        });

        conversion.for("dataDowncast").elementToElement({
            model: "shape", view: (modelItem, {writer: viewWriter}) => this._createShapeView(modelItem, viewWriter)
        });
    }

    _createShapeView(modelItem, viewWriter) {
        const id = modelItem.getAttribute("id");
        const externalId = modelItem.getAttribute(EXTERNAL_ID_NAME);
        const width = modelItem.getAttribute("width");
        const height = modelItem.getAttribute("height");
        const shapeView = viewWriter.createContainerElement('span', {class: "shape-external"});
        viewWriter.setAttribute("id", id, shapeView);
        viewWriter.setAttribute(EXTERNAL_ID_NAME, externalId, shapeView);
        viewWriter.setAttribute("width", width, shapeView);
        viewWriter.setAttribute("height", height, shapeView);
        viewWriter.setStyle("display", "inline-block", shapeView);
        viewWriter.setStyle("width", width, shapeView);
        viewWriter.setStyle("height", height, shapeView);
        return shapeView;
    }
}