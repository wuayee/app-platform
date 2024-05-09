import {TreeSelect} from "antd";
import {useShapeContext} from "@/components/DefaultRoot.jsx";
import {useEffect, useRef, useState} from "react";

/**
 * jade的带引用功能的级联选择框.
 *
 * @param reference 引用数据.
 * @param onReferencedValueChange 当引用组件的值发生变化时触发.
 * @param onReferencedKeyChange 当引用的key发生变化时触发.
 * @param rest 其他参数.
 * @return {JSX.Element}
 * @constructor
 */
export const JadeReferenceTreeSelect = ({reference, onReferencedValueChange, onReferencedKeyChange, ...rest}) => {
    const shape = useShapeContext();
    const stopObserve = useRef(null);
    const [treeData, setTreeData] = useState([]);

    /**
     * 当选择发生变化时，触发.监听不同的组件.
     *
     * @param selected 是个数组，包含从根节点到被选中节点的所有节点数据.
     */
    const onChange = (selected) => {
        if (!selected) {
            return;
        }

        // 若之前已有监听，则取消之前的监听.
        if (stopObserve.current) {
            stopObserve.current();
        }

        const treeMap = new Map(treeData.map(d => [d.id, d]));
        const treeNode = treeMap.get(selected);
        const value = buildValue(treeMap, treeNode);
        stopObserve.current = shape.observeTo(treeNode.nId, treeNode.value, onReferencedValueChange);
        onReferencedKeyChange({referenceNode: treeNode.nId, referenceId: treeNode.value, value});
    };

    const buildValue = (observableMap, o) => {
        const ans = [];
        ans.unshift(o.title);

        let parentId = o.pId;
        while (parentId) {
            const parent = observableMap.get(parentId);
            if (!parent || parent.pId === 0) {
                break;
            }

            ans.unshift(parent.title);
            parentId = parent.pId;
        }

        return ans;
    };

    /**
     * 当dropdown显示时触发，实时获取tree数据.
     */
    const onDropdownVisibleChange = () => {
        const nodeInfos = shape.getPreNodeInfos();
        const simpleTree = nodeInfos.map(n => {
            const ans = [];
            ans.push({id: n.id, pId: 0, value: n.id, title: n.name, selectable: false});
            n.observableList.forEach(o => {
                if (!o.parentId) {
                    o.parentId = n.id;
                }
                const treeNode = {
                    nId: n.id,
                    id: o.observableId,
                    pId: o.parentId,
                    value: o.observableId,
                    title: o.value
                };
                ans.push(treeNode);
            });

            return ans;
        }).flatMap(n => n);

        setTreeData(simpleTree);
    };

    // 组件初始化后，若存在引用关系，则进行监听.
    useEffect(() => {
        if (reference.referenceNode && reference.referenceId) {
            stopObserve.current = shape.observeTo(reference.referenceNode, reference.referenceId, onReferencedValueChange);
        }

        // 当组件unmount时，停止监听.
        return () => {
            stopObserve.current && stopObserve.current();
        };
    }, []);

    return (<>
        <TreeSelect
                {...rest}
                treeDataSimpleMode
                style={{fontSize: "12px", width: '100%'}}
                value={reference.referenceKey}
                dropdownStyle={{maxHeight: 400, overflow: 'auto', minWidth: 250}}
                placeholder="请选择"
                onChange={onChange}
                treeData={treeData}
                onDropdownVisibleChange={onDropdownVisibleChange}
                treeDefaultExpandAll={false}
        />
    </>);
};