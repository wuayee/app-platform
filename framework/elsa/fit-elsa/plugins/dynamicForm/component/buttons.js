import {htmlDiv} from "../form.js";
import {ALIGN, DOCK_MODE} from "../../../common/const.js";
import {deleteRegion} from "../regions/deleteRegion.js";
import {DYNAMIC_FORM_EVENT_TYPE} from "../const.js";
import {containerDrawer} from "../../../core/drawers/containerDrawer.js";

/**
 * 按钮.
 *
 * @override
 */
const htmlButton = (id, x, y, width, height, parent, drawer) => {
    const self = htmlDiv(id, x, y, width, height, parent, drawer ? drawer : buttonDrawer);
    self.type = "htmlButton";
    self.hAlign = ALIGN.MIDDLE;
    self.dockMode = DOCK_MODE.NONE;
    self.height = 36;
    self.deleteRegion = deleteRegion(self);
    self.disableKeys = new Set();
    self.text = "按钮";
    self.disabled = false;

    /**
     * 只有formButton触发的点击事件才需要响应.
     *
     * @override
     */
    const click = self.click;
    self.click = (x, y, targets = []) => {
        if (targets.some(t => t === self.drawer.buttonContainer)) {
            click.apply(self);
        }
    };

    /**
     * @override
     */
    self.formClicked = () => {
        const form = self.getForm();
        form.onSubmitCallback && form.onSubmitCallback();
    };

    /**
     * modeManager中history模式下重写需要用到.
     *
     * @returns {*|boolean} true/false.
     */
    const getVisibility = self.getVisibility;
    self.getVisibility = () => {
        return getVisibility.apply(self);
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
     * 使按钮不能点击.
     */
    self.disable = (disableKeys) => {
        disableKeys.forEach(dk => {
            !self.disableKeys.has(dk) && self.disableKeys.add(dk);
        });
        if (self.disableKeys.size === 0) {
            return;
        }
        self.globalAlpha = 0.5;
        self.page.shapeClickAble = false;
        self.disabled = true;
        self.getForm().invalidate()
    };

    /**
     * 激活按钮.
     */
    self.active = () => {
        if (self.disableKeys.size > 0) {
            return;
        }
        self.globalAlpha = 1;
        self.page.shapeClickAble = true;
        self.disabled = false;
        self.getForm().invalidate();
    };

    /*
     * 子类可按需覆写此方法
     */
    self.getIcon = () => {
        return `
            <svg width="14.000000" height="14.000000" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
            \t<desc>
            \t\t\tCreated with Pixso.
            \t</desc>
            \t<defs/>
            \t<path id="形状结合" d="M7 0C10.866 0 14 3.13403 14 7C14 10.866 10.866 14 7 14C3.13403 14 0 10.866 0 7C0 3.13403 3.13403 0 7 0ZM7 1C3.68628 1 1 3.68628 1 7C1 10.3137 3.68628 13 7 13C10.3137 13 13 10.3137 13 7C13 3.68628 10.3137 1 7 1ZM5 4.69995C5 4.31543 5.41602 4.07495 5.74927 4.2666L9.74927 6.56665C10.0835 6.75879 10.0835 7.24121 9.74927 7.43335L5.74927 9.7334C5.41602 9.92505 5 9.68457 5 9.30005L5 4.69995ZM6 5.56396L6 8.43506L8.49609 6.99902L6 5.56396Z" fill-rule="nonzero" fill="#047BFC"/>
            </svg>
        `;
    };

    /**
     * @override
     */
    self.formLoaded = () => {
        // 只有文件上传相关组件，并且uploadData不存在，才会导致form触发disable.
        self.disable(self.getForm().getShapes().filter(s => s.isTypeof("htmlFileInput") && !s.uploadData).map(s => s.id));
        self.addPageEventListener(DYNAMIC_FORM_EVENT_TYPE.FILE_UPLOAD_SUCCESS, (shapeId) => {
            self.disableKeys.delete(shapeId);
            self.active();
        });
        self.addPageEventListener(DYNAMIC_FORM_EVENT_TYPE.FORM_DISABLE, (disableKeys) => {
            self.disable(disableKeys);
        });
    };

    return self;
};

/**
 * button绘制器
 *
 * @override
 */
const buttonDrawer = (shape, div, x, y) => {
    const self = containerDrawer(shape, div, x, y);
    self.type = "buttonDrawer";
    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.buttonContainer = document.createElement("div");
        self.buttonContainer.className = "aipp-form-button-container";


        const icon = document.createElement("div");
        icon.className = "aipp-form-button-icon";
        icon.innerHTML = shape.getIcon();

        const textContainer = document.createElement("div");
        textContainer.textContent = shape.text;
        textContainer.contentEditable = shape.getEnableInteract().toString();
        textContainer.style.outline = "none";

        // 绑定事件
        self.buttonContainer.addEventListener("click", () => shape.click(shape.x, shape.y, [self.buttonContainer]));
        textContainer.addEventListener("input", () => shape.text = textContainer.textContent, false);


        self.buttonContainer.appendChild(icon);
        self.buttonContainer.appendChild(textContainer);
        self.parent.appendChild(self.buttonContainer);

        // CSS格式化
        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-form-button-container {
                height: 32px;
                border-radius: 15px;
                background-color: #FFFFFF;
                border-color: #FFFFFF;
                color: #047BFC;
                font-size: 14px;
                text-align: center;
                display: flex;
                justify-content: center;
                align-items: center;
                pointer-events: auto;
            }
            
            .aipp-form-button-container:hover {
                cursor: pointer;
            }
        
            .aipp-form-button-icon {
                padding: 5px 5px 0px 0px;
                display: inline-block;
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
        self.buttonContainer.style.pointerEvents = shape.disabled ? "none" : "auto";
    };

    return self;
};

/**
 * 启动按钮.
 *
 * @override
 */
const startButton = (id, x, y, width, height, parent) => {
    const self = htmlButton(id, x, y, width, height, parent);
    self.type = "startButton";
    self.text = "启动";

    /**
     * @override
     */
    const formClicked = self.formClicked;
    self.formClicked = () => {
        formClicked.apply(self);
        const form = self.getForm();
        const url = self.getUrl();
        const data = form.getData();
        data["aipp_instance_name"] = form.name;

        // 发起创建aipp实例的请求.
        self.page.httpUtil.post(self.getUrl(), {initContext: data}, (data) => {
            if (data.code !== 0) {
                throw new Error("request [" + url + "] is wrong.");
            }

            // 创建成功，触发回调.
            const instanceId = data.data;
            const form = self.getForm();
            form.submitted(instanceId);
        });
    };

    /**
     * 获取url.
     *
     * @returns {string}
     */
    self.getUrl = () => {
        const form = self.getForm();
        return form.protocol + "://" + form.domains.jane + "/" + form.tenantId + "/aipp/" + form.aippId + "?version=" + form.aippVersion;
    };

    return self;
};

/**
 * 会话启动按钮.
 *
 * @override
 */
const sessionStartButton = (id, x, y, width, height, parent) => {
    const self = startButton(id, x, y, width, height, parent);
    self.type = "sessionStartButton";
    self.hAlign = ALIGN.LEFT;
    self.autoWidth = true;
    self.text = "会话启动";

    return self;
};

/**
 * 确认、下一步按钮.
 *
 * @override
 */
const confirmButton = (id, x, y, width, height, parent) => {
    const self = htmlButton(id, x, y, width, height, parent);
    self.type = "confirmButton";
    self.text = "确认";

    /**
     * @override
     */
    const formClicked = self.formClicked;
    self.formClicked = () => {
        formClicked.apply(self);
        const form = self.getForm();
        const url = self.getUrl();

        const data = form.getData();
        const args = self.beforeSubmit(data);

        // 驱动实例流转.
        self.page.httpUtil.put(url, {businessData: args}, (data) => {
            if (data.code !== 0) {
                throw new Error("request [" + url + "] is wrong.");
            }
            form.submitted();
        });
    };

    /**
     * 提交前的钩子函数，让用户能重写并修改数据.
     *
     * @param data 待修改的数据.
     */
    self.beforeSubmit = (data) => {
        return data;
    };

    self.getUrl = () => {
        const form = self.getForm();
        return form.protocol + "://" + form.domains.jane + "/" + form.tenantId + "/aipp/" + form.aippId + "/instances/" + form.aippInstanceId + "?version=" + form.aippVersion;
    };

    return self;
};

/**
 * 会话继续按钮
 *
 * @override
 */
const sessionContinueButton = (id, x, y, width, height, parent) => {
    const self = confirmButton(id, x, y, width, height, parent);
    self.type = "sessionContinueButton";
    self.hAlign = ALIGN.LEFT;
    self.autoWidth = true;
    self.text = "会话继续";

    return self;
};

/**
 * 报告保存按钮.
 *
 * @override
 */
const reportSaveButton = (id, x, y, width, height, parent) => {
    const self = htmlButton(id, x, y, width, height, parent, reportSaveButtonDrawer);
    self.type = "reportSaveButton";
    self.text = "保存";

    /**
     * @override
     */
    const formClicked = self.formClicked;
    self.formClicked = () => {
        formClicked.apply(self);
        const form = self.getForm();
        const report = form.getShapes().find(s => s.type === "htmlReport");
        if (!report) {
            return;
        }
        const url = getUrl();
        const reportData = report.getData();

        // 驱动实例流转.
        self.page.httpUtil.put(url, {businessData: reportData}, (data) => {
            if (data.code !== 0) {
                throw new Error("request [" + url + "] is wrong.");
            }

            // 保存完成之后，触发报告只读事件.
            self.page.triggerEvent({type: "report_readonly", value: {page: self.page.id}});
            form.submittedCallback && form.submittedCallback(form.graph.serialize());
        });
    };

    /**
     * @override
     */
    self.disable = () => {
        if (!self.getVisibility()) {
            return;
        }
        self.drawer.buttonContainer.style.opacity = "0.5";
        self.disabled = true;
        self.invalidateAlone();
    };

    /**
     * @override
     */
    self.active = () => {
        if (!self.getVisibility()) {
            return;
        }
        self.drawer.buttonContainer.style.opacity = "1";
        self.disabled = false;
        self.invalidateAlone();
    };

    const getUrl = () => {
        const form = self.getForm();
        return form.protocol + "://" + form.domains.jane + "/" + form.tenantId + "/aipp/" + form.aippId + "/instances/" + form.aippInstanceId + "/form";
    };

    /*-- 注册事件处理 --*/
    self.addPageEventListener("report_editable", (data) => {
        // 只接受当前页面的事件.
        if (self.page.id !== data.page || !self.disabled) {
            return;
        }
        self.active();
    });

    self.addPageEventListener("report_readonly", (data) => {
        // 只接受当前页面的事件.
        if (self.page.id !== data.page || self.disabled) {
            return;
        }
        self.disable();
    });

    return self;
};

/**
 * 报告中的按钮绘制器
 *
 * @override
 */
const reportSaveButtonDrawer = (shape, div, x, y) => {
    const self = containerDrawer(shape, div, x, y);
    self.type = "reportSaveButtonDrawer";

    /**
     * @override
     */
    const initialize = self.initialize;
    self.initialize = () => {
        initialize.apply(self);
        self.buttonContainer = document.createElement("div");
        self.buttonContainer.className = "aipp-form-button-container";

        const textContainer = document.createElement("div");
        textContainer.textContent = shape.text;
        textContainer.contentEditable = shape.getEnableInteract() + "";
        textContainer.style.outline = "none";

        // 绑定事件
        self.buttonContainer.addEventListener("click", () => {
            shape.click(shape.x, shape.y, [self.buttonContainer]);
        });
        textContainer.addEventListener("input", () => shape.text = textContainer.textContent, false);

        self.buttonContainer.appendChild(textContainer);
        self.parent.appendChild(self.buttonContainer);

        // CSS格式化
        const style = document.createElement("style");
        style.innerHTML = `
            .aipp-form-button-container {
                width: 60px;
                height: 32px;
                border-radius: 4px;
                background-color: rgb(4, 123, 252);
                border-color: #FFFFFF;
                color: #FFFFFF;
                font-size: 14px;
                text-align: center;
                display: flex;
                justify-content: center;
                align-items: center;
                pointer-events: auto;
            }
            
            .aipp-form-button-container:hover {
                cursor: pointer;
            }
        
            .aipp-form-button-icon {
                padding: 5px 5px 0px 0px;
                display: inline-block;
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
        self.buttonContainer.style.pointerEvents = shape.disabled ? "none" : "auto";
    };

    return self;
};

export {startButton, confirmButton, htmlButton, sessionStartButton, sessionContinueButton, reportSaveButton};