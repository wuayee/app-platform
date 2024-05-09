import {EditorUIView, InlineEditableUIView, Template, ToolbarView} from "@ckeditor/ckeditor5-ui";

/**
 * 多根节点的编辑器的 UI view层组件.提供inline的可编辑区域.
 *
 * @author z00559346 张越.
 */
export class MultiRootEditorUIView extends EditorUIView {
    /**
     * Creates an instance of the multi-root editor UI view.
     *
     * @param locale The locale instance.
     * @param editingView The editing view instance this view is related to.
     * @param editableElements The list of editable elements, containing name and html element
     * for each editable.
     */
    constructor(locale, editingView, editableElements) {
        super(locale);

        /**
         * The main toolbar of the multi-root editor UI.
         *
         * @readonly
         * @member {ToolbarView}
         */
        this.toolbar = new ToolbarView(locale);

        /**
         * The editables of the multi-root editor UI.
         *
         * @readonly
         * @member {Array.<InlineEditableUIView>}
         */
        this.editables = [];

        // Create InlineEditableUIView instance for each editable.
        for (const editableName of Object.keys(editableElements)) {
            const editable = new InlineEditableUIView(locale, editingView, editableElements[editableName].dom);
            editable.name = editableName;
            this.editables.push(editable);
        }

        // This toolbar may be placed anywhere in the page so things like font size need to be reset in it.
        // Because of the above, make sure the toolbar supports rounded corners.
        // Also, make sure the toolbar has the proper dir attribute because its ancestor may not have one
        // and some toolbar item styles depend on this attribute.
        Template.extend(this.toolbar.template, {
            attributes: {
                class: ['ck-reset_all', 'ck-rounded-corners'], dir: locale.uiLanguageDirection
            }
        });
    }

    /**
     * @inheritDoc
     */
    render() {
        super.render();
        this.registerChild(this.editables);
        this.registerChild([this.toolbar]);
    }
}