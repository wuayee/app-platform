import React, {useEffect, useState} from 'react';
import { Tree, Input, Button, Modal, Form, Menu, Dropdown } from 'antd';
import { PlusOutlined, BulbOutlined, PlusCircleOutlined, EditOutlined, DeleteOutlined, EllipsisOutlined} from '@ant-design/icons';
import {uuid} from "../../../common/utils";
import {Message} from "@shared/utils/message";

const TreeComponent = (props) => {
    const [treeData, setTreeData] = useState([]);
    const [editingId, setEditingId] = useState(null);
    const [expandedKeys, setExpandedKeys] = useState(['']);

    const handleTreeData = (data) => {
        const newData = data.map(node => {
            if (node.children.length) {
                handleTreeData(node.children);
            }
            return node;
        });
        setTreeData(newData);
    }

    const handleExpand = (value) => {
        setExpandedKeys(value);
    }

    useEffect(() => {
        setTreeData(props.tree);
    }, [props.tree])

    const titleRender = (node) => {
        const {id, title, children, parent} = node;

        const handleDeleteNode = (treeData, parentNode, id) => {
            if (treeData.children.length) {
                treeData.children = treeData.children.map((childrenNode) => {
                    if (childrenNode.id === parentNode && childrenNode.children.length === 1) {
                        return {
                            ...childrenNode,
                            children: []
                        }
                    } else if (childrenNode.id === id) {
                        return null;
                    } else {
                        return handleDeleteNode(childrenNode, parentNode, id);
                    }
                });
            }
            return treeData;
        };

        const handleDelete = (parentNode, id) => {
            const newTreeData = treeData.map((data) => {
                if (data.id === parentNode && data.children.length === 1) {
                    return {
                        ...data,
                        children: []
                    }
                } else if (data.id === id) {
                    return null;
                } else {
                    const updateNode = handleDeleteNode(data, parentNode, id);
                    return updateNode;
                }
            }).filter(Boolean);
            setTreeData(newTreeData);
            props.updateTreeData(newTreeData);
        };

        const editTag = (e, id) => {
            setEditingId(id);
        }

        const handleTagBlur = (e) => {
            let {value} = e.target;
            value = value.trim();
            if (value === "") {
                if (id === "key") {
                    Message({type: "error", content: "分类名称不能为空"});
                    handleDelete(id);
                } else {
                    Message({type: "error", content: "分类名称不能为空"});
                }
            } else {
                if (id === "key") {
                    const nodeId = uuid();
                    node.id = nodeId;
                    node.parent = node.parent + ":" + nodeId;
                }
                node.title = value;
            }
            setTreeData([...treeData]);
            setEditingId(null);
            props.updateTreeData([...treeData]);
        }

        const addTag = () => {
            if (hasInspiration(id)) {
                Message({type: "error", content: "分类下已有灵感大全，请先删除灵感大全"});
                return;
            }
            let newNode = {id: "key", title: "新建分类", children: [], parent: id};
            setEditingId(newNode.id);
            setExpandedKeys([...expandedKeys, id])
            node.children.push(newNode);
            setTreeData([...treeData]);
            props.updateTreeData([...treeData]);
        }

        const deleteNode = (parent) => {
            const [parentNode, id] = parent.split(":");
            if (children.length) {
                Message({type: "error", content: "删除不可逆，请先删除子元素"});
            } else if (hasInspiration(id)) {
                Message({type: "error", content: "分类下已有灵感大全，请先删除灵感大全"});
            } else {
                handleDelete(parentNode, id);
            }
        }

        const hasInspiration = (value) => {
            return props.nodeList.includes(value);
        }

        const handleDoubleClick = () => {
            setEditingId(id);
        }

        const dropItems = [
            {key:"add", icon: <PlusCircleOutlined />, onClick: (e) => addTag(), label: "添加分类"},
            {key:"edit", icon: <EditOutlined />, onClick: (e) => editTag(e, id), label:"编辑分类"},
            {key: "delete", icon: <DeleteOutlined />, onClick: (e) => deleteNode(parent), label: "删除"},
        ];

        const dropMenu = (
            <Menu items={dropItems} />
        );

        return (
            <div>
                {node.id === editingId ? (
                    <Input
                        autoFocus
                        defaultValue={title}
                        onBlur={handleTagBlur}
                        onPressEnter={handleTagBlur}
                    />
                ) : (
                    <>
                        <span onDoubleClick={handleDoubleClick}>{node.title}</span>
                        <Dropdown overlay={dropMenu} trigger={['click']} placement="bottomRight">
                            <Button type="text" icon={<EllipsisOutlined />}/>
                        </Dropdown>
                    </>
                )}
            </div>
        );
    };

    const createTagTree = () => {
        const id = uuid();
        const newTree = {
            id,
            title: "新建分类",
            children: [],
            parent: "root:" + id,
        }
        setEditingId(id);
        setTreeData([...treeData, newTree]);
        props.updateTreeData([...treeData, newTree]);
    }

    return (
        <>
            <div style={{overflow: "auto", height: "50vh"}}>
                <div>
                    <Button type="link" onClick={createTagTree}>
                        <PlusOutlined />
                        创建分类
                    </Button>
                </div>
                <Tree
                    treeData={treeData}
                    titleRender={titleRender}
                    fieldNames={{
                        title:"title",
                        key:"id"
                    }}
                    defaultExpandAll={true}
                    expandedKeys={expandedKeys}
                    onExpand={handleExpand}
                    showLine
                />
            </div>
        </>
    );
};

export default TreeComponent;
