import React, { useEffect, useRef, useState } from 'react';
import { Button, Space, DatePicker, Drawer } from 'antd';
import './style.scoped.scss';
import { CloseOutlined } from '@ant-design/icons';
import TableHW, { getColumnSearchProps } from '../../../components/table';
import { feedbackType } from './model';
import { AppIcons } from '../../../components/icons/app';

const feedbackIcon = {
  0: <AppIcons.UnFeedbackIcon style={{ verticalAlign: 'text-bottom' }} />,
  1: <AppIcons.LikeIcon style={{ verticalAlign: 'text-bottom' }} />,
  2: <AppIcons.DisLikeIcon style={{ verticalAlign: 'text-bottom' }} />,
};

const basicInfoCols = [
  {
    key: 'time',
    label: '时间',
  },
  {
    key: 'speed',
    label: '相应速度',
  },
  {
    key: 'user',
    label: '用户',
  },
  {
    key: 'department',
    label: '部门',
  },
  {
    key: 'feedback',
    label: '用户反馈',
    render: (value) => (
      <div>
        <span>{feedbackType[value]}</span> {feedbackIcon[value]}
      </div>
    ),
  },
];

const FeedBack = () => {
  const [open, setOpen] = useState(false);
  const [data, setData] = useState([]);
  const [searchParams,setSearchParams]=useState({});
  const currentRow = useRef(null);
  const refreshData = () => {
    const dataSource = new Array(100).fill(1).map((i) => {
     return {
      id: i,
      question: '这是一个很长长长长长长长长长长长长长长长长长长长长的问题',
      answer: '回答',
      time: '2024-03-04 14:33:23',
      speed: '20ms',
      user: `用户${i}`,
      department: '部门',
      feedback: i % 3,
      }
    });
    setData(dataSource);
  };
  useEffect(() => {
    refreshData();
  }, [searchParams]);
  const handleChange: void = (pagination, filters, sorter) => {
    setSearchParams({...searchParams,pagination,filters,sorter})
    console.log('Various parameters', pagination, filters, sorter);
  };
  const columns = [
    {
      title: '用户提问',
      dataIndex: 'question',
      key: 'question',
      width: 300,
      ellipsis: true,
      ...getColumnSearchProps('question'),
    },
    {
      title: '应用问答',
      dataIndex: 'answer',
      key: 'answer',
      width: 300,
      ellipsis: true,
      ...getColumnSearchProps('answer'),
    },
    {
      title: '时间',
      dataIndex: 'time',
      key: 'time',
      width: 200,
    },
    {
      title: '相应速度',
      dataIndex: 'speed',
      key: 'speed',
      sorter: (a, b) => a.speed - b.speed,
    },
    {
      title: '用户',
      dataIndex: 'user',
      key: 'user',
    },
    {
      title: '部门',
      dataIndex: 'department',
      key: 'department',
    },
    {
      title: '用户反馈',
      dataIndex: 'feedback',
      key: 'feedback',
      render: (value, record) => (
        <div>
          {feedbackIcon[value]} <span>{feedbackType[value]}</span>{' '}
        </div>
      ),
    },
    {
      title: '操作',
      dataIndex: 'operate',
      key: 'operate',
      render: (value, record) => (
        <a
          onClick={() => {
            currentRow.current = record;
            setOpen(true);
          }}
        >
          详情
        </a>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 14 }}>
        <DatePicker.RangePicker
          showTime
          onChange={(_date, dateString) => {
            setSearchParams({...searchParams,date:dateString})
            console.log(dateString);
          }}
        />
        <Button type='primary'>导出</Button>
      </div>
      <TableHW dataSource={data} columns={columns} onChange={handleChange} scroll={{ y: 'calc(100vh - 320px)' }}/>
      <Drawer
        title='反馈详情'
        placement='right'
        size='large'
        closeIcon={false}
        onClose={false}
        open={open}
        extra={
          <CloseOutlined
            onClick={() => {
              setOpen(false);
            }}
          />
        }
        footer={
          <Button
            style={{ float: 'right', width: 90 }}
            onClick={() => {
              setOpen(false);
            }}
          >
            关闭
          </Button>
        }
      >
        <div className='drawer-title'>基本信息</div>
        <div
          style={{
            display: 'flex',
            flexWrap: 'wrap',
            whiteSpace: 'normal',
            wordBreak: 'break-all',
            marginTop: '10px',
          }}
        >
          {basicInfoCols.map((item) => (
            <div style={{ width: 'calc((100%) / 3)', marginBottom: 10 }}>
              <div style={{ color: '#4d4d4d', fontSize: 12 }}>{item.label}</div>
              <div style={{ marginTop: '5px', fontSize: '14px' }}>
                {item.render?.(currentRow.current?.[item.key]) || currentRow.current?.[item.key]}
              </div>
            </div>
          ))}
        </div>
        <div className='drawer-title'>问答详情</div>
        <div className='drawer-sub-title'>用户提问</div>
        <div className='question-card '>天舟的按时发大发？</div>
        <div className='drawer-sub-title'>用户回答</div>
        <div className='answer-card'>
          哈利·波特，是英国女作家J.K.罗琳的魔幻系列小说《哈利·波特》系列及其衍生作品中的主人公，是詹姆·波特和莉莉·波特（原名莉莉·伊万斯）的独生子，出生于1980年7月31日，成年后身高182cm，教父为小天狼星布莱克（Sirius
          Black），或者说西里斯·布莱克。魔杖长11英寸，冬青木，杖芯是凤凰福克斯的尾羽。
          身怀母亲莉莉用生命施加的只保护哈利的保护咒，可保护自身不受伏地魔伤害。因只有待在有母族血缘的身边，血缘魔法才能生效，所以被麻瓜弗农·德思礼姨夫与佩妮·德思礼（原名佩妮·伊万斯）姨妈收养，就读于霍格沃茨魔法学校格兰芬多学院，曾两次在阿瓦达索命下依然存活，被称为“大难不死的男孩”（The
          boy who lived）。
        </div>
        <div className='drawer-title'>用户反馈</div>
        <div className='drawer-sub-title'>用户提问</div>
        <div className='question-card '>天舟的按时发大发？</div>
        <div className='drawer-sub-title'>用户回答</div>
        <div className='answer-card'>
          哈利·波特，是英国女作家J.K.罗琳的魔幻系列小说《哈利·波特》系列及其衍生作品中的主人公，是詹姆·波特和莉莉·波特（原名莉莉·伊万斯）的独生子，出生于1980年7月31日，成年后身高182cm，教父为小天狼星布莱克（Sirius
          Black），或者说西里斯·布莱克。魔杖长11英寸，冬青木，杖芯是凤凰福克斯的尾羽。
          身怀母亲莉莉用生命施加的只保护哈利的保护咒，可保护自身不受伏地魔伤害。因只有待在有母族血缘的身边，血缘魔法才能生效，所以被麻瓜弗农·德思礼姨夫与佩妮·德思礼（原名佩妮·伊万斯）姨妈收养，就读于霍格沃茨魔法学校格兰芬多学院，曾两次在阿瓦达索命下依然存活，被称为“大难不死的男孩”（The
          boy who lived）。
        </div>
      </Drawer>
    </div>
  );
};

export default FeedBack;
