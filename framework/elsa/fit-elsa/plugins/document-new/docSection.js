import {container} from "../../core/container.js";
import {CURSORS, DOCK_MODE} from "../../common/const.js";
import {docDrawer} from "./drawer/docDrawer.js";
import {uuid} from "../../common/util.js";
import BuilderFactory from "./builders/builderFactory.js";
import {SHAPE_IN_DOCUMENT_MODE} from "./common/const.js";
import {addCommand, deleteCommand, layoutCommand, positionCommand} from "../../core/commands.js";

/**
 * 代表文档中的一个元素.
 *
 * @param id 唯一标识.
 * @param x 横坐标.
 * @param y 纵坐标.
 * @param width 宽度.
 * @param height 高度.
 * @param parent 父容器.
 * @param drawer 绘制器.
 * @return {*}
 */
export const docSection = (id, x, y, width, height, parent, drawer = docDrawer) => {
    const self = container(id, x, y, width, height, parent, drawer);
    self.type = "docSection";
    self.backColor = "rgba(255,255,255,0)";
    self.fontColor = "black";
    self.dockMode = DOCK_MODE.NONE;
    self.autoHeight = false;
    self.autoWidth = false;
    self.selectable = true;
    self.deletable = true;
    self.moveable = true;

    self.fontFace = "arial";
    self.headColor = "transparent";
    self.borderWidth = 0;
    self.focusBorderWidth = 0;

    // 顶层docSection.
    self.topSection = null;
    self.text = "default";
    self.hAlign = "left";
    self.cursorStyle = CURSORS.DEFAULT;
    self.ignoreCoEditFields = ['x', 'y', 'width', 'height'];

    /**
     * 获取数据，并将数据打平.
     *
     * @return {FlatArray<*[], number>[]|*[]} 数组.
     */
    self.getData = () => {
        const docSections = self.getShapes(s => s.isTypeof("docSection"));
        if (docSections.length === 0) {
            if (self.isTopSection()) {
                return [{
                    attributes: {id: uuid()}, name: "paragraph", children: [{data: "请输入内容"}]
                }];
            }
            return [self.text];
        }

        // 将数组打平.
        return docSections
            .map(s => s)
            .orderBy(s => s.docIndex)
            .map(s => s.getData())
            .flat(Infinity);
    };

    /**
     * 解析数据.
     * 根据编辑器的数据，对docSection进行相应的增删改操作.
     *
     * @param data 编辑器数据.
     */
    self.parseData = (data) => {
        if (!self.isTopSection()) {
            return;
        }

        // 因为编辑器中没有结构化文档这个概念，因此这里需要排除结构化文档.
        const sectionMap = new Map(self.getDescendants()
            .filter(s => s.isTypeof("docSection"))
            .filter(s => !s.isDoc())
            .map(s => [s.id, s]));

        const containers = [];
        let totalHeight = 0;
        for (let index = 0; index < data.length; index++) {
            const element = data[index];

            // 累加总高度.
            totalHeight += element.height;

            let section = sectionMap.get(element.attributes.id);
            const builder = BuilderFactory.getBuilder(element.name);

            // 不存在，则创建section.
            if (!section) {
                section = builder.create(element, self, containers.length > 0 ? containers[containers.length - 1] : self);
            } else {
                // 否则，删除sectionMap中的数据，最后sectionMap中剩下的数据就是需要删除的数据.
                sectionMap.delete(section.id);
            }

            // 设置section属性.
            builder.set(section, element, {index, containers, topSection: self});

            // 如果当前section是heading了，则将其所属结构化文档加入到containers中.
            if (section.isHeading()) {
                containers.push(section.getContainer());
            }
        }

        // 删除该删除的图形.
        sectionMap.forEach(s => s.remove());

        // 设置高度.
        self.setHeight(totalHeight);
        self.heightChangeCallback && self.heightChangeCallback(self.height);
    };

    /**
     * 设置section的高度.
     *
     * @param height 高度.
     */
    self.setHeight = (height) => {
        self.height = height;
    }

    self.calcPosition = (element) => {
        const topSection = self.getTopSection();
        const topSectionDomRect = topSection.drawer.parent.getBoundingClientRect();
        // @马莉亚 2022.12.30修改：doc图形中文本水平方向默认和跟文档齐平
        // const x = topSection.x;
        const x = topSection.x + (element.x - topSectionDomRect.x);
        const y = topSection.y + (element.y - topSectionDomRect.y);
        return {x: parseFloat(x.toFixed(2)), y: parseFloat(y.toFixed(2))};
    }

    self.onHeightChange = (callback) => {
        if (typeof callback !== "function") {
            return;
        }
        self.heightChangeCallback = callback;
    };

    /**
     * 添加 {@link #docSection} 对象到当前 {@link #docSection} 中.
     * 1、添加时，需要重新计算当前 {@link #docSection} 的宽高.
     * 2、并且需要通知当前 {@link #docSection} 的父容器更新宽高.
     *
     * @param section 待添加的docSection的对象.
     */
    self.addSection = (section) => {
        section.container = self.id;
        self.onSizeChange(section.height, section.width);
    };

    /**
     * 当resize发生变化时调用.
     *
     * @param height 变化的高度.
     * @param width 宽度.
     */
    self.onSizeChange = (height, width) => {
        if (self.isTopSection()) {
            return;
        }

        (self.height === 2.1) && (self.height = 0);
        self.height += height;
        self.width = Math.max(self.width, width);
        self.getContainer().onSizeChange(height, width);
    };

    /**
     * 重写remove方法.
     * 1、若当前 {@link #docSection} 对象是一个标题，在删除该对象的时候，需要同步删除其容器节点.
     * 2、这里不需要考虑其子节点的所属container变化的问题，因为在解析编辑器数据的时候，会重新计算每个元素的containerId.
     *
     * @param source 来源.
     */
    const remove = self.remove;
    self.remove = (source) => {
        if (self.isDoc()) {
            const container = self.getContainer();
            self.getShapes().forEach(s => s.container = container.id);
            return remove.apply(self, [source]);
        } else {
            const container = self.getContainer();
            const removed = remove.apply(self, [source]);
            if (self.isHeading()) {
                removed.push.apply(removed, container.remove(source));
            }
            return removed;
        }
    };

    /**
     * 判断一个docSection是否是一个结构化文档.
     *
     * @return {string|*|boolean} true/false.
     */
    self.isDoc = () => {
        return self.docType && self.docType === "structuredDoc";
    };

    /**
     * 判断一个docSection是否是标题.
     *
     * @return {string|*|boolean} true/false.
     */
    self.isHeading = () => {
        return self.docType && self.docType.startsWith("heading");
    };

    /**
     * 判断当前docSection是否是顶级docSection.
     *
     * @return {boolean} true，是顶级section；false，不是顶级section.
     */
    self.isTopSection = () => {
        // 如果已被删除，则肯定不是顶层结构化文档.
        if (self.container === "") {
            return false;
        }
        const topSection = self.getTopSection();
        return topSection === self;
    };

    /**
     * 获取顶级docSection
     *
     * @return {null|*} 顶级section.
     */
    self.getTopSection = () => {
        if (self.topSection !== null) {
            return self.topSection;
        }
        let parent = self;
        while (parent.getContainer().isTypeof("docSection")) parent = parent.getContainer();
        return parent;
    };

    /**
     * 获取所有的后代列表.
     *
     * @return {*} shapes列表.
     */
    self.getDescendants = () => {
        return self.page.shapes.filter(s => self !== s && self.isMyBlood(s));
    };

    /**
     * 顶层文档才有连接点.
     *
     * @override
     */
    const initConnectors = self.initConnectors;
    self.initConnectors = () => {
        if (self.isTopSection() && self.dragable) {
            initConnectors.apply(self);
        } else {
            self.connectors = [];
        }
    };

    /**
     * 当鼠标进入docSection，开启docSection的text，接收鼠标事件.
     *
     * @override
     */
    self.onMouseIn = (current, pre) => {
        const topSection = self.getTopSection();
        const isPreShapeDocSectionAndMyBlood = pre && pre.isTypeof("docSection") && topSection.isMyBlood(pre);
        if (!isPreShapeDocSectionAndMyBlood && self.page.wantedShape.isEmpty()) {
            topSection.drawer.enableTextPointerEvents();
        }

        //结构化段落 显示“共享”region
        // if (self.isHeading()) {
        //     self.shareAble = true;
        // }

        // 编辑时也会触发该方法.
        // if (self.isTopSection() || self.getTopSection().isEditing()) {
        //     return;
        // }
        //
        // if (self.isHeading()) {
        //     self.getContainer().backColor = "whitesmoke";
        // } else {
        //     self.backColor = "whitesmoke";
        // }
    };

    /**
     * 当鼠标进入docSection，禁用docSection的text，不接收鼠标事件.
     */
    self.onMouseOut = () => {
        const topSection = self.getTopSection();
        const mouseInShape = self.page.mouseInShape;
        if (!mouseInShape.isTypeof("docSection") || !topSection.isMyBlood(mouseInShape)) {
            topSection.drawer.disableTextPointerEvents();
        }

        //隐藏“共享”region
        // if (self.isHeading()) {
        //     self.shareAble = false;
        // }

        // if (self.isTopSection() || self.getTopSection().isEditing()) {
        //     return;
        // }
        //
        // if (self.isHeading()) {
        //     self.getContainer().backColor = "rgba(255,255,255,0)";
        // } else {
        //     self.backColor = "rgba(255,255,255,0)";
        // }
    };

    /**
     * 其他docSection的mouseMove事件托管给顶层文档处理.
     *
     * @inheritDoc
     * @override
     */
    const onMouseMove = self.onMouseMove;
    self.onMouseMove = (position) => {
        if (!self.isTopSection()) {
            self.getTopSection().onMouseMove(position);
        } else {
            onMouseMove.apply(self, [position]);
        }
    };

    /**
     * 顶层文档才能拖拽.
     *
     * @inheritDoc
     * @override
     */
    const getDragable = self.getDragable;
    self.getDragable = () => {
        return self.isTopSection() && getDragable.apply(self);
    };

    /**
     * 只有是顶层 {@link #docSection} 并且 selectable 为 true 才能选中.
     *
     * @inheritDoc
     * @override
     */
    const getSelectable = self.getSelectable;
    self.getSelectable = () => {
        return self.isTopSection() && getSelectable.apply(self);
    };

    /**
     * 只允许 {@link #docSection} 作为 child.
     *
     * @override
     */
    self.childAllowed = s => s.isTypeof("docSection");

    /**
     * 当选区没在 {@link #docSection} 中时，不用获取值..
     *
     * @inheritDoc
     * @override
     */
    const getFormatValue = self.getFormatValue;
    self.getFormatValue = (key) => {
        if (!self.isTopSection()) {
            return;
        }
        return self.drawer.getEditor().isFocused() && getFormatValue.apply(self, [key]);
    };

    /**
     * 当选区没在 {@link #docSection} 中时，不进行计算.
     *
     * @inheritDoc
     * @override
     */
    const isFormatted = self.isFormatted;
    self.isFormatted = (key) => {
        if (!self.isTopSection()) {
            return;
        }
        return self.drawer.getEditor().isFocused() && isFormatted.apply(self, [key]);
    };

    /**
     * 1、当获取 deletable 时，必须要是 topSection 才能被删除.
     * 2、当获取 moveable 时，必须要是 topSection 才能移动.
     *
     * @inheritDoc
     * @override
     */
    const get = self.get;
    self.get = (field) => {
        if (field === "deletable") {
            return self.isTopSection() && self._data.deletable;
        }

        if (field === "moveable") {
            return self.isTopSection() && self._data.moveable;
        }

        return get.apply(self, [field]);
    };

    /**
     * 当新增嵌入式图形时触发.
     *
     * @param data 数据.
     */
    self.onEmbedShapeAdd = (data) => {
        if (data.isUndo) {
            return;
        }
        const isEmbed = (s) => s.mode && s.mode === SHAPE_IN_DOCUMENT_MODE.EMBED;
        const embedShapeMap = new Map(self.page.shapes.filter(s => isEmbed(s)).map(s => [s.id, s]));
        data.shapes.forEach(s => {
            const shape = embedShapeMap.get(s.shapeId);
            if (shape) {
                const position = self.calcPosition({x: s.item.x, y: s.item.y});
                const commandData = shape.adjust(position);
                if (commandData.x.preValue !== commandData.x.value || commandData.y.preValue !== commandData.y.value) {
                    positionCommand(shape.page, [commandData]);
                }

                // 文本:
                // xxxxxxxx[  嵌入图形  ]xxxxxx
                // 当在嵌入图形之前按下回车，会重新生成一个shape，导致图形和shape的关联关系不正确，从而出现异常.因此这里需要判断，如果externalId不一致的话需要重新设置.
                if (shape.externalId !== s.item.attributes.id) {
                    layoutCommand(shape.page, [{shape, externalId: s.item.attributes.id}]).execute(shape.page);
                }
            }
        });
    };

    /**
     * 当删除嵌入式图形时触发.
     *
     * @param data 数据.
     */
    self.onEmbedShapeRemove = (data) => {
        if (data.isUndo) {
            return;
        }
        const shapes = data.shapes.map(id => self.page.getShapeById(id)).filter(s => s && s.mode === SHAPE_IN_DOCUMENT_MODE.EMBED && !s.inDragging);
        if (shapes && shapes.length > 0) {
            const cmd = deleteCommand(self.page, shapes.map(s => {
                return {shape: s};
            }));
            cmd.execute();
        }
    };

    /**
     * 当拷贝嵌入式图形时触发.
     *
     * @param data 数据.
     */
    self.onEmbedShapeCopy = (data) => {
        if (data.isUndo) {
            return;
        }
        data.shapes.forEach(s => {
            const shape = self.page.getShapeById(s.oldId);
            const position = self.calcPosition({x: s.item.x, y: s.item.y});
            const shapeData = shape.serialize();
            delete shapeData.id;
            delete shapeData.x;
            delete shapeData.y;
            const newShape = self.page.createNew(shape.type, position.x, position.y, s.newId, shapeData);
            newShape.externalId = s.item.attributes.id;
            addCommand(self.page, [{shape: newShape}]);
        });
    };

    /**
     * 当修改嵌入式图形时触发.
     *
     * @param data 数据.
     */
    self.onEmbedShapeModify = (data) => {
        data.shapes.forEach(s => {
            const shape = self.page.getShapeById(s.attributes.externalid);
            const position = self.calcPosition({x: s.x, y: s.y});
            shape.adjust(position);
        });
    }

    /**
     * 获得所有悬浮态的图形.
     *
     * @return {*} 处于悬浮状态的图形集合.
     */
    self.getSuspensionShapes = () => {
        return self.page.shapes.filter(s => s.mode && s.mode === SHAPE_IN_DOCUMENT_MODE.SUSPENSION);
    };

    /* ---------------------------- 文档变化相关操作 ---------------------------- */
    /**
     * 文档选区发生变化时调用.
     *
     * @param selectedBlocks 文档中选中的元素.
     */
    self.onDocSelectionChange = (selectedBlocks) => {
        if (selectedBlocks.length === 0 || selectedBlocks.length === 1) {
            self.getSuspensionShapes().filter(s => s.isSelectedWithText).forEach(s => s.isSelectedWithText = false);
            return;
        }

        const topSection = self.getTopSection();
        const positions = selectedBlocks.map(block => topSection.calcPosition(block));
        const y1 = positions[0].y;
        const y2 = positions[positions.length - 1].y + selectedBlocks[selectedBlocks.length - 1].height;
        self.getSuspensionShapes().forEach(s => {
            s.isSelectedWithText = s.y > y1 && s.y < y2;
        });
    };

    /**
     * 文档批量删除时的调用.
     */
    self.onDocDeleteBatch = () => {
        const shapes = self.getSuspensionShapes().filter(s => s.isSelectedWithText);
        if (shapes && shapes.length > 0) {
            const cmd = deleteCommand(self, shapes.map(s => {
                return {shape: s};
            }));
            cmd.execute(self);
        }
    };

    /**
     * 文档拷贝时调用.
     *
     * @param event 事件对象.
     * @param anchorElement 锚点元素.
     * @return {*} 复制结果.
     */
    self.onDocCopy = (event, anchorElement) => {
        // 阻止冒泡，防止调用到elsa的拷贝逻辑.
        event.stopPropagation();
        const selectedShapes = self.getSuspensionShapes().filter(s => s.isSelectedWithText);
        const copyResult = self.page.onCopy(selectedShapes);
        if (event.clipboardData && copyResult) {
            event.preventDefault();
            const position = self.calcPosition(anchorElement);

            // 计算y轴的偏移量.粘贴的时候需要基于该偏移量计算粘贴图形的位置.
            const data = copyResult.data;
            data.forEach(d => {
                d.offsetY = d.y - position.y;
            });
            event.clipboardData.setData("elsa/" + copyResult.type + "/doc", JSON.stringify(data));
        }
        return copyResult;
    };

    /**
     * 文档剪切时调用.
     *
     * @param event 事件对象.
     * @param anchorElement 锚点元素.
     */
    self.onDocCut = (event, anchorElement) => {
        const copyResult = self.page.onDocCopy(event, anchorElement);
        copyResult && copyResult.cut();
    };

    /**
     * 文档粘贴时调用.
     *
     * @param event 事件对象.
     * @param anchorElement 锚点元素.
     */
    self.onDocPaste = (event, anchorElement) => {
        const data = event.clipboardData.getData("elsa/shape/doc");
        if (!data) {
            return;
        }

        const position = self.calcPosition(anchorElement);
        const shapesData = (typeof data === 'string') ? eval("(" + data + ")") : data;
        self.page.paste(shapesData, (data) => data.x, (data) => {
            const y =  position.y + data.offsetY;
            delete data.offsetY;
            return y;
        });
    };

    self.serializedFields.add("docType");
    self.serializedFields.add("docIndex");

    return self;
};