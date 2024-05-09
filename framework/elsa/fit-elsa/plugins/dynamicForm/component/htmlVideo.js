import {htmlFileInput, htmlFileInputDrawer} from "./htmlFile.js";
import videojs from "video.js";
import {formComponent} from "../form.js";
import {rectangle} from "../../../core/rectangle.js";
import {rectangleDrawer} from "../../../core/drawers/rectangleDrawer.js";
import {videoIcon} from "../icons/icons.js";

// 视频播放时的各种dom参数.
const PLAY_VIDEO_HEIGHT = 380;

/**
 * 视频组件.
 *
 * @override
 */
const htmlVideoInput = (id, x, y, width, height, parent, drawer) => {
    const getDrawer = () => {
        return parent.graph.environment === "wecode" ? weCodeVideoUploaderDrawer: htmlVideoUploaderDrawer;
    };
    const self = htmlFileInput(id, x, y, width, height, parent, drawer ? drawer : getDrawer());
    self.serializedFields.add("uploadData");
    self.type = "htmlVideoInput";
    self.componentId = "video_" + self.id;
    self.acceptTypes = "video/*";
    self.status = "init";
    self.meta = [{
        key: self.componentId, type: 'string', name: 'video_' + self.id
    }];

    /**
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLabel") || child.isTypeof("htmlFileInput");
    };

    /**
     * @override
     */
    self.getData = () => {
        const result = {};
        if (self.uploadData) {
            result[self.meta[0].key] = JSON.stringify({
                "video_name": self.uploadData["video_name"],
                "s3_url": self.uploadData["s3_url"],
                "proxy_url": self.uploadData["proxy_url"]
            });
        }
        return result;
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        const uploadData = data[self.meta[0].key];
        if (uploadData) {
            const data = typeof uploadData === "string" ? JSON.parse(uploadData) : uploadData;
            self.onSuccess(data);
        }
    };

    /**
     * 获取播放组件的高度.
     *
     * @return {number} 高度.
     */
    self.getDisplayComponentHeight = () => {
        return PLAY_VIDEO_HEIGHT;
    };

    /**
     * @override
     */
    self.doUpload = (file, form) => {
        const url = form.protocol + "://" + form.domains.elsa + "/common/upload4ProxyDownload";
        self.page.httpUtil.uploadFile(url, file, self.onProcess, (data) => {
            console.log("============== welink test: uploadedData: ", JSON.stringify(data));
            self.onSuccess({video_name: file.name, proxy_url: data.data.proxyUrl, s3_url: data.data.s3Url});
        }, new Map()).then(abortable => self.abortable = abortable);

        self.status = "uploading";
        self.fileName = file.name;
        self.percent = 0;
        self.height = self.drawer.getChildrenTotalHeight();
        self.getForm().invalidate();
    };

    /**
     * @override
     */
    const onSuccess = self.onSuccess;
    self.onSuccess = (data) => {
        // 如果在成功之前就已经被abort了，那么返回，不执行成功操作.
        if (self.status === "abort") {
            return;
        }

        onSuccess.apply(self, [data]);
        self.status = "success";

        // 展示视频时时，不需要展示border.
        self.borderWidth = 0;
        self.fileName = data["video_name"];
        self.drawer.drawStatic();
        self.height = self.drawer.getChildrenTotalHeight();
    };

    /**
     * @override
     */
    self.onProcess = (data) => {
        self.percent = data;
        self.drawer.displayProgress();
    };

    /**
     * @override
     */
    self.getIcon = () => {
        return `${videoIcon}`;
    };

    /**
     * @override
     */
    self.getIconText = () => {
        return "上传视频";
    };

    /**
     * 回滚到上传之前.
     */
    self.rollBackToBeforeUpload = () => {
        self.fileName = null;
        self.percent = null;
        self.height = self.drawer.getChildrenTotalHeight();
        self.getForm().invalidate();
    };

    /**
     * form完成加载时，需要再render一次，否则视频可能错乱（在submitted的场景下）
     *
     * @override
     */
    self.formLoaded = () => {
        self.getForm().invalidate();
    };

    /**
     * @Override
     */
    self.getAllowedFiles = () => {
        return ["ogm", "wmv", "mpg", "webm", "ogv", "mov", "asx", "mpeg", "mp4", "m4v", "avi"];
    };

    return self;
};


/**
 * 视频上传组件绘制器.
 *
 * @override
 */
const htmlVideoUploaderDrawer = (shape, div, x, y) => {
    const self = htmlFileInputDrawer(shape, div, x, y);
    self.prevVideoUrl = "";

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.fileContainer.style.height = "";
        self.fileDisplay.style.display = "block";
        self.fileDisplay.style.height = "";
        self.textContainer.style.marginTop = "12px";
        self.textContainer.style.fontSize = "14px";

        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-video-upload-name {
                font-weight: 600;
                font-size: 16px;
                word-break: break-word;
            }
            
            .aipp-video-upload-progress {
                display: flex;
                align-items: center;
                justify-content: center;
                height: 28px;
                margin-top: 4px;
                gap: 8px;
            }
            
            .aipp-video-upload-cancel {
                color: #71757F;
                font-size: 14px;
                font-weight: 400;
                height: 20px;
                text-align: center;
                margin-top: 4px;
                pointer-events: auto;
            }
        `;
        self.parent.appendChild(style);
    };

    /**
     * 播放时暂时名称.
     */
    self.getChildrenTotalHeight = () => {
        let totalHeight = 0;
        for (const child of self.parent.children) {
            totalHeight += child.offsetHeight;
        }
        return totalHeight;
    };

    /**
     * 展示视频
     *
     * @override
     */
    self.display = () => {
        if (!self.player) {
            self.parent.innerHTML = "";
            self.nameDom = document.createElement("div");
            self.nameDom.className = "aipp-video-upload-name"
            self.fileContainer.appendChild(self.nameDom);

            // 创建video容器，margin等样式需要在该container上设置，否则会导致video渲染完成之后错位.
            const videoContainer = document.createElement("div");
            videoContainer.style.marginTop = "10px";
            self.parent.appendChild(videoContainer);

            // 创建播放器dom.
            const video = document.createElement("video");
            video.classList.add("video-js");
            video.style.pointerEvents = "auto";
            videoContainer.appendChild(video);

            // 创建播放器.
            self.player = videojs(video, {
                width: shape.width + "",
                height: shape.getDisplayComponentHeight() + "",
                controls: true,
                preload: "auto",
                autoplay: false
            });
            self.player.on("ready", () => {
                shape.height = self.getChildrenTotalHeight();
                shape.getForm().invalidate();
            });
        }
        self.nameDom.textContent = shape.fileName;
        const form = shape.getForm();
        const videoUrl = form.protocol + "://" + form.domains.elsa + shape.uploadData["proxy_url"];
        console.log("============== welink test: videoUrl:", videoUrl);
        if (videoUrl !== self.prevVideoUrl) {
            self.player.src({src: videoUrl});
            self.prevVideoUrl = videoUrl;
        }
    };

    /**
     * 展示名字.
     */
    self.showName = () => {
        self.textContainer.innerHTML = `${shape.getIcon()}<div>${shape.fileName}</div>`;
    };

    /**
     * 展示百分比.
     */
    self.displayProgress = () => {
        if (shape.percent === null || shape.percent === undefined) {
            return;
        }

        if (!self.progressDom) {
            self.progressDom = document.createElement("div");
            self.progressDom.className = "aipp-video-upload-progress";
            self.fileDisplay.appendChild(self.progressDom);
        }
        self.progressDom.innerHTML = `<div style="font-weight: 400;font-size: 20px">上传中</div>
                        <div style="font-weight: 400;font-size: 20px;color: #047BFC">${shape.percent}%</div>`;
    };

    /**
     * 展示取消元素.
     */
    self.displayCancel = () => {
        if (self.cancelDom) {
            return;
        }
        self.cancelDom = document.createElement("div");
        self.cancelDom.className = "aipp-video-upload-cancel";
        self.cancelDom.textContent = "取消上传";
        self.cancelDom.addEventListener("click", (e) => {
            e.stopPropagation();
            e.preventDefault();

            shape.status = "abort";
            shape.abortable && shape.abortable.abort();

            // 清除input的值，否则无法再次上传同一个文件
            self.fileUploader.value = "";
            self.fileContainer.style.pointerEvents = "auto";
            shape.rollBackToBeforeUpload();
        });
        self.fileDisplay.appendChild(self.cancelDom);
    };

    /**
     * 清理数据.
     */
    self.abort = () => {
        self.cancelDom && self.cancelDom.remove();
        self.progressDom && self.progressDom.remove();
        self.cancelDom = self.progressDom = null;
        self.textContainer.innerHTML = `${shape.getIcon()}<div>${shape.getIconText()}</div>`;
    };

    /**
     * 上传中.
     */
    self.uploading = () => {
        self.showName();
        self.displayProgress();
        self.displayCancel();
        self.fileContainer.style.pointerEvents = "none";
    };

    /**
     * @override
     */
    self.drawStatic = () => {
        if (!shape.getForm().loaded) {
            return;
        }
        switch (shape.status) {
            case "success":
                self.display();
                break;
            case "uploading":
                self.uploading();
                break;
            case "abort":
                self.abort();
                break;
            case "init":
                shape.uploadData && shape.onSuccess(shape.uploadData);
                break
            default:
                break;
        }
    };

    /*
     * @Override
     */
    self.getContentHeight = () => {
        return Math.max(self.fileContainer.offsetHeight, 94);
    }

    return self;
};

/**
 * weCode视频上传绘制器.
 *
 * @override
 */
const weCodeVideoUploaderDrawer = (shape, div, x, y) => {
    const self = htmlVideoUploaderDrawer(shape, div, x, y);

    /**
     * @override
     */
    self.initializeFileSelect = () => {
        self.fileContainer.addEventListener("click", () => {
            console.log("============== welink test: select videos 1");
            HWH5.chooseImage({
                flag: 1,
                imagePickerMode: 'VIDEO',
                maxSelectedCount: 1,
                btntxtEN: 'Done',
                btntxtCN: '完成',
                cameraFacing: 0,
                compress: '1',
                type: 0
            })
                .then((data) => {
                    if (!data || !Array.isArray(data) || (Array.isArray(data) && data.length === 0)) {
                        return;
                    }
                    console.log("============== welink test: select videos 2 data: ", data);
                    const url = data[0];
                    const urls = url.split("/");
                    const name = urls[urls.length - 1];
                    shape.onFileChange({name, path: url});
                })
                .catch((error) => {
                    throw new Error("Visit images failed: " + error + ".");
                });
        });
    };

    return self;
};

const videoPlayer = (id, x, y, width, height, parent) => {
    const self = formComponent(rectangle, id, x, y, width, height, parent, videoPlayerDrawer);
    self.type = "videoPlayer";
    self.hideText = true;
    self.height = 200;

    return self;
};

/**
 * @override
 */
const videoPlayerDrawer = (shape, div, x, y) => {
    const self = rectangleDrawer(shape, div, x, y);
    self.type = "videoPlayerDrawer";

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.video = document.createElement("video");
        self.video.id = "video_" + shape.id;
        self.video.classList.add("video-js");
        self.parent.appendChild(self.video);
    };

    /**
     * 绘制radio.
     *
     * @override
     */
    const drawStatic = self.drawStatic;
    self.drawStatic = (x, y) => {
        drawStatic.apply(self, [x, y]);
        if (!shape.url) {
            return;
        }
        self.player = videojs(self.video.id, {
            width: "300", height: "200", controls: true, preload: "auto", autoplay: false
        });
        self.player.src({src: shape.url});
        self.player.on("ready", () => {
            console.log("播放器已加载.");
        });
    };

    return self;
};

// 添加videoJs的样式表.
(() => {
    const link = document.createElement('link');
    link.href = "https://vjs.zencdn.net/8.9.0/video-js.css";
    link.rel = "stylesheet";
    document.head.appendChild(link);
})();

export {htmlVideoInput, htmlVideoUploaderDrawer, videoPlayer};