import {Tree} from 'antd';
import {v4 as uuidv4} from "uuid";
import TreeSwitcherIcon from "@/components/common/TreeSwitcherIcon.jsx";

const generateTreeData = (data) => {
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
                    children: typeof item === 'object' ? generateTreeData(item) : [],
                })),
            };
        } else {
            title = `${key} {${Object.keys(value).length}}`;
        }

        return {
            title,
            key: uuidv4(),
            children: isLeaf ? [] : generateTreeData(value),
        };
    });
};

const SectionContent = ({data}) => {
    if (typeof data !== 'object') {
        return (<div className={"value-display-area"}>{data}</div>);
    } else {
        const treeData = generateTreeData(data);
        return (<>
            <Tree showLine
                  switcherIcon={({expanded}) => <TreeSwitcherIcon expanded={expanded}/>}
                  defaultExpandAll={false}
                  treeData={treeData}/>
        </>);
    }
};

export default SectionContent;
