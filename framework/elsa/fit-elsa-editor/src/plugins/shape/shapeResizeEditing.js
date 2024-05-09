import {Plugin} from "@ckeditor/ckeditor5-core";
import ShapeResizeCommand from "./shapeResizeCommand";

/**
 * 调整图形大小编辑组件.
 *
 * @author z00559346 张越.
 */
export default class ShapeResizeEditing extends Plugin {
    /**
     * @inheritDoc
     * @override
     */
    init() {
        this._registerConverters();
        this.editor.commands.add('resizeShape', new ShapeResizeCommand(this.editor));
    }

    _registerConverters() {
        const editor = this.editor;

        editor.conversion.for('downcast').add(dispatcher => {
            // 当shape的width变化时，设置viewElement的属性及style.
            dispatcher.on(`attribute:width:shape`, (evt, data, conversionApi) => {
                if (!conversionApi.consumable.consume(data.item, evt.name)) {
                    return;
                }
                const viewWriter = conversionApi.writer;
                const shape = conversionApi.mapper.toViewElement(data.item);
                if (data.attributeNewValue !== null) {
                    viewWriter.setStyle('width', data.attributeNewValue, shape);
                    viewWriter.setAttribute('width', data.attributeNewValue, shape);
                } else {
                    viewWriter.removeStyle('width', shape);
                }
            });

            // 当shape的height变化时，设置viewElement的属性及style.
            dispatcher.on(`attribute:height:shape`, (evt, data, conversionApi) => {
                if (!conversionApi.consumable.consume(data.item, evt.name)) {
                    return;
                }
                const viewWriter = conversionApi.writer;
                const shape = conversionApi.mapper.toViewElement(data.item);
                if (data.attributeNewValue !== null) {
                    viewWriter.setStyle('height', data.attributeNewValue, shape);
                    viewWriter.setAttribute('height', data.attributeNewValue, shape);
                } else {
                    viewWriter.removeStyle('height', shape);
                }
            });
        });
    }
}