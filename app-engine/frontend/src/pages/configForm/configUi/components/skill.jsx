
import React, { useEffect, useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { Form, Select,  Modal, Button } from 'antd';
import { DownOutlined, UpOutlined, PlusOutlined, EyeOutlined } from '@ant-design/icons';
import { getTools, getWaterFlows } from "@shared/http/appBuilder";
import { ConfigFormContext } from '../../../aippIndex/context';
import {createAipp} from "@shared/http/aipp";
const { Option } = Select;

const Skill = (props) => {
  const { waterflowChange, updateData } = props;
  const [ showToolControl, setShowToolControl ] = useState(true);
  const [ showFlowControl, setShowFlowControl ] = useState(true);
  const [ showFlowModal, setShowFlowModal ] = useState(false);
  const { appId, tenantId } = useContext(ConfigFormContext);
  const [ tools, setTools ] = useState([]);
  const [ waterFlow, setWaterFlow ] = useState(null);
  const navigate = useNavigate();

  const filterOption = (input, option) =>
        (option?.label ?? '').toLowerCase().includes(input.toLowerCase());

  const onArrowClick = (value, func) => {
    func(!value);
  }

  // 新增工具流
  const handleAddWaterFlow = async () => {
    const timeStr = new Date().getTime().toString();
    const res = await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc3', { type: 'waterFlow', name: timeStr });
    if (res.code === 0) {
      const aippId = res.data.id;
      navigate(`/app-develop/${tenantId}/app-detail/add-flow/${aippId}`);
    }
  }

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
  },[]);

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
    navigate(`/app-develop/${option.data.tenantId}/app-detail/flow-detail/${option.data.appId}`);
  }

  return (
    <>
      <div className="control-container">
        <div className="control">
          <div className="control-header">
            <div className="control-title">
              工具
            </div>
          </div>
          <Form.Item
            name="tools"
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
              onChange={(value) => {updateData(value, "tools")}}
            ></Select>
          </Form.Item>
        </div>
        <div className="control">
          <div className="control-header">
            <div className="control-title">
              工具流
            </div>
            <PlusOutlined className="icon plus-icon" onClick={handleAddWaterFlow}/>
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
};

export default Skill;
