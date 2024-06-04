import {Tree} from 'antd';
import {v4 as uuidv4} from "uuid";
import TreeSwitcherIcon from "@/components/common/TreeSwitcherIcon.jsx";
import {useEffect} from "react";

const generateTreeData = (data, isFirstLevel = true) => {
    // 如果数据是字符串或数字，直接返回一个树节点
    if (typeof data === 'string' || typeof data === 'number') {
        return [{
            title: <div style={{wordBreak: "break-all"}}>{data.toString()}</div>,
            key: uuidv4(),
            children: [],
            isFirstLevel
        }];
    }

    // 如果数据是对象（包括数组），进行递归解析
    return Object.keys(data).map(key => {
        const value = data[key];
        const isLeaf = typeof value !== 'object' || value === null;
        let title;

        if (isLeaf) {
            title = <div style={{wordBreak: "break-all"}}>{`${key}: ${value}`}</div>;
        } else if (Array.isArray(value)) {
            title = `${key} [${value.length}]`;
            return {
                title,
                key: uuidv4(),
                children: value.map((item, index) => ({
                    title: typeof item === 'object' ? `${index} {${Object.keys(item).length}}` : `${index} ${item}`,
                    key: uuidv4(),
                    children: typeof item === 'object' ? generateTreeData(item, false) : [],
                    isFirstLevel: false
                })),
                isFirstLevel
            };
        } else {
            title = `${key} {${Object.keys(value).length}}`;
        }

        return {
            title,
            key: uuidv4(),
            children: isLeaf ? [] : generateTreeData(value, false),
            isFirstLevel
        };
    });
};

/**
 * 内容展示区域
 *
 * @param data 数据
 * @return {JSX.Element}
 * @constructor
 */
const SectionContent = ({data}) => {
    const treeData = generateTreeData(data);

    useEffect(() => {
        document.querySelectorAll('.first-level').forEach(firstLevel => {
            const wrapper = firstLevel.closest('.ant-tree-title').closest('.ant-tree-node-content-wrapper'); // 根据实际的父元素类名修改
            if (wrapper) {
                const switcher = wrapper.previousElementSibling; // 选择紧邻的前一个兄弟元素
                if (switcher && switcher.classList.contains('ant-tree-switcher')) {
                    const leafLine = switcher.querySelector('.ant-tree-switcher-leaf-line');
                    if (leafLine) {
                        leafLine.style.display = 'none'; // 设置样式为 display: none;
                    }
                }
            }
        });
    }, [treeData]);

    return (<>
        <Tree showLine={{showLeafIcon: false}}
              switcherIcon={({expanded}) => <TreeSwitcherIcon expanded={expanded}/>}
              defaultExpandAll={false}
              treeData={treeData}
              titleRender={(nodeData) => {
                  const className = nodeData.isFirstLevel && nodeData.children.length === 0 ? "first-level" : "not-first";
                  return <span className={className}>{nodeData.title}</span>;
              }}
        />
    </>);
};

export default SectionContent;
