import {useShapeContext} from "@/components/DefaultRoot.jsx";
import {useEffect, useRef} from "react";
import {connector, DIRECTION} from "@fit-elsa/elsa-core";

/**
 * 连接点提供器.
 *
 * @param name 名称，用于区分不同的connector.
 * @param children 子组件.
 * @return {JSX.Element}
 * @constructor
 */
export const ConnectorProvider = ({name, children}) => {
    const shape = useShapeContext();
    const divRef = useRef();

    // 初始化之后，创建connector.
    useEffect(() => {
        const getX = () => {
            const dom = divRef.current;
            const shapeDiv = shape.drawer.parent;
            const rect = dom.getBoundingClientRect();
            const shapeRect = shapeDiv.getBoundingClientRect();
            return (rect.right - shapeRect.left) / shape.page.scaleX;
        };

        const getY = () => {
            const dom = divRef.current;
            const shapeDiv = shape.drawer.parent;
            const rect = dom.getBoundingClientRect();
            const shapeRect = shapeDiv.getBoundingClientRect();
            return (rect.top - shapeRect.top + (rect.bottom - rect.top) / 2) / shape.page.scaleY;
        };

        const c = connector(shape, getX, getY, () => {
                    return {
                        cursor: "crosshair",
                        key: name,
                        color: "white",
                        ax: "x",
                        vector: 1,
                        value: DIRECTION.E
                    };
                },
                s => s.visible,
                () => true,
                () => true,
                () => {},
                () => false
        );
        c.type = "dynamic";
        shape.activeConnector(c);
        c.isSolid = true;
        c.allowToLink = false;
        c.allowFromLink = true;

        return () => {
            // 组件卸载时，删除connector.
            shape.connectors = shape.connectors.filter(c => c.getDirection().key !== name);

            // 组件卸载时，删除connector的同时，删除线.
            deleteLine();
        };
    }, []);

    const deleteLine = () => {
        const lines = shape.page.shapes
                .filter(s => s.fromShape === shape.id || s.toShape === shape.id)
                .filter(l => l.definedFromConnector === name);
        lines.forEach(l => l.remove());
    };

    return (<>
        <div id={"connector-provider-" + shape.id} ref={divRef}>
            {children}
        </div>
    </>);
};