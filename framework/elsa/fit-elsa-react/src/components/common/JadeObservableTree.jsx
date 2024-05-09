import {Tree} from "antd";
import {useShapeContext} from "../DefaultRoot.jsx";
import {useEffect, useState} from "react";
import "./jadeObservableTree.css";

/**
 * 可被观察的树装结构.
 *
 * @param data 数据.
 * @return {JSX.Element}
 * @constructor
 */
export const JadeObservableTree = ({data}) => {
    if (!Array.isArray(data)) {
        throw new Error("data must be array.");
    }

    const [treeData, setTreeData] = useState(null);
    const shape = useShapeContext();

    useEffect(() => {
        const treeData = data.map(d => buildNode(d, null));
        setTreeData(treeData);

        // unmount时取消监听.
        return () => {
            if (treeData) {
                traverseTree(treeData, (n) => {
                    shape.page.removeObservable(shape.id, n.key)
                });
            }
        };
    }, []);

    const traverseTree = (nodes, action) => {
        nodes.forEach(n => {
           n.children && traverseTree(n.children, action);
           action(n);
        });
    };

    /**
     * 构建节点，只有Object节点有子孙节点.
     * 构建过程中，逐级注册observable，使后续节点可以进行reference操作.
     *
     * @param nodeData 节点数据.
     * @param parent 父节点数据.
     * @return {{title, isLeaf: boolean, key}|{children: *, title, key}} 树节点.
     */
    const buildNode = (nodeData, parent) => {
        shape.page.registerObservable(shape.id, nodeData.id, nodeData.name, parent ? parent.id : null);
        if (nodeData.type === "Object") {
            return {
                title: nodeData.name,
                type: nodeData.type,
                key: nodeData.id,
                children: nodeData.value.map(v => buildNode(v, nodeData))
            };
        } else {
            return {
                title: nodeData.name, type: nodeData.type, key: nodeData.id, isLeaf: true
            };
        }
    };

    /**
     * 自定义标题展示.
     *
     * @param nodeData 节点数据.
     * @return {JSX.Element} react 组件对象.
     */
    const displayTitle = (nodeData) => {
        return (<>
            <div className="jade-observable-tree-node-div">
                <div style={{display: "flex"}}>
                    <span className="jade-observable-tree-node-title">{nodeData.title}</span>
                    <div className="jade-observable-tree-node-type-div">
                        <span className="jade-observable-tree-node-type-name">{nodeData.type}</span>
                    </div>
                </div>
            </div>
        </>);
    };

    return (<>
        <Tree treeData={treeData} titleRender={(nodeData) => displayTitle(nodeData)}
              showLine={true}
              selectable={false}/>
    </>);
};