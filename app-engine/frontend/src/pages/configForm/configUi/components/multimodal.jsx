
import React, { useEffect, useState } from 'react';
import { Form, Select } from 'antd';
import { DownOutlined, UpOutlined } from '@ant-design/icons';
import { multiModal } from "../../common/common";

const Multimodal = () => {
  const [ showMultiControl, setShowMultiControl ] = useState(true);
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
};

export default Multimodal;
