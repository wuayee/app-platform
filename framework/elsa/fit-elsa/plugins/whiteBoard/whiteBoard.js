import {graph} from "../../core/graph.js";
import {page} from "../../core/page.js";
import {inPolygon} from "../../common/graphics.js";
import {
    ALIGN, DOCK_MODE, FONT_STYLE, FONT_WEIGHT, INFO_TYPE, PARENT_DOCK_MODE, PROGRESS_STATUS
} from "../../common/const.js";
import {brush} from "../../editor/whiteboard/brush.js";
import {pageCommandHistory} from "../../core/history";

const whiteBoard = (div, title) => {
    let self = graph(div, title);
    self.type = "whiteBoard";
    self.pageType = "whiteBoardPage";
    self.setting = whiteBoardSetting;
    self.historyStrategy = "page";

    /**
     * 白板只能用原生的编辑器进行文本处理。
     *
     * @param shape 图形对象.
     * @return {*} 编辑器对象.
     */
    self.createEditor = (shape) => {
        return brush(shape);
    };
    return self;
};

const whiteBoardSetting = {
    borderColor: "steelblue",
    backColor: "whitesmoke",
    headColor: "steelblue",
    fontColor: "steelblue",
    captionfontColor: "whitesmoke",
    fontFace: "arial",
    captionfontFace: "arial black",
    fontSize: 12,
    captionfontSize: 14,
    fontStyle: FONT_STYLE.NORMAL,
    captionfontStyle: FONT_STYLE.NORMAL,
    fontWeight: FONT_WEIGHT.BOLD,
    captionfontWeight: FONT_WEIGHT.BOLD,
    hAlign: ALIGN.MIDDLE,
    vAlign: ALIGN.TOP,
    captionhAlign: ALIGN.MIDDLE,
    lineHeight: 1.5,
    captionlineHeight: 1,
    focusMargin: 8,
    focusBorderColor: "white",
    mouseInColor: "orange",
    focusBackColor: "whitesmoke",
    borderWidth: 1,
    globalAlpha: 1,
    backAlpha: 0.15,
    cornerRadius: 0,
    dashWidth: 0,
    autoText: false,
    autoHeight: false,
    autoWidth: false,
    margin: 20,
    pad: 5,
    code: "",
    rotateDegree: 0,
    shadow: false,
    shadowData: "2px 2px 4px",
    outstanding: false,
    pDock: PARENT_DOCK_MODE.NONE,
    dockMode: DOCK_MODE.NONE,
    priority: 0,
    infoType: INFO_TYPE.NONE,
    progressStatus: PROGRESS_STATUS.NONE,
    progressPercent: 0.65,
    showedProgress: false,
    itemPad: [5, 5, 5, 5],
    enableAnimation: false,
    enableSocial: true,
    emphasized: false,
    bulletSpeed: 1,
    tag: {}//其他任何信息都可以序列化后放在这里
}

const whiteBoardPage = (div, graph, name, id) => {
    let self = page(div, graph, name, id);
    self.type = 'whiteBoardPage';
    self.groupType = 'whiteBoardGroup';
    self.freeLineType = 'whiteBoardFreeLine';
    self.serializedFields.batchAdd("backgroundGrid", "backgroundGridMargin", "backgroundGridSize");
    self.erasering = false;

    self.interactDrawer.drawSelection = () => {
    }

    self.mouseCancel = (position) => {
        if (self.currentLine) {
            self.currentLine.remove();
            self.currentLine = undefined;
        }
    }

    self.handAction = (mousedownShape) => {
        let focusedShapes = self.getFocusedShapes();
        if (focusedShapes.length > 0 && focusedShapes.find(shape => shape === mousedownShape) !== undefined) {
            return false;
        }
        if (self.eraser && (!self.erasePrecise)) {
            return true;
        }
        return self.inHandDrawing;
    };

    self.onMouseDrag = (position) => {
        let selectLine = self.selectLine;
        if (!selectLine) {
            selectLine = self.createNew('freeSelectionLine', self.mousedownx, self.mousedowny);
            selectLine.resize(self.width, self.height);
            selectLine.linewidth = 2;
            selectLine.borderColor = 'orange';
            selectLine.focusBorderColor = 'orange';
            selectLine.mouseInColor = 'orange';
            selectLine.newLine();
            self.selectLine = selectLine;
        }
        selectLine.addPoint(position.x, position.y);
    }

    function checkFreeLine(s, points) {
        const offSet = s.getOffSet();
        const x = s.x + offSet;
        const y = s.y + offSet;
        for (let line of s.lines) {
            for (let point of line) {
                if (inPolygon(points, point[0] + x, point[1] + y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 白板模式下，只有处于擦除模式并正在触摸擦除时，才显示cursor
     * @returns {boolean|*|boolean}
     */
    self.showCursor = () => {
        return self.erasering && self.touching;
    }

    self.inputModeChanged = (mode) => {
        self.erasering = mode === "eraser";
    }

    self.onMouseUp = position => {
        const selectLine = self.selectLine;
        if (!selectLine) {
            return;
        }
        const points = selectLine.currentPoints();
        points.forEach(point => {
            point[0] += selectLine.x;
            point[1] += selectLine.y;
        })
        selectLine.remove();
        self.selectLine = null;
        let selectedShapes = [];
        self.shapes.forEach(s => {
            if (s.protected) {
                return;
            }
            let x1 = s.x, y1 = s.y, x2 = x1 + s.width, y2 = y1 + s.height;
            // 目前只支持freeline的框选
            if (s.isTypeof('freeLine')) {
                if (checkFreeLine(s, points)) {
                    selectedShapes.push(s);
                }
            } else {
                if (s.freeLineSelect(points)) {
                    selectedShapes.push(s);
                }
            }
        })
        if (selectedShapes.length > 1) {
            // container作为一个整体加入
            let containerIds = new Set();
            selectedShapes.filter(shape => shape.isTypeof("container")).forEach(shape => containerIds.add(shape.id));
            selectedShapes = selectedShapes.filter(shape => !containerIds.has(shape.container));
        }
        // 构造临时group
        if (selectedShapes.length > 1) {
            let g = self.createNew("freeLineSelection", 0, 0);
            g.select();
            g.group(selectedShapes);
        } else if (selectedShapes.length === 1) {
            selectedShapes[0].select();
        }
    }
    return self;
};

export {whiteBoard, whiteBoardPage}