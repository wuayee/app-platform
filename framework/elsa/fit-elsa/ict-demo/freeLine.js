import {rectangle} from './rectangle.js';
import {canvasRectangleDrawer} from './drawers/rectangleDrawer.js';
import {
    compareAndSet,
    getRotatedCoordinate,
    isPointInRect,
    isRectInteractRect,
    pixelRateAdapter
} from '../common/util.js';
import {CURSORS} from '../common/const.js';
import {addFreeLineCommand} from "./commands.js";


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
    self.backColor = self.focusBackColor = "transparent";
    self.borderWidth = 0;
    self.lineWidth = 3;
    self.lines = [];
    self.x = 0;
    self.y = 0;
    self.margin = 0;
    self.enableCache = true;
    self.penMode = "solid";//solid,highlight,brush
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
        if (self.page.handDrawingAlpha < 0.999) {
            self.penMode = "highlight";
        }
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
        if (self.x !== x || self.y !== y || self.width !== width || self.height !== height) {
            self.refreshed = true;
        }
        // const width = self.page.width / self.page.scaleX + self.page.x;
        // const height = self.page.height / self.page.scaleY + self.page.y;

        // width > self.width && (self.width = width);
        // height > self.height && (self.height = height);
        // self.x = 0;
        // self.y = 0;
    };

    self.serialized = serialized => {
        setTimeout(()=>{
            serialized.lines = JSON.parse(JSON.stringify(serialized.lines));
            serialized.lines.forEach(line => {
                delete line.pathData;
            })
        }, 1000);
    };

    self.deSerialized = () => {
        if (self.lines.length === 0) {
            self.remove();
            return;
        }
        self.lines.forEach(l => {
            boundLine(l);
            delete l.pathData;
        });
        self.borderWidth = 0;
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
        function bound(lines) {
            const bound = {};
            const offset = self.lineWidth;
            bound.x = lines.min(l => l.bound.x) - offset;
            bound.y = lines.min(l => l.bound.y) - offset;
            bound.width = lines.max(l => l.bound.x + l.bound.width) - bound.x + 2 * offset;
            bound.height = lines.max(l => l.bound.y + l.bound.height) - bound.y + 2 * offset;
            // bound.x += self.x;
            // bound.y += self.y;
            return bound;
        }
        if (self.deleteLines) {
            let deleteLines = self.deleteLines;
            delete self.deleteLines;
            return bound(deleteLines);
        }
        if (self.lines.contains(l => l.isNew)) return {x: 0, y: 0, width: 0.1, height: 0.1};
        if (self.page.graph.inUndo || self.page.graph.inRedo) {
            return {x: self.x, y: self.y, width: self.width, height: self.height};
        }
        const lines = self.lines.filter(l => l.selected);
        if (lines.length === 0) {
            return getFrame.call(self);
        }
        return bound(lines);
    };

    self.isLocked = () => false;

    const onMouseDown = self.onMouseDown;
    self.onMouseDown = position => {
        // if (self.eraser) return;
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
        if (self.mousedownConnector || self.indraggingArea) return;
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
            if (l.selected) l.isNew = true;
            delete l.selected
        });
        unSelect.call(self);
    };

    self.contains = (x, y) => {
        if (self.page.eraser) return false;
        const step = 4;
        // const frame = self.getFrame();
        // const isInFrame = isPointInRect({ x, y }, frame)
        // if (self.lines.contains(l => {
        //     l.selected && (l.preSelected = true)
        //     return l.selected;
        // }) && isInFrame) {
        //     return true;
        // }

        // const frame_inner = { x: frame.x + step, y: frame.y + step, width: frame.width - 2 * step, height: frame.height - 2 * step };
        // const frame_outer = { x: frame.x - step, y: frame.y - step, width: frame.width + 2 * step, height: frame.height + 2 * step };
        // const point = { x, y };
        // self.indraggingArea = isPointInRect(point, frame_outer) && !isPointInRect(point, frame_inner) && self.isFocused
        // if (self.indraggingArea) return true;

        const rect = {x: x - step, y: y - step, width: 2 * step, height: 2 * step};
        return self.inSelection(rect);
    };

    self.inSelection = rect => {
        return false;
        let selected = false;
        self.lines.forEach(l => {
            delete l.preSelected;
            if (!isRectInteractRect(l.bound, rect)) return;
            for (let i = 0; i < l.points.length; i++) {
                if (isPointInRect({x: l.points[i][0], y: l.points[i][1]}, rect)) {
                    l.preSelected = true;
                    selected = true;
                    break;
                }
            }
        })
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
            position.context.shapes.push({shape, preValue: shape.lines});
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
        if (self.page.eraser) {
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
        let eraser = self.page.eraser;
        if (eraser) {
            self.erase(position);
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
        const pointNW = {x: x, y: y};
        const pointNE = {x: x + width, y: y};
        const pointSE = {x: x + width, y: y + height};
        const pointSW = {x: x, y: y + height};

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
            return isPointInRect(point, {x, y, width, height}) && !self.onBorder(point);
        }

        /**
         * 是否包含在矩形中(包含边界).
         *
         * @param point 坐标点.
         * @returns {*|boolean} true/false.
         */
        self.contains = (point) => {
            return isPointInRect(point, {x, y, width, height});
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
            if (self.onSegment({x, y}) && otherLine.onSegment({x, y})) {
                return {x, y}
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
        //todo
    };

    self.paste = data => {
        return true;
        //todo
    };
    self.keyPressed = e => {
        if ((e.ctrlKey || e.metaKey) && (e.code === "KeyD")) {
            return false;
        }
    }

    // self.getIndex = () => {
    //     self.index = self.page.shapes.length - 1 + Z_INDEX_OFFSET;
    //     return self.index;
    // };


    self.erase = (position, width, height) => {
        self.drawer.erase(position.x - width / 2, position.y - height / 2, width, height);
    };

    self.addPoint = (x, y, collaboration) => {
        appendToBuffer({x, y});
        const pt = getAveragePoint(0);
        if (pt) {
            points.push([pt.x, pt.y]);
            if (points.length >= 2) {
                self.drawer.addPoint(pt.x, pt.y);
            }
        }

        (!collaboration) && self.page.graph.collaboration.invoke({
            method: "add_freeline_point",
            page: self.page.id,
            shape: self.id,
            value: {x, y},
            mode: self.page.mode
        }, () => {
        });

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
            self.local = {action: ACTIONS.UPDATE, lines};
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
                    boundLine(l);
                    lines.push(l);
                })
                const dirty = position.context.shapes.find(s => s.shape === self);
                dirty.lines.value = JSON.parse(JSON.stringify(lines));
                // self.render();
            };
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

    self.getFocused = () => self.lines.filter(l => l.selected);
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
        self.local = {action: ACTIONS.DELETE, lines};
        return [self];
        // removeFreeLineCommand(self.page, [{ shape: self, lines }]);
    };
    self.undoRemove = (page, index, focused) => {
        self.lines.forEach(l => l.selected = false);
        focused.forEach(l => l.selected = true);
        self.lines.push.apply(self.lines, focused);
        if (!self.isFocused) {
            self.isFocused = true;
        } else {
            self.render();
        }
        self.local = {action: ACTIONS.ADD, lines: focused};
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
            l.selected = action.context.lines.contains(l1 => l1 === l.id);
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
        // self.render();
    }

    self.resizeRelative = (x, y, rx, ry) => {
        let pagePathMap = self.page.graph.pathCache.get(self.page.id);
        const lines = [];
        self.lines.forEach(l => {
            if (!l.selected) return;
            if (pagePathMap) {
                pagePathMap.delete(l.id);
            }
            l.points.forEach(p => {
                p[0] = x + (p[0] - x) * rx;
                p[1] = y + (p[1] - y) * ry;
            })
            l.bound.x = x + (l.bound.x - x) * rx;
            l.bound.y = y + (l.bound.y - y) * ry;
            l.bound.width = l.bound.width * rx;
            l.bound.height = l.bound.height * ry;
            delete l.pathData;
            lines.push(l);
        })
        // self.local = {action: ACTIONS.UPDATE, lines};
        delayDrawRunning();
    }

    self.moveTo = (x, y) => {
        const dx = x - self.x;
        const dy = y - self.y;
        const lines = [];
        let pagePathMap = self.page.graph.pathCache.get(self.page.id);
        self.lines.forEach(l => {
            if (!l.selected) return;
            if (pagePathMap) {
                pagePathMap.delete(l.id);
            }
            l.points.forEach(p => {
                p[0] += dx;
                p[1] += dy;
            })
            l.bound.x += dx;
            l.bound.y += dy;
            delete l.pathData;
            lines.push(l);
        })
        // self.local = {action: ACTIONS.UPDATE, lines};
        delayDrawRunning();
    };

    const delayDrawRunning = () => {
        if (self.isDrawRunning) {
            return;
        }
        self.isDrawRunning = true;
        setTimeout(() => {
            self.drawer.drawRunning();
            self.isDrawRunning = false;
        }, 5)
    }

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
            funcs[localAction.action](localAction.lines);
            self.render();
        };
    })();

    self.acceptLine = line => {
        self.lines.forEach(l => delete l.selected);
        self.lines.push(line);
        boundLine(line);//确认边框，提高选中性能
        self.invalidate();
        delete line.isNew;
        delete line.selected;
        addFreeLineCommand(self.page, [{ shape: self, lines: [line] }]);
        if (line.alpha > 0.999) {
            self.page.eraserCacheAdd([line]);
        }
        // self.eraserExecutor.add(syncLine);
        //
        // if (self.isHighLighter()) {
        //     return;
        // }
    };

    const boundLine = line => {
        const x = line.points.min(p => p[0]);
        const y = line.points.min(p => p[1]);
        const width = line.points.max(p => p[0]) - x;
        const height = line.points.max(p => p[1]) - y;
        line.bound = {x, y, width, height};
        (!line.id) && (line.id = self.page.idGenerator());
        self.page.handWritingChanged && self.page.handWritingChanged(line);
        // delete line.pathData;
        return line;
    };

    self.done = (inCollaboration) => {
        if (self.closed === true) return;
        self.closed = true;

        if (!points || points.length < 2) {
            self.remove(true);
            return;
        }
        const line = {
            id: self.id,
            points,
            time: new Date().getTime(),
            color: self.get("borderColor"),
            width: self.lineWidth,
            alpha: self.globalAlpha,
            from: self.page.graph.session.name,
            isNew: true
        };
        // let free = self.page.shapes.find(s => s.isTypeof(self.page.freeLineType) && s.closed === true && s.id !== self.id);
        let free = self.page.determineLineHost(2, self.penMode);
        if (free) {
            free.acceptLine(line);
            self.remove(true);
        } else {
            self.acceptLine(line);
            free = self;
        }
        // self.closed = true;
        (!inCollaboration) && self.page.graph.collaboration.invoke({
            method: "freeline_done",
            page: free.page.id,
            shape: self.id,//free.id,
            mode: free.page.mode,
            value: {line, to: free.id}
        }, () => {
        });
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
    self.beginErase = () => {
        if (self.penMode === 'solid') {
            self.drawer.parent.style.display = "none";
        }
    }

    self.endErase = () => {
        if (self.penMode === 'solid') {
            self.drawer.parent.style.display = "block";
        }
    }
    return self;
};
//new Path2D("m 75.8672,80.4954 c -0.6572,-0.0938 -2.252,-0.4688 -2.627,-0.375 -1.0313,0.1875 0.375,1.501 -0.1875,1.876 -0.4688,0.1875 -1.0313,-0.0938 -1.501,0.0938 -0.1875,0.0938 0.0938,0.4688 -0.0938,0.6572 -0.9375,0.75 -2.4385,0.8438 -3.377,1.6875 -1.125,1.0322 2.0635,3.6582 0.6572,4.6904 -3.2832,2.3447 -1.2197,-1.5947 -3.377,1.501 -0.2813,0.375 0.8438,0.4688 1.0313,0.9375 0.1875,0.376 -0.9375,0.2822 -0.9375,0.6572 0,0.1875 0.9375,1.4072 0.5625,1.876 -1.0313,1.0313 -2.5322,-0.5635 -3.4707,-0.5635 -1.4063,0 1.3135,1.3135 -1.0313,0.6572 -0.6572,-0.1875 -1.501,-1.2197 -1.7822,-1.7822 -0.1875,-0.1875 -0.376,-0.751 -0.4697,-0.5625 -0.375,0.5625 -0.2813,1.2188 -0.6563,1.6875 -1.2188,1.5947 -2.9082,0 -4.3145,0.4697 -0.1875,0.0938 0.1875,0.4688 0,0.6563 -0.8447,0.4688 -2.5332,0.375 -3.377,0 0,0 0,-0.0938 0.0938,-0.0938 0.4688,-0.4688 1.0313,-0.9375 1.2197,-1.4072 3.2822,0.1875 -0.4697,-2.0635 -1.4072,-2.626 -0.6563,-0.375 0.375,-1.5947 0.1875,-2.0635 -0.1875,-0.4697 -1.6885,0.8438 -1.3135,-0.376 0.2813,-0.8438 2.0635,-1.5938 1.4072,-2.1572 -0.4688,-0.375 -2.627,-0.1875 -2.4385,-1.3125 0.1875,-1.501 2.5322,-1.126 2.7197,-3.002 -0.0938,0 -0.0938,0 -0.1875,0 0.5625,0.0938 1.126,0.1875 1.7822,0.2813 0.75,0.0938 1.501,0.6563 2.251,0.75 0.751,0.0938 1.501,-0.4688 2.252,-0.375 0.4688,0.0938 0.75,0.751 1.2188,0.751 0.1875,0.0938 0.0938,-0.4697 0.2813,-0.5635 0.4697,-0.4688 1.2197,-0.6563 1.6885,-1.2188 0.376,-0.2822 0.0938,-0.9385 0.376,-1.2197 1.7813,-1.4072 3.6582,0.375 5.3457,-0.375 0.5635,-0.2813 0.6572,-1.126 1.2197,-1.3135 0.0938,-0.0938 3.1895,0.375 3.2832,0.2813 0.2813,-0.0938 -0.2813,-0.4688 -0.375,-0.75 -0.2813,-1.501 0.8438,-1.876 2.251,-1.876 0.3752,0 1.1262,2.9072 3.0959,4.502 l 0,0 z");
const freeLineDrawer = (shape, div, x, y) => {
    const self = canvasRectangleDrawer(shape, div, x, y, {noText : true});
    self.type = "freeLineDrawer";
    self.dynamicCanvas = document.createElement("canvas");
    self.dynamicCanvas.style.position = "absolute";
    self.parent.appendChild(self.dynamicCanvas);

    let runningData;

    const createSinglePathData = (line, svgPathArray) => {
        const pathData = {};
        pathData.color = line.color;
        pathData.width = line.width;
        pathData.alpha = line.alpha;
        if (!shape.graph.pathCache) {
            shape.graph.pathCache = new Map();
        }
        let pagePathMap = shape.graph.pathCache.get(shape.page.id);
        if (!pagePathMap) {
            pagePathMap = new Map();
            shape.graph.pathCache.set(shape.page.id, pagePathMap);
        }
        let linePath = pagePathMap.get(line.id);
        if (!linePath) {
            if (!svgPathArray) {
                svgPathArray = new Array(line.points.length * 4);
            }
            let index = 0;
            line.points.forEach((p, i) => {
                const x = p[0], y = p[1];
                if (i === 0) {
                    svgPathArray[index++] = "M";
                    svgPathArray[index++] = x;
                    svgPathArray[index++] = " ";
                    svgPathArray[index++] = y;
                }
                if (i > 0) {
                    svgPathArray[index++] = " L";
                    svgPathArray[index++] = x;
                    svgPathArray[index++] = " ";
                    svgPathArray[index++] = y;
                }
            });
            for (;index < svgPathArray.length; ++index) {
                svgPathArray[index] = undefined;
            }
            linePath = new Path2D(svgPathArray.join(""));
            pagePathMap.set(line.id, linePath);
        }
        pathData.data = linePath;
        pathData.bound = line.bound;
        return pathData;
    };

    const createStaticPathData = () => {
        const pathsData = [];
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

        const bound = shape.getFrame();// self.runningBound ? self.runningBound : shape;
        let len = 0;
        shape.lines.forEach(line => {
            len = Math.max(len, line.points.length);
        })
        let svgPathArray = new Array(len * 4);
        shape.lines.forEach(line => {
            if (line.selected) return;
            if (!line.points) return;
            if (!line.pathData) {
                line.pathData = createSinglePathData(line, svgPathArray);
            }
            if (!isRectInteractRect(line.bound, bound)) return;
            pathsData.push(line.pathData);
        })
        return pathsData;
    };

    const createDynamicPathData = () => {
        const pathsData = [];
        shape.lines.forEach(line => {
            if (!line.points) return;
            if (!line.selected) return;
            delete line.pathData;
            const pathData = createSinglePathData(line);
            pathsData.push(pathData);
        })
        return pathsData;
    };

    // const containsBack = self.containsBack;
    // self.containsBack = (x, y) => {
    //     if (shape.eraser) {
    //         return true;
    //     } else {
    //         const line = self.lines.find(l => {
    //             if (!isPointInRect({ x, y }, l.bound)) return false;
    //             const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
    //         });

    //         //return containsBack.call(self, x, y);
    //     }
    // };

    // self.dirty = bound => self.dirtyBound = bound;
    const resize = self.resize;
    self.resize = () => {
        const size = resize.apply(self);
        compareAndSet(self.dynamicCanvas, 'id', "dynamic:" + shape.id);
        resizeDynamic(size)
        // self.size = size;
        return size;
    };
    const resizeCanvas = self.resizeCanvas;
    self.resizeCanvas = size => {
        resizeCanvas.call(self, size);
        self.canvas.style.left = self.canvas.style.top = "0px";
    };

    const resizeDynamic = size => {
        self.updateCanvas(size.width, size.height, 'dynamicCanvas');
        self.updateIfChange(self.dynamicCanvas.style, 'left', self.canvas.style.left, 'dynamic_left');
        self.updateIfChange(self.dynamicCanvas.style, 'top', self.canvas.style.top, 'dynamic_top');
        self.updateIfChange(self.dynamicCanvas.style, 'width', self.canvas.style.width, 'dynamic_width');
        self.updateIfChange(self.dynamicCanvas.style, 'height', self.canvas.style.height, 'dynamic_height');
    }

    const updateCanvas = self.updateCanvas;
    self.updateCanvas = (width, height, canvasName) => {
        if (shape.closed && shape.refreshed) {
            return;
        }
        if (canvasName !== 'canvas') {
            // todo 此处限制了dynamicCanvas的缩放，导致avd新增一笔时，笔画位置不对，待修复
            // if (shape.closed && shape.refreshed) {
            //     return;
            // }
            setTimeout(() => {
                updateCanvas.call(self, width, height, canvasName);
            }, 300);
            return;
        }
        // if(shape.page.isMouseDown()) return;
        return updateCanvas.call(self, width, height, canvasName);
    };

    const translate = (context, func) => {
        context.save();
        context.translate(shape.page.x + shape.get("margin"), shape.page.y + shape.get("margin"));
        // context.translate(shape.get("margin"), shape.get("margin"));
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
        self.tranformData = {degree, x, y};

        const context = self.dynamicCanvas.getContext("2d");
        // context.clearRect(0, 0, self.dynamicCanvas.width / shape.page.scaleX, self.dynamicCanvas.height / shape.page.scaleY)
        context.clearRect(0, 0, self.dynamicCanvas.clientWidth, self.dynamicCanvas.clientHeight);
        translate(context, context => {
            if (!shape.closed) {
                if (!runningData) return;
                context.beginPath();
                context.strokeStyle = shape.get("borderColor");
                context.lineWidth = shape.lineWidth;
                context.globalAlpha = shape.globalAlpha;
                context.lineCap = "round";
                context.lineJoin = "round";
                context.stroke(new Path2D(runningData));
            } else {
                createDynamicPathData().forEach(d => {
                    // context.beginPath();
                    // context.strokeStyle = "rgba(0,100,200,0.2)";
                    // context.lineWidth = d.width + 4;
                    // context.stroke(new Path2D(d.data));
                    // context.stroke(d.data);
                    context.beginPath();
                    context.strokeStyle = d.color;
                    context.lineWidth = d.width;
                    context.globalAlpha = d.alpha;
                    context.lineCap = "round";
                    context.lineJoin = "round";
                    // context.stroke(new Path2D(d.data));
                    context.stroke(d.data);
                })
            }
        })
        self.drawFocus();
        // if (shape.page.graph.inUndo || shape.page.graph.inRedo || !shape.isFocused) {
        //     delete self.runningBound;
        // } else {
        //     self.runningBound = shape.getFrame();
        // }
    };

    self.erase = (x, y, width, height) => {
        translate(self.context, context => {
            context.clearRect(x, y, width, height);
        })
        snapshot && snapshot.clear(x - 1, y - 1, width + 2, height + 2);
        // self.dirty();
    };

    const clearCnavas = self.clearCanvas;
    self.clearCanvas = context => {
        const frame = shape.getFrame();
        // if (self.runningBound) {
        const x = frame.x + shape.page.x + shape.get("margin");
        const y = frame.y + shape.page.y + shape.get("margin");
        const width = frame.width;
        const height = frame.height;
        context.clearRect(x, y, width, height);

        // (!shape.refreshed) && snapshotCanvas.getContext("2d").clearRect(x - shape.page.x, y - shape.page.y, width, height);
        (!shape.refreshed && !shape.transformed) && snapshot && snapshot.clear(x - shape.page.x, y - shape.page.y, width, height);

    };
    let snapshot;
    const createSnapshot = () => {
        const snap = {};
        const newSnapshotPart = (x, y, name) => {
            const canvas = document.createElement("canvas");
            self.parent.appendChild(canvas);
            canvas.id = "snapshot:" + name;
            canvas.style.position = "absolute";
            canvas.style.left = x + "px";
            canvas.style.top = y + "px";
            canvas.style.visibility = "hidden";
            canvas.width = shape.page.width;
            canvas.height = shape.page.height;
            const context = canvas.getContext("2d")
            pixelRateAdapter(context, 1, 1, false);
            context.lineCap = "round";
            context.lineJoin = "round";
            context.translate(-x, -y);
            return {
                canvas, x, y,
                width: shape.width,
                height: shape.height
            }
        };
        snap.leftTop = newSnapshotPart(0, 0, "left-top");
        snap.rightTop = newSnapshotPart(shape.width * shape.page.scaleX, 0, "right-top");
        snap.leftBottom = newSnapshotPart(0, shape.height * shape.page.scaleY, "left-bottom");
        snap.rightBottom = newSnapshotPart(shape.width * shape.page.scaleX, shape.height * shape.page.scaleY, "right-bottom");
        snap.visible = () => {
            if (snap.leftTop.canvas.style.visibility === "visible") return;
            snap.do(s => {
                s.canvas && (s.canvas.style.visibility = "visible");
            });
        };
        snap.inVisible = () => {
            if (snap.leftTop.canvas.style.visibility === "hidden") return;
            snap.do(s => {
                s.canvas && (s.canvas.style.visibility = "hidden");

            });
        };
        snap.clear = (x, y, width, height) => {
            snap.do(s => {
                s.canvas.getContext("2d").clearRect(x, y, width, height);
            });
        };
        snap.do = fn => {
            for (let f in snap) {
                if (!snap[f].canvas) continue;
                fn(snap[f]);
            }
        };
        snap.draw = (pathsData, frame) => {
            if (pathsData.isNew) {
                snap.do(s => {
                    const context = s.canvas.getContext("2d");
                    // translate(context, context => {
                    //
                    // })
                    pathsData.forEach(d => {
                        context.beginPath();
                        context.lineCap = "round";
                        context.lineJoin = "round";
                        context.strokeStyle = d.color;
                        context.lineWidth = d.width;
                        context.globalAlpha = d.alpha;
                        context.stroke(d.data);
                    })

                });
                return;
            }
            setTimeout(() => {
                snap.do(s => {
                    const context = s.canvas.getContext("2d");
                    context.save();
                    context.beginPath();
                    context.rect(frame.x - 1, frame.y - 1, frame.width + 2, frame.height + 2);
                    context.clip();
                });
                // context.strokeStyle = "rgb(" + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + ")";
                const drawLine = (context, d) => {
                    context.beginPath();
                    context.lineCap = "round";
                    context.lineJoin = "round";
                    context.strokeStyle = d.color;
                    context.lineWidth = d.width;
                    context.globalAlpha = d.alpha;
                    context.stroke(d.data);
                }
                pathsData.forEach(d => {
                    snap.do(s => {
                        if (isRectInteractRect(d.bound, s)) {
                            drawLine(s.canvas.getContext("2d"), d);
                        }

                    });
                })

                snap.do(s => {
                    s.canvas.getContext("2d").restore();

                });
            }, 500)
        };
        snap.transform = transform => {
            if (snap.transformStr === transform) return;
            snap.transformStr = transform;
            snap.do(s => {
                s.canvas.style.transform = transform;
            });
        };
        snap.inVisible();
        return snap;
    };
    self.drawStatic = (() => {
        // const isTransformed = (() => {
        //     let x = shape.page.x, y = shape.page.y, scale = shape.page.scaleX;
        //     return () => {
        //         if (x !== shape.page.x || y !== shape.page.y || scale !== shape.page.scaleX) {
        //             x = shape.page.x, y = shape.page.y, scale = shape.page.scaleX;
        //             return true;
        //         } else {
        //             return false;
        //         }
        //     };

        // })();
        const drawStatic = (context, x, y) => {
            context.canvas.style.visibility = "visible";
            // snapshotCanvas.style.visibility = "visible";
            snapshot && snapshot.inVisible();
            if (!shape.closed) return;
            const frame = shape.getFrame();
            const pathsData = createStaticPathData();
            if (pathsData.isNew) {
                translate(context, context => {
                    pathsData.forEach(d => {
                        context.beginPath();
                        context.lineCap = "round";
                        context.lineJoin = "round";
                        context.strokeStyle = d.color;
                        context.lineWidth = d.width;
                        context.globalAlpha = d.alpha;
                        context.stroke(d.data);
                    })
                })
                return pathsData;
            }

            context.save();
            context.rect(frame.x + shape.page.x - 1, frame.y + shape.page.y - 1, frame.width + 2, frame.height + 2);
            context.clip();
            // context.strokeStyle = "rgb(" + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + ")";
            context.lineCap = "round";
            context.lineJoin = "round";
                translate(context, context => {
                        pathsData.forEach(d => {
                                context.beginPath();
                                context.globalAlpha = d.alpha;
                                context.strokeStyle = d.color;
                                context.lineWidth = d.width;
                                context.globalAlpha = d.alpha;
                                // context.stroke(new Path2D(d.data));
                                context.stroke(d.data);
                        })
                })
            context.restore();
            runningData = undefined;
            // console.log("page.x:" + shape.page.x + ",page.y:" + shape.page.y + ",page.scale:" + shape.page.scaleX);
            return pathsData;
        };

        const drawSnapShot = (() => {
            // self.parent.appendChild(snapshotCanvas);
            // // snapshotCanvas.style.visibility = "hidden";
            // snapshotCanvas.id = "snapshot";
            // snapshotCanvas.style.position = "absolute";
            // snapshotCanvas.style.left = "0px";
            // snapshotCanvas.style.top = "0px";
            // const context = snapshotCanvas.getContext("2d");
            // snapshotCanvas.width = 2 * shape.page.width;
            // snapshotCanvas.height = 2 * shape.page.height;
            // pixelRateAdapter(context, 1, 1, false);

            return pathsData => {
                (!snapshot) && (snapshot = createSnapshot());
                if (!shape.enableCache) return;
                let frame = shape.getFrame();
                if (!pathsData.isNew && !shape.lines.contains(l => l.selected)) {
                    pathsData = [];
                    shape.lines.forEach(l => pathsData.push(l.pathData))
                    frame = {x: 0, y: 0, width: 2 * shape.width, height: 2 * shape.height};
                }
                // context.save();
                // context.beginPath();
                // // context.rect(frame.x + shape.page.x, frame.y + shape.page.y, frame.width, frame.height);
                // context.rect(frame.x, frame.y, frame.width, frame.height);
                // context.clip();
                // // context.strokeStyle = "rgb(" + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + "," + Math.floor(Math.random() * 255) + ")";
                // pathsData.forEach(d => {
                //     context.beginPath();
                //     context.strokeStyle = d.color;
                //     context.lineWidth = d.width;
                //     // context.stroke(new Path2D(d.data));
                //     context.stroke(d.data);
                // })
                // context.restore();
                snapshot.draw(pathsData, frame);

            };
        })();

        const displaySnapShot = context => {
            // snapshotCanvas.style.visibility = "visible";
            if (!snapshot) return;
            context.canvas.style.visibility = "hidden";
            snapshot.visible();
            snapshot.transform(" translate(" + (shape.page.x) + "px," + (shape.page.y) + "px)");
            // snapshotCanvas.style.transform = " translate(" + (shape.page.x) + "px," + (shape.page.y) + "px)";

            // context.clearRect(shape.x, shape.y, shape.width, shape.height);
            // context.drawImage(snapshotCanvas, shape.page.x, shape.page.y, snapshotCanvas.width * shape.page.scaleX / self.pixelRate.ratioX, snapshotCanvas.height * shape.page.scaleY / self.pixelRate.ratioY);
            // console.log("page.x:" + shape.page.x + ",page.y:" + shape.page.y + ",page.scale:" + shape.page.scaleX);
        };

        return (context, x, y) => {
            if (shape.refreshed === true && shape.enableCache) {
                displaySnapShot(context);
                shape.refreshed = false;
            } else {
                let pathsData = drawStatic(context, x, y);
                if (pathsData && !shape.transformed) {
                    drawSnapShot(pathsData);
                } else {
                    delete shape.transformed;
                }
                self.drawRunning();
            }
        };
    })();


    self.addPoint = (x, y) => {
        if (!runningData) {
            runningData = "M" + x + " " + y;
        } else {
            runningData += " L" + x + " " + y;
            self.drawRunning();
        }
    };
    return self;
}
export {freeLine};