import {ALIGN, ANIMATION_TRIGGER, ANIMIATION_ACTION, PAGE_MODE, Z_INDEX_OFFSET} from '../../common/const.js';
import {page} from '../../core/page.js';
import {presentationActions} from './presentationActions.js';
import {defaultGraph} from "../../core/defaultGraph.js";

/**
 * 一个演讲，相当于一个ppt文件
 */
let presentation = (div, title) => {
    let self = defaultGraph(div, title);

    // 将点赞点踩加入白名单.
    self.collaboration.whiteTopics.push("procons");
    self.collaboration.whiteTopics.push("comment");

    self.type = "presentation";
    self.pageType = "presentationPage";
    //----------------setting---------------------
    self.setting.borderColor = "gray";
    self.setting.backColor = "whitesmoke";
    self.setting.headColor = "gray";
    self.setting.fontColor = "dimgray";
    self.setting.captionfontColor = "dimgray";
    self.setting.fontFace = "arial";
    self.setting.captionfontFace = "arial black";
    self.setting.fontSize = 12;
    self.setting.captionfontSize = 14;
    self.auxiliaryToolConfig.enableGuides = false;
    //--------------end of setting----------------
    /**
     * 往前找到最近的含有agenda的页面
     * huiz 2021
     */
    self.getLatestAgenda = page => {
        let index = self.getPageIndex(page.id);
        while (index > 0) {
            index--;
            let pageData = self.getPageData(index);
            let agenda = pageData.shapes.find(s => s.type === 'agenda');
            if (agenda !== undefined) {
                let items = pageData.shapes.filter(s => s.container === agenda.id);
                return {agenda: agenda, items: items};
            }
        }
        return undefined;
    };

    /**
     * clone 某page到该page的下一页
     * clone出来的是序列化数据数据
     */
    self.clonePage = pageId => {

    };
    /**
     * 复制page的所有数据
     */
    self.copyPage = pageId => {

    };
    self.pastePage = pageId => {

    };

    /**
     * 对外公布page create事件
     * 辉子 2021
     */
    self.pageCreated = page => {
    };

    /**
     * 主动跳到当前演示的位置
     * 辉子 2021
     */
    const gotoCurrentPage = self.gotoCurrentPage;
    self.gotoCurrentPage = (page, presented) => {
        const presented1 = (page, position) => {
            //将动画播放到当前位置
            page.movePageActionToPresentPosition(position);
            presented && presented(page, position);
        };
        gotoCurrentPage.call(self, page, presented1);
    };

    /**
     * 下一页指令
     */
    const nextPage = self.subscriptions["page_step_moved"];
    self.subscriptions["page_step_moved"] = message => {
        if (self.getMode() !== PAGE_MODE.VIEW) {
            return;
        }
        nextPage.call(self, message);
        self.activePage.movePageActionToPresentPosition(message);
    };

    self.subscriptions["procons"] = async message => {
        if (message.page !== self.activePage.id) {
            return;
        }
        const procons = message.value;
        self.activePage["pros"] = procons.pros;
        self.activePage["cons"] = procons.cons;
        // const procons = await self.collaboration.invoke({
        //     method: "get_appreciations", mode: self.activePage.mode, value: message.page
        // });
        // self.activePage["pros"] = procons[0];
        // self.activePage["cons"] = procons[1];
        if (self.proconsUpdating) {
            return;
        }
        self.proconsUpdating = true;
        Promise.resolve().then(() => {
            let frame = self.activePage.getFrame();
            frame.invalidateAlone();
            self.proconsUpdating = false;
        })

    };

    /**
     * 获取上一页数据.
     *
     * @param nextPageId 下一页的pageId.
     * @param condition 筛选条件函数.
     * @return {*} page的数据.
     */
    const getPreviousPage = self.getPreviousPage;
    self.getPreviousPage = (nextPageId, condition) => {
        if (self.contains(nextPageId)) {
            return getPreviousPage.apply(self, [nextPageId, condition]);
        }
        return self.pages[self.pages.length - 1];
    }

    /**
     * 是否包含某一页.
     *
     * @param pageId page的唯一标识.
     * @return {boolean} true/false.
     */
    self.contains = (pageId) => {
        return self.pages.some(p => p.id === pageId);
    }

    /**
     * 重写present方法.
     * 1、当开始演示时，需要添加最后一页.
     *
     * @param index 下标.
     * @param div dom元素.
     * @return {Promise<*>} Promise对象.
     */
    const present = self.present;
    self.present = async (index, div) => {
        // 演示时，添加结束页.
        addTerminalPage();
        return await present.apply(self, [index, div]);
    }

    /**
     * 取消全屏时的回调.
     *
     * @param pageId 页面id.
     */
    self.fullScreenCancelled = (pageId) => {
        // 只有演示模式退出时执行该方法，才需要执行下列操作.
        if (self.getMode() === PAGE_MODE.PRESENTATION) {
            // 删除最后一页.
            self.removePage(self.pages.length - 1);

            // 若找不到当前页，说明是已被删除的最后一页，取目前的最后一页id.
            const index = self.getPageIndex(pageId);
            if (index === -1) {
                pageId = self.getPageData(self.pages.length - 1).id;
            }
        }

        // 目前演示都是新建一个graph，因此不需要再进行edit，只需要通知退出了演示.
        self.dirtied(self.serialize(), {page: pageId, action: "exit_present", session: self.session});
    };

    const addTerminalPage = () => {
        const terminalPage = self.newPage(undefined, PAGE_MODE.PRESENTATION);
        terminalPage.ignoreReact(() => {
            terminalPage.backColor = "black";
            const frame = terminalPage.getFrame();
            frame.text = "放映结束, 单击鼠标退出";
            frame.hAlign = ALIGN.MIDDLE;
            frame.backColor = "black";
            frame.readOnly = true;
            frame.hideText = false;
            frame.selectable = false;
            frame.item = [0, 0, 0, 0];
            const vector = frame.getVector();
            vector.x = 1;
            vector.y = 1;
            vector.itemPad = [6, 6, 6, 6];
            vector.selectable = false;
            terminalPage.serialize();
        });
    }

    return self;
};

/**
 * 一个演讲中的一页
 */
let presentationPage = (div, graph, name, id) => {
    let self = page(div, graph, name, id);
    self.fontColor = "whitesmoke";
    self.hAlign = ALIGN.MIDDLE;
    self.vAlign = ALIGN.MIDDLE;
    self.backColor = "#edebe9";
    self.type = "presentationPage";
    self.namespace = "presentation";
    self.isTemplate = false;//template will not display, it will be other page's template
    self.basePage = "";//base will be the background of this page,base is a ID
    self.inMethod = "fadeIn";
    self.outMethod = "fadeOut";
    self.pros = 0;
    self.cons = 0;
    self.isTerminal = false;

    /**
     * * 注意 *
     * 这里主要是规避在ppt场景中，图形被拖出frame层级发生变化的问题.
     *
     * update by z00559346 2023.03.30
     */
    self.childAllowed = (child) => {
        // 如果模式不是configuration，则允许所有的图形作为child.
        // 否则，新建的时候缩略图显示不出来.
        // * 注意 * 暂时规避.
        if (!self.isReady || self.mode !== PAGE_MODE.CONFIGURATION) {
            return true;
        }
        return child.type === "presentationFrame";
    }

    /**
     * [
     * action:in/out
     * method:fadein/flyin
     * shape: some shape
     * trigger:click/follow
     * follow: other shape id
     * ]
     */
    self.animations = [];//shapes in/out animations
    const dirtyAnimations = () => {
        !self.animations && (self.animations = []);
        const push = self.animations.push;
        self.animations.push = items => {
            push.call(self.animations, items);
            // @马莉亚 增加备注：因未修改数据地址，所以无法直接通过shape.property来实现变化监听，需手动触发
            self.propertyChanged("animations", self.animations, null);
        };

        self.animations.clean = () => {
            const len = self.animations.length;
            self.animations.remove(a => !self.shapes.contains(s => s.id === a.shape));
            if (self.animations.length !== len) {
                // @马莉亚 增加备注：因未修改数据地址，所以无法直接通过shape.property来实现变化监听，需手动触发
                self.propertyChanged("animations", self.animations, null);
            }
        };
    };
    dirtyAnimations();

    self.deSerialized = () => dirtyAnimations();
    self.shapeDeSerialized = shape => self.getFrame().shapeDeSerialized(shape);

    self.createAnimation = (shape, action, method, trigger, follow, animationCode, finalizeCode) => {
        let animation = {
            action: action,
            method: method,
            shape: shape.id,
            trigger: trigger,
            follow: follow,
            animationCode: animationCode,
            finalizeCode: finalizeCode
        };
        self.animations.push(animation);
        return animation;
    };

    /**
     * 批量创建动画
     */
    self.createAnimations = (shapes, action, method, trigger, follow) => {
        if (!shapes) {
            return;
        }
        shapes.forEach(shape => self.createAnimation(shape, action, method, trigger, follow))
    }

    /**
     * 批量清除动画
     *
     * @param shapes 需要清除动画的shape数组
     */
    self.clearAnimations = shapes => {
        if (!shapes) {
            return;
        }
        shapes.forEach(shape => self.animations && self.animations.remove(item => item.shape === shape.id));
        self.propertyChanged("animations", self.animations, null);
    };

    self.createCodeAnimation = (trigger, animationCode, finalizeCode, method, follow) => {
        return self.createAnimation({id: undefined}, ANIMIATION_ACTION.CODE, method === undefined ? "code" : method, trigger, follow, animationCode, finalizeCode);
    };

    /**
     * 形状成组
     */
    const group = self.group;
    self.group = shapes => {
        group.apply(self, [shapes]);
        self.clearAnimations(shapes);
    };

    self.getFrame = () => self.shapes.find(s => s.isType('presentationFrame'));

    /**
     * 确定view模式下，不能选中模板中的frame，编辑模式和演示模式不影响
     */
    const filterPositionShapes = self.filterPositionShapes;
    self.filterPositionShapes = (x, y) => {
        const shapes = filterPositionShapes.call(self, x, y);
        if (self.page.mode === PAGE_MODE.VIEW) {
            shapes.remove(s => s.isTypeof("presentationFrame") && s.container !== self.page.id);
        }
        return shapes;
    };

    let getScale = function () {
        let scale = {};
        return function (refresh) {
            if (scale.x === undefined || refresh) {
                scale.x = div.clientWidth / self.getFrame().width;
            }
            if (scale.y === undefined || refresh) {
                scale.y = div.clientHeight / self.getFrame().height;
            }
            return scale;
        }
    }();

    self.zoomed = () => {
        self.getFrame().invalidateAlone();
    }
    /**
     * fill screen when display graph
     */
    self.fillScreen = (refresh) => {
        let scale = getScale(refresh);
        if (self.get("geoScale")) {
            self.scale(scale.x, scale.y);
        } else {
            const s = Math.min(scale.x, scale.y);
            self.scale(s, s);
        }
        // self.getFrame().invalidate();
        // self.drawer.reset();
        // self.interactDrawer.reset();
        if (self.countdown) {
            let frame = self.getFrame();
            self.countdown.visible = true;
            self.countdown.moveTo(frame.x + frame.width / 2 - 70, frame.y + frame.height - 50);
            // return;
        }
        self.invalidate();
        self.interactDrawer.reset();
    };

    const fullScreen = self.fullScreen;
    self.fullScreen = async () => {
        self.getFrame().drawer.parent.style.visibility = "hidden";
        await fullScreen.call(self);
        self.getFrame().drawer.parent.style.visibility = "visible";
    };

    let initialize = self.initialize;
    self.initialize = () => {
        self.createNew("presentationFrame", 0, 0);
        initialize.call(self);
    };

    let keyPressed = self.keyPressed;
    self.keyPressed = e => {
        if (!keyPressed.call(self, e)) {
            return false;
        }
        let base = (e.ctrlKey || e.metaKey || e.shiftKey);

        switch (self.mode) {
            case PAGE_MODE.PRESENTATION:
                if (base && e.code === "KeyH") {
                    self.disableAppreciationAnimation = !self.disableAppreciationAnimation;
                    return false;
                }

                if (base && e.code === "KeyE") {
                    self.getFrame().enableSocial = !self.getFrame().get("enableSocial");
                }
                break;
            case PAGE_MODE.CONFIGURATION:
                if (base && e.code === "KeyT") {
                    self.isTemplate = !self.isTemplate;
                }

                if (base && e.code === "KeyN") {
                    let index = self.graph.getPageIndex(self.id);
                    let newPage = self.graph.addPage("new page");
                    newPage.basePage = self.basePage;
                    self.graph.movePageIndex(index, index + 1);
                    self.graph.pageCreated(newPage);
                    return false;
                }

                break;
        }
        return true;
    };

    /**
     * 加载数据后
     */
    let onLoaded = self.onLoaded;
    self.onLoaded = () => {
        onLoaded.call(self);
        if (!self.allowPresent()) {
            return;
        }
        self.animations.clean();
        self.animations.forEach(a => {
            let s = self.shapes.find(s => s.id === a.shape);
            let getSelectable = s.getSelectable;
            s.getSelectable = () => {
                return !s.animationHide && getSelectable.apply(s);
            }
        });

        self.animations.filter(a => a.action === ANIMIATION_ACTION.IN).forEach(a => {
            let s = self.shapes.find(s => s.id === a.shape)
            s.drawer.parent.style.opacity = "0";
            s.animationHide = true;
            // s.visible = false;
            // s.invalidate();
            //todo:handle following shapes
        });
        self.animationIndex = 0;
    };

    /**
     * 跳到下一页
     * 如果有形状动画，先走动画
     */
    self.moveNext = async () => {
        const nextPage = self.graph.getNextPage(self.id, p => !p.isTemplate);

        // 若下一页不存在，则直接退出演示.
        if (!nextPage) {
            self.cancelFullScreen();
            return;
        }

        //handle shape animation
        if (self.animations.length > self.animationIndex) {
            let animation = self.animations[self.animationIndex];
            //找到跟随的shapes，一起in...那种一个一个来的就算了，懒得弄 辉子
            let follow = self.animations.filter(a => a.trigger === ANIMATION_TRIGGER.FOLLOW && a.follow === animation.shape);
            follow.push(animation);
            follow.forEach(f => {
                presentationActions.animate(self.shapes.find(s => s.id === f.shape), f.method, f.animationCode, f.finalizeCode);
            });
            self.animationIndex += follow.length;
            return true;
        }

        // todo@xiafei 暂时规避，斐哥后续进行修改.
        // presentationActions.currentPage.id = nextPage.id;//id改变为下一页id，所有pagecode就会此时停止运行
        await presentationActions.animate(self.getFrame(), self.outMethod);
        await self.take(nextPage);
        return true;
    };

    self.loadComment = comments => {
        let host = self.shapes.find(s => s.id === comments.shape);
        if (host === undefined) {
            return;
        }
        if (host.comments === undefined) {
            host.comments = [];
        }
        comments.socialValue.forEach(s => host.comments.push(s));
        host.drawer.drawRegions();
    }

    /**
     * 播放某页
     */
    const take = self.take;
    self.take = async (data, afterPresent) => {
        await take.call(self, data);

        if (self.mode === PAGE_MODE.CONFIGURATION) {
            self.fillScreen();
            // todo@zhangyue presentation在编辑时，需要缩放一点，使其不充满整个画布.暂时规避，后续需要和辉哥讨论(和activePage一起).
            // todo@zhangyue zoom的数据需要计算.
            self.zoom(-0.02);
            return;
        }

        if (self.mode === PAGE_MODE.PRESENTATION || self.mode === PAGE_MODE.VIEW) {
            self.graph.activePage = self;
            presentationActions.animate(self.getFrame(), self.inMethod).then(() => {//self.inMethod确定了下一页的进入方式
                let follow = self.animations.filter(a => a.trigger === ANIMATION_TRIGGER.FOLLOW && a.follow === self.id);//进入页面时找到页面中跟随的所有shape，一起进入
                follow.forEach(f => {
                    presentationActions.animate(self.shapes.find(s => s.id === f.shape), f.method, f.animationCode, f.finalizeCode);
                });
                self.animationIndex = follow.length;
                self.getAppreciations();
                afterPresent && afterPresent(self);
            });
        }
    };

    /**
     * 刷新踩赞数.
     */
    self.getAppreciations = () => {
        if (self.mode !== PAGE_MODE.PRESENTATION && self.mode !== PAGE_MODE.VIEW) {
            return;
        }
        self.graph.collaboration.invoke({method: "get_appreciations", mode: self.mode, value: self.id})
            .then(proCons => {
                if (!proCons) {
                    return;
                }
                self["pros"] = proCons[0];
                self["cons"] = proCons[1];
                self.getFrame().invalidateAlone()
            });
    }

    /**
     * 跳到上一页，忽略动画
     * 覆盖基类方法
     */
    self.movePrevious = () => {
        //rollback all animations
        if (self.animationIndex > 0) {
            self.onLoaded();
        } else {
            let previousPage = self.graph.getPreviousPage(self.id, p => !p.isTemplate);
            if (previousPage === undefined) {
                return;
            }
            self.take(previousPage);
            // self.deSerialize(previousPage);
            // self.fillScreen();
            // self.reset();
        }
    };

    //-----------------------------end hand writing--------------------------------------------

    self.toImage = handle => self.getFrame().toImage(handle);

    self.selectAll = () => self.getFrame().getShapes().filter(s => s !== self.getFrame().getVector()).forEach(shape => {
        shape.select();
    });

    //--------------------------serialization & detection------------------------------  
    // self.serializedFields.batchAdd("isTemplate", "basePage", "animations", "inMethod", "outMethod", "geoScale", "isTerminal");

    self.addDetection(["basePage"], (property, value, preValue) => {
        if (value === preValue) {
            return;
        }
        const frame = self.getFrame();
        frame && (frame.referencePage = value);
    });

    self.addDetection(["id"], (property, value, preValue) => {
        if (self.getFrame() === undefined) {
            return;
        }
        self.getFrame().id = "frame:" + self.id;
    });
    //----------------------------------------------------------------------------------  

    //--------------------------注册远程调用回调-----------------------------------------
    self.movePageActionToPresentPosition = async (position) => {
        if (position.value === "-1") {
            return;
        }
        if (self.animationIndex > position.value) {
            await self.movePrevious();
        }
        while (self.animationIndex < position.value) {
            const moveNext = await self.moveNext();
            if (!moveNext) {
                break;
            }
        }
        //self.moveNext();
    }

    //---------------for countdown----------------------------
    //self.clear = () => {
    //self.shapes.remove(s => s.id !== "countdown" && self.allowPresent());
    //};
    const onLoading = self.onLoading;
    self.onLoading = page => {
        try {
            onLoading.call(self, page);
        } catch (e) {
            console.warn(e);
        }

        if (self.mode !== PAGE_MODE.PRESENTATION) {
            return;
        }
        if (!self.graph.setting.countdownSeconds) {
            return;
        }
        if (!self.countdown) {
            self.countdown = self.createNew("countdown", 1, 1, "countdown");//self.shapes.find(s => s.id === "countdown");
            // self.countdown.id = "countdown";
            self.countdown.initValue = self.graph.setting.countdownSeconds;
            self.countdown.width = self.countdown.originWidth * 0.7;
            self.countdown.height = self.countdown.originHeight * 0.7;
            let get = self.countdown.get;
            self.countdown.get = field => {
                if (field === "selectable" || field === "moveable") {
                    return true;
                } else {
                    return get.call(self.countdown, field);
                }
            }
            self.countdown.onMouseDrag = (position) => {
                self.countdown.dragTo(position);
            }
            self.countdown.timeUping = () => {
                let timeupColor = "red";
                self.countdown.emphasized = true;
                self.countdown.borderColor = self.countdown.fontColor = timeupColor;
                let frame = self.getFrame();
                if (frame.borderWidth === 2) {
                    frame.borderWidth = 5;
                    frame.borderColor = timeupColor;
                } else {
                    frame.borderWidth = 2;
                    frame.borderColor = "darkgray";
                }
            }
        }

        if (self.countdown.container !== self.id) {
            self.countdown.container = self.id;
            self.shapes.push(self.countdown);
            self.countdown.invalidate();
        }
    }

    //--------------------------------------------------------
    /**
     * 重写switchMouseInShape.
     * 1、如果图形是不可选中的，则找不到该图形.
     */
    const switchMouseInShape = self.switchMouseInShape;
    self.switchMouseInShape = (x, y, condition) => {
        const newCondition = s => {
            const con = condition ? condition : () => true;
            //refactored by huizi
            // if (condition) {
            //     return condition(s) && (s.getSelectable() || self.isTerminal);
            // }
            return con(s) && (s.getSelectable() || self.isTerminal || s.isLocked());//locked shape could be target huizi
        }
        return switchMouseInShape.apply(self, [x, y, newCondition]);
    };

    //--------------------------------------------------------

    /**
     *  获取当前页面中允许移动到的最小层级
     * 1、因为presentationPage有presentationFrame和referenceVector两个容器，因此前两个位置不能进行移动.
     * 2、引用其他页面的场景下，有引用页的presentationFrame，会排在第三个位置，且不能进行移动.
     * 3、引用其他页面的场景下，有引用页的isPlaceholder不为1的图形，此类图形不可被序列化，且位置不能进行移动.
     */
    self.getMinIndex = () => {
        let min = 2 + Z_INDEX_OFFSET;

        if (self.getFrame() && self.getFrame().referenceData) {
            // 引用页的presentationFrame和不允许被操作的图形，位置都不能移动
            let rShapeIds = Object.keys(self.getFrame().referenceData).filter(r => r !== "placed");
            const shapes = self.shapes.filter(s => rShapeIds.includes(s.id));
            if (shapes.length > 0) {
                min = shapes[shapes.length - 1].index + 1;
            }
        }

        return min;
    }

    /**
     * 重写moveIndexBefore方法.
     * 不能移动到最小图层之前（最小图层需要计算）.
     *
     * @param shape 图形对象.
     * @param index 数组下标.
     */
    const moveIndexBefore = self.moveIndexBefore;
    self.moveIndexBefore = (shape, index) => {
        if (index <= self.getMinIndex() - 1) {
            return;
        }
        moveIndexBefore.apply(self, [shape, index]);
    }

    /**
     * 重写moveIndexBottom方法.
     * 1、和moveIndexBefore方法类似，不能移动到最小图层之前（最小图层需要计算）.
     * @param shape 图形对象.
     */
    self.moveIndexBottom = (shape) => {
        if (self.basePage && self.basePage !== "") {
            self.moveIndexBefore(shape, self.getMinIndex());
        } else {
            self.moveIndexBefore(shape, 2 + Z_INDEX_OFFSET);
        }
    }

    /**
     * 得到演示文稿页面中图形的默认上下文工具栏菜单
     * 说明：为了支持不同应用不同默认菜单功能，故在对应应用的page上也能获取图形默认上下文工具栏菜单
     * @maliya
     */
    // self.getShapeContextMenuScript = () => {
    //     return [
    //         {
    //             type: "icon",
    //             icon: "🖌️",
    //             text: "删除",
    //             action: function (shape) {
    //                 alert("Icon button clicked for rectangle");
    //             }
    //         }
    //     ];
    // };

    presentationActions.currentPage = self;
    return self;
};

export {presentation, presentationPage};