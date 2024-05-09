import {fileIconFactory} from "../icons/fileIconFactory.js";
import {downloadIcon} from "../icons/icons.js";
import {ALIGN, CURSORS, DOCK_MODE} from "../../../common/const.js";
import {deleteRegion} from "../regions/deleteRegion.js";
import {labelContainer} from "./labelContainer.js";
import {containerDrawer} from "../../../core/drawers/containerDrawer.js";

/**
 * 文件下载器.
 *
 * @override
 */
const htmlFileDownload = (id, x, y, width, height, parent) => {
    const self = labelContainer(id, x, y, width, height, parent, fileDownloadDrawer);
    self.type = "htmlFileDownload";
    self.dockAlign = ALIGN.TOP;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.serializedFields.add("fileData");
    self.autoFit = false;
    self.height = 40;
    self.isWecodeEnv = parent.graph.environment === "wecode";
    self.componentId = "file_download_" + self.id;
    self.meta = [{
        key: self.componentId, type: 'string', name: 'file_download_' + self.id
    }];
    self.cursorStyle = CURSORS.POINTER;
    self.deleteRegion = deleteRegion(self);

    self.childAllowed = (s) => {
        return s.isTypeof("htmlLabel");
    };

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        const label = initialize.apply(self);
        label.text = "文件下载";
    };

    /**
     * fileData格式:
     * [{
     *     fileName: "DME Storage 2.0.0 技术白皮书 01.pdf",
     *     filePic: "", // base64加密的内容，需要解密
     *     fileUrl: "" // base64加密的内容，需要解密
     * }]
     *
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        let fileData = data[self.meta[0].key];
        if (!fileData) {
            return;
        }
        self.fileData = typeof fileData === "string" ? JSON.parse(fileData) : fileData;
    };

    return self;
};

const LABEL_HEIGHT = 48;

/**
 * fileDownload绘制器.
 *
 * @override
 */
const fileDownloadDrawer = (shape, div, x, y) => {
    const self = containerDrawer(shape, div, x, y);

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);

        self.fileDownloadContainer = document.createElement("div");
        self.fileDownloadContainer.className = "aipp-download-container";
        self.parent.appendChild(self.fileDownloadContainer);
    };

    /**
     * @override
     */
    self.drawStatic = () => {
        if (!shape.fileData) {
            return;
        }
        shape.fileData.forEach(fd => {
            const itemContainer = document.createElement("div");
            itemContainer.className = "aipp-download-item-container";

            // 文件图标.
            const fileIcon = document.createElement("div");
            fileIcon.className = "aipp-download-file-icon";
            fileIcon.innerHTML = fileIconFactory.getIconByFileName(fd.fileName);

            // 文件名
            const fileName = document.createElement("div");
            fileName.className = "aipp-download-file-name";
            const nameAnchor= document.createElement("a");
            nameAnchor.textContent = fd.fileName;
            nameAnchor.style.maxWidth = self.parent.offsetWidth * 0.6 + 'px';
            self.configDownload(nameAnchor, fd);
            fileName.appendChild(nameAnchor);

            // 下载图标
            const downloadContainer = document.createElement("div");
            downloadContainer.className = "aipp-download-icon-container";
            const downloadAnchor = document.createElement("a");
            downloadAnchor.innerHTML = downloadIcon;
            self.configDownload(downloadAnchor, fd);
            downloadContainer.appendChild(downloadAnchor);

            [fileIcon, fileName, downloadContainer]
                .forEach(node => itemContainer.appendChild(node));

            // 绑定点击事件，wecode特殊逻辑
            const downloadHandler = () => {
                if (!shape.isWecodeEnv) {
                    return;
                }
                self.downloadFileWecode(fd);
            }
            downloadAnchor.addEventListener("click", downloadHandler);
            nameAnchor.addEventListener("click", downloadHandler);

            self.fileDownloadContainer.appendChild(itemContainer);
            self.fileDownloadContainer.appendChild(document.createElement("br"));
        });


        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-download-file-icon {
                margin: 8px;
                width: 32px;
                height: 32px;
                display: inline-block;
            }
            
            .aipp-download-file-name {
                font-size: 16px;
                font-weight: 400;
                line-height: 48px;
                height: 48px;
                display: inline-block;
            }
            
            .aipp-download-file-name a {
                text-decoration: none;
                display: inline-block;
                overflow: hidden;
                white-space: nowrap;
                color: unset; 
                text-overflow: ellipsis;
            }
            
            .aipp-download-file-name-prefix {
                text-overflow: ellipsis;
            }
            
            .aipp-download-icon-container {
                margin: 14px;
                display: inline-block;
            }
            
            .aipp-download-container {
                position: absolute;
                top: 42px;
                width: 100%;
                left: 0;
                pointer-events: auto;
            }
            
            .aipp-download-item-container {
                height: 48px;
                margin-bottom: 8px;
                align-items: center;
                border-radius: 4px;
                display: inline-flex;
                border: 1px solid rgb(215, 216, 218);
                background-color: rgb(255, 255, 255);
                color: rgb(37, 43, 58);
            }
        `;
        self.parent.appendChild(style);
        shape.height = self.getContentHeight();
    };

    /**
     * 配置PC下载链接
     */
    self.configDownload = (element, data) => {
        const url = getDownloadUrl(data);
        console.log("============== welink test: url:", url);
        element.href = url;
        element.download = url;
    }

    self.getContentHeight = () => {
        return self.fileDownloadContainer.offsetHeight + LABEL_HEIGHT;
    }

    self.downloadFileWecode = (data) => {
        const url = getDownloadUrl(data);
        console.log("============== welink test: download file 2:", url);

        HWH5.downloadFileAndEncrypt({
            url: url,
            headers: {},
            progress: 1,
            filePath: data.fileName,
            onProgress: (_data) => {
                console.log("下载中:", _data);
            }
        }).then((result) => {
            console.log("下载成功:", result);
            HWH5.showToast({ msg: '下载成功，文件路径为:' + result.filePath, type: 'w'});
            HWH5.openFile({filePath: result.filePath})
                .then((data) => {
                    console.log("打开文件成功:", data);
                })
                .catch((error) => {
                    console.log('打开文档失败', error)
                });
        }).catch((error) => {
            console.log("下载异常", error)
        });
    };

    const getDownloadUrl = data => {
        const form = shape.getForm();
        return form.protocol + "://" + form.domains.jane + "/" + form.tenantId + data.fileUrl;
    }

    return self;
};


export {htmlFileDownload};