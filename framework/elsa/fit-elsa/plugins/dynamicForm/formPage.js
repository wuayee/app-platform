import {page} from "../../core/page.js";
import {ALIGN, EVENT_TYPE} from "../../common/const.js";
import {formAddCommand} from "./commands.js";

/**
 * 动态表单page.
 *
 * @param div dom元素.
 * @param graph 画布.
 * @param name 名称.
 * @param id 唯一标识.
 * @returns {(WorkerGlobalScope & Window) | Window}
 */
export const formPage = (div, graph, name, id) => {
    const self = page(div, graph, name, id);

    // 最外层展示滚动条.
    if (div) {
        div.style.overflow = "auto";
    }
    self.type = "formPage";
    self.namespace = "dynamicForm";
    self.disableContextMenu = true;
    self.getMenuScript = () => [];

    /**
     * @override
     */
    self.showContextMenu = () => {
        if (self.disableContextMenu === undefined || self.disableContextMenu === null) {
            return false;
        }
        return !self.disableContextMenu;
    };

    /* 图形缓存. */
    self.shapeStore = (function () {
        const buffer = new Map();
        const store = {};

        /**
         * 通过组件id获取组件.
         *
         * @param componentId 组件id.
         */
        store.get = (componentId) => {
            if (!componentId) {
                throw new Error("ComponentId is invalid.");
            }
            let shape = buffer.get(componentId);
            if (!shape) {
                const shapes = self.shapes.filter(s => s.componentId === componentId);
                if (shapes.length > 1) {
                    throw new Error("Expect one but got two.");
                }
                shape = shapes[0];

                // 当图形存在，并且图形被删除时，同时删除缓存数据.
                if (shape) {
                    buffer.set(componentId, shape);
                    const remove = shape.remove;
                    shape.remove = (source) => {
                        const removed = remove.apply(shape, [source]);
                        buffer.delete(shape.componentId);
                        return removed;
                    };
                }
            }

            return shape;
        };

        /**
         * 清理数据.
         */
        store.clear = () => {
            buffer.clear();
        };

        return store;
    })();

    /* http工具. */
    self.httpUtil = (function () {
        const httpRequest = (method, url, headers, data, callback) => {
            const xhr = new XMLHttpRequest();
            xhr.withCredentials = true // 跨域时自动带上cookie信息
            xhr.open(method, url, true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            headers && headers.forEach((v, k) => {
                xhr.setRequestHeader(k, v);
            });
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        callback(JSON.parse(xhr.responseText));
                    } else {
                        self.graph.fireEvent({
                            type: EVENT_TYPE.ERROR_OCCURRED,
                            value: new Error('HTTP request failed with status ' + xhr.status)
                        });
                    }
                }
            };
            if (data) {
                xhr.send(JSON.stringify(data));
            } else {
                xhr.send();
            }
        }

        const http = {};

        /**
         * get请求.
         *
         * @param url 请求的url地址.
         * @param callback 回调.
         * @param headers 请求头.
         */
        http.get = (url, callback, headers = new Map()) => {
            httpRequest("GET", url, headers, null, callback);
        };

        /**
         * post请求.
         *
         * @param url 请求的url地址.
         * @param data 请求的数据.
         * @param callback 回调.
         * @param headers 请求头.
         */
        http.post = (url, data, callback, headers = new Map()) => {
            httpRequest("POST", url, headers, data, callback);
        };

        /**
         * put请求.
         *
         * @param url 请求的url地址.
         * @param data 请求的数据.
         * @param callback 回调.
         * @param headers 请求头.
         */
        http.put = (url, data, callback, headers = new Map()) => {
            httpRequest("PUT", url, headers, data, callback);
        };

        /**
         * 文件上传.
         *
         * @param url 接口地址.
         * @param file 文件对象.
         * @param processCallback 上传进度处理回调.
         * @param callback 上传完成或失败回调.
         * @param headers 请求头.
         * @return XMLHttpRequest 请求对象.
         */
        http.uploadFile = async (url, file, processCallback, callback, headers = new Map()) => {
            const formData = new FormData();
            formData.set("file", file);
            const xhr = new XMLHttpRequest();
            xhr.withCredentials = true // 跨域时自动带上cookie信息
            xhr.open('POST', url, true);
            headers && headers.forEach((v, k) => {
                xhr.setRequestHeader(k, v);
            });

            // 处理上传进度
            xhr.upload.onprogress = function (event) {
                if (event.lengthComputable) {
                    // 直接返回好计算后的进度.
                    const _progress = Math.min(99, Math.round((event.loaded / event.total) * 100));
                    processCallback(_progress);
                }
            };

            // 处理上传完成
            xhr.onload = function () {
                if (xhr.status === 200) {
                    const response = JSON.parse(xhr.responseText);
                    callback(response);
                } else {
                    // todo@zhangyue agent提供onHttpError方法，统一交由外部处理http异常.
                    throw new Error("upload file failed: " + xhr.status);
                    // callback(new Error("upload file failed: " + xhr.status));
                }
            };

            // 发送请求
            console.log("============== welink test: httpUtil#uploadFile#send");
            await xhr.send(formData);
            return xhr;
        };

        return http;
    })();

    /**
     * 外部也可设置自己的httpUtil.
     *
     * @param httpUtil http工具对象.
     */
    self.setHttpUtil = (httpUtil) => {
        if (!httpUtil) {
            return;
        }
        self.httpUtil = httpUtil;
    };

    /**
     * 加载form.
     *
     * @param data form数据.
     * @returns {Promise<void>}
     */
    self.loadForm = async (data) => {
        if (!data) {
            return;
        }
        if (!data.pages) {
            throw new Error("pages not exists.");
        }
        const page = data.pages[0];
        if (!page.shapes) {
            throw new Error("shapes not exists.");
        }

        const formData = page.shapes.find(s => s.type === "form");
        if (!formData) {
            throw new Error("form not exists.");
        }

        // 设置form的container为当前page.
        formData.container = self.id;
        self.ignoreReact(() => {
            page.shapes.orderBy(s => s.index).forEach(sd => {
                const createdShape = self.createShape(sd.type, sd.x, sd.y, sd.id, true);
                createdShape.deSerialize(sd);
                self.shapeDeSerialized(createdShape);
            });
        });

        self.reset();
        const form = self.shapes.filter(s => s.isTypeof("form"))[0];
        self.form = form;

        // 执行用户设置的脚本.
        form.traverse(s => s.runScript && s.runScript());
        form.formLoaded();
    };

    /**
     * 运行page.
     *
     * @param customizedData 自定义数据.
     * @param submittedCallback 表单提交后的回调.
     * @param onSubmitCallback 表单提交时的回调.
     */
    self.run = (customizedData, submittedCallback, onSubmitCallback) => {
        self.fillScreen();
        const form = self.getForm();
        customizedData && form.loadCustomizedData(customizedData);
        form.runScript();
        submittedCallback && (form.submittedCallback = submittedCallback);
        onSubmitCallback && (form.onSubmitCallback = onSubmitCallback);
        form.notifyRendered();
    };

    /**
     * 是否是form对象.
     *
     * @param type 类型.
     * @returns {boolean} true/false.
     */
    self.isForm = (type) => {
        return type === "form";
    };

    /**
     * 获取form.
     *
     * @returns {T|null} form对象.
     */
    self.getForm = () => {
        if (!self.form) {
            const forms = self.shapes.filter(s => s.isTypeof("form"));
            self.form = forms.length > 0 ? forms[0] : null;
        }
        return self.form;
    };

    /**
     * 使form充满整个parent dom元素.
     *
     * @override
     */
    self.fillScreen = () => {
        const form = self.getForm();
        if (!self.div || !form) {
            return;
        }

        // 减去滚动条的宽度，否则出现滚动条时，会遮挡一部分表单内容.
        form.width = self.div.offsetWidth - getScrollbarWidth();
        form.invalidate();
    };

    const getScrollbarWidth = () => {
        // 创建一个具有滚动条的元素
        const outer = document.createElement('div');
        outer.style.visibility = 'hidden';
        outer.style.width = '100px';
        document.body.appendChild(outer);

        const widthNoScroll = outer.offsetWidth;

        // 添加滚动条
        outer.style.overflow = 'scroll';

        const inner = document.createElement('div');
        inner.style.width = '100%';
        outer.appendChild(inner);

        const widthWithScroll = inner.offsetWidth;

        // 移除元素
        outer.parentNode.removeChild(outer);

        // 计算滚动条宽度
        return widthNoScroll - widthWithScroll;
    };

    /**
     * 重写take方法，take完成后，使其充满屏幕.
     *
     * @override
     */
    const take = self.take;
    self.take = async (data) => {
        await take.call(self, data);
        self.fillScreen();
    };

    /**
     * 重写clear方法，清理shapeStore中的数据.
     *
     * @override
     */
    const clear = self.clear;
    self.clear = () => {
        clear.apply(self);
        self.shapeStore.clear();
    };

    /**
     * 在form的场景下，scroll需要计算到根节点元素那一层，而不只是交互层.
     *
     * @override
     */
    self.getScrollPosition = () => {
        const x = self.interactDrawer.sensor.scrollLeft + div.scrollLeft;
        const y = self.interactDrawer.sensor.scrollTop + div.scrollTop;
        return {x, y};
    };

    /**
     * 在form中，触发want时，直接在form中插入.
     *
     * @override
     */
    self.want = (shapeType, properties) => {
        const form = self.getForm();
        if (!form) {
            return;
        }
        self.graph.change(() => {
            const newShape = self.createNew(shapeType, form.x, form.y + form.height, undefined, properties);
            newShape.container = form.id;
            form.invalidate();
            self.getFocusedShapes().forEach(s => s.unSelect());
            newShape.select();
            const newShapes = [{shape: newShape}];
            if (newShape.isTypeof("container")) {
                newShape.getShapes().forEach(s => {
                    newShapes.push({shape: s});
                });
            }
            formAddCommand(self, newShapes);
        });
    };

    /**
     * 创建label，调用地方太多，这里抽取方法.
     *
     * @param x 横坐标.
     * @param y 纵坐标.
     * @returns {*} label对象.
     */
    self.createLabel = (x, y, parent) => {
        const label = self.createShape("htmlLabel", x, y);
        label.container = parent.id;
        label.vAlign = ALIGN.MIDDLE;
        label.hAlign = ALIGN.LEFT;
        label.selectable = false;
        return label;
    };

    return self;
};