import React, { useEffect, useRef, useState } from 'react';
import { CloseOutlined } from '@ant-design/icons';
import { Button, Checkbox, Drawer, Select, Table, Tag } from 'antd';
import { GetProp } from 'antd/lib';
import {v4 as uuidv4} from 'uuid';
import DraggerUpload from '@/components/draggerUpload';
import { uploadPlugin } from '@shared/http/plugin';
import { Message } from '@shared/utils/message';
import '../style.scoped.scss';

const uploadSpaceOptions = [
  { value: 'user', label: '个人空间' },
  { value: 'team', label: '某个团队' },
];

const columns = [
  {
    title: '参数名',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '参数类型',
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: '参数说明',
    dataIndex: 'description',
    key: 'description',
  },
];

const UploadToolDrawer = ({ openSignal, refreshPluginList }) => {
  const [open, setOpen] = useState(false);
  const [result,setResult] =useState([]);
  const [checkedList,setCheckedList]=useState([]);
  const [fileList,setFileList]=useState([]);
  const [loading,setLoading]=useState(false);
  const pluginList = useRef([]);
  const fileData = useRef([]);
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
  
  const onChangeSpace = (value) => {};
  // 添加数据
  const addFileData = (data, file) => {
    if (fileData.current.length > 4) return;
    fileData.current = [...fileData.current, file];
    setFileList(fileData.current);
    Object.keys(data).forEach(key => {
      data[key].forEach(item => {
        let pluginObj:any = {
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
              id: uuidv4() ,
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
      Message({ type: 'warning', content: '未选择插件' })
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
        Message({ type: 'success', content: '添加插件成功' });
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
      title='上传工具'
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
            onClick={() => {
              setOpen(false);
            }}
          >
            取消
          </Button>
          <Button
            style={{ width: 90, backgroundColor: '#2673e5', color: '#ffffff' }}
            onClick={confirm}
            loading={loading}
          >
            确定
          </Button>
        </div>
      }
    >
      <div className='upload-info-head'>
        <img src='/src/assets/images/ai/info-upload.png' />
        <span>建议用户在本地调试插件，避免部署失败；相同工具将会覆盖。</span>
      </div>
      <div>
        上传至：
        <Select
          disabled={true}
          defaultValue='user'
          className='select-space'
          onChange={onChangeSpace}
          options={uploadSpaceOptions}
        />
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
              <div className={['collapse-plugin', item.open ? 'collapse-plugin-open': ''].join(' ')} key={item.key}>
                <div className='collapse-plugin-head' onClick={() => itemClick(item)}>
                  <div className='head-left'>
                    <img src='/src/assets/images/ai/down.png' />
                    <span>{item.name}</span>
                  </div>
                  <div className='head-right'>
                    <img src='/src/assets/images/ai/complate.png' />
                    <span className='text'>解析成功</span>
                    <span>{item.list.length}/{item.checkedNum}</span>
                  </div>
                </div>
                <div className='collapse-plugin-content'>
                  {item.list?.map((lItem, index) => (
                    <div className='param-card' key={index}>
                      <div style={{ float: 'right' }}>
                        <Checkbox value={lItem}/>
                      </div>
                      <div className='card-header-left'>
                        <img src='/src/assets/images/knowledge/knowledge-base.png' />
                        <div>
                          <div style={{ fontSize: 20, marginBottom: 8, wordBreak: 'break-all' }}>{lItem.schema?.name}</div>
                          <div className='card-user'>
                            {lItem?.tags?.map((tag: string, index: number) => (
                              <Tag style={{ margin: 0 }} key={index}>
                                {tag}
                              </Tag>
                            ))}
                            <span className='card-detail-btn' onClick={() => pluginDetailClick(item, lItem)}>查看参数</span>
                          </div>
                        </div>
                      </div>
                      <div className='card-des'>{lItem?.schema?.description}</div>
                      <div className='card-table' style={{ display: lItem.open ? 'block' : 'none' }}>
                        <Table
                          dataSource={lItem?.parameterEntities}
                          columns={columns}
                          virtual
                          rowKey='name'
                          pagination={false}
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
    </Drawer>
  );
};

export default UploadToolDrawer;
