import React, {useEffect, useRef, useState} from "react";
import {Button, Dropdown, Form, Input} from "antd";
import "./headerStyle.css";

/**
 * 头部.
 *
 * @param shape 图形.
 * @return {JSX.Element}
 * @constructor
 */
export const Header = ({shape}) => {
    const [edit, setEdit] = useState(false);
    const inputRef = useRef(null);

    useEffect(() => {
        inputRef.current && inputRef.current.focus({
            cursor: 'end'
        });
    });

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
        const m = shape.toolMenus.find(t => t.key === e.key);
        m.action(setEdit);
    };

    /**
     * 获取文本组件，处于编辑态时，需要能修改标题；否则只展示标题.
     *
     * @return {JSX.Element}
     */
    const getTitle = () => {
        if (edit) {
            return (<>
                <Form initialValues={{title: shape.text}}>
                    <Form.Item name="title" rules={[{required: true, message: "请输入名称"}]}>
                        <Input onBlur={(e) => onInputBlur(e)}
                               ref={inputRef}
                               // defaultValue={shape.text}
                               placeholder="请输入名称"
                               style={{height: "24px", borderColor: shape.focusBorderColor}}/>
                    </Form.Item>
                </Form>
            </>);
        } else {
            return <p style={{margin: 0}}><span>{shape.text}</span></p>;
        }
    };

    /**
     * 展示菜单.
     *
     * @return {JSX.Element}
     */
    const showMenus = () => {
        if (shape.toolMenus && shape.toolMenus.length > 0) {
            const menus = shape.toolMenus.map(t => {
                return {key: t.key, label: t.label};
            });
            return (<>
                <div>
                    <Dropdown menu={{items: menus, onClick: (e) => onMenuClick(e)}} placement="bottomRight">
                        <Button type="text" size="small" style={{
                            margin: 0,
                            padding: 0,
                            width: "28px",
                            height: "28px",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center"
                        }}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="none"
                                 viewBox="0 0 16 16">
                                <path fill="#1C1D23" fillOpacity="0.8"
                                      d="M3.667 7.833a1.167 1.167 0 1 1-2.334 0 1.167 1.167 0 0 1 2.334 0ZM9.15 7.833a1.167 1.167 0 1 1-2.333 0 1.167 1.167 0 0 1 2.333 0ZM14.667 7.833a1.167 1.167 0 1 1-2.334 0 1.167 1.167 0 0 1 2.334 0Z"></path>
                            </svg>
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
                    {shape.getHeaderIcon()}
                </div>
                <div className="react-node-toolbar-name">
                    {getTitle()}
                </div>
                {showMenus()}
            </div>
            <span className="react-node-header-description">{shape.description}</span>
        </div>
    </>);
};