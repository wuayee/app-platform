import {audioIcon} from "../icons/icons.js";
import {htmlVideoInput, htmlVideoUploaderDrawer} from "./htmlVideo.js";

/**
 * 视频组件.
 *
 * @override
 */
const htmlAudioInput = (id, x, y, width, height, parent, drawer) => {
    const getDrawer = () => parent.graph.environment === "wecode" ? weCodeAudioUploaderDrawer: htmlAudioUploaderDrawer;
    const self = htmlVideoInput(id, x, y, width, height, parent, drawer ? drawer : getDrawer());
    self.type = "htmlAudioInput";
    self.componentId = "audio_" + self.id;
    self.acceptTypes = "audio/*";
    self.status = "init";

    self.meta = [{
        key: self.componentId, type: 'string', name: 'audio_' + self.id
    }];

    /**
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLabel");
    };

    /**
     * @Override
     */
    self.getAllowedFiles = () => {
        return ["opus", "flac", "webm", "weba", "wav", "ogg", "m4a", "mp3", "oga", "mid", "amr", "aiff", "wma", "au", "acc"];
    };

    /**
     * @override
     */
    self.getIcon = () => {
        return `${audioIcon}`;
    };

    /**
     * @override
     */
    self.getIconText = () => {
        return "上传音频";
    };

    return self;
};

/**
 * 视频上传组件绘制器.
 *
 * @override
 */
const htmlAudioUploaderDrawer = (shape, div, x, y) => {
    const self = htmlVideoUploaderDrawer(shape, div, x, y);

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);

        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-audio-upload-name {
                font-weight: 600;
                font-size: 16px;
                word-break: break-word;
                margin-top: 10px;
            }
        `;
        self.parent.appendChild(style);
    };
    /**
     * 展示音频
     *
     * @override
     */
    self.display = () => {
        if (!self.audio) {
            self.parent.innerHTML = "";
            self.nameDom = document.createElement("div");
            self.nameDom.className = "aipp-video-upload-name";
            self.fileContainer.appendChild(self.nameDom);

            const audioContainer = document.createElement("div");
            audioContainer.style.marginTop = "10px";
            self.parent.appendChild(audioContainer);

            // 创建播放器dom.
            self.audio = document.createElement("audio");
            self.audio.setAttribute("controls", "true");
            self.audio.style.pointerEvents = "auto";
            self.audio.style.width = shape.width + "px";
            self.audio.style.height = "60px";
            audioContainer.appendChild(self.audio);

            self.nameDom.textContent = shape.fileName;
        }
        const form = shape.getForm();
        self.audio.src = form.protocol + "://" + form.domains.elsa + shape.uploadData["proxy_url"];
    };

    return self;
};

/**
 * weCode视频上传绘制器.
 *
 * @override
 */
const weCodeAudioUploaderDrawer = (shape, div, x, y) => {
    console.log("============== welink test: weCodeAudioUploaderDrawer");
    const self = htmlAudioUploaderDrawer(shape, div, x, y);

    /**
     * @override
     */
    self.initializeFileSelect = () => {
        self.fileContainer.addEventListener("click", () => {
            console.log("============== welink test: select audio 1");
            HWH5.getLocalFiles({
                actionName: '确定', supportType: 'mp3;m4a;'
            }).then((data) => {
                if (!data) {
                    console.log("============== welink test: select audio 2");
                    return;
                }
                const selectResult = {name: data[0].fileName, path: data[0].filePath};
                console.log("============== welink test: select audio 3: ", JSON.stringify(selectResult));
                shape.onFileChange(selectResult);
            }).catch((error) => {
                console.log('获取失败', error)
            });
        });
    };

    return self;
};

export {htmlAudioInput};