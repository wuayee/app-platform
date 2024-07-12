import React, {useEffect, useRef, useState} from "react";
import {Button, Dropdown, Form} from "antd";
import {HEADER_TOOL_MENU_ICON} from "@/components/asserts/svgIcons.jsx";
import "./headerStyle.css";
import {JadeInput} from "@/components/common/JadeInput.jsx";

/**
 * 头部.
 *
 * @param shape 图形.
 * @param disabled 是否禁用.
 * @return {JSX.Element}
 * @constructor
 */
export const Header = ({shape, disabled}) => {
    const [edit, setEdit] = useState(false);
    const inputRef = useRef(null);
    const [, setEndNodeCount] = useState(0);

    useEffect(() => {
        inputRef.current && inputRef.current.focus({
            cursor: 'end'
        });
    });

    useEffect(() => {
        // 节点数量变化
        shape.page.addEventListener("TOOL_MENU_CHANGE", (value) => {
            if (shape.type === "endNodeEnd") {
                setEndNodeCount(value[0])
            }
        });
    }, []);

    const onInputBlur = () => {
        if (inputRef.current.input.value === "") {
            return;
        }
        shape.text = inputRef.current.input.value;
        setEdit(false);
    };

    /**
     * menu点击事件.
     *
     * @param e 事件对象.
     */
    const onMenuClick = (e) => {
        const m = shape.drawer.getToolMenus().find(t => t.key === e.key);
        m.action && m.action(setEdit);
    };

    /**
     * 获取文本组件，处于编辑态时，需要能修改标题；否则只展示标题.
     *
     * @return {JSX.Element} 标题组件.
     */
    const getTitle = () => {
        if (edit) {
            return (<>
                <Form.Item name="title" rules={[{required: true, message: "请输入名称"}]} initialValue={shape.text}>
                    <JadeInput onBlur={(e) => onInputBlur(e)}
                               ref={inputRef}
                               onMouseDown={(e) => e.stopPropagation()}
                               placeholder="请输入名称"
                               style={{height: "24px", borderColor: shape.focusBorderColor}}/>
                </Form.Item>
            </>);
        } else {
            return <p className={"jade-component-title"} style={{margin: 0}}><span>{shape.text}</span></p>;
        }
    };

    const onOpenChange = (openKeys) => {
        shape.drawer.getToolMenus().forEach(m => {
           if (openKeys.includes(m.key)) {
               m.onOpen && m.onOpen();
           }
        });
    };

    /**
     * 获取菜单项.
     *
     * @return {*} 菜单项
     */
    const getMenu = () => {
        const items = shape.drawer.getToolMenus().map(t => {
            const menu = {
                key: t.key,
                label: t.label,
                popupClassName: "react-node-header-menu-sub",
                popupOffset: [10, 0]
            };
            t.children && (menu.children = t.children);
            t.onTitleClick && (menu.onTitleClick = t.onTitleClick);
            return menu;
        });
        return {items, onClick: (e) => onMenuClick(e), onOpenChange, triggerSubMenuAction: "click"};
    };

    /**
     * 展示菜单.
     *
     * @return {JSX.Element}
     */
    const showMenus = () => {
        if (shape.drawer.getToolMenus().length > 0) {
            return (<>
                <div>
                    <Dropdown disabled={disabled} menu={getMenu()} trigger="click" placement="bottomRight">
                        <Button type="text" size="small" className={"react-node-header-button"}>
                            {HEADER_TOOL_MENU_ICON}
                        </Button>
                    </Dropdown>
                </div>
            </>);
        }
        return <></>;
    };

    return (<>
        <div className="react-node-header">
            <div className="react-node-toolbar" style={{alignItems: "center"}}>
                <div style={{display: "flex", alignItems: "center"}}>
                    {shape.drawer.getHeaderIcon()}
                </div>
                <div className="react-node-toolbar-name">
                    {getTitle()}
                    {shape.drawer.getHeaderTypeIcon()}
                </div>
                {showMenus()}
            </div>
            <span className="react-node-header-description">{shape.description}</span>
        </div>
    </>);
};