
import React, { useEffect, useState } from 'react';
import { Form, Input, Button } from 'antd';
import { PlusCircleOutlined  } from '@ant-design/icons';

const Recommend = ({ updateData, recommendValues }) => {
return <>{(
  <div className="control-container">
    <div className="control">
      <div className="control-header ">
        <div className="control-title">
          <span>首次与应用对话时的推荐问题，最多创建3个</span>
        </div>
      </div>
      <Form.Item
        name="recommend"
        style={{
          marginTop: "10px",
          display: "block",
        }}
      >
        <div className="inspiration-add">
          <Button type="link" icon={<PlusCircleOutlined />} >创建</Button>
        </div>
      </Form.Item>
    </div>
  </div>
)}</>
};


export default Recommend;
