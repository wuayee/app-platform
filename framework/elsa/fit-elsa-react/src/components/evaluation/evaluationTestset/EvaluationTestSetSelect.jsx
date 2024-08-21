import {Button, Form, message} from "antd";
import {JadeStopPropagationSelect} from "@/components/common/JadeStopPropagationSelect.jsx";
import {useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import ArrayUtil from "@/components/util/ArrayUtil.js";
import React from "react";
import {PlusCircleOutlined} from "@ant-design/icons";
import httpUtil from "@/components/util/httpUtil.jsx";
import PropTypes from "prop-types";

_EvaluationTestSetSelect.propTypes = {
    testSets: PropTypes.array,
    disabled: PropTypes.bool,
    selectedTestSet: PropTypes.object,
    config: PropTypes.object
};

/**
 * 测试集选择组件
 *
 * @param testSets 接口获取的数据集列表
 * @param disabled 是否禁用
 * @param selectedTestSet 选择的测试集
 * @param config 组件配置信息，外部设置
 * @return {JSX.Element} 测试集选择组件
 * @constructor
 */
function _EvaluationTestSetSelect({testSets, disabled, selectedTestSet, config}) {
    const dispatch = useDispatch();
    const shape = useShapeContext();

    /**
     * 将返回的测试集元数据信息转换为select组件可以展示的选项
     *
     * @param dataList 测试集描述信息
     */
    const getTestSetOptions = (dataList) => {
        return dataList.map(data => {
            return {
                value: data.id,
                label: data.name
            }
        });
    };

    /**
     * 切换测试集
     *
     * @param value 算法唯一名称
     */
    const handleAlgorithmChange = (value) => {
        const newSelectedTestSet = testSets.find(item => item.id === value);
        dispatch({type: "changeTestSet", value: newSelectedTestSet});
    };

    const filterOption = (input, option) =>
        (option?.label ?? '').toLowerCase().includes(input.toLowerCase());

    /**
     * 清空选项
     */
    const handleKnowledgeClear = () => {
        dispatch({type: "clearTestSet"});
    };

    /**
     * 触发弹出新建测试集弹窗页面
     *
     * @param e 点击事件
     */
    const clickCreateButton = (e) => {
        e.preventDefault();
        shape.page.triggerEvent({
            type: "CREATE_TEST_SET",
            value: {}
        });
    };

    /**
     * 选择下拉框选项时调用接口
     *
     * @param value 选中的值
     * @return {Promise<void>}
     */
    const handleSelect = (value) => {
        // 点击同一个选项，只会调用这里
        // 如果切换选项，会先调用onChange，再调用到这里
        const url = config.datasetUrlPrefix + 'dataset/' + value;
        httpUtil.get(url, undefined, (response) => {
            // 将数据集的content内容设置到jadeConfig中
            dispatch({type: "parseDataset", value: response.data});
        }, (error) => {
            message.error("数据集详细信息获取失败，请联系系统管理员");
        });
    };

    return (<>
        <div className={"jade-common-content"}>
            <Form.Item
                id={`jade-form-item-evaluation-${shape.id}`}
                className="jade-form-item"
                label="测试集名称"
                name={`jade-form-item-evaluation-${shape.id}`}
                rules={[{required: true, message: "测试集不能为空"}]}
                validateTrigger="onBlur"
            >
                <JadeStopPropagationSelect
                    disabled={disabled}
                    onClear={() => handleKnowledgeClear()}
                    showSearch
                    allowClear
                    onSelect={handleSelect} // 在选择选项时调用后端接口
                    placeholder="请选择测试集"
                    className="jade-select"
                    filterOption={filterOption}
                    optionFilterProp="label"
                    value={selectedTestSet}
                    onChange={(value) => handleAlgorithmChange(value)}
                    options={getTestSetOptions(testSets)}
                />
            </Form.Item>
            <Button
                type="link"
                className={"button-create"}
                disabled={disabled}
                onClick={e => clickCreateButton(e)}>
                <div className={"button-create-wrapper"}><PlusCircleOutlined/>
                    <div className={"create-button-text"}>新建测试集</div>
                </div>
            </Button>
        </div>
    </>);
}

const areEqual = (prevProps, nextProps) => {
    return prevProps.disabled === nextProps.disabled
        && ArrayUtil.isEqual(prevProps.testSets, nextProps.testSets)
        && prevProps.selectedTestSet === nextProps.selectedTestSet
        && prevProps.config === nextProps.config;
};

export const EvaluationTestSetSelect = React.memo(_EvaluationTestSetSelect, areEqual);
