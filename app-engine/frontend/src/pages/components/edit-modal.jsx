
import React, { useEffect, useState, useImperativeHandle, useContext } from 'react';
import { useParams } from 'react-router-dom';
import { Input, Modal, Button, Select, Form, Upload } from 'antd';
import {
  ToTopOutlined
} from '@ant-design/icons';
import { Message } from '../../shared/utils/message';
import { uploadChatFile, updateAippInfo, createAipp } from '../../shared/http/aipp';
import { httpUrlMap } from '../../shared/http/httpConfig';
import robot from '../../assets/images/ai/robot1.png';
import './styles/edit-modal.scss';

const { TextArea } = Input;
const { AIPP_URL } = process.env.NODE_ENV === 'development' ? {AIPP_URL: `${window.location.origin}/api/jober/v1/api`} : httpUrlMap[process.env.NODE_ENV];
const EditModal = (props) => {
  const { modalRef, aippInfo, updateAippCallBack, type, addAippCallBack } = props;
  const [ form ] = Form.useForm();
  const { appId } = useParams();
  const tenantId = '31f20efc7e0848deab6a6bc10fc3021e';
  const [ isModalOpen, setIsModalOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [ avatarId, setAvatarId ] = useState('');
  const [filePath, setFilePath] = useState('');
  const [fileName, setFileName] = useState('');
  let fileHeaders = {
    'Content-Type' :'application/octet-stream'
  };
  const tagOptions = [
    { value: '编程开发', label: '编程开发' },
    { value: '决策分析', label: '决策分析' },
    { value: '写作助手', label: '写作助手' },
  ];
  const showModal = () => {
    setIsModalOpen(true);
  };
  useEffect(() => {
    form.setFieldsValue({
      name: aippInfo.name,
      description: aippInfo.attributes?.description,
      greeting: aippInfo.attributes?.greeting,
      icon: aippInfo.attributes?.icon,
      app_type:aippInfo.attributes?.app_type
    })
  }, [isModalOpen])
  const confrimClick = () => {
    if (type === 'add') {
      handleAddOk();
    } else {
      handleOk();
    }
  }
  // 创建应用
  const handleAddOk = async () => {
    try {
      setLoading(true);
      const formParams = await form.validateFields();
      const params = {
        name: formParams.name,
        greeting: formParams.greeting,
        description: formParams.description,
        icon: type === 'add' && filePath ? `${AIPP_URL}/${tenantId}/file?filePath=${filePath}&fileName=${fileName}` : formParams.icon,
        app_type: formParams.app_type,
        type: 'app'
      }
      const res = await createAipp(tenantId, 'df87073b9bc85a48a9b01eccc9afccc4', params);
      if (res.code === 0) {
        let { id } = res.data;
        handleCancel();
        Message({ type: 'success', content: '添加成功' });
        addAippCallBack(id);
      }
    } finally {
      setLoading(false);
    }
  }
  // 编辑应用基本信息
  const handleOk = async () => {
    try {
      setLoading(true);
      const formParams = await form.validateFields();
      const params = {
        name: formParams.name,
        attributes: formParams,
        type: aippInfo.type,
        version: aippInfo.version
      }
      filePath ? params.attributes.icon = `${AIPP_URL}/${tenantId}/file?filePath=${filePath}&fileName=${fileName}` : params.attributes.icon = aippInfo.attributes?.icon;
      const res = await updateAippInfo(tenantId, appId, params);
      if (res.code === 0) {
        updateAippCallBack(res.data);
        handleCancel();
        Message({ type: 'success', content: '操作成功' });
      }
    } finally {
      setLoading(false);
    }
  };
  const handleCancel = () => {
    setIsModalOpen(false);
  };
  const beforeUpload = (file) => {
    return false
  }
  const onChange = ({ file }) => {
    pictureUpload(file);
  }
  // 上传图片
  async function pictureUpload( file ) {
    let headers = {
      "attachment-filename": encodeURI(file.name || ""),
    };
    try {
      const formData = new FormData();
      formData.append("file", file);
      let res = await uploadChatFile(tenantId, appId, formData, headers);
      if (res.code === 0) {
        setFileName(res.data.file_name);
        setFilePath(res.data.file_path);
      }
    } catch (err) {
      Message({ type: 'error',  content: err.message || '上传图片失败'})
    }
  }
  useImperativeHandle(modalRef, () => {
    return {
      'showModal': showModal
    }
  })
  return <>
    {(
      <Modal
        title={ type ? '添加应用' : '修改基础信息' }
        width='600px'
        keyboard={false}
        maskClosable={false}
        forceRender={true}
        open={isModalOpen}
        onCancel={handleCancel}
        footer={[
          <Button key="back" onClick={handleCancel}>
            取消
          </Button>,
          <Button key="submit" type="primary" loading={loading} onClick={confrimClick}>
            确定
          </Button>
        ]}>
        <div className='edit-form-list'>
          <Form
            form={form}
            layout="vertical"
            autoComplete="off"
            className='edit-form-content'
          >
            <Form.Item
              label="名称"
              name="name"
              rules={[{ required: true, message: '请输入名称' }]}
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="简介"
              name="description"
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="开场白"
              name="greeting"
            >
              <TextArea rows={3} />
            </Form.Item>
            <Form.Item
              label="分类"
              name="app_type"
              rules={[{ required: true, message: '不能为空' }]}
            >
              <Select options={tagOptions} />
            </Form.Item>
            <Form.Item
              label="头像"
              name="icon"
            >
              <div className='avatar'>
                {  filePath ?
                  (<img className="img-send-item" src={`${AIPP_URL}/${tenantId}/file?filePath=${filePath}&fileName=${fileName}`}/>)
                  : (<Img icon={aippInfo.attributes?.icon}/>)}
                <Upload
                  beforeUpload={beforeUpload}
                  onChange={onChange}
                  showUploadList={false}
                  accept='.jpg,.png'>
                  <Button icon={<ToTopOutlined />}>手动上传</Button>
                </Upload>
              </div>
            </Form.Item>
          </Form>
        </div>
      </Modal>
    )}</>
};

const Img = (props) => {
  const { icon } = props;
  return <>{(
    <span>
      { icon ? <img src={icon}/> : <img src={robot}/> }
    </span>
  )}</>
}

export default EditModal;
