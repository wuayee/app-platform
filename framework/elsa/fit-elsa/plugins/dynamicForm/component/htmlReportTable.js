import {formComponent} from "../form.js";
import {rectangle} from "../../../core/rectangle.js";
import {rectangleDrawer} from "../../../core/drawers/rectangleDrawer.js";

/**
 * table表格.
 *
 * @override
 */
const htmlReportTable = (id, x, y, width, height, parent, drawer) => {
    const self = formComponent(rectangle, id, x, y, width, height, parent, drawer ? drawer : htmlReportTableDrawer);
    self.type = "htmlReportTable";
    self.serializedFields.batchAdd("tableData");
    self.autoHeight = true;
    self.minHeight = 60;

    // 不展示文本，因此不需要text dom元素.
    self.hideText = true;

    /**
     * @override
     */
    self.getData = () => {
        return self.tableData;
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        if (!data) {
            return;
        }
        self.tableData = data;
    };

    return self;
};

/**
 * table表格绘制器.
 *
 * @override
 */
const htmlReportTableDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);
    self.type = "htmlReportTableDrawer";

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.tableContainer = document.createElement("div");
        self.tableContainer.id = "table_container_" + shape.id;
        self.tableContainer.style.width = "100%";
        self.tableContainer.style.pointerEvents = "auto";
        self.parent.appendChild(self.tableContainer);

        // CSS样式
        const style = document.createElement("style");
        style.innerHTML = `
            table {
                width: 100%;
            }
            
            th {
                background-color: rgb(242, 245, 252);
            }
            
            th, td {
                padding-top: 6px;
                padding-bottom: 6px;
                height: 32px;
                font-size: 14px;
                text-align: center;
            }
            
            table, th, td {
                border: none;
                outline-style: solid;
                outline-color: #ebecef;
                outline-width: 1px;
            }
            
            .table-wrapper {
                max-height: 500px;
                overflow: auto;
            }
        `;
        self.parent.appendChild(style);
    };

    /**
     * @override
     */
    const drawStatic = self.drawStatic;
    self.drawStatic = () => {
        drawStatic.apply(self);
        if (shape.tableData) {
            self.tableContainer.innerHTML = "";

            // 创建标题.
            const title = document.createElement("div");
            title.innerHTML = shape.tableData.chartTitle;
            title.style.fontSize = "14px";
            title.style.color = "rgb(113, 117, 127)";
            title.style.fontWeight = "400";
            self.tableContainer.appendChild(title);

            // 创建表格
            const chartData = shape.tableData.chartData;
            if (chartData) {
                const wrapper = document.createElement("div");
                wrapper.classList.add("table-wrapper");
                wrapper.style.marginTop = "10px";
                const table = createTable(chartData.columns, chartData.rows);
                wrapper.appendChild(table);
                self.tableContainer.appendChild(wrapper);
            }

            // 此时由于在drawStatic绘制了table，导致dom变高了
            // autoHeight(查看rectangleDrawer中的autoHeight，会发现其会随着text元素的变化而变化)会导致parent的高度随着子元素的高度而变化，但不会修改图形的高度
            // 容器的autoFit又是根据子组件的高度来判断是否需要resize，因此，这里需要手动修改一下图形的高度.
            if (self.tableContainer.clientHeight !== 0) {
                shape.height = self.tableContainer.clientHeight;
            }
        }
    };

    return self;
};

/**
 * 创建表格.
 *
 * @param columns 列.
 * @param rows 行.
 * @return {HTMLTableElement}
 */
const createTable = (columns = [], rows = []) => {
    const table = document.createElement("table");

    // 构建标题.
    const tr = document.createElement("tr");
    tr.style.position = "sticky";
    tr.style.top = "1px";
    table.appendChild(tr);
    columns.forEach(c => {
        const th = document.createElement("th");
        tr.appendChild(th);
        th.innerHTML = c;
    });

    // 构建数据.
    rows.forEach(r => {
        const tr = document.createElement("tr");
        table.appendChild(tr);

        if (!Array.isArray(r)) {
            throw new Error("row must be array.");
        }
        r.forEach(d => {
            const td = document.createElement("td");
            tr.appendChild(td);
            td.innerHTML = d;
        });
    });

    return table;
};

export {htmlReportTable, htmlReportTableDrawer};