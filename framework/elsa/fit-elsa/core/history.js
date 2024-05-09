import { EVENT_TYPE, PAGE_MODE } from "../common/const.js";
import { uuid } from "../common/util.js";

/**
 * 历史变更记录基类，两种实现
 * 1.基于command实现
 * 2.基于序列化后有改变实现
 */
const commandHistory = (strategy, graph) => {
    switch (strategy) {
        case "graph":
            return graphCommandHistory(graph);
        case "page":
            return pageCommandHistory(graph);
    }
}

/**
 * graph級別全局历史管理
 * @type {function(...[*]=)}
 */
const graphCommandHistory = (() => {
    let history = {};
    return (graph) => {
        if (history[graph.id]) {
            return history[graph.id];
        }
        const self = {};
        history[graph.id] = self;
        self.cursor = -1;
        self.commands = [];

        self.getUndoCommand = () => {
            return self.commands[self.cursor];
        }

        self.getRedoCommand = () => {
            return self.commands[self.cursor];
        }

        /**
         * ctrl-z
         */
        self.undo = async () => {
            if (!self.canUndo()) {
                return;
            }

            graph.inUndo = true;
            try {
                for (let command = self.getUndoCommand(), serialNo = command.batchNo; command && serialNo === command.batchNo; command = self.getUndoCommand()) {
                    const h = command.host;
                    if (h.isPage) {
                        if (h.id !== graph.activePage.id) {
                            /*
                             * 若当前撤销的图形所在页面不是当前激活状态页面，则需先激活图形所在页面（考虑合理性 graph.edit）
                             *
                             * 这里需要变成同步，否则下列情况会出现问题：
                             * 1、页面1中ctrl+x剪切图形并粘贴
                             * 2、切换到页面2
                             * 3、撤销，回到页面1
                             * 4、主画布中图形存在，缩略图中图形不存在
                             * 像这种涉及到页面切换的，并且需要进行invalidateAlone的操作都存在该问题。原因如下：
                             * 若未await，那么在执行到page.take方法时，会调用ignoreInvalidateAsync，里面会把disableInvalidate
                             * 设置为true，此时就不会执行后续了，会回到这里执行command.undo，若undo里面有invalidateAlone()相关操作，
                             * 由于disableInvalidate为true，就不会执行，导致出问题.
                             *
                             * * 注意 *
                             * 这里有一个疑惑的地方，设置属性等操作，在发生切换页面的时候，撤销是没问题的.
                             * 原因是因为在没加await的时候，先执行command，再执行page.take()中的page.active()方法，会调用图形的
                             * invalidateAlone()方法.而删除比较特殊，由于在command中，已经将container设置为空字符串了，所以在active
                             * 执行时，是获取不到该图形对象的，也就无法执行其invalidateAlone了.
                             */
                            await graph.fireEvent({type: EVENT_TYPE.FOCUSED_PAGE_CHANGE, value: h.id});
                        }
                        command.undo(graph.activePage);
                    } else {
                        command.undo(graph);
                    }
                    self.cursor--;
                }
            } finally {
                delete graph.inUndo;
            }
        };

        self.canUndo = () => {
            return self.cursor >= 0;
        }

        /**
         * ctrl-shit-z
         */
        self.redo = async () => {
            if (!self.canRedo()) {
                return;
            }

            graph.inRedo = true;
            self.cursor++;
            try {
                for (let command = self.getRedoCommand(), serialNo = command.batchNo; command && serialNo === command.batchNo; command = self.getRedoCommand()) {
                    self.cursor++;
                    const h = command.host;
                    if (h.isPage) {
                        if (h.id !== graph.activePage.id) {
                            // 若当前撤销的图形所在页面不是当前激活状态页面，则需先激活图形所在页面（考虑合理性 graph.edit）
                            await graph.fireEvent({type: EVENT_TYPE.FOCUSED_PAGE_CHANGE, value: h.id});
                        }
                        command.redo(graph.activePage);
                    } else {
                        command.redo(graph);
                    }
                }
            } catch (e) {
                console.warn(e);
            } finally {
                delete graph.inRedo;
                self.cursor -= 1;
            }
        };

        self.canRedo = () => {
            return self.cursor < self.commands.length - 1;
        }

        self.addCommand = (command, host) => {
            if (command.host.isPage && !host.enableHistory()) {
                return;
            }
            while (self.commands.length > self.cursor + 1) self.commands.pop();

            // 批次号不存在，则重新生成.并且设置command的batchNo.
            !self.batchNo && (self.batchNo = uuid());
            command.batchNo = self.batchNo;

            self.commands.push(command);
            self.cursor = self.commands.length - 1;
            console.log("new command created: " + command.type);
        };

        /**
         * 清理批次号.批次号用于确定哪些操作command应该被合并.
         */
        self.clearBatchNo = () => {
            self.batchNo && (self.batchNo = null);
        };

        /**
         * 删除最后一个指令.
         *
         * @param type command类型.
         */
        self.removeLastCommand = (type = undefined) => {
            if (type) {
                const index = self.commands.findLastIndex(c => c.type === type);
                if (index !== -1) {
                    self.commands.splice(index, 1);
                }
            } else {
                self.commands.splice(self.commands.length - 1, 1);
            }

            self.cursor = self.commands.length - 1;
        };

        return self;
    }
})();

/**
 * 单页面级别历史管理
 */
const pageCommandHistory = (graph) => {
    const histories = {};
    const self = commandHistory(graph);
    self.getUndoCommand = (host) => {
        const history = histories[host.id];
        return history.commands[history.cursor--];
    }

    self.getRedoCommand = (host) => {
        const history = histories[host.id];
        return history.commands[++history.cursor];
    }

    self.canRedo = (host) => {
        const history = histories[host.id];
        return history.cursor < history.commands.length - 1;
    }

    self.canUndo = (host) => {
        return histories[host.id].cursor >= 0;
    }

    self.addCommand = (command, h) => {
        if (h.isTypeof && h.isTypeof("page") && h.enableHistory()) {
            return;
        }

        const host = command.host;
        let redoUndoChange = self.canRedo(host) || !self.canUndo(host);
        const history = histories[host.id] ? histories[host.id] : { cursor: -1, commands: [] };
        if (history.cursor > -1) {
            while (history.commands.length > history.cursor + 1) history.commands.pop();
        }
        history.commands.push(command);
        history.cursor = history.commands.length - 1;
        if (!host.isTypeof || !host.isTypeof("page")) {
            return;
        }
        if (!redoUndoChange) {
            return;
        }
        host.triggerEvent({
            type: EVENT_TYPE.PAGE_HISTORY, value: {
                page: host.id, undo: true, redo: false
            }
        })
    }
}

/**
 * 记录图形编辑过程中的改动，提供redo，undo动作
 * history没有采用command模式记录每个模式，而是每秒中比较一次page.serialize后的数据是否相同，如果不同，则记录一次变化
 * 比较serialize之前判断page.isDirty确定page是否有编辑变动
 * shape.dirty精确的表示了哪个shape有了改动，history会把这些变化通过handleDiryPage，handleDirtyShape方法做存储或者分布式协同
 * 辉子 2020
 */
// let serializedHistory = page => {
//     let self = {};
//     let cursor = -1;
//     let records = [];
//     let lastTime = new Date();
//     const saveInterval = 1000;

//     /**
//      * 在page.rest中初始化第一次history
//      * 如果没有init，页面编辑过程中会丢失第一次编辑改动
//      * 辉子 2021
//      */
//     self.init = () => {
//         records = [{ data: page.serialize(), shapes: [] }];
//         cursor = 0;
//         console.log("trace is initialized...");
//     };

//     /**
//      * 一次记录动作
//      * 记录动作有animation触发
//      * 辉子 2021
//      */
//     self.record = async function () {
//         if (self.muting) {
//             return;
//         }
//         if ((((new Date()).getTime() - lastTime.getTime()) < saveInterval) || page.disableReact || page.mode !== PAGE_MODE.CONFIGURATION) {
//             return;
//         }
//         recordPage();
//         recordGraph();
//     };
//     /**
//      * 存储最新一次的snapshot
//      */
//     self.lastSerialized;

//     /**
//      * 记录页面信息相关的变动
//      * 包括page变动以及里面shape的变动
//      * 辉子 2020
//      * update 2021
//      */
//     let recordPage = () => {
//         if (!page.isReady || page.isMouseDown() || page.isKeyDown || !page.isDirty()) {
//             return;
//         }

//         let serialized = page.serialize();
//         if (JSON.stringify(self.lastSerialized) === JSON.stringify(serialized)) {
//             return;
//         }
//         self.lastSerialized = serialized;

//         if (cursor > -1) {
//             while (records.length > cursor + 1) records.pop();
//         }
//         //精确确定改动点，发送变更通知，以处理persistence和协同显示
//         let dirties = page.serializeDirties();
//         self.handleDirtyShapes(dirties);
//         page.clearDirty();
//         page.dirtied(dirties);

//         records.push({ data: serialized, shapes: dirties });
//         cursor = records.length - 1;
//         lastTime = new Date();
//         console.log("page is dirty...");
//     };

//     /**
//      * 记录graph的变动信息
//      * 包括page增删和顺序变动
//      * 不包含page的改信息
//      * 辉子 2021
//      */
//     let recordGraph = () => {
//         if (!page.graph.dirty) {
//             return;
//         }
//         self.handleDirtyGrah(page.graph.serialize());
//         page.graph.dirty = false;
//         page.graph.dirtied();
//     };

//     /**
//      * 每次有改变生成一个缩略图，供尾部事件捕捉
//      * 辉子 2021
//      */
//     self.handlePageThumb = async function (image) {
//     };
//     /**
//      * 用户或者二次开发选择精确存储或者协同每一个修改过的形状
//      * 精确处理对系统性能好，但处理相对复杂
//      * 辉子 2021
//      */
//     self.handleDirtyShapes = shapesData => {

//     };
//     /**
//      * 用户或者二次开发选择一次性存储或者协同整个page数据
//      * 一次性传输或者存储方案比较简单，但数据量稍大，性能稍差
//      * 辉子 2021
//      */
//     self.handleDirtyPage = data => {
//         //todo:console.log("page "+data.id+" is dirty....");
//     };

//     self.handleDirtyGrah = data => {
//         //todo:console.log("graph is dirty....");
//     };

//     const undoOrRedo = (cursor, hint) => {
//         self.muting = true;
//         page.deSerialize(records[cursor].data);
//         page.invalidate();
//         records[cursor].data = page.serialize();
//         page.clearDirty();
//         page.dirtied();
//         console.log(hint + cursor);
//         self.muting = false;
//     }
//     const undoOrRedoDirties = shapes => {

//     };

//     /**
//      * ctrl-z
//      * 跟word一样，不解释
//      * huiz 2020
//      */
//     self.undo = () => {
//         if (cursor <= 0) {
//             return;
//         }
//         cursor--;
//         undoOrRedo(cursor, "page is undoing: ");
//         undoOrRedoDirties(records[cursor + 1].shapes);
//     };

//     /**
//      * ctrl-shit-z
//      * 同上
//      * 辉子 2020
//      */
//     self.redo = () => {
//         if (cursor >= records.length - 1) {
//             return;
//         }
//         cursor++;
//         undoOrRedo(cursor, "page is redoing: ");
//         //undoOrRedoDirties(records[cursor - 1].shapes);
//     };
//     return self;
// };

export { commandHistory };