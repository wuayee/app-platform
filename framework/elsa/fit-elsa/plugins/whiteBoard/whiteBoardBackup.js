import {page} from "../../core/page.js";
import {
    ALIGN,
    DOCK_MODE,
    FONT_STYLE,
    FONT_WEIGHT,
    INFO_TYPE,
    PAGE_MODE,
    PARENT_DOCK_MODE,
    PROGRESS_STATUS
} from "../../common/const.js";
import {graph} from "../../core/graph.js";
import {container} from "../../core/container.js";

let whiteBoardBackup = (div, title) => {
    let self = graph(div, title, PAGE_MODE.CONFIGURATION);

    self.type = "whiteBoard";
    self.pageType = "whiteBoardPage";
    self.setting = WHITE_BOARD_SETTING;
    return self;
};

let whiteBoardPage = (div, graph, name, id) => {
    let self = page(div, graph, name, id);
    self.fontColor = "whitesmoke";
    self.hAlign = ALIGN.MIDDLE;
    self.vAlign = ALIGN.MIDDLE;
    self.backColor = "lightgray";
    self.type = "whiteBoardPage";
    self.namespace = "whiteBoard";
    self.isTemplate = false;//template will not display, it will be other page's template
    self.basePage = "";//base will be the background of this page,base is a ID
    self.coEditingSyncMe = false;
    return self;
};

let writeArea = (id, x, y, width, height, parent, drawer, initializing) => {
    let self = container(id, x, y, width, height, parent, drawer, initializing);
    self.type = "writeArea";
    self.borderWidth = 0;
    self.inHandDrawing = true;
    self.handDrawing = () => self.inHandDrawing && (self.getMode() !== PAGE_MODE.CONFIGURATION);
    self.handDrawingLines = {};
    self.currentLines = {config: {color: "white"}, lines: []};
    self.syncLines = [];
    self.writeMode = 'write';
    self.serializedFields.batchAdd("syncLine");
    self.selectedShape = null;

    let selfSelect = self.select;
    self.select = (x, y) => {
        selfSelect(x, y);
        self.isFocused = false;
    }

    self.setSelectedShape = shape => {
        self.selectedShape = shape;
    }

    let shapeAdded = self.shapeAdded;
    self.shapeAdded = (newShape, preContainer) => {
        shapeAdded(newShape, preContainer);
        // 限制拖拽范围
        let onMouseDrag = newShape.onMouseDrag;
        newShape.onMouseDrag = (position) => {
            self.selectedShape = null;
            rangeLimit(self, newShape, position);
            onMouseDrag(position);
        }

        let onMouseUp = newShape.onMouseUp;
        newShape.onMouseUp = (position) => {
            self.selectedShape = null;
            onMouseUp(position);
        }

        let onMouseDown = newShape.onMouseDown;
        newShape.onMouseDown = (position) => {
            self.selectedShape = null;
            onMouseDown(position);
        }
    }

    function rangeLimit(area, self, position) {
        if (self.x + position.deltaX < area.x) {
            position.deltaX = area.x - self.x;
        } else if (self.x + self.width + position.deltaX > area.x + area.width) {
            position.deltaX = area.x + area.width - self.x - self.width;
        }
        if (self.y + position.deltaY < area.y) {
            position.deltaY = area.y - self.y;
        } else if (self.y + self.height + position.deltaY > area.y + area.height) {
            position.deltaY = area.y + area.height - self.y - self.height;
        }
    }

    self.click = (x, y) => {
        let selectedShape = self.selectedShape;
        if (selectedShape === null) {
            return;
        }
        const newShape = self.page.createNew(selectedShape.type, x, y);
        newShape.container = self.id;
        newShape.select(x, y);

        if (selectedShape.copyProperties) {
            selectedShape.copyProperties.forEach(property => {
                newShape[property] = selectedShape[property];
            })
        }
        if (selectedShape.onShapePlaced) {
            selectedShape.onShapePlaced(newShape, self);
        }
        selectedShape.invalidate();
        newShape.invalidate();
        self.selectedShape = null;
    }

    const interactDrawer = self.page.interactDrawer;
    let shapeSetProperty = self.setProperty;
    self.setProperty = (property, value) => {
        if (property !== 'syncLine') {
            shapeSetProperty(property, value);
            return;
        }
        if (value === null) {
            clearAllHandwritingLines();
            interactDrawer.draw();
            return;
        }
        // 协同场景下获得的
        value.forEach(eachLine => {
            const existLine = self.syncLines.filter(line => line.id === eachLine.id);
            if (existLine.length > 0) {
                existLine[0].lines = [eachLine.line];
            } else {
                self.syncLines.push({config: eachLine.config, lines: [eachLine.line], id: eachLine.id});
            }
        })
        // interactDrawer.draw({x:value.line[value.line.length-1][0], y:value.line[value.line.length-1][1]});
        interactDrawer.draw();
    }

    function clearAllHandwritingLines() {
        self.handDrawingLines[self.id] = [];
        self.syncLines = [];
        self.changeHandDrawingConfig(self.currentLines.config);
    }

    self.clear = () => {
        self.getShapes().forEach(s => s.remove(false));
        self.syncLine = null;
        clearAllHandwritingLines();
    }

    self.getPageHandwritingLines = () => {
        let lines = self.handDrawingLines[self.id];
        if (lines === undefined) {
            lines = [self.currentLines];
            self.handDrawingLines[self.id] = lines;
        }
        return lines;
    }

    self.changeHandDrawingConfig = (handDrawingConfig) => {
        self.currentLines = {config: handDrawingConfig, lines: []};
        self.getPageHandwritingLines().push(self.currentLines);
    }

    let onMouseDown = self.onMouseDown;
    self.onMouseDown = position => {
        onMouseDown.call(self, position);
        if (!self.handAction()) {
            return;
        }

        if (self.writeMode === 'write') {
            self.currentLine = [];
            self.currentLineId = self.page.graph.uuid();
            self.currentLine.push([position.x, position.y]);
            self.currentLines.lines.push(self.currentLine);
        }
    };

    let onMouseUp = self.onMouseUp;
    self.onMouseUp = position => {
        onMouseUp.call(self, position);
        if (self.writeMode === 'write') {
            self.currentLine = undefined;
        }
    };

    let onMouseDrag = self.onMouseDrag;
    self.onMouseDrag = position => {
        if (!self.handAction()) {
            onMouseDrag.call(self, position);
            return;
        }

        const number = 20;
        if (self.writeMode === 'write') {
            if (self.currentLine === undefined) {
                return;
            }
            // self.cursor = CURSORS.POINTER;
            let lastpoint = self.currentLine[self.currentLine.length - 1];
            if (Math.abs(position.x - lastpoint[0]) + Math.abs(position.y - lastpoint[1]) < 6) {
                return;
            }
            self.currentLine.push([position.x, position.y]);
            self.syncLine = [{
                line: self.currentLine, config: self.currentLines.config, id: self.currentLineId
            }]
        } else if (self.writeMode === 'eraser') {
            // self.cursor = CURSORS.MOVE;
            let minX = position.x - number;
            let minY = position.y - number;
            let maxX = position.x + number;
            let maxY = position.y + number;
            console.log('eraser', position.x, position.y)

            self.getPageHandwritingLines().forEach(configLines => {
                let newLines = [];
                configLines.lines.forEach(line => {
                    let newLine = [];
                    for (let i = 0; i < line.length; i++) {
                        let lineX = line[i][0];
                        let lineY = line[i][1];
                        if (lineX > minX && lineX < maxX && lineY > minY && lineY < maxY) {
                            if (newLine.length > 1) {
                                newLines.push(newLine);
                            }
                            newLine = [];
                        } else {
                            newLine.push([lineX, lineY]);
                        }
                    }
                    newLines.push(newLine);
                });
                configLines.lines = newLines;
            });
        }
    };

    let drawDynamic = interactDrawer.drawDynamic;
    interactDrawer.drawDynamic = (context, x, y) => {
        drawDynamic.call(interactDrawer, context, x, y);
        if (self.getPageHandwritingLines().length === 0) {
            return;
        }

        //
        // let configLines = self.getPageHandwritingLines()[self.getPageHandwritingLines().length-1];
        // context.strokeStyle = configLines.config.color;
        // context.lineWidth = 2;
        //
        // const length = configLines.lines.length;
        // let line = configLines.lines[length-1];
        // if (!line || line.length < 2) {
        //   return;
        // }
        //
        // const lineLength = line.length;
        // context.beginPath();
        // context.moveTo((line[lineLength-2][0] + self.x) * self.scaleX, (line[lineLength-2][1] + self.y) * self.scaleY);
        // context.lineTo((line[lineLength-1][0] + self.x) * self.scaleX, (line[lineLength-1][1] + self.y) * self.scaleY);
        // context.stroke();

        function drawHandwritingLines(lines) {
            lines.forEach(configLines => {
                context.strokeStyle = configLines.config.color;
                context.lineWidth = 2;
                configLines.lines.forEach(line => {
                    if (line.length < 2) {
                        return;
                    }
                    context.beginPath();
                    context.moveTo((line[0][0] + self.x) * self.scaleX, (line[0][1] + self.y) * self.scaleY);
                    for (let i = 1; i < line.length - 1; i++) {
                        context.lineTo((line[i][0] + self.x) * self.scaleX, (line[i][1] + self.y) * self.scaleY);
                    }
                    context.stroke();
                });
            });
        }

        drawHandwritingLines(self.getPageHandwritingLines());
        drawHandwritingLines(self.syncLines);
    };
    return self;
}

const WHITE_BOARD_SETTING = {
    borderColor: "transparent",
    backColor: "white",
    headColor: "gray",
    fontColor: "dimgray",
    captionfontColor: "dimgray",
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
    itemPad: [6, 6, 6, 6], // focusBorderColor: "darkorange",
    // mouseInColor: "orange",
    // focusBackColor: "#666666",
    borderWidth: 1,
    globalAlpha: 1,
    backAlpha: 0.1,
    cornerRadius: 0,
    dashWidth: 0,
    autoText: false,
    autoHeight: false,
    autoWidth: false,
    margin: 5,
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
    progressPercent: 0.75,
    showedProgress: false,
    tag: {},
    geoScale: false,
    enableSocial: true
}

export {whiteBoardBackup, whiteBoardPage, writeArea};
