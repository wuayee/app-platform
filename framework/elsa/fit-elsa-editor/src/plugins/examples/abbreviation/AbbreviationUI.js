import {Plugin} from "@ckeditor/ckeditor5-core";
import {ButtonView, clickOutsideHandler, ContextualBalloon} from "@ckeditor/ckeditor5-ui";
import FormView from "./AbbreviationView";
import getRangeText from "./utils";

export default class AbbreviationUI extends Plugin {
    static get requires() {
        return [ContextualBalloon];
    }

    init() {
        console.log('AbbreviationUI#init() got called.');
        const editor = this.editor;

        this._ballon = this.editor.plugins.get(ContextualBalloon);
        this.formView = this._createFormView();

        editor.ui.componentFactory.add('abbreviation', () => {
            const button = new ButtonView();
            button.label = 'Abbreviation';
            button.tooltip = true;
            button.withText = true;

            this.listenTo(button, 'execute', () => {
                this._showUI();
            });

            return button;
        });
    }

    _createFormView() {
        const editor = this.editor;
        const formView = new FormView(editor.locale);
        this.listenTo(formView, 'submit', () => {
            const value = {
                abbr: formView.abbrInputView.fieldView.element.value,
                title: formView.titleInputView.fieldView.element.value
            };

            editor.execute('addAbbreviation', value);
            this._hideUI();
        });

        this.listenTo(formView, 'cancel', () => {
            this._hideUI();
        });

        clickOutsideHandler({
            emitter: formView,
            activator: () => this._ballon.visibleView === formView,
            contextElements: [this._ballon.view.element],
            callback: () => this._hideUI()
        });

        formView.keystrokes.set('Esc', (data, cancel) => {
            this._hideUI();
            cancel();
        });

        return formView;
    }

    _getBalloonPositionData() {
        const view = this.editor.editing.view;
        const viewDocument = view.document;
        let target;
        target = () => view.domConverter.viewRangeToDom(viewDocument.selection.getFirstRange());
        return {target};
    }

    _showUI() {
        const selection = this.editor.model.document.selection;

        const commandValue = this.editor.commands.get('addAbbreviation').value;

        this._ballon.add({
            view: this.formView, position: this._getBalloonPositionData()
        });

        this.formView.abbrInputView.isEnabled = selection.getFirstRange().isCollapsed;

        if (commandValue) {
            this.formView.abbrInputView.fieldView.value = commandValue.abbr;
            this.formView.titleInputView.fieldView.value = commandValue.title;
        } else {
            const selectedText = getRangeText(selection.getFirstRange());
            this.formView.abbrInputView.fieldView.value = selectedText;
            this.formView.titleInputView.fieldView.value = '';
        }

        this.formView.focus();
    }

    _hideUI() {
        this.formView.abbrInputView.fieldView.value = '';
        this.formView.titleInputView.fieldView.value = '';
        this.formView.element.reset();

        this._ballon.remove(this.formView);
        this.editor.editing.view.focus();
    }
}