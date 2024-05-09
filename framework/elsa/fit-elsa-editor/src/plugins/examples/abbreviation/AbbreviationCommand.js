import {Command} from "@ckeditor/ckeditor5-core";
import {findAttributeRange} from "@ckeditor/ckeditor5-typing";
import getRangeText from "./utils";
import {toMap} from "@ckeditor/ckeditor5-utils";

export default class AbbreviationCommand extends Command {
    refresh() {
        const model = this.editor.model;
        const selection = model.document.selection;

        this.isEnabled = model.schema.checkAttributeInSelection(
            selection, 'abbreviation'
        );

        const firstRange = selection.getFirstRange();

        if (firstRange.isCollapsed) {
            if (selection.hasAttribute('abbreviation')) {
                const attributeValue = selection.getAttribute('abbreviation');

                const abbreviationRange = findAttributeRange(selection.getFirstPosition(), 'abbreviation', attributeValue, model);
                this.value = {
                    abbr: getRangeText(abbreviationRange), title: attributeValue, range: abbreviationRange
                };
            } else {
                this.value = null;
            }
        } else {
            if (selection.hasAttribute('abbreviation')) {
                const attributeValue = selection.getAttribute('abbreviation');
                const abbreviationRange = findAttributeRange(selection.getFirstPosition(), 'abbreviation', attributeValue, model);

                if (abbreviationRange.containsRange(firstRange, true)) {
                    this.value = {
                        abbr: getRangeText(firstRange), title: attributeValue, range: firstRange
                    };
                } else {
                    this.value = null;
                }
            } else {
                this.value = null;
            }
        }
    }

    execute({title, abbr}) {
        const model = this.editor.model;
        const selection = model.document.selection;

        model.change(writer => {
            if (selection.isCollapsed) {
                if (this.value) {
                    const {end: positionAfter} = model.insertContent(writer.createText(abbr, {abbreviation: title}), this.value.range);
                    writer.setSelection(positionAfter);
                } else if (abbr !== "") {
                    const firstPosition = selection.getFirstPosition();
                    const attributes = toMap(selection.getAttributes());
                    attributes.set('abbreviation', title);
                    const {end: positionAfter} = writer.insertContent(writer.createText(abbr, attributes), firstPosition);
                    writer.selection(positionAfter);
                }
                writer.removeSelectionAttribute('abbreviation');
            } else {
                const ranges = model.schema.getValidRanges(selection.getRanges(), 'abbreviation');
                for (const range of ranges) {
                    writer.setAttribute('abbreviation', title, range);
                }
            }
        });
    }
}