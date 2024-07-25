import {JadeJsonTree} from "@/components/common/json/JadeJsonTree.jsx";

/**
 * json viewer.
 *
 * @param data json数据.
 * @return {JSX.Element} 组件对象.
 * @constructor
 */
export const JsonViewer = ({jsonData}) => {
    return (<>
        <div style={{padding: "10px 20px"}}>
            <JadeJsonTree jsonObj={jsonData}/>
        </div>
    </>);
};