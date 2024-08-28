
import React, { useEffect, useState } from 'react';
import { PlusOutlined, DeleteOutlined, QuestionCircleOutlined, PlusCircleOutlined } from '@ant-design/icons';
import { Form, Select, Input, Modal, Switch, Table, Button, TreeSelect, Popover } from 'antd';
import TreeComponent from '../tree';
import { getFitables } from '@/shared/http/appBuilder';
import { sourceTypes } from '../../common/common';
import { uuid } from '@/common/util';
import { useTranslation } from 'react-i18next';
import '../styles/inspiration.scss';
import { Message } from '@/shared/utils/message';
import '../styles/inspiration.scoped.scss';

const Inspiration = (props) => {
  const { t } = useTranslation();
  const { updateData } = props;
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
  const [id, setId] = useState('');
  const [disabled, setDisabled] = useState(false);
  const [type, setType] = useState('');
  const [modalForm] = Form.useForm();
  const { TextArea } = Input;
  let regex = /{{(.*?)}}/g;

  const formItemLayout = {
    labelCol: { span: 3 },
    wrapperCol: { span: 24 },
  };

  const columns = [
    {
      title: t('variable'),
      dataIndex: 'var',
      key: 'var',
    },
    {
      title: t('varType'),
      dataIndex: 'varType',
      key: 'varType',
    },
    {
      title: t('sourceType'),
      dataIndex: 'sourceType',
      key: 'sourceType',
      render: (sourceType, record) => (
        <>
          <Select options={sourceTypes} defaultValue={sourceType} onChange={(sourceType) => handleTableChange(sourceType, record, 'sourceType')} />
        </>
      )
    },
    {
      title: t('sourceInfo'),
      dataIndex: 'sourceInfo',
      key: 'sourceInfo',
      render: (sourceInfo, record) => (
        <>
          {
            record.sourceType === 'fitable' ?
              <Select options={fitables}
                onFocus={handleGetFitable}
                fieldNames={{
                  label: 'name',
                  value: 'fitableId'
                }}
                defaultValue={sourceInfo}
                onChange={(sourceInfo) => handleTableChange(sourceInfo, record, 'sourceInfo')} /> :
              <Input defaultValue={sourceInfo} onBlur={(e) => handleTableChange(e.target.value, record, 'sourceInfo')} />
          }
        </>
      )
    },
    {
      title: t('multiple'),
      dataIndex: 'multiple',
      key: 'multiple',
      render: (checked, record) => (
        <>
          <Switch checked={checked} onChange={(checked) => handleTableChange(checked, record, 'multiple')} />
        </>
      )
    },
    {
      title: t('operate'),
      key: 'action',
      render: (text, record) => {
        return (
          <div style={{ display: 'flex' }}>
            <Button type='text' onClick={(event) => handleDeleteClick(record, event)}>
              <DeleteOutlined />
              <span style={{ fontSize: '12px' }}>{t('delete')}</span>
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
    setType('edit');
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
    modalForm.setFieldsValue({ category: null, auto: false, name: '', description: '', prompt: '', promptVarData: [], promptTemplate: '' });
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
        if (key === 'sourceType') {
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
    modalForm.setFieldValue('category', value);
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
            return { ...values, id };
          }
          return item;
        }) : [...inspirationValues.inspirations, { ...values, id: uuid() }];
        const newInspirationValues = { ...inspirationValues, inspirations: newvalues };
        updateData(newInspirationValues, 'inspiration');
        setInspirationValues(newInspirationValues);
        setType('');
      })
      .catch((errorInfo) => { });
  }

  const handleModalCancel = () => {
    setShowModal(false);
    setType('');
  }

  /**
   * 检验是否有不合理类目
   */
  const validateCate = () => {
    if (disabled) {
      Message({ type: 'warning', content: t('invalidCategory') });
      return true;
    }
    return false;
  }
  /**
   * 点击树形类目弹框确认按钮的回调
   */
  const handleCateModalOK = () => {
    if (validateCate()) return;
    setTreeData(cacheTreeData);
    setShowCateModal(false);
    const newInspirationValues = {
      ...inspirationValues, category: [
        {
          title: 'root',
          id: 'root',
          children: cacheTreeData
        }]
    };
    updateData(newInspirationValues, 'inspiration');
    setInspirationValues(newInspirationValues);
  }
  /**
   * 点击树形类目弹框取消按钮的回调
   */
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
  }, []);

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
        varType: '选择框',
        sourceType: 'fitable',
        sourceInfo: '',
        multiple: false
      }
    });
    setPromptVarData([...promptVarData, ...data]);
  }, [promptVar]);

  useEffect(() => {
    modalForm.setFieldValue('promptVarData', promptVarData);
  }, [promptVarData]);

  useEffect(() => {
    if (!props.inspirationValues) return;
    setInspirationValues(props.inspirationValues);
    setTreeData(props.inspirationValues.category[0].children ? props.inspirationValues.category[0].children : []);
  }, [props.inspirationValues]);

  const updateTreeData = (value) => {
    setCacheTreeData(value);
  }

  useEffect(() => {
    if (!inspirationValues) return;
    const data = inspirationValues.inspirations.map(item => item.category?.split(':')[1]);
    setNodeList(data);
  }, [inspirationValues]);

  useEffect(() => {
    if (!treeData) return;
    setSelectTreeData(disableNodes(treeData));
  }, [treeData])

  const handleDeleteIns = (id) => {
    const newvalues = inspirationValues.inspirations.filter(item => item.id !== id);
    const newInspirationValues = { ...inspirationValues, inspirations: newvalues };
    updateData(newInspirationValues, 'inspiration');
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
      <div className='control-container'>
        <div className='control'>
          <div className='control-header '>
            <div className='control-title'>
              <span>{t('invalidTip')}</span>
            </div>
          </div>
          <Form.Item
            name={['inspiration', 'inspirations']}
            label=''
            style={{
              marginTop: '10px',
              display: showInspControl ? 'block' : 'none',
            }}
          >
            <div className='inspiration-add'>
              <Button type='link' onClick={onAddClick} icon={<PlusCircleOutlined />} >{t('createInspiration')}</Button>
            </div>
            {
              inspirationValues && inspirationValues.inspirations.map(item => (
                <div className='inspiration-container' key={item.id}>
                  <div className='card-title'>
                    <span className='left'>
                      {item.name}
                    </span>
                    <span className='right'>
                      <span onClick={() => clickInspiration(item)}>{t('modify')}</span>
                      <span onClick={() => handleDeleteIns(item.id)}>{t('delete')}</span>
                    </span>
                  </div>
                  <div className='card-prompt'>
                    {item.description}
                  </div>
                </div>
              ))
            }
          </Form.Item>
        </div>
        <Modal
          title={type !== 'edit'? t('createInspiration') : t('editInspiration')}
          open={showModal}
          onOk={handleModalOK}
          onCancel={handleModalCancel}
          forceRender
          maskClosable={false}
          width='1000px'>
          <div className='inspiration-wrap'>
            <Form
              form={modalForm}
              {...formItemLayout}
            >
              <Form.Item
                name='name'
                label={t('name')}
                rules={[
                  {
                    required: true,
                  }
                ]}
                style={{ marginBottom: '6px' }}
              >
                <Input placeholder={t('plsEnter')}
                  maxLength={20}
                  showCount />
              </Form.Item>
              <Form.Item
                name='description'
                label={t('description')}
                rules={[
                  {
                    required: true,
                  }
                ]}
                style={{ marginBottom: '16px' }}
              >
                <TextArea placeholder={t('plsEnter')} rows={3} />
              </Form.Item>
              <Form.Item
                name='prompt'
                label={t('promptName')}
                rules={[
                  {
                    required: true,
                  }
                ]}
                style={{ marginBottom: '16px' }}
              >
                <TextArea
                  placeholder={t('promptTextarea')}
                  rows={6}
                  onBlur={onPromptChange}
                />
              </Form.Item>
              <Form.Item
                name='promptTemplate'
                label={t('prompt')}
                rules={[
                  {
                    required: false,
                  }
                ]}
                style={{ marginBottom: '16px' }}
              >
                <TextArea
                  placeholder={t('plsEnter')}
                  rows={6}
                  onBlur={onPromptChange}
                />
              </Form.Item>
              <Form.Item
                name='promptVarData'
                label={t('promptVar')}
                style={{ display: promptVar.length ? 'block' : 'none', marginTop: '10px' }}
              >
                <Table columns={columns} dataSource={promptVarData} />
              </Form.Item>
              <Form.Item
                name='category'
                label={t('classify')}
                style={{
                  flex: 5
                }}
              >
                <div style={{
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center'
                }}>
                  <TreeSelect
                    treeData={selectTreeData}
                    treeDefaultExpandAll
                    fieldNames={{
                      label: 'title',
                      value: 'parent'
                    }}
                    value={category}
                    onSelect={handleChangeCategory}
                  />
                  <PlusOutlined className='plus-icon' style={{ flex: 1, fontSize: '14px' }} onClick={openCategoryModal} />
                </div>
              </Form.Item>
              <Form.Item
                name='auto'
                label={
                  <div>
                    <span>{t('automatic')}</span>
                    <Popover content={<div>{t('automaticDesc')}<br />
                      {t('automaticDesc2')} </div>}>
                      <QuestionCircleOutlined />
                    </Popover>
                  </div>
                }
                checked={modalForm.getFieldValue('auto')}
              >
                <Switch />
              </Form.Item>
            </Form>
          </div>
        </Modal>
        <Modal
          title={t('categoryConfiguration')}
          destroyOnClose
          open={showCateModal}
          onOk={handleCateModalOK}
          onCancel={handleCateModalCancel}
          maskClosable={false}
          width='1000px'>
          <TreeComponent tree={treeData}
            nodeList={nodeList}
            updateTreeData={updateTreeData}
            setDisabled={setDisabled}
            category={category}
          />
        </Modal>
      </div>
    </>
  )
};


export default Inspiration;
