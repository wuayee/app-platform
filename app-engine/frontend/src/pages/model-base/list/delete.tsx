import React from 'react';
import { ExclamationCircleFilled } from '@ant-design/icons';
import { Modal } from 'antd';

export const deleteModel = (data: any) => {

  Modal.confirm({
    title: '确认删除',
    icon: <ExclamationCircleFilled />,
    content: `确认删除模型 ${data?.modelName} ?`,
    okType: 'danger',
    onOk() {
      //删除逻辑
    }
  })
}

