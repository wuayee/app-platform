import {
    INFO_TYPE,
    MIND_ITEM_DIRECTION,
    MIND_ITEM_HEIGHT,
    MIND_ITEM_STATUS,
    MIND_MODE,
    PROGRESS_STATUS
} from '../../common/const.js';
import { topic } from './topic.js';
import { itemExpandRegion } from './hitRegion.js';

/*
xmind的第二层主题
subtopic不可以移动，只能通过shadow调节位置
辉子 2018-11
*/

let subTopic = (id, x, y, width, height, parent) => {
    const ROW_GAP = 24, COLUMN_GAP = 24, STEP = 13, OFFSET_Y = -7;
    let self = topic(id, x, y, width, height, parent);
    self.borderWidth = 1;
    self.fontSize = 13;
    self.pad = 5;
    self.type = "subTopic";
    self.text = "Sub Topic";
    self.height = MIND_ITEM_HEIGHT;
    self.cornerRadius = 4;
    self.parent = "";
    self.image = "";

    //-----------need to be serialized----------------------
    self.priority = 0;
    self.infoType = INFO_TYPE.NONE;//信息，警告，错误
    self.progressStatus = PROGRESS_STATUS.NONE;//未开始，运行，暂停，完成
    self.progressPercent = 0.28;//0-1
    self.showedProgress = false;

    // self.getParent = () => self.getContainer().getShapes().find(s => s.id === self.parent);
    self.getParent = () => self.page.shapes.find(s => s.id === self.parent);

    let getImageHeightOrWidth = heightorWidth => {
        let img = self.page.getShapeById(self.image);
        if (img === null || img === undefined) {
            return 0;
        }
        return img[heightorWidth];

    };
    self.getImageHeight = () => getImageHeightOrWidth("height");
    self.getImageWidth = () => getImageHeightOrWidth("width");

    self.virtualSize = {};
    self.getVirtualWidth = (session) => {
        if (session && self.virtualSize.session === session) return self.virtualSize.width;
        
        let virtualWidth = self.width;
        if (self.getContainer().mode !== MIND_MODE.ORG) {
            return self.width;
        }

        let parent = self.getParent();
        self.visible = parent.visible && parent.status === MIND_ITEM_STATUS.EXPANDED
        if (self.status === MIND_ITEM_STATUS.COLLAPSED) {
            return self.width;
        }

        let shapes = self.getContainer().getShapes().filter(s => s.parent == self.id);
        self.expandRegion.visible = shapes.length > 0;
        if (shapes.length > 0 && self.status === MIND_ITEM_STATUS.EXPANDED) {
            virtualWidth = shapes.sum(s => s.getVirtualWidth(session) + COLUMN_GAP);
            virtualWidth -= COLUMN_GAP;
            virtualWidth = virtualWidth < self.width ? self.width : virtualWidth;
        }

        if (session) {
            self.virtualSize.session = session;
            self.virtualSize.width = virtualWidth;
        }
        return virtualWidth;
    };
    self.getVirtualHeight = (session, mode) => {
        if (session && self.virtualSize.session === session) return self.virtualSize.height;

        let virtualHeight = self.height;
        if (self.getContainer().mode === MIND_MODE.ORG) {
            return virtualHeight;
        }

        let parent = self.getParent();
        self.visible = (parent.visible && parent.status === MIND_ITEM_STATUS.EXPANDED);
        // self.render();
        if (self.status === MIND_ITEM_STATUS.COLLAPSED) {
            return virtualHeight;
        }

        let shapes = self.getChildren();// self.getContainer().getShapes().filter(s => s.parent == self.id);
        self.expandRegion.visible = shapes.length > 0;
        if (shapes.length > 0 && self.status === MIND_ITEM_STATUS.EXPANDED) {
            virtualHeight = shapes.sum(s => s.getVirtualHeight(session) + ROW_GAP);
            virtualHeight -= ROW_GAP;
            virtualHeight = virtualHeight < self.height ? self.height : virtualHeight;
        }

        if (session) {
            self.virtualSize.session = session;
            self.virtualSize.height = virtualHeight;
        }
        return virtualHeight;
    };

    // let keyPressed = self.keyPressed;
    // self.keyPressed = (e) => {
    //     if (e.code.indexOf("ArrowUp") >= 0) {
    //         let items = self.getParent().getChildren();
    //         var index = items.indexOf(self);
    //         if (index > 0) {
    //             self.unSelect();
    //             items[index - 1].select();
    //         }
    //         return false;
    //     }
    //     if (e.code.indexOf("ArrowDown") >= 0) {
    //         let items = self.getParent().getChildren();
    //         var index = items.indexOf(self);
    //         if (index < items.length - 1) {
    //             self.unSelect();
    //             items[index + 1].select();
    //         }
    //         return false;
    //     }
    //     return keyPressed.apply(self, [e]);
    // };

    let mouseUp = self.onMouseUp;
    self.onMouseUp = position => {
        mouseUp.apply(self, [position]);
        const mind = self.getContainer();
        if (!mind.gotoShape || mind.gotoShape.shape === null) {
            return;
        }
        mind.gotoShape.takeEffect();
        mind.gotoShape.shape = null;
        mind.gotoShape.source = null;
        mind.invalidate();
    };
    self.onMouseDrag = (position) => {
        let notParent = shape => {//can't move self to its children
            if (!shape.parent) return true;
            let parentId = shape.parent;
            if (shape.root !== self.root) return true;
            while (parentId !== self.root) {
                if (parentId === self.id) {
                    return false;
                }
                parentId = self.getContainer().getShapes().find(s => s.id === parentId).parent;
            }
            return true;
        }
        let shape = self.page.switchMouseInShape(position.x, position.y);
        if (!shape.isTypeof("topic")) return;

        const gotoShape = shape.getContainer().gotoShape;
        gotoShape.source = self;
        if (shape !== self && shape.namespace === self.namespace && notParent(shape)) {
            gotoShape.shape = shape;
            gotoShape.moveTo(position.x, position.y);
        } else {
            gotoShape.shape = null;
        }
        self.getContainer().invalidateAlone();
    };
    let drawConnection = self.drawConnection;
    self.drawConnection = (context, x, y, mode) => {
        if (self.status === MIND_ITEM_STATUS.COLLAPSED || !self.visible) {
            return
        }

        drawConnection.apply(self, [context, x, y, mode]);

        let drawShadow = (context, x1, y1, width, height) => {
            context.roundRect(x + x1, y + y1, width, height, 4, "gray", "lightgray", 1, 0.2);
        }
        let drawMyFrame = () => {
            context.roundRect(x + self.x - 3, y + self.y - 3, self.width + 4, self.height + 4, 4, "RGBA(222,222,222,0.3)", "darkorange", 1, 0.2);
            // context.strokeStyle = "darkorange";
            // context.beginPath();
            // context.globalAlpha = 1;
            // context.rect(x + self.x - 3, y + self.y - 3, self.width + 4, self.height + 4);
            // context.stroke();
            // context.closePath();
        }

        let gotoShape = self.getContainer().gotoShape;

        if (gotoShape.source !== self || !self.page.isMouseDown()) {
            return;
        }
        drawMyFrame();
        if (gotoShape.shape === null) {
            return;
        }
        drawShadow(context, gotoShape.x, gotoShape.y, gotoShape.source.width, gotoShape.source.height);
        // if (gotoShape.shape.direction === MIND_ITEM_DIRECTION.RIGHT && gotoShape.x > gotoShape.shape.x + gotoShape.shape.width * 2 / 3) {//right side
        //     gotoShape.takeEffect = () => self.parent = gotoShape.shape.id;
        //     drawShadow(context, gotoShape.shape.x + gotoShape.shape.width + 5, gotoShape.shape.y - 1, self.width, self.height);
        //     return;
        // }
        // if (gotoShape.shape.direction === MIND_ITEM_DIRECTION.LEFT && gotoShape.x < gotoShape.shape.x + gotoShape.shape.width / 3) {//left side
        //     gotoShape.takeEffect = () => self.parent = gotoShape.shape.id;
        //     drawShadow(context, gotoShape.shape.x - self.width - 5, gotoShape.shape.y - 1, self.width, self.height);
        //     return;
        // }
        // if (gotoShape.y < gotoShape.shape.y + gotoShape.shape.height / 2) {//upper
        //     gotoShape.takeEffect = () => {
        //         self.parent = gotoShape.shape.parent;
        //         self.page.moveIndexBefore(self, gotoShape.shape.getIndex());
        //     };
        //     drawShadow(context, gotoShape.shape.x, gotoShape.shape.y - gotoShape.shape.height + 2, self.width, self.height - 6);
        // } else {//below
        //     gotoShape.takeEffect = () => {
        //         self.parent = gotoShape.shape.parent;
        //         self.page.moveIndexBefore(self, gotoShape.shape.getIndex() + 1);
        //     };
        //     drawShadow(context, gotoShape.shape.x, gotoShape.shape.y + self.height + 2, self.width, self.height - 6);
        // }
    };


    const reset = self.reset;
    self.reset = () => {
        reset.call(self);
        self.getContainer().invalidateAlone();
    }

    const draw = self.drawer.draw;
    self.drawer.draw = () => {
        if (self.parent === self.root || self.parent === "") {
            self.borderWidth = 1;
            self.backColor = "whitesmoke";
            self.fontSize = 13;
        } else {
            self.borderWidth = 0;
            self.fontSize = 12;
            self.backColor = "transparent";
        }
        draw.call(self.drawer);
        self.drawer.parent.style.borderBottomWidth = ((self.parent === self.root || self.parent === "") ? undefined : "1px");
    };

    self.expandRegion = itemExpandRegion(self, shape => {
        let container = shape.getContainer();
        // if (contaienr.mode === MIND_MODE.MIND) {
        //     return shape.direction === MIND_ITEM_DIRECTION.RIGHT ? shape.width - 6 : -6;
        // }
        if (container.mode === MIND_MODE.ORG) {
            return self.width / 2 - 6;
        }else{
            return shape.direction === MIND_ITEM_DIRECTION.RIGHT ? shape.width - 6 : -6;
        }
    }, shape => {
        let container = shape.getContainer();
        // if (contaienr.mode === MIND_MODE.MIND) {
        //     return self.height / 2 - 6;
        // }
        if (container.mode === MIND_MODE.ORG) {
            return self.height - 6;
        }else{
            return self.height / 2 - 6;
        }
    });

    //--------------------------serialization & detection------------------------------


    self.addDetection(["x", "y"], (property, value, preValue) => {
        let img = self.page.getShapeById(self.image);
        if (img === null || img === undefined) {
            return;
        }
        img.moveTo(self.x, self.y + self.height);
    });
    //-----------------------------------------------------------------------------------

    return self;
};

export { subTopic };

