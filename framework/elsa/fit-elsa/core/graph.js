import {
    ALIGN,
    DOCK_MODE,
    ELSA_NAME_SPACE,
    EVENT_TYPE,
    FONT_STYLE,
    FONT_WEIGHT,
    INFO_TYPE,
    PAGE_MODE,
    PARENT_DOCK_MODE,
    PROGRESS_STATUS,
    Z_INDEX_OFFSET
} from '../common/const.js';
import { uuid } from '../common/util.js';

import { page } from './page.js';
import { collaboration } from '../common/collaboration.js';
import { pageAddedCommand, pageIndexChangedCommand, pageRemovedCommand, transactionCommand, addCommand } from './commands.js';
import { commandHistory } from './history.js';
import { countRegion } from './hitRegion.js';
import { ENV_CONFIG } from "../config/envConfig.js";
import ShapeCache from "./cache/shapeCache.js";
import DomCache from "./cache/domCache.js";
import DomFactory from "./cache/domFactory.js";
import {contextMenu} from "./popupMenu.js";

/**
 * 绘画最高层级管理，管理了事件，画布以及所有的交互
 * 辉子 2020-02-20
 *
 * @param {*} div //默认绘制区域，是一个div，具体如何画进去，看drawer的实现，如果是svg，则在div里绘制svg；如果是canvas，则在div里添加一个canvas
 * @param title 名称.
 */
let graph = (div, title) => {
    const self = { id: uuid(true) };//创建graph

    self.uuid = () => uuid();
    self.title = title || '';
    self.type = "graph";
    self.cookie = uuid();
    self.session = { name: "huizi", id: uuid(), time: (new Date()).toDateString() };//session information
    self.ing = false;
    self.ignoreHighQuality = false;
    self.tenant = "default";
    self.source = "elsa";
    //----------------------------
    div && (div.style.overflow = "hidden");
    div && (self.cssText = div.style.cssText);
    self.div = div;
    self.tabToken = '';
    self.collaborationSession;//正在协作的session id；
    self.enableSocial = true;
    self.enableText = true;

    //----------------------------
    /**
     * determine pages the graph is going to create
     */
    self.pageType = "page";
    /**
     * 所有的绘画页面序列化后的数据
     */
    self.pages = [];
    /**
     * 通过方法得到所有page，该方法可以覆盖
     */
    self.getPages = () => self.pages;

    // self.history = commandHistory(self);
    /**
     * 页面操作的undo redo
     */

    self.historyStrategy = "graph";
    self.getHistory = () => commandHistory(self.historyStrategy, self);

    //------------------------------collaboration----------------------------------------
    /**
     * 与服务端通讯组件
     */
    self.collaboration = collaboration(self, ENV_CONFIG.collaborationUrl);
    /**
     * 注册協同能力
     */
    self.subscriptions = {};
    /**
     * 協同消息廣播
     */
    self.publish = message => {
        const subscription = self.subscriptions[message.topic];
        if (subscription === undefined) {
            return;
        }
        //更新序列化数据
        subscription(message);
        //更新渲染page数据
        //self.activePage.onMessage(message); pages included activepage
        self.syncSubscribedPage(message);
    };
    /**
     * 更新所有有显示的页面
     */
    self.syncSubscribedPage = (message, localChange) => {
        pageSubscribers.forEach(page => {
            page.onMessage(message, localChange);
        })
    };
    /**
     * 协作消息默认更新graph.pages里的静态数据
     * 被注册的这些page是有显示的对象数据，所以需要主动注册并改变
     */
    const pageSubscribers = [];
    self.getSubscribedPage = id => pageSubscribers.filter(p => p.id === id);
    self.getSubscribedPageByData = (id, div) => pageSubscribers.find(p => (p.id === id && p.div === div));
    // todo@zhangyue 考虑按page维度进行订阅.
    self.subscribePage = page => {
        if (pageSubscribers.contains(p => p === page)) {
            return;
        }
        if (page.graph !== self) {
            return;
        }
        pageSubscribers.remove(p => p.div === page.div);
        pageSubscribers.push(page);
    };

    self.unSubscribePage = page => pageSubscribers.remove(p => p === page);

    self.openCollaboration = () => {
        if (self.collaboration === undefined) {
            self.collaboration = collaboration(self, ENV_CONFIG.collaborationUrl);
        }
        self.collaboration.connect();
    };

    self.closeCollaboration = () => {
        if (self.collaboration === undefined) {
            return;
        }
        self.collaboration.close();
    };

    /**
     * 得到在綫人數消息
     * 只在activepage上处理
     * 辉子 2022
     */
    self.subscriptions["session_count"] = message => {
        const page = self.activePage;
        if (!page) {
            return;
        }
        (!page.sessionCountRegion) && (page.sessionCountRegion = countRegion(page));
        page.sessionCount = message.value;
        page.drawer.draw();
        self.sessionCount = page.sessionCount;
    };

    /**
     * 收到comment
     */
    self.subscriptions["comment"] = message => {
        // if (message.page == self.activePage.id) {
        //     self.activePage.handleComment(message, null);
        // } else {
        const page = self.pages.find(p => p.id === message.page);
        if (page === undefined) {
            return;
        }
        let shape = (page.id === message.shape) ? page : (page.shapes.find(s => s.id === message.shape));
        if (shape === undefined) {
            return;
        }
        if (shape.comments === undefined) {
            shape.comments = [];
        }
        shape.comments.push(message.value);
        // }
    };

    // const freeLines = {};
    /**
     * 收到freeline的add point 命令
     */
    self.subscriptions["add_freeline_point"] = message => {
        //dont need to handle graph data
        // if (message.page !== self.activePage.id) {
        //     return;
        // }
        // if (message.from === self.session.id) {
        //     return;
        // }
        // const shape = self.activePage.shapes.find(s => s.id === message.shape);
        // if (shape === undefined) {
        //     return;
        // }
        // shape.addPoint(message.value.x, message.value.y, true);

        // (!freeLines[message.shape]) && (freeLines[message.shape] = []);
        // const points = freeLines[message.shape];
        // message.value.forEach(point=>{
        //     points.push([point.dx,point.dy]);
        // });
    };
    /**
     * 收到freeline的done 命令
     */
    self.subscriptions["freeline_done"] = message => {
        // if (message.page !== self.activePage.id) {
        //     return;
        // }
        // if (message.from === self.session.id) {
        //     return;
        // }
        // const shape = self.activePage.shapes.find(s => s.id === message.shape);
        // if (shape === undefined) {
        //     return;
        // }
        // shape.done(true);

        // const points = freeLines[message.shape];
        // if (!points) {
        //     return;
        // }
        // self.pages.find(p => p.id === message.page).shapes.find(s => s.id === message.shape).lines.push(points);
        // delete freeLines[message.shape];

        const page = self.pages.find(p => p.id === message.page);
        let free = page.shapes.find(s => s.id === message.value.to);
        free.lines.push(message.value.line);
    };

    /**
     * 新增页面
     */
    self.subscriptions["page_added"] = message => {
        if (self.pages.find(p => p.id === message.page) !== undefined) {
            return;
        }

        // const current = self.activePage;
        const index = parseInt(message.value.index);
        let pageData = self.removedCache.pages.find(p => p.id === message.page);
        if (pageData === undefined) {
            pageData = message.value;
            pageData.shapes = pageData.shapes.orderBy(s => s.index).filter(s => s.container !== "");
        }
        self.insertPage(pageData, index, true);
    };

    self.ignoreCoedit = (f) => {
        self.coediteIgnored = true;
        const result = f();
        self.coediteIgnored = false;
        return result;
    };

    const invoke = self.collaboration.invoke;
    self.collaboration.invoke = async (method, args, pageId, callback) => {
        if (self.coediteIgnored) {
            return;
        }
        return invoke.call(self.collaboration, method, args, pageId, callback)
    };

    self.enableCache = true;
    self.shapeCache = new ShapeCache();

    /**
     * 创建shape的统一方法.
     *
     * @param owner 图形dom所属的dom元素根节点.
     * @param id 图形的id.
     * @param type 图形的类型.
     * @param x 图形的横坐标.
     * @param y 图形的纵坐标.
     * @param width 图形的宽度.
     * @param height 图形的高度.
     * @param parent 图形的container.
     * @return {*} 图形对象.
     */
    self.createShape = (owner, id, type, x, y, width, height, parent) => {
        const pageId = parent.page.id;
        let newShape = self.shapeCache.get(owner, pageId, id);
        if (!newShape) {
            if (!self.plugins[type] && type.split(".").length < 2) {
                type = self.createShapePath(type);
            }
            if (!self.plugins[type]) {
                type = "rectangle";
            }
            newShape = self.plugins[type](id, x, y, width, height, parent);
            //set nameespace and type
            const namespaces = type.split(".");
            const typeName = namespaces[namespaces.length - 1];
            newShape.type = typeName;
            if (namespaces.length > 1) {
                newShape.namespace = type.substr(0, type.length - typeName.length - 1);
            }
            self.enableCache && self.shapeCache.cache(owner, pageId, newShape);
        } else {
            if (parent.page.shapes.indexOf(newShape) < 0) {
                /*
                 * 设置page的原因：
                 * 1、初始化一个pageA，此时shape的page是pageA
                 * 2、新建一页pageB，此时graph.activePage为pageB(具体查看graph的addPage方法)
                 * 3、重新切到pageA，由于shape已经存在，则进入该逻辑
                 * 4、若不重新设置page，那么此时newShape的page是pageA，而不是pageB
                 */
                newShape.page = parent.page;
                newShape.pageId = parent.page.id;

                /*
                 * 设置容器必须在设置了page之后，否则，在disableReact为true的情况下不生效的问题，原因如下：
                 * 1、被删除图形中缓存了page引用
                 * 2、新建一页之后，activePage发生了变化
                 * 3、撤销之后，activePage对象复用，deserialize新页面的数据
                 * 4、执行command时，传入的是activePage，此时设置disableReact是设置的activePage的属性
                 * 5、但是在这里设置属性判断的disableReact是shape缓存的page对象，此时page对象和activePage不是同一个对象
                 * 6、因此，这里需要将属性设置放在page设置之后.
                 */
                newShape.container = parent.id;

                /*
                 * 设置cachedContainer的原因（以frame举例）:
                 * 1、初始化一个pageA，此时frameA的page是pageA
                 * 2、新建一页pageB，此时graph.activePage为pageB(具体查看graph的addPage方法)
                 * 3、重新切换到pageA，由于frame已经存在，不重新创建
                 * 4、由于activePage存在，此时会直接deserialize pageA的数据，
                 *    而activePage的dom还是pageB的(interactiveLayer等dom还是pageB的dom)
                 * 5、渲染时，由于shape的cachedContainer存在，在move时（htmlDrawer的move方法），
                 *    并不会做任何处理(因为当前shape的dom父节点没有变)，所以frame及其下面的所有图形不会渲染出来
                 *
                 * 这里设置cachedContainer就正常的原因：
                 * 这里相当于把cachedContainer设置为了pageB，而pageB的dom不是frame的dom父节点，
                 * 因此会进行move()，将frame的dom添加到pageB的dom中
                 */
                newShape.cachedContainer = { id: parent.id, shape: parent };
                parent.page.shapes.push(newShape);
                newShape.invalidate();
            }
        }
        return newShape;
    };

    self.domCache = new DomCache();
    self.domFactory = new DomFactory(self.domCache, self.enableCache);

    /**
     * 创建dom元素.
     *
     * @param owner 待缓存元素所属的根节点.
     * @param tagName 元素名称.
     * @param pageId 页面的id.
     * @param id dom元素的id.
     * @param ignoreExisting 是否在缓存存在的情况下也重新创建.
     * @return {*} dom对象.
     */
    self.createDom = (owner, tagName, id, pageId, ignoreExisting) => {
        return self.domFactory.create({ owner, tagName, id, pageId, ignoreExisting });
    };

    /**
     * 获取缓存的dom元素.
     *
     * @param owner 待缓存元素所属的根节点.
     * @param pageId 页面id.
     * @param id 待缓存元素id.
     * @return {null|*} 若不存在，则返回null，否则返回对应元素.
     */
    self.getElement = (owner, pageId, id) => {
        return self.domCache.get(owner, pageId, id);
    };

    /**
     * 清理一页的dom元素.
     *
     * @param owner 元素所属的根节点.
     * @param pageId 页面id.
     */
    self.clearDomElements = (owner, pageId) => {
        self.domCache.clearElementsByPageId(owner, pageId);
    };

    self.setElementId = (element, id) => {
        if (!element) {
            return;
        }
        element.id = id;
    };

    self.resetElementId = (id, preId) => {
        self.domCache.forEachDom((domId, dom) => {
            if (domId === preId) {
                dom.id.replace(preId, id);
            }
        });
    };

    /**
     * 删除页面
     * 删除后其他同步页面怎么处理有待产品讨论 辉子
     */
    self.subscriptions["page_removed"] = message => {
        if (self.pages.find(p => p.id === message.page) === undefined) {
            return;
        }//remove信息会返回自己，如果发起方已经删除，就退出，否则会多删除
        self.removePage(self.getPageIndex(message.value), true);
    };

    /**
     * 改变页面index
     */
    self.subscriptions["page_index_changed"] = message => {
        self.movePageIndex(message.value.fromIndex, message.value.toIndex, true);
    };

    /**
     * 新增一个shape
     */
    self.subscriptions["shape_added"] = message => {
        const pageData = self.pages.find(p => p.id === message.page);//get page data
        if (pageData === undefined) {
            return;
        }
        pageData.shapes.push(message.value);
    };

    /**
     * 改变shape index
     */
    self.subscriptions["shape_index_changed"] = message => {
        const pageData = self.pages.find(p => p.id === message.page);//get page data
        if (pageData === undefined) {
            return;
        }
        const shape = pageData.shapes.find(s => s.id === message.shape);
        pageData.shapes.remove(s => s.id === shape.id);
        pageData.shapes.insert(shape, message.value.toIndex - Z_INDEX_OFFSET);
    };

    /**
     * page内容有变化
     * 辉子 2021 update 2022
     */
    self.subscriptions["page_shape_data_changed"] = message => {
        self.ignoreCoedit(() => {
            const pageData = self.pages.find(p => p.id === message.page);
            if (pageData === undefined) {
                console.warn("page " + message.page + " is not found");
                return;
            }

            message.value.forEach(shapeData => {
                let target = pageData.shapes.find(s => s.id === shapeData.shape || s.entangleId === shapeData.shape);

                //如果shape没有找到，并且container不为空，说明删除的形状被恢复了,将缓存的形状加入
                if (target === undefined && shapeData.properties.container !== "" && shapeData.properties.container !== undefined) {
                    target = self.removedCache.shapes.find(s => s.id === shapeData.shape);
                    if (target === undefined) {
                        return;
                    }
                    pageData.shapes.splice(target.index, 0, target)
                }

                if (!target) {
                    return;
                }//這是一個不應該發生的事情

                //先更新所有字段信息
                for (let f in shapeData.properties) {
                    target[f] = shapeData.properties[f];
                    if (f === "local") {//局部处理
                        target.needReload = true;//无法处理shape特别的local操作，needreload表明edit,display,present时数据需要重新载入
                    }
                }

                //如果container为空，说明该shape被删除了
                if (target.container === "") {
                    pageData.shapes.remove(s => s === target);

                    /*
                     * 如果不保护，这里会产生内存泄漏.
                     * 当一个图形不停的删除撤销重做的时候，cache中的数据量会线性增大.
                     */
                    if (!self.removedCache.shapes.contains(s => s.id === target.id)) {
                        self.removedCache.shapes.push(target);
                    }

                    /*
                     * 复现问题场景，协同（ppt场景下）：
                     * 1、在协同方1中，在第一页中添加一个矩形，然后切换到第二页
                     * 2、这时在协同方2中，删除第一页中的矩形
                     * 3、在协同方1中，缩略图中的图形被删除，但是切换到第一页中时，发现dom没有被删除
                     * 因为dom是缓存起来的，在删除时，只调用了缩略图中的图形对象进行删除，主画布中的dom结构并没有删除，虽然在page的
                     * 序列化数据中已经把图形删除掉了，但是dom并没有删除，重新编辑第一页时，dom又会跟着其parent被渲染出来
                     *
                     * 这里将所有未被pageSubscribers处理的图形删除事件统一处理.
                     */
                    self.shapeCache.forEachPage((owner, pageId, shapeMap) => {
                        if (pageSubscribers.find(p => p.id === pageId && p.div === owner)) {
                            return;
                        }
                        const shape = shapeMap.get(target.id);
                        shape && shape.invalidateAlone();
                    });
                }
            });
        });
    };

    /**
     * 上一页/下一页指令,presentation or display发会发出的指令
     * 辉子 2022
     */
    self.subscriptions["page_step_moved"] = topic => {
        self.gotoCurrentPage(self.activePage);
    };

    self.subscriptions["graph_data_changed"] = topic => {
        self.setProperty(topic.value.field, topic.value.value, true);
    };

    /**
     * 上一页指令
     */
    // self.subscriptions["previous_page"] = topic => {
    //     self.gotoCurrentPage(self.activePage);
    // };

    //-------------------------------serilization------------------------------------

    /**
     * graph里page有增删改
     * page里的变动不触发
     * 辉子 2021
     */
    self.dirtied = (data, dirtyAction) => {
    };
    self.isDirty = () => {
        return self.activePage && self.activePage.isDirty();
    }

    /**
     * 序列化整个graph
     */
    self.serialize = () => {
        // 设置页面的顺序.
        const pages = self.pages.map(p => {
            const {shapes, ...left} = p;
            left.shapes = [...shapes];
            return left;
        });
        pages.forEach((p, index) => p.index = index);
        const serialized = {
            id: self.id,
            title: self.title,
            author: self.author,
            createTime: self.createTime,
            source: self.source,
            type: self.type,
            tenant: self.tenant,
            setting: self.setting,
            pages: pages,
            dirty: self.dirty,
            version: self.version,
            enableText: self.enableText,
            exceptionFitables: self.exceptionFitables,
        };

        // 深拷贝.
        return JSON.parse(JSON.stringify(serialized));
    };

    /**
     * 反序列化整个graph
     */
    self.deSerialize = data => {
        for (let f in data) {
            self[f] = data[f];
        }
    };

    self.pageSerialized = id => {
    };

    //------------------------page operations-----------------------------------------
    /**
     * 手动发起调整graph里page的顺序
     * 辉子 2022
     */
    self.changePageIndex = (fromIndex, toIndex) => {
        self.movePageIndex(fromIndex, toIndex);
        pageIndexChangedCommand(self, fromIndex, toIndex);
    };

    /**
     * 手动吊证多个页面的顺序
     * @param pageIds
     * @param index（表示从这个位置开始排下去）
     */
    self.changePagesIndex = (pageIds, index) => {
        let commands = [];
        pageIds.forEach(id => {
            const fromIndex = self.getPageIndex(id);
            self.movePageIndex(fromIndex, index);
            const command = pageIndexChangedCommand(self, fromIndex, index);
            commands.push(command);
            index += 1;
        })

        transactionCommand(self, commands, true).execute();
    };

    /**
     * 通过page的id调整page的顺序
     * @param id
     * @param toIndex
     */
    self.changePageIndexById = (id, toIndex) => {
        const index = self.getPageIndex(id);
        self.changePageIndex(index, toIndex);
    }

    /**
     * 调整graph里page的顺序
     * 辉子 2021
     */
    self.movePageIndex = (fromIndex, toIndex, isCoEditing) => {
        let page = self.pages[fromIndex];
        moveIndex(page.id, toIndex);
        (!isCoEditing) && self.collaboration.invoke({
            method: "change_page_index", page: page.id, value: { fromIndex, toIndex }
        });
    };

    /**
     * 手动删除一页
     * 辉子 2022
     */
    self.deletePage = index => {
        const page = self.pages[index];
        self.removePage(index);
        pageRemovedCommand(self, page, index);
    };

    /**
     * 手动批量删除多页
     * @param pageIds
     */
    self.deletePages = pageIds => {
        let commands = [];
        pageIds.forEach(id => {
            const index = self.getPageIndex(id);
            const page = self.pages[index];
            self.removePage(index);
            const command = pageRemovedCommand(self, page, index);
            commands.push(command);
        })
        transactionCommand(self, commands, true).execute();
    };

    /**
     * 手动新增一页
     */
    self.addPage = (name, id, targetDiv, index, data, mode = PAGE_MODE.CONFIGURATION) => {
        const page = self.ignoreCoedit(() => {
            const p = self.newPage(targetDiv, mode, name, id);
            if (data) {
                Object.keys(data).forEach(f => {
                    p[f] = data[f];
                });
            }
            return p;
        });
        self.activePage = page;
        const pageData = page.serialize();
        index = self.insertPage(pageData, index);
        pageAddedCommand(self, pageData, index);//create history command
        return page;
    };

    /**
     * 缓存删除的pages,shapes，在协作场景下undo
     */
    self.removedCache = { pages: [], shapes: [] };

    /**
     * graph中删除一 page
     */
    self.removePage = (index, isCoEditing) => {
        const page = self.pages[index];
        self.pages.splice(index, 1);
        (!self.removedCache.pages.contains(p => p.id === page.id)) && self.removedCache.pages.push(page);

        // @maliya 如果移除的页面 同时也是选中的页面，则选中页面也需要清空
        if (page.id === self.activePage.id) {
            self.activePage.expired = true;
        }

        self.pageRemoved && self.pageRemoved(page, index);
        (!isCoEditing) && self.collaboration.invoke({ method: "remove_page", page: page.id, value: page.id });
        self.dirtied(self.serialize(), { page: page.id, action: "page_removed", session: self.session });

        //删除页面时 需要清除订阅
        const pageSubscribers = self.getSubscribedPage(page.id);
        pageSubscribers.forEach(p => self.unSubscribePage(p));
    };

    /**
     * 把孤立的page加入到graph中
     * 辉子 2022
     */
    self.insertPage = (page, index, isCoEditing) => {
        //page.serialize();
        //page列表中如果没有该page，则加入该page
        (!self.pages.contains(p => p.id === page.id)) && self.pages.push(page);
        (index !== undefined && index !== self.pages.length) && moveIndex(page.id, index);
        let idx = index;
        idx === undefined && (idx = self.pages.length - 1);
        self.pageAdded && self.pageAdded(page, idx);
        if (!isCoEditing) {
            const serialized = {};
            serialized.index = idx;
            for (let f in page) {
                // if (f === "shapes") {
                //     continue;
                // }
                serialized[f] = page[f];
            }
            serialized.index = idx;
            self.collaboration.invoke({ method: "new_page", page: page.id, value: serialized });
        }
        self.dirtied(self.serialize(), { page: page.id, action: "page_added", session: self.session });
        return idx;
    };

    /**
     * create a new page object. page is not listed in graph.pages
     * 通过newPage得到的page未必属于graph，也可以是（编辑，演示）用来承载graph.pages的对象，所以在newPage里不能serialize，否则会为graph新增一页
     * 辉子 2022
     */
    self.newPage = (targetDiv, mode = PAGE_MODE.CONFIGURATION, name = "--", id, isEmpty) => {
        if (targetDiv === undefined) {
            targetDiv = self.div;
            targetDiv.style.cssText = self.cssText;
        }

        self.newPageMode = mode;
        let p = self.plugins[self.pageType](targetDiv, self, name, id);
        delete self.newPageMode;
        p.serialized = data => {
            const idx = self.pages.findIndex(p => p.id === data.id);
            (idx === -1) && (self.pages.push(data));
            self.pageSerialized(data.id);
        };

        // 未传id时，才执行initialize方法.
        (!id) && (!isEmpty) && p.initialize();
        p.load();
        p.active();
        (!self.activePage || self.activePage.expired) && (self.activePage = p);
        self.subscribePage(p);
        return p;
    };

    self.removePageById = id => {
        const index = self.getPageIndex(id);
        self.removePage(index);
    }

    // let deSerializePage = (div, mode, index) => {
    //     self.mode = mode;
    //     let page = self.newPage(div, mode);
    //     page.deSerialize(self.getPageData(index));
    //     return page;
    // }

    /**
     * 通过page在该graph中的index，得到某page数据
     * 留待覆盖，为得到最新数据，可以判断page.isDirty确定是否从persistence得到最新数据
     * 辉子 2021
     */
    self.getPageData = index => self.pages[index === undefined ? 0 : index];

    /**
     * 通过page.id得到某page数据
     * @param {*} id
     */
    self.getPageDataById = id => self.pages.find(p => p.id === id);
    /**
     * 得到页面数量
     * 辉子 2021
     */
    self.getPagesNumber = () => self.pages.length;
    /**
     * 得到某页面在本graph的index
     * 辉子 2021
     */
    self.getPageIndex = pageId => self.pages.map(p => p.id).indexOf(pageId);

    /**
     * 如果没有active的page，默认是configuration
     * @returns {string}
     */
    self.getMode = () => {
        return !self.activePage ? PAGE_MODE.CONFIGURATION : self.activePage.mode;
    }

    /**
     * start edit mode
     */
    self.edit = async (index, div, id) => {
        let page = self.newPage(div, PAGE_MODE.CONFIGURATION, undefined, id, true);
        await page.take(self.getPageData(index));
        return page;
    }

    /**
     * start runtime display mode
     */
    self.display = async (id, div) => {
        let page = self.getSubscribedPageByData(id, div);
        const index = self.getPageIndex(id);
        const data = self.getPageData(index);
        !page && (page = self.newPage(div, PAGE_MODE.DISPLAY, undefined, id, true));
        await page.take(data);
    }

    /**
     * start presentation mode
     * 辉子 2021
     */
    self.present = async (index, div) => {
        self.collaboration.mute = false;
        const pageData = self.getPageData(index);
        const page = self.newPage(div, PAGE_MODE.PRESENTATION, undefined, pageData.id, true);
        await page.take(pageData, page => {
            self.collaboration.invoke({ method: "move_page_step", page: page.id, value: page.animationIndex });
        });
        return page;
    };

    /**
     * 如果觀察者沒能得到數據，則觸發该方法
     * 辉子 2021
     */
    self.viewPresentFail = () => {
    };

    /**
     * 观看模式
     * 辉子 2021
     */
    self.viewPresent = (div) => {
        self.activePage && self.activePage.close();
        self.activePage = null;
        // todo@xiafei 避免view因为frame创建newshape， 待讨论优化
        self.collaboration.mute = true;
        self.newPage(div, PAGE_MODE.VIEW);
        self.collaboration.mute = false;
        const result = { then: f => result.presented = f };
        self.gotoCurrentPage(self.activePage, result.presented);
        // page.fullScreen().then(() => {
        //得到当前演示的页面index

        // self.collaboration.invoke({ method: "load_graph" }, pages => {
        //     if (pages === null) {
        //         self.viewPresentFail();
        //         return;
        //     }
        //     let pageData = JSON.parse(pages);
        //     self.pages = pageData.pages;
        //     self.setting = pageData.setting;

        //     //得到当前演示的页面index
        //     self.gotoCurrentPage(self.activePage, result.presented);

        // });
        // });
        return result;
    };

    /**
     * 得到所有页面简要信息
     * 可根据实际应用扩展
     * 辉子 2021
     */
    self.getPagesBrief = () => {
        let pages = [];
        self.pages.forEach((p, i) => {
            let page = {};
            page.id = p.id;
            page.index = i;
            page.isTemplate = p.isTemplate;
            pages.push(page);
        });
        return pages;
    };

    /**
     * 主动跳到当前演示的位置
     * 辉子 2021
     */
    self.gotoCurrentPage = (page, presented) => {
        self.collaboration.invoke({ method: "get_present_page_index", value: self.collaborationSession }, position => {
            const pageIndex = self.getPageIndex(position.page);
            if (page.id === position.page) {
                if (pageIndex === -1 || self.getPageData(pageIndex).isTerminal) {
                    page.cancelFullScreen();
                } else {
                    presented && presented(page, position);
                }
            } else {
                if (pageIndex === -1) {
                    console.log("page[" + position.page + "] not exists.");
                    return;
                }

                page.take(self.getPageDataById(position.page), () => {
                    presented && presented(page, position);
                });
            }
        });
    }

    /**
     * 取消全屏时触发.
     */
    self.fullScreenCancelled = () => {
    };

    /**
     * prsentation 模式下得到下一页数据
     * graph.pages在初始化时就有所有page的数据
     * todo：判断persistence是否有更新数据，如果有则取得最新数据，如果没有，采用本地graph.pages里数据
     * 辉子 2021
     */
    self.getNextPage = (previousPageId, condition = p => true) => {
        //if (condition === undefined) condition = p => true;
        let idx = self.getPageIndex(previousPageId);
        if (idx >= self.getPagesNumber() - 1) {
            return undefined;
        }
        let data = self.getPageData(idx + 1);
        if (!data) {
            return undefined;
        }
        if (condition(data)) {
            return data;
        }
        return self.getNextPage(data.id, condition);
    };

    /**
     * presentation模式下得到上一页数据
     * 其他同上
     * 辉子 2021
     */
    self.getPreviousPage = (nextPageId, condition = p => true) => {
        //if (condition === undefined) condition = p => true;
        let idx = self.getPageIndex(nextPageId);
        if (idx <= 0) {
            return undefined;
        }
        let data = self.getPageData(idx - 1);
        if (condition(data)) {
            return data;
        }
        return self.getPreviousPage(data.id, condition);
    };

    /**
     * 切page的层
     */
    const moveIndex = (pageId, index) => {
        let data = self.pages.find(p => p.id === pageId);
        if (data === undefined) {
            return;
        }
        self.pages.remove(p => p.id === pageId);
        self.pages.insert(data, index);
        self.dirtied(self.serialize(), { page: pageId, action: "page_index_changed", session: self.session });
    };

    /**
     * setting for all shapes properties: bordercolor,fontsize.....
     */
    self.setting = defaultSetting;


    /**
     * get system setting from text
     */
    self.importSetting = settingString => self.setting = eval(settingString);

    /**
     * plugins to manage all extended shapes
     * 辉子 2021
     */
    self.plugins = { page: page };

    self.auxiliaryToolConfig = auxiliaryToolConfig;

    self.login = account => {
        if (!account) {
            return;
        }
        self.session = account;//todo: need more security measures here
    }

    /**
     * pre import all core shapes
     * 辉子 2021
     */
    self.initialize = () => {
        self.loadConfig && self.loadConfig();
        const core = ".";
        return self.staticImport(() => import(/* webpackMode: "eager" */ `${core}/connector.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/container.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/ellipse.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/others.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/group.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/icon.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/image.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/line.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/rectangle.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/reference.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/svg.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/vector.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/grid.js`))
            // .staticImport(() => import(/* webpackMode: "eager" */ `${core}/table.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/video.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/triangle.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/rightArrow.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/bottomArrow.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/dovetailArrow.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/parallelogram.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/diamond.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/regularPentagonal.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/pentagram.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/leftAndRightArrow.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/rightCurlyBrace.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/roundedRectangleCallout.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ `${core}/freeLine.js`))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/presentation/caption.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/presentation/frame.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/presentation/presentation.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/presentation/textList.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/mind/hitRegion.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/mind/mind.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/mind/topic.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/mind/subTopic.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/document-new/docSection.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/officeCommon/freeShape.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/chart/interval.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/officeCommon/formula.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/flowable/nodes/node.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/flowable/nodes/startEnd.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/flowable/nodes/state.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/flowable/nodes/condition.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/flowable/nodes/subflow.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/flowable/nodes/parallel.js"))
            .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/flowable/nodes/event.js"))
            // .staticImport(() => import(/* webpackMode: "eager" */ "../plugins/chart/echart.js"))
            ;
    };

    self.staticImport = (importStatement, definedShapes, next) => {
        let createStaticBuilder = () => {
            const builder = createBuilder();
            builder.staticImport = (importStatement, definedShapes) => {
                builder.importStatement = importStatement;
                builder.definedShapes = definedShapes;
                builder.next = createStaticBuilder();
                return builder.next;
            };
            return builder;
        }
        (!next) && (next = createStaticBuilder()) && (next.definedShapes = definedShapes);//add first defined shapes huizi 2023
        importStatement().then(shapes => {
            setShapes(shapes);
            next.callback && next.callback();
            next.importStatement && self.staticImport(next.importStatement, next.definedShapes, next.next);
            next.address && self.import(next.address, next.definedShapes, next.next);
        });
        return next;
    };

    const createBuilder = () => {
        const builder = {};
        builder.then = callback => builder.callback = callback;
        builder.import = (address, definedShapes) => {
            builder.address = address;
            builder.definedShapes = definedShapes;
            builder.next = createBuilder();
            return builder.next;
        };
        return builder;
    }

    const setShapes = (shapes, definedShapes = undefined) => {
        const namespace = shapes.namespace;//看是否有namespace huizi 2023
        if (definedShapes === undefined) {//没有指定载入的shapes，默认载入所有export的变量
            for (let shape in shapes) {
                if (shape === "namespace") continue;
                if (namespace) {
                    self.plugins[`${namespace}.${shape}`] = shapes[shape];
                } else {
                    self.plugins[shape] = shapes[shape];//有待优化，只保留对shape的引用，而不是所有export的变量
                }
            }
        } else {
            definedShapes.forEach(s => self.plugins[s] = shapes[s]);
        }
    }

    self.createShapePath = (type, namespace = ELSA_NAME_SPACE) => {
        return `${namespace}.${type}`;
    };

    /**
     * import plugin shapes
     * 辉子 2021
     */
    self.import = (address, definedShapes, next) => {
        (!next) && (next = createBuilder());
        import(/* webpackIgnore: true */ address).then(shapes => {
            setShapes(shapes, definedShapes);
            next.callback && next.callback();
            next.address && self.import(next.address, next.definedShapes, next.next);
        });
        return next;
    };

    self.dynamicImport = async address => {
        const shapes = await import(/* webpackIgnore: true */ address);
        setShapes(shapes);
    };

    self.dynamicImportStatement = async (importStatement) => {
        const shapes = await importStatement();
        setShapes(shapes);
    };

    let eventHandlers = {};

    self.fireEvent = async (event) => {
        const handlers = eventHandlers[event.type];
        if (handlers) {
            for (let i = 0; i < handlers.length; i++) {
                const handler = handlers[i];
                await handler(event.value);
            }
        }
    };

    /**
     * 添加事件监听器.
     *
     * @param type 事件类型.
     * @param handler 事件监听器.
     */
    self.addEventListener = (type, handler) => {
        !eventHandlers[type] && (eventHandlers[type] = []);
        eventHandlers[type].push(handler);
    };

    /**
     * 移除事件监听器.
     *
     * @param type 事件类型.
     * @param handler 事件监听器.
     */
    self.removeEventListener = (type, handler) => {
        const handlers = eventHandlers[type];
        if (!handlers || handlers.length === 0) {
            return;
        }
        const index = handlers.findIndex(h => h === handler);
        handlers.splice(index, 1);
    };

    //-------------graph data change collaboration------------------------huizi 2022.07.10
    self.setProperty = (field, value, isCoEditing) => {
        if (self[field] !== undefined) {
            self[field] = value;
        } else {
            if (self.setting[field] !== undefined) {
                self.setting[field] = value;
            } else {
                return;
            }
        }
        (!isCoEditing) && self.collaboration.invoke({ method: "change_graph_data", value: { field, value } });
    };

    /**
     * 创建编辑器.
     *
     * @param shape 图形对象.
     * @return #editorInterface 编辑器对象.
     * @abstract 抽象方法.
     */
    self.createEditor = (shape) => {
    };

    /**
     * 理论上，对数据的所有修改都应该走这里进行操作.
     *
     * @param operation 操作.
     */
    self.change = (operation) => {
        if (typeof operation !== "function") {
            throw new Error("operation must be a function.");
        }
        self.getHistory().clearBatchNo();
        operation();
    }

    self.loadConfig = ()=>{
        self.contextMenu = {};
        self.contextMenu.shape = [{
            text: shape => "删除",
            action: shapes => shapes.forEach(s => s.remove()),
            draw: (context) => {
                context.fillStyle = "red";
                context.fillRect(-2, -2, 12, 12);
            }
        }];
    };

    builtInListeners(self);
    return self;
};

/**
 * 添加graph中默认的事件监听事件.
 *
 * @param graph graph对象.
 */
const builtInListeners = (graph) => {
    // 监听contextMenu事件，统一处理followBar创建.
    graph.addEventListener(EVENT_TYPE.CONTEXT_CREATE, shapes => {
        graph.activePage.contextToolbar && graph.activePage.contextToolbar.destroy();
        if (graph.activePage.showContextMenu()) {
            contextMenu(graph.activePage, graph.activePage.getFocusedShapes());
        }
    });

    // 统一处理图形添加的事件，创建图形添加的command
    graph.addEventListener(EVENT_TYPE.SHAPE_ADDED, shapes => {
        let commandShapes = [];
        shapes.map(s => commandShapes.push({shape: s}));
        addCommand(graph.activePage, commandShapes);
    });
};

/**
 * default setting from graph
 */
const defaultSetting = {
    borderColor: "steelblue",
    backColor: "whitesmoke",
    headColor: "steelblue",
    fontColor: "steelblue",
    captionfontColor: "whitesmoke",
    fontFace: "arial",
    captionfontFace: "arial black",
    fontSize: 12,
    captionfontSize: 14,
    fontStyle: FONT_STYLE.NORMAL,
    captionfontStyle: FONT_STYLE.NORMAL,
    fontWeight: FONT_WEIGHT.LIGHTER,
    captionfontWeight: FONT_WEIGHT.LIGHTER,
    hAlign: ALIGN.MIDDLE,
    vAlign: ALIGN.TOP,
    captionhAlign: ALIGN.MIDDLE,
    lineHeight: 1.5,
    lineWidth: 2,
    captionlineHeight: 1,
    focusMargin: 0,
    focusBorderColor: "darkorange",
    focusFontColor: "darkorange",
    focusBackColor: "whitesmoke",
    mouseInColor: "orange",
    mouseInBorderColor: "orange",
    mouseInFontColor: "orange",
    mouseInBackColor: "whitesmoke",
    borderWidth: 1,
    focusBorderWidth: 1,
    globalAlpha: 1,
    backAlpha: 0.15,
    cornerRadius: 4,
    dashWidth: 0,
    autoText: false,
    autoHeight: false,
    autoWidth: false,
    margin: 25,
    pad: 10,
    code: "",
    rotateDegree: 0,
    shadow: "",
    focusShadow: "",
    shadowData: "2px 2px 4px",
    outstanding: false,
    pDock: PARENT_DOCK_MODE.NONE,
    dockMode: DOCK_MODE.NONE,
    priority: 0,
    infoType: INFO_TYPE.NONE,
    progressStatus: PROGRESS_STATUS.NONE,
    progressPercent: 0.65,
    showedProgress: false,
    itemPad: [5, 5, 5, 5],
    itemScroll: { x: 0, y: 0 },
    scrollLock: { x: false, y: false },
    resizeable: true,
    selectable: true,
    rotateAble: true,
    editable: true,
    moveable: true,
    dragable: true,
    visible: true,
    deletable: true,
    allowLink: true,//允许line链接
    shared: false,//是否被不同的page引用
    strikethrough: false,
    underline: false,
    numberedList: false,
    bulletedList: false,
    enableAnimation: false,
    enableSocial: true,
    emphasized: false,
    bulletSpeed: 1,
    tag: {}//其他任何信息都可以序列化后放在这里
}

const auxiliaryToolConfig = {
    enableGuides: false // 开启智能参考线
};

export { graph };