import React, { useState } from 'react';
import { Form, Modal } from 'antd';
import LocalUpload from '../select-form/local-upload';
import { createTableColumns } from '../../shared/http/knowledge';
import { useTranslation } from "react-i18next";

interface props {
  open: boolean;
  setOpen: any;
  // 知识库id
  repositoryId: string | null;
  // 知识表id
  knowledgeTableId: string | null;
}

// 创建知识库配置
type FieldType = {
  // 知识表名称
  selectedFile: any[]
};

const ImportTable = ({ open, setOpen, repositoryId, knowledgeTableId }: props) => {
  const { t } = useTranslation();
  const [formLayout, setFormLayout] = useState<any>('vertical');
  const [form] = Form.useForm();

  const initialValues = {
    selectedFile: []
  };
  const formItemLayout =
    formLayout === 'horizontal' ? { labelCol: { span: 8 }, wrapperCol: { span: 20 } } : null;

  const handleOk = async () => {
    try {
      const res = await form.validateFields();
      const fileName = res.selectedFile?.map((file: any) => `${file.uid}_${file.name}`)?.[0] || '';
      await createTableColumns({
        repositoryId: repositoryId as string,
        knowledgeTableId: knowledgeTableId as string,
        fileName,
      })
    } catch (error) {
      
    }
    form.setFieldValue('selectedFile', []);
    setOpen(false);
  }
  return (<>
    <Modal
        title={t('importData')}
        centered
        open={open}
        onOk={handleOk}
        onCancel={()=> {
          form.setFieldValue('selectedFile', []);
          setOpen(false);
        }}
        mousePosition={{ x: 300, y: 300 }}
      >
                   <Form<FieldType>
            {...formItemLayout}
            layout={formLayout}
            form={form}
            initialValues={initialValues}
            style={{ maxWidth: formLayout === 'inline' ? 'none' : 800 }}
          >
            <Form.Item label={t('pickFile')}  name = 'selectedFile' validateStatus={'error'}
              rules={[
                {
                  required: true,
                  message: t('fileCannotEmpty'),
                  validator: (_, value) => {
                    return value && value.length
                      ? Promise.resolve()
                      : Promise.reject(new Error(t('noFile')));
                  },
                },
              ]}>
              <LocalUpload form={form} respId={repositoryId} tableId={knowledgeTableId}/>
            </Form.Item>
          </Form>
      </Modal>
  </>)
};

export {
  ImportTable,
}
