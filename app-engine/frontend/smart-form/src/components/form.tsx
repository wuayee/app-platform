/*************************************************此处为人工表单示例***************************************************/
/*********************************************data为表单的初始化入参数据***********************************************/
/*******************************************terminateClick为调用终止对话接口的回调方法**********************************/
/**************************************resumingClick为调用继续对话接口的回调方法***************************************/
/***************************************restartClick为调用重新对话接口的回调方法**************************************/
import {Button, Input, Radio, Select, Switch} from 'antd';
import React, {useContext, useEffect, useState} from 'react';
import {DataContext} from "../context";
// import '../styles/form.scss';

const SmartForm = ({onSubmit, onCancel}) => {

    function buildFormSchema(parameters, data) {
        return parameters.map((param) => {
            // renderType 与本 SmartForm 中需要的 type 映射
            let fieldType;
            switch (param.renderType) {
                case 'Input':
                    fieldType = 'input';
                    break;
                case 'Radio':
                    fieldType = 'radio';
                    break;
                case 'Switch':
                    fieldType = 'switch';
                    break;
                case 'Select':
                    fieldType = 'select';
                    break;
                default:
                    fieldType = 'input'; // 默认当做普通输入框
                    break;
            }

            // 若 data 中存在 name + '-options'，则作为可选项
            const dynamicKey = `${param.name}-options`;
            const dynamicOptions = data[dynamicKey] || [];

            return {
                name: param.name,
                label: param.displayName,
                type: fieldType,
                options: dynamicOptions,
            };
        });
    }

    const {data} = useContext(DataContext);

    const [formData, setFormData] = useState({});
    const [buttonsDisabled, setButtonsDisabled] = useState(false);


    // // 初始化表单数据
    useEffect(() => {
        if (!data) return;
        setFormData(data.data);
    }, [data])

    if (!data) return (<div></div>);

    const formSchema = buildFormSchema(data.schema.parameters, formData);

    const handleChange = (name, value) => {
        setFormData((prev) => ({...prev, [name]: value}));
    };

    const renderField = (field) => {
        switch (field.type) {
            case 'input':
                return (
                    <Input
                        style={{width: '100%'}}
                        value={formData[field.name] || ''}
                        onChange={(e) => handleChange(field.name, e.target.value)}
                    />
                );
            case 'radio': {
                const options = field.options || [];
                return (
                    <Radio.Group
                        style={{display: 'flex', flexDirection: 'column'}}
                        value={formData[field.name] || ''}
                        onChange={(e) => handleChange(field.name, e.target.value)}
                    >
                        {options.map((opt) => (
                            <Radio
                                key={opt}
                                value={opt}
                                style={{marginBottom: '8px', marginLeft: '16px'}}
                            >
                                {opt}
                            </Radio>
                        ))}
                    </Radio.Group>
                );
            }
            case 'select': {
                const options = field.options || [];
                return (
                    <Select
                        style={{width: '100%'}}
                        value={formData[field.name] || ''}
                        onChange={(val) => handleChange(field.name, val)}
                    >
                        {options.map((opt) => (
                            <Select.Option key={opt} value={opt}>
                                {opt}
                            </Select.Option>
                        ))}
                    </Select>
                );
            }
            case 'switch':
                return (
                    <Switch
                        checked={formData[field.name] || false}
                        onChange={(checked) => handleChange(field.name, checked)}
                    />
                );
            default:
                return null;
        }
    };

    const submitForm = () => {
        setButtonsDisabled(true);
        // 在提交时过滤掉所有以 '-options' 结尾的 key
        const dataToSubmit = Object.keys(formData).reduce((acc, key) => {
            if (!key.endsWith('-options')) {
                acc[key] = formData[key];
            }
            return acc;
        }, {});

        onSubmit(dataToSubmit);
    };
    const cancelForm = () => {
        setButtonsDisabled(true);
        onCancel();
    };
    return (
        <div style={{maxWidth: '200px', margin: '1rem', padding: '1rem'}}>
            {formSchema.map((field) => (
                <div key={field.name} style={{marginBottom: '1rem'}}>
                    <div style={{marginBottom: 4, fontWeight: 'bold'}}>{field.label}</div>
                    {renderField(field)}
                </div>
            ))}
            <Button.Group style={{marginTop: '1rem', width: '100%', display: 'flex', justifyContent: 'space-between'}}>
                <Button type="primary" disabled={buttonsDisabled} onClick={submitForm}>
                    提交
                </Button>
                <Button type="primary" disabled={buttonsDisabled} onClick={cancelForm}>
                    取消
                </Button>
            </Button.Group>
        </div>
    );
};

export default SmartForm;