import {Plugin} from "@ckeditor/ckeditor5-core";
import AbbreviationCommand from "./AbbreviationCommand";

export default class AbbreviationEditing extends Plugin {
    init() {
        console.log('AbbreviationEditing#init got called.');
        this._defineSchema();
        this._defineConverters();

        this.editor.commands.add('addAbbreviation', new AbbreviationCommand(this.editor));
    }

    _defineSchema() {
        const schema = this.editor.model.schema;
        schema.extend('$text', {
            allowAttributes: ['abbreviation']
        });
    }

    _defineConverters() {
        const conversion = this.editor.conversion;
        conversion.for('downcast').attributeToElement({
            model: 'abbreviation',
            view: (modelAttributeValue, conversionApi) => {
                const {writer} = conversionApi;
                return writer.createAttributeElement('abbr', {title: modelAttributeValue});
            }
        });

        conversion.for('upcast').elementToAttribute({
            view: {
                name: 'abbr',
                attributes: ['title']
            },
            model: {
                key: 'abbreviation',
                value: viewElement => {
                    return viewElement.getAttribute('title');
                }
            }
        });
    }
}