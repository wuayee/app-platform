
import React, { useEffect, useState } from 'react';
import { Drawer } from 'antd';
import { CloseOutlined } from '@ant-design/icons';

const AddKnowledge = (props) => {
  const { open, setOpen } = props;
  return <>{(
    <Drawer
      title='选择知识库'
      placement='right'
      width='1000px'
      closeIcon={false}
      onClose={false}
      open={open}
      extra={
        <CloseOutlined onClick={() => {setOpen(false)}}/>
      }>
    </Drawer>
  )}</>
};


export default AddKnowledge;
