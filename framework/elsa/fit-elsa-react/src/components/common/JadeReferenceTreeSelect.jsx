import {Form, Popover, TreeSelect} from "antd";
import {useFormContext, useShapeContext} from "@/components/DefaultRoot.jsx";
import {useEffect, useRef, useState} from "react";
import {VIRTUAL_CONTEXT_NODE} from "@/common/Consts.js";
import {InfoCircleOutlined} from '@ant-design/icons';
import PropTypes from "prop-types";
import { useTranslation, Trans } from "react-i18next";

/**
 * jade的带引用功能的级联选择框.
 *
 * @param props 参数
 * @return {JSX.Element}
 * @constructor
 */
export const JadeReferenceTreeSelect = (props) => {
    const {reference, onReferencedValueChange, onReferencedKeyChange, rules, className, ...rest} = props;
    const shape = useShapeContext();
    const { t } = useTranslation();
    const form = useFormContext();
    const stopObserve = useRef(null);
    const [treeData, setTreeData] = useState([]);
    const name = `reference-${reference.id}`;
    // 合并外部传入的 className 和内部定义的 className
    const combinedClassName = `jade-tree-select ${className || ''}`.trim();
    // 后续可能需要根据新的剩余的宽度，动态设置: 100 - rest.level * 20
    const minWidth = '100px';

    /**
     * 当选择发生变化时，触发.监听不同的组件.
     *
     * @param selected 是个数组，包含从根节点到被选中节点的所有节点数据.
     */
    const onChange = (selected) => {
        if (!selected) {
            return;
        }
        const treeMap = new Map(treeData.map(d => [d.id, d]));
        const treeNode = treeMap.get(selected);
        const value = buildValue(treeMap, treeNode);
        form.setFieldsValue({[name]: treeNode.title});
        onReferencedKeyChange({
            referenceNode: treeNode.nId,
            referenceId: treeNode.value,
            value,
            type: treeNode.type,
            referenceKey: treeNode.title
        });
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

    const content = (<>
        <div className={"jade-font-size"} style={{lineHeight: "1.2"}}>
            <p>每次对话生成的上下文，包含应用及对话实例相关的系统属性，只读。属性列表：</p>
            <p>1.instanceId：每次对话实例的唯一标识</p>
            <p>2.appId：所属应用的唯一标识</p>
            <p>3.memories：所属应用的历史记录QA对列表</p>
            <p>4.useMemory：表示本次对话是否使用历史记录</p>
            <p>5.userId：用户的唯一标识</p>
            <p>6.fileUrl：表示本次对话开始传入的图片、文档、音频等等的下载地址</p>
            <p>7.chatId：所属会话的唯一标识</p>
        </div>
    </>);

    /**
     * 当dropdown显示时触发，实时获取tree数据.
     */
    const onDropdownVisibleChange = () => {
        const nodeInfos = shape.getPreReferenceNodeInfos().filter(n => n.observableList.length > 0);

        const getTitle = (nodeInfo) => {
            return nodeInfo.id === VIRTUAL_CONTEXT_NODE.id ? <>
                <div className={"jade-font-size"}>
                    <span>{nodeInfo.name}</span>
                    <Popover className={"jade-drop-down-popover"} overlayClassName="jade-drop-down-popover-overlay" content={content}>
                        <InfoCircleOutlined/>
                    </Popover>
                </div>
            </> : nodeInfo.name;
        };

        const simpleTree = nodeInfos.map(n => {
            const ans = [];
            ans.push({
                id: n.id,
                pId: 0,
                value: n.id,
                title: getTitle(n),
                selectable: false
            });
            n.observableList.forEach(o => {
                if (!o.parentId) {
                    o.parentId = n.id;
                }
                const treeNode = {
                    nId: n.id,
                    id: o.observableId,
                    pId: o.parentId,
                    value: o.observableId,
                    title: o.value,
                    selectable: o.selectable ?? true,
                    type: o.type
                };
                ans.push(treeNode);
            });

            return ans;
        }).flatMap(n => n);

        setTreeData(simpleTree);
    };

    // 组件初始化后，若存在引用关系，则进行监听.
    useEffect(() => {
        // 当组件unmount时，停止监听.
        return () => {
            stopObserve.current && stopObserve.current();
        };
    }, []);

    // reference变化需要重新监听.
    useEffect(() => {
        stopObserve.current && stopObserve.current();
        if (reference.referenceNode && reference.referenceId) {
            stopObserve.current = shape.observeTo(reference.id,
                reference.referenceNode,
                reference.referenceId,
                (args) => _onReferenceValueChange(args.value, args.type));
        }
        form.setFieldsValue({[name]: reference.referenceKey});
    }, [reference.referenceNode, reference.referenceId, reference.referenceKey, rest.disabled]);

    /*
     * 当Form.Item设置了name之后，只能通过form.setFieldValue设置value的值.
     */
    const _onReferenceValueChange = (v, t) => {
        form.setFieldsValue({[name]: v});
        onReferencedValueChange(v, t);
    };

    return (<>
        <Form.Item
                id={`reference-id-${reference.id}`}
                name={[name]}
                initialValue={reference.referenceKey}
                rules={rules}
                validateTrigger="onBlur"
        >
            <TreeSelect
                    {...rest}
                    className={combinedClassName}
                    treeDataSimpleMode
                    dropdownStyle={{maxHeight: 400, overflow: 'auto', minWidth: 250}}
                    placeholder={t('pleaseSelect')}
                    onChange={onChange}
                    treeData={treeData}
                    onDropdownVisibleChange={onDropdownVisibleChange}
                    treeDefaultExpandAll={false}
                    style={{minWidth: `${minWidth}`}}
            />
        </Form.Item>
    </>);
};

JadeReferenceTreeSelect.propTypes = {
    reference: PropTypes.object,
    onReferencedValueChange: PropTypes.func,
    onReferencedKeyChange: PropTypes.func,
    rules: PropTypes.array,
    className: PropTypes.string
};