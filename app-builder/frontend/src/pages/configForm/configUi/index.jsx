import React, {useEffect, useState, useContext, useRef, useCallback} from 'react';
import {DownOutlined, UpOutlined, PlusOutlined, DeleteOutlined, EllipsisOutlined, QuestionCircleOutlined, EyeOutlined} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import {Form, Select, Slider, Col, Row, InputNumber, Input, Modal, Switch, Table, Button, TreeSelect, Card, Popover} from 'antd';
import {sourceTypes, multiModal} from "../common/common";
import { ConfigWrap, InspirationWrap } from './styled';
import { AippContext } from '../../aippIndex/context';
import TreeComponent from "./tree.jsx";
import {getModels, getTools, getWaterFlows, getKnowledges, getFitables} from "../../../shared/http/appBuilder";
import {uuid} from "../../../common/utils";
import {Message} from "../../../shared/utils/message";
const { Option } = Select;

function LLM(props) {
    const { updateData } = props;
    const [showControl, setShowControl] = useState(true);
    const [models, setModels] = useState([]);
    const {TextArea} = Input;

    const onArrowClick = () => {
        setShowControl(!showControl);
    }

    const handleGetModels = (open) => {
        if (!open) return;
        getModels().then((res) => {
            setModels(res.data);
        })
    }

    useEffect(() => {
        handleGetModels(true);
    },[])

    return (
        <>
            <div className="control-container llm-container">
                <div className="control-header c-header">
                    <div className="control-title">
                        <span className="title-icon"><svg t="1713603904185" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1002" width="16" height="16"><path d="M969.485312 337.5616c0.0512 24.8832-3.6352 49.664-10.9568 73.472-1.792 5.632-1.28 9.4208 2.816 14.08a251.904 251.904 0 0 1 59.1872 126.976c2.4576 13.7216 3.6352 27.648 3.584 41.6256a259.1232 259.1232 0 0 1-47.616 146.432 255.8464 255.8464 0 0 1-98.0992 83.7632 240.5376 240.5376 0 0 1-56.32 19.7632 10.496 10.496 0 0 0-8.704 7.68c-28.0576 75.3152-79.872 127.488-155.648 156.5184-24.6784 9.3184-50.688 14.6432-77.1072 15.7696-67.584 3.2768-127.6928-16.0256-180.224-58.0096a315.2384 315.2384 0 0 1-24.32-22.6304 11.1616 11.1616 0 0 0-11.264-3.584c-61.952 10.9056-121.0368 2.4576-175.872-28.3648-71.5264-40.192-114.5856-101.2224-130.4576-180.5312a239.9744 239.9744 0 0 1 6.7072-117.5552l1.1264-3.584a7.168 7.168 0 0 0-1.7408-8.0896A255.3344 255.3344 0 0 1 0.730112 448.6144a249.1904 249.1904 0 0 1 28.672-134.912 255.2832 255.2832 0 0 1 118.016-114.5856 242.7904 242.7904 0 0 1 54.4768-18.8416c4.2496-0.5632 7.68-3.584 8.7552-7.68a250.368 250.368 0 0 1 68.5568-103.5776 266.1376 266.1376 0 0 1 302.336-38.6048c24.3712 12.6976 46.2848 29.3376 64.9216 49.3056 4.352 4.6592 8.448 5.632 14.6432 4.608a261.0176 261.0176 0 0 1 148.6848 16.2816c33.536 13.824 63.6928 34.6624 88.4224 61.0304a257.2288 257.2288 0 0 1 71.2704 175.9232z m-387.584-226.4576c-1.6896-1.6384-2.816-2.8672-4.096-3.8912a194.0992 194.0992 0 0 0-140.4928-39.936 187.392 187.392 0 0 0-107.8272 46.592C286.118912 151.808 263.283712 199.68 262.771712 256.9216c-0.6656 78.08-0.1024 156.16-0.256 234.2912-0.4608 4.4544 2.048 8.704 6.144 10.5472 26.112 14.6432 52.0192 29.5424 78.0288 44.3392 1.536 0.9216 3.2768 1.536 5.7344 2.6624v-8.3968-277.504c0-13.9776 5.888-24.2176 18.1248-31.232l204.4928-116.4288 6.8608-4.1472zM195.187712 251.7504c-2.2528 0.6656-3.7888 1.024-5.376 1.6384a181.1968 181.1968 0 0 0-59.904 37.5296c-47.104 44.8-68.2496 99.3792-60.5184 163.5328 7.68 62.8736 39.8848 111.104 95.0784 143.3088 68.1472 39.68 137.0112 78.2336 205.4656 117.4016a9.6768 9.6768 0 0 0 11.1104-0.1024c25.7536-14.848 51.5584-29.4912 77.4144-44.1856 1.8944-1.024 3.5328-2.2016 5.7344-3.6864-1.8944-1.3312-3.2768-2.3552-4.7104-3.1744l-177.7664-101.376-69.632-39.68a29.5424 29.5424 0 0 1-14.4896-17.152 57.6512 57.6512 0 0 1-2.3552-16.1792c-0.1536-76.032-0.1536-151.9616 0-227.9424v-9.9328z m703.744 119.296a175.104 175.104 0 0 0-0.3584-67.84c-13.824-65.8944-51.5584-113.9712-114.2784-140.6976-60.8768-25.9584-120.832-21.0432-178.432 11.776a114064.2816 114064.2816 0 0 1-201.728 114.8928 8.1408 8.1408 0 0 0-4.864 7.9872c0.2048 30.5152 0.1024 60.928 0.1024 91.4432 0 1.4336 0.256 2.816 0.512 5.12l11.1104-6.2976 178.8928-101.888 60.7232-34.6112a36.2496 36.2496 0 0 1 21.504-5.12 40.3456 40.3456 0 0 1 16.7936 5.9904c35.072 20.0704 70.144 40.0896 105.2672 60.0576l100.3008 57.1392a41.3184 41.3184 0 0 0 4.4544 2.048z m-774.656 280.8832c-0.8192 10.8544-1.792 20.0704-2.048 29.3376a177.2032 177.2032 0 0 0 25.088 98.304c41.216 68.1984 102.912 100.2496 182.9376 97.28 31.3344-1.0752 60.416-11.3664 87.5008-27.0336 29.3888-16.9984 58.9824-33.7408 88.5248-50.5344 37.6832-21.504 75.3664-42.9568 113.152-64.3584a8.96 8.96 0 0 0 5.12-9.0624c-0.1536-29.6448-0.0512-59.2896-0.1024-88.8832 0-2.048-0.2048-3.9936-0.3584-6.656-2.4064 1.2288-4.096 1.9968-5.7344 2.9184-26.5216 15.0528-53.0944 30.208-79.616 45.4144l-113.664 64.768-52.9408 30.1568a32.8192 32.8192 0 0 1-34.2016 0l-5.0688-2.816-186.0608-106.0352c-7.0144-4.096-14.0288-7.9872-22.528-12.8z m435.0464-295.3728c2.048 1.3312 3.0208 2.1504 4.096 2.7136 52.736 30.1568 105.472 60.2112 158.1568 90.2144l90.2144 51.4048c7.6288 4.096 13.2096 11.1616 15.36 19.456 1.1264 4.096 1.6896 8.2944 1.6384 12.544 0.1024 77.6192 0.1024 155.2896 0 232.96v6.5536c3.6352-1.3312 6.656-2.3552 9.5744-3.584 22.8352-9.472 43.52-23.296 60.8256-40.7552 40.3456-40.7552 60.2112-89.6 56.5248-146.5856-4.4544-67.7888-36.864-119.5008-95.488-154.9824-19.3536-11.7248-39.2704-22.6304-58.9824-33.7408l-147.5072-84.224a9.216 9.216 0 0 0-10.5472 0.1536c-17.5616 10.24-35.2768 20.1728-52.8896 30.1568l-30.976 17.7152z m112.2304 118.528V759.9104c0 14.1824-5.3248 24.9856-17.92 32.0512l-103.1168 58.7264-103.5776 59.0848c-1.536 0.8704-2.9696 2.048-4.9664 3.4304 2.3552 1.8944 4.096 3.4816 5.9392 4.864 56.832 40.8064 118.784 50.8416 185.088 27.6992a190.8224 190.8224 0 0 0 128.1024-177.3056c0.4096-78.9504 0-157.9008 0.1536-236.8a9.1136 9.1136 0 0 0-5.2224-9.1648c-26.4704-14.848-52.736-29.952-79.1552-45.0048-1.1776-0.7168-2.56-1.1264-5.3248-2.4064z m-272.1792 101.12l112.64 64.1536 112.0256-63.8464a12.8 12.8 0 0 0 0.4096-1.8944c0-41.0112 0-82.0736 0.1536-123.136 0-3.3792-1.6896-4.7616-4.3008-6.2464A41552.384 41552.384 0 0 1 521.690112 389.12l-9.728-5.376-3.9424 2.1504-104.2432 59.1872a7.936 7.936 0 0 0-4.5056 8.0896c0.1536 28.9792 0 57.9584 0 86.9888l0.0512 36.0448z" p-id="1003"></path></svg></span>
                        <span>大模型</span>
                    </div>
                </div>
                <div className="control">
                    <div
                        style={{
                            display: showControl ? "block":"none"
                        }}
                    >
                        <div
                            style={{
                                display: "flex"
                            }}>
                            <Form.Item
                                style={{
                                    flex: 2
                                }}
                                name="model"
                                label="模型"
                                rules={[
                                    {
                                        required: true,
                                    }
                                ]}
                            >
                                <Select
                                    className={"no-right-radius full-border"}
                                    placeholder="选择一个合适的大模型"
                                    allowClear
                                    options={models}
                                    onDropdownVisibleChange={(open) => handleGetModels(open)}
                                    onChange={(value) => {updateData(value, "model")}}
                                    fieldNames={{
                                        label: 'id',
                                        value: 'id'
                                    }}
                                >
                                </Select>
                            </Form.Item>
                            {/*<Row>*/}
                            {/*<Col span={18}>*/}
                            <Form.Item
                                style={{
                                    flex:1
                                }}
                                name="temperature"
                                label="温度"
                                rules={[
                                    {
                                        required: true,
                                    }
                                ]}
                            >
                                <InputNumber
                                    className={"no-left-radius"}
                                    style={{
                                        width: "100%",
                                        borderLeft: "none"
                                    }}
                                    min={0}
                                    max={1}
                                    controls={true}
                                    changeOnWheel={true}
                                    keyboard={true}
                                    onChange={(value) => {updateData(value, "temperature")}}
                                    step={0.1}
                                    formatter={(value) => {
                                        if (value === 0.0) {
                                            return 0;
                                        }
                                        return value;
                                    }}
                                />
                            </Form.Item>
                        </div>
                        {/*</Col>*/}
                        {/*<Col span={4}>*/}
                        {/*<InputNumber*/}
                        {/*    min={0}*/}
                        {/*    max={1}*/}
                        {/*    step={0.1}*/}
                        {/*    onChange={onSliderChange}*/}
                        {/*></InputNumber>*/}
                        {/*</Col>*/}
                        {/*</Row>*/}
                        <Form.Item
                            name="systemPrompt"
                            label="提示词"
                        >
                            <TextArea
                                placeholder="输入一段提示词，可以给应用预设身份"
                                rows={6}
                                onBlur={(e) => {updateData(e.target.value, "systemPrompt")}}
                            />
                        </Form.Item>
                    </div>
                </div>
            </div>
        </>
    )
}

function Skill(props) {
    const { waterflowChange, updateData } = props;
    const [showToolControl, setShowToolControl] = useState(true);
    const [showFlowControl, setShowFlowControl] = useState(true);
    const [showFlowModal, setShowFlowModal] = useState(false);
    const { appId, tenantId } = useContext(AippContext);
    const [tools, setTools] = useState([]);
    const [waterFlow, setWaterFlow] = useState(null);
    const navigate = useNavigate();

    const filterOption = (input, option) =>
        (option?.label ?? '').toLowerCase().includes(input.toLowerCase());

    const onArrowClick = (value, func) => {
        func(!value);
    }

    const onAddFlowClick = () => {
      navigate(`/aipp/${tenantId}/addFlow/${appId}`);
    };

    const onAddToolClick = () => {
        setShowFlowModal(true);
    }

    const closeFlowModal = () => {
        setShowFlowModal(false);
    }

    const handleGetTools = () => {
        const params = {
            includeTags: "FIT",
            excludeTags: null,
            pageNum: 0,
            pageSize: 10
        };
        getTools(params).then((res) => {
          if (res.code === 0) {
            setTools(res.data);
          }
        })
    }

    useEffect(() => {
      handleGetTools();
      handleGetWaterFlows();
    },[])

    const handleGetWaterFlows = () => {
        const params = {
            pageNum: 0,
            pageSize: 10,
            tenantId: tenantId,
        };
        getWaterFlows(params).then(async (res) => {
            if (res.code === 0) {
              await setWaterFlow(res.data);
              waterflowChange();
            }
        })
    }

    const handleCheck = (option, event) => {
        event.stopPropagation();
        console.log(option);
        navigate(`/aipp/${option.data.tenantId}/flowDetail/${option.data.appId}`);
    }

    return (
        <>
            <div className="control-container">
                <div className="control-header c-header">
                    <div className="control-title">
                        <span className="title-icon"><svg t="1713605271988" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1645" width="200" height="200"><path d="M814.933333 1009.066667c-38.4 0-74.666667-14.933333-104.533333-42.666667l-258.133333-256 76.8-76.8 12.8 12.8 29.866666-29.866667 46.933334 46.933334-46.933334 46.933333 196.266667 196.266667c23.466667 23.466667 64 23.466667 89.6 0l53.333333-53.333334c25.6-25.6 25.6-64 0-89.6l-196.266666-196.266666-46.933334 46.933333-46.933333-46.933333 29.866667-29.866667-10.666667-14.933333 76.8-76.8 256 258.133333c57.6 57.6 57.6 151.466667 0 209.066667l-53.333333 53.333333c-29.866667 29.866667-66.133333 42.666667-104.533334 42.666667zM328.533333 490.666667l-85.333333-83.2-113.066667-59.733334L6.4 189.866667 196.266667 0l157.866666 123.733333 59.733334 115.2 89.6 87.466667-59.733334 61.866667-130.133333-130.133334 10.666667-8.533333-36.266667-70.4-85.333333-64-81.066667 81.066667 64 85.333333 70.4 36.266667 8.533333-10.666667 123.733334 123.733333z" p-id="1646"></path><path d="M179.2 1009.066667c-29.866667 0-57.6-10.666667-81.066667-34.133334l-53.333333-53.333333c-44.8-44.8-44.8-117.333333 0-162.133333l452.266667-452.266667c-12.8-83.2 12.8-166.4 72.533333-226.133333C650.666667 0 772.266667-19.2 874.666667 34.133333l51.2 27.733334-149.333334 149.333333 32 32 149.333334-149.333333 27.733333 51.2c53.333333 102.4 34.133333 224-46.933333 305.066666-59.733333 59.733333-145.066667 85.333333-226.133334 72.533334L260.266667 977.066667c-21.333333 21.333333-51.2 32-81.066667 32z m576-917.333334c-44.8 0-89.6 17.066667-123.733333 51.2-44.8 44.8-61.866667 108.8-44.8 168.533334l6.4 23.466666L106.666667 821.333333c-10.666667 10.666667-10.666667 29.866667 0 40.533334l53.333333 53.333333c10.666667 10.666667 29.866667 10.666667 40.533333 0l486.4-486.4 23.466667 6.4c59.733333 17.066667 125.866667-2.133333 168.533333-44.8 38.4-38.4 57.6-93.866667 51.2-145.066667L810.666667 364.8 657.066667 213.333333l119.466666-119.466666c-6.4-2.133333-14.933333-2.133333-21.333333-2.133334z" p-id="1647"></path></svg></span>
                        <div>技能</div>
                    </div>
                </div>
                <div className="control">
                    <div className="control-header">
                        <div className="control-title">
                            {
                                showToolControl ? <DownOutlined onClick={() => onArrowClick(showToolControl, setShowToolControl)}/>
                                    : <UpOutlined onClick={() => onArrowClick(showToolControl, setShowToolControl)}/>
                            }
                            <div style={{marginLeft: "10px"}}>工具</div>
                        </div>
                        {/*<PlusOutlined className="icon plus-icon" onClick={onAddToolClick}/>*/}
                    </div>
                    <Form.Item
                        name="tool"
                        label=""
                        style={{
                            marginTop: "10px",
                            marginBottom: "20px",
                            display: showToolControl ? "block":"none",
                        }}
                    >
                        <Select
                            mode="multiple"
                            showSearch
                            allowClear
                            placeholder="选择合适的工具"
                            filterOption={filterOption}
                            optionFilterProp="label"
                            options={tools}
                            onFocus={handleGetTools}
                            fieldNames={{
                                label: "name",
                                value: "uniqueName"
                            }}
                            onChange={(value) => {updateData(value, "tool")}}
                        ></Select>
                    </Form.Item>
                </div>
                <div className="control">
                    <div className="control-header">
                        <div className="control-title">
                            {
                                showFlowControl ? <DownOutlined onClick={() => onArrowClick(showFlowControl, setShowFlowControl)}/>
                                    : <UpOutlined onClick={() => onArrowClick(showFlowControl, setShowFlowControl)}/>
                            }
                            <div style={{marginLeft: "10px"}}>工具流</div>
                        </div>
                        <PlusOutlined className="icon plus-icon" onClick={onAddFlowClick}/>
                    </div>
                    <Form.Item
                        name="workflows"
                        label=""
                        style={{
                            marginTop: "10px",
                            display: showFlowControl ? "block":"none",
                        }}
                    >
                        <Select
                            mode="multiple"
                            showSearch
                            allowClear
                            placeholder="选择合适的工具流"
                            filterOption={filterOption}
                            optionFilterProp="label"
                            optionRender={(option) => (
                                <div style={{display: "flex", justifyContent: "space-between"}}>
                                    <span>{option.label}</span>
                                    <Button
                                        style={{ height: '16px', padding: '0', fontSize: '12px', lineHeight: '16px' }}
                                        type="text"
                                        size="small"
                                        icon={<EyeOutlined/>}
                                        onClick={(event) => handleCheck(option, event)} />
                                </div>
                            )
                            }
                            onFocus={handleGetWaterFlows}
                            onChange={(value) => {updateData(value, "workflows")}}
                        >
                            {waterFlow && waterFlow.map(option => (
                                <Option key={option.itemData.uniqueName}
                                        value={option.itemData.uniqueName}
                                        label={option.itemData.name}
                                        tenantId={option.tenantId}
                                        appId={option.appId}
                                >
                                    {option.itemData.name}
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>
                </div>
                <Modal open={showFlowModal} footer={null} onCancel={closeFlowModal} width="90vw">
                    <p>添加工具流</p>
                </Modal>
            </div>
        </>
    )
}

function Knowledge(props) {
    const {knowledge, updateData} = props;
    const [showKnowControl, setShowKnowControl] = useState(true);
    const [knowledgeOptions, setKnowledgeOptions] = useState(null);
    const [knows, setKnows] = useState(null);
    const { tenantId } = useContext(AippContext);
    const searchName = useRef('')
    const onArrowClick = (value, func) => {
        func(!value);
    }

    const handleSearch = (value) => {
      searchName.current = value;
      handleGetKnowledgeOptions();
    }

    const handleClose = (open) => {
      if (!open) {
        searchName.current = '';
        handleGetKnowledgeOptions();
      }
    }

    const handleGetKnowledgeOptions = () => {
        const params = {
            tenantId,
            pageNum: 1,
            pageSize: 10,
            name: searchName.current
        };
        getKnowledges(params).then((res) => {
            if (res.code === 0) {
              setKnowledgeOptions(res.data.items);
            }
        })
    }

    const handleChange = (value, option) => {
        setKnows(value);
        updateData(option, "knowledge");
    }

    useEffect(() => {
        handleGetKnowledgeOptions();
    }, [])

    useEffect(() => {
        setKnows(knowledge?.map(item => item.id));
    }, [knowledge])

    return (
        <>
            <div className="control-container">
                <div className="control-header c-header">
                    <div className="control-title">
                        <span className="title-icon"><svg t="1713604392971" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1321" width="16" height="16"><path d="M912.9 129.3H769.2c-24.9 0-45 20.1-45 45v677.8c0 24.9 20.1 45 45 45h143.7c24.9 0 45-20.1 45-45V174.3c0-24.8-20.1-45-45-45z m-27 72v466.9h-89.7V201.3h89.7z m-89.7 623.8v-84.9h89.7v84.9h-89.7zM636.8 129.3H493.1c-24.9 0-45 20.1-45 45v677.8c0 24.9 20.1 45 45 45h143.7c24.9 0 45-20.1 45-45V174.3c0-24.8-20.2-45-45-45z m-27 72v466.9h-89.7V201.3h89.7z m-89.7 623.8v-84.9h89.7v84.9h-89.7zM409.3 162.7l-140-32.5c-3.4-0.8-6.8-1.2-10.2-1.2-20.5 0-39 14.1-43.8 34.8L65.6 808.9c-5.6 24.2 9.5 48.4 33.7 54l140 32.5c3.4 0.8 6.8 1.2 10.2 1.2 20.5 0 39-14.1 43.8-34.8l116-499.9c0.3-1 0.6-2.1 0.9-3.2 0.2-1.1 0.4-2.1 0.6-3.2L443 216.6c5.6-24.1-9.5-48.3-33.7-53.9z m-130 43.7l87.4 20.3-18.7 80.6-87.4-20.3 18.7-80.6z m-50 612.8l-87.4-20.3 102.5-441.7 87.4 20.3-102.5 441.7z" p-id="1322"></path></svg></span>
                        <div>记忆</div>
                    </div>
                </div>
                <div className="control">
                    <div className="control-header">
                        <div className="control-title">
                            {
                                showKnowControl ? <DownOutlined onClick={() => onArrowClick(showKnowControl, setShowKnowControl)}/>
                                    : <UpOutlined onClick={() => onArrowClick(showKnowControl, setShowKnowControl)}/>
                            }
                            <div style={{marginLeft: "10px"}}>知识库</div>
                        </div>
                    </div>
                    <Form.Item
                        name="knowledge"
                        label=""
                        style={{
                            marginTop: "10px",
                            display: showKnowControl ? "block":"none",
                        }}
                    >
                        <div>
                            <Select
                                mode="multiple"
                                showSearch
                                allowClear
                                placeholder="选择合适的知识库"
                                options={knowledgeOptions}
                                onSearch={handleSearch}
                                value={knows}
                                onFocus={handleGetKnowledgeOptions}
                                onDropdownVisibleChange={handleClose}
                                fieldNames={{
                                    label: "name",
                                    value: "id"
                                }}
                                filterOption={(input, option) => true}
                                onChange={(value, option) => handleChange(value, option)}
                            ></Select>
                        </div>
                    </Form.Item>
                </div>
            </div>
        </>
    )
}

function Inspiration(props) {
    const {updateData} = props;
    const [inspirationValues, setInspirationValues] = useState(null);
    const [treeData, setTreeData] = useState(null);
    const [cacheTreeData, setCacheTreeData] = useState(null);
    const [selectTreeData, setSelectTreeData] = useState(null);
    const [showInspControl, setShowInspControl] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [showCateModal, setShowCateModal] = useState(false);
    const [promptVar, setPromptVar] = useState([]);
    const [cachePromptVar, setCachePromptVar] = useState(null);
    const [promptVarData, setPromptVarData] = useState([]);
    const [nodeList, setNodeList] = useState(null);
    const [fitables, setFitables] = useState(null);
    const [category, setCategory] = useState(null);
    const [id, setId] = useState("");
    const [modalForm] = Form.useForm();
    const {TextArea} = Input;
    let regex = /{{(.*?)}}/g;

    const formItemLayout = {
        labelCol: { span: 3 },
        wrapperCol: { span: 24 },
    };

    const columns = [
        {
            title: '变量',
            dataIndex: 'var',
            key: 'var',
        },
        {
            title: '类型',
            dataIndex: 'varType',
            key: 'varType',
        },
        {
            title: '来源类型',
            dataIndex: 'sourceType',
            key: 'sourceType',
            render: (sourceType, record) => (
                <>
                    <Select options={sourceTypes} defaultValue={sourceType} onChange={(sourceType) => handleTableChange(sourceType, record, 'sourceType')}/>
                </>
            )
        },
        {
            title: '来源信息',
            dataIndex: 'sourceInfo',
            key: 'sourceInfo',
            render: (sourceInfo, record) => (
                <>
                    {record.sourceType === "fitable" ? <Select options={fitables}
                                                               onFocus={handleGetFitable}
                                                               fieldNames={{
                                                                   label: "name",
                                                                   value: "fitableId"
                                                               }}
                                                               defaultValue={sourceInfo}
                                                               onChange={(sourceInfo) => handleTableChange(sourceInfo, record, 'sourceInfo')}/> :
                        <Input defaultValue={sourceInfo} onBlur={(e) => handleTableChange(e.target.value, record, 'sourceInfo')}/>}
                </>
            )
        },
        {
            title: '是否多选',
            dataIndex: 'multiple',
            key: 'multiple',
            render: (checked, record) => (
                <>
                    <Switch checked={checked} onChange={(checked) => handleTableChange(checked, record, 'multiple')} />
                </>
            )
        },
        {
            title: '操作',
            key: 'action',
            render: (text, record) => {
                return (
                    <div style={{display: 'flex'}}>
                            <Button type="text" onClick={(event) => handleDeleteClick(record, event)}>
                                <DeleteOutlined />
                                <span style={{fontSize: "12px"}}>删除</span>
                            </Button>
                    </div>
                );
            },
        }
    ];

    const onArrowClick = (value, func) => {
        func(!value);
    }

    const clickInspiration = (value) => {
        setShowModal(true);
        setId(value.id);
        setCategory(value.category);
        modalForm.setFieldsValue(value);
        setPromptVarData(value.promptVarData);
        const newPromptVar = value.promptVarData ? value.promptVarData.map(item => {
            return item.var;
        }) : [];
        setCachePromptVar(null);
        setPromptVar(newPromptVar);
    }

    const onAddClick = () => {
        modalForm.setFieldsValue({category: null, auto: false, name: "", description: "", prompt: "", promptVarData: []});
        setPromptVar([]);
        setShowModal(true);
        setId(null);
        setCategory(null);
    }

    const onPromptChange = (event) => {
        let result = [];
        let match;
        while (match = regex.exec(event.target.value)) {
            result.push(match[1]);
        }
        setCachePromptVar(promptVar);
        setPromptVar(result);
    }

    const handleTableChange = (checked, record, key) => {
        const newData = promptVarData.map(item => {
            if (item.var === record.var) {
                if (key === "sourceType") {
                    return {
                        ...item,
                        [key]: checked,
                        sourceInfo: null
                    }
                } else {
                    return {
                        ...item,
                        [key]: checked,
                    };
                }
            }
            return item;
        });
        setPromptVarData(newData);
    }

    const handleChangeCategory = (value) => {
        modalForm.setFieldValue("category", value);
        setCategory(value);
    }

    const handleDeleteClick = (record, event) => {
        const data = promptVarData.filter(item => item.var !== record.var);
        setPromptVarData(data);
    }

    const handleModalOK = () => {
        modalForm
            .validateFields()
            .then((values) => {
                setShowModal(false);
                const newvalues = id ? inspirationValues.inspirations.map(item => {
                    if (item.id === id) {
                        return {...values, id};
                    }
                    return item;
                }) : [...inspirationValues.inspirations, {...values, id: uuid()}];
                const newInspirationValues = {...inspirationValues, inspirations: newvalues};
                updateData(newInspirationValues, "inspiration");
                setInspirationValues(newInspirationValues);
            })
            .catch((errorInfo) => {
            });
    }

    const handleModalCancel = () => {
        setShowModal(false);
    }

    const handleCateModalOK = () => {
        setTreeData(cacheTreeData);
        setShowCateModal(false);
        const newInspirationValues = {...inspirationValues, category: [
            { title: "root",
                id: "root",
                children: cacheTreeData
            }]};
        updateData(newInspirationValues, "inspiration");
        setInspirationValues(newInspirationValues);
    }

    const handleCateModalCancel = () => {
        setShowCateModal(false);
    }

    const openCategoryModal = () => {
        setShowCateModal(true);
    }

    const handleGetFitable = () => {
        getFitables().then(res => {
            if (res.code === 0) {
              setFitables(res.data);
            }
        })
    }

    useEffect(() => {
        handleGetFitable();
    }, [])

    useEffect(() => {
        if (!cachePromptVar && id) return;
        if (!promptVar.length && !id) {
            setPromptVarData([]);
            return;
        }
        const newVar = promptVar.filter(item => !cachePromptVar.includes(item));
        const data = newVar.map((item) => {
            return {
                key: uuid(),
                var: item,
                varType: "选择框",
                sourceType: "fitable",
                sourceInfo: "",
                multiple: false
            }
        });
        setPromptVarData([...promptVarData, ...data]);
    }, [promptVar]);

    useEffect(() => {
        modalForm.setFieldValue("promptVarData", promptVarData);
    }, [promptVarData])

    useEffect(() => {
        if (!props.inspirationValues) return;
        setInspirationValues(props.inspirationValues);
        setTreeData(props.inspirationValues.category[0].children ? props.inspirationValues.category[0].children : []);
    },[props.inspirationValues])

    const updateTreeData = (value) => {
        setCacheTreeData(value);
    }

    useEffect(() => {
        if (!inspirationValues) return;
        const data = inspirationValues.inspirations.map(item => item.category?.split(":")[1]);
        setNodeList(data);
    }, [inspirationValues])

    useEffect(() => {
        if (!treeData) return;
        setSelectTreeData(disableNodes(treeData));
    }, [treeData])

    const handleDeleteIns = (id) => {
        const newvalues = inspirationValues.inspirations.filter(item => item.id !== id);
        const newInspirationValues = {...inspirationValues, inspirations: newvalues};
        updateData(newInspirationValues, "inspiration");
        setInspirationValues(newInspirationValues);
    }

    const disableNodes = (nodes) => {
        return nodes.map(node => {
            const children = node.children ? disableNodes(node.children) : [];
            const isLeafNode = children.length === 0;

            return {
                ...node,
                disabled: !isLeafNode,
                children: children
            };
        });
    }

    return (
        <>
            <div className="control-container">
                <div className="control-header c-header">
                    <div className="control-title">
                        <span className="title-icon"><svg t="1713604487821" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="1482" width="16" height="16"><path d="M648.533333 674.474667a34.133333 34.133333 0 0 1 34.048 31.607466l0.085334 2.542934v51.2a68.266667 68.266667 0 0 1-64.853334 68.181333l-3.413333 0.085333H409.6a68.266667 68.266667 0 0 1-68.181333-64.853333l-0.085334-3.413333v-51.2a34.133333 34.133333 0 0 1 68.181334-2.56l0.085333 2.56v51.2h204.8v-51.2a34.133333 34.133333 0 0 1 34.133333-34.133334zM597.333333 871.1168a34.133333 34.133333 0 0 1 2.56 68.164267l-2.56 0.1024H426.666667a34.133333 34.133333 0 0 1-2.56-68.181334l2.56-0.085333h170.666666z" fill="#444444" p-id="1483"></path><path d="M512 51.2c197.9392 0 358.4 160.4608 358.4 358.4 0 145.749333-80.554667 272.7936-208.213333 328.567467a34.133333 34.133333 0 0 1-27.306667-62.549334C737.28 630.852267 802.133333 528.5888 802.133333 409.6c0-160.238933-129.8944-290.133333-290.133333-290.133333s-290.133333 129.8944-290.133333 290.133333c0 118.9888 64.836267 221.252267 167.253333 266.018133a34.133333 34.133333 0 0 1-27.306667 62.549334C234.154667 682.3936 153.6 555.349333 153.6 409.6 153.6 211.6608 314.0608 51.2 512 51.2z" fill="#444444" p-id="1484"></path><path d="M490.018133 204.868267L494.933333 204.8v68.266667c-64.477867 0-117.230933 51.421867-119.3984 115.370666L375.466667 392.533333a34.133333 34.133333 0 0 1-68.181334 2.56L307.2 392.533333c0-101.888 81.527467-185.053867 182.818133-187.665066z" fill="#00B386" p-id="1485"></path></svg></span>
                        <div>灵感大全</div>
                    </div>
                </div>
                <div className="control">
                    <div className="control-header ">
                        <div className="control-title">
                            <span>创建灵感</span>
                        </div>
                        <PlusOutlined className="icon plus-icon" onClick={onAddClick}/>
                    </div>
                    <Form.Item
                        name={["inspiration", "inspirations"]}
                        label=""
                        style={{
                            marginTop: "10px",
                            display: showInspControl ? "block":"none",
                        }}
                    >
                        <div>
                        {(!inspirationValues || !inspirationValues?.inspirations.length) &&
                            <div className="empty-container">请创建<Button type="link" onClick={() => setShowModal(true)}>灵感大全</Button></div>
                        }
                        <Row gutter={[16, 16]}>
                        {inspirationValues && inspirationValues.inspirations.map((item, index) => (
                            <Col key={index} span={8}>
                                <Card>
                                    <div className="inspiration-container">
                                    <span onClick={() => clickInspiration(item)} className="card-title">{item.name}</span>
                                    <DeleteOutlined onClick={() => handleDeleteIns(item.id)}/>
                                    </div>
                                </Card>
                            </Col>
                        ))}
                        </Row>
                        </div>
                    </Form.Item>
                </div>
                  <Modal title="添加新的灵感" open={showModal} onOk={handleModalOK} onCancel={handleModalCancel} forceRender width="50vw">
                      <InspirationWrap>
                          <Form
                              form={modalForm}
                              {...formItemLayout}
                          >
                              <Form.Item
                                  name="name"
                                  label="名称"
                                  rules={[
                                      {
                                          required: true,
                                      }
                                  ]}
                                  style={{ marginBottom: '6px' }}
                              >
                                  <Input placeholder="请输入灵感大全名称" />
                              </Form.Item>
                              <Form.Item
                                  name="description"
                                  label="描述"
                                  rules={[
                                      {
                                          required: true,
                                      }
                                  ]}
                                  style={{ marginBottom: '16px' }}
                              >
                                  <TextArea placeholder="请输入灵感大全描述"
                                            rows={3}
                                  />
                              </Form.Item>
                              <Form.Item
                                  name="prompt"
                                  label="提示词"
                                  rules={[
                                      {
                                          required: true,
                                      }
                                  ]}
                                  style={{ marginBottom: '16px' }}
                              >
                                  <TextArea
                                      placeholder="你可以使用{{变量名}}添加变量"
                                      rows={6}
                                      onBlur={onPromptChange}
                                  />
                              </Form.Item>
                              <Form.Item
                                  name="promptVarData"
                                  label="提示词变量"
                                  style={{display: promptVar.length ? "block" : "none", marginTop: "10px"}}
                              >
                                  <Table columns={columns} dataSource={promptVarData}
                                  />
                              </Form.Item>
                                  <Form.Item
                                      name="category"
                                      label="分类"
                                      style={{
                                          flex: 5
                                      }}
                                  >
                                      <div style={{
                                          display: "flex",
                                          justifyContent: "center",
                                          alignItems: "center"
                                      }}>
                                      <TreeSelect
                                          treeData={selectTreeData}
                                          treeDefaultExpandAll
                                          fieldNames={{
                                              label: "title",
                                              value: "parent"
                                          }}
                                          value={category}
                                          onSelect={handleChangeCategory}
                                      />
                                      <PlusOutlined className="plus-icon" style={{flex: 1, fontSize: "14px"}} onClick={openCategoryModal}/>
                                      </div>
                                  </Form.Item>
                              <Form.Item
                                  name="auto"
                                  label={
                                  <div>
                                      <span> 是否自动执行</span>
                                      <Popover content={<div>选择自动执行，应用会自动将灵感大全的提示词发送给助手；<br/>
                                          选择不自动执行，灵感大全的提示词会默认填充在对话框，支持修改，由用户自己发送给助手</div>}>
                                          <QuestionCircleOutlined />
                                      </Popover>
                                  </div>
                                  }
                                  checked={modalForm.getFieldValue("auto")}
                              >
                                  <Switch />
                              </Form.Item>
                          </Form>
                      </InspirationWrap>
                  </Modal>
                  <Modal title="类目配置" open={showCateModal} onOk={handleCateModalOK} onCancel={handleCateModalCancel} width="50vw">
                      <TreeComponent tree={treeData} nodeList={nodeList} updateTreeData={updateTreeData}/>
                  </Modal>
            </div>
        </>
    )
}

function Multimodal() {
    const [showMultiControl, setShowMultiControl] = useState(true);

    const onArrowClick = (value, func) => {
        func(!value);
    }

    return (
        <>
            <div className="control">
                <div className="control-header">
                    <div className="control-title">
                        {
                            showMultiControl ? <DownOutlined onClick={() => onArrowClick(showMultiControl, setShowMultiControl)}/>
                                : <UpOutlined onClick={() => onArrowClick(showMultiControl, setShowMultiControl)}/>
                        }
                        <div style={{marginLeft: "10px"}}>多模态</div>
                    </div>
                </div>
                <Form.Item
                    name="multimodal"
                    label=""
                    style={{
                        marginTop: "10px",
                        display: showMultiControl ? "block":"none",
                    }}
                >
                    <Select
                        mode="multiple"
                        allowClear
                        placeholder="选择多模态"
                        defaultValue={["file", "image", "radio", "video"]}
                        options={multiModal}
                    ></Select>
                </Form.Item>
            </div>
        </>
    )
}

function ConfigUI(props) {
    const {formData, handleConfigDataChange, inspirationChange, status} = props;
    const [form] = Form.useForm();
    const [inspirationValues, setInspirationValues] = useState(null);
    const [knowledge, setKnowledge] = useState(null);
    const [isDisabled, setIsDisabled] = useState(false);

    useEffect(() => {
        setIsDisabled(status === "published");
    }, [status])

    useEffect(() => {
        if (!formData) return;
        const newData = formData.properties.reduce((acc, item) => {
            acc[item.name] = item.defaultValue;
            return acc;
        }, {});
        form.setFieldsValue(newData);
    }, [formData])

    const buildSaveData = (key, value, saveData) => {
        for (let prop of saveData.properties) {
            if (prop.name === key) {
                prop.defaultValue = value;
            }
        }
    }

    const handleValuesChange = (changedValues, allValues) => {
        const entries = Object.entries(changedValues);
        const saveData = {...formData};
        entries.forEach(([key, value]) => {
            buildSaveData(key, value, saveData);
        });
        handleConfigDataChange(saveData);
    }

    useEffect(() => {
        setInspirationValues(form.getFieldValue("inspiration"));
        setKnowledge(form.getFieldValue("knowledge"));
    }, [form.getFieldsValue()])

    const updateConfig = (value, key) => {
        const saveData = {...formData};
        buildSaveData(key, value, saveData);
        handleConfigDataChange(saveData);
        if (key === "inspiration") {
          inspirationChange();
        }
    }

    const waterflowChange = () => {
      let uniqueName = sessionStorage.getItem('uniqueName');
      if (uniqueName) {
        let workflows = form?.getFieldValue('workflows');
        let workflwArr =  Array.from(new Set([...workflows, uniqueName]))
        form.setFieldValue('workflows', workflwArr);
        handleValuesChange({ workflows: workflwArr });
        sessionStorage.removeItem('uniqueName');
                                            }
    }

    return (
        <>
            <ConfigWrap>
                <Form
                    form={form}
                    layout="vertical"
                    // onValuesChange={handleValuesChange}
                    disabled={isDisabled}
                >
                    <LLM updateData={updateConfig}/>
                    <Skill waterflowChange={waterflowChange} updateData={updateConfig}/>
                    <Knowledge knowledge={knowledge} updateData={updateConfig}/>
                    <Inspiration inspirationValues={inspirationValues} updateData={updateConfig}/>
                </Form>
            </ConfigWrap>
        </>
    )
}
export default ConfigUI;
