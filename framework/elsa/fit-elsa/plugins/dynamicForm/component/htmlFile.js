import {formComponent} from "../form.js";
import {ALIGN, DOCK_MODE} from "../../../common/const.js";
import {image} from "../../../core/image.js";
import {labelContainer} from "./labelContainer.js";
import {imageIcon, pdfIcon, wordIcon} from "../icons/icons.js";
import {DYNAMIC_FORM_EVENT_TYPE} from "../const.js";
import {containerDrawer} from "../../../core/drawers/containerDrawer.js";
import {deleteRegion} from "../regions/deleteRegion.js";

/**
 * 完整的文件组件.
 *
 * @override
 */
const htmlFileInput = (id, x, y, width, height, parent, drawer = undefined) => {
    const getDefaultDrawer = () => {
        return !parent || parent.graph.environment === "wecode" ? weCodeFileDrawer : htmlFileInputDrawer;
    }
    const self = labelContainer(id, x, y, width, height, parent, drawer ? drawer : getDefaultDrawer());
    self.serializedFields.batchAdd("acceptTypes", "uploadData");
    self.type = "htmlFileInput";
    self.autoFit = false;
    self.height = 80;
    self.dockAlign = ALIGN.TOP;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.namespace = "dynamic-form";
    self.acceptTypes = ".doc,.docx,.pdf";
    self.deleteRegion = deleteRegion(self);
    self.meta = [{
        key: self.componentId, type: 'string', name: 'file_' + self.id
    }];

    /**
     * 图片组件获取的数据为图片上传之后返回的url.
     *
     * @override
     */
    self.getData = () => {
        const result = {};
        if (self.uploadData) {
            result[self.meta[0].key] = self.uploadData;
        }
        return result;
    };

    /**
     * 初始化htmlFileInput.
     */
    const initialize = self.initialize;
    self.initialize = () => {
        const label = initialize.apply(self);
        label.visible = false;
    };


    /**
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLabel");
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        self.uploadData = data[self.meta[0].key];
    };

    /**
     * 文件上传成功的回调.
     * 子类应该按需覆写此方法
     *
     * @param data 文件上传后返回的数据.
     */
    self.onSuccess = (data) => {
        console.log("============== welink test: htmlFileUploader#success 2");
        self.uploadData = data;
        self.page.triggerEvent({
            type: DYNAMIC_FORM_EVENT_TYPE.FILE_UPLOAD_SUCCESS, value: self.id
        });
    };

    /**
     * 上传过程中处理.
     * 子类应该按需覆写此方法
     */
    self.onProcess = () => {
    };

    /**
     * 上传.
     * 子类应该按需覆写此方法
     *
     * @param file 文件对象.
     * @param form 表单对象.
     */
    self.doUpload = (file, form) => {
        const headers = new Map();
        headers.set("attachment-filename", encodeURIComponent(file.name));
        const url = form.protocol + "://" + form.domains.jane + "/" + form.tenantId + "/file?aipp_id=" + form.aippId;
        self.page.httpUtil.uploadFile(url, file, self.onProcess, (data) => {
            console.log("============== welink test: htmlFileUploader#success 1");
            self.onSuccess(data.data);
            self.getForm().invalidate();
        }, headers);
    };

    /**
     * 当上传的文件发生变更时触发。
     * 此方法不应被覆写
     *
     * @param file 上传的文件对象.
     */
    self.onFileChange = (file) => {
        console.log("============== welink test: htmlFileUploader#onFileChange");

        // 再次检查
        const fileExt = getFileExt(file.name).toLowerCase();
        const allowedFiles = self.getAllowedFiles();
        if (allowedFiles.length !== 0 && !allowedFiles.includes(fileExt)) {
            self.drawer.showWarning();
            return;
        }

        const form = self.getForm();
        // 禁用上传按钮
        self.page.triggerEvent({
            type: DYNAMIC_FORM_EVENT_TYPE.FORM_DISABLE, value: [self.id]
        });
        self.doUpload(file, form);
    };

    /**
     * modeManager中history模式下重写需要用到.
     *
     * @override
     */
    self.getEnableInteract = () => {
        return true;
    };

    /**
     * 获取图标
     * 子类应该按需覆写此方法
     *
     * @returns {string}
     */
    self.getIcon = () => {
        return `${wordIcon + pdfIcon}`;
    };

    /**
     * 获取允许上传文件扩展名列表。空列表表示无限制
     * 子类应该按需覆写此方法
     *
     * @returns {Array<String>}
     */
    self.getAllowedFiles = () => {
        return ["doc", "docx", "pdf"];
    };

    /**
     * 获取图标相关文字.
     * 子类应该按需覆写此方法
     *
     * @returns {*|string}
     */
    self.getIconText = () => {
        return "上传word、pdf文档";
    };

    /**
     * 获取告警相关文字.
     * 子类应该按需覆写此方法
     *
     * @returns {*|string}
     */
    self.getWarningText = () => {
        return "不支持此类型文件上传";
    }

    const getFileExt = (name) => {
        const i = name.lastIndexOf('.');
        return (i < 1) ? "" : name.substring(i + 1);
    }

    return self;
};

/**
 * 文件上传组件绘制器.
 *
 * @override
 */
const htmlFileInputDrawer = (shape, div, x, y) => {
    const self = containerDrawer(shape, div, x, y);


    self.initializeFileSelect = () => {
        /* 监听input[type=file]的change事件，通知选择的文件发生了变化. */
        self.fileUploader.addEventListener('change', () => {
            const selectedFile = self.fileUploader.files[0];
            self.warningContainer.style.display = "none";
            shape.height = self.getContentHeight();
            selectedFile && shape.onFileChange(selectedFile);
        });
    }

    /**
     * 初始化时，设置acceptTypes，并且修改pointerEvent为可点击.
     *
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        // 组件容器.
        self.fileContainer = self.createElement("div");
        self.fileContainer.className = "aipp-upload-container"
        toggleInteraction();

        // 上传组件.
        self.fileUploader = self.createElement("input");
        self.fileUploader.type = "file";
        self.fileUploader.style.display = "none";
        self.fileUploader.accept = shape.acceptTypes;

        self.fileDisplay = document.createElement("div");
        self.fileDisplay.className = "aipp-upload-display-container";

        const textArea = document.createElement("div");
        textArea.className = "aipp-upload-text-area";

        self.textContainer = document.createElement("div");
        self.textContainer.innerHTML = `${shape.getIcon()}<div>${shape.getIconText()}</div>`;

        self.warningContainer = document.createElement("div");
        self.warningContainer.textContent = `${shape.getWarningText()}`;
        self.warningContainer.className = "aipp-upload-warning";

        // 生成DOM树
        textArea.appendChild(self.textContainer);
        textArea.appendChild(self.warningContainer);
        self.fileDisplay.appendChild(textArea);
        self.fileContainer.appendChild(self.fileDisplay);
        self.parent.appendChild(self.fileContainer);

        // 绑定事件
        self.fileContainer.addEventListener('click', () => {
            self.fileUploader.click();
        });
        self.initializeFileSelect();
        dropUpload();

        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-upload-container {
                color: DIMGRAY;
                width: 100%;
                padding: 10px 0;
                background-color: #fff;
                border: 1px dashed rgb(215, 216, 218);
                border-radius: 4px;
            }
            
            .aipp-upload-display-container {
                display: flex;
                width: 100%;
                height: 100%;
                align-items: center;
                cursor: pointer;
            }
            
            .aipp-upload-text-area {
                width: 100%;
                text-align: center;
                font-size: 16px;
                font-weight: 400;
                line-height: 22px;
            }
            
            .aipp-upload-warning {
                font-size: 16px;
                color: red;
                display: none;
                line-height: 22px;
            }
        `;
        self.parent.appendChild(style);
        shape.height = self.getContentHeight();
    };

    /**
     * @override
     */
    const drawStatic = self.drawStatic;
    self.drawStatic = () => {
        drawStatic.apply(self);

        // 提交表单后，文件上传展示文件名
        if (shape.uploadData) {
            self.textContainer && (self.textContainer.innerHTML = `${shape.getIcon()}<div>${decodeURIComponent(shape.uploadData["file_name"])}</div>`);
        }
        shape.height = self.getContentHeight();
    };

    const toggleInteraction = () => {
        if (shape.getEnableInteract()) {
            self.fileContainer.style.pointerEvents = "auto";
        } else {
            self.fileContainer.style.pointerEvents = "none";
        }
    };

    self.getContentHeight = () => {
        return Math.max(self.fileContainer.offsetHeight, 80);
    }

    self.showWarning = () => {
        self.warningContainer.style.display = "block";
        shape.height = self.getContentHeight();
        shape.getForm().invalidate();
    };

    //拖拽上传
    const dropUpload = () => {
        const preventDefaults = (e) => {
            e.preventDefault();
            e.stopPropagation();
        };

        const handleDrop = (e) => {
            const selectedFile = e.dataTransfer.files[0];
            self.warningContainer.style.display = "none";
            selectedFile && shape.onFileChange(selectedFile);
        };

        // 当文件拖拽到区域上时，改变背景颜色
        const highlight = () => {
            self.fileContainer.style.backgroundColor = '#f0f0f0';
        }

        // 当文件离开区域时，恢复背景颜色
        const unHighlight = () => {
            self.fileContainer.style.backgroundColor = '';
        }

        ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            self.fileContainer.addEventListener(eventName, preventDefaults);
        });

        ['dragenter', 'dragover'].forEach(eventName => {
            self.fileContainer.addEventListener(eventName, highlight);
        });

        ['dragleave', 'drop'].forEach(eventName => {
            self.fileContainer.addEventListener(eventName, unHighlight);
        });

        self.fileContainer.addEventListener('drop', handleDrop);
    };

    return self;
};

/**
 * 完整的pdf文件组件.
 *
 * @override
 */
const htmlPdfInput = (id, x, y, width, height, parent) => {
    const self = htmlFileInput(id, x, y, width, height, parent);
    self.type = "htmlPdfInput";
    self.acceptTypes = ".pdf";

    /**
     * @Override
     */
    self.getIcon = () => {
        return `${pdfIcon}`;
    };

    /**
     * @Override
     */
    self.getIconText = () => {
        return "上传pdf文档";
    };

    /**
     * @Override
     */
    self.getAllowedFiles = () => {
        return ["pdf"];
    };

    return self;
};

/**
 * 完整的pdf文件组件.
 *
 * @override
 */
const htmlDocInput = (id, x, y, width, height, parent) => {
    const self = htmlFileInput(id, x, y, width, height, parent);
    self.type = "htmlDocInput";
    self.acceptTypes = ".doc,.docx";

    /**
     * @Override
     */
    self.getIcon = () => {
        return `${wordIcon}`;
    };

    /**
     * @Override
     */
    self.getIconText = () => {
        return `上传word文档`;
    };

    /**
     * @Override
     */
    self.getAllowedFiles = () => {
        return ["doc", "docx"];
    };

    return self;
};

/**
 * weCode绘制器.
 */
const weCodeFileDrawer = (shape, div, x, y) => {
    console.log("============== welink test: weCodeFileUploaderDrawer");
    const self = htmlFileInputDrawer(shape, div, x, y);

    /**
     * @override
     */
    self.initializeFileSelect = () => {
        self.fileContainer.addEventListener("click", () => {
            console.log("============== welink test: select files 1");
            const supportType = shape.acceptTypes.split(",").map(s => s.replace(".", "")).join(";");
            console.log("============== welink test: supportType: ", supportType);
            HWH5.getLocalFiles({actionName: '确定', supportType: supportType})
                .then(data => {
                    if (!data) {
                        console.log("============== welink test: select files 2");
                        return;
                    }
                    console.log("============== welink test: select files 3 data:", JSON.stringify(data));
                    shape.onFileChange({name: data[0].fileName, path: data[0].filePath});
                })
                .catch(error => {
                    throw new Error("Select file failed: " + error + ".");
                });
        });
    };

    return self;
};

/**
 * 图形上传组件.
 *
 * @override
 */
const htmlImageInput = (id, x, y, width, height, parent) => {
    const getDefaultDrawer = () => {
        return !parent || parent.graph.environment === "wecode" ? weCodeImageDrawer : htmlImageInputDrawer;
    }
    const self = htmlFileInput(id, x, y, width, height, parent, getDefaultDrawer());
    self.type = "htmlImageInput";
    self.componentId = "image_" + self.id;
    self.meta = [{
        key: self.componentId, type: 'string', name: 'image_' + self.id
    }];
    self.acceptTypes = ".jpg,.png";

    /**
     * @override
     */
    self.getData = () => {
        const result = {};
        if (self.uploadData) {
            result[self.meta[0].key] = JSON.stringify(self.uploadData);
        }
        return result;
    };

    /**
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        const uploadData = data[self.meta[0].key];
        if (uploadData) {
            self.uploadData = typeof uploadData === "string" ? JSON.parse(uploadData) : uploadData;
        }
    };

    /**
     * @override
     */
    self.doUpload = (file, form) => {
        const url = form.protocol + "://" + form.domains.elsa + "/common/upload4ProxyDownload";
        self.page.httpUtil.uploadFile(url, file, self.onProcess, (data) => {
            // image在onload之后会调用form的invalidate，因此这里不需要进行form的invalidate操作.
            self.onSuccess(data.data);
        }, new Map());
    };

    /**
     * modeManager中history模式下重写需要用到.
     *
     * @override
     */
    self.getEnableInteract = () => {
        return true;
    };

    /**
     * 成功时的回调.
     *
     * @override
     */
    const onSuccess = self.onSuccess;
    self.onSuccess = (data) => {
        // 展示图片时，不需要展示border.
        self.borderWidth = 0;
        onSuccess.apply(self, [data]);
    };

    /**
     * @override
     */
    self.getIcon = () => {
        return ` ${imageIcon}`;
    };

    /**
     * @override
     */
    self.getIconText = () => {
        if (self.graph.environment === "wecode") {
            return "点击上传图片";
        }
        return "点击上传图片，支持png、jpg";
    };

    /**
     * @override
     */
    self.getWarningText = () => {
        return "不支持此类型图片上传";
    }

    /**
     * 构建url.
     *
     * @return {string} url.
     */
    self.buildUrl = () => {
        const form = self.getForm();
        return form.protocol + "://" + form.domains.elsa + self.uploadData.proxyUrl;
    };

    /**
     * @Override
     */
    self.getAllowedFiles = () => {
        return ["jpg", "png"];
    };

    return self;
};

/**
 * 图片上传组件绘制器.
 *
 * @override
 */
const htmlImageInputDrawer = (shape, div, x, y) => {
    const self = htmlFileInputDrawer(shape, div, x, y);

    /**
     * @override
     */
    const drawStatic = self.drawStatic;
    self.drawStatic = () => {
        drawStatic.apply(self);

        // 上传图片后展示
        if (shape.uploadData) {
            if (self.textContainer) {
                self.textContainer.remove();
                self.textContainer = null;
            }
            if (!self.image) {
                self.image = document.createElement("img");
                self.fileDisplay.appendChild(self.image);
            }

            const url = shape.buildUrl();
            if (decodeURIComponent(self.image.src) === url) {
                return;
            }

            // url的格式为
            self.image.src = url;
            self.image.onload = () => {
                const height = shape.width * (self.image.height / self.image.width);
                self.image.height = height - 5;
                self.image.width = shape.width;
                shape.height = height;
                shape.getForm().invalidate();
            };
        }
    };

    return self;
};

/**
 * weCode图片绘制器.
 */
const weCodeImageDrawer = (shape, div, x, y) => {
    console.log("============== welink test: weCodeImageUploaderDrawer");
    const self = htmlImageInputDrawer(shape, div, x, y);

    /**
     * @override
     */
    self.initializeFileSelect = () => {
        self.fileContainer.addEventListener("click", () => {
            console.log("============== welink test: select images 1");
            HWH5.chooseImage({
                flag: 1,
                imagePickerMode: 'IMAGE',
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
                    console.log("============== welink test: select images 2 data: ", data);
                    shape.onFileChange({path: data[0], name: data[0]});
                })
                .catch((error) => {
                    console.log("============== welink test: select images error: ", error);
                    throw new Error("Visit images failed: " + error + ".");
                });
        });
    };

    return self;
};

/**
 * 图片展示组件.
 *
 * @override
 */
const htmlImageDisplay = (id, x, y, width, height, parent) => {
    const self = labelContainer(id, x, y, width, height, parent);
    self.type = "htmlImageDisplay";
    self.dockAlign = ALIGN.TOP;
    self.dockMode = DOCK_MODE.VERTICAL;
    self.autoFit = true;
    self.serializedFields.add("src");
    self.meta = [{
        key: self.componentId, type: 'string', name: 'image_display_' + self.id
    }];

    /**
     * @override
     */
    self.childAllowed = (child) => {
        return child.isTypeof("htmlLabel") || child.isTypeof("htmlImagePlayer");
    };

    /**
     * 初始化.
     *
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        const label = initialize.apply(self);
        label.text = "图片展示";

        // 创建imageUploader.
        const imagePlayer = self.page.createShape("htmlImagePlayer", x, y);
        imagePlayer.container = self.id;
        imagePlayer.selectable = false;
    };

    /**
     * 接收数据并设置.
     *
     * @override
     */
    self.formDataRetrieved = (shapeStore, data) => {
        const imagePath = data[self.meta[0].key];
        if (imagePath) {
            self.src = imagePath;
        }
    };

    self.addDetection(["src"], (property, value, preValue) => {
        if (!value || value === preValue) {
            return;
        }
        const player = getPlayer();
        player.src = value;
    });

    const getPlayer = () => {
        return self.getShapes().find(s => s.isTypeof("htmlImagePlayer"));
    };

    return self;
};

/**
 * 图片展示器.
 *
 * @override
 */
const htmlImagePlayer = (id, x, y, width, height, parent) => {
    const self = formComponent(image, id, x, y, width, height, parent);
    self.type = "htmlImagePlayer";

    // 使其不为负数即可，容器会自动计算其宽度.
    self.width = 1;
    self.height = 1;

    /**
     * 初始化连接点，不展示连接点，初始化未空数组.
     *
     * @override
     */
    self.initConnectors = () => {
        self.connectors = [];
    };

    /**
     * 图片加载后，自动适配img元素的宽高.
     *
     * @override
     */
    self.onImageLoaded = (img) => {
        const height = self.width * (img.height / img.width);
        self.height = height;
        self.drawer.img.height = height;
        self.drawer.img.width = self.width;
        self.getForm().invalidate();
    };

    return self;
};

export {
    htmlFileInput,
    htmlImageInput,
    htmlImageDisplay,
    htmlImagePlayer,
    htmlFileInputDrawer,
    htmlImageInputDrawer,
    htmlPdfInput,
    htmlDocInput,
};