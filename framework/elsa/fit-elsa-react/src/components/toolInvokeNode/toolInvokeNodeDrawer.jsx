import {jadeNodeDrawer} from "@/components/jadeNode.jsx";
import {VersionInfo} from "@/components/toolInvokeNode/VersionInfo.jsx";
import {useEffect, useState} from "react";

/**
 * 工具调用节点绘制器
 *
 * @override
 */
export const toolInvokeNodeDrawer = (shape, div, x, y) => {
    const self = jadeNodeDrawer(shape, div, x, y);

    /**
     * 获取版本信息组件.
     *
     * @return {JSX.Element} 组件对象.
     */
    self.getVersionInfoComponent = () => {
        return <VersionInfoLoader shape={shape} drawer={self}/>
    };

    return self;
};

/**
 * 加载VersionInfo所需数据.
 *
 * @param shape 图形对象.
 * @param drawer 绘制器.
 * @return {JSX.Element} 组件.
 * @constructor
 */
const VersionInfoLoader = ({shape, drawer}) => {
    const [data, setData] = useState({});

    /**
     * 刷新版本信息.
     */
    drawer.refreshVersionInfo = () => {
        shape.fetchVersionInfo((data) => setData(data));
    };

    useEffect(() => {
        shape.fetchVersionInfo((data) => setData(data));
    }, []);

    return (<>
        <VersionInfo versionInfo={data}/>
    </>);
};