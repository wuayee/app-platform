import {ALIGN, DOCK_MODE, DOCUMENT_FORMAT} from '../../common/const.js';
import {graph} from '../../core/graph.js';
import {page} from '../../core/page.js';
import {container} from '../../core/container.js';
import {docDrawer} from './docDrawer.js';

let documentGraph = (div, title) => {
    let self = graph(div, title);
    self.createDocument = title => {
        return self.addPage(title);
    }
    self.type = "document";
    self.pageType = "docPage";
    self.setting.borderColor = "gray";
    return self;
}
/**
 * 文档承载页
 * 页里可以有多个document（听起来有点反，但为了通用性）
 * 辉子 2021-6
 */
let docPage = (div, graph, name, id) => {
    let self = page(div, graph, name, id);
    self.pageType = "docPage";
    self.vAlign = ALIGN.TOP;
    self.itemSpace = 15;
    self.minDocHeight = 1280;
    self.docWidth = 960;

    self.getContent = () => {
        //todo:get content tree from slate
    }
    // self.shapeCreated = s => {
    //     if (s.type !== "document") return;
    //     s.pageId = self.getDocuments().max(s => s.pageId) + 1;
    //     s.deleteable = s.moveable = false;
    //     s.heightChanged = () => self.invalidate();
    //     s.manageConnectors = () => s.connectors = [];
    // };

    const invalidate = self.invalidate;
    self.invalidate = () => {
        const docs = self.getDocuments();
        for (let i = 0; i < docs.length; i++) {
            docs[i].x = (self.width - docs[i].width) / 2;
            docs[i].y = (i > 0 ? (docs[i - 1].y + docs[i - 1].height) : 0) + self.itemSpace;
            docs[i].width = self.docWidth;
            docs[i].height = undefined;
            docs[i].height = self.minDocHeight
            docs[i].format = DOCUMENT_FORMAT.DOCUMENT;
        }
        self.height = docs[docs.length - 1].y + docs[docs.length - 1].drawer.parent.clientHeight + self.itemSpace;
        self.adaptLayout();
        invalidate.call(self);
    };

    self.initialize = () => {
        let doc = self.createNew("document", 10, 10);
        doc.format = DOCUMENT_FORMAT.DOCUMENT;
        doc.container = self.id;
    };

    self.getDocuments = () => self.getShapes().filter(s => s.isType('document')).orderBy(s => s.pageId);

    // self.serializedFields.batchAdd("docWidth", "minDocHeight");

    return self;
};

const slateRender = (div, id) => {
    //todo
    const result = {
        id: id, nodes: [{id: '111', x: 10, y: 20, width: 940, height: 300, format: 'paragraph', nodes: []}, {
            id: '222',
            x: 10,
            y: 340,
            width: 940,
            height: 400,
            format: 'paragraph',
            nodes: [{id: '333', x: 10, y: 10, width: 920, height: 180, format: 'body', nodes: []}]
        }]
    };
    return result;
}

/**
 * 文档
 * 辉子 2021-6
 */
let document = (id, x, y, width, height, parent) => {
    const MINI_HEIGHT = {document: 500, paragraph: 100, body: 100};
    let self = container(id, x, y, width, height, parent, docDrawer);
    self.type = "document";
    self.namespace = "document";
    self.editable = false;
    self.paragraphStyles = [];//设置doc里标题的统一风格
    self.bodyStyle = {};//设置doc里正文的统一风格
    self.text = "Elsa Document";
    self.headColor = "transparent";
    self.fontSize = 28;
    self.pageId = 0;
    self.ifMaskItems = false;
    self.format = DOCUMENT_FORMAT.BODY;
    self.height = MINI_HEIGHT[self.format];

    self.parseContent = () => {
        if (self.format !== DOCUMENT_FORMAT.DOCUMENT) {
            return;
        }
        const doc = slateRender(self.drawer.parent, self.id);
        const loadNode = (parent, nodes) => {
            const shapes = parent.getShapes();
            nodes.forEach(n => {
                let node = shapes.find(s => s.id === n.id);
                if (!node) {
                    node = self.page.createShape("document", parent.x + 1, parent.y + 1,n.id);
                    node.container = parent.id;
                    // node.id = n.id;
                }
                ;node.x = n.x + parent.x;
                node.y = n.y + parent.y;
                node.width = n.width;
                node.height = n.height;
                node.format = n.format;
                loadNode(node, n.nodes);
            })
        }
        loadNode(self, doc.nodes);
    }
    const invalidate = self.invalidate;
    self.invalidate = () => {
        self.parseContent();
        invalidate.call(self);
    };

    // let parentResize = self.drawer.parentResize;
    // self.drawer.parentResize = (width, height) => {
    //     parentResize.call(self.drawer, width, height);
    //     self.drawer.parent.style.height = "";
    //     self.drawer.parent.style.minHeight = self.minDocHeight / self.scaleY + "px";
    //     if (self.drawer.parent.clientHeight !== self.height) self.height = self.drawer.parent.clientHeight * self.scaleY;
    // };

    // self.serializedFields.batchAdd("paragraphStyles", "bodyStyle", "pageId", "docWidth");
    self.addDetection(["format"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        //self.borderWidth = value === DOCUMENT_FORMAT.DOCUMENT ? 1 : 0;
        self.shadow = value === DOCUMENT_FORMAT.DOCUMENT;
        const height = MINI_HEIGHT[self.format];
        self.height = height > self.height ? height : self.height;

    });
    self.addDetection(["height"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        self.heightChanged && self.heightChanged();
    });
    // self.initialize = () => {
    //     self.paragraphStyles = [];
    //     //set paragraph styles
    //     for (let i = 0; i < 3; i++) {
    //         //第一级
    //         self.paragraphStyles.push({
    //             fontSize: 18,
    //             fontFace: "arial",
    //             fontColor: "dimgray",
    //             fontStyle: FONT_STYLE.NORMAL,
    //             fontWeight: FONT_WEIGHT.BOLD,
    //             align: ALIGN.LEFT,
    //             indent: 10,
    //             lineHeight: 2,
    //             paragraphSpace: 5
    //         });
    //         //第二级
    //         self.paragraphStyles.push({
    //             fontSize: 16,
    //             fontFace: "arial",
    //             fontColor: "dimgray",
    //             fontStyle: FONT_STYLE.NORMAL,
    //             fontWeight: FONT_WEIGHT.BOLD,
    //             align: ALIGN.LEFT,
    //             indent: 10,
    //             lineHeight: 2,
    //             paragraphSpace: 5
    //         });
    //         //第三级
    //         self.paragraphStyles.push({
    //             fontSize: 14,
    //             fontFace: "arial",
    //             fontColor: "dimgray",
    //             fontStyle: FONT_STYLE.NORMAL,
    //             fontWeight: FONT_WEIGHT.BOLD,
    //             align: ALIGN.LEFT,
    //             indent: 20,
    //             lineHeight: 2,
    //             paragraphSpace: 5
    //         });

    //     }
    //     // set body style
    //     self.bodyStyle.fontSize = 12;
    //     self.bodyStyle.fontFace = "arial";
    //     self.bodyStyle.fontColor = "dimgray";
    //     self.bodyStyle.fontStyle = FONT_STYLE.NORMAL;
    //     self.bodyStyle.fontWeight = FONT_WEIGHT.LIGHTER;
    //     self.bodyStyle.lineHeight = 2;
    //     self.bodyStyle.indent = 0;

    // }

    return self;
};

/**
 * 文档中的段落
 * 可以是标题，可以是正文
 * 辉子 2021-6
 */
let docSection = (id, x, y, width, height, parent) => {

    let self = container(id, x, y, width, height, parent, docDrawer);
    self.type = "docSection";
    self.namespace = "document";
    self.format = DOCUMENT_FORMAT.PARAGRAPH;

    self.hideText = false;
    self.hAlign = ALIGN.LEFT;
    self.autoFit = true;
    self.autoWidth = false;
    self.itemPad = [3, 3, 3, 3];
    self.borderWidth = 0;
    self.dashWidth = 6;
    self.itemSpace = 0;
    self.pad = 5;
    self.text = "";

    let paragraphStyle = () => {
        let level = 0;
        let container = self.getContainer();
        while (container.type !== "docFrame" && container.page !== contaienr) {
            level++;
            container = container.getContainer();
        }
        (!container.paragraphStyles) && (container.paragraphStyle = {
            fontSize: self.get("fontSize"),
            fontFace: self.get("fontFace"),
            fontColor: self.get("fontColor"),
            fontStyle: self.get("fontStyle"),
            fontWeight: self.get("fontWeight"),
            lineHeight: self.get("fontHeight"),
            indent: 0
        });
        return container.paragraphStyles[level];
    };

    let bodyStyle = () => {
        let container = self.getDocument();
        if (container === undefined) {
            return;
        }
        return container.bodyStyle;
    };

    self.getStyle = () => eval("(" + self.format + "Style())");

    let isPointInEdge = (x, y) => {
        const SPACE = self.pad;
        if (x - self.x < SPACE) {
            return true;
        }
        if (self.x + self.width - x < SPACE) {
            return true;
        }
        if (y - self.y < SPACE) {
            return true;
        }
        if (self.y + self.height - y < SPACE) {
            return true;
        }

        return false;
    }
    self.caretPosition = 0;
    let onMouseMove = self.onMouseMove;
    self.onMouseMove = (position) => {
        if (isPointInEdge(position.x, position.y)) {
            //self.endEdit();
            onMouseMove.call(self, position);
        } else {
            let edit = self.beginEdit();
            edit.setCaretPosition(self.caretPosition);
        }
    };

    let endEidt = self.endEdit;
    self.endEdit = () => {
        let edit = document.getElementById(EDITOR_NAME);
        if (edit !== undefined && edit !== null) {
            self.caretPosition = edit.getCaretPosition();
        }
        endEidt.call(self);
    };

    self.getDocument = () => {
        let container = self.getContainer();
        while (container.type !== "docFrame") {
            container = container.getContainer();
            if (container.id == self.page.id) {
                return undefined;
            }
        }
        return container;
    };

    let findNextSection = node => {
        //先找子孙
        let children = node.getShapes();
        if (children.length > 0) {
            return children[0];
        }

        //再找兄弟
        let parent = node.getContainer();
        let brothers = parent.getShapes();
        let idx = brothers.indexOf(node);
        if (idx < brothers.length - 1) {
            return brothers[idx + 1];
        }

        //再找祖先
        let findAncestor = node => {
            if (node.isType('docFrame') || node.namespace !== "document") {
                return;
            }
            let parent = node.getContainer();
            let brothers = parent.getShapes();
            let idx = brothers.indexOf(node);
            if (idx < brothers.length - 1) {
                return brothers[idx + 1];
            } else {
                findAncestor(parent);
            }

        };
        return findAncestor(parent);
    };
    let findPreviousSection = node => {
        //先找兄弟的最后一个孩子
        let parent = node.getContainer();
        let brothers = parent.getShapes();
        let idx = brothers.indexOf(node);
        if (idx > 0) {
            let findLastChild = node => {
                let children = node.getShapes();
                if (children.length === 0) {
                    return node;
                } else {
                    return findLastChild(children[children.length - 1]);
                }
            };
            return findLastChild(brothers[idx - 1]);
        }
        ;

        //再找祖先
        if (parent.isType('docFrame') || parent.namespace !== "document") {
            return;
        }
        return parent;
    };

    self.editingBackspacePressed = (edit, e) => {
        let previous = findPreviousSection(self);
        if (previous === undefined) {
            return;
        }
        previous.text += self.text;
        self.remove();
        self.endEdit();
        let pos = self.text.length;
        let edit1 = previous.beginEdit();
        edit1.setCaretPosition(pos);
        previous.getDocument().invalidate();
    };

    self.editingDeletePressed = (edit, e) => {
        let next = findNextSection(self);
        if (next === undefined) {
            return;
        }
        let pos = self.text.length;
        self.text += next.text;
        edit.innerHTML = self.text;
        edit.setCaretPosition(pos);
        next.remove();
        self.getDocument().invalidate();

    };

    self.editingLeftPressed = (edit, e) => {
        let prvious = findPreviousSection(self);
        if (prvious === undefined) {
            return;
        }
        self.endEdit();
        prvious.beginEdit();
    };

    self.editingRightPressed = (edit, e) => {
        let next = findNextSection(self);
        if (next === undefined) {
            return;
        }
        self.endEdit();
        let newEdit = next.beginEdit();
        newEdit && newEdit.setCaretPosition(0);
    };

    self.editingEnterPressed = (edit, e) => {//todo:通过ctrl enter作为新增一个  body
        if (!e.ctrlKey) {
            return;
        }
        self.endEdit();
        let body = self.page.createNew("docBody", 0, 0);
        body.container = self.container;
        self.page.moveIndexAfter(body, self.getIndex());
        self.getDocument().invalidate();
        body.beginEdit();
        return true;
    };

    self.editing = (edit, e) => {
        //backspace, to join the previous line if back the first charactor
        if (e.keyCode === 8 && edit.keydownText === edit.innerText) {// && editor.innerText.trim() === ""
            self.editingBackspacePressed(edit, e);
            return;
        }
        //delete, to join next line if delete the end charactor
        if (e.keyCode === 46 && edit.keydownText === edit.innerText) {
            self.editingDeletePressed(edit, e);
            return;
        }
        //left,up
        if ((e.keyCode === 37 || e.keyCode === 38) && edit.keyDownCursorPosition === edit.keyUpCursorPosition) {
            self.editingLeftPressed(edit, e);
            return;
        }
        //right,down
        if ((e.keyCode === 39 || e.keyCode === 40) && edit.keyDownCursorPosition === edit.keyUpCursorPosition) {
            self.editingRightPressed(edit, e);
            return;
        }
        //enter to add a new section
        if (e.keyCode === 13) {
            if (self.editingEnterPressed(edit, e)) {
                return;
            }
        }
        self.text = edit.innerHTML;
        let height = self.drawer.text.clientHeight;
        self.drawer.resize();
        if (height !== self.drawer.text.clientHeight) {
            self.getDocument().invalidate();
        }
    };

    let get = self.get;
    self.get = field => {
        let style = self.getStyle();
        if (style !== undefined && style[field] !== undefined) {
            return style[field];
        } else {
            if (field === "headColor") {
                field = "backColor";
            }
            return get.call(self, field);
        }
    };

    let invalidateAlone = self.invalidateAlone;
    self.invalidateAlone = () => {
        let style = self.getStyle();
        if (style !== undefined) {
            self.itemPad[0] = style.indent;
            self.fontSize = style.fontSize;
            self.fontFace = style.fontFace;
            self.fontColor = style.fontColor;
            self.fontStyle = style.fontStyle;
            self.fontWeight = style.fontWeight;
            self.lineHeight = style.lineHeight;
        }
        if (self.format === DOCUMENT_FORMAT.BODY) {
            self.dockMode = DOCK_MODE.NONE;
            self.autoHeight = true;
        } else {
            self.dockMode = DOCK_MODE.VERTICAL;
            self.autoHeight = false;
        }

        invalidateAlone.call(self);

        // if (self.format === DOCUMENT_FORMAT.PARAGRAPH) {
        //     if (self.itemPad[2] !== self.drawer.text.clientHeight + PARAGRAPH_OFFSET) {
        //         self.itemPad[2] = self.drawer.text.clientHeight + PARAGRAPH_OFFSET;
        //         self.getDocument().invalidate(); todo
        //     }
        // }
    };

    // self.serializedFields.batchAdd("format");
    return self;
};

export {documentGraph, document, docPage, docSection};
