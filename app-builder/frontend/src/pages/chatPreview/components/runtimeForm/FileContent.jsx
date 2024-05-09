/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */
import React, {useContext, useState} from 'react';
import { Input, Button, Typography } from 'antd';
import { CommentOutlined } from '@ant-design/icons';
import {AippContext} from "../../../aippIndex/context";
import {saveContent} from "../../../../shared/http/appBuilder";
import styled from "styled-components";
import {uuid} from "../../../../common/utils";
import {Message} from "../../../../shared/utils/message";

const { TextArea } = Input;
const { Text } = Typography;
const FormWrap = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: rgb(37, 43, 58);
    .save-button {
        background-color: rgb(4, 123, 252);
        border-radius: 4px;
        font-size: 14px;
        color: white;
        border-color: white;
        width: 60px;
        height: 32px;
        display: flex;
        justify-content: center;
        align-items: center;
    }
    .thumb {
        width: 100%;
        min-height: 200px;
        background-color: #f5f5f5;
        border: 1px dashed #d9d9d9;
        border-radius: 4px;
        padding: 12px;
        font-size: 14px;
        color: #bfbfbf;
        cursor: text;
        box-sizing: border-box;
        margin: 0 0 10px 0;
    }
`;
const FileContent = ({ data, instanceId, mode }) => {
    const id = "fileContent";
    const {appId, tenantId} = useContext(AippContext);
    const [result, setResult] = useState(() => {
        return data;
    });

    const handleChange = (value) => {
        setResult(value);
    };

    const handleSave = () => {
        saveContent(tenantId, appId, instanceId, {"businessData": {[id]: result}}).then((res) => {
            if (res.code !== 0) {
                Message({ type: 'warning', content: res.msg || '保存失败' });
            }
        })
    }

    return (
        <FormWrap>
            <div style={{pointerEvents: mode === "history" ? "none" : "auto", width: "100%"}}>
                {(result) ? (
                    <TextArea
                        rows={8}
                        value={result}
                        onChange={(e) => handleChange(e.target.value)}
                        style={{ margin: '0 0 10px 0' }}
                    />
                ) : (
                    <div className={"thumb"}>
                        这里展示大模型处理的文件总结内容
                    </div>
                )}

                <Button onClick={handleSave} className="save-button">保存</Button>
            </div>
        </FormWrap>
    );
};

export default FileContent;
