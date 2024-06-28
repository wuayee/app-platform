import React from 'react';
import { ExclamationCircleFilled } from '@ant-design/icons';
import { message, Modal } from 'antd';
import { deleteModelbase } from '../../../shared/http/model-base';

export const deleteModel = (data: any, okCallback: Function) => {

  Modal.confirm({
    title: '确认删除',
    icon: <ExclamationCircleFilled />,
    content: `确认删除模型 ${data?.model_name} ?`,
    okType: 'danger',
    onOk() {
      //删除逻辑
      deleteModelbase(data?.model_name).then(res => {
        if (res && (res?.code===200 || res?.code===0)) {
          message.success('删除成功');
          okCallback?.({ offset: 0, limit: 10 });
        } else {
          message.error('删除失败');
        }
      })
    }
  })
}

