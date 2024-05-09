import { rectangle } from './rectangle.js';
import { rectangleDrawer } from './drawers/rectangleDrawer.js';
import { hitRegion } from './hitRegion.js';

function createVideoElementId(shapeId) {
    return "video_player:" + shapeId;
}

/**
 * 视频播放器
 * 辉子 2021
 */
const video = (id, x, y, width, height, parent, drawer) => {
    let self = rectangle(id, x, y, width, height, parent, drawer === undefined ? videoDrawer : drawer);
    self.type = "video";
    self.width = self.height = 300;
    self.backColor = "RGBA(0,0,0,0)";
    self.src = undefined;

    self.isPlaying = () => !self.drawer.video.paused;
    self.playControl = playRegion(self, (shape, region) => (shape.width - region.width) / 2, (shape, region) => (shape.height - region.height) / 2)

    // self.serializedFields.batchAdd("src");
    self.addDetection(["src"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        self.drawer.video.src = value;
    });
    self.addDetection(["id"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        const video = document.getElementById(createVideoElementId(preValue));
        if (video) {
            self.drawer.parent.removeChild(video);
        }
    });
    return self;
};

const videoDrawer = (shape, div, x, y) => {
    let self = rectangleDrawer(shape, div, x, y);
    self.type = "video drawer";

    self.textResize = (width, height) => {
        self.text.remove();
    };

    const parentResize = self.parentResize;
    self.parentResize = (width, height) => {
        const VIDEO_PLAYER = createVideoElementId(shape.id);
        parentResize.call(self, width, height);
        // self.video = document.getElementById(VIDEO_PLAYER);
        // if (!self.video) {
        //     self.video = document.createElement("video");
        //     self.video.src = shape.src;
        //     self.parent.appendChild(self.video);
        // }
        // self.video.id = VIDEO_PLAYER;
        // const clientWidth = self.parent.clientWidth;
        // const clientHeight = self.parent.clientHeight;
        // self.video.style.width = clientWidth + "px";
        // self.video.style.height = clientHeight + "px";


        self.video = self.createElement("video", VIDEO_PLAYER);
        if (self.video.parentNode !== self.parent) {
            self.parent.appendChild(self.video);
            self.video.src = shape.src;
        }
        self.video.style.width = width + "px";
        self.video.style.height = height + "px";
    };

    return self;
};

const playRegion = (shape, getx, gety, index) => {
    let self = hitRegion(shape, getx, gety, index);
    self.type = "videoPlay";
    self.text = "";
    self.width = self.height = 48;
    self.drawStatic = (context, x, y) => {
        context.beginPath();
        context.strokeStyle = "gray";
        context.lineWidth = 3;
        context.arc(self.width / 2, self.height / 2, 20, 0, 2 * Math.PI);
        context.stroke();
        if (shape.drawer.video.paused) {
            const x = 15, y = 12, r = 25;
            context.beginPath();
            context.moveTo(x, y);
            context.lineTo(x + r, y + r / 2);
            context.lineTo(x, y + r);
            context.closePath();
            context.fillStyle = "rgba(233,233,233,0.3)";
            context.fill();
            context.stroke();
        } else {
            const x = 17, y = 12, r = 25;
            context.lineWidth = 3;
            context.beginPath();
            context.moveTo(x, y);
            context.lineTo(x, y + r);
            context.stroke();

            context.beginPath();
            context.moveTo(x + 13, y);
            context.lineTo(x + 13, y + r);
            context.stroke();
        }
    };

    self.getVisibility = () => shape.page.mouseInShape === shape;

    self.click = () => {
        if (shape.drawer.video.paused) {
            shape.drawer.video.play();
        } else {
            shape.drawer.video.pause();
        }
        shape.render();
    }
    return self;
};

export { video };