import {MIND_ITEM_STATUS, ATTACHED_THEME} from '../../common/const.js';
import {hitRegion} from '../../core/hitRegion.js';

const WIDTH = 12;
/**
 * xmind里某个节点的展开/收缩子项小图标
 * 辉子 2021
 */
let itemExpandRegion = (item, getx, gety, index) => {
    let self = hitRegion(item, getx, gety, () => WIDTH, () => WIDTH, index);
    self.type = "expand";
    self.drawStatic = (context, x, y) => {
        if(item.getChildren().length===0) return;
        context.beginPath();
        context.strokeStyle = item.getBorderColor();
        context.arc(x + 12, y + 12, 10, 0, 2 * Math.PI);
        context.fillStyle = "white";
        context.fill();
        context.moveTo(x + 5, y + 12);
        context.lineTo(x + 20, y + 12);
        context.stroke();
        if (item.status === MIND_ITEM_STATUS.COLLAPSED) {
            context.beginPath();
            context.moveTo(x + 12, y + 5);
            context.lineTo(x + 12, y + 20);
            context.stroke();
        }
    };
    self.drawDynamic = (context, x, y) => {
    };
    self.click = () => {
        if (item.status === MIND_ITEM_STATUS.EXPANDED) {
            console.log("collapsing....");
            item.status = MIND_ITEM_STATUS.COLLAPSED;
        } else {
            console.log("expanding....");
            item.status = MIND_ITEM_STATUS.EXPANDED;
        }
        // item.getContainer().invalidate();
        item.getRoot().place();
        item.getContainer().invalidate();
    };
    return self;
};

/**
 * xmind里某个节点的comment图标
 * 有comments时展现，没有时消失
 * 点击comment图标显示该节点的所有comments
 * 辉子 2021
 */
// let commentRegion = (item, getx, gety, getWidth, getHeight, index) => {
//     let self = hitRegion(item, getx, gety, getWidth, getHeight, index);
//     self.type = "mind-comment";
//     self.click = () => {
//         let parent = item.getContainer();
//         parent.attached.theme = ATTACHED_THEME.COMMENTS;
//         parent.attached.topic = item;
//     };

//     self.drawStatic = (context, x, y) => {
//         context.fillStyle = "white";
//         context.strokeStyle = "green";
//         //context.fillRect(x, y, self.width, self.height);
//         context.lineWidth = 1;

//         context.beginPath();
//         context.rect(6, 2, 17, 12);
//         context.fill();
//         context.stroke();

//         context.beginPath();
//         context.rect(1, 7, 17, 12);
//         context.fill();
//         context.stroke();

//         context.fillStyle = "green";
//         context.font = "normal bold 12px Arial";
//         context.fillText("...", 5, 14);

//         context.beginPath();
//         let x1 = 5, y1 = 19;
//         context.moveTo(x1, y1);
//         context.lineTo(x1 - 3, y1 + 3);
//         context.lineTo(x1 + 6, y1);
//         context.closePath();
//         context.fill();
//     };
//     return self;
// };

/**
 * xmind里某个节点的附件图标
 * 有附件时展现，没有时消失
 * 点击附件图标显示该节点关联的附件列表，点击某个附件打开该附件或者进入该附件网址
 * 辉子 2021
 */
let attachedRegion = (item, getx, gety, getWidth, getHeight, index) => {
    let self = hitRegion(item, getx, gety, getWidth, getHeight, index);
    self.type = "attached_files";
    self.width = 36;
    self.height = 36;
    self.getVisibility = () => item.attached !== undefined && item.attached.length > 0 && self.visible && item.getVisibility();
    self.click = () => {
        item.showAttached();
        // let parent = item.getContainer();
        // parent.attached.theme = ATTACHED_THEME.ATTACHED;
        // parent.attached.topic = item;
    };
    self.drawStatic = (context, x, y) => {
        context.save();
        context.strokeStyle = "white";
        context.fillStyle = "green";
        //context.fillRect(x, y, self.width, self.height);
        context.translate(x + self.width / 2, y + self.height / 2);
        context.rotate(40 * Math.PI / 180);

        context.roundRect(x - 16, y - 6, 14, 9, 5, "white", "green", 2);
        context.roundRect(x + 1, y - 6, 14, 9, 5, "white", "green", 2);
        context.beginPath();
        context.rect(x - 5, y - 3, 10, 4);
        context.lineWidth = 1;
        context.fill();
        context.stroke();
        context.restore();

    };

    return self;
};

export {itemExpandRegion, attachedRegion};
