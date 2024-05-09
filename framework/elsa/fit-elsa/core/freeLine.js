import { rectangle, shapeComment } from './rectangle.js';
import { canvasRectangleDrawer } from './drawers/rectangleDrawer.js';
import { compareAndSet, convertPositionWithParents, getRotatedCoordinate, isPointInRect, isRectInteractRect, pixelRateAdapter, sleep, uuid } from '../common/util.js';
import { CURSORS, LINEMODE, SHAPE_HIT_TYPE, Z_INDEX_OFFSET } from '../common/const.js';
import { addFreeLineCommand, updateFreeLineCommand } from './commands.js';
import { line } from './line.js';


const ACTIONS = {
    ADD: "add_free_lines",
    DELETE: "delete_free_lines",
    UPDATE: "update_free_lines"
};

const freeLine = (id, x, y, width, height, parent) => {
    const bufferSize = 4, off = 0;
    const self = rectangle(id, x, y, width, height, parent, freeLineDrawer);
    self.type = "freeLine";
    self.overflowHidden = false;
    self.disableNewResize = true;
    self.backColor = self.focusBackColor = "transparent";
    self.borderWidth = 0;
    self.lineWidth = 3;
    self.lines = [];
    self.x = 0;
    self.y = 0;
    self.margin = 0;
    self.enableCache = false;
    self.penMode = "solid";//solid,highlight,brush
    self.slices = 2;//怎么切画布，2代表切成四块，3代表切成9块
    self.staticSlices = 2;

    if (self.page.handDrawingColor) {
        self.borderColor = self.page.handDrawingColor;
        self.focusBorderColor = self.page.handDrawingColor;
        self.mouseInColor = self.page.handDrawingColor;
    }
    if (self.page.handDrawingWidth) {
        self.lineWidth = self.page.handDrawingWidth;
    }
    if (self.page.handDrawingAlpha) {
        self.globalAlpha = self.page.handDrawingAlpha;
    }

    self.initialize = () => {
        self.newLine();
        self.addPoint(x, y);
    }
    // self.moveable = false;

    const points = [];
    const buffer = [];

    const refreshSize = () => {
        const x = self.x;
        const y = self.y;
        const width = self.width;
        const height = self.height;
        self.x = -self.page.x;
        self.y = -self.page.y;
        self.width = self.page.width / self.page.scaleX;
        self.height = self.page.height / self.page.scaleY;
        self.refreshed = self.x !== x || self.y !== y || self.width !== width || self.height !== height;
        // const width = self.page.width / self.page.scaleX + self.page.x;
        // const height = self.page.height / self.page.scaleY + self.page.y;

        // width > self.width && (self.width = width);
        // height > self.height && (self.height = height);
        // self.x = 0;
        // self.y = 0;
    };

    self.serialized = serialized => {
        serialized.lines = JSON.parse(JSON.stringify(serialized.lines));
        serialized.lines.forEach(line => {
            delete line.pathData;
        })
    };

    self.deSerialized = () => {
        if (self.lines.length === 0) {
            self.remove(true);
            return;
        }

        self.lines.forEach(l => {
            boundLine(l);
            delete l.pathData;
        });

        // const last = self.page.shapes.find(s => s.type === "freeLine" && s.id !== self.id);
        // if (last) {
        //     self.lines.forEach(l => last.acceptLine(l));
        //     doneFreeLine(last,self.lines);
        //     self.remove();
        //     last.invalidate();
        // } else {
        //     self.invalidate();
        // }
        self.invalidate();
        console.log("freeline is deserialized...");
        // const maxRight = self.lines.max(l => l.bound.x + l.bound.width);
        // const maxBottom = self.lines.max(l => l.bound.y + l.bound.height);
        // maxRight > self.width && (self.width = maxRight);
        // maxBottom > self.height && (self.height = maxHeight);
    };

    refreshSize();

    const getFrame = self.getFrame;
    self.getFrame = () => {
        const getBound = lines => {
            const offset = self.lineWidth;
            const bound = {};
            bound.x = lines.min(l => l.bound.x) - offset;
            bound.y = lines.min(l => l.bound.y) - offset;
            bound.width = lines.max(l => l.bound.x + l.bound.width) - bound.x + 2 * offset;
            bound.height = lines.max(l => l.bound.y + l.bound.height) - bound.y + 2 * offset;
            return bound;
        }
        //has new
        if (self.lines.contains(l => l.isNew)) return { x: 0, y: 0, width: 0.1, height: 0.1 };
        //updated
        let lines = self.lines.filter(l => l.isUpdated);
        if (lines.length > 0) {
            return getBound(lines);
        }
        //whole refresh
        if (self.page.graph.inUndo || self.page.graph.inRedo) {
            return { x: self.x, y: self.y, width: self.width, height: self.height };
        }
        //select
        lines = self.lines.filter(l => l.selected);
        if (lines.length === 0) {
            return getFrame.call(self);
        }
        return getBound(lines);
        // bound.x += self.x;
        // bound.y += self.y;
        // return bound;

    };

    self.isLocked = () => false;

    const onMouseDown = self.onMouseDown;
    self.onMouseDown = position => {
        if (self.eraser) return;
        const isFocused = self.isFocused;
        onMouseDown.call(self, position);
        if (isFocused) {
            self.select();
            self.render();
        }
    };

    const onMouseUp = self.onMouseUp;
    self.onMouseUp = async position => {
        if (self.mousedownConnector !== null) {
            await self.mousedownConnector.release(position);
            self.mousedownConnector = null;
        }
        // onMouseUp.call(self, position);
        !self.closed && self.done();
        self.drawer.drawRunning();
        // if (self.lines.contains(l => l.pathData === undefined)) {
        //     self.render();
        // }
    };

    const select = self.select;
    self.select = () => {
        // if (self.mousedownConnector || self.indraggingArea) return;
        if (self.mousedownConnector) return;
        if (self.lines.contains(l => l.selected === true && l.preSelected === true)) return;
        self.isFocused && self.unSelect();
        self.lines.forEach(l => {
            // delete l.selected;
            if (l.preSelected === true) {
                l.selected = l.preSelected;
                delete l.preSelected;
                // self.moveable = true;
            }
        })
        select.call(self);
        // self.drawer.drawRunning();
    };

    const unSelect = self.unSelect;
    self.unSelect = () => {
        self.lines.forEach(l => {
            // if (l.selected) l.isNew = true;
            if (l.selected) l.isUpdated = true;
            delete l.selected
        });
        unSelect.call(self);
    };

    self.contains = (x, y) => {
        if (self.eraser) return true;
        const step = 4;


        const rect = { x: x - step, y: y - step, width: 2 * step, height: 2 * step };

        return self.inSelection(rect);
    };

    self.inSelection = (rect) => {
        let selected = false;
        for (let j = 0; j < self.lines.length; j++) {
            const l = self.lines[j];
            // delete l.preSelected;

            if (!isRectInteractRect(l.bound, rect)) {
                delete l.preSelected;
                continue;
            }
            if (l.selected) {
                l.preSelected = true;
                selected = true;
                continue;
            }
            if (l.fmla) {
                l.preSelected = true;
                selected = true;
                if (rect.width < 10) break;

            } else {
                for (let i = 0; i < l.points.length; i++) {
                    if (isPointInRect({ x: l.points[i][0], y: l.points[i][1] }, rect)) {
                        l.preSelected = true;
                        selected = true;
                        break;
                    }
                }
            }
        }
        return selected;
    };

    self.newLine = () => {
        buffer.splice(0, buffer.length);
        points.splice(0, points.length);
    };

    const addToEraserCommand = (position, shape) => {
        const CMD = "eraser";
        if (position.context.command !== CMD) {
            position.context.command = CMD;
            position.context.shapes = [];
        }

        if (!position.context.shapes.contains(s => s.shape === shape)) {
            position.context.shapes.push({ shape, preValue: shape.lines });
        }

        // let dirty = position.context.shapes.find(s => s.shape === self);
        // if (!dirty) {
        //     dirty = { shape: self, lines: {} };
        //     dirty.lines.preValue = [];
        //     self.lines.forEach(l => {
        //         const line = l.points;
        //         const copy = [];
        //         line.forEach(p => copy.push([p[0], p[1]]));
        //         dirty.lines.preValue.push(copy);
        //     });
        //     position.context.command = "eraser";
        //     position.context.shapes.push(dirty);
        // }
    };

    const onMouseMove = self.onMouseMove;
    self.onMouseMove = position => {
        if (self.eraser) {
            self.page.cursor = CURSORS.ERASER;
        } else {
            onMouseMove.call(self, position);
        }
    };

    const onMouseDrag = self.onMouseDrag;
    self.onMouseDrag = (position) => {
        if (!self.closed) {
            self.addPoint(position.x, position.y);
            return;
        }
        if (self.eraser) {
            self.page.shapes.filter(s => s.isTypeof("freeLine")).forEach(s => {
                s.drawer.erase(position.x - self.eraser, position.y - self.eraser, 2 * self.eraser, 2 * self.eraser);
                s.erase(position);

            })
            // self.drawer.erase(position.x - self.eraser, position.y - self.eraser, 2 * self.eraser, 2 * self.eraser);
            // self.erase(position);
            return;
        }
        onMouseDrag.call(self, position);
    };

    /**
     * 矩形.封装矩形对应的行为.
     *
     * @param x 横坐标.
     * @param y 纵坐标.
     * @param width 宽度.
     * @param height 高度.
     * @returns {{}} 矩形对象.
     */
    const eraserRect = (x, y, width, height) => {
        const self = {};
        self.x = x;
        self.y = y;
        self.width = width;
        self.height = height;

        // 矩形的四个顶点.
        const pointNW = { x: x, y: y };
        const pointNE = { x: x + width, y: y };
        const pointSE = { x: x + width, y: y + height };
        const pointSW = { x: x, y: y + height };

        // 矩形的四个边.
        const borders = [
            lineSegment(pointNW, pointNE),
            lineSegment(pointNW, pointSW),
            lineSegment(pointSW, pointSE),
            lineSegment(pointSE, pointNE)
        ];

        /**
         * 是否在矩形中，不包含边界.
         *
         * @param point 坐标点.
         * @returns {*|boolean} true/false.
         */
        self.in = (point) => {
            return isPointInRect(point, { x, y, width, height }) && !self.onBorder(point);
        }

        /**
         * 是否包含在矩形中(包含边界).
         *
         * @param point 坐标点.
         * @returns {*|boolean} true/false.
         */
        self.contains = (point) => {
            return isPointInRect(point, { x, y, width, height });
        }

        /**
         * 判断坐标是否在矩形的四条边上.
         *
         * @param point 坐标点.
         * @returns {*} true/false.
         */
        self.onBorder = (point) => {
            return borders.some(border => border.onSegment(point));
        }

        /**
         * 求线和矩形的交点.
         *
         * @param eraserLine 线段.
         * @returns {*[]} 交点数组.
         */
        self.intersectionsWithLine = (eraserLine) => {
            const intersections = [];
            borders.forEach(border => {
                const intersection = border.intersection(eraserLine);
                if (intersection && !self.isVertex(intersection)) {
                    intersections.push(intersection);
                }
            });
            return intersections;
        }

        /**
         * 是否是四个顶点之一.
         *
         * @param point 待判断的点.
         * @returns {boolean} true/false.
         */
        self.isVertex = (point) => {
            return (pointNE.x === point.x && pointNE.y === point.y)
                || (pointNW.x === point.x && pointNW.y === point.y)
                || (pointSE.x === point.x && pointSE.y === point.y)
                || (pointNE.x === point.x && pointNE.y === point.y)
        }

        return self;
    }
    /**
     * 根据两点式构建线段.
     *
     * @param point1 点1.
     * @param point2 点2.
     */
    const lineSegment = (point1, point2) => {
        const self = {};
        self.point1 = point1;
        self.point2 = point2;

        // 直线一般式的参数.ax + by + c = 0 的形式.
        self.a = point2.y - point1.y;
        self.b = point1.x - point2.x;
        self.c = (point2.x - point1.x) * point1.y - (point2.y - point1.y) * point1.x;

        /**
         * 求交点.
         *
         * @param otherLine 其他的线.
         * @returns {null|{x: number, y: number}} 没有交点则返回null，否则返回交点.
         */
        self.intersection = (otherLine) => {
            const denominator = otherLine.a * self.b - self.a * otherLine.b;
            if (denominator === 0) {
                return null;
            }

            // 因为坐标系都是正数，因此需要取绝对值.
            const x = Math.abs(-(self.b * otherLine.c - otherLine.b * self.c) / denominator);
            const y = Math.abs((self.a * otherLine.c - otherLine.a * self.c) / denominator);
            if (self.onSegment({ x, y }) && otherLine.onSegment({ x, y })) {
                return { x, y }
            }
            return null;
        }

        /**
         * 判断点是否在线段上.
         *
         * @param point 点.
         * @returns {boolean} true/false.
         */
        self.onSegment = (point) => {
            const minX = Math.min(point1.x, point2.x);
            const maxX = Math.max(point1.x, point2.x);
            const minY = Math.min(point1.y, point2.y);
            const maxY = Math.max(point1.y, point2.y);

            // 计算结果有可能是小数.这里取近似值即可,直接等于0，会导致有些点无法删除的问题.
            const diff = (point.x - point1.x) * (point2.y - point1.y) - (point2.x - point1.x) * (point.y - point1.y);
            return diff < 0.01
                && point.x >= minX && point.x <= maxX
                && point.y >= minY && point.y <= maxY;
        }

        return self;
    };

    self.duplicate = () => {
        const colonLine = line => {
            const nl = {};
            const DELTA = 20;
            for (let f in line) {
                nl[f] = line[f];
            }
            nl.id = uuid();
            nl.bound = { x: line.bound.x + DELTA, y: line.bound.y + DELTA, width: line.bound.width, height: line.bound.height };
            nl.points = [];
            line.points.forEach(p => {
                nl.points.push([p[0] + DELTA, p[1] + DELTA]);
            })
            return nl;
        }
        const newLines = [];
        self.lines.filter(l => l.selected).forEach(l => {
            delete l.selected;
            delete l.preSelected;
            const nl = colonLine(l);
            self.lines.push(nl);
            nl.preSelected = true;
            newLines.push(nl);
        })
        addFreeLineCommand(self.page, [{ shape: self, lines: newLines }]);

        self.page.graph.collaboration.invoke({
            method: "freeline_done",
            page: self.page.id,
            shape: self.id,//free.id,
            mode: self.page.mode,
            value: { lines: newLines, to: self.id }
        }, () => { });

        self.select();
    };

    self.copy = () => {
        const lines = self.lines.filter(l => l.selected);
        if (lines.length === 0) return;

        const data = self.serialize();
        data.lines = lines;
        return data;
    };
    self.cut = () => {
        const data = self.copy();
        self.lines.remove(l => l.selected)
        return data;
    };
    // self.paste = data => {
    //     return true;
    //     //todo
    // };
    // self.keyPressed = e => {
    //     if ((e.ctrlKey || e.metaKey) && (e.code === "KeyD")) {
    //         return false;
    //     }
    // }

    // self.getIndex = () => {
    //     self.index = self.page.shapes.length - 1 + Z_INDEX_OFFSET;
    //     return self.index;
    // };

    self.erase = async (position) => {
        const eraserWidth = 1 * self.eraser;
        const initNewLine = (color, width) => {
            return { points: [], color, width };
        };

        // 宽高需要与cursorDrawer中的保持一致.
        const rect = eraserRect(position.x - eraserWidth, position.y - eraserWidth, 2 * eraserWidth, 2 * eraserWidth);
        // self.drawer.erase(rect.x, rect.y, rect.width, rect.height);
        const newLines = [];
        let newLine = initNewLine();
        let erased = false;

        self.lines.forEach(l => {
            const off_x = 0;// self.x;
            const off_y = 0;// self.y;
            const color = l.color;
            const width = l.width;
            const line = l.points;
            newLine.color = color;
            newLine.width = width;
            for (let i = 0; i < line.length - 1; i++) {
                if (line[i] === undefined || line[i + 1] === undefined) {
                    continue;
                }
                let point = { x: (line[i][0] + off_x + off), y: (line[i][1] + off_y + off) };
                let pointNext = { x: (line[i + 1][0] + off_x + off), y: (line[i + 1][1] + off_y + off) };
                // 简单排除不可能相交情况
                if ((point.x < rect.x && pointNext.x < rect.x)
                    || (point.y < rect.y && pointNext.y < rect.y)
                    || (point.x > rect.x + rect.width && pointNext.x > rect.x + rect.width)
                    || (point.y > rect.y + rect.height && pointNext.y > rect.y + rect.height)) {
                    newLine.points.push(line[i]);
                    continue;
                }

                const pointInRect = rect.in(point);
                const pointNextInRect = rect.in(pointNext);

                // 两个点都在范围内，直接丢弃当前点，交给下一个点计算
                if (pointInRect && pointNextInRect) {
                    // 已有的线加入结果列表
                    if (newLine.points.length > 1) {
                        newLines.push(newLine);
                    }
                    newLine = initNewLine(color, width);
                    erased = true;
                    continue;
                }

                const lineSeg = lineSegment(point, pointNext);
                const crossPoints = rect.intersectionsWithLine(lineSeg);

                // 没有交点
                if (crossPoints.length === 0) {
                    newLine.points.push(line[i]);
                    continue;
                }

                // 不能直接parseInt设置为整数，会导致freeLine在删除的过程中跳变.
                crossPoints.forEach(p => {
                    p.x = p.x - off_x - off;
                    p.x = Math.floor(p.x * 1000) / 1000; // 保留3位有效数字.不四舍五入.
                    p.y = p.y - off_y - off;
                    p.y = Math.floor(p.y * 1000) / 1000; // 保留3位有效数字.不四舍五入.
                })

                // 一个交点
                if (crossPoints.length === 1) {
                    // 当前点在范围内，删除当前点，结束前一条线，以交点为起点开始下一条
                    if (pointInRect) {
                        if (newLine.points.length > 1) {
                            newLines.push(newLine);
                        }
                        newLine = initNewLine(color, width);

                        newLine.points.push([crossPoints[0].x, crossPoints[0].y]);
                        erased = true;
                        continue;
                    }
                    // 下一个点在范围内
                    if (pointNextInRect) {
                        newLine.points.push(line[i]);
                        newLine.points.push([crossPoints[0].x, crossPoints[0].y]);
                        newLines.push(newLine);
                        newLine = initNewLine(color, width);
                        erased = true;
                        continue;
                    }
                    // 极端情况，交点在边界，无视
                    newLine.points.push(line[i]);
                    continue;
                }
                // 两个交点情况，橡皮擦横穿，找到合适的交点，分割线段
                let key = 'x';
                if (Math.abs(pointNext.x - point.x) < 0.01) {
                    key = 'y';
                }
                newLine.points.push(line[i]);
                let oldLineEnd, newLineStart;
                // 同号
                if (pointNext[key] - point[key] < 0 === crossPoints[1][key] - crossPoints[0][key] < 0) {
                    oldLineEnd = [crossPoints[0].x, crossPoints[0].y];
                    newLineStart = [crossPoints[1].x, crossPoints[1].y];
                } else {
                    oldLineEnd = [crossPoints[1].x, crossPoints[1].y];
                    newLineStart = [crossPoints[0].x, crossPoints[0].y];
                }
                newLine.points.push(oldLineEnd);
                newLines.push(newLine);
                newLine = initNewLine(color, width);
                newLine.points.push(newLineStart);
                erased = true;
            }
            // 添加最后一个点
            const lastPointIndex = line.length - 1;
            let lastPoint = { x: (line[lastPointIndex][0] + off_x + off), y: (line[lastPointIndex][1] + off_y + off) };
            if (!rect.in(lastPoint)) {
                newLine.points.push(line[lastPointIndex]);
            } else {
                erased = true;
            }
            // 遍历完成，确认最后一条线长度
            if (newLine.points.length > 1) {
                newLines.push(newLine);
            }
            newLine = initNewLine(color, width);
        });
        if (erased) {
            newLines.forEach(l => {
                (!l.bound) && boundLine(l);
                (!l.id) && (l.id = uuid());
            })
            addToEraserCommand(position, self);
            self.lines = newLines;
            // self.invalidate();
        }
    };

    let newPoints = [];
    let inTransaction = false;
    self.addPoint = (x, y, collaboration) => {
        appendToBuffer({ x, y });
        const pt = getAveragePoint(0);
        if (pt) {
            points.push([pt.x, pt.y]);
            if (points.length >= 2) {
                self.drawer.addPoint(pt.x, pt.y);
            }
        }

        newPoints.push({ x, y });
        if (!inTransaction) {
            inTransaction = true;
            setTimeout(() => {
                (!collaboration) && self.page.graph.collaboration.invoke({
                    method: "add_freeline_point",
                    page: self.page.id,
                    shape: self.id,
                    value: newPoints,
                    mode: self.page.mode
                }, () => {
                });
                inTransaction = false;
                newPoints = [];
            }, 30)
        }


    };

    const appendToBuffer = (pt) => {
        buffer.push(pt);
        while (buffer.length > bufferSize) {
            buffer.shift();
        }
    };
    const getAveragePoint = offset => {
        const len = buffer.length;
        if (len % 2 === 1 || len >= bufferSize) {
            let totalX = 0, totalY = 0, count = 0;
            for (let i = offset; i < len; i++) {
                count++;
                totalX += buffer[i].x;
                totalY += buffer[i].y;
            }
            return {
                x: totalX / count, y: totalY / count
            }
        }
        return null;
    };

    let ct;

    self.getOffSet = () => off;


    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        const syncResize = (degree, x, y) => {
            const lines = self.lines.filter(l => {
                !degree && l.selected && boundLine(l);
                return l.selected;
            });
            self.local = { action: ACTIONS.UPDATE, lines };
            self.drawer.drawRunning(degree, x, y);
        }
        const off = self.lineWidth;
        initConnectors.call(self);
        self.connectors.forEach((c, i) => {
            const onMouseDrag = c.onMouseDrag;
            c.onMouseDrag = position => {
                //history
                const isBegin = !position.context.shapes.contains(s => s.shape === self);
                let preValue;
                if (isBegin) preValue = JSON.parse(JSON.stringify(self.lines.filter(l => l.selected)));
                onMouseDrag.call(c, position);
                position.context.command = "updateFreeLine";
                const dirty = position.context.shapes.find(s => s.shape === self);
                if (isBegin) {
                    dirty.lines = {};
                    dirty.lines.preValue = preValue;
                } else {
                    // dirty.lines.value = JSON.parse(JSON.stringify(self.lines.filter(l => l.selected))).map(l=>boundLine(l));
                }

            };
            c.release = (position) => {
                const lines = [];
                self.lines.forEach(l => {
                    if (!l.selected) return;
                    l.points.forEach(p => {
                        delete p.x;
                        delete p.y;
                    });
                    delete l.preRotate;
                    boundLine(l);
                    lines.push(l);
                })
                const dirty = position.context.shapes.find(s => s.shape === self);
                dirty && (dirty.lines.value = JSON.parse(JSON.stringify(lines)));
                // self.render();
            };
            const scale = (l, scalex, scaley) => {
                if (l.scale) {
                    l.scale[0] *= 1 + scalex;
                    l.scale[1] *= 1 + scaley;
                }
            }
            switch (c.type) {
                case "rightBottom":
                    c.moving = (deltaX, deltaY, x, y) => {
                        const frame = self.getFrame();
                        self.lines.forEach(l => {
                            if (!l.selected) return;
                            l.points.forEach(p => {
                                p[0] += (p[0] - frame.x - off) * deltaX / frame.width;
                                p[1] += (p[1] - frame.y - off) * deltaY / frame.height;
                            })
                            scale(l, deltaX / frame.width, deltaY / frame.height);
                            // l.bound.width *= 1 + (deltaX / frame.width);
                            // l.bound.height *= 1 + (deltaY / frame.height);
                        })
                        syncResize();
                        // self.drawer.drawRunning();
                    };
                    break;
                case "rightTop":
                    c.moving = (deltaX, deltaY, x, y) => {
                        const frame = self.getFrame();
                        self.lines.forEach(l => {
                            if (!l.selected) return;
                            l.points.forEach(p => {
                                p[0] += (p[0] - frame.x - off) * deltaX / frame.width;
                                p[1] -= (p[1] - frame.y - frame.height - 2 * off) * deltaY / frame.height;
                            })
                            scale(l, deltaX / frame.width, -deltaY / frame.height);
                            // l.bound.width *= 1 + (deltaX / frame.width);
                            // l.bound.y += deltaY;
                            // l.bound.height -= deltaY;
                        })
                        syncResize();
                        // self.drawer.drawRunning();
                    };
                    break;
                case "leftTop":
                    c.moving = (deltaX, deltaY, x, y) => {
                        const frame = self.getFrame();
                        self.lines.forEach(l => {
                            if (!l.selected) return;
                            l.points.forEach(p => {
                                p[0] -= (p[0] - frame.x - frame.width - 2 * off) * deltaX / frame.width;
                                p[1] -= (p[1] - frame.y - frame.height - 2 * off) * deltaY / frame.height;
                            })
                            scale(l, -deltaX / frame.width, -deltaY / frame.height);
                            // l.bound.x += deltaX;
                            // l.bound.y += deltaY;
                            // l.bound.width -= deltaX;
                            // l.bound.height -= deltaY;
                        })
                        syncResize();
                        // self.drawer.drawRunning();
                    };
                    break;
                case "leftBottom":
                    c.moving = (deltaX, deltaY, x, y) => {
                        const frame = self.getFrame();
                        self.lines.forEach(l => {
                            if (!l.selected) return;
                            l.points.forEach(p => {
                                p[0] -= (p[0] - frame.x - frame.width - 2 * off) * deltaX / frame.width;
                                p[1] += (p[1] - frame.y - off) * deltaY / frame.height;
                            })
                            scale(l, -deltaX / frame.width, deltaY / frame.height);
                            // l.bound.x += deltaX;
                            // l.bound.width -= deltaX;
                            // l.bound.height *= 1 + (deltaY / frame.height);
                        })
                        syncResize();
                        // self.drawer.drawRunning();
                    };
                    break;
                case "rotate":
                    c.moving = (deltaX, deltaY, x, y) => {
                        const frame = self.getFrame();
                        const cx = frame.x + frame.width / 2;
                        const cy = frame.y + frame.height / 2;

                        let deg = (Math.atan2(y - cy, x - cx) * 180 / Math.PI + 90);
                        if (deg > 360) {
                            deg -= 360;
                        }
                        self.lines.forEach(l => {
                            if (!l.selected) return;
                            l.points.forEach(p => {
                                !p.x && (p.x = p[0]);
                                !p.y && (p.y = p[1]);
                                const p1 = getRotatedCoordinate(p.x, p.y, cx, cy, deg * Math.PI / 180);
                                p[0] = p1.x;
                                p[1] = p1.y;
                            });
                            (l.preRotate === undefined) && (l.preRotate = l.rotate);
                            (l.rotate !== undefined) && (l.rotate = l.preRotate + deg * Math.PI / 180);

                        })
                        syncResize(deg * Math.PI / 180, cx, cy);
                        // self.drawer.drawRunning(deg * Math.PI / 180, cx, cy);
                    };

                    break;
                default:
                    c.visible = false;

            }
        });
    };

    self.getFocused = () => self.local && self.local.lines;// self.lines.filter(l => l.selected);
    // let lines;
    const remove = self.remove;
    self.remove = (removeSelf, focused) => {
        if (removeSelf === true) {//传统删除，删除自己
            remove.call(self);
            return;
        }
        focused && focused.forEach(l => l.selected = true);
        //删除线
        const lines = [];
        self.lines.remove(l => {
            if (l.selected) {
                lines.push(l);
                return true;
            }
        });
        self.render();
        self.local = { action: ACTIONS.DELETE, lines };
        return [self];
        // removeFreeLineCommand(self.page, [{ shape: self, lines }]);
    };
    self.undoRemove = (page, index, focused) => {
        self.lines.forEach(l => l.selected = false);
        focused.forEach(l => l.selected = true);
        self.lines.push.apply(self.lines, focused);
        if (!self.isFocused) {
            self.select();
        } else {
            self.render();
        }
        self.local = { action: ACTIONS.ADD, lines: focused };
    };

    const dragTo = self.dragTo;
    self.dragTo = (position) => {
        const x = position.x, y = position.y;
        const isBegin = !position.context.shapes.contains(s => s.shape === self);
        const dirty = dragTo.call(self, position);
        // position.context.command = "updateFreeLine";
        if (isBegin) {
            dirty.dx = dirty.dy = 0;
            dirty.lines = self.lines.filter(l => l.selected).map(l => l.id);
        }
        // dirty.x.value += x;
        // dirty.y.value += y;
        dirty.dx += position.deltaX;
        dirty.dy += position.deltaY;

        return dirty;
    };

    self.preMove = action => {
        self.lines.forEach(l => {
            // l.selected = action.context.lines.contains(l1 => l1 === l.id);
            l.preSelected = action.context.lines.contains(l1 => l1 === l.id);
        })
        if (!action.context.transfered) {
            const dx = action.context.dx;// action.context.x.value - action.context.x.preValue;
            const dy = action.context.dy;// action.context.y.value - action.context.y.preValue;
            action.context.x.preValue = -dx + self.x;
            action.context.y.preValue = -dy + self.y;
            action.context.x.value = dx + self.x;
            action.context.y.value = dy + self.y;
            action.context.transfered = true;
        }
        self.select();
        // self.render();
    }

    self.moveTo = (x, y) => {
        const dx = x - self.x;
        const dy = y - self.y;
        const lines = [];
        self.lines.forEach(l => {
            if (!l.selected) return;
            l.points.forEach(p => {
                p[0] += dx;
                p[1] += dy;
            })

            l.bound.x += dx;
            l.bound.y += dy;
            delete l.pathData;
            lines.push(l);
        })
        self.local = { action: ACTIONS.UPDATE, lines };
        self.drawer.drawRunning();
    };

    // self.moved = () => {
    //     if (self.page.graph.inUndo || self.page.graph.InRedo) {
    //         self.render();
    //     }
    // };

    self.currentPoints = () => points;
    self.setLocal = (() => {
        const updateLines = lines => {
            lines.forEach(l => {
                const line = self.lines.find(l1 => l1.id === l.id);
                line.points = l.points;
                line.bound = l.bound;
                delete line.selected;
                delete line.pathData;
            })
        };

        const deleteLines = lines => {
            self.lines.remove(l => lines.contains(l1 => l1.id === l.id));
        };

        const addLines = lines => {
            self.lines.push.apply(self.lines, lines);
        };

        const funcs = {};
        funcs[ACTIONS.ADD] = addLines;
        funcs[ACTIONS.DELETE] = deleteLines;
        funcs[ACTIONS.UPDATE] = updateLines;
        return localAction => {
            localAction.lines.forEach(l => delete l.pathData);
            funcs[localAction.action](localAction.lines);
            self.render();
        };
    })();
    self.acceptLine = line => {
        self.lines.forEach(l => delete l.selected);
        self.lines.push(line);
        boundLine(line);//确认边框，提高选中性能
        self.invalidate();
        addFreeLineCommand(self.page, [{ shape: self, lines: [line] }]);
    };

    const boundLine = line => {
        line.bound = self.getLineBound(line);
        (!line.id) && (line.id = uuid());
        self.page.handWritingChanged && self.page.handWritingChanged(line);
        // delete line.pathData;
        return line;
    };

    self.getLineBound = line => {
        const x = line.points.min(p => p[0]);
        const y = line.points.min(p => p[1]);
        const width = line.points.max(p => p[0]) - x;
        const height = line.points.max(p => p[1]) - y;
        return { x, y, width, height };
    }

    const determineLineHost = () => {
        return self.page.shapes.find(s => s.type === "freeLine" && s.closed === true && s.id !== self.id
            && self.page.shapes.indexOf(s) === self.page.shapes.length - 2 && s.penMode === self.penMode);
    };

    self.buildLine = () => {
        return { id: self.id, points, time: new Date().getTime(), color: self.borderColor, width: self.lineWidth, from: self.page.graph.session.name, isNew: true };
    };

    const doneFreeLine = (free, lines) => {
        self.page.graph.collaboration.invoke({
            method: "freeline_done",
            page: free.page.id,
            shape: self.id,//free.id,
            mode: free.page.mode,
            value: { lines, to: free.id }
        }, () => { });
        return free;
    };

    self.done = (inCollaboration) => {
        if (self.closed === true) return;
        self.closed = true;

        const line = self.buildLine();
        let free = determineLineHost();
        if (free) {
            free.acceptLine(line);
            self.remove(true);
        } else {
            self.acceptLine(line);
            free = self;
        }
        (!inCollaboration) && doneFreeLine(free, [line]);
        return [free, line];
    };


    // const render = self.render;
    // self.render = () => {
    //     // self.drawer.resize();
    //     if (!self.isFocused) return;
    //     render.call(self);
    // }

    let invalidating = false;
    const invalidate = self.invalidate;
    self.invalidate = () => {
        if (invalidating === true) {
            console.warn("freeline is in drawing....");
            return;
        }
        refreshSize();
        invalidating = true;
        invalidate.call(self);
        invalidating = false;
    };

    self.refresh = () => {
        self.lines.forEach(l => l.selected = false);
        self.invalidate();
    };

    self.set = (key, value) => {
        if (key === "borderColor" || key === "backColor") {
            self.lines.filter(l => l.selected).forEach(l => {
                l[key === "borderColor" ? "color" : key] = value;
            });
        }
    };

    self.containerAllowed = container => container === self.page;
    // self.serializedFields.batchAdd("lines", "closed");
    // self.serializedFields.delete("x");
    // self.serializedFields.delete("y");
    // self.serializedFields.delete("width");
    // self.serializedFields.delete("height");

    // self.addDetection(["local"], function (property, value, preValue) {
    //     if (value === preValue || value.action !== ACTIONS.UPDATE) return;
    //     updateFreeLineCommand(self.page, [{ shape: self, lines: value.lines }]);
    // });
    return self;
};

//new Path2D("m 75.8672,80.4954 c -0.6572,-0.0938 -2.252,-0.4688 -2.627,-0.375 -1.0313,0.1875 0.375,1.501 -0.1875,1.876 -0.4688,0.1875 -1.0313,-0.0938 -1.501,0.0938 -0.1875,0.0938 0.0938,0.4688 -0.0938,0.6572 -0.9375,0.75 -2.4385,0.8438 -3.377,1.6875 -1.125,1.0322 2.0635,3.6582 0.6572,4.6904 -3.2832,2.3447 -1.2197,-1.5947 -3.377,1.501 -0.2813,0.375 0.8438,0.4688 1.0313,0.9375 0.1875,0.376 -0.9375,0.2822 -0.9375,0.6572 0,0.1875 0.9375,1.4072 0.5625,1.876 -1.0313,1.0313 -2.5322,-0.5635 -3.4707,-0.5635 -1.4063,0 1.3135,1.3135 -1.0313,0.6572 -0.6572,-0.1875 -1.501,-1.2197 -1.7822,-1.7822 -0.1875,-0.1875 -0.376,-0.751 -0.4697,-0.5625 -0.375,0.5625 -0.2813,1.2188 -0.6563,1.6875 -1.2188,1.5947 -2.9082,0 -4.3145,0.4697 -0.1875,0.0938 0.1875,0.4688 0,0.6563 -0.8447,0.4688 -2.5332,0.375 -3.377,0 0,0 0,-0.0938 0.0938,-0.0938 0.4688,-0.4688 1.0313,-0.9375 1.2197,-1.4072 3.2822,0.1875 -0.4697,-2.0635 -1.4072,-2.626 -0.6563,-0.375 0.375,-1.5947 0.1875,-2.0635 -0.1875,-0.4697 -1.6885,0.8438 -1.3135,-0.376 0.2813,-0.8438 2.0635,-1.5938 1.4072,-2.1572 -0.4688,-0.375 -2.627,-0.1875 -2.4385,-1.3125 0.1875,-1.501 2.5322,-1.126 2.7197,-3.002 -0.0938,0 -0.0938,0 -0.1875,0 0.5625,0.0938 1.126,0.1875 1.7822,0.2813 0.75,0.0938 1.501,0.6563 2.251,0.75 0.751,0.0938 1.501,-0.4688 2.252,-0.375 0.4688,0.0938 0.75,0.751 1.2188,0.751 0.1875,0.0938 0.0938,-0.4697 0.2813,-0.5635 0.4697,-0.4688 1.2197,-0.6563 1.6885,-1.2188 0.376,-0.2822 0.0938,-0.9385 0.376,-1.2197 1.7813,-1.4072 3.6582,0.375 5.3457,-0.375 0.5635,-0.2813 0.6572,-1.126 1.2197,-1.3135 0.0938,-0.0938 3.1895,0.375 3.2832,0.2813 0.2813,-0.0938 -0.2813,-0.4688 -0.375,-0.75 -0.2813,-1.501 0.8438,-1.876 2.251,-1.876 0.3752,0 1.1262,2.9072 3.0959,4.502 l 0,0 z");
const freeLineDrawer = (() => {
    let snapshot;//全局一份
    return (shape, div, x, y) => {
        const self = canvasRectangleDrawer(shape, div, x, y);
        self.type = "freeLineDrawer";
        self.dynamicCanvas = self.createElement("canvas", "dynamicCanvas:" + shape.id);
        self.dynamicCanvas.style.position = "absolute";
        self.dynamicCanvas.style.zIndex = 900;
        self.dynamicCanvas.style.left = self.dynamicCanvas.style.top = "0px";
        (self.dynamicCanvas.parentNode !== self.parent) && self.parent.appendChild(self.dynamicCanvas);

        let runningData;

        const calculateLineData = (fmla, line) => {
            return fmla.replaceAll(/(-?\d+(?:\.\d+)?)x/g, (m, g) => line.bound.x + parseInt(g) * line.scale[0])
                .replaceAll(/(-?\d+(?:\.\d+)?)y/g, (m, g) => line.bound.y + parseInt(g) * line.scale[1])
                .replaceAll(/(-?\d+(?:\.\d+)?)dx/g, (m, g) => parseInt(g) * line.scale[0])
                .replaceAll(/(-?\d+(?:\.\d+)?)dy/g, (m, g) => parseInt(g) * line.scale[1]);

        };
        const createSinglePathData = line => {
            const pathData = {};
            pathData.color = line.color;
            pathData.width = line.width;
            pathData.backColor = line.backColor;
            pathData.rotate = line.rotate;
            if (line.fmla) {
                pathData.metaData = calculateLineData(line.fmla, line);
            } else {
                line.points.forEach((p, i) => {
                    const x = p[0], y = p[1];
                    if (i === 1) {
                        pathData.metaData = "M" + x + " " + y;
                    }
                    if (i > 1) {
                        pathData.metaData += " L" + x + " " + y;
                    }
                });
            }
            pathData.data = new Path2D(pathData.metaData);
            pathData.bound = line.bound;
            return pathData;
        };

        const createStaticPathData = (bound) => {
            const pathsData = [];
            //new lines
            const newLines = shape.lines.filter(l => l.isNew);
            newLines.forEach(line => {
                line.pathData = createSinglePathData(line);
                delete line.isNew;
                pathsData.push(line.pathData);
            })
            if (pathsData.length > 0) {
                pathsData.isNew = true;
                return pathsData;
            }

            shape.lines.forEach(line => {
                if (line.selected && shape.isFocused) return;
                if (!line.points) return;
                if (!line.pathData || line.isUpdated) {
                    line.pathData = createSinglePathData(line);
                    delete line.isUpdated;
                }
                if (!isRectInteractRect(line.bound, bound)) return;
                pathsData.push(line.pathData);
            })
            return pathsData;
        };

        const createDynamicPathData = () => {
            const pathsData = [];
            if (shape.isFocused) {
                shape.lines.forEach(line => {
                    if (!line.points) return;
                    if (!line.selected) return;
                    delete line.pathData;
                    const pathData = createSinglePathData(line);
                    pathsData.push(pathData);
                })
            }
            return pathsData;
        };

        const resize = self.resize;
        self.resize = () => {
            const size = resize.apply(self);
            // compareAndSet(self.dynamicCanvas, 'id', "dynamic:" + shape.id);
            // resizeDynamic(size)
            // self.size = size;
            return size;
        };
        // const resizeCanvas = self.resizeCanvas;
        // self.resizeCanvas = size => {
        //     resizeCanvas.call(self, size);
        //     self.canvas.style.left = self.canvas.style.top = "0px";
        // };

        // const resizeDynamic = size => {
        //     self.updateCanvas(size.width, size.height, 'dynamicCanvas');
        //     self.updateIfChange(self.dynamicCanvas.style, 'left', self.canvas.style.left, 'dynamic_left');
        //     self.updateIfChange(self.dynamicCanvas.style, 'top', self.canvas.style.top, 'dynamic_top');
        //     self.updateIfChange(self.dynamicCanvas.style, 'width', self.canvas.style.width, 'dynamic_width');
        //     self.updateIfChange(self.dynamicCanvas.style, 'height', self.canvas.style.height, 'dynamic_height');
        // }

        const updateCanvas = self.updateCanvas;
        self.updateCanvas = (width, height, canvasName) => {
            // if(shape.page.isMouseDown()) return;
            return updateCanvas.call(self, width, height, canvasName);
        };

        const translate = (context, func, ox, oy) => {
            ox = ox === undefined ? 0 : ox;
            oy = oy === undefined ? 0 : oy
            context.save();
            context.translate(shape.page.x + ox + shape.margin, shape.page.y + oy + shape.margin);
            try {
                func(context)
            } finally {
                context.restore();
            }
        };

        const drawConnectors = self.drawConnectors;
        self.drawConnectors = (context, condition) => {
            if (!shape.lines.contains(l => l.selected === true)) return;
            context.save();
            if (self.tranformData && self.tranformData.degree) {
                const dx = self.tranformData.x - shape.width / 2 - shape.x;
                const dy = self.tranformData.y - shape.height / 2 - shape.y;
                context.translate(dx, dy);
                context.rotate(self.tranformData.degree);
                context.translate(-dx, -dy);
            }
            drawConnectors.call(self, context, condition);
            context.restore();
        };

        // const tranformData = {};
        self.customizedDrawFocus = (context, x, y, width, height) => {
            if (!shape.lines.contains(l => l.selected === true)) return;
            context.save();
            if (self.tranformData && self.tranformData.degree) {
                const dx = self.tranformData.x - shape.width / 2 - shape.x;
                const dy = self.tranformData.y - shape.height / 2 - shape.y;
                context.translate(dx, dy);
                // context.translate(0,0);
                context.rotate(self.tranformData.degree);
                x -= dx;
                y -= dy;
            }
            context.dashedRect(x, y, width, height, 2, 1, shape.page.focusFrameColor);
            context.restore();
        };

        self.drawRunning = (degree, x, y) => {
            self.tranformData = { degree, x, y };

            const context = self.dynamicCanvas.getContext("2d");
            // context.clearRect(0, 0, self.dynamicCanvas.width / shape.page.scaleX, self.dynamicCanvas.height / shape.page.scaleY)
            context.clearRect(0, 0, self.dynamicCanvas.clientWidth, self.dynamicCanvas.clientHeight);

            const pad = 10;
            if (!shape.closed) {
                if (!runningData) return;
                const dx = runningData.bound.x - pad + shape.page.x;
                const dy = runningData.bound.y - pad + shape.page.y
                self.dynamicCanvas.style.transform = " translate(" + dx + "px," + dy + "px)";
                // self.dynamicCanvas.style.left = (runningData.bound.x - pad + shape.page.x) + "px";
                // self.dynamicCanvas.style.top = (runningData.bound.y - pad + shape.page.y) + "px";
                self.updateCanvas((runningData.bound.width + pad * 2), (runningData.bound.height + pad * 2), "dynamicCanvas");

                translate(context, context => {

                    context.beginPath();
                    context.strokeStyle = shape.borderColor;
                    context.lineWidth = shape.lineWidth;
                    context.stroke(new Path2D(runningData.path));
                }, -runningData.bound.x + pad - shape.page.x, -runningData.bound.y + pad - shape.page.y);
            } else {
                const frame = (() => {
                    const lines = shape.lines.filter(l => {
                        if (l.selected) {
                            l.tmpBound = shape.getLineBound(l);
                            return true
                        }
                    });
                    if (lines.length == 0) {
                        return { x: 0, y: 0, width: 10, height: 10 };
                    }
                    const bound = {};
                    const offset = shape.lineWidth;
                    bound.x = lines.min(l => l.tmpBound.x) - offset;
                    bound.y = lines.min(l => l.tmpBound.y) - offset;
                    bound.width = lines.max(l => l.tmpBound.x + l.tmpBound.width) - bound.x + 2 * offset;
                    bound.height = lines.max(l => l.tmpBound.y + l.tmpBound.height) - bound.y + 2 * offset;
                    lines.forEach(l => delete l.tmpBound);
                    return bound;
                })();

                const dx = frame.x - pad + shape.page.x;
                const dy = frame.y - pad + shape.page.y;
                self.dynamicCanvas.style.transform = " translate(" + dx + "px," + dy + "px)";

                // self.dynamicCanvas.style.left = (frame.x - pad + shape.page.x) + "px";
                // self.dynamicCanvas.style.top = (frame.y - pad + shape.page.y) + "px";
                self.updateCanvas((frame.width + pad * 2), (frame.height + pad * 2), "dynamicCanvas");
                translate(context, context => {
                    createDynamicPathData().forEach(d => {
                        if (d.rotate) {
                            context.save();
                            context.translate(d.bound.x + d.bound.width / 2, d.bound.y + d.bound.height / 2);
                            context.rotate(d.rotate)
                            context.translate(-d.bound.x - d.bound.width / 2, -d.bound.y - d.bound.height / 2);
                        }
                        context.beginPath();
                        context.strokeStyle = "rgba(0,100,200,0.2)";
                        context.lineWidth = d.width + 4;
                        context.stroke(d.data);
                        context.beginPath();
                        context.strokeStyle = d.color;
                        context.lineWidth = d.width;
                        context.stroke(d.data);
                        if (d.backColor) {
                            context.fillStyle = d.backColor;
                            context.fill(d.data);
                        }
                        d.rotate && context.restore();
                    })
                }, -frame.x + pad - shape.page.x, -frame.y + pad - shape.page.y);
            }

            self.drawFocus();
        };

        self.erase = (x, y, width, height) => {
            self.canvases.clear(x - 1 + shape.page.x, y - 1 + shape.page.y, width + 2, height + 2);
            // translate(self.context, context => {
            //     context.clearRect(x - 1, y - 1, width + 2, height + 2);
            // })
            snapshot && snapshot.clear(x - 1, y - 1, width + 2, height + 2);
            // self.dirty();
        };

        self.clearCanvas = () => {
            const frame = shape.getFrame();
            // if (self.runningBound) {
            const x = frame.x + shape.page.x + shape.margin;
            const y = frame.y + shape.page.y + shape.margin;
            const width = frame.width;
            const height = frame.height;
            self.canvases.clear(x, y, width, height);
            (!shape.refreshed) && snapshot && snapshot.clear(x - shape.page.x, y - shape.page.y, width, height);
        };
        const createCanvasSlice = (x, y, width, height, name, parent) => {//newSnapshotPart
            const canvas = self.createElement("canvas", name + " " + shape.id);
            parent.appendChild(canvas);
            // canvas.id = "snapshot:" + name;
            canvas.style.position = "absolute";
            canvas.style.left = x + "px";
            canvas.style.top = y + "px";
            canvas.style.visibility = "hidden";
            canvas.width = width;
            canvas.height = height;
            const context = canvas.getContext("2d")
            //pixelRateAdapter(context, shape.page.scaleX, shape.page.scaleY, false);
            context.lineCap = "round";
            context.lineJoin = "round";
            // context.translate(-x, -y);
            return {
                canvas, x, y,
                width: shape.width / shape.slices,
                height: shape.height / shape.slices
            }
        }
        // let snapshot;
        const createSnapshot = () => {
            const width = shape.page.width / shape.slices;
            const height = shape.page.height / shape.slices;
            let snap = [];
            for (let i = 0; i < shape.slices * 2; i++) {
                for (let j = 0; j < shape.slices * 2; j++) {
                    const slice = createCanvasSlice(i * width, j * height, width, height, ("snapshot:" + i + "-" + j), self.parent.parentNode);
                    slice.canvas.getContext("2d").translate(-slice.x, -slice.y);
                    snap.push(slice);
                }
            }
            snap.visible = () => {
                snap.forEach(s => {
                    s.canvas && (s.canvas.style.visibility = "visible");
                })
                shape.page.shapes.forEach(s => s.isType("freeLine") && s.canvases && s.canvases.invisible());
            };

            snap.inVisible = () => {
                snap.forEach(s => {
                    s.canvas && (s.canvas.style.visibility = "hidden");
                })
                shape.page.shapes.forEach(s => s.isType("freeLine") && s.canvases && s.canvases.visible());
            };
            snap.clear = (x, y, width, height) => {
                snap.forEach(s => {
                    s.canvas.getContext("2d").clearRect(x, y, width, height);
                });
            };

            snap.draw = (pathsData, frame) => {
                const drawLine = (context, d) => {
                    context.beginPath();
                    if (d.rotate) {
                        context.save();
                        context.translate(d.bound.x + d.bound.width / 2, d.bound.y + d.bound.height / 2);
                        context.rotate(d.rotate)
                        context.translate(-d.bound.x - d.bound.width / 2, -d.bound.y - d.bound.height / 2);
                    }
                    context.strokeStyle = d.color;
                    context.lineWidth = d.width;
                    context.stroke(d.data);
                    if (d.backColor) {
                        context.fillStyle = d.backColor;
                        context.fill(d.data);
                    }

                    d.rotate && context.restore();
                }
                if (pathsData.isNew) {
                    snap.forEach(s => {
                        const context = s.canvas.getContext("2d");
                        // translate(context, context => {
                        pathsData.forEach(d => {
                            if (isRectInteractRect(d.bound, s)) {
                                drawLine(s.canvas.getContext("2d"), d);
                            }
                            // context.beginPath();
                            // context.strokeStyle = d.color;
                            // context.lineWidth = d.width;
                            // context.stroke(d.data);
                        })
                        // })

                    });
                    return;
                }

                setTimeout(() => {
                    snap.forEach(s => {
                        const context = s.canvas.getContext("2d");
                        context.save();
                        context.beginPath();
                        context.rect(frame.x - 1, frame.y - 1, frame.width + 2, frame.height + 2);
                        context.clip();
                    });
                    // context.strokeStyle = "rgb(" + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + ")";
                    pathsData.forEach(d => {
                        snap.forEach(s => {
                            if (isRectInteractRect(d.bound, s)) {
                                drawLine(s.canvas.getContext("2d"), d);
                            }

                        });
                    })

                    snap.forEach(s => {
                        s.canvas.getContext("2d").restore();

                    });
                }, 2)
            };
            snap.transform = transform => {
                if (snap.transformStr === transform) return;
                snap.transformStr = transform;
                snap.forEach(s => {
                    s.canvas.style.transform = transform;
                });
            };
            return snap;
        };

        const createCanvases = () => {
            const width = shape.width / shape.slices;
            const height = shape.height / shape.slices;
            const canvases = [];
            for (let i = 0; i < shape.slices; i++) {
                for (let j = 0; j < shape.slices; j++) {
                    const slice = createCanvasSlice(i * width, j * height, width, height, ("freeline canvas slice:" + i + "-" + j), self.parent);
                    slice.canvas.style.visibility = "visible";
                    slice.xIndex = i;
                    slice.yIndex = j;
                    canvases.push(slice);
                }
            }
            canvases.visible = () => {
                canvases.forEach(slice => slice.canvas.style.visibility = "visible");
            };
            canvases.inVisible = () => {
                canvases.forEach(slice => slice.canvas.style.visibility = "hidden");
            };

            canvases.resize = (size) => {
                canvases.forEach(slice => {
                    let x = slice.xIndex * size.width / shape.slices;
                    let y = slice.yIndex * size.height / shape.slices;
                    x = x > 0 ? x - 1 : x;
                    y = y > 0 ? y - 1 : y;
                    if (slice.x === x && slice.y === y && slice.width === size.width && slice.height === size.height) return;
                    slice.x = x;
                    slice.y = y;
                    slice.width = size.width;
                    slice.height = size.height;
                    slice.canvas.style.left = x + "px";
                    slice.canvas.style.top = y + "px";
                    slice.canvas.width = size.width / shape.slices;
                    slice.canvas.height = size.height / shape.slices;
                    pixelRateAdapter(slice.canvas.getContext("2d"), shape.page.scaleX, shape.page.scaleY);
                });
            };

            canvases.clear = (x, y, width, height) => {
                canvases.forEach(c => {
                    // if (width > 20) {
                    c.canvas.getContext("2d").clearRect(x - c.x, y - c.y, width, height);
                    // } else {
                    //     const context = c.canvas.getContext("2d");
                    //     context.fillStyle = "red";
                    //     context.fillRect(x - c.x, y - c.y, width, height);
                    // }
                });
            };

            canvases.draw = (pathsData, frame) => {
                const drawLine = (context, d) => {
                    context.beginPath();
                    if (d.rotate) {
                        context.save();
                        context.translate(d.bound.x + d.bound.width / 2, d.bound.y + d.bound.height / 2);
                        context.rotate(d.rotate)
                        context.translate(-d.bound.x - d.bound.width / 2, -d.bound.y - d.bound.height / 2);
                    }
                    context.strokeStyle = d.color;
                    context.lineWidth = d.width;
                    context.stroke(d.data);
                    if (d.backColor) {
                        context.fillStyle = d.backColor;
                        context.fill(d.data);
                    }
                    d.rotate && context.restore();
                }
                canvases.forEach(slice => {
                    const context = slice.canvas.getContext("2d");
                    if (pathsData.isNew) {
                        translate(context, context => {
                            pathsData.forEach(d => {
                                if (isRectInteractRect(d.bound, slice)) {
                                    drawLine(context, d);
                                }
                            })
                        }, -slice.x, -slice.y)
                    } else {
                        context.save();
                        context.rect(frame.x + shape.page.x + shape.margin - 1 - slice.x, frame.y + shape.page.y + shape.margin - 1 - slice.y, frame.width + 2, frame.height + 2);
                        context.clip();
                        context.strokeStyle = "rgb(" + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + ")";
                        context.lineCap = "round";
                        context.lineJoin = "round";
                        translate(context, context => {
                            pathsData.forEach(d => {
                                if (isRectInteractRect(d.bound, slice)) {
                                    drawLine(context, d);
                                }
                            })
                        }, -slice.x, -slice.y)
                        context.restore();
                    }
                })

            }

            return canvases;
        }

        self.resizeCanvas = size => {
            // if (self.canvas !== self.dynamicCanvas) {
            //     self.canvas.remove();
            //     self.canvas = self.dynamicCanvas;
            // }
            // const canvasSize = self.updateCanvas(size.width, size.height, 'canvas');
            // const dx = canvasSize.width - size.width;
            // const dy = canvasSize.height - size.height;
            // self.updateIfChange(self.canvas.style, 'left', "0px", 'canvas_left');
            // self.updateIfChange(self.canvas.style, 'top', "0px", 'canvas_top');
            // self.updateIfChange(self.canvas.style, 'opacity', shape.get("globalAlpha"), 'canvas_opacity');
            self.canvases.resize(size);
        };

        self.draw = (() => {
            const drawStatic = (context, x, y) => {
                snapshot && snapshot.inVisible();
                if (!shape.closed) return;
                const frame = shape.getFrame();
                const pathsData = createStaticPathData(frame);
                self.canvases.draw(pathsData, frame);
                runningData = undefined;
                console.log("page.x:" + shape.page.x + ",page.y:" + shape.page.y + ",page.scale:" + shape.page.scaleX);
                return pathsData;
            };

            const drawSnapShot = (() => {
                return pathsData => {
                    if (!shape.enableCache) return;
                    (!snapshot) && (snapshot = createSnapshot());
                    let frame = shape.getFrame();
                    if (!shape.lines.contains(l => l.selected)) {
                        pathsData = [];
                        shape.lines.forEach(l => pathsData.push(l.pathData))
                        frame = { x: 0, y: 0, width: 2 * shape.width, height: 2 * shape.height };
                    }
                    snapshot.draw(pathsData, frame);

                };
            })();

            const displaySnapShot = context => {
                if (!snapshot) return;
                snapshot.visible();
                snapshot.transform(" translate(" + (shape.page.x) + "px," + (shape.page.y) + "px)");
            };

            return () => {
                !self.canvases && (self.canvases = createCanvases());
                self.resize();
                self.clearCanvas();
                if (shape.refreshed === true && shape.enableCache) {
                    displaySnapShot();
                    shape.refreshed = false;
                } else {
                    drawSnapShot(drawStatic());
                    self.drawRunning();
                }
            }

        })();


        self.addPoint = (x, y) => {
            if (!runningData) {
                runningData = {};
                runningData.path = "M" + x + " " + y;
                runningData.bound = { x, y, width: 0, height: 0 };
            } else {
                runningData.path += " L" + x + " " + y;
                const dx = runningData.bound.x - x;
                if (dx > 0) {
                    runningData.bound.x = x;
                    runningData.bound.width += dx;
                }
                const dy = runningData.bound.y - y;
                if (dy > 0) {
                    runningData.bound.y = y;
                    runningData.bound.height += dy;
                }
                const dw = x - runningData.bound.x - runningData.bound.width;
                if (dw > 0) {
                    runningData.bound.width += dw;
                }
                const dh = y - runningData.bound.y - runningData.bound.height;
                if (dh > 0) {
                    runningData.bound.height += dh;
                }
                self.drawRunning();
            }
        };

        return self;
    };
})();


const freeShape = (id, x, y, width, height, parent) => {
    const self = freeLine(id, x, y, width, height, parent);
    self.definedType = "freeLine";
    self.backColor = "orange";
    self.buildLine = () => {
        return {
            id: self.id,
            fmla: self.buildPathfmla(), points: [[x, y], [x + width, y], [x + height, y + height], [x, y + height]], delta: [0, 0], scale: [1, 1], rotate: 0,
            time: new Date().getTime(), backColor: self.backColor,
            color: self.borderColor, width: self.lineWidth, from: self.page.graph.session.name, isNew: true
        };

    };
    self.initialize = () => {
        const result = self.done()
        result[1].preSelected = true;
        result[0].select();
    };
    self.getPoints = () => [];
    return self;
}

const freeRect = (id, x, y, w, h, parent) => {
    const WIDTH = 100;
    const self = freeShape(id, x, y, WIDTH, WIDTH, parent);
    self.buildPathfmla = () => {
        return `M 0x 0y L ${WIDTH}x 0y L ${WIDTH}x ${WIDTH}y L 0x ${WIDTH}y z`;
    }
    return self;
}

const freeEllipse = (id, x, y, w, h, parent) => {
    const WIDTH = 100;
    const self = freeShape(id, x, y, WIDTH, WIDTH, parent);
    self.buildPathfmla = () => {
        return `M ${WIDTH / 2}x, 0y a ${WIDTH / 2}dx,${WIDTH / 2}dy 0 1,0 1,0 z`;
    };

    return self;
}

export { freeLine, freeRect, freeEllipse };