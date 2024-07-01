import { CloseOutlined } from '@ant-design/icons';
import { Button, Checkbox, Drawer, Select, Table, Tag } from 'antd';
import React, { useEffect, useState } from 'react';
import '../style.scoped.scss';
import DraggerUpload from '../../../components/draggerUpload';
import { GetProp } from 'antd/lib';
import { uploadPlugin } from '../../../shared/http/plugin';

const data = [
  {
    name: '笔记',
    tags: ['FIT', 'HTTP'],
    data: [
      {
        name: 1,
        type: 2,
        des: 3,
      },
    ],
    des: 'aaaaaaaaaaaaaaaaaaa',
  },
];

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

const UploadToolDrawer = ({ openSignal }) => {
  const [open, setOpen] = useState(false);
  const [result,setResult] =useState([]);
  const [checkedList,setCheckedList]=useState([]);
const onCheckChange: GetProp<typeof Checkbox.Group, 'onChange'> = async(checkedValues) => {
  setCheckedList(checkedValues);
};
  useEffect(() => {
    if (openSignal > 0) {
      setOpen(true);
    }
  }, [openSignal]);

  const onChangeSpace = (value) => {};

  return (
    <Drawer
      title='上传工具'
      placement='right'
      closeIcon={false}
      onClose={false}
      width={500}
      maxCount={3}
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
            onClick={async() => {
              await uploadPlugin(checkedList);
              setOpen(false);
            }}
          >
            确定
          </Button>
        </div>
      }
    >
      <div>
        上传至：
        <Select
          disabled={true}
          defaultValue='user'
          className='select-space'
          onChange={onChangeSpace}
          options={uploadSpaceOptions}
        />
        <DraggerUpload accept='.zip,.tar,.jar' maxCount={1} setResult={setResult}/>
        <Checkbox.Group style={{ width: '100%' }} onChange={onCheckChange}>
        {result?.map((item) => (
          <div className='param-card' key={item?.methodName}>
            <div style={{ float: 'right' }}>
              <Checkbox value={item}/>
            </div>
            <div className='card-header-left'>
              <img src='/src/assets/images/knowledge/knowledge-base.png' />
              <div>
                <div style={{ fontSize: 20, marginBottom: 8 }}>{item?.methodName}</div>
                <div className='card-user'>
                  {item?.tags?.map((tag: string, index: number) => (
                    <Tag style={{ margin: 0 }} key={index}>
                      {tag}
                    </Tag>
                  ))}
                </div>
              </div>
            </div>
            <div className='card-des'>{item?.methodDescription}</div>
            <Table
              scroll={{ y: 120 }}
              dataSource={item?.parameterEntities}
              columns={columns}
              virtual
              pagination={false}
            />
          </div>
        ))}
        </Checkbox.Group>
      </div>
    </Drawer>
  );
};

export default UploadToolDrawer;
