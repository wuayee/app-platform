import React, { useEffect, useRef, useState } from 'react';
import { CloseOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import { Button, Checkbox, Drawer, Select, Table, Tag, Spin, Empty, Tooltip } from 'antd';
import { GetProp } from 'antd/lib';
import { v4 as uuidv4 } from 'uuid';
import DraggerUpload from '@/components/draggerUpload';
import { uploadPlugin } from '@shared/http/plugin';
import { Message } from '@shared/utils/message';
import { useTranslation } from 'react-i18next';
import i18n from '@/locale/i18n';
import '../style.scoped.scss';

const uploadSpaceOptions = [
  { value: 'user', label: i18n.t('personalSpace') },
  { value: 'team', label: i18n.t('aTeam') },
];

const columns = [
  {
    title: i18n.t('paramName'),
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: i18n.t('paramType'),
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: i18n.t('paramDescription'),
    dataIndex: 'description',
    key: 'description',
  },
];

const UploadToolDrawer = ({ openSignal, refreshPluginList }) => {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [result, setResult] = useState([]);
  const [checkedList, setCheckedList] = useState([]);
  const [fileList, setFileList] = useState([]);
  const [loading, setLoading] = useState(false);
  const pluginList = useRef([]);
  const fileData = useRef([]);
  const tipsStr = t('zipDescription');
  const onCheckChange: GetProp<typeof Checkbox.Group, 'onChange'> = (checkedValues) => {
    pluginList.current.forEach(pItem => {
      pItem.checkedNum = 0;
      checkedValues.forEach(cItem => {
        if (cItem.uid === pItem.key) {
          pItem.checkedNum += 1
        }
      })
    });
    setResult([...pluginList.current]);
    setCheckedList(checkedValues);
  };

  const onChangeSpace = (value) => { };
  // 添加数据
  const addFileData = (data, file) => {
    if (fileData.current.length > 4) return;
    fileData.current = [...fileData.current, file];
    setFileList(fileData.current);
    Object.keys(data).forEach(key => {
      data[key].forEach(item => {
        let pluginObj: any = {
          open: true,
          checkedNum: 0
        };
        pluginObj.key = key;
        pluginObj.name = file.name;
        if (item.tools && Array.isArray(item.tools)) {
          let list = item.tools.map(tItem => {
            return {
              ...tItem,
              uid: key,
              open: false,
              id: uuidv4(),
              parameterEntities: setTableData(tItem.schema?.parameters)
            }
          });
          pluginObj.list = list;
        }
        pluginList.current.unshift(pluginObj);
        setResult([...pluginList.current]);
      });
    })
  }
  // 设置表格数据
  const setTableData = (data) => {
    let list = [];
    if (data.properties) {
      Object.keys(data.properties).forEach(item => {
        let aItem = { ...data.properties[item], name: item };
        list.push(aItem);
      })
    }
    return list
  }
  // 删除数据
  const removeFileData = (uid) => {
    pluginList.current = pluginList.current.filter(item => item.key !== uid);
    let checkList = checkedList.filter(item => item.uid !== uid);
    fileData.current = fileData.current.filter(item => item.uid !== uid);
    setResult(pluginList.current);
    setCheckedList(checkList);
    setFileList(fileData.current);
  }
  // 确定
  const confirm = () => {
    if (!checkedList.length) {
      Message({ type: 'warning', content: t('noPluginSelected') });
      return
    }
    let nameArr = [];
    let uidArr = [];
    checkedList.forEach(item => {
      nameArr.push(item.schema.name);
      !uidArr.includes(item.uid) && uidArr.push(item.uid);
    })
    let fileConfirmList = fileData.current.filter(item => uidArr.includes(item.uid));
    customRequest(fileConfirmList, nameArr);
  }
  // 上传文件
  const customRequest = (fileArr, nameArr) => {
    setLoading(true);
    let formData = new FormData();
    fileArr.forEach(item => {
      formData.append('file', item);
    });
    uploadPlugin(formData, nameArr.join(',')).then(res => {
      if (res.code === 0) {
        setOpen(false);
        Message({ type: 'success', content: t('addPluginSuccess') });
        refreshPluginList();
      } else {
        setLoading(false);
      }
    }).catch(() => {
      setLoading(false);
    });
  }
  const itemClick = (item) => {
    pluginList.current.forEach(pItem => {
      if (item.key === pItem.key) {
        pItem.open = !pItem.open
      }
    })
    setResult([...pluginList.current]);
  }
  const pluginDetailClick = (item, lItem) => {
    let pItemIndex = pluginList.current.findIndex(fItem => fItem.key === item.key);
    pluginList.current[pItemIndex].list.forEach(cItem => {
      if (cItem.id === lItem.id) {
        cItem.open = !cItem.open
      }
    });
    setResult([...pluginList.current]);
  }
  const dataReset = () => {
    pluginList.current = [];
    fileData.current = [];
    setOpen(true);
    setResult([]);
    setCheckedList([]);
    setFileList([]);
    setLoading(false);
  }
  useEffect(() => {
    if (openSignal > 0) {
      dataReset();
    }
  }, [openSignal]);

  return (
    <Drawer
      title={t('uploadTool')}
      placement='right'
      closeIcon={false}
      width={650}
      open={open}
      extra={
        <CloseOutlined
          onClick={() => {
            setOpen(false);
          }}
        />
      }
      footer={
        <div className='drawer-footer'>
          <Button
            style={{ width: 90 }}
            disabled={loading}
            onClick={() => {
              setOpen(false);
            }}
          >
            {t('cancel')}
          </Button>
          <Button
            style={{ width: 90, backgroundColor: '#2673e5', color: '#ffffff' }}
            onClick={confirm}
            loading={loading}
          >
            {t('ok')}
          </Button>
        </div>
      }
    >
      <div className='upload-info-head'>
        <img src='./src/assets/images/ai/info-upload.png' />
        <span>{t('uploadTip')}</span>
      </div>
      <Spin tip={t('uploadingPlugin')} size='small' spinning={loading}>
        <div>
          {t('uploadTo')}
          <Select
            disabled={true}
            defaultValue='user'
            className='select-space'
            onChange={onChangeSpace}
            options={uploadSpaceOptions}
          />
          <Tooltip
            color='#ffffff'
            placement='right'
            overlayInnerStyle={{ color: '#212121', padding: '12px', lineHeight: '24px' }}
            title={tipsStr}
            trigger='click'
          >
            <QuestionCircleOutlined style={{ fontSize: '18px', marginLeft: '12px' }} />
          </Tooltip>
          <DraggerUpload
            accept='.zip'
            multiple
            addFileData={addFileData}
            fileList={fileList}
            removeFileData={removeFileData}
          />
          <Checkbox.Group style={{ width: '100%' }} onChange={onCheckChange}>
            {
              result?.map(item => (
                <div className={['collapse-plugin', item.open ? 'collapse-plugin-open' : ''].join(' ')} key={item.key}>
                  <div className='collapse-plugin-head' onClick={() => itemClick(item)}>
                    <div className='head-left'>
                      <img src='./src/assets/images/ai/down.png' />
                      <span>{item.name}</span>
                    </div>
                    <div className='head-right'>
                      {item.list.length > 0 && <img src='./src/assets/images/ai/complate.png' />}
                      <span className='text'>{item.list.length > 0 ? t('parsedSuccessfully') : t('parsingFailed')}</span>
                      <span>{item.list.length}/{item.checkedNum}</span>
                    </div>
                  </div>
                  <div className='collapse-plugin-content'>
                    {item.list.length === 0 && <Empty description={t('noData')} />}
                    {item.list?.map((lItem, index) => (
                      <div className='param-card' key={index}>
                        <div style={{ float: 'right' }}>
                          <Checkbox value={lItem} />
                        </div>
                        <div className='card-header-left'>
                          <img src='./src/assets/images/knowledge/knowledge-base.png' />
                          <div>
                            <div style={{ fontSize: 20, marginBottom: 8, wordBreak: 'break-all' }}>{lItem.schema?.name}</div>
                            <div className='card-user'>
                              {lItem?.tags?.map((tag: string, index: number) => (
                                <Tag style={{ margin: 0 }} key={index}>
                                  {tag}
                                </Tag>
                              ))}
                              <span className='card-detail-btn' onClick={() => pluginDetailClick(item, lItem)}>{t('viewParam')}</span>
                            </div>
                          </div>
                        </div>
                        <div className='card-des'>{lItem?.schema?.description}</div>
                        <div className='card-table' style={{ display: lItem.open ? 'block' : 'none' }}>
                          <Table
                            dataSource={lItem?.parameterEntities}
                            columns={columns}
                            rowKey='name'
                            pagination={false}
                            scroll={{ y: 200 }}
                          />
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              ))
            }
          </Checkbox.Group>
        </div>
      </Spin>
    </Drawer>
  );
};

export default UploadToolDrawer;
