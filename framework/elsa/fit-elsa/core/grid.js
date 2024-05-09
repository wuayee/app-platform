import { CURSORS, DIRECTION, FONT_WEIGHT, GRID_TOOL_WIDTH, SHAPE_HIT_TYPE, GRID_HEAD_WIDTH, GRID_HEAD_HEIGHT, GRID_CELL_WIDTH, GRID_CELL_HEIGHT } from '../common/const.js';
import { isPointInRect, isRectInteractRect, getInteractRect, showDialog, uuid, } from '../common/util.js';
import { command } from './commands.js';
import { connector } from './connector.js';
import { canvasDrawer } from './drawers/canvasDrawer.js';
import { hitRegion } from './hitRegion.js';
import { rectangle } from './rectangle.js';

const GRID_MODE = {
    SHEET: "sheet",
    TABLE: "table"
};
const ACTIONS = {
    ACTIONS: "actions",
    CELL_VALUE_CHANGE: "grid_cell_value_change",
    COLUMN_ADD_BEFORE: "column_add_before",
    COLUMN_ADD_AFTER: "column_add_after",
    COLUMN_DUPLICATE: "column_duplicate",
    COLUMN_DELETE: "column_delete",
    ROW_ADD_BEFORE: "row_add_before",
    ROW_ADD_AFTER: "row_add_after",
    ROW_DUPLICATE: "row_duplicate",
    ROW_DELETE: "row_delete",
    COLUMN_CHANGE: "column_change",
    ROW_CHANGE: "row_change"
};

/**
 * 新的表格，通过canvas绘制
 * 辉子 2022
 */
const grid = (id, x, y, width, height, parent) => {
    const self = rectangle(id, x, y, width, height, parent, gridDrawer);
    self.type = "grid";
    self.width = 700;
    self.height = 500;
    self.hideText = true;
    self.mode = GRID_MODE.SHEET;
    self.entangleId = self.page.graph.uuid();
    self.cells = [];//{r:row,c:column,cs:columnspan,rs:rowspan,v:value(can be function),b:bold,ff:fontfamliy,fs:fontsize,fc:fontcolor,bk:backcolor,wrap:1,va:vertical align,ha:horizontal align,f:flag(green),s:star(5),e:emphasized(1),p:priority(2),i:error(e)/warning(w)/information(i)}
    self.rows = {};//{index:{h:height,bc:backcolor}}
    // self.rowNum = 10;
    self.columns = {};//{index:{n:name,t:title,w:width,bc:backcolor}}
    // self.columnNum = 6;
    self.borders = [];//{r:row,c:column,d:h|v,clr:color,w:width}
    self.scrollX = 0;
    self.scrollY = 0;
    self.withColumnHead = true;
    self.withRowHead = true;
    self.pad = 3;
    self.padLeft = self.padRight = 5;
    self.scale = 1;
    self.headBorderColor = "dimgray";
    self.headBackColor = "whitesmoke";
    self.headTextColor = "dimgray";
    self.headWidth = GRID_HEAD_WIDTH;
    self.headHeight = GRID_HEAD_HEIGHT;
    self.cellWidth = GRID_CELL_WIDTH;
    self.cellHeight = GRID_CELL_HEIGHT;
    self.minWidth = 20;
    self.minHeight = 20;
    self.minRow = 0;
    self.minColumn = 0;
    self.maxRow = -1;
    self.maxColumn = -1;
    self.groups = [];//分组[fromIndex,toIndex,type,status,level];type:1列，0行;status:0缩，1展开;level:层级，从1开始
    self.frozens = {};//锁定{"rows":10,"columns":5} 10行前锁定，5列前锁定
    self.hiddens = {};//隐藏{"rows":[2,3,5],"columns":[6,7]} 2,3,5行隐藏，6,7列隐藏
    self.groupHiddens = { "rows": [], "columns": [] };
    self.enableAnimation = true;
    self.scrollStep = 10;

    /**
     * 暂时用self.cells里的所有数据，cells如果过大，产生性能问题，在cache这个对象里修改，解决性能问题
     * 参考注释掉的ranges
     */
    self.cache = (() => {
        const me = {};
        me.getRange = (rowFrom, colFrom, rowTo, colTo, filter) => {
            return self.cells.filter(c => c.c >= colFrom && c.c <= colTo && c.r >= rowFrom && c.r <= rowTo && (filter ? filter(c) : true));
        };
        me.find = filter => {
            return self.cells.find(c => filter(c));
        };
        me.filter = filter => {
            return self.cells.filter(c => filter(c));
        };
        me.forEach = filter => {
            self.cells.forEach(c => filter(c));
        };
        me.push = cell => {
            self.cells.push(cell);
        };
        me.remove = filter => {
            self.cells.remove(c => filter(c));
        };
        me.len = () => {
            return self.cells.length;
        };
        me.indexOf = cell => {
            return self.cells.indexOf(cell);
        };
        me.splice = (start, deleteCount) => {
            return self.cells.splice(start, deleteCount);
        };
        me.get = index => {
            return self.cells[index];
        };
        me.max = filter => {
            return self.cells.max(c => filter(c));
        };
        me.min = filter => {
            return self.cells.min(c => filter(c));
        };
        me.orderByDesc = field => {
            return self.cells.orderByDesc(field);
        };
        me.orderBy = field => {
            return self.cells.orderBy(field);
        };
        return me;
    })();

    // self.ranges = (() => {
    //     const size = 20;
    //     let ranges = {};
    //     const me = {};
    //     me.init = () => {
    //         ranges = {};
    //         self.cells.forEach(cell => {
    //             me.push(cell);
    //         })
    //     };

    //     me.getRange = (r1, c1, r2, c2) => {
    //         const fr1 = Math.floor(r1 / size);
    //         const fc1 = Math.floor(c1 / size);
    //         const fr2 = Math.floor(r2 / size);
    //         const fc2 = Math.floor(c2 / size);
    //         const cells = [];
    //         for (let i = fr1; i <= fr2; i++) {
    //             for (let j = fc1; j <= fc2; j++) {
    //                 const range = ranges[i + "|" + j];
    //                 range && cells.push.apply(cells, range);
    //             }
    //         }
    //         return cells.filter(c => !(c.r > r2 || c.r + (c.rs ? c.rs - 1 : 0) < r1) && !(c.c > c2 || c.c + (c.cs ? c.cs - 1 : 0) < c1));

    //     };
    //     me.get = (r, c) => {
    //         const range = me.getRange(r, c, r, c);
    //         if (range) return range[0];
    //     };
    //     me.push = cell => {
    //         const r = Math.floor(cell.r / size);
    //         const c = Math.floor(cell.c / size);
    //         let range = ranges[r + "|" + c];
    //         if (!range) {
    //             range = [];
    //             ranges[r + "|" + c] = range;
    //         }
    //         range.push(cell);
    //         self.cells.indexOf(cell) === -1 && self.cells.push(cell);
    //     };
    //     me.remove = (r, c) => {
    //         const fr = Math.floor(r / size);
    //         const fc = Math.floor(c / size);
    //         const range = ranges[fr + "|" + fc];
    //         if (!range) return;
    //         const cell = range.find(c1 => c1.r === r && c1.c === c);
    //         if (!cell) return;
    //         range.splice(range.indexOf(cell), 1);
    //         self.cells.splice(self.cells.indexOf(cell), 1);
    //     };
    //     return me;
    // })();


    /**
     * {action:CELL_VALUE_CHANGE,cells:[{cell,value,preValue}]}
     * updateRows = {t:1,v:[{i:index,v:row data}]}
     * updateColumns = {t:2,[{i:index, v:column data}]}
     * updateBorders = {t:3,[r:row,c:column,v:border value]}
     * addRows = {t:4,i:index,[data]}
     * addColumns = {t:5,i:index,[data]}
     * removeRows = {t:5,fi:fromindex,ti:toindex}
     * removeColumns = {t:5,fi:fromindex,ti:toindex}
     */
    self.setLocal = (() => {
        const updateCellValues = local => {
            const cells = local.cells;
            cells.forEach(c => {
                let cell = self.cache.find(c1 => c1.r === c.r && c1.c === c.c);
                if (!cell) {
                    cell = { r: c.r, c: c.c };
                    self.cache.push(cell);
                }
                // if (cell) {
                for (let f in c) {
                    if (f.indexOf("pre-") >= 0) continue;
                    (c[f] !== undefined && c[f] !== "") ? cell[f] = c[f] : (delete cell[f]);
                }
            })
            self.invalidateData();
        };

        const addColumnsBefore = local => {
            const from = local.from, steps = local.steps;
            self.addColumnsBefore(from, steps);
        };
        const addColumnsAfter = local => {
            const from = local.from, steps = local.steps;
            self.addColumnsAfter(from, steps);
        };
        const duplicateColumns = local => {
            const from = local.from, steps = local.steps;
            self.duplicateColumns(from, steps);
        };
        const deleteColumns = local => {
            const from = local.from, steps = local.steps;
            self.deleteColumns(from, steps);
        };
        const addRowsBefore = local => {
            const from = local.from, steps = local.steps;
            self.addRowsBefore(from, steps);
        };
        const addRowsAfter = local => {
            const from = local.from, steps = local.steps;
            self.addRowsAfter(from, steps);
        };
        const duplicateRows = local => {
            const from = local.from, steps = local.steps;
            self.duplicateRows(from, steps);
        };
        const deleteRows = local => {
            const from = local.from, steps = local.steps;
            self.deleteRows(from, steps, true);
        };

        const changeColumns = local => {
            self.changeColumns(local.columns);
        };

        const changeRows = local => {
            self.changeRows(local.rows);
        };

        const funcs = {};
        funcs[ACTIONS.CELL_VALUE_CHANGE] = updateCellValues;
        funcs[ACTIONS.COLUMN_ADD_BEFORE] = addColumnsBefore;
        funcs[ACTIONS.COLUMN_ADD_AFTER] = addColumnsAfter;
        funcs[ACTIONS.COLUMN_DUPLICATE] = duplicateColumns;
        funcs[ACTIONS.COLUMN_DELETE] = deleteColumns;
        funcs[ACTIONS.ROW_ADD_BEFORE] = addRowsBefore;
        funcs[ACTIONS.ROW_ADD_AFTER] = addRowsAfter;
        funcs[ACTIONS.ROW_DUPLICATE] = duplicateRows;
        funcs[ACTIONS.ROW_DELETE] = deleteRows;
        funcs[ACTIONS.COLUMN_CHANGE] = changeColumns;
        funcs[ACTIONS.ROW_CHANGE] = changeRows;
        return localAction => {
            funcs[localAction.action](localAction)
        };
    })();
    self.isLocked = () => false;
    self.selectedRows = { from: -1, to: -1 };
    self.selectedColumns = { from: -1, to: -1 };
    self.selectedCells = { from: undefined, to: undefined };
    self.cornerCells = { from: undefined, to: undefined };

    /**
     * 当前可见的所有cell的内存缓存，供鼠标选择时使用
     */
    self.visibleCells = [];
    self.visibleColumns = [];
    self.visibleRows = [];

    let bindDataChanged = undefined;
    self.cellChanged = (cell, property, previouValue) => {
        property === "v" && bindDataChanged && bindDataChanged(cell, previouValue);
        //todo:more
    };

    self.bindData = (data, heads) => {
        if (!(data && data.length > 0)) {
            console.warn("no validate data to bind");
            return;
        }
        (!heads) && (heads = {});
        self.columns = {};
        self.rows = {};
        self.cells = [];
        self.groups = [];
        self.frozens = {};
        self.hiddens = {};
        self.borders = [];
        self.maxRow = data.length - 1;
        data.forEach((d, i) => {
            let index = 0;
            for (let f in d) {
                if (heads[f] && heads[f].ignore) continue;//不显示
                if (i === 0) {
                    const title = (heads[f] && heads[f].title) ? heads[f].title : f;
                    const col = { t: title, a: isNaN(d[f]) ? "c" : "r", bindField: f };
                    (heads[f] && heads[f].readonly) && (col.ro = 1);
                    self.columns[index] = col;
                }
                self.cache.push({ r: i, c: index, v: d[f] });
                index++;
            }
            i === 0 && (self.maxColumn = index - 1);
        });
        self.data = data;
        self.invalidate(true);
        bindDataChanged = (cell, previouValue) => {
            data[cell.r][self.columns[cell.c].bindField] = cell.dv;
            self.bindDataOnChange(cell.r, self.columns[cell.c].bindField, cell.dv);
        };
    };

    let copiedCells;
    self.copy = () => {
        copiedCells = undefined;
        const data = self.serialize();
        data.cells = [];//用于生成新的grid
        data.copied = {};//用于grid里拷贝数据
        if (self.selectedColumns.from !== -1) {//选列
            data.copied.selection = self.inCopyType = "column";
            data.copied.range = [self.selectedColumns.from, self.selectedColumns.to];
            // data.copied.cells = self.cache.filter(c => c.c >= self.selectedColumns.from && c.c <= self.selectedColumns.to);
            copiedCells = self.cache.filter(c => c.c >= self.selectedColumns.from && c.c <= self.selectedColumns.to);
        }
        if (self.selectedRows.from !== -1) {//选行
            data.copied.selection = self.inCopyType = "row";
            data.copied.range = [self.selectedRows.from, self.selectedRows.to];
            // data.copied.cells = self.cache.filter(c => c.r >= self.selectedRows.from && c.r <= self.selectedRows.to);
            copiedCells = self.cache.filter(c => c.r >= self.selectedRows.from && c.r <= self.selectedRows.to);
            
        }
        if (self.selectedCells.from) {//选cell
            data.copied.selection = self.inCopyType = "cell";
            data.copied.range = [self.selectedCells.from, self.selectedCells.to];
            // data.copied.cells = self.cache.filter(c => c.r >= self.selectedCells.from[0] && c.c >= self.selectedCells.from[1] && c.r <= self.selectedCells.to[0] && c.c <= self.selectedCells.to[1]);
            copiedCells = self.cache.filter(c => c.r >= self.selectedCells.from[0] && c.c >= self.selectedCells.from[1] && c.r <= self.selectedCells.to[0] && c.c <= self.selectedCells.to[1]);
        }
        if(copiedCells===undefined) return undefined;
        
        const min_r = copiedCells.min(c => c.r);
        const max_r = copiedCells.max(c => c.r);
        const min_c = copiedCells.min(c => c.c);
        const max_c = copiedCells.max(c => c.c);
        data.maxRow = max_r - min_r;
        data.maxColumn = max_c - min_c;
        data.columns = {};
        data.rows = {};

        let width = self.getHeadWidth(), height = self.getHeadWidth();
        Object.keys(self.columns).forEach(column => {
            const col = Number.parseInt(column);
            if (col >= min_c && col <= max_c) data.columns[column - min_c] = self.columns[column];
        })
        Object.keys(self.rows).forEach(row => {
            const r = Number.parseInt(row);
            if (r >= min_r && r <= max_r) data.rows[row - min_r] = self.rows[row];
        })

        for (let i = min_r; i <= max_r; i++) {
            height += (self.rows[i] && self.rows[i].h) ? self.rows[i].h : self.cellHeight;
        }
        for (let i = min_c; i <= max_c; i++) {
            width += (self.columns[i] && self.columns[i].w) ? self.columns[i].w : self.cellWidth;
        }
        copiedCells.forEach(c => {
            const cell = {};
            for (let f in c) {
                cell[f] = c[f];
            }
            cell.r = c.r - min_r;
            cell.c = c.c - min_c;
            data.cells.push(cell);
        });
        data.width = width;
        data.height = height;
        self.inCopy = true;
        self.inCopyDisplayed = false;
        self.drawer.drawSelection();
        return data;

    };
    self.cut = (data) => {
        const all = [];
        copiedCells.forEach(c => {
            const pv = c.v;
            delete c.v;
            delete c.dv;
            // all.push({ cell: c, v: undefined, "pre-v": pv });
            all.push({ v: undefined, "pre-v": pv });
        })
        self.cellsModified(all);
    };
    self.paste = data => {
        // if (data.type !== "grid") return false;
        if (!data.copied.selection) return false;
        const preMaxRow = self.maxRow;
        const preMaxColumn = self.maxColumn;
        let pasted;
        switch (data.copied.selection) {
            case "column":
                if (self.selectedCells.from || self.selectedRows.from !== -1) showDialog("无法粘贴，区域不匹配");
                if (self.selectedColumns.from !== -1) pasted = pasteColumns2Columns(data.cells, data.copied.range, [self.selectedColumns.from, self.selectedColumns.to]);
                break;
            case "row":
                if (self.selectedCells.from || self.selectedColumns.from !== -1) showDialog("无法粘贴，区域不匹配");
                if (self.selectedRows.from !== -1) pasted = pasteRows2Rows(data.cells, data.copied.range, [self.selectedRows.from, self.selectedRows.to]);
                break;
            case "cell"://cell
                if (self.selectedCells.from) pasted = pasteCells2Cells(data.cells, data.copied.range, [self.selectedCells.from, self.selectedCells.to]);
                if (self.selectedColumns.from !== -1) pasted = pasteCells2Columns(data.cells, data.copied.range, [self.selectedColumns.from, self.selectedColumns.to]);
                if (self.selectedRows.from !== -1) pasted = pasteCells2Rows(data.cells, data.copied.range, [self.selectedRows.from, self.selectedRows.to]);
                break;
            default:
                break;
        }
        if (self.maxRow !== -1) self.maxRow = self.cache.max(c => c.r);
        if (self.maxColumn !== -1) self.maxColumn = self.cache.max(c => c.c);
        self.invalidate(true);
        // const cells = data.copied.cells.map(c => {
        //     // return { cell: c, v: c.v, "pre-v": c.prev }
        //     return { cell: c }
        // });
        const cells = pasted;
        cells.maxRow = self.maxRow;
        cells.maxColumn = self.maxColumn;
        cells["pre-maxRow"] = preMaxRow;
        cells["pre-maxColumn"] = preMaxColumn;
        self.cellsModified(cells);
        return true;
    };

    const pasteCell = (cell, col, row) => {
        const nc = JSON.parse(JSON.stringify(cell));
        nc.c = col;
        nc.r = row;
        let preCell = self.cache.find(c => c.r === nc.r && c.c === nc.c);
        if (preCell) self.cache.splice(self.cache.indexOf(preCell), 1);
        else preCell = {};
        self.cache.push(nc);

        for (let f in nc) {
            if (f.indexOf("pre-") === 0) {
                console.warn(".....");
            }
            preCell["pre-" + f] = preCell[f];
            preCell[f] = nc[f];
        }

        return preCell;
    }

    const pasteColumns2Columns = (cells, fromrange, torange) => {
        const column_from = Math.min(fromrange[0], fromrange[1]);
        const width_from = Math.abs(fromrange[1] - fromrange[0]) + 1;

        const column_to = Math.min(torange[0], torange[1]);
        const width_to = Math.abs(torange[0] - torange[1]) + 1;
        const times_x = width_to % width_from === 0 ? width_to / width_from : 1;

        const pasted = [];

        for (let i = 0; i < times_x; i++) {
            cells.forEach(c => {
                pasted.push(pasteCell(c, c.c + column_to + width_from * i, c.r));
            });
        }
        return pasted;

    };
    const pasteRows2Rows = (cells, fromrange, torange) => {
        const row_from = Math.min(fromrange[0], fromrange[1]);
        const height_from = Math.abs(fromrange[1] - fromrange[0]) + 1;

        const row_to = Math.min(torange[0], torange[1]);
        const height_to = Math.abs(torange[0] - torange[1]) + 1;
        const times_y = height_to % height_from === 0 ? height_to / height_from : 1;

        const pasted = [];
        for (let i = 0; i < times_y; i++) {
            cells.forEach(c => {
                pasted.push(pasteCell(c, c.c, c.r + row_to + height_from * i));
            });
        }

        return pasted;

    };

    const pasteCells2Cells = (cells, fromrange, torange) => {
        const row1 = Math.min(fromrange[0][0], fromrange[1][0]);
        const column1 = Math.min(fromrange[0][1], fromrange[1][1]);
        const row2 = Math.min(torange[0][0], torange[1][0]);
        const column2 = Math.min(torange[0][1], torange[1][1]);

        const width_from = Math.abs(fromrange[0][1] - fromrange[1][1]) + 1;
        const height_from = Math.abs(fromrange[0][0] - fromrange[1][0]) + 1;
        const width_to = Math.abs(torange[0][1] - torange[1][1]) + 1;
        const height_to = Math.abs(torange[0][0] - torange[1][0]) + 1;

        const times_x = (width_to % width_from === 0 && height_to % height_from === 0) ? width_to / width_from : 1;
        const times_y = (width_to % width_from === 0 && height_to % height_from === 0) ? height_to / height_from : 1;

        const pasted = [];

        for (let i = 0; i < times_x; i++) {
            for (let j = 0; j < times_y; j++) {
                cells.forEach(c => {
                    pasted.push(pasteCell(c, c.c + column2 + width_from * i, c.r + row2 + height_from * j));
                    // c.r = c.r - row1 + row2 + height_from * j;
                    // c.c = c.c - column1 + column2 + width_from * i;
                    // const nc = JSON.parse(JSON.stringify(c));
                    // self.cache.remove(c => c.r === nc.r && c.c === nc.c);//todo: ready for history
                    // self.cache.push(nc);
                })
            }
        }
        return pasted;

    };

    const pasteCells2Columns = (cells, fromrange, torange) => {
        const row_from = Math.min(fromrange[0][0], fromrange[1][0]);
        const column_from = Math.min(fromrange[0][1], fromrange[1][1]);
        const width_from = Math.abs(fromrange[0][1] - fromrange[1][1]) + 1;
        const height_from = Math.abs(fromrange[0][0] - fromrange[1][0]) + 1;

        const column_to = Math.min(torange[0], torange[1]);
        const width_to = Math.abs(torange[0] - torange[1]) + 1;



        const times_x = width_to % width_from === 0 ? width_to / width_from : 1;
        const times_y = self.maxRow === -1 ? 1 : (self.maxRow - self.minRow + 1) / height_from;
        for (let i = 0; i < times_x; i++) {
            for (let j = 0; j < times_y; j++) {
                cells.forEach(c => {
                    c.r = c.r - row_from + self.minRow + j * height_from;
                    c.c = c.c - column_from + column_to + width_from * i;
                    const nc = JSON.parse(JSON.stringify(c));
                    self.cache.remove(c => c.r === nc.r && c.c === nc.c);//todo: ready for history
                    self.cache.push(nc);
                });
            }
        }

    };
    const pasteCells2Rows = (cells, fromrange, torange) => {
        const row_from = Math.min(fromrange[0][0], fromrange[1][0]);
        const column_from = Math.min(fromrange[0][1], fromrange[1][1]);
        const width_from = Math.abs(fromrange[0][1] - fromrange[1][1]) + 1;
        const height_from = Math.abs(fromrange[0][0] - fromrange[1][0]) + 1;

        const row_to = Math.min(torange[0], torange[1]);
        const height_to = Math.abs(torange[0] - torange[1]) + 1;



        const times_y = height_to % height_from === 0 ? height_to / height_from : 1;
        const times_x = self.maxColumn === -1 ? 1 : (self.maxColumn - self.minColumn + 1) / width_from;
        for (let i = 0; i < times_y; i++) {
            for (let j = 0; j < times_x; j++) {
                cells.forEach(c => {
                    c.r = c.r - row_from + row_to + height_from * i;
                    c.c = c.c - column_from + self.minColumn + j * width_from;
                    const nc = JSON.parse(JSON.stringify(c));
                    self.cache.remove(c => c.r === nc.r && c.c === nc.c);//todo: ready for history
                    self.cache.push(nc);
                });
            }
        }
    };

    //列操作api
    (function columnsOperations() {
        const deleteColumns = (from, steps) => {
            for (let i = from; i < from + steps; i++) {
                delete self.columns[i];
            }

            self.cache.forEach(cell => {
                if (cell.c >= from && cell.c < from + steps) cell.c = -1;
                if (cell.c >= from + steps) cell.c -= steps;
            })

            self.cache.remove(c => c.c === -1);
            if (self.maxColumn !== -1 && !self.changeIgnored) self.maxColumn -= steps;
        };

        const duplicateColumns = (from, steps) => {
            let cols = Object.keys(self.columns);
            cols = cols.sort((a, b) => b - a);
            for (let i = 0; i < cols.length; i++) {
                if (Number.isNaN(cols[i])) continue;
                const col = Number.parseInt(cols[i]);
                if (col >= from) self.columns[(col + steps)] = JSON.parse(JSON.stringify(self.columns[col]));
                else break;
            }
            if (self.maxColumn !== -1 && !self.changeIgnored) self.maxColumn += steps;
        }
        const moveCellsColumn = (from, steps, isCopy) => {
            const cells = self.cache.orderByDesc("c");
            for (let i = 0; i < cells.length; i++) {
                const cell = cells[i];
                if (cell.c >= from) {
                    if (isCopy) {
                        const newCell = JSON.parse(JSON.stringify(cell));
                        self.cache.push(newCell);
                    }
                    cell.c += steps;
                } else break;
            }
        }
        self.addColumnsBefore = (from, steps) => {
            duplicateColumns(from, steps);
            for (let i = 0; i < steps; i++) {
                self.columns[from + i] = {};
            }
            moveCellsColumn(from, steps);
            self.invalidateData();
            !self.changeIgnored && (self.local = { action: ACTIONS.COLUMN_ADD_BEFORE, from, steps });
        };

        self.addColumnsAfter = (from, steps) => {
            duplicateColumns(from, steps);
            for (let i = 0; i < steps; i++) {
                self.columns[from + steps + i] = {};
            }
            moveCellsColumn(from + steps, steps);
            self.invalidateData();
            !self.changeIgnored && (self.local = { action: ACTIONS.COLUMN_ADD_AFTER, from, steps });
        };

        self.duplicateColumns = (from, steps) => {
            duplicateColumns(from, steps);
            moveCellsColumn(from, steps, true);
            self.invalidateData();;
            !self.changeIgnored && (self.local = { action: ACTIONS.COLUMN_DUPLICATE, from, steps });
        };

        self.deleteColumns = (from, steps) => {
            deleteColumns(from, steps);
            self.invalidateData();;
            !self.changeIgnored && (self.local = { action: ACTIONS.COLUMN_DELETE, from, steps });
        };
    })();
    //行操作api
    (function rowsOperations() {
        const deleteRows = (from, steps) => {
            for (let i = from; i < from + steps; i++) {
                delete self.rows[i];
            }

            self.cache.forEach(cell => {
                if (cell.r >= from && cell.r < from + steps) cell.r = -1;
                if (cell.r >= from + steps) cell.r -= steps;
            })

            self.cache.remove(c => c.r === -1);
            if (self.maxRow !== -1 && !self.changeIgnored) self.maxRow -= steps;
        };
        const duplicateRows = (from, steps) => {
            let rows = Object.keys(self.rows);
            rows = rows.sort((a, b) => b - a);
            for (let i = 0; i < rows.length; i++) {
                if (Number.isNaN(rows[i])) continue;
                const row = Number.parseInt(rows[i]);
                if (row >= from) self.rows[(row + steps)] = JSON.parse(JSON.stringify(self.rows[row]));
                else break;
            }
            if (self.maxRow !== -1 && !self.changeIgnored) self.maxRow += steps;
        };
        const moveCellsRow = (from, steps, isCopy) => {
            const cells = self.cache.orderByDesc("r");
            for (let i = 0; i < cells.length; i++) {
                const cell = cells[i];
                if (cell.r >= from) {
                    if (isCopy) {
                        const newCell = JSON.parse(JSON.stringify(cell));
                        self.cache.push(newCell);
                    }
                    cell.r += steps;
                } else break;
            }
        };

        self.addRowsBefore = (from, steps) => {
            duplicateRows(from, steps);
            for (let i = 0; i < steps; i++) {
                delete self.rows[from + i];// = {};
            }
            moveCellsRow(from, steps);
            self.invalidate(true);
            !self.changeIgnored && (self.local = { action: ACTIONS.ROW_ADD_BEFORE, from, steps });
        };

        self.addRowsAfter = (from, steps) => {
            duplicateRows(from, steps);
            for (let i = 0; i < steps; i++) {
                delete self.rows[from + steps + i];// = {};
            }
            moveCellsRow(from + steps, steps);
            self.invalidateData();
            !self.changeIgnored && (self.local = { action: ACTIONS.ROW_ADD_AFTER, from, steps });
        };

        self.duplicateRows = (from, steps) => {
            duplicateRows(from, steps);
            moveCellsRow(from, steps, true);
            self.invalidateData();;
            !self.changeIgnored && (self.local = { action: ACTIONS.ROW_DUPLICATE, from, steps });
        };


        self.deleteRows = (from, steps) => {
            deleteRows(from, steps);
            self.invalidateData();
            !self.changeIgnored && (self.local = { action: ACTIONS.ROW_DELETE, from, steps });
        };

        self.changeColumns = cols => {
            cols.forEach(col => {
                const index = col.c;
                delete col.c;
                self.columns[index] = col;
            });
            self.invalidate();
        };

        self.changeRows = rows => {
            rows.forEach(row => {
                const index = row.r;
                delete row.r;
                self.rows[index] = row;
            });
            self.invalidate();
        };

    })();

    //行操作api
    (function fitWidthHeight() {
        let width = self.minWidth;
        self.columnFitWidth = colId => {
            self.visibleCells.forEach(cell => {
                if (cell.c !== colId) return;
                const txt = document.getElementById(self.page.div.id + "|" + self.id + "|" + cell.r + "|" + cell.c);
                if (!txt) return;
                if (txt.lastElementChild.clientWidth > width) width = txt.lastElementChild.clientWidth + 5;
            })
            if (width > self.minWidth) {
                const column = self.columns[colId];
                if (column) {
                    column.w !== undefined && (Number.isNaN(column.w) || column.w < width) && (column.w = width);
                } else {
                    self.columns[colId] = { w: width };

                }
                self.invalidate(true);
            }

        };
        self.rowFitHeight = rowId => {
            let height = self.minHeight;
            self.visibleCells.forEach(cell => {
                if (cell.r !== rowId) return;
                const txt = document.getElementById(self.page.div.id + "|" + self.id + "|" + cell.r + "|" + cell.c);
                if (!txt) return;
                if (txt.lastElementChild.clientHeight > height) height = txt.lastElementChild.clientHeight + 5;
            })
            if (height > self.minHeight) {
                const row = self.rows[rowId];
                if (row) {
                    row.h !== undefined && (Number.isNaN(row.h) || row.h < height) && (row.h = height);
                } else {
                    self.rows[rowId] = { h: height };

                }
                self.invalidate(true);
            }

        };
    })();

    self.getMenuScript = (x, y) => {
        const cfrom = self.selectedColumns.from, cto = self.selectedColumns.to, rfrom = self.selectedRows.from, rto = self.selectedRows.to, cellfrom = self.selectedCells.from, cellto = self.selectedCells.to;
        const x1 = x - self.x, y1 = y - self.y;
        beginSelection(x1, y1);
        closeSelection(x1 + 1, y1 + 1);
        defineNormalSelection();
        if (self.selectedColumns.from !== -1 && cfrom !== -1) {
            self.selectedColumns.from = cfrom;
            self.selectedColumns.to = cto;
        }
        if (self.selectedRows.from !== -1 && rfrom !== -1) {
            self.selectedRows.from = rfrom;
            self.selectedRows.to = rto;
        }
        if (self.selectedCells.from !== undefined && cellfrom !== undefined) {
            self.selectedCells.from = cellfrom;
            self.selectedCells.to = cellto;
        }
        self.drawer.drawSelection();

        const menus = [];
        //列选中
        if (self.selectedColumns.from > -1 || self.selectedCells.from !== undefined) {
            const min = self.selectedColumns.from > -1 ? (self.selectedColumns.from <= self.selectedColumns.to ? self.selectedColumns.from : self.selectedColumns.to) : Math.min(self.selectedCells.from[1], self.selectedCells.to[1]);
            const num = self.selectedColumns.from > -1 ? (Math.abs(self.selectedColumns.from - self.selectedColumns.to) + 1) : (Math.abs(self.selectedCells.to[1] - self.selectedCells.from[1]) + 1);

            menus.push({
                text: "向左新增列", action: (shape, x, y) => {
                    self.addColumnsBefore(min, num);
                }
            });
            menus.push({
                text: "向右新增列", action: (shape, x, y) => {
                    self.addColumnsAfter(min, num);
                }
            });
            menus.push({
                text: "复制列", action: (shape, x, y) => {
                    self.duplicateColumns(min, num);
                }
            });
            menus.push({
                text: "删除列", action: (shape, x, y) => {
                    self.deleteColumns(min, num);
                }
            });
        }
        if (self.selectedRows.from > -1 || self.selectedCells.from !== undefined) {
            const min = self.selectedRows.from > -1 ? (self.selectedRows.from <= self.selectedRows.to ? self.selectedRows.from : self.selectedRows.to) : Math.min(self.selectedCells.from[0], self.selectedCells.to[0]);
            const num = self.selectedRows.from > -1 ? (Math.abs(self.selectedRows.from - self.selectedRows.to) + 1) : (Math.abs(self.selectedCells.to[0] - self.selectedCells.from[0]) + 1);

            menus.push({
                text: "向上新增行", action: (shape, x, y) => {
                    self.addRowsBefore(min, num);
                }
            });
            menus.push({
                text: "向下新增行", action: (shape, x, y) => {
                    self.addRowsAfter(min, num);
                }
            });
            menus.push({
                text: "复制行", action: (shape, x, y) => {
                    self.duplicateRows(min, num);
                }
            });
            menus.push({
                text: "删除行", action: (shape, x, y) => {
                    self.deleteRows(min, num);
                }
            });
        }
        if (self.selectedCells.from !== undefined) {
            //todo: more on cells;
        }

        menus.width = 100;
        menus.borderColor = self.borderColor;
        menus.fontColor = self.fontColor;
        return menus;
    };

    self.focused = () => {
        // runCode("focusedCode");
    };

    // self.updateData = value => {
    //     value.forEach(d => {
    //         let cell = self.cache.find(c => c.r === d.r && c.c === d.c);
    //         if (!cell) {
    //             cell = { r: d.r, c: d.c };
    //             self.cache.push(cell);
    //         }
    //         for (let f in d.v) {
    //             cell[f] = d.v[f];
    //         }
    //     })
    // };

    // self.updateRows = value => {
    //     value.forEach(r => {
    //         let row = self.rows[r.i];
    //         if (!row) {
    //             row = {};
    //             self.rows[r.i] = row;
    //         }
    //         for (let f in r.v) {
    //             row[f] = r.v[f];
    //         }
    //     })
    // };

    // self.updateColumns = value => {
    //     value.forEach(c => {
    //         let column = self.columns[c.i];
    //         if (!column) {
    //             column = {};
    //             self.columns[c.i] = column;
    //         }
    //         for (let f in c.v) {
    //             column[f] = c.v[f];
    //         }
    //     })
    // };

    // self.addRowsBefore = (fromIndex, toIndex) => {
    //     const newNumber = toIndex - fromIndex + 1;
    //     const existRows = [];
    //     for (let i = fromIndex; i <= toIndex; i++) {
    //         existRows.push(self.rows.find(r => r.i === i));
    //     }
    //     const newCells = [];
    //     self.cache.forEach(c => {
    //         if (c.r < fromIndex) return;
    //         c.r += newNumber;
    //         if (c.r === fromIndex && c.bk) {
    //             for (let i = fromIndex; i <= toIndex; i++) {
    //                 newCells.push({ r: i, c: c.c, bk: c.bk })
    //             }
    //         }
    //     });
    //     self.cache.push.apply(self.cache, newCells);
    // };

    // self.addColumns = (value, index) => {
    //     // value.forEach((c, i) => {
    //     //     self.data.forEach((r, j) => {
    //     //         r.insert(c[j], i + index);
    //     //     })
    //     // });
    // };

    // self.removeRows = (fromIndex, toIndex) => {
    //     // self.data.splice(fromIndex, toIndex - fromIndex + 1);
    // };

    // self.removeColumns = (fromIndex, toIndex) => {
    //     // self.data.forEach(r => {
    //     //     r.splice(fromIndex, toIndex - fromIndex + 1);
    //     // })

    // };


    // self.updateBorders = value => {
    //     value.forEach(b => {
    //         const border = self.borders.find(b1 => b1.r === b.r && b1.c === b.c);
    //         if (!border) {
    //             self.borders.push(b);
    //         } else {
    //             for (let f in b.v) {
    //                 border.v[f] = b.v[f];
    //             }
    //         }
    //     })
    // };

    self.getHeadHeight = () => self.getToolHeight() + (self.withColumnHead ? self.headHeight : 0);
    self.getHeadWidth = () => self.getToolWidth() + (self.withRowHead ? self.headWidth : 0);
    self.getToolHeight = () => (self.groups.contains(g => g[2] === 1) ? GRID_TOOL_WIDTH * self.groups.max(g => g[2] === 1 ? g[4] : 1) : 1);
    self.getToolWidth = () => (self.groups.contains(g => g[2] === 0) ? GRID_TOOL_WIDTH * self.groups.max(g => g[2] === 0 ? g[4] : 1) : 1);

    let cornerSelection = undefined;
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        initConnectors.call(self);
        const id = self.id + "|focus";
        const getx = () => {
            const focus = document.getElementById(id);
            return focus && focus.frame && (focus.frame.x + focus.frame.width);
        };
        const gety = () => {
            const focus = document.getElementById(id);
            return focus && focus.frame && (focus.frame.y + focus.frame.height);
        };
        const getVisiblity = () => {
            const focus = document.getElementById(id);
            return focus && focus.style.visibility === "visible";
        };
        const release = (position) => {
            delete position.context.command;
            try {
                const isCtrl = self.page.ctrlKeyPressed
                const fr1 = self.selectedCells.from[0];
                const fc1 = self.selectedCells.from[1];
                const fr2 = self.selectedCells.to[0];
                const fc2 = self.selectedCells.to[1];

                const tr1 = self.cornerCells.from[0];
                const tc1 = self.cornerCells.from[1];
                const tr2 = self.cornerCells.to[0];
                const tc2 = self.cornerCells.to[1];

                const toCells = self.cache.getRange(tr1, tc1, tr2, tc2);
                const fromCells = self.cache.getRange(fr1, fc1, fr2, fc2);

                const columnFeature = (column, direction) => {
                    const colCells = fromCells.filter(c => c.r >= fr1 && c.r <= fr2 && c.c === column).orderBy("r");
                    if (colCells.length < fr2 - fr1 + 1) return { isNum: false, diretion };
                    for (let i = 0; i < colCells.length; i++) {
                        if (isNaN(colCells[i].v)) return { isNum: false, direction };
                    }
                    if (direction === 1) {
                        return { isNum: true, direction, delta: colCells.length >= 2 ? Number(colCells[colCells.length - 1].v) - Number(colCells[colCells.length - 2].v) : 1, startFrom: Number(colCells[colCells.length - 1].v) };
                    } else {
                        return { isNum: true, direction, delta: colCells.length >= 2 ? Number(colCells[0].v) - Number(colCells[1].v) : -1, startFrom: Number(colCells[0].v) };

                    }
                };
                const rowFeature = (row, direction) => {
                    const rowCells = fromCells.filter(c => c.c >= fc1 && c.c <= fc2 && c.r === row).orderBy("c");
                    if (rowCells.length < fc2 - fc1 + 1) return { isNum: false, diretion };
                    for (let i = 0; i < rowCells.length; i++) {
                        if (isNaN(rowCells[i].v)) return { isNum: false, direction };
                    }
                    if (direction === 1) {
                        return { isNum: true, direction, delta: rowCells.length >= 2 ? Number(rowCells[rowCells.length - 1].v) - Number(rowCells[rowCells.length - 2].v) : 1, startFrom: Number(rowCells[rowCells.length - 1].v) };
                    } else {
                        return { isNum: true, direction, delta: rowCells.length >= 2 ? Number(rowCells[0].v) - Number(rowCells[1].v) : -1, startFrom: Number(rowCells[0].v) };

                    }
                }
                const interpretFormular = value => {
                    //todo
                };
                const translateFormular = (value, r1, c1, r2, c2) => {
                    //todo
                };
                const attachedCells = [];
                const attachData = (fromCell, i, j, feature, steps) => {
                    if (!fromCell) return;
                    let toCell = toCells.find(c => c.r === i && c.c === j);
                    if (toCell) self.cache.remove(c => c.r === toCell.r && c.c === toCell.c);
                    else toCell = { r: i, c: j };
                    const pv = toCell.v;
                    self.cache.push(toCell);
                    for (let f in fromCell) {
                        if (f === "r" || f === "c") continue;
                        if (feature.isNum && f === "v") {
                            toCell.v = feature.startFrom + steps * feature.delta;
                            delete toCell.dv;// = undefined;
                        } else {
                            if (f === "v") {
                                const interpret = interpreter(self, fromCell.v, fromCell.r, fromCell.c);
                                if (interpret.isFormular()) toCell[f] = interpret.dynamicChangeRanges(toCell.r - fromCell.r, toCell.c - fromCell.c);
                                else toCell[f] = fromCell[f];
                                delete toCell.dv;// = undefined;
                            }
                            else toCell[f] = fromCell[f];
                        }
                    }
                    const cell = JSON.parse(JSON.stringify(toCell));
                    cell["pre-v"] = pv;
                    // attachedCells.push({ cell: toCell, value: toCell.v, preValue: pv });
                    attachedCells.push(cell);
                }

                if (self.cornerCells.from[0] === self.selectedCells.from[0]) {//horizontal direction
                    for (let j = tr1; j <= tr2; j++) {
                        if (self.cornerCells.from[1] > self.selectedCells.to[1]) {//right side
                            const feature = rowFeature(j, 1);
                            for (let i = tc1; i <= tc2; i++) {
                                let dr = i - tc1;
                                let fromCell = fromCells.find(c => c.c === fc1 + dr % (fc2 - fc1 + 1) && c.r === j);
                                attachData(fromCell, j, i, feature, dr + 1);
                            }
                        } else {//left side
                            const feature = rowFeature(j, -1);
                            for (let i = tc2; i >= tc1; i--) {
                                let dr = i - tc2;
                                let fromCell = fromCells.find(c => c.c === fc2 + dr % (fc2 - fc1 + 1) && c.r === j);
                                attachData(fromCell, j, i, feature, 1 - dr);
                            }
                        }
                    }
                } else {//vertical direction
                    for (let j = tc1; j <= tc2; j++) {
                        if (self.cornerCells.from[0] > self.selectedCells.to[0]) {//down side
                            const feature = columnFeature(j, 1);
                            for (let i = tr1; i <= tr2; i++) {
                                let dr = i - tr1;
                                let fromCell = fromCells.find(c => c.r === fr1 + dr % (fr2 - fr1 + 1) && c.c === j);
                                attachData(fromCell, i, j, feature, dr + 1);
                            }
                        } else {//up side
                            const feature = columnFeature(j, -1);
                            for (let i = tr2; i >= tr1; i--) {
                                let dr = i - tr2;
                                let fromCell = fromCells.find(c => c.r === fr2 + dr % (fr2 - fr1 + 1) && c.c === j);
                                attachData(fromCell, i, j, feature, 1 - dr);

                            }
                        }
                    }

                }
                self.cellsModified(attachedCells);
            } catch (e) {
                console.warn(e);
            } finally {
                self.invalidate(true);
                cornerSelection = undefined;
                (self.cornerCells.from[0] < self.selectedCells.from[0]) && (self.selectedCells.from[0] = self.cornerCells.from[0]);
                (self.cornerCells.from[1] < self.selectedCells.from[1]) && (self.selectedCells.from[1] = self.cornerCells.from[1]);
                (self.cornerCells.to[0] > self.selectedCells.to[0]) && (self.selectedCells.to[0] = self.cornerCells.to[0]);
                (self.cornerCells.to[1] > self.selectedCells.to[1]) && (self.selectedCells.to[1] = self.cornerCells.to[1]);
                self.cornerCells = [];
                self.drawer.drawSelection();
            }
        };

        const direction = { cursor: "crosshair", key: "corner", color: "steelblue" };
        self.corner = connector(self, getx, gety, s => direction, getVisiblity, s => true, () => true, release, s => false);
        self.corner.onMouseDown = position => {
            cornerSelection = {};
            self.drawer.endEdit();
        };
        self.corner.moving = (deltaX, deltaY, x0, y0) => {
            // closeSelection(pos.x - self.x, pos.y - self.y);
            closeSelection(x0 - self.x, y0 - self.y);
            self.drawer.drawSelection();
        };
        self.corner.type = "corner";
        self.corner.radius = 3;
        self.corner.shapeStyle = "rect";
        self.corner.strokeStyle = "white";
    };

    const resized = self.resized;
    self.resized = () => {
        resized.call(self);
        self.drawer.endEdit();
    };

    const defineNormalSelection = () => {
        self.selectedColumns.from = self.selectedColumns.to = self.selectedCells.from = self.selectedCells.to = -1;
        self.selectedCells.from = self.selectedCells.to = undefined;

        //select cell
        if (selection.fromColumn > -1 && selection.fromRow > -1) {
            self.selectedCells.from = [selection.fromRow, selection.fromColumn];
            self.selectedCells.to = [selection.toRow, selection.toColumn];
        }
        //select column
        if (selection.fromColumn > -1 && selection.fromRow === -1) {
            self.selectedColumns.from = selection.fromColumn;
            self.selectedColumns.to = selection.toColumn;
        }
        //select row
        if (selection.fromColumn === -1 && selection.fromRow > -1) {
            self.selectedRows.from = selection.fromRow;
            self.selectedRows.to = selection.toRow;
        }
    };

    const defineCornerSelection = () => {
        if (cornerSelection.fromColumn === -1 || cornerSelection.fromRow === -1) return;

        const focus = document.getElementById(self.id + "|focus");
        const x = cornerSelection.x;
        const y = cornerSelection.y;
        let dx = 0, dy = 0;
        if (x > focus.frame.x + focus.frame.width) dx = x - focus.frame.x - focus.frame.width;
        if (x < focus.frame.x) dx = x - focus.frame.x;
        if (y > focus.frame.y + focus.frame.height) dy = y - focus.frame.y - focus.frame.height;
        if (y < focus.frame.y) dy = y - focus.frame.y;

        if (dx === 0 && dy === 0) return;

        const isX = Math.abs(dx) > Math.abs(dy);//is x expanding or y expanding
        const actual = self.selectedCells.actualCells;

        if (isX) {
            self.cornerCells.from = dx > 0 ? [actual.from[0], actual.to[1] + 1] : [actual.from[0], cornerSelection.toColumn];
            self.cornerCells.to = [actual.to[0], dx > 0 ? cornerSelection.toColumn : actual.from[1] - 1];
        } else {
            self.cornerCells.from = dy > 0 ? [actual.to[0] + 1, actual.from[1]] : [cornerSelection.toRow, actual.from[1]];
            self.cornerCells.to = [dy > 0 ? cornerSelection.toRow : actual.from[0] - 1, actual.to[1]];
        }

        // self.visibleCells.forEach(c => {
        //     if (self.selectedCells.contains(c1 => c1.r === c.r && c1.c === c.c)) return;
        //     const cell = self.cornerCells.find(c1 => c1.r === c.r && c1.c === c.c);
        //     (cell) && self.cornerCells.splice(self.cornerCells.indexOf(cell), 1);
        //     if (isX) {//x expanding
        //         cornerSelection.locky = true;
        //         cornerSelection.lockx = false;
        //         if (c.r < cornerSelection.r1 || c.r > cornerSelection.r2) return;
        //     } else {//y expanding
        //         cornerSelection.lockx = true;
        //         cornerSelection.locky = false;
        //         if (c.c < cornerSelection.c1 || c.c > cornerSelection.c2) return;
        //     }
        //     const realCell = { x: c.x, y: c.y, width: c.width, height: c.height, c: c.c, r: c.r };
        //     (c.r <= self.frozenRow && !selection.yInFrozen) && (realCell.y -= self.scrollY);
        //     (c.c <= self.frozenColumn && !selection.xInFrozen) && (realCell.x -= self.scrollX);
        //     if (isX) {//x expanding
        //         realCell.r >= cornerSelection.r1 && realCell.r <= cornerSelection.r2 && ((realCell.x > x1 && realCell.x < x2) || (realCell.x + realCell.width > x1 && realCell.x + realCell.width < x2)) && self.cornerCells.push(c);
        //     } else {//y expanding
        //         realCell.c >= cornerSelection.c1 && realCell.c <= cornerSelection.c2 && ((realCell.y > y1 && realCell.y < y2) || (realCell.y + realCell.height > y1 && realCell.y + realCell.height < y2)) && self.cornerCells.push(c);
        //     }
        // })

    };

    const defineSelection = () => {
        if (cornerSelection) defineCornerSelection();
        else defineNormalSelection();
    }

    const selection = {};// {x1:0,y1:0,x2:0,y2:0};
    const beginSelection = (x, y) => {
        const headHeight = self.getHeadHeight();
        const headWidth = self.getHeadWidth();
        const select = cornerSelection ? cornerSelection : selection;
        if (cornerSelection) {//右下角拖动选择
            self.cornerCells = { from: undefined, to: undefined };
        } else {//常规选择
            self.selectedCells = { from: undefined, to: undefined };
            self.selectedRows = { from: -1, to: -1 };
            self.selectedColumns = { from: -1, to: -1 };
        }
        if (x > headWidth && y > headHeight) {
            if (!self.visibleCells.find(c => isPointIn({ x, y }, c))) return;
        }

        select.fromColumn = select.toColumn = select.fromRow = select.toRow = -1;
        const column = self.visibleColumns.find(c => isPointIn({ x, y: headHeight + 1 }, c));
        if (column) {
            select.fromColumn = select.toColumn = column.c;
            if (column.indent > 0) self.scrollX -= column.indent;
            if (column.x + column.width >= self.width) self.scrollX += column.x + column.width - self.width;
        }
        const row = self.visibleRows.find(r => isPointIn({ x: headWidth + 1, y }, r));
        if (row) {
            select.fromRow = select.toRow = row.r;
            if (row.indent > 0) self.scrollY -= row.indent;
            if (row.y + row.height >= self.height) self.scrollY += row.y + row.height - self.height;
        }
        // self.focusCell = self.visibleCells.find(c => isPointInRect({ x, y }, c));
        select.x = x;
        select.y = y;
        // defineSelection();
    };

    const isPointIn = (position, bound) => {
        return isPointInRect(position, { x: bound.x, y: bound.y, width: bound.width - 1, height: bound.height - 1 });
    }
    const closeSelection = (x, y) => {
        const scrollStep = self.scrollStep;
        const headHeight = self.getHeadHeight();
        const headWidth = self.getHeadWidth();
        const select = cornerSelection ? cornerSelection : selection;
        if (select.fromColumn === -1 && select.fromRow === -1) return;
        if (x > headWidth && y > headHeight) {
            if (!self.visibleCells.find(c => isPointIn({ x, y }, c))) return;
        }

        ((x - headWidth - self.frozenX) / (select.x - headWidth - self.frozenX)) < 0 && !select.lockx && (self.scrollX = 0);
        ((y - headHeight - self.frozenY) / (select.y - headHeight - self.frozenY)) < 0 && !select.locky && (self.scrollY = 0);

        x > self.width && (self.scrollX += scrollStep);
        y > self.height && (self.scrollY += scrollStep);

        if (select.fromRow !== -1) {
            const row = self.visibleRows.find(r => isPointIn({ x: headWidth + 1, y }, r));
            if (row) {
                select.toRow = row.r;
                if (row.indent > 0) self.scrollY -= row.indent;
                if (row.y + row.height > self.height) self.scrollY += scrollStep;// row.y + row.height - self.height + scrollStep;
            }
        }
        if (select.fromColumn !== -1) {
            const column = self.visibleColumns.find(c => isPointIn({ x, y: headHeight + 1 }, c));
            if (column) {
                select.toColumn = column.c;
                if (column.indent > 0) self.scrollX -= column.indent;
                if (column.x + column.width >= self.width) self.scrollX += scrollStep;// column.x + column.width - self.width + scrollStep;
            }
        }
        if (select.fromColumn !== -1 && select.fromRow !== -1) {
            self.focusCell = self.visibleCells.find(c => isPointIn({ x, y }, c));
            if (self.focusCell) {
                const row = self.focusCell.row;
                const column = self.focusCell.column;
                const scrollx = self.scrollX, scrolly = self.scrollY;
                if (column.indent > 0) self.scrollX -= column.indent;
                if (row.indent > 0) self.scrollY -= row.indent;
                if (row.y + row.height >= Math.floor(self.height)) self.scrollY += scrollStep;//row.y + row.height - self.height + scrollStep;
                if (column.x + column.width >= Math.floor(self.width)) self.scrollX += scrollStep;//column.x + column.width - self.width + scrollStep;
                (scrollx !== self.scrollX || scrolly !== self.scrollY) && (self.focusCell = self.visibleCells.find(c => isPointIn({ x, y }, c)));

            }
        }
        select.x = x;
        select.y = y;
        defineSelection();
    };
    let inDraggingArea = false;
    // let inToolArea = false;

    self.cellsModified = (cells) => {//[{cell, value, preValue}]
        self.local = { action: ACTIONS.CELL_VALUE_CHANGE, cells };
        self.invalidateData();
    };

    const onMouseDown = self.onMouseDown;
    self.onMouseDown = (position) => {
        onMouseDown.call(self, position);
        if (self.mousedownConnector) return;
        if (!self.mousedownRegion.dubble) return;

        selection.fromColumn = selection.toColumn = selection.fromRow = selection.toRow = -1;
        self.selectedCells.from = undefined;
        self.selectedColumns.from = -1;
        self.selectedRows.from = -1;

        //selection cells in editing
        self.drawer.endEdit(true);

        //resizer
        captureResizer(position.x1, position.y1);
        const x = position.x1 - self.x;
        const y = position.y1 - self.y;
        if (self.resizer) {
            self.resizer.y1 = y;
            self.resizer.x1 = x;
            return;
        };

        //move table
        const dragSpace = 10;
        const dx1 = position.x1 - self.x;
        const dy1 = position.y1 - self.y;
        const dx2 = Math.abs(dx1 - self.width);
        const dy2 = Math.abs(dy1 - self.height);
        // selection.x1 = undefined;
        inDraggingArea = false;
        // inToolArea = false;
        if ((dx1 < dragSpace || dy1 < dragSpace || dx2 < dragSpace || dy2 < dragSpace) && self.moveable) {
            inDraggingArea = true;
            return;
        }

        //工具栏上操作group
        if (x < self.getToolWidth() || y < self.getToolHeight()) {
            // selection.x1 = selection.y1 = undefined;
            // inToolArea = true;
            return;
        }

        //select content
        beginSelection(x, y);
        closeSelection(x + 1, y + 1);
        self.drawer.drawSelection();
    };
    const resizerPace = 6;
    const captureResizer = (x, y) => {
        // const x = position.x1 - self.x;
        // const y = position.y1 - self.y;
        x -= self.x;
        y -= self.y;
        self.resizer = undefined;
        for (let i = 0; i < self.visibleRows.length; i++) {
            const r = self.visibleRows[i];
            if (isPointInRect({ x, y }, { x: r.x, y: r.y + r.height - resizerPace, width: self.visibleRows.width, height: resizerPace })) {
                self.resizer = { target: r, type: "row" };
                break;
            }
        };
        if (!self.resizer) {
            for (let i = 0; i < self.visibleColumns.length; i++) {
                const c = self.visibleColumns[i];
                if (isPointInRect({ x, y }, { x: c.x + c.width - resizerPace, y: c.y, width: resizerPace, height: self.visibleColumns.height })) {
                    self.resizer = { target: c, type: "column" };
                    break;
                }
            };
        }
        if (self.resizer) {
            if (self.resizer.type === "row") self.page.cursor = DIRECTION.ROW.cursor;
            else self.page.cursor = DIRECTION.COL.cursor;
        } else {
            //if (isPointInRect({ x, y }, {x:10,y:10,width:self.width-20,height:self.height-20})) self.page.cursor = CURSORS.DEFAULT;
        }
        // self.drawer.drawSelection();
    }

    const onMouseMove = self.onMouseMove;
    self.onMouseMove = position => {
        onMouseMove.call(self, position);
        if (self.mouseOnConnector) return;
        if (self.mouseOnRegion && !self.mouseOnRegion.dubble) return;
        captureResizer(position.x1, position.y1);
    };

    self.dbClick = (x, y) => {
        captureResizer(x, y);
        if (self.resizer) {
            if (self.resizer.target.c) {
                self.columnFitWidth(self.resizer.target.c);
            }
            if (self.resizer && self.resizer.target.r) {
                self.rowFitHeight(self.resizer.target.r);

            }
        } else {
            self.drawer.beginEdit();
        }
    };

    self.selectCell = (cell) => {
        beginSelection(cell.x, cell.y);
        closeSelection(cell.x + 1, cell.y + 1);
    }

    const onMouseDrag = self.onMouseDrag;
    self.onMouseDrag = (position) => {
        if ((inDraggingArea && !self.resizer) || self.mousedownConnector) {
            onMouseDrag.call(self, position);
        } else {
            if (self.resizer) {
                self.resizer.dragging = true;
                self.resizer.x2 = position.x1 - self.x;
                self.resizer.y2 = position.y1 - self.y;
                if (self.resizer.y2 - self.resizer.target.y < resizerPace) self.resizer.y2 = self.resizer.target.y + resizerPace;
                if (self.resizer.x2 - self.resizer.target.x < resizerPace) self.resizer.x2 = self.resizer.target.x + resizerPace;
            } else {
                closeSelection(position.x1 - self.x, position.y1 - self.y);
            }
        }
        self.drawer.drawSelection();
    };

    self.columnResized = column => { };
    const onMouseUp = self.onMouseUp;
    self.onMouseUp = async (position) => {
        self.inDragging = false;
        if (self.mousedownConnector !== null) {
            onMouseUp.call(self, position);
            return;
        }
        if (self.focusCell) {
            self.invalidate(true);
        }
        if (self.resizer) {
            if (self.resizer.type === "row") {
                self.resizer.target.height += self.resizer.y2 - self.resizer.y1;
                let ph;
                if (self.resizer.target.row) {
                    ph = self.resizer.target.row.h;
                    if (!Number.isNaN(self.resizer.target.height)) {
                        self.resizer.target.row.h = self.resizer.target.height > self.minHeight ? self.resizer.target.height : self.minHeight;
                    }
                } else {
                    if (self.rows[self.resizer.target.r]) {
                        ph = self.rows[self.resizer.target.r].h;
                    } else {
                        self.rows[self.resizer.target.r] = { h: self.resizer.target.height };
                    }

                    self.resizer.target.row = self.rows[self.resizer.target.r];
                }
                const row = JSON.parse(JSON.stringify(self.resizer.target.row));
                row.r = self.resizer.target.r;
                row["pre-h"] = ph;
                self.local = { action: ACTIONS.ROW_CHANGE, rows: [row] }
            } else {
                self.resizer.target.width += self.resizer.x2 - self.resizer.x1;
                let pw;
                if (self.resizer.target.column) {
                    pw = self.resizer.target.column.w
                    if (!Number.isNaN(self.resizer.target.width)) {
                        self.resizer.target.column.w = self.resizer.target.width > self.minWidth ? self.resizer.target.width : self.minWidth;
                    }
                } else {
                    if (self.columns[self.resizer.target.c]) {
                        pw = self.columns[self.resizer.target.c].w;
                    } else {
                        self.columns[self.resizer.target.c] = { w: self.resizer.target.width };
                    }
                    self.resizer.target.column = self.columns[self.resizer.target.c];
                }
                const column = JSON.parse(JSON.stringify(self.resizer.target.column));
                column.c = self.resizer.target.c;
                column["pre-w"] = pw;
                const next = self.columnResized(column);
                self.local = { action: ACTIONS.COLUMN_CHANGE, columns: next ? [column, next] : [column] };
            }
            self.invalidate(true);
            self.resizer = undefined;
            self.drawer.drawSelection();
        }
    };

    self.keyPressed = (() => {
        const space = 2;
        const step = 5;
        const goUpCell = (row, col) => {
            let delta = 0;
            const bound = findBound(row, col);
            delete self.drawer.focus.actualy;
            self.drawer.focus.actualx = self.visibleColumns.find(c => c.c >= bound.c1).x + 1
            const r1 = self.visibleRows.find(r => r.r >= bound.r1);
            if (r1.r === 0) return;
            // if (r1.indent === 0 && self.frozenY > 0 && r1.y === self.frozenY + self.getHeadHeight() && self.scrollY > 0) {
            if (r1.indent === 0 && r1.y === self.frozenY + self.getHeadHeight() && self.scrollY > 0) {
                self.scrollY -= step;
                delta = step;
            }
            return { x: self.drawer.focus.actualx, y: r1.y - 2 + delta };
        };
        const goDownCell = (row, col) => {
            let delta = 0
            const bound = findBound(row, col);
            delete self.drawer.focus.actualy;
            // (!self.drawer.focus.actualx) && (self.drawer.focus.actualx = self.visibleColumns.find(c => c.c === bound.c1).x + 1);
            self.drawer.focus.actualx = self.visibleColumns.find(c => c.c >= bound.c1).x + 1
            const r2 = self.visibleRows.filter(r => r.r <= bound.r2).maxBy(r => r.r);
            if (r2.y + r2.height === self.height) {
                self.scrollY += step;
                delta = step;
            }
            return { x: self.drawer.focus.actualx, y: r2.y + r2.height - delta + 1 };
        };
        const goLeftCell = (row, col) => {
            let delta = 0;
            const bound = findBound(row, col);
            delete self.drawer.focus.actualx;
            self.drawer.focus.actualy = self.visibleRows.find(r => r.r >= bound.r1).y + 1
            const c1 = self.visibleColumns.find(c => c.c >= bound.c1);
            if (c1.c === 0) return;
            // if (c1.indent === 0 && self.frozenX > 0 && c1.x === self.frozenX + self.getHeadWidth() && self.scrollX > 0) {
            if (c1.indent === 0 && c1.x === self.frozenX + self.getHeadWidth() && self.scrollX > 0) {
                self.scrollX -= step;
                delta = step;
            }
            return { x: c1.x - 2 + delta, y: self.drawer.focus.actualy };
        };
        const goRightCell = (row, col) => {
            let delta = 0;
            const bound = findBound(row, col);
            delete self.drawer.focus.actualx;
            // (!self.drawer.focus.actualx) && (self.drawer.focus.actualx = self.visibleColumns.find(c => c.c === bound.c1).x + 1);
            self.drawer.focus.actualy = self.visibleRows.find(r => r.r >= bound.r1).y + 1
            let c2 = self.visibleColumns.filter(c => c.c <= bound.c2).maxBy(c => c.c);
            if (c2.x + c2.width === self.width) {
                self.scrollX += step;
                delta = step;
            }
            return { x: c2.x + c2.width - delta + 1, y: self.drawer.focus.actualy };
        };

        const goUpRow = row => {
            if (!row || row.r === 0) return;
            let delta = 0;
            if (row.indent === 0 && self.frozenY > 0 && row.y === self.frozenY + self.getHeadHeight() && self.scrollY > 0) {
                self.scrollY -= step;
                delta = step;
            }
            return { x: row.x + 1, y: row.y - 2 + delta };
        };

        const goDownRow = row => {
            if (!row) return;
            let delta = 0;
            if (row.y + row.height === self.height) {
                self.scrollY += step;
                delta = step;
            }
            return { x: row.x + 1, y: row.y + row.height - delta + 1 };
        };
        const goLeftColumn = col => {
            let delta = 0;
            if (!col || col.c === 0) return;
            if (col.indent === 0 && self.frozenX > 0 && col.x === self.frozenX + self.getHeadWidth() && self.scrollX > 0) {
                self.scrollX -= step;
                delta = step;
            }
            return { x: col.x - 2 + delta, y: col.y + 1 };
        };
        const goRightColumn = col => {
            if (!col) return;
            let delta = 0;
            if (col.x + col.width >= self.width) {
                self.scrollX += step + col.x + col.width - self.width;
                delta = step;
            }
            return { x: col.x + col.width - delta + 1, y: col.y + 1 };
        };

        self.enterNextCell = cell => {
            if (!(cell || self.selectedCells.lastPosition)) return;
            let last;
            if (self.selectedCells.lastPosition) {
                last = goDownCell(self.selectedCells.lastPosition.r, self.selectedCells.lastPosition.c);
                last.x = self.visibleColumns.find(c => c.c === selection.fromColumn).x + 1;
            } else {
                last = { x: cell.x + 1, y: cell.y + 1 };
            }
            beginSelection(last.x, last.y);
            closeSelection(last.x + 1, last.y + 1);
            self.drawer.drawSelection();
        };
        const findBound = (row, col) => {
            let rlines = [], clines = []
            const findLines = () => {
                rlines = [];
                clines = [];
                const rows = [], cols = [];
                const range = self.selectedCells.actualCells;
                //find all cells in ranges
                const cells = self.cache.getRange(range.from[0], range.from[1], range.to[0], range.to[1]);
                cells.forEach(c => {
                    if (!(c.cs || c.rs)) return;
                    const rect1 = { x: c.c, y: c.r, width: c.cs - 0.1, height: c.rs - 0.1 };
                    const rect2 = {
                        x: range.from[1], y: range.from[0],
                        width: range.to[1] - range.from[1] + 0.9, height: range.to[0] - range.from[0] + 0.9
                    };
                    if (!isRectInteractRect(rect1, rect2)) return;

                    (c.cs) && cols.push(c);
                    (c.rs) && rows.push(c);
                });
                rows.orderBy("r").forEach(r => {
                    const to = r.r + r.rs - 1;
                    (rlines.length === 0) && rlines.push([r.r, to]);
                    const line = rlines[rlines.length - 1];
                    if (r.r <= line[1] && to > line[1]) line[1] = to;
                    if (r.r > line[1]) {
                        rlines.push([r.r, to]);
                    }
                })
                cols.orderBy("c").forEach(c => {
                    const to = c.c + c.cs - 1;
                    (clines.length === 0) && clines.push([c.c, to]);
                    const line = clines[clines.length - 1];
                    if (c.c <= line[1] && to > line[1]) line[1] = to;
                    if (c.c > line[1]) {
                        clines.push([c.c, to]);
                    }
                })
            };
            findLines();
            const bound = { r1: row, r2: row, c1: col, c2: col };
            const rline = rlines.find(r => r[0] <= row && r[1] >= row);
            if (rline) {
                bound.r1 = rline[0];
                bound.r2 = rline[1];
            }
            const cline = clines.find(c => c[0] <= col && c[1] >= col);
            if (cline) {
                bound.c1 = cline[0];
                bound.c2 = cline[1];
            }
            return bound;
        };

        return e => {
            if (e.ctrlKey && (e.code === "KeyC" || e.code === "KeyX" || e.code === "KeyV" || e.code === "KeyZ")) return true;
            if (e.code === "Escape") {
                self.inCopy = false;
                self.drawer.drawSelection();
            }
            if (e.code === "Delete") {
                if (!self.selectedCells.from && self.selectedColumns.from === -1 && self.selectedRows.from === -1) return true;
                const all = [];
                const addClearedCell = c => {
                    const pv = c.v;
                    delete c.v;
                    delete c.dv;
                    all.push({ r: c.r, c: c.c, v: "", "pre-v": pv });
                    // all.push({ cell, value: undefined, preValue: pv });
                }
                if (self.selectedCells.from) {
                    self.cache.forEach(c => {
                        if (c.r >= self.selectedCells.from[0] && c.c >= self.selectedCells.from[1] && c.r <= self.selectedCells.to[0] && c.c <= self.selectedCells.to[1]) {
                            addClearedCell(c);
                        }
                    });
                }
                if (self.selectedColumns.from !== -1) {
                    self.cache.forEach(c => {
                        if (c.c >= self.selectedColumns.from && c.c <= self.selectedColumns.to) {
                            addClearedCell(c)
                        }
                    })

                }
                if (self.selectedRows.from !== -1) {
                    self.cache.forEach(c => {
                        if (c.r >= self.selectedRows.from && c.r <= self.selectedRows.to) {
                            addClearedCell(c)
                        }
                    })
                }
                self.inCopy = false;
                self.invalidate(true);
                self.cellsModified(all);
                return false;
            }
            if (e.key.indexOf("Arrow") >= 0) {
                let last;
                if (e.key.indexOf("Up") >= 0) {
                    if (self.selectedCells.lastPosition) {
                        last = goUpCell(self.selectedCells.lastPosition.r, self.selectedCells.lastPosition.c);
                        if (!e.shiftKey) {
                            last.x = self.visibleColumns.find(c => c.c === selection.fromColumn).x + 1;
                        }

                    }

                    if (self.selectedRows.from !== -1 && self.selectedRows.to > 0) {
                        last = goUpRow(self.visibleRows.find(r => r.r === self.selectedRows.to));
                    }
                }
                if (e.key.indexOf("Down") >= 0) {
                    if (self.selectedCells.lastPosition) {
                        last = goDownCell(self.selectedCells.lastPosition.r, self.selectedCells.lastPosition.c)
                        if (!e.shiftKey) {
                            last.x = self.visibleColumns.find(c => c.c === selection.fromColumn).x + 1;
                        }

                    }
                    if (self.selectedRows.from !== -1) {
                        last = goDownRow(self.visibleRows.find(r => r.r === self.selectedRows.to));
                    }
                }
                if (e.key.indexOf("Left") >= 0) {
                    // const step = 5;
                    // let delta = 0;
                    if (self.selectedCells.lastPosition) {
                        last = goLeftCell(self.selectedCells.lastPosition.r, self.selectedCells.lastPosition.c);
                        if (!e.shiftKey) {
                            last.y = self.visibleRows.find(r => r.r === selection.fromRow).y + 1;
                        }

                    }

                    if (self.selectedColumns.from !== -1 && self.selectedColumns.to > 0) {
                        last = goLeftColumn(self.visibleColumns.find(c => c.c === self.selectedColumns.to));

                    }
                }
                if (e.key.indexOf("Right") >= 0) {
                    // const step = 5;
                    // let delta = 0;
                    if (self.selectedCells.lastPosition) {
                        last = goRightCell(self.selectedCells.lastPosition.r, self.selectedCells.lastPosition.c);
                        if (!e.shiftKey) {
                            last.y = self.visibleRows.find(r => r.r === selection.fromRow).y + 1;
                        }
                    }

                    if (self.selectedColumns.from !== -1) {
                        last = goRightColumn(self.visibleColumns.find(c => c.c === self.selectedColumns.to));

                    }
                }
                if (last) {
                    if (!e.shiftKey) {
                        beginSelection(last.x, last.y);
                    }
                    closeSelection(last.x + 1, last.y + 1);
                    self.drawer.drawSelection();
                }
                return false;
            }
            const validKey =
                (e.keyCode > 47 && e.keyCode < 58) || // number keys
                e.keyCode == 32 || e.keyCode == 13 || // spacebar & return key(s) (if you want to allow carriage returns)
                (e.keyCode > 64 && e.keyCode < 91) || // letter keys
                (e.keyCode > 95 && e.keyCode < 112) || // numpad keys
                (e.keyCode > 185 && e.keyCode < 193) || // ;=,-./` (in order)
                (e.keyCode > 218 && e.keyCode < 223) ||   // [\]' (in order)
                e.code === "Enter";

            if (self.drawer.focus && self.drawer.focus.contentEditable !== "true" && validKey) {//处于编辑态
                self.drawer.beginEdit();
                if (e.code !== "Enter" && self.drawer.focus.contentEditable === "true") {
                    self.drawer.focus.innerHTML = e.key;
                    document.execCommand('selectAll', false, null);
                    document.getSelection().collapseToEnd();
                }
                return false;
            }
            return true;
        };
    })();

    /**
     * 用自身canvas绘制hitregion，而不是各自一个canvas
     * huizi 2022
     */
    const myRegion = (getx, gety, getWdith, getHeight) => {
        const r = hitRegion(self, getx, gety, getWdith, getHeight);
        r.draw = context => {
            if(!context) return;
            r.drawStatic(context);
        }
        r.drawDynamic = (context, x, y, width, height) => {
        };
        return r;
    };

    const groupRegion = (group, from, to) => {
        const toolWidth = self.getToolWidth();
        const toolHeight = self.getToolHeight();
        const expanded = group[3] === 1;
        const level = group[4];
        const type = group[2];
        const getx = () => {
            if (type === 0) {//行
                return toolWidth - GRID_TOOL_WIDTH * level;
            } else {//列
                if (from) return from.x + from.width / 2;
                else {
                    let next = self.visibleColumns.find(c => c.c > group[0]);
                    if (next && next.c <= group[1] + 1) return next.x;
                }
            }
        };

        const gety = () => {
            if (type === 1) {//行
                return toolHeight - GRID_TOOL_WIDTH * level;
            } else {
                if (from) return from.y + from.height / 2;
                else {
                    let next = self.visibleRows.find(r => r.r > group[0]);
                    if (next && next.r <= group[1] + 1) return next.y;
                }
            }
        };

        const getWidth = () => {
            if (type === 1 && expanded) {
                if (to) {
                    return to.x + to.width - getx() + GRID_TOOL_WIDTH;
                } else {
                    const next = self.visibleColumns.find(c => c.c > group[1]);
                    if (next) return next.x - getx() + GRID_TOOL_WIDTH;
                    else return self.width;
                }
            } else return GRID_TOOL_WIDTH;
        };

        const getHeight = () => {
            if (type === 0 && expanded) {
                if (to) {
                    return (to.y + to.height - gety() + GRID_TOOL_WIDTH);
                } else {
                    const next = self.visibleRows.find(r => r.r > group[1]);
                    if (next) return next.y - gety() + GRID_TOOL_WIDTH;
                    else return self.height;
                    // : (self.visibleRows.min(r => r.r) > group[0] ? TOOL_WIDTH : self.height);
                }
            } else return GRID_TOOL_WIDTH;
        };

        const region = myRegion(() => getx() + getWidth() - GRID_TOOL_WIDTH, () => gety() + getHeight() - GRID_TOOL_WIDTH, () => GRID_TOOL_WIDTH, () => GRID_TOOL_WIDTH);
        region.type = "table-group";
        region.drawStatic = (context) => {
            if (type === 0) {//行
                if (group[0] > self.visibleRows.max(r => r.r)) return;
                // if (group[1] < self.visibleRows.min(r => r.r)) return;
            } else {
                if (group[0] > self.visibleColumns.max(c => c.c)) return;
                // if (group[1] < self.visibleColumns.min(c => c.c)) return;

            }
            const pace = 6;
            const x = getx();
            if (x === undefined) return;
            const y = gety();
            if (y === undefined) return;
            const x1 = x + getWidth() - GRID_TOOL_WIDTH / 2 - pace;
            const y1 = y + getHeight() - GRID_TOOL_WIDTH / 2 - pace;
            const x2 = self.getHeadWidth() + 1, y2 = self.getHeadHeight() + 1;
            const w = self.width - self.getHeadWidth(), h = self.height - self.getHeadHeight();
            let clipRect;
            if (type === 0) {
                clipRect = [0, y2, self.getToolWidth(), h];
            }
            if (type === 1) {
                clipRect = [x2, 0, w, self.getToolWidth()];
            }
            self.clip(context, clipRect[0], clipRect[1], clipRect[2], clipRect[3], () => {
                context.font = "italic bold 18px Arial";
                context.beginPath();
                context.strokeStyle = "gray";
                context.lineWidth = 1;
                if (type === 0) {
                    context.moveTo(x1 + 2 * pace, y);
                    context.lineTo(x1 + pace, y);
                    context.lineTo(x1 + pace, y1);
                } else {
                    context.moveTo(x, y1 + 2 * pace);
                    context.lineTo(x, y1 + pace);
                    context.lineTo(x1, y1 + pace);
                }
                context.stroke();
                if (type === 1 && !self.visibleColumns.find(c => c.c === group[1] + 1)) return;
                if (type === 0 && !self.visibleRows.find(r => r.r === group[1] + 1)) return;
                context.roundRect(x1, y1, 2 * pace, 2 * pace, 2, expanded ? "white" : "dimgray", "", 1);
                context.beginPath();
                context.moveTo(x1 + 2, y1 + pace);
                context.lineTo(x1 + 2 * pace - 2, y1 + pace);
                if (expanded) {
                    context.strokeStyle = "black";
                } else {
                    context.strokeStyle = "white";
                    context.moveTo(x1 + pace, y1 + 2);
                    context.lineTo(x1 + pace, y1 + 2 * pace - 2);

                }
                context.stroke();
            });
        };

        region.click = (x, y) => {
            group[3] = Math.abs(group[3] - 1);
            if (group[3] === 1) {
                const RC = (type === 0) ? "rows" : "columns";
                self.hiddens[RC] && self.hiddens[RC].remove(i => i >= group[0] && i <= group[1]);
            }
            //todo
            self.invalidate(true);
            // self.selectedCells.remove(c => !self.visibleCells.contains(c1 => c1.r === c.r && c1.c === c.c));
            defineSelection();
            self.drawer.drawSelection();
        };

        const getBound = region.getBound;
        region.getBound = () => {
            const bound = getBound.call(region);
            return bound;
        }
        return region;

    };

    const hiddenRegion = (pre, next, preIndex, nextIndex, type) => {
        const width = 16;
        const getx = () => {
            if (type === 0) {//行
                return self.getToolWidth();
            } else {//列
                return (pre ? pre.x + pre.width : next.x) - width / 2;
            }
        };

        const gety = () => {
            if (type === 1) {//列
                return self.getToolHeight() + self.headHeight / 2 - width / 2;
            } else {
                return (pre ? pre.y + pre.height : next.y) - width / 2;
            }
        };
        const region = myRegion(() => getx(), () => gety(), () => width, () => width);
        region.type = "table-hidden";
        region.drawStatic = (context) => {
            const delta = 5;
            let x = getx();
            let y = gety();
            const x2 = self.getHeadWidth() + 1, y2 = self.getHeadHeight() + 1;
            const w = self.width - self.getHeadWidth(), h = self.height - self.getHeadHeight();
            let clipRect;
            if (type === 0) {
                clipRect = [self.getToolWidth(), y2, self.headWidth, h];
            }
            if (type === 1) {
                clipRect = [x2, self.getToolHeight(), w, self.headHeight];
            }
            self.clip(context, clipRect[0], clipRect[1], clipRect[2], clipRect[3], () => {
                context.fillStyle = "black";
                if (type === 0) {
                    context.beginPath();
                    context.moveTo(x + width / 2, y);
                    context.lineTo(x + width / 2 - delta / 2, y + delta);
                    context.lineTo(x + width / 2 + delta / 2, y + delta);
                    context.closePath();
                    context.fill();
                    context.beginPath();
                    context.moveTo(x + width / 2, y + width);
                    context.lineTo(x + width / 2 - delta / 2, y + width - delta);
                    context.lineTo(x + width / 2 + delta / 2, y + width - delta);
                    context.closePath();
                    context.fill();
                } else {
                    context.beginPath();
                    context.moveTo(x, y + width / 2);
                    context.lineTo(x + delta, y + width / 2 - delta / 2);
                    context.lineTo(x + delta, y + width / 2 + delta / 2);
                    context.closePath();
                    context.fill();
                    context.beginPath();
                    context.moveTo(x + width, y + width / 2);
                    context.lineTo(x + width - delta, y + width / 2 - delta / 2);
                    context.lineTo(x + width - delta, y + width / 2 + delta / 2);
                    context.closePath();
                    context.fill();
                }
            })
        };
        region.click = () => {
            self.hiddens[(type === 0 ? "rows" : "columns")].remove(h => h > preIndex && h < nextIndex);
            self.invalidate(true);
        };

        return region;
    }

    self.arrangeRegions = () => {
        self.regions.remove(r => r.type === "table-group" || r.type === "table-hidden");
        //group regions
        if (self.groups.length > 0) {
            const min_row = self.visibleCells.min(c => c.r);
            const max_row = self.visibleCells.max(c => c.r);
            const min_col = self.visibleCells.min(c => c.c);
            const max_col = self.visibleCells.max(c => c.c);
            self.groups.forEach(g => {
                if (g[2] === 0) {//行
                    if (min_row > g[1] + 1 || max_row < g[0]) return;
                    const from = self.visibleRows.find(r => r.r === g[0]);
                    const to = self.visibleRows.find(r => r.r === g[1]);
                    groupRegion(g, from, to);
                } else {//列
                    if (min_col > g[1] + 1 || max_col < g[0]) return;
                    const from = self.visibleColumns.find(c => c.c === g[0]);
                    const to = self.visibleColumns.find(c => c.c === g[1]);
                    groupRegion(g, from, to);
                }
            });
        }
        //hide regions
        if (self.hiddens["columns"] && self.withColumnHead) {
            // const cs = [];
            let pre, next;
            self.hiddens["columns"].orderBy().forEach((c, i) => {
                if (i !== 0 && c < next) return;
                pre = c - 1;
                next = c + 1;
                while (self.hiddens["columns"].indexOf(next) >= 0) next++;
                // if (cs.indexOf(next) < 0) {
                //     cs.push(next);
                //     hiddenRegion(pre, next, 1);
                // }
                const preCol = self.visibleColumns.find(c => c.c === pre), nextCol = self.visibleColumns.find(c => c.c === next);
                (preCol || nextCol) && hiddenRegion(preCol, nextCol, pre, next, 1);
            })
        }
        if (self.hiddens["rows"] && self.withRowHead) {
            // const rs = [];
            let pre, next;
            self.hiddens["rows"].orderBy().forEach((r, i) => {
                if (i !== 0 && r < next) return;
                pre = r - 1;
                next = r + 1;
                while (self.hiddens["rows"].indexOf(next) >= 0) next++;
                // if (rs.indexOf(next) < 0) {
                //     rs.push(next);
                //     hiddenRegion(pre, next, 0);
                // }
                const preRow = self.visibleRows.find(r => r.r === pre), nextRow = self.visibleRows.find(r => r.r === next);
                (preRow || nextRow) && hiddenRegion(preRow, nextRow, pre, next, 0);
            })

        }
    };

    // self.invalidateArea = (rect) => {
    //     const off = 10;
    //     self.drawer.drawCells(self.visibleCells.filter(c => isRectInteractRect(rect,
    //         { x: (c.tx && c.tx < c.x ? c.tx : c.x), y: c.y, width: (c.twidth && c.twidth > c.width ? c.twidth : c.width), height: c.height })),
    //         { x: rect.x - off, y: rect.y - off, width: rect.width + 2 * off, height: rect.height + 2 * off });
    // };

    const invalidate = self.invalidate;
    self.invalidate = all => {
        invalidate.call(self);
        if (all) {
            defineSelection();
        }
        self.drawer.drawSelection();
    };
    self.invalidateData = () => {
        self.cache.forEach(c => c.dv !== c.v && (delete c.dv));//出现性能问题时，再解决性能问题
        self.invalidate(true);
    }

    // self.addDetection(["changes"], (property, value, preValue) => {
    //     if (value === preValue) {
    //         return;
    //     }
    //     if (!value) return;
    //     if (value.t === 0) self.updateData(value.v);
    //     if (value.t === 1) self.updateRows(value.v);
    //     if (value.t === 2) self.updateColumns(value.v);
    //     if (value.t === 3) self.updateBorders(value.v);
    //     if (value.t === 4) self.addRows(value.v, value.i);
    //     if (value.t === 5) self.addColumns(value.v, value.i);
    //     if (value.t === 6) self.removeRows(value.fi, value.ti);
    //     if (value.t === 7) self.removeColumns(value.f, value.ti);

    // });

    self.addDetection(["cells"], (property, value, preValue) => {
        self.invalidate(true);
    });

    self.addDetection(["scrollX", "scrollY"], (property, value, preValue) => {
        if (value === preValue) return;
        self[property] = Math.floor(value);
        (value < 0) && (self[property] = 0);
        const delta = self[property] - preValue;
        // const realFrozenX = self.frozenX + self.getHeadWidth();
        // const realFrozenY = self.frozenY + self.getHeadHeight();
        if (property === "scrollX") {
            if (cornerSelection && !cornerSelection.xInFrozen) {
                cornerSelection.x1 -= delta;
                cornerSelection.dx -= delta;
            }
            if (selection.xInFrozen === false) {
                selection.x1 -= delta;
                selection.dx -= delta;
            }
            // self.selectedColumns.forEach(c => (realFrozenX < c.x) && (c.x -= delta));
            // self.selectedCells.forEach(c => (realFrozenX < c.x) && (c.x -= delta));
        } else {
            if (cornerSelection && !cornerSelection.yInFrozen) {
                cornerSelection.y1 -= delta;
                cornerSelection.dy -= delta;
            }
            if (selection.yInFrozen === false) {
                selection.y1 -= delta;
                selection.dy -= delta;
            }
            // self.selectedRows.forEach(c => (realFrozenY < c.y) && (c.y -= delta));
            // self.selectedCells.forEach(c => (realFrozenY < c.y) && (c.y -= delta));
        }
        //将所有cell value按照scroll位移
        const expired = [];
        for (let i = 0; i < self.drawer.container.children.length; i++) {
            const c = self.drawer.container.children[i];
            c.style[property === "scrollX" ? "left" : "top"] = (c["offset" + (property === "scrollX" ? "Left" : "Top")] - delta) + "px";
            if (c.offsetLeft + c.offsetWidth < 0 || c.offsetTop + c.offsetHeight < 0 || c.offsetLeft > self.drawer.container.offsetWidth || c.offsetTop > self.drawer.container.offsetHeight) {
                expired.push(c);
            }
        };
        expired.forEach(e => e.remove());
        self.invalidate(true);
        if (self.drawer.focus.contentEditable === "true") {
            const cell = self.visibleCells.find(c => c.r === self.drawer.focus.attach.r && c.c === self.drawer.focus.attach.c);
            if (cell) {
                self.drawer.focus.attach = cell;
                self.drawer.focus.style.left = cell.x + "px";
                self.drawer.focus.style.top = cell.y + "px";
                self.drawer.focus.style.minWidth = cell.width + "px";
                self.drawer.focus.style.minHeight = cell.height + "px";
            } else {
                self.endEdit();
            }
        }
    });

    self.addDetection(["local"], function (property, value, preValue) {
        (!value.etag) && (value.etag = uuid());
        if (self.commandIgnored) return;
        if (self.changeIgnored) return;

        gridCommand(self.page, self, value);
    });

    self.ignoreCommand = ops => {
        self.commandIgnored = true;
        try {
            ops();
        } catch { }
        self.commandIgnored = false;
    }

    // self.serializedFields.batchAdd("cells", "rows", "columns", "borders", "changes", "groups", "frozens", "hiddens",
    //     "headBorderColor", "headBackColor", "headTextColor", "headWidth", "headHeight", "cellWidth", "cellHeight", "minWidth", "minHeight",
    //     "minRow", "minColumn", "maxRow", "maxColumn", "data", "withColumnHead", "withRowHead", "wrap");

    return self;
};

const table = (id, x, y, width, height, parent) => {
    const self = grid(id, x, y, width, height, parent);
    self.mode = GRID_MODE.TABLE;
    self.type = "table";
    self.cellWidth = 100;
    self.cellHeight = 40;
    self.minHeight = 40;
    self.scrollStep = 0;
    self.fontColor = "steelblue";
    self.fontSize = 18;
    self.withRowHead = self.withColumnHead = false;
    self.wrap = true;
    self.setTable = (rowCount, columnCount, template) => {
        self.minRow = self.minColumn = 0;
        self.maxRow = rowCount - 1;
        self.maxColumn = columnCount - 1;
        self.width = self.cellWidth * columnCount;
        self.height = self.cellHeight * rowCount;
        if (template) {

        } else {
            const rows = {};
            rows[0] = { bk: "lightblue", fs: 23, b: true, fc: "white" };

            self.rows = rows;

        }
        // self.invalidate(true);
    };

    self.unselected = () => {
        self.drawer.endEdit();
    };

    self.columnResized = column => {
        if (column.c === self.maxColumn) return;
        let next = self.columns[(column.c + 1)];
        let preWidth = undefined;
        if (!next) {
            next = { c: column.c + 1 };
            self.columns[next.c] = next;
        } else {
            preWidth = next.w;
        }
        next.w = (preWidth ? preWidth : self.cellWidth) - column.w + (column["pre-w"] ? column["pre-w"] : self.cellWidth);
        const copy = JSON.parse(JSON.stringify(next));
        copy["pre-w"] = preWidth;
        return copy;
    };
    const calculateTableBound = () => {
        let width = 1;
        for (let i = 0; i <= self.maxColumn; i++) {
            width += (self.columns[i] && self.columns[i].w) ? self.columns[i].w : self.cellWidth;
        }
        let height = 1;
        for (let i = 0; i <= self.maxRow; i++) {
            height += (self.rows[i] && self.rows[i].h) ? self.rows[i].h : self.cellHeight;
        }

        return { width, height };
    }

    const beginEdit = self.drawer.beginEdit;
    self.drawer.beginEdit = () => {
        const focus = beginEdit.call(self.drawer);
        // const id = self.page.div.id + "|" + self.id + "|" + focus.attach.r + "|" + focus.attach.c;
        const row = self.rows[focus.attach.r] ? self.rows[focus.attach.r] : {};
        const column = self.columns[focus.attach.c] ? self.columns[focus.attach.c] : {};
        let cell = self.cells.find(c => c.c === focus.attach.c && c.r === focus.attach.r);
        (!cell) && (cell = {});
        // const width = column.w ? column.w : self.cellWidth;
        // const height = row.h ? row.h : self.cellHeight;

        focus.style.marginTop = focus.style.marginBottom = "0px";
        focus.style.fontFamily = row.ff ? row.ff : self.fontFace;
        focus.style.fontSize = (row.fs ? row.fs : self.fontSize) + "px";
        focus.style.fontWeight = row.b ? true : (self.fontWeight === FONT_WEIGHT.BOLD);
        focus.style.fontStyle = row.fstyle ? row.fstyle : self.fontStyle;
        focus.style.color = row.fc ? row.fc : self.fontColor;

        focus.style.textAlign = "left";
        (cell.ha === "c") && (focus.style.textAlign = "center");
        (cell.ha === "r") && (focus.style.textAlign = "right");
        focus.style.width = focus.style.minWidth = focus.style.maxWidth = (focus.clientWidth - self.getPadLeft()) + "px";
        focus.style.paddingLeft = self.getPadLeft() + "px";
        focus.style.padingTop = self.getPadTop() + "px";

    };

    const endEdit = self.drawer.endEdit;
    self.drawer.endEdit = () => {
        const cell = endEdit.call(self);
        if (cell === undefined) return;
        self.rowFitHeight(cell.r);
        self.columnFitWidth(cell.c);
    }
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        let preWidth, preHeight;
        initConnectors.call(self);
        self.connectors.forEach(c => {
            c.onMouseDown = position => {
                preWidth = self.width;
                preHeight = self.height;
            };
            c.release = position => {
                position.context.command = "";
                self.syncColumnsRowsSize(preWidth, preHeight, self.width, self.height);
                // self.local = { action: ACTIONS.ROW_CHANGE, rows };
                // self.local = { action: ACTIONS.COLUMN_CHANGE, columns };
            }
        })
    }
    self.syncColumnsRowsSize = (preWidth, preHeight, width, height) => {
        const rows = [], columns = [];
        const percent_x = width / preWidth;
        const percent_y = height / preHeight;
        for (let i = 0; i <= self.maxRow; i++) {
            (!self.rows[i]) && (self.rows[i] = {});
            const row = { "pre-h": self.rows[i].h, r: i };
            if (self.rows[i].h) {
                self.rows[i].h *= percent_y;
            } else {
                self.rows[i].h = self.cellHeight * percent_y;
            }
            if (self.rows[i].h < self.minHeight) self.rows[i].h = self.minHeight;
            self.rowFitHeight(i);
            row.h = self.rows[i].h;
            rows.push(row);
        }
        for (let i = 0; i <= self.maxColumn; i++) {
            (!self.columns[i]) && (self.columns[i] = {});
            const column = { "pre-w": self.columns[i].w, c: i };
            if (self.columns[i].w) {
                self.columns[i].w *= percent_x;
            } else {
                self.columns[i].w = self.cellWidth * percent_x;
            }
            if (self.columns[i].w < self.minWidth) self.columns[i].w = self.minWidth;
            column.w = self.columns[i].w;
            columns.push(column);
        }

        const size = calculateTableBound();
        // if (size.width !== self.width || size.height !== self.height) {
        self.width = size.width;
        self.height = size.height;
        self.invalidate(true);
        // }


        !self.changeIgnored && (self.local = { action: ACTIONS.ACTIONS, actions: [{ action: ACTIONS.ROW_CHANGE, rows }, { action: ACTIONS.COLUMN_CHANGE, columns }], preWidth, preHeight, width, height });

    };
    const setLocal = self.setLocal;
    self.setLocal = local => {
        if (local.action === ACTIONS.ACTIONS) {
            self.syncColumnsRowsSize(local.preWidth, local.preHeight, local.width, local.height);

        } else {
            setLocal.call(self, local);
        }
    }
    self.initialize = () => {
        if (self.maxColumn > 0) {
            self.width = 900;
            self.cellWidth = self.width / (self.maxColumn + 1);
            self.height = self.cellHeight * (self.maxRow + 1);
        } else {
            self.setTable(4, 4);
        }
    }

    const invalidate = self.invalidate;
    self.invalidate = all => {
        invalidate.call(self, all);
        const size = calculateTableBound();
        if (size.width !== self.width || size.height !== self.height) {
            self.width = size.width;
            self.height = size.height;
            self.invalidate();
        }
    }
    return self;
};

const gridCommand = (page, g, local) => {
    const self = command(page, [g],"gridOperation");
    // self.type = "gridOperation";
    const action = local.action;
    let grid;

    const cellsValueChange = (() => {
        const execute = (undo, local) => {
            const cells = [];//协作
            local.cells.forEach(cell => {
                let c_now = grid.cache.find(c => c.r === cell.r && c.c === cell.c);
                if (!c_now) {
                    c_now = { c: cell.c, r: cell.r };
                    grid.cache.push(c_now);
                }
                const c_sync = {};//协作
                cells.push(c_sync);
                // if (c_now) {
                for (let f in cell) {
                    if (undo) {
                        if (f.indexOf("pre-") !== 0) continue;
                        c_now[f.substring(4)] = cell[f];
                        c_sync[f.substring(4)] = cell[f];
                    } else {
                        if (f.indexOf("pre-") === 0) continue;
                        c_now[f] = cell[f];
                        c_sync[f] = cell[f];
                    }

                }
                c_sync.r = cell.r;
                c_sync.c = cell.c;
                if (c_now.r === undefined) {
                    grid.cache.splice(grid.cache.indexOf(c_now), 1);
                    c_sync.delete = true;
                }
                // }

            })

            if (undo) {
                local.cells["pre-maxRow"] && (grid.maxRow = local.cells["pre-maxRow"]);
                local.cells["pre-maxColumn"] && (grid.maxColumn = local.cells["pre-maxColumn"]);
            } else {
                local.cells["maxRow"] && (grid.maxRow = local.cells["maxRow"]);
                local.cells["maxColumn"] && (grid.maxColumn = local.cells["maxColumn"]);
            }

            grid.invalidateData();
            grid.ignoreCommand(() => {
                grid.local = { action, cells };
            })
        }
        return {
            redo: (local) => {
                execute(false, local)
            },
            undo: (local) => {
                execute(true, local);
            }
        };
    })();

    const columnAdd = (isBefore, isCopy) => {
        return {
            redo: (local) => {
                grid.ignoreCommand(() => {
                    const from = local.from;
                    const steps = local.steps;
                    if (isCopy) {
                        grid.duplicateColumns(from, steps);
                    } else {
                        isBefore ? grid.addColumnsBefore(from, steps) : grid.addColumnsAfter(from, steps);
                    }

                });
            },
            undo: (local) => {
                grid.ignoreCommand(() => {
                    const from = local.from;
                    const steps = local.steps;
                    isBefore ? grid.deleteColumns(from, steps) : grid.deleteColumns(from + steps, steps);
                });
            }
        };

    };

    const rowAdd = (isBefore, isCopy) => {
        return {
            redo: (local) => {
                grid.ignoreCommand(() => {
                    const from = local.from;
                    const steps = local.steps;
                    if (isCopy) {
                        grid.duplicateRows(from, steps);
                    } else {
                        isBefore ? grid.addRowsBefore(from, steps) : grid.addRowsAfter(from, steps);
                    }

                });
            },
            undo: (local) => {
                grid.ignoreCommand(() => {
                    const from = local.from;
                    const steps = local.steps;
                    isBefore ? grid.deleteRows(from, steps) : grid.deleteRows(from + steps, steps);
                });
            }
        };

    };

    const columnChange = (() => {
        const execute = (isUndo, local) => {
            grid.ignoreCommand(() => {
                const impacts = [];
                local.columns.forEach(c => {
                    const column = grid.columns[c.c];
                    for (let f in c) {
                        if (isUndo) {
                            if (f.indexOf("pre-") !== 0) continue;
                            column[f.substring(4)] = c[f];
                        } else {
                            if (f.indexOf("pre-") === 0) continue;
                            column[f] = c[f];
                        }
                    }
                    const impact = JSON.parse(JSON.stringify(column));
                    impact.c = c.c;
                    impacts.push(impact);
                });
                grid.invalidate();
                grid.local = { action: ACTIONS.COLUMN_CHANGE, columns: impacts }
            });
        }
        return {
            redo: (local) => {
                execute(false, local);
            },
            undo: (local) => {
                execute(true, local);
            }
        };
    })();

    const rowChange = (() => {
        const execute = (isUndo, local) => {
            grid.ignoreCommand(() => {
                const impacts = [];
                local.rows.forEach(r => {
                    const row = grid.rows[r.r];
                    for (let f in r) {
                        if (isUndo) {
                            if (f.indexOf("pre-") !== 0) continue;
                            row[f.substring(4)] = r[f];
                        } else {
                            if (f.indexOf("pre-") === 0) continue;
                            row[f] = r[f];
                        }
                    }
                    const impact = JSON.parse(JSON.stringify(row));
                    impact.r = r.r;
                    impacts.push(impact);
                });
                grid.invalidate();
                grid.local = { action: ACTIONS.ROW_CHANGE, rows: impacts }
            });
        }
        return {
            redo: (local) => {
                execute(false, local);
            },
            undo: (local) => {
                execute(true, local);
            }
        };
    })();

    const transaction = (() => {
        const execute = (isUndo, local) => {
            const impacted = [];
            local.actions.forEach(action => {
                isUndo ? actions[action.action].undo(action) : actions[action.action].redo(action);
                impacted.push(grid.local);
            })
            grid.ignoreCommand(() => {
                grid.local = { action: ACTIONS.ACTIONS, actions: impacted, preWidth: (isUndo ? local.width : local.preWidth), preHeight: (isUndo ? local.height : local.preHeight), width: (!isUndo ? local.width : local.preWidth), height: (!isUndo ? local.height : local.preHeight) };
            });
        }

        return {
            redo: (local) => {
                execute(false, local);
            },
            undo: (local) => {
                execute(true, local);
            }
        };
    })();

    const actions = {};
    actions[ACTIONS.ACTIONS] = transaction;
    actions[ACTIONS.CELL_VALUE_CHANGE] = cellsValueChange;
    actions[ACTIONS.COLUMN_ADD_BEFORE] = columnAdd(true, false);
    actions[ACTIONS.COLUMN_ADD_AFTER] = columnAdd(false, false);
    actions[ACTIONS.COLUMN_DUPLICATE] = columnAdd(true, true);
    actions[ACTIONS.ROW_ADD_BEFORE] = rowAdd(true, false);
    actions[ACTIONS.ROW_ADD_AFTER] = rowAdd(false, false);
    actions[ACTIONS.ROW_DUPLICATE] = rowAdd(true, true);
    actions[ACTIONS.COLUMN_CHANGE] = columnChange;
    actions[ACTIONS.ROW_CHANGE] = rowChange;

    self.redo = (host) => {
        grid = host.shapes.find(s => s.id === g.id);
        grid && actions[action].redo(local);
    };

    self.undo = (host) => {
        grid = host.shapes.find(s => s.id ===g.id);
        grid && actions[action].undo(local);
    };

    return self;
};

const gridDrawer = (shape, div, x, y) => {
    const self = canvasDrawer(shape, div, x, y);
    self.container = self.createElement("div", "data container:" + shape.id);
    // self.container.id = "data container:" + shape.id;
    self.container.style.position = "absolute";
    self.container.style.overflow = "hidden";
    self.parent.appendChild(self.container);

    self.boundary = self.createElement("div", "boundary:" + shape.id);
    // self.boundary.id = "boundary:" + shape.id;
    self.boundary.style.position = "absolute";
    self.boundary.style.overflow = "hidden";
    self.parent.appendChild(self.boundary);


    const getSnapshot = self.getSnapshot;
    self.getSnapshot = () => {
        const node = getSnapshot.call(self);
        const tb = document.createElement("table");
        tb.style.width = "100%";
        tb.style.height = "100%";
        tb.style.borderSpacing = "0px";
        const colNum = self.visibleColumns.length;
        const rowNum = self.visibleRows.length;
        for (let i = 0; i < rowNum; i++) {
            const tr = document.createElement("tr");
            tb.appendChild(tr);
            for (let j = 0; i < colNum; j++) {
                const td = document.createElement("td");
                td.style.border = "solid 1px gray";
                tr.appendChild(td);
            }
        }
        node.appendChild(tb);
        return node;
    };

    self.containsBack = (x, y) => true;
    const resize = self.resize;
    self.resize = () => {
        resize.call(self);
        const headWidth = shape.getHeadWidth();
        const headHeight = shape.getHeadHeight();
        self.updateIfChange(self.container.style, 'left', headWidth + "px", 'container_left');
        self.updateIfChange(self.container.style, 'top', headHeight + "px", 'container_top');
        self.updateIfChange(self.container.style, 'width', (shape.width - 2 - headWidth) + "px", 'container_width');
        self.updateIfChange(self.container.style, 'height', (shape.height - 2 - headHeight) + "px", 'container_height');

        self.updateIfChange(self.boundary.style, 'left', "0px", 'container_left');
        self.updateIfChange(self.boundary.style, 'top', "0px", 'container_top');
        self.updateIfChange(self.boundary.style, 'width', (shape.width) + "px", 'container_width');
        self.updateIfChange(self.boundary.style, 'height', (shape.height) + "px", 'container_height');
    };

    self.beginEdit = () => {
        if (!self.focus) return;
        if (self.focus.style.visibility === "hidden") return;
        if (shape.focusCell.readonly) return;

        if (shape.inCopy) {
            shape.inCopy = false;
            self.drawSelection();
        }
        self.focus.attach = shape.focusCell;
        let isWrap = false;
        if (shape.rows[self.focus.attach.r] && shape.rows[self.focus.attach.r].wrap !== undefined) {
            isWrap = shape.rows[self.focus.attach.r].wrap === 1;
        } else {
            isWrap = shape.wrap === true;
        }
        // const isWrap = (self.focus.attach.cell && self.focus.attach.cell.wrap === 1);
        self.focus.contentEditable = true;
        self.focus.style.minWidth = self.focus.style.width;
        self.focus.style.width = "auto";//(self.focus.attach.cell && self.focus.attach.cell.wrap === 1) ? "auto" : (self.focus.attach.width + "px");
        self.focus.style.whiteSpace = isWrap ? "pre-wrap" : "nowrap";
        // self.focus.style.minWidth = (self.focus.attach.width-2) + "px";
        if (isWrap) self.focus.style.maxWidth = self.focus.style.minWidth;
        else self.focus.style.maxWidth = "";
        self.focus.style.background = self.focus.attach.bk ? self.focus.attach.bk : ((shape.rows[self.focus.attach.r] && shape.rows[self.focus.attach.r].bk) ? shape.rows[self.focus.attach.r].bk : "white");
        self.focus.innerHTML = (self.focus.attach.cell && self.focus.attach.cell.v !== undefined) ? self.focus.attach.cell.v : "";
        self.focus.focus();
        displayEditCells(self.focus.attach.r, self.focus.attach.c, self.focus.innerText);
        document.execCommand('selectAll', false, null);
        document.getSelection().collapseToEnd();
        self.focus.onkeypress = e => {
            if (e.code === "Enter") {
                if (e.shiftKey) {
                    self.focus.innerHTML += "</br>";
                    document.execCommand('selectAll', false, null);
                    document.getSelection().collapseToEnd();
                } else {
                    self.endEdit();
                    shape.enterNextCell(self.focus.attach);
                }
            }
        };
        self.focus.onkeydown = e => {
            if ((e.code === "ArrowRight" || e.code === "ArrowDown") && window.getSelection().anchorOffset === self.focus.innerText.length) self.endEdit();
            if ((e.code === "ArrowLeft" || e.code === "ArrowUp") && window.getSelection().anchorOffset === 0) self.endEdit();
        };
        self.focus.onkeyup = e => {
            if (e.code === "Escape") {
                self.cancelEdit();
            } else {
                displayEditCells(self.focus.attach.r, self.focus.attach.c, self.focus.innerText);
                currentDisplayer = editDisplayers.getFromCaret(window.getSelection().anchorOffset);
            }
        };
        self.focus.onmousedown = e => {
            currentDisplayer = editDisplayers.getFromCaret(window.getSelection().anchorOffset);
        };
        return self.focus;
    };

    self.cancelEdit = () => {
        self.focus.innerHTML = "";
        self.focus.contentEditable = false;
        shape.selectCell(self.focus.attach);
        editDisplayers.clear();
        self.drawSelection();
        // shape.invalidate(true);
    };

    self.endEdit = keepWidthFomular => {
        const checkIfStillInEditing = () => {
            const interpret = interpreter(shape, self.focus.innerText, self.focus.attach.r, self.focus.attach.c);
            return keepWidthFomular && interpret.isFormular() && (interpret.isOperator() || currentDisplayer);//add more here
        };
        if (!(self.focus && self.focus.contentEditable === "true" && self.focus.attach)) return;
        if (checkIfStillInEditing()) return false;
        if (interpreter(shape, self.focus.innerText, self.focus.attach.r, self.focus.attach.c).inRecursion()) {
            showDialog('发现循环引用，请修改公式！');
        }
        self.focus.contentEditable = false;

        editDisplayers.clear();
        if (!self.focus.attach.cell) {
            if (self.focus.innerText === "") return;
            const cell = { r: self.focus.attach.r, c: self.focus.attach.c, v: "" };
            shape.cache.push(cell);
            self.focus.attach.cell = cell;
        }
        const pv = self.focus.attach.cell.v;
        self.focus.attach.cell.v = self.focus.innerText === "" ? undefined : self.focus.innerHTML;
        if (pv !== self.focus.attach.cell.v) self.focus.attach.cell.dv = undefined;
        self.focus.innerHTML = "";
        shape.invalidate();
        const cell = JSON.parse(JSON.stringify(self.focus.attach.cell));
        cell["pre-v"] = pv;
        shape.cellsModified([cell]);
        // shape.cellsModified([{ cell: self.focus.attach.cell, value: self.focus.attach.cell.v, preValue: pv }]);
        shape.cellChanged && shape.cellChanged(self.focus.attach.cell, "v", pv);

        for (let f in self.focus.attach.cell) {
            if (self.focus.attach.cell[f]) return cell;//说明cell有属性
        }
        //cell没有属性，删除该cell
        if (!self.focus.attach.cell) {
            shape.cache.remove(self.focus.attach.cell.r, self.focus.attach.cell.c, self.focus.attach.cell.r, self.focus.attach.cell.c);
            delete self.focus.attach.cell;
        }
        return cell;
    };


    /**
     * 公式里的cells在界面上的框选
     * huizi 2022
     */
    const cellsRefDisplayers = shape => {
        const colors = ["steelblue", "darkorange", "OLIVE", "teal", "darkred", "green", "#EAC117", "red", "#CD7F32"];
        const displayers = [];
        const displayer = () => {
            const d = {};
            const div = self.createElement("div", "formular_frame:" + shape.id);
            div.style.position = "absolute";
            div.style.background = "rgba(70,130,180,0.1)";
            d.display = (c, index) => {
                !index && (index = d.index);
                div.style.border = "1px dashed " + colors[(index % colors.length)];
                let x, y, width, height;
                if (c[0] === "col") {
                    d.id = div.id = c[1] + ":" + c[2];
                    const col_from = shape.visibleColumns.find(col => col.c === util.charToIndex(c[1]));
                    const col_to = c[1] === c[2] ? col_from : shape.visibleColumns.find(col => col.c === util.charToIndex(c[2]));
                    x = col_from ? col_from.x - 1 : -1;
                    y = -shape.getHeadHeight();
                    width = col_to ? col_to.x + col_to.width - x - 2 : (x === -1 ? -1 : shape.width + 1);
                    height = shape.height;
                    x -= shape.getHeadWidth();

                } else if (c[0] === "row") {
                    d.id = div.id = (c[1] + 1) + ":" + (c[2] + 1);
                    const row_from = shape.visibleRows.find(row => row.r === c[1]);
                    const row_to = c[1] === c[2] ? row_from : shape.visibleRows.find(row => row.r === c[2]);
                    x = -shape.getHeadWidth();
                    y = row_from ? row_from.y - 1 : -1;
                    width = shape.width;
                    height = row_to ? row_to.y + row_to.height - y - 2 : (y === -1 ? -1 : shape.height + 1);
                    y -= shape.getHeadHeight();
                } else {
                    let cell_from = shape.visibleCells.find(cell => cell.c === util.charToIndex(c[0]) && cell.r === Number.parseInt(c[1]));
                    if (!cell_from) {
                        const c_from = shape.visibleColumns.find(col => col.c === util.charToIndex(c[0]));
                        if (c_from) {
                            const r_from = shape.visibleRows.find(row => row.r === Number.parseInt(c[1]));
                            if (r_from) {
                                cell_from = { x: c_from.x, y: r_from.y, width: c_from.width, height: r_from.height }
                            }
                        }
                    }
                    let cell_to = c.length > 2 ? shape.visibleCells.find(cell => cell.c === util.charToIndex(c[2]) && cell.r === Number.parseInt(c[3])) : cell_from;
                    if (!cell_to && c.length > 2) {
                        const c_to = shape.visibleColumns.find(col => col.c === util.charToIndex(c[2]));
                        if (c_to) {
                            const r_to = shape.visibleRows.find(row => row.r === Number.parseInt(c[3]));
                            if (r_to) {
                                cell_to = { x: c_to.x, y: r_to.y, width: c_to.width, height: r_to.height }
                            }
                        }
                    }
                    d.id = div.id = c[0] + (c[1] + 1) + (c.length > 2 ? ":" + c[2] + (c[3] + 1) : "");
                    x = (cell_from ? cell_from.x - 1 : -1);
                    y = (cell_from ? cell_from.y - 1 : -1);
                    width = cell_to ? (cell_to.x + cell_to.width - x - 2) : (x === -1 ? -1 : shape.width + 1);
                    height = cell_to ? (cell_to.y + cell_to.height - y - 2) : (y === -1 ? -1 : shape.height + 1);
                    x -= shape.getHeadWidth();
                    y -= shape.getHeadHeight();
                }
                c.index && (d.start = c.index);
                d.end = d.start + d.id.length;
                div.style.left = x + "px";
                div.style.top = y + "px";
                div.style.width = width + "px";
                div.style.height = height + "px";
                shape.drawer.container.appendChild(div);
            };

            d.remove = () => {
                div.remove();
            };

            displayers.push(d);
            return d;
        }
        const ds = {};
        ds.clear = () => {
            displayers.forEach(d => d.remove());
            displayers.splice(0);
        };

        ds.getFromCaret = position => {
            return displayers.find(d => d.start <= position && d.end >= position);
        };

        ds.getFromId = id => {
            return displayers.find(d => d.id === id);
        }

        /**
         * 开始显示该公式下的所有reference cells
         */

        ds.begin = (row, column) => {
            // if (ds.row !== row || ds.column !== column) {
            ds.clear();
            ds.row = row;
            ds.column = column;
            // }
            maxIndex = -1;
        };

        let maxIndex = -1;
        ds.add = (range, index) => {
            let d;
            if (index >= 0 && index < displayers.length) {
                d = displayers[index];
                maxIndex = index;
            } else {
                d = displayer();
                maxIndex = index = displayers.length - 1;
            }
            d.index = index;
            d.display(range, index);
            return d;
        };

        /**
         * 结束显示，清理废弃的reference 显示
         */
        ds.close = () => {
            if (maxIndex == -1) {
                ds.clear();
            } else {
                const rest = displayers.splice(maxIndex + 1);
                rest.forEach(r => r.remove());
            }

        }
        return ds;

    };
    const editDisplayers = cellsRefDisplayers(shape);
    const displayEditCells = (row, column, value) => {
        // editDisplayers.forEach(d => d.remove());
        editDisplayers.begin(row, column);
        if (!self.focus.contentEditable) return;
        const interpret = interpreter(shape, value);
        if (!interpret.isFormular()) return;
        interpret.getCells().forEach((c, i) => {
            editDisplayers.add(c, i);
        })
        editDisplayers.close();

    }

    const drawRegions = self.drawRegions;
    self.drawRegions = context => {
        // const context = self.canvas.getContext("2d");
        const regions = shape.regions.filter(r => r.type === "table-group" || r.type === "table-hidden");
        translate(context ? context : self.context, shape.margin, shape.margin, () => {
            regions.forEach(r => {
                r.draw(context);
                r.visible = false;
            });
        })
        drawRegions.call(self);
        regions.forEach(r => r.visible = true);
    };


    self.drawStatic = (context, x, y) => {
        translate(context, x, y, () => {
            x = 0, y = 0;
            let sx = shape.getHeadWidth();
            let sy = shape.getHeadHeight();
            const toolWidth = shape.getToolWidth();
            const toolHeight = shape.getToolHeight();
            sx < 1 && (sx = 1);
            sy < 1 && (sy = 1);
            const rows = drawRowHeads(context, x + toolWidth, y + sy, sx - toolWidth, shape.height - sy - 2);
            const columns = drawColumnHeads(context, x + sx, y + toolHeight, shape.width - sx - 2, sy - toolHeight);
            shape.visibleRows.width = columns[columns.length - 1].x + columns[columns.length - 1].width;// - columns[0].x;
            shape.visibleColumns.height = rows[rows.length - 1].y + rows[rows.length - 1].height;// - rows[0].y;

            (shape.withColumnHead && shape.withRowHead) && drawIcon(context, x + toolWidth, y + toolHeight);
            drawToolBar(context, x, y);
            drawAllCells(context, rows, columns, x + sx, y + sy, shape.width - sx, shape.height - sy);
            shape.arrangeRegions();

        });
    };

    self.drawDynamic = (context, x, y) => {
        if (!shape.mouse) return;
        const x1 = shape.mouse.x1 - shape.x - shape.width / 2;
        const y1 = shape.mouse.y1 - shape.y - shape.height / 2;
        context.beginPath();
        context.rect(x1 - 2, y1 - 2, 4, 4);
        context.fillStyle = "steelBlue";
        context.fill();
    };

    let currentDisplayer;
    self.drawSelection = (() => {
        const id = shape.id + "|focus";
        const vid = shape.id + "|vertical-selection";
        const hid = shape.id + "|horizontal-selection";
        const rid = shape.id + "|resizer";
        const aid = shape.id + "|resizer-refer-line";
        const splitvid = shape.id + "|split-line-vertical";
        const splithid = shape.id + "|split-line-horizon";
        const cornerid = shape.id + "|corner";
        const copyid = shape.id + "|copy";
        const selectionBk = "rgba(70,130,180,0.1)";
        const selectionBk2 = "rgba(10,10,10,0.1)";
        const borderColor = "steelblue";
        const resizerWidth = 4;
        const focus = self.createElement("div", id);//cell选中高亮
        // focus.id = id;
        shape.get ? focus.style.fontSize = (shape.fontSize + "px") : (focus.style.fontSize = "12px");
        focus.style.position = "absolute";
        focus.style.outline = "0px";
        // focus.style.border = lineStyle;
        self.boundary.appendChild(focus);

        const vfocus = self.createElement("div",vid);//列选中高亮
        // vfocus.id = vid;
        vfocus.style.position = "absolute";
        self.boundary.appendChild(vfocus);

        const hfocus = self.createElement("div",hid);//行选中高亮
        // hfocus.id = hid;
        hfocus.style.position = "absolute";
        self.boundary.appendChild(hfocus);

        const rfocus = self.createElement("div",rid);//resize高亮
        // rfocus.id = rid;
        rfocus.style.position = "absolute";
        self.boundary.appendChild(rfocus);

        const afocus = self.createElement("div",aid);//resize时的对齐线
        // afocus.id = aid;
        afocus.style.position = "absolute";
        self.boundary.appendChild(afocus);

        const splitv = self.createElement("div",splitvid);//resize时的对齐线
        // splitv.id = splitvid;
        splitv.style.position = "absolute";
        splitv.style.background = "silver";
        splitv.style.width = "4px";
        self.boundary.appendChild(splitv);

        const splith = self.createElement("div",splithid);//resize时的对齐线
        // splith.id = splithid;
        splith.style.position = "absolute";
        splith.style.background = "silver";
        splith.style.height = "4px";
        self.boundary.appendChild(splith);

        const corner = self.createElement("div",cornerid);
        // corner.id = cornerid;
        corner.style.position = "absolute";
        self.boundary.appendChild(corner);

        const copy = self.createElement("div",copyid);
        // copy.id = copyid;
        copy.style.position = "absolute";
        copy.style.border = "1px dashed teal";
        self.boundary.appendChild(copy);

        // corner.style.borderStyle = "dashed";
        // corner.style.border = "1px solid " + borderColor;

        const lineStyle = () => `${Math.ceil(1 / shape.page.scaleX)}px solid ${borderColor}`;

        const drawEditDisplayer = () => {
            // if (!(self.focus && self.focus.contentEditable === "true" && shape.page.isMouseDown()) || (shape.mousedownConnector && shape.mousedownConnector !== shape.corner)) return false;
            if (!(self.focus && self.focus.contentEditable === "true") || (shape.mousedownConnector && shape.mousedownConnector !== shape.corner)) return false;
            const interpret = interpreter(shape, self.focus.innerHTML, self.focus.attach.r, self.focus.attach.c);
            if (!interpret.isFormular()) return false;

            let newId = undefined;
            self.focus.style.visibility = "visible";
            if (shape.selectedCells.from !== undefined && shape.selectedCells.to !== undefined) {
                if (shape.selectedCells.from[0] === self.focus.attach.r && shape.selectedCells.from[1] === self.focus.attach.c) return false;
                //single cell
                if (shape.selectedCells.from[0] === shape.selectedCells.to[0] && shape.selectedCells.from[1] === shape.selectedCells.to[1]) {
                    newId = util.indexToChar(shape.selectedCells.from[1]) + (shape.selectedCells.from[0] + 1);
                } else {//range of cells
                    const c1 = util.indexToChar(Math.min(shape.selectedCells.from[1], shape.selectedCells.to[1]));
                    const c2 = util.indexToChar(Math.max(shape.selectedCells.from[1], shape.selectedCells.to[1]));
                    const r1 = shape.selectedCells.from[0] + 1;
                    const r2 = shape.selectedCells.to[0] + 1;
                    newId = c1 + Math.min(r1, r2) + ":" + c2 + Math.max(r1, r2);
                }
            }
            if (shape.selectedColumns.from !== -1 && shape.selectedColumns.to !== -1) {
                const minIdx = shape.selectedColumns.from < shape.selectedColumns.to ? shape.selectedColumns.from : shape.selectedColumns.to;
                const maxIdx = shape.selectedColumns.from > shape.selectedColumns.to ? shape.selectedColumns.from : shape.selectedColumns.to;
                newId = util.indexToChar(minIdx) + ":" + util.indexToChar(maxIdx);

            }
            if (shape.selectedRows.from !== -1 && shape.selectedRows.to !== -1) {
                const minIdx = shape.selectedRows.from < shape.selectedRows.to ? shape.selectedRows.from : shape.selectedRows.to;
                const maxIdx = shape.selectedRows.from > shape.selectedRows.to ? shape.selectedRows.from : shape.selectedRows.to;
                newId = (minIdx + 1) + ":" + (maxIdx + 1);

            }

            if (newId !== undefined) {
                const id = currentDisplayer ? currentDisplayer.id : undefined;
                if (currentDisplayer) {
                    self.focus.innerHTML = interpret.replace(id, newId, currentDisplayer.start);
                } else {
                    self.focus.innerHTML += newId;
                }
                displayEditCells(self.focus.attach.r, self.focus.attach.c, self.focus.innerText);
                currentDisplayer = editDisplayers.getFromId(newId);
                setCaret(self.focus, currentDisplayer.end);
                return true;
            } else {
                if (self.focus.contentEditable === "true") {
                    self.focus.style.visibility = "visible";
                    return true;
                } else return false;
            }
        };
        const setCaret = (el, position) => {
            const range = document.createRange();
            const sel = window.getSelection();

            range.setStart(el.childNodes[0], position);
            range.collapse(true)

            sel.removeAllRanges()
            sel.addRange(range)
        }

        const drawCornerSelection = () => {
            if (shape.cornerCells.from === undefined || shape.cornerCells.to === undefined) return;
            const lineStyle = "1px dashed " + borderColor;
            corner.style.visibility = "visible";
            const col1 = shape.visibleColumns.find(c => c.c >= shape.cornerCells.from[1]);
            const x1 = col1.x;
            const col2 = shape.visibleColumns.filter(c => c.c <= shape.cornerCells.to[1]).maxBy(c => c.c);
            const x2 = col2.x + col2.width;
            const row1 = shape.visibleRows.find(c => c.r >= shape.cornerCells.from[0]);
            const y1 = row1.y;
            const row2 = shape.visibleRows.filter(c => c.r <= shape.cornerCells.to[0]).maxBy(c => c.r);
            const y2 = row2.y + row2.height;
            corner.style.left = (x1 - 1) + "px";
            corner.style.top = (y1 - 1) + "px";

            corner.style.width = (x2 - x1 - 3) + "px";
            corner.style.height = (y2 - y1 - 3) + "px";
            corner.style.border = lineStyle;
            corner.frame = { x: x1, y: y1, width: x2 - x1, height: y2 - y1 };
            self.corner = corner;
        };
        const drawCellSelection = () => {
            if (shape.selectedCells.from === undefined || shape.selectedCells.to === undefined) return;
            const calculateActualSelection = (row1, col1, row2, col2) => {
                const r1 = Math.min(row1, row2);
                const r2 = Math.max(row1, row2);
                const c1 = Math.min(col1, col2);
                const c2 = Math.max(col1, col2);
                const matched = shape.cache.getRange(r1, c1, r2, c2).filter(c => {
                    const rect1 = { x: c.c, y: c.r, width: (c.cs ? c.cs : 1) - 0.1, height: (c.rs ? c.rs : 1) - 0.1 };
                    const rect2 = { x: c1, y: r1, width: c2 - c1 + 0.9, height: r2 - r1 + 0.9 };
                    return isRectInteractRect(rect1, rect2);
                });
                if (matched.length === 0) return { ar1: r1, ar2: r2, ac1: c1, ac2: c2 };
                let ar1 = matched.min(m => m.r);
                let ar2 = matched.max(m => m.r + (m.rs ? (m.rs - 1) : 0));
                let ac1 = matched.min(m => m.c);
                let ac2 = matched.max(m => m.c + (m.cs ? (m.cs - 1) : 0));
                (ar1 > r1) && (ar1 = r1);
                (ar2 < r2) && (ar2 = r2);
                (ac1 > c1) && (ac1 = c1);
                (ac2 < c2) && (ac2 = c2);
                if (ar1 === r1 && ar2 === r2 && ac1 === c1 && ac2 === c2) return { ar1, ar2, ac1, ac2 };
                else return calculateActualSelection(ar1, ac1, ar2, ac2);
            };
            vfocus.style.visibility = shape.withColumnHead ? "visible" : "hidden";
            hfocus.style.visibility = shape.withRowHead ? "visible" : "hidden";
            vfocus.style.background = hfocus.style.background = selectionBk2;
            vfocus.style.borderLeft = vfocus.style.borderRight = hfocus.style.borderTop = hfocus.style.borderBottom = "";
            focus.style.visibility = "visible";

            const actual = calculateActualSelection(shape.selectedCells.from[0], shape.selectedCells.from[1], shape.selectedCells.to[0], shape.selectedCells.to[1])
            shape.selectedCells.actualCells = { from: [actual.ar1, actual.ac1], to: [actual.ar2, actual.ac2] };
            //confirm last row and column
            shape.selectedCells.lastPosition = {};
            if (shape.selectedCells.from[0] < shape.selectedCells.to[0]) {
                shape.selectedCells.lastPosition.r = shape.selectedCells.actualCells.to[0];
            } else {
                shape.selectedCells.lastPosition.r = shape.selectedCells.actualCells.from[0];
            }
            if (shape.selectedCells.from[1] < shape.selectedCells.to[1]) {
                shape.selectedCells.lastPosition.c = shape.selectedCells.actualCells.to[1];
            } else {
                shape.selectedCells.lastPosition.c = shape.selectedCells.actualCells.from[1];
            }
            //get range from column and row
            let min_r = Math.max(actual.ar1, shape.visibleRows[0].r);
            let max_r = Math.min(actual.ar2, shape.visibleRows[shape.visibleRows.length - 1].r);
            let min_c = Math.max(actual.ac1, shape.visibleColumns[0].c);
            let max_c = Math.min(actual.ac2, shape.visibleColumns[shape.visibleColumns.length - 1].c);


            const min_column = shape.visibleColumns.find(c => c.c >= min_c);
            const min_row = shape.visibleRows.find(r => r.r >= min_r);
            const max_column = shape.visibleColumns.filter(c => c.c <= max_c).maxBy(c => c.c);
            const max_row = shape.visibleRows.filter(r => r.r <= max_r).maxBy(r => r.r);
            if (!(min_column && min_row && max_column && max_row)) return;
            const x = min_column.x;
            const y = min_row.y;
            const x1 = max_column.x + max_column.width;
            const y1 = max_row.y + max_row.height;
            //top cell
            const x2 = shape.cellsFrame.x > x ? shape.cellsFrame.x : x;
            if (x2 === x && x !== shape.frozenX + shape.getHeadWidth()) {
                focus.style.borderLeft = lineStyle();
            } else {
                focus.style.borderLeft = "0px";
            }
            const y2 = shape.cellsFrame.y > y ? shape.cellsFrame.y : y;
            if (y2 === y && y !== shape.frozenY + shape.getHeadHeight()) {
                focus.style.borderTop = lineStyle();
            } else {
                focus.style.borderTop = "0px";
            }
            const x3 = (shape.cellsFrame.x + shape.cellsFrame.width) > x1 ? x1 : (shape.cellsFrame.x + shape.cellsFrame.width);
            if (x3 === x1) {
                focus.style.borderRight = lineStyle();
            } else {
                focus.style.borderRight = "0px";
            }
            const y3 = (shape.cellsFrame.y + shape.cellsFrame.height) > y1 ? y1 : (shape.cellsFrame.y + shape.cellsFrame.height);
            if (y3 === y1) {
                focus.style.borderBottom = lineStyle();
            } else {
                focus.style.borderBottom = "0px";
            }
            focus.style.left = (x2 - 1) + "px";
            focus.style.top = (y2 - 1) + "px";
            focus.style.width = (x3 - x2 - 2) + "px";
            focus.style.minHeight = (y3 - y2 - 2) + "px";
            focus.frame = { x: x2, y: y2, width: x3 - x2, height: y3 - y2 };
            focus.style.height = "auto";
            if (focus.contentEditable !== "true") {
                focus.style.background = selectionBk;// vFocusCells.length > 1 ? selectionBk : "";
                focus.innerHTML = "";
                focus.style.minWidth = focus.style.maxWidth = "";
                focus.style.paddingLeft = focus.style.paddingTop = "";
            }
            if (x3 - x2 < 1 || y3 - y2 < 1) focus.style.visibility = "hidden";

            if (shape.inCopy && !shape.inCopyDisplayed) {// && copy.style.visibility === "hidden") {
                copy.style.visibility = "visible";
                copy.style.left = (x2) + "px";
                copy.style.top = (y2) + "px";
                copy.style.width = (x3 - x2 - 4) + "px";
                copy.style.height = (y3 - y2 - 4) + "px";
                shape.inCopyDisplayed = true;
            }

            // vfocus.style.visibility = shape.withColumnHead ? "visible" : "hidden";
            // hfocus.style.visibility = shape.withRowHead ? "visible" : "hidden";
            // vfocus.style.background = hfocus.style.background = selectionBk2;
            // vfocus.style.borderLeft = vfocus.style.borderRight = hfocus.style.borderTop = hfocus.style.borderBottom = "";
            if (shape.withColumnHead) {
                vfocus.style.left = focus.style.left;
                vfocus.style.width = (x3 - x2) + "px";
                vfocus.style.top = shape.getToolHeight() + "px";
                vfocus.style.height = shape.headHeight + "px";
            }
            if (shape.withRowHead) {
                hfocus.style.left = shape.getToolWidth() + "px";
                hfocus.style.width = (shape.headWidth + 1) + "px";
                hfocus.style.top = focus.style.top;
                hfocus.style.height = (y3 - y2) + "px";

            }
            self.focus = focus;

        };
        const drawColumnHeadSelection = () => {
            if (shape.selectedColumns.from === -1 || shape.selectedColumns.to === -1) return;
            vfocus.style.visibility = "visible";
            hfocus.style.visibility = "hidden";
            focus.style.visibility = "hidden";
            rfocus.style.visibility = "hidden";
            vfocus.style.borderLeft = vfocus.style.borderRight = lineStyle();
            vfocus.style.background = selectionBk;

            const minIdx = shape.selectedColumns.from < shape.selectedColumns.to ? shape.selectedColumns.from : shape.selectedColumns.to
            const maxIdx = shape.selectedColumns.from > shape.selectedColumns.to ? shape.selectedColumns.from : shape.selectedColumns.to
            const vMin = shape.visibleColumns.minBy(c => c.c);
            const vMax = shape.visibleColumns.maxBy(c => c.c);
            const min = (minIdx < vMin.c) ? vMin : shape.visibleColumns.minBy(c => c.c >= minIdx ? (c.c - minIdx) : 1000);
            const max = (maxIdx > vMax.c) ? vMax : shape.visibleColumns.find(c => c.c === maxIdx);

            let left = min.x - 1;
            let top = min.y - 1;
            let width = max.x + max.width - min.x - 2;
            let height = shape.visibleColumns.height;//shape.height - shape.getToolHeight() - 1;
            if (left < shape.cellsFrame.x) {
                left = shape.cellsFrame.x;
                vfocus.style.borderLeft = "";
            }
            if (left + width > shape.cellsFrame.x + shape.cellsFrame.width) {
                width = shape.cellsFrame.x + shape.cellsFrame.width - left - 3;
                vfocus.style.borderRight = "";
            }
            vfocus.style.left = left + "px";
            vfocus.style.top = top + "px";
            vfocus.style.width = width + "px";
            vfocus.style.height = height + "px";

            if (shape.inCopy && !shape.inCopyDisplayed) {
                copy.style.visibility = "visible";
                copy.style.left = (left + 1) + "px";
                copy.style.top = (top + shape.getHeadHeight()) + "px";
                copy.style.width = (width - 2) + "px";
                copy.style.height = (height - shape.getHeadHeight() - 2) + "px";
                shape.inCopyDisplayed = true;
            }
        };
        const drawRowHeadSelection = () => {
            if (shape.selectedRows.from === -1 || shape.selectedRows.to === undefined) return;
            hfocus.style.visibility = "visible";
            vfocus.style.visibility = "hidden";
            focus.style.visibility = "hidden";
            rfocus.style.visibility = "hidden";
            hfocus.style.borderTop = hfocus.style.borderBottom = lineStyle();
            hfocus.style.background = selectionBk;

            const minIdx = shape.selectedRows.from < shape.selectedRows.to ? shape.selectedRows.from : shape.selectedRows.to
            const maxIdx = shape.selectedRows.from > shape.selectedRows.to ? shape.selectedRows.from : shape.selectedRows.to
            const vMin = shape.visibleRows.minBy(r => r.r);
            const vMax = shape.visibleRows.maxBy(r => r.r);
            const min = (minIdx < vMin.r) ? vMin : shape.visibleRows.minBy(r => r.r >= minIdx ? (r.r - minIdx) : 1000);
            const max = (maxIdx > vMax.r) ? vMax : shape.visibleRows.find(r => r.r === maxIdx);

            let left = min.x - 1;
            let top = min.y - 1;
            let width = shape.visibleRows.width;//shape.width - shape.getToolWidth() - 1;
            let height = max.y + max.height - min.y - 2;
            if (top < shape.cellsFrame.y) {
                top = shape.cellsFrame.y;
                hfocus.style.borderTop = "";
            }
            if (top + height > shape.cellsFrame.y + shape.cellsFrame.height) {
                height = shape.cellsFrame.y + shape.cellsFrame.height - top - 3;
                hfocus.style.borderBottom = "";
            }
            hfocus.style.left = left + "px";
            hfocus.style.top = top + "px";
            hfocus.style.width = width + "px";
            hfocus.style.height = height + "px";

            if (shape.inCopy && !shape.inCopyDisplayed) {
                copy.inCopyType = "row";
                copy.style.visibility = "visible";
                copy.style.left = (left + shape.getHeadWidth()) + "px";
                copy.style.top = (top + 1) + "px";
                copy.style.width = (width - shape.getHeadWidth() - 2) + "px";
                copy.style.height = (height - 2) + "px";
                shape.inCopyDisplayed = true;
            }
        };
        const drawRowResizer = () => {
            const top = shape.resizer.target.y + shape.resizer.target.height - resizerWidth + (shape.resizer.y2 ? (shape.resizer.y2 - shape.resizer.y1) : 0) + 1;
            const headWidth = shape.getHeadWidth();
            rfocus.style.visibility = shape.withColumnHead ? "visible" : "hidden";
            rfocus.style.borderTop = rfocus.style.borderBottom = "";
            rfocus.style.background = borderColor;
            rfocus.style.left = (shape.getToolWidth() - 1) + "px";
            rfocus.style.top = top + "px";
            rfocus.style.width = shape.headWidth + "px";
            rfocus.style.height = resizerWidth + "px";
            afocus.style.visibility = shape.resizer.dragging ? "visible" : "hidden";
            afocus.style.top = (top + resizerWidth - 2) + "px";
            afocus.style.height = "1px";
            afocus.style.background = borderColor;
            afocus.style.left = headWidth + "px";
            afocus.style.width = (shape.width - headWidth) + "px";
        };
        const drawColumnResizer = () => {
            const left = shape.resizer.target.x + shape.resizer.target.width - resizerWidth + (shape.resizer.x2 ? (shape.resizer.x2 - shape.resizer.x1) : 0) + 1;
            const headHeight = shape.getHeadHeight();
            rfocus.style.visibility = shape.withRowHead ? "visible" : "hidden";
            rfocus.style.borderLeft = rfocus.style.borderRight = "";
            rfocus.style.background = borderColor;
            rfocus.style.left = left + "px";
            rfocus.style.top = (shape.getToolHeight() - 1) + "px";
            rfocus.style.width = resizerWidth + "px";
            rfocus.style.height = shape.headHeight + "px";
            afocus.style.visibility = shape.resizer.dragging ? "visible" : "hidden";
            afocus.style.left = (left + resizerWidth - 2) + "px";
            afocus.style.width = "1px";
            afocus.style.background = borderColor;
            afocus.style.top = headHeight + "px";
            afocus.style.height = (shape.height - headHeight) + "px";
        };

        const drawFrozenVertialSplit = () => {
            splitv.style.visibility = "visible";
            splitv.style.left = (shape.frozenX + shape.getHeadWidth() - 3) + "px";
            splitv.style.top = (shape.getToolHeight()) + "px";
            splitv.style.height = (shape.height - shape.getToolHeight()) + "px";
        }
        const drawFrozenHorizonSplit = () => {
            splith.style.visibility = "visible";
            splith.style.top = (shape.frozenY + shape.getHeadHeight() - 3) + "px";
            splith.style.left = (shape.getToolWidth()) + "px";
            splith.style.width = (shape.width - shape.getToolWidth()) + "px";
        }

        return () => {
            focus.style.visibility = vfocus.style.visibility = hfocus.style.visibility = rfocus.style.visibility =
                afocus.style.visibility = splith.style.visibility = splitv.style.visibility = corner.style.visibility = "hidden";
            !shape.inCopy && (copy.style.visibility = "hidden");

            if (drawEditDisplayer() !== true) {
                currentDisplayer = undefined;
                drawCellSelection();
                drawRowHeadSelection();
                drawColumnHeadSelection();
            }
            if (shape.resizer && shape.resizer.type === "column") {
                drawColumnResizer();
            }
            if (shape.resizer && shape.resizer.type === "row") {
                drawRowResizer();
            }
            if (shape.frozenX > 0) {
                drawFrozenVertialSplit();
            }
            if (shape.frozenY > 0) {
                drawFrozenHorizonSplit();
            }
            drawCornerSelection();

        };
    })();



    const fillHeadGray = (context, x, y, width, height) => {
        // context.strokeStyle = shape.headBorderColor;//"dimgray";
        context.fillStyle = shape.headBackColor;//"whitesmoke";
        context.beginPath();
        context.rect(x, y, width, height);
        context.fill();

    };

    const ifColumnVisible = index => {
        if (shape.minColumn > -1 && shape.minColumn > index) return false;
        const CS = "columns";
        if (shape.hiddens[CS] && shape.hiddens[CS].indexOf(index) >= 0) return false;
        for (let i = 0; i < shape.groups.length; i++) {
            const g = shape.groups[i];
            if (g[2] === 1 && g[3] === 0 && g[0] <= index && g[1] >= index) {
                shape.groupHiddens[CS].indexOf(index) < 0 && shape.groupHiddens[CS].push(index);//缓存group hidden
                return false;
            } else {
                const idx = shape.groupHiddens[CS].indexOf(index);
                idx >= 0 && shape.groupHiddens[CS].splice(idx, 1);//移除 group hidden缓存
            }
        }
        return true;
    };
    const ifRowVisible = index => {
        if (shape.minRow > -1 && shape.minRow > index) return false;
        const RS = "rows";
        if (shape.hiddens[RS] && shape.hiddens[RS].indexOf(index) >= 0) return false;
        for (let i = 0; i < shape.groups.length; i++) {
            const g = shape.groups[i];
            if (g[2] === 0 && g[3] === 0 && g[0] <= index && g[1] >= index) {
                shape.groupHiddens[RS].indexOf(index) < 0 && shape.groupHiddens[RS].push(index);//缓存group hidden
                return false;
            } else {
                const idx = shape.groupHiddens[RS].indexOf(index);
                idx >= 0 && shape.groupHiddens[RS].splice(idx, 1);//移除 group hidden缓存
            }
        }
        return true;
    };

    const clip = (context, x, y, width, height, f) => {
        context.save();
        context.beginPath();
        context.rect(x, y, width, height);
        context.clip();
        context.closePath();
        try {
            f();
        } catch (e) {
            console.warn(e);
        }
        context.restore();
    };
    shape.clip = clip;

    const translate = (context, x, y, f) => {
        context.save();
        context.translate(x, y);
        try {
            f();
        } catch { }
        context.restore();
    }

    const drawRowHeads = (() => {
        let index, beginy, lasth, lastbk, lastbktime, lastrow, auto;
        const drawRowHead = (context, index, x, y, width, height, bk, bktime, row, auto, align, indent) => {
            if (!ifRowVisible(index)) return;
            shape.visibleRows.push({ r: index, y, height, bk, bktime, auto, x, width: shape.width, row, align, readonly: ((row && row.ro) ? true : false), indent });
            if (!shape.withRowHead) return;
            context.strokeStyle = context.fillStyle = shape.headTextColor;
            context.lineWidth = 1;
            context.font = "italic bold 11px Arial";

            const text = (row && row.t) ? row.t : (index + 1);
            // const readonly = (row && row.ro) ? true : false;
            context.fillText(text, x + width / 2 - (context.measureText(text).width / 2), y + height / 2 + 5);
            context.beginPath();
            context.moveTo(x, y);
            context.lineTo(x + width, y);
            context.stroke();
            // shape.visibleRows.push({ r: index, y, height, bk, bktime, auto, x, width, row, align, readonly, indent });
        };

        const drawRowHeadFrame = (context, x, y, width, height) => {
            if (!shape.withRowHead) return;
            fillHeadGray(context, x, y, width-1, height-1);
            context.beginPath();
            context.lineWidth = 1;
            context.moveTo(x, y);
            context.lineTo(x + width, y);
            context.lineTo(x + width, y + height);
            context.stroke();
        };

        const init = () => {
            shape.visibleRows = [];
            index = -1, beginy = 0, lasth = 0, lastbk = "", lastbktime = 0, lastrow, auto = true;
        };

        const drawFrozenRows = (context, frozenRow, x, y, width) => {
            shape.frozenRowIndex = -1;
            while (true) {
                if (frozenRow === index) break;
                index++;
                if (!ifRowVisible(index)) continue;
                const r = shape.rows[index]
                lasth = (r && r.h) ? r.h : shape.cellHeight;
                lastbk = (r && r.bk) ? r.bk : "";
                lastbktime = (r && r.bktime) ? r.bktime : 0;
                auto = !(r && r.h);
                lastrow = r;
                drawRowHead(context, index, x, y + beginy, width, lasth, lastbk, lastbktime, lastrow, auto, r && r.a);
                shape.frozenRowIndex++;
                beginy += lasth;
                if (shape.maxRow > -1 && index >= shape.maxRow) break;
            }

        };

        const drawNormalRows = (context, x, y, width, height) => {
            shape.frozenY = beginy;
            beginy += -shape.scrollY;
            while (true) {
                index++;
                if (!ifRowVisible(index)) continue;
                const r = shape.rows[index]
                lasth = (r && r.h) ? r.h : shape.cellHeight;
                lastbk = (r && r.bk) ? r.bk : "";
                lastbktime = (r && r.bktime) ? r.bktime : 0;
                auto = !(r && r.h);
                lastrow = r;
                if (beginy + lasth > shape.frozenY) {
                    const indent = shape.frozenY - beginy;
                    if (indent > 0) {
                        beginy += indent;
                        lasth -= indent;
                    }
                    drawRowHead(context, index, x, y + beginy, width, lasth, lastbk, lastbktime, lastrow, auto, r && r.a, indent > 0 ? indent : 0);
                }
                beginy += lasth;
                if (shape.maxRow > -1 && index >= shape.maxRow) {
                    context.strokeStyle = shape.headTextColor;
                    context.lineWidth = 1;
                    context.beginPath();
                    context.moveTo(x, y + beginy);
                    context.lineTo(x + width, y + beginy);
                    context.stroke();
                    break;
                }
                if (isNaN(beginy) || beginy > height) break;
            }
        };

        return (context, x, y, width, height) => {
            shape.frozenRow = shape.frozens["rows"] ? shape.frozens["rows"] : -1;//行锁定
            //clear background
            clearDraw(context, x - 1, y - 1, width + 1, height + 1);
            //init
            init();
            //draw row head frame
            drawRowHeadFrame(context, x, y, width, height);
            //draw frozen rows first
            drawFrozenRows(context, shape.frozenRow, x, y, width, height);
            //draw normal rows
            clip(context, x - 1, y + beginy, width + 1, height - beginy, () => drawNormalRows(context, x, y, width, height));
            return shape.visibleRows;
        }
    })();

    const drawColumnHeads = (() => {
        let index, beginx, lastw, lastbk, lastbktime, lastcolumn;
        const drawColumnHead = (context, index, x, y, width, height, bk, bktime, column, align, indent) => {
            if (!ifColumnVisible(index)) return;
            shape.visibleColumns.push({ c: index, x, width, bk, bktime, y, height: shape.height, column, align, readonly: ((column && column.ro) ? true : false), indent });
            if (!shape.withColumnHead) return;
            context.strokeStyle = context.fillStyle = shape.headTextColor;
            context.lineWidth = 1;
            context.font = "italic bold 11px Arial";
            const chars = (column && column.t) ? column.t : util.indexToChar(index);
            // const readonly = (column && column.ro) ? true : false;
            context.beginPath();
            context.fillText(chars, x + width / 2 - (context.measureText(chars).width / 2), y + height / 2 + 5);
            context.beginPath();
            context.moveTo(x, y);
            context.lineTo(x, y + height);
            context.stroke();
            // shape.visibleColumns.push({ c: index, x, width, bk, bktime, y, height, column, align, readonly, indent });
        };

        const drawColumnHeadFrame = (context, x, y, width, height) => {
            if (!shape.withColumnHead) return;
            fillHeadGray(context, x+1, y+1, width-2, height-2);
            context.lineWidth = 1;
            context.beginPath();
            context.moveTo(x, y);
            context.lineTo(x, y + height);
            context.lineTo(x + width, y + height);
            context.stroke();
        };

        const init = () => {
            shape.visibleColumns = [];
            index = -1, beginx = 0, lastw = 0, lastbk = "", lastbktime = 0, lastcolumn;

        };

        const drawFrozenColumns = (context, frozenColumn, x, y, height) => {
            shape.frozenColumnIndex = -1;
            while (true) {
                if (frozenColumn === index) break;
                index++;
                if (!ifColumnVisible(index)) continue;
                const col = shape.columns[index];
                lastw = (col && col.w) ? col.w : shape.cellWidth;
                lastbk = (col && col.bk) ? col.bk : "";
                lastbktime = (col && col.bktime) ? col.bktime : 0;
                lastcolumn = col;
                drawColumnHead(context, index, x + beginx, y, lastw, height, lastbk, lastbktime, lastcolumn, col && col.a);
                shape.frozenColumnIndex++;
                beginx += lastw;
                if (shape.maxColumn > -1 && index >= shape.maxColumn) break;
            }

        };

        const drawNormalColumns = (context, x, y, width, height) => {
            //draw normal columns
            shape.frozenX = beginx;
            beginx += -shape.scrollX;
            //find the first visible normal column
            while (true) {
                index++;
                //if column is invisible, ignore it: invisible== column.hide || column.collapse
                if (!ifColumnVisible(index)) continue;
                const col = shape.columns[index];
                lastw = (col && col.w) ? col.w : shape.cellWidth;
                lastbk = (col && col.bk) ? col.bk : "";
                lastbktime = (col && col.bktime) ? col.bktime : 0;
                lastcolumn = col;
                if (beginx + lastw > shape.frozenX) {
                    const indent = shape.frozenX - beginx;
                    if (indent > 0) {
                        beginx += indent;
                        lastw -= indent;
                    }
                    drawColumnHead(context, index, x + beginx, y, lastw, height, lastbk, lastbktime, lastcolumn, col && col.a, indent > 0 ? indent : 0);
                }
                beginx += lastw;
                if (shape.maxColumn > -1 && index >= shape.maxColumn) {
                    context.strokeStyle = shape.headTextColor;
                    context.lineWidth = 1;
                    context.beginPath();
                    context.moveTo(x + beginx, y);
                    context.lineTo(x + beginx, y + height);
                    context.stroke();
                    break;
                }
                if (isNaN(beginx) || beginx > width) break;
            }
        };

        return (context, x, y, width, height) => {
            shape.frozenColumn = shape.frozens["columns"] ? shape.frozens["columns"] : -1;//列锁定
            //clear background
            clearDraw(context, x - 1, y - 1, width + 1, height + 1);
            //init
            init();
            //draw head frame
            drawColumnHeadFrame(context, x, y, width, height);
            //draw frozen columns first
            drawFrozenColumns(context, shape.frozenColumn, x, y, height);
            //draw non frozen columns
            clip(context, x - 1 + beginx, y, width + 1 - beginx, height, () => drawNormalColumns(context, x, y, width, height));
            return shape.visibleColumns;
        };
    })();

    const drawCell = (() => {
        const drawCellBackground = (context, bound) => {
            let color = bound.bk;
            if (!color) {
                //find row
                const row = shape.rows[bound.r];
                const col = shape.columns[bound.c];
                //background color
                if (row && row.bk) color = row.bk;
                if (col && col.bk && (!(row && row.bktime) || col.bktime > row.bktime)) color = col.bk;
            }
            bound.readonly && (color = "whitesmoke");
            if (!color) return;

            context.beginPath();
            context.rect(bound.x, bound.y, bound.width, bound.height);
            context.fillStyle = color;
            context.fill();
        };

        const drawCellBorder = (context, bound) => {
            context.beginPath();
            context.strokeStyle = shape.borderColor;
            context.lineWidth = 1;
            context.moveTo(bound.x + bound.width, bound.y);
            context.lineTo(bound.x + bound.width, bound.y + bound.height);
            context.lineTo(bound.x, bound.y + bound.height);
            //draw default border
            context.stroke();

            const bl = shape.borders.find(b => b.r === bound.r && b.c === bound.c && b.d === "v");
            if (bl) {
                context.beginPath();
                context.strokeStyle = bl.clr;
                context.lineWidth = bl.w;
                context.moveTo(bound.x, bound.y);
                context.lineTo(bound.x, bound.y + bound.height);
                context.stroke();
            }
            const bt = shape.borders.find(b => b.r === bound.r && b.c === bound.c && b.d === "h");
            if (bt) {
                context.beginPath();
                context.strokeStyle = bt.clr;
                context.lineWidth = bt.w;
                context.moveTo(bound.x, bound.y);
                context.lineTo(bound.x + bound.width, bound.y);
                context.stroke();

            }
        };

        const fillCellValue = (() => {
            const createCellValueDisplayer = (bound) => {
                const id = shape.page.div.id + "|" + shape.id + "|" + bound.cell.r + "|" + bound.cell.c;
                let textContainer = document.getElementById(id);
                if (!textContainer) {
                    textContainer = self.createElement("div",id);
                    textContainer.style.position = "absolute";
                    textContainer.style.overflow = "hidden";
                    // textContainer.id = id;
                    textContainer.style.border = "0px";
                    textContainer.style.left = bound.x + "px";
                    textContainer.style.height = bound.height;
                    const text = self.createElement("p",id+":text");
                    text.style.position = "absolute";
                    text.style.fontSize = shape.fontSize + "px";
                    textContainer.appendChild(text);
                    self.container.appendChild(textContainer);
                }
                return textContainer;
            };

            const setValueContainerStyle = (textContainer, text, bound) => {
                //先得到默认边框
                const rect = { x: bound.x, y: bound.y, width: text.clientWidth + shape.getPadLeft() + shape.getPadRight(), height: bound.height };
                if (bound.cell.wrap === 2 || bound.cell.cs > 1 || bound.cell.rs > 1) {//
                    rect.width = bound.width - 1;
                    rect.height = bound.height - 1;

                } else {
                    bound.ha === "c" && (rect.x -= rect.width / 2 - bound.width / 2);
                    bound.ha === "r" && (rect.x -= rect.width - bound.width);
                }
                const stop_left = shape.visibleCells.find(c => c.r === bound.cell.r && c.c < bound.cell.c && c.cell && c.cell.v !== undefined && c.x + c.width > rect.x);
                const stop_right = shape.visibleCells.find(c => c.r === bound.cell.r && c.c > bound.cell.c && c.cell && c.cell.v !== undefined && c.x < rect.x + rect.width);
                const originx = rect.x;
                if (stop_left && stop_left !== bound) {
                    const delta = stop_left.x + stop_left.width - rect.x;
                    rect.x += delta;
                    rect.width -= delta;
                }
                stop_right && stop_right !== bound && (rect.width = stop_right.x - rect.x);

                bound.tx = originx;
                bound.twidth = text.clientWidth;
                text.style.left = (originx - rect.x) + shape.getPadLeft() + "px";

                textContainer.style.left = (rect.x - shape.getHeadWidth()) + "px";
                textContainer.style.top = (rect.y - shape.getHeadHeight()) + "px";
                textContainer.style.width = rect.width + "px";
                textContainer.style.height = rect.height + "px";
            };

            const calculateValue = cell => {
                if (cell.v === undefined || cell.v === null || cell.v.length === 0) return "";
                if (cell.dv !== undefined) return cell.dv;
                return interpreter(shape, cell.v, cell.r, cell.c).calculate(cell);
            }
            const setValueStyle = (text, bound) => {
                text.style.height = text.style.width = "auto";
                text.style.marginTop = text.style.marginBottom = "0px";
                let isWrap = false;
                if (bound.cell.wrap !== undefined) {
                    isWrap = bound.cell.wrap === 1;
                } else {
                    isWrap = shape.wrap === true;
                }
                const row = shape.rows[bound.cell.r] ? shape.rows[bound.cell.r] : {};
                text.style.whiteSpace = isWrap ? "pre-wrap" : "nowrap";//1 autowrap, 2 no wrap but cut, 0 no wrap,not cut
                text.style.width = text.style.maxWidth = (bound.width - shape.getPadLeft() - shape.getPadRight()) + "px";
                text.style.fontFamily = row.ff ? row.ff : shape.fontFace;
                text.style.fontSize = (row.fs ? row.fs : shape.fontSize) + "px";
                text.style.fontWeight = row.b ? true : (shape.fontWeight === FONT_WEIGHT.BOLD);
                text.style.fontStyle = row.fstyle ? row.fstyle : shape.fontStyle;
                text.style.color = row.fc ? row.fc : shape.fontColor;
                // text.style.wordBreak = "break-all";
                bound.cell.wrap === 1 && (text.style.maxWidth = text.style.minWidth);
                //horizon align
                text.style.textAlign = "left";
                (bound.ha === "c") && (text.style.textAlign = "center");
                (bound.ha === "r") && (text.style.textAlign = "right");

                text.innerHTML = calculateValue(bound.cell);
                //vertical align
                const height = text.clientHeight;
                text.style.top = (bound.height - height - shape.getPadBottom()) + "px";
                (bound.va === "c") && (text.style.top = (bound.height - height) / 2 + "px");
                (bound.va === "t") && (text.style.top = shape.getPadTop() + "px");
            };

            return (bound) => {
                const cell = bound.cell;
                if (cell && (cell.v !== undefined) && ifColumnVisible(bound.c)) {
                    const textContainer = createCellValueDisplayer(bound);
                    const text = textContainer.lastElementChild;

                    setValueStyle(text, bound);
                    setValueContainerStyle(textContainer, text, bound);
                    self.container.appendChild(textContainer);
                } else {
                    const id = div.id + ":" + bound.r + "|" + bound.c;
                    const textContainer = document.getElementById(id);
                    textContainer !== null && textContainer.remove();
                }
            }
        })();
        return (context, bound) => {
            drawCellBackground(context, bound);
            drawCellBorder(context, bound);
            fillCellValue(bound);
        };
    })();

    const drawAllCells = (() => {
        // let spanedIndex = [];//cell.colspan,rowspan跨过的cell不画
        /**
         * 得到cell边框，包括了colspan，rowspan
         */
        const getCellBound = (cell, x, y, r, c) => {
            let bound = { x, y, width: 0, height: 0, r, c };
            let colSpan = 1, rowSpan = 1, br = 0, bc = 0;
            (cell && cell.cs) && (colSpan = cell.cs, bc = c - cell.c);
            (cell && cell.rs) && (rowSpan = cell.rs, br = r - cell.r);

            for (let i = 0; i < rowSpan - br; i++) {
                for (let j = 0; j < colSpan - bc; j++) {
                    if (j === 0) {
                        const r = shape.visibleRows.find(r => r.r === i + bound.r);
                        bound.height += r ? r.height : 0;
                        r && (bound.row = r);
                    }
                    if (i === 0) {
                        const c = shape.visibleColumns.find(c => c.c === j + bound.c);
                        bound.width += c ? c.width : 0;
                        c && (bound.column = c);
                    }
                    if (i === 0 && j === 0) continue;
                }
            }
            return bound;
        };

        const init = () => {
            shape.visibleCells = [];
            // context.save();
            // context.clearRect(x + 1, y + 1, width - 2, height - 2);
            // context.rect(x, y, width - 2, height - 2);
            // context.clip();
            while (self.container.children.length > 0) self.container.children[0].remove();

        };

        const final = (x, y, width, height) => {
            // context.restore();
            shape.cellsFrame = { x, y, width, height };
            // shape.selectedCells.remove(c => !shape.visibleCells.contains(c1 => c1.r === c.r && c1.c === c.c));
            // if (shape.selectedCells.length === 0) {
            //     shape.selectedCells.push(shape.visibleCells[0]);
            //     self.drawSelection();
            // }
        };

        return (context, rows, columns, x, y, width, height) => {
            clip(context, x + 1, y + 1, width - 2, height - 2, () => {
                init();
                const spaned = [];
                const fr = rows[0];
                const lr = rows[rows.length - 1];
                const fc = columns[0];
                const lc = columns[columns.length - 1];
                const cells = shape.cache.getRange(fr.r, fc.c, lr.r, lc.c);
                rows.forEach((r, i) => {
                    columns.forEach((c, j) => {
                        context.beginPath();
                        const cell = cells.find(ec => {
                            const r1 = ec.r, c1 = ec.c;
                            const r2 = ec.rs ? (r1 + ec.rs - 1) : r1, c2 = ec.cs ? (c1 + ec.cs - 1) : c1;
                            return (r1 <= r.r && r2 >= r.r && c1 <= c.c && c2 >= c.c);
                        });
                        if (cell && spaned.contains(s => s.r === cell.r && s.c === cell.c)) return;//被 colspan和rowspan的cell不再绘制
                        if (cell && shape.visibleCells.contains(c => c.r === cell.r && c.c === cell.c)) return;
                        const bound = getCellBound(cell, c.x, r.y, r.r, c.c);
                        bound.va = r.align;
                        bound.ha = c.align;
                        (c.readonly || r.readonly) && (bound.readonly = true);
                        if (cell) {
                            bound.cell = cell;
                            cell.va && (bound.va = cell.va);
                            cell.ha && (bound.ha = cell.ha);
                            cell.bk && (bound.bk = cell.bk);
                            spaned.push(cell);
                        }
                        shape.visibleCells.push(bound);//记录所有显示的cell
                    });
                });
                shape.visibleCells.forEach(c => drawCell(context, c));
                final(x, y, width, height);
            });
        };
    })();


    self.drawCells = (cellBounds, validRect) => {
        const context = self.canvas.getContext("2d");
        translate(context, shape.margin, shape.margin, () => {
            const rect = {};
            rect.x = validRect ? validRect.x : cellBounds.min(c => c.x);
            rect.y = validRect ? validRect.y : cellBounds.min(c => c.y);
            rect.width = validRect ? validRect.width : (cellBounds.max(c => c.x + c.width) - minx);
            rect.height = validRect ? validRect.height : (cellBounds.max(c => c.y + c.height) - miny);

            const finalRect = getInteractRect(rect, shape.cellsFrame);
            if (!finalRect) return;
            context.beginPath();
            context.rect(finalRect.x, finalRect.y, finalRect.width, finalRect.height);
            context.clip();
            context.clearRect(finalRect.x + 1, finalRect.y + 1, finalRect.width - 2, finalRect.height - 2);

            cellBounds.forEach(c => drawCell(context, c));

        })
    };

    const drawToolBar = (context, x, y) => {
        const toolHeight = shape.getToolHeight();
        const toolWidth = shape.getToolWidth();
        context.fillStyle = "#edebe9";
        context.strokeStyle = shape.headBorderColor;// "gray";
        context.lineWidth = 1;
        if (toolHeight > 0) {
            context.beginPath();
            context.moveTo(x + 2, y + 2);
            context.lineTo(x + toolWidth, y + toolHeight);
            context.lineTo(x + shape.width - 2, y + toolHeight);
            context.lineTo(x + shape.width - 2, y + 2);
            context.closePath();
            context.fill();
            context.beginPath();
            context.moveTo(x + toolWidth, y + toolHeight);
            context.lineTo(x + shape.width - 1, y + toolHeight);
            context.stroke();
        }
        if (toolWidth > 0) {
            context.beginPath();
            context.moveTo(x + 2, y + 2);
            context.lineTo(x + toolWidth, y + toolHeight);
            context.lineTo(x + toolWidth, y + shape.height - 2);
            context.lineTo(x + 2, y + shape.height - 2);
            context.closePath();
            context.fill();
            context.beginPath();
            context.moveTo(x + toolWidth, y + toolHeight);
            context.lineTo(x + toolWidth, y + shape.height - 1);
            context.stroke();
        }
    };

    const drawIcon = (context, x, y) => {
        context.fillStyle = "gray";
        context.font = "italic bold 10px Arial";
        context.fillText("神笔马良", x + 3, y + 15);

    };

    const clearDraw = (context, x, y, width, height) => {
        context.clearRect(x, y, width, height);
        // context.save();
        // context.rect(x, y, width, height);
        // context.clip();

    };

    return self;
};

/**
 * 解释cell.value
 */
const interpreter = (shape, value, ridx, cidx) => {
    const cell_locator1 = '(\\$?)([a-zA-Z]+)(\\$?)([1-9]\\d*(?!\\d*[a-zA-Z]))';
    const cell_locator2 = '\\$?([a-zA-Z]+)\\$?([1-9]\\d*(?!\\d*[a-zA-Z]))';
    const cell_locator3 = cell_locator2 + "\\:" + cell_locator2 + "|" + cell_locator2 + "|\\$?([a-zA-Z]+)\\:([a-zA-Z]+)|\\$?([1-9]\\d*)\\:([1-9]\\d*)";

    const self = {};
    self.isFormular = () => {
        return /^=\s*\$?\w*/.test(value);
    };

    self.isOperator = () => {
        return /(\=\s*|[\(\/\+\-\*])$/.test(value);
    };

    self.inRecursion = (all) => {
        const findDuplicate = f => {
            for (let i = 0; i < all.length; i++) {
                if (f(all[i])) return true;
            }
            return false;
        }
        const findRangeDuplicate = (r1, c1, r2, c2, cells_id) => {
            return findDuplicate(c => {
                if (c[1] === cells_id) return false;
                if (c[0] === "cell" && c[2] >= r1 && c[2] <= r2 && c[3] >= c1 && c[4] <= c2) return true;
                if (c[0] === "range" && isRectInteractRect({ y: c[2], x: c[3], height: c[4] - c[2] + 0.9, width: c[5] - c[3] + 0.9 }, { x: c1, y: r1, width: c2 - c1 + 0.9, height: r2 - r1 + 0.9 })) return true;
                if (c[0] === "col" && (Math.max(c1, c2, c[2], c[3]) - Math.min(c1, c2, c[2], c[3]) <= (c2 - c1 + c[3] - c[2]))) return true;
                if (c[0] === "row" && (Math.max(r1, r2, c[2], c[3]) - Math.min(r1, r2, c[2], c[3]) <= (r2 - r1 + c[3] - c[2]))) return true;
                return false;
            });
        };
        const findCellDuplicate = (r, c, cells_id) => {
            return findDuplicate(cell => {
                if (cell[1] === cells_id) return false;
                if (cell[0] === "cell" && cell[2] === r && cell[3] === c) return true;
                if (cell[0] === "range" && r >= cell[2] && c >= cell[3] && r <= cell[4] && c <= cell[5]) return true;
                if (cell[0] === "col" && c >= cell[2] && c <= cell[3]) return true;
                if (cell[0] === "row" && r >= cell[2] && r <= cell[3]) return true;
                return false;
            });
        };
        const findColumnDuplicate = (c1, c2, cells_id) => {
            return false;
        };
        const findRowDuplicate = (r1, r2, cells_id) => {
            return false;
        };
        !all && (all = [["cell", ridx, cidx]]);
        // all.push(["cell", r, c]);
        const cells = self.getCells();
        const cells_id = ridx + "|" + cidx;
        for (let i = 0; i < cells.length; i++) {
            const c = cells[i];
            //range match
            if (c.length === 4) {
                const c1 = util.charToIndex(c[0]);
                const r1 = Number.parseInt(c[1]);
                const c2 = util.charToIndex(c[2]);
                const r2 = Number.parseInt(c[3]);
                if (findRangeDuplicate(r1, c1, r2, c2, cells_id)) return true;
                all.push(["range", cells_id, r1, c1, r2, c2]);
                const cs = shape.cache.getRange(r1, c1, r2, c2);
                for (let j = 0; j < cs.length; j++) {
                    if (!cs[j].v) continue;
                    const interpret = interpreter(shape, cs[j].v, cs[j].r, cs[j].c);
                    if (!interpret.isFormular()) continue;
                    if (interpret.inRecursion(all)) return true;
                };

            }
            //cell match
            if (c.length === 2) {
                const c1 = util.charToIndex(c[0]);
                const r1 = Number.parseInt(c[1]);
                if (findCellDuplicate(r1, c1, cells_id)) return true;
                all.push(["cell", cells_id, r1, c1]);
                const cell = shape.cache.find(c0 => c0.c === c1 && c0.r === r1);
                if (!(cell && cell.v)) continue;
                const interpret = interpreter(shape, cell.v, r1, c1);
                if (!interpret.isFormular()) continue;
                if (interpret.inRecursion(all)) return true;
            }
            //columns match
            if (c[0] === "col") {
                const c1 = util.charToIndex(c[0]);
                const c2 = util.charToIndex(c[1]);
                if (findColumnDuplicate(c1, c2, cells_id)) return true;
                all.push(["col", cells_id, c1, c2]);
            }
            //rows match
            if (c[0] === "row") {
                const r1 = Number.parseInt(c[0]);
                const r2 = Number.parseInt(c[1]);
                if (findRowDuplicate(r1, r2, cells_id)) return true;
                all.push(["row", cells_id, r1, r2]);
            }
        }
        return false;
    };

    const calculate = () => {
        const NULL = "#NULL!", RECURSION = "#RECURSION!", ERROR = "#ERROR!";
        if (!self.isFormular()) {
            const num = Number(value);
            if (isNaN(num)) return value;
            else return num;
        }
        if (self.inRecursion()) return RECURSION;
        const code = self.injectData(shape);
        if (code === NULL || code === RECURSION || code === ERROR) return code;
        try {
            const result = eval(code);
            if (result === undefined) return NULL;
            else return result;
        } catch (e) {
            console.warn(e);
            return ERROR;
        }
    }
    self.calculate = cell => {
        if (cell) {
            if (cell.dv === undefined) {
                cell.dv = calculate();
            }
            return cell.dv;
        } else {
            return calculate();
        }
    };

    self.replace = (oldKey, newKey, start) => {
        const reg = new RegExp("(?<!\\w)(" + oldKey + ")(?!\\w)", "g");
        if (reg.test(value)) {
            return value.replace(reg, (match, group, index) => index === start ? newKey : group);
        } else {
            return value + newKey;
        }
    };

    self.injectData = () => {
        const reg = new RegExp(cell_locator3, "g");
        const replace = (match, col_from, row_from, col_to, row_to, col, row, cols_from, cols_to, rows_from, rows_to) => {
            if (col_from) {//its range
                const c_f = util.charToIndex(col_from);
                const c_t = util.charToIndex(col_to);
                const r_f = Number.parseInt(row_from) - 1;
                const r_t = Number.parseInt(row_to) - 1;
                const cells = shape.cache.filter(c => c.c >= c_f && c.c <= c_t && c.r >= r_f && c.r <= r_t && c.v);
                return cells.map(c => interpreter(shape, c.v, c.r, c.c).calculate(c));
            }
            if (col) {//its cell
                const c = util.charToIndex(col);
                const cell = shape.cache.find(c1 => c1.r === Number.parseInt(row) - 1 && c1.c === c);
                if (!(cell && cell.v)) return "";
                else {
                    return interpreter(shape, cell.v, cell.r, cell.c).calculate(cell);
                }
            }
            if (cols_from) {
                return "[" + shape.cache.filter(c => c.c >= util.charToIndex(cols_from) && c.c <= util.charToIndex(cols_to) && c.v).map(c => interpreter(shape, c.v, c.r, c.c).calculate(c)) + "]";
            }
            if (rows_from) {
                return shape.cache.filter(c => c.r >= Number.parseInt(rows_from) - 1 && c.r <= Number.parseInt(rows_to) - 1 && c.v).map(c => interpreter(shape, c.v, c.r, c.c).calculate(c));

            }
        }
        return value.replace(reg, replace).substr(1);

    };

    self.dynamicChangeRanges = (offset_row, offset_col) => {
        // const reg = new RegExp(cell_locator, "gm");
        const replace = (match, fix_col, col, fix_row, row) => {
            const fix_col1 = fix_col ? fix_col : "";
            const fix_row1 = fix_row ? fix_row : "";
            const col1 = fix_col && offset_col !== 0 ? col : util.indexToChar(util.charToIndex(col) + offset_col);
            const row1 = fix_row ? row : (Number.parseInt(row) + offset_row);
            return fix_col1 + col1 + fix_row1 + row1;
        }
        return value.replace(new RegExp(cell_locator1, "gm"), replace);
    };

    self.getCells = () => {
        const reg = new RegExp(cell_locator3, "g");
        let match = reg.exec(value);
        const cells = [];
        while (match) {
            let cell;
            if (match[1]) {
                cell = [match[1], match[2] - 1, match[3], match[4] - 1];
            }
            if (match[5]) {
                cell = [match[5], match[6] - 1];
            }
            if (match[7]) {
                cell = ["col", match[7], match[8]];
            }
            if (match[9]) {
                cell = ["row", Number.parseInt(match[9]) - 1, Number.parseInt(match[10]) - 1];
            }
            cell.index = match.index;
            cells.push(cell);
            match = reg.exec(value);
        }
        return cells;
    };

    return self;

};

const util = (() => {
    const self = {};
    const n2a = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"]
    const COUNT = n2a.length;
    self.indexToChar = (num, base) => {
        const d1 = Math.floor(num / COUNT);
        const d2 = num % COUNT;
        const result = n2a[d2] + (base ? base : "");
        if (d1 === 0) return result;
        else return self.indexToChar(d1 - 1, result);
    };
    self.charToIndex = chars => {
        chars = chars.toUpperCase();
        const len = chars.length;
        let index = 0;
        for (let i = 0; i < len; i++) {
            index += n2a.indexOf(chars[i]) + COUNT * (len - i - 1);
        }
        return index;
    }
    return self;
})();

export { grid, table, GRID_MODE };