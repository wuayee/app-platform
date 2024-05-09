import {Plugin} from "@ckeditor/ckeditor5-core";
import {toWidget, toWidgetEditable} from "@ckeditor/ckeditor5-widget";

export default class SimpleBoxEditing extends Plugin {
    init() {
        console.log("SimpleBoxEditing#init() got called");

        this._defineSchema();
        this._defineConverters();
    }

    _defineSchema() {
        const schema = this.editor.model.schema;

        schema.register("simpleBox", {
            isObject: true, allowWhere: "$block"
        });

        schema.register("simpleBoxTitle", {
            isLimit: true, allowIn: "simpleBox", allowContentOf: "$block"
        });

        schema.register("simpleBoxDescription", {
            isLimit: true, allowIn: "simpleBox", allowContentOf: "$root"
        });
    }

    _defineConverters() {
        const conversion = this.editor.conversion;

        conversion.for('upcast').elementToElement({
            model: 'simpleBox', view: {
                name: 'section', classes: 'simple-box'
            }
        });
        conversion.for('dataDowncast').elementToElement({
            model: 'simpleBox', view: {
                name: 'section', classes: 'simple-box'
            }
        });
        conversion.for('editingDowncast').elementToElement({
            model: 'simpleBox', view: (modelElement, {writer: viewWriter}) => {
                const section = viewWriter.createContainerElement('section', {class: 'simple-box'});
                return toWidget(section, viewWriter, {label: 'simple box widget'});
            }
        });

        // <simpleBoxTitle> converters
        conversion.for('upcast').elementToElement({
            model: 'simpleBoxTitle', view: {
                name: 'h1', classes: 'simple-box-title'
            }
        });
        conversion.for('dataDowncast').elementToElement({
            model: 'simpleBoxTitle', view: {
                name: 'h1', classes: 'simple-box-title'
            }
        });
        conversion.for('editingDowncast').elementToElement({
            model: 'simpleBoxTitle', view: (modelElement, {writer: viewWriter}) => {
                // Note: You use a more specialized createEditableElement() method here.
                const h1 = viewWriter.createEditableElement('h1', {class: 'simple-box-title'});

                return toWidgetEditable(h1, viewWriter);
            }
        });

        // <simpleBoxDescription> converters
        conversion.for('upcast').elementToElement({
            model: 'simpleBoxDescription', view: {
                name: 'div', classes: 'simple-box-description'
            }
        });
        conversion.for('dataDowncast').elementToElement({
            model: 'simpleBoxDescription', view: {
                name: 'div', classes: 'simple-box-description'
            }
        });
        conversion.for('editingDowncast').elementToElement({
            model: 'simpleBoxDescription', view: (modelElement, {writer: viewWriter}) => {
                // Note: You use a more specialized createEditableElement() method here.
                const div = viewWriter.createEditableElement('div', {class: 'simple-box-description'});

                return toWidgetEditable(div, viewWriter);
            }
        });
    }
}