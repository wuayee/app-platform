import {HeadingEditing} from "@ckeditor/ckeditor5-heading";
import {priorities} from "@ckeditor/ckeditor5-utils";
import ElsaHeadingCommand from "./elsaHeadingCommand";
import {SERIAL_NO_ATTRIBUTES_HANDLERS} from "./utils/utils";

/**
 * Elsa的标题编辑类.
 *
 * @author z00559346 张越.
 */
export default class ElsaHeadingEditing extends HeadingEditing {
    static get pluginName() {
        return 'elsaHeadingEditing';
    }

    static _OPTIONS = [{model: 'paragraph', title: 'Paragraph', class: 'ck-heading_paragraph'},
        {model: 'heading1', view: 'h2', title: 'Heading 1', class: 'ck-heading_heading1'},
        {model: 'heading2', view: 'h3', title: 'Heading 2', class: 'ck-heading_heading2'},
        {model: 'heading3', view: 'h4', title: 'Heading 3', class: 'ck-heading_heading3'},
        {model: 'heading4', view: 'h5', title: 'Heading 4', class: 'ck-heading_heading4'},
        {model: 'heading5', view: 'h6', title: 'Heading 5', class: 'ck-heading_heading5'}];

    constructor(editor) {
        super(editor);
        editor.config.set('heading', {options: ElsaHeadingEditing._OPTIONS});
        this._optionMap = new Map();
        ElsaHeadingEditing._OPTIONS.forEach(op => this._optionMap.set(op.model, op));
    }

    init() {
        const editor = this.editor;
        const options = editor.config.get('heading.options');

        const modelElements = [];
        for (const option of options) {
            if (option.model !== "paragraph") {
                // 注册schema.
                editor.model.schema.register(option.model, {inheritAllFrom: '$block', allowAttributes: ["serialNo"]});
                editor.conversion.for("upcast").elementToElement(option);
                editor.conversion.for("dataDowncast").elementToElement(option);
                editor.conversion.for("editingDowncast").elementToElement({
                    model: option.model, view: (modelItem, {writer: viewWriter}) => {
                        return viewWriter.createContainerElement(option.view, {class: option.class});
                    }
                });

                /**
                 * 监听editingDowncast时的heading插入事件.
                 * 当插入新的heading时，需要为其加入序号.
                 * 例如:
                 * 1、标题1
                 * 1.1、标题1.1
                 * 1.1.1、 标题1.1.1
                 * 2、标题2
                 * ....
                 */
                editor.conversion.for("editingDowncast").add(dispatcher => {
                    dispatcher.on(`insert:${option.model}`, (evt, data, conversionApi) => {
                        const {writer, mapper} = conversionApi;
                        const viewElement = mapper.toViewElement(data.item);
                        const container = this._createContainerElement(writer, data.item, option);
                        writer.insert(writer.createPositionBefore(viewElement), container);
                        writer.move(writer.createRangeOn(viewElement), writer.createPositionAt(container, 'end'));
                    });
                }, {priority: "low"});
                modelElements.push(option.model);
            }
        }

        /**
         * 监听序号的变化事件.
         * 1、若此时viewElement的 parent 不是 headingWrapper，说明是插入的情况，不做任何处理
         * 2、否则，将之前的序号对应的rawElement删除，创建新的rawElement.
         */
        editor.conversion.for("editingDowncast").add(dispatcher => {
            dispatcher.on("attribute", (evt, data, conversionApi) => {
                if (!this._isHeading(data.item)) {
                    return;
                }

                const {writer, mapper} = conversionApi;

                // 此时item有可能是text，因此需要判断来获取text所属的heading元素
                const modelElement = data.item.name ? data.item : data.item.parent;
                let viewElement = mapper.toViewElement(modelElement);

                // 如果未在wrapper中，表明是插入的情况，不需要处理
                // 是序号相关的事件，或者光标在heading的开始位置，需要重新生成序号对象
                if (this._isInWrapper(viewElement) && (data.attributeKey === "serialNo" || data.range.start.isAtStart)) {
                    writer.remove(viewElement.previousSibling);
                    const rawElement = this._createRawElement(writer, modelElement, this._optionMap.get(modelElement.name));
                    writer.insert(writer.createPositionBefore(viewElement), rawElement);
                }
            }, {priority: "lowest"});
        }, {priority: "lowest"});

        this._addDefaultH1Conversion(editor);

        editor.commands.add('heading', new ElsaHeadingCommand(editor, modelElements));
    }

    _isHeading(item) {
        let parent = item;
        while (parent) {
            if (parent.name && parent.name.startsWith("heading")) {
                return true;
            }
            parent = parent.parent;
        }
        return false;
    }

    _isInWrapper(viewElement) {
        return viewElement.parent && viewElement.parent.getAttribute("name") === "headingWrapper";
    }

    /**
     * 创建容器元素，用于包裹标题和序号.container元素可被编辑器识别，从而不会被选中.
     *
     * @private
     */
    _createContainerElement(writer, modelElement, option) {
        const rawElement = this._createRawElement(writer, modelElement, option);
        return writer.createContainerElement('div', {name: "headingWrapper", class: "heading-container"}, rawElement);
    }

    /**
     * 创建序号元素.序号元素使用 {@link #RawElement} 自定义实现.
     *
     * @private
     */
    _createRawElement(writer, modelElement, option) {
        const manager = this._getElsaHeadingTreeManager();
        const heading = manager.getHeadingById(modelElement.root.rootName, modelElement._attrs.get("id"));

        const serialNoElement = document.createElement("span");
        serialNoElement.innerHTML = heading.getSerialNo() + "、 ";
        this._decorateSerialNo(serialNoElement, modelElement)
        return writer.createRawElement('span', {contenteditable: "false"}, (domElement) => {
            domElement.innerHTML = `<${option.view}>${serialNoElement.outerHTML}</${option.view}>`;
            domElement.style.userSelect = "none";
            domElement.style.display = "flex";
            domElement.style.alignItems = "center";
        });
    }

    _decorateSerialNo(serialNoElement, modelElement) {
        const children = Array.from(modelElement.getChildren());
        if (children.length === 0) {
            return;
        }
        const attributeMap = new Map(children[0].getAttributes());
        const attributes = Array.from(children[0].getAttributeKeys());
        attributes.forEach(attr => {
            // 当标题的第一个元素是shape时，有些属性是不需要处理的，比如id.因此这里需要进行条件保护.
            const handler = SERIAL_NO_ATTRIBUTES_HANDLERS[attr];
            handler && handler(serialNoElement, attributeMap.get(attr));
        });
    }

    _getElsaHeadingTreeManager() {
        return this.editor.plugins.get("ElsaHeadingTreeManager");
    }

    _addDefaultH1Conversion(editor) {
        editor.conversion.for('upcast').elementToElement({
            model: 'heading1', view: 'h1', converterPriority: priorities.get('low') + 1
        });
    }
}