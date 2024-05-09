import Essentials from '@ckeditor/ckeditor5-essentials/src/essentials';
import Alignment from '@ckeditor/ckeditor5-alignment/src/alignment';
import FontSize from '@ckeditor/ckeditor5-font/src/fontsize';
import FontFamily from '@ckeditor/ckeditor5-font/src/fontfamily';
import FontColor from '@ckeditor/ckeditor5-font/src/fontcolor';
import FontBackgroundColor from '@ckeditor/ckeditor5-font/src/fontbackgroundcolor';
import UploadAdapter from '@ckeditor/ckeditor5-adapter-ckfinder/src/uploadadapter';
import Autoformat from '@ckeditor/ckeditor5-autoformat/src/autoformat';
import Bold from '@ckeditor/ckeditor5-basic-styles/src/bold';
import Italic from '@ckeditor/ckeditor5-basic-styles/src/italic';
import Strikethrough from '@ckeditor/ckeditor5-basic-styles/src/strikethrough';
import Underline from '@ckeditor/ckeditor5-basic-styles/src/underline';
import BlockQuote from '@ckeditor/ckeditor5-block-quote/src/blockquote';
import CKBox from '@ckeditor/ckeditor5-ckbox/src/ckbox';
import CKFinder from '@ckeditor/ckeditor5-ckfinder/src/ckfinder';
import Image from '@ckeditor/ckeditor5-image/src/image';
import ImageUpload from '@ckeditor/ckeditor5-image/src/imageupload';
import Indent from '@ckeditor/ckeditor5-indent/src/indent';
import IndentBlock from '@ckeditor/ckeditor5-indent/src/indentblock';
import Link from '@ckeditor/ckeditor5-link/src/link';
import List from '@ckeditor/ckeditor5-list/src/list';
import ListProperties from '@ckeditor/ckeditor5-list/src/listproperties';
import MediaEmbed from '@ckeditor/ckeditor5-media-embed/src/mediaembed';
import Paragraph from '@ckeditor/ckeditor5-paragraph/src/paragraph';
import PasteFromOffice from '@ckeditor/ckeditor5-paste-from-office/src/pastefromoffice';
import CloudServices from '@ckeditor/ckeditor5-cloud-services/src/cloudservices';
import PictureEditing from '@ckeditor/ckeditor5-image/src/pictureediting';
import Table from '@ckeditor/ckeditor5-table/src/table';
import TableToolbar from '@ckeditor/ckeditor5-table/src/tabletoolbar';
import TextTransformation from '@ckeditor/ckeditor5-typing/src/texttransformation';
import JSONData from "./plugins/jsondata/JSONData";
import JSONDataProcessor from "./plugins/jsondata/JSONDataProcessor";
import "../theme/editable.css";
import IDPlugin from "./plugins/id/IDPlugin";
import ElsaHeading from "./plugins/heading/elsaHeading";
import {MultiRootEditor} from "./multiroots/MultiRootEditor";
import {ChangeUtils} from "./utils/ChangeUtils";
import Shape from "./plugins/shape/shape";
import ShapeResize from "./plugins/shape/shapeResize";
import CustomizedDragDrop from "./plugins/dragdrop/customizedDragDrop";
import CustomizedUndo from "./plugins/undo/customizedUndo";
import PositionUtils from "./utils/PositionUtils";
import {Range} from "@ckeditor/ckeditor5-engine";
import { Base64UploadAdapter } from 'ckeditor5/src/upload';
import {
    ImageCaption,
    ImageResize,
    ImageResizeButtons,
    ImageResizeEditing,
    ImageResizeHandles,
    ImageStyle,
    ImageToolbar,
} from "@ckeditor/ckeditor5-image";
import ImageInline from "@ckeditor/ckeditor5-image/src/imageinline";
import ImageBlock from "@ckeditor/ckeditor5-image/src/imageblock";

/**
 * Elsa编辑器.
 *
 * @author z00559346 张越.
 */
export default class ElsaEditor extends MultiRootEditor {
    /**
     * 记录注册的选区变化的监听器.
     *
     * @type {{}}
     * @private
     */
    _selectionCallback = null;

    constructor(sourceElementOrData, config = {}, generateId) {
        super(sourceElementOrData, config);
        this.data.processor = new JSONDataProcessor(this.data);
        this.generateId = generateId;
    }

    /**
     * 渲染toolbar.
     *
     * @param toolbarDom toolbar对应的dom元素.
     */
    renderToolbar(toolbarDom) {
        toolbarDom && toolbarDom.appendChild(this.ui.view.toolbar.element);
    }

    /**
     * 添加选区变化监听器.
     *
     * @param callback 选区变化时的回调函数.
     */
    addSelectionListener(callback) {
        if (this._selectionCallback) {
            this.removeSelectionListener();
        }
        this._selectionCallback = () => {
            callback(this.model.document.selection);
        }
        this.model.document.selection.on("change", this._selectionCallback);
    }

    /**
     * 删除选区变化监听器.
     */
    removeSelectionListener() {
        this.model.document.selection.off("change", this._selectionCallback);
    }

    /**
     * 重写setData方法.
     * 当调用setData时，需要清除对一个的root的id数据.
     *
     * @param data 数据.
     */
    setData(data) {
        const idPlugin = this.plugins.get("IDPlugin");
        Object.keys(data).forEach(rootName => idPlugin.clearIds(rootName));
        super.setData(data);
    }

    /**
     * 获取数据.重写getData方法，复杂场景存在获取数据时，root已不存在的问题.
     *
     * @param options 参数.
     * @return {string|*} 数据.
     * @override
     */
    getData(options) {
        if (!options.rootName) {
            throw new Error("MultiRootEditor#getData: rootName is required");
        }
        const root = this.model.document.getRoot(options.rootName);
        return root ? super.getData(options) : "";
    }

    /**
     * 将外部坐标转换为编辑器选区。
     *
     * @param position 坐标数据.
     * @return {*} 选区。
     */
    positionToSelection(position) {
        return this.model.createSelection(PositionUtils.toModelRange(position.x, position.y, this));
    }

    /**
     * 克隆一个选区.
     *
     * @param selection 待克隆的选区对象.
     * @return {*} 克隆后的选区对象.
     */
    cloneSelection(selection) {
        const ranges = Array.from(selection.getRanges());
        const clonedRanges = ranges.map(r => Range.fromJSON(r.toJSON(), this.model.document));
        return this.model.createSelection(clonedRanges);
    }

    _operationRootNameGetters = {
        "split": (operation) => operation.splitPosition.root.rootName,
        "addAttribute": (operation) => operation.range.root.rootName,
        "removeAttribute": (operation) => operation.range.root.rootName,
        "changeAttribute": (operation) => operation.range.root.rootName,
        "detach": (operation) => operation.sourcePosition.root.rootName,
        "insert": (operation) => operation.position.root.rootName,
        "marker": (operation) => {
            return operation.oldRange ? operation.oldRange.root.rootName : operation.newRange.root.rootName;
        },
        "merge": (operation) => {
            const sourceRootName = operation.sourcePosition.root.rootName
            if (sourceRootName !== "$graveyard") {
                return sourceRootName;
            }
            return operation.targetPosition.root.rootName;
        },
        "remove": (operation) => operation.sourcePosition.root.rootName,
        "reinsert": (operation) => operation.targetPosition.root.rootName,
        "move": (operation) => operation.sourcePosition.root.rootName,
        "rename": (operation) => operation.position.root.rootName,

    }

    /**
     * 通过operation获取rootName.
     *
     * @param operation 操作对象.
     * @return {*} rootName.
     */
    getRootNameByOperation(operation) {
        return this._operationRootNameGetters[operation.type](operation);
    }

    /**
     * 创建编辑器对象.
     *
     * @param data 数据.
     * @param config 配置信息.
     * @return {Promise<unknown>}
     */
    static create(data, config = {}) {
        return new Promise(resolve => {
            const generateId = config.generateId;
            delete config.generateId;
            const editor = new this(data, config, generateId);
            resolve(editor.initPlugins()
                .then(() => editor.ui.init())
                .then(() => editor.data.init(editor.config.get('initialData')))
                .then(() => editor.fire('ready'))
                .then(() => this._observeDataChange(editor))
                .then(() => this._initEvents(editor))
                .then(() => editor));
        });
    }

    static _initEvents(editor) {
        /* ------------------------- 触发不同root的拷贝剪切粘贴事件. ------------------------- */
        const getAnchorElement = () => {
            const blocks = Array.from(editor.model.document.selection.getSelectedBlocks());
            if (blocks.length === 0) {
                return null;
            }
            const lastBlock = blocks[blocks.length - 1];
            return editor.data.stringify({getChildren: () => [lastBlock]})[0];
        };

        const copyOrCut = (type, eventInfo, data) => {
            const rootName = data.document.selection.getFirstRange().start.root.rootName;
            let anchorElement = getAnchorElement();
            editor.fire(rootName + ":" + type, {anchorElement, event: data.domEvent});
        }

        editor.editing.view.document.on("copy", (eventInfo, data) => {
            copyOrCut("copy", eventInfo, data);
        });

        editor.editing.view.document.on("cut", (eventInfo, data) => {
            copyOrCut("cut", eventInfo, data);
        });

        editor.editing.view.document.on("paste", (eventInfo, data) => {
            const rootName = editor.model.document.selection.getFirstRange().start.root.rootName;
            const anchorElement = getAnchorElement();
            editor.fire(rootName + ":paste", {anchorElement, event: data.domEvent});
        }, {priority: 'lowest'});

        /* ------------------- 触发selection:change:blocks事件，用于处理需要关注选中的元素的业务场景. ------------------- */
        editor.model.document.selection.on("change", () => {
            const selection = editor.model.document.selection;
            const rootName = selection.getFirstRange().start.root.rootName;
            const blocks = Array.from(selection.getSelectedBlocks());
            const jsonBlocks = editor.data.stringify({getChildren: () => blocks});
            editor.fire(rootName + ":selection:change", {selectedBlocks: jsonBlocks});
        });

        /* ------------------- 批量删除事件，这里必须是highest，否则会先触发选区变化. ------------------- */
        editor.model.on("deleteContent", (eventInfo, data) => {
            const selection = data[0];
            const blocks = Array.from(selection.getSelectedBlocks());
            if (blocks.length > 1) {
                const rootName = selection.getFirstRange().start.root.rootName;
                const jsonBlocks = editor.data.stringify({getChildren: () => blocks});
                editor.fire(rootName + ":deleteBatch", {selectedBlocks: jsonBlocks});
            }
        }, {priority: 'highest'});
    }

    /**
     * 在编辑器初始化之后，监听编辑器数据变化.
     *
     * @param editor 编辑器对象.
     * @private
     */
    static _observeDataChange(editor) {
        editor.model.document.on("change:data", (eventInfo, batch) => {
            const changes = editor.model.document.differ.getChanges();
            if (changes.length > 0) {
                const rootMap = new Map();
                changes.forEach(c => {
                    const rootName = ChangeUtils.getRootName(c);
                    let rootChanges = rootMap.get(rootName);
                    if (!rootChanges) {
                        rootChanges = [];
                        rootMap.set(rootName, rootChanges);
                    }
                    rootChanges.push(c);
                });

                rootMap.forEach((value, key) => {
                    editor.fire("root:change:" + key, {changes, isUndo: batch.isUndo, isSetManually: batch.isSetManually});
                });
            }
        });
    }
}

ElsaEditor.builtinPlugins = [Essentials,
    Alignment,
    FontSize,
    FontFamily,
    FontColor,
    FontBackgroundColor,
    UploadAdapter,
    Autoformat,
    Bold,
    Italic,
    Strikethrough,
    Underline,
    BlockQuote,
    CKBox,
    CKFinder,
    CloudServices,
    // Heading,
    ElsaHeading,
    Image,
    // ImageCaption,
    ImageResizeEditing,
    ImageResizeButtons,
    // ImageResizeHandles,
    ImageStyle,
    ImageToolbar,
    ImageUpload,
    Indent,
    IndentBlock,
    Link,
    List,
    ListProperties,
    MediaEmbed,
    Paragraph,
    PasteFromOffice,
    PictureEditing,
    Table,
    TableToolbar,
    TextTransformation,
    JSONData,
    IDPlugin,
    Shape,
    ShapeResize,
    CustomizedDragDrop,
    CustomizedUndo,
    Base64UploadAdapter,
    ImageBlock,
    ImageInline,
    ImageCaption,
    // Title,
    // FixedInput,
    // Timestamp,
    // Abbreviation
    // , SimpleBox
];

// Editor configuration.
ElsaEditor.defaultConfig = {
    toolbar: {
        items: ['heading',
            '|',
            'fontfamily',
            'fontsize',
            'fontColor',
            'fontBackgroundColor',
            '|',
            'bold',
            'italic',
            'underline',
            'strikethrough',
            '|',
            'alignment',
            '|',
            'numberedList',
            'bulletedList',
            '|',
            'outdent',
            'indent',
            '|',
            'link',
            'blockquote',
            '|',
            'undo',
            'redo',
            '|',
            'insertTable',
            'uploadImage',
            // 'timestamp',
            // 'abbreviation'
        ]
    },
    list: {
        properties: {
            styles: true, startIndex: true, reversed: true
        }
    },
    fontFamily: {
        options: ["Microsoft YaHei", "arial", "SimSun"],
        supportAllValues: true,
    },
    fontSize: {
        options: ["12px", "18px", "28px", "40px", "44px", "48px"],
        supportAllValues: true
    }, // This value must be kept in sync with the language defined in webpack.config.js.
    language: 'zh-cn',
    // table: {
    //     contentToolbar: [ 'tableColumn', 'tableRow', 'mergeTableCells' ]
    // }
    image: {
        toolbar: ['imageStyle:block','imageStyle:alignLeft','imageStyle:alignRight','imageStyle:alignBlockLeft','imageStyle:alignBlockRight','resizeImage'],
        resizeOptions: [
            {
                name: 'resizeImage:original',
                value: null,
                label: 'Original'
            },
            {
                name: 'resizeImage:20',
                value: '20',
                label: '20%'
            },
            {
                name: 'resizeImage:40',
                value: '40',
                label: '40%'
            },
            {
                name: 'resizeImage:60',
                value: '60',
                label: '60%'
            }
        ],
    }

};