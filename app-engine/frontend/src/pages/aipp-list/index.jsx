
import React, { useEffect, useState, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button, Spin, Input, Pagination } from 'antd';
import {
  PlusOutlined,
} from '@ant-design/icons';
import { getAippList, createAipp } from '../../shared/http/aipp';
import { UserIcon } from '../../assets/icon';
import EditModal from '../components/edit-modal.jsx';
import robot from '../../assets/images/ai/robot1.png';
import './home.scss';

const { Search } = Input;
const Home = () => {
  const [ tableData, setTableData ] = useState([]);
  const [ tableLoading, setTableLoading ] = useState(false);
  const [ aippInfo, setAippInfo ] = useState({});
  const [ total, setTotal ] = useState(1);
  const [ current, setCurrent] = useState(1);
  const filterParams = useRef({});
  const pageNo = useRef(1);
  const employeeNumber = localStorage.getItem('currentUserId') || null;
  const tenantId = '727d7157b3d24209aefd59eb7d1c49ff';
  let modalRef = useRef();
  const navigate = useNavigate();

  useEffect(() => {
    getAippData();
  }, []);

  // 获取列表接口
  async function getAippData(filters = undefined) {
    setTableLoading(true)
    // if (filters) {
    //   let params = {};
    //   for (const key in filters) {
    //     filters[key].value.text && (params[key] = filters[key].value.text);
    //   }
    //   if (
    //     Object.keys(params).length ||
    //     (!Object.keys(params).length && Object.keys(filterParams.current).length)
    //   ) {
    //     pageNo.current = 1;
    //     setCurrent(pageNo.current)
    //   }
    //   filterParams.current = params;
    // }
    try {
      const res = await getAippList(tenantId, {
        offset: (pageNo.current - 1) * 20,
        limit: 20,
        creator: employeeNumber,
        ...filterParams.current,
      });
      if (res.code === 0) {
        let data = res.data.results.map((item) => {
          item.version = item.version || '1.0.0';
          return item;
        });
        setTableData(data);
        setTotal(res.data.range.total)
      }
    } finally {
      setTableLoading(false);
    }
  }
  const onSearch = (val) => {
    console.log(val);
  }
  const onChange = (page) => {
    setCurrent(() => {
      pageNo.current = page;
      getAippData();
      return page;
    })
  }
  // 新增aipp
  function addClick() {
    setAippInfo(() => {
      modalRef.current.showModal();
      return {
        name: '',
        attributes: {
          description: '',
          greeting: '',
          icon: '',
          app_type: '编程开发',
        }
      }
    })
  }
  function addAippCallBack(appId) {
    navigate(`/app/${tenantId}/detail/${appId}`);
  }
  return <>{(
    <div className="home-content">
      <div className="home-h1">模型应用市场</div>
      <div className="home-search">
        <Search  placeholder="请输入" allowClear onSearch={onSearch} />
        <Button type="primary" className="refresh btn" onClick={getAippData}>刷新</Button>
        <Button type="primary" icon={<PlusOutlined />} className="btn" onClick={addClick}>新建应用</Button>
      </div>
      <Spin spinning={tableLoading}>
        <div className="home-list">
          {
            tableData.map((item, index) => {
              return <AppItem key={index} item={item} />
            })
          }
        </div>
      </Spin>
      <div className="home-page">
        { total > 20 &&  <Pagination
          current={current}
          pageSize={20}
          onChange={onChange}
          showSizeChanger={false}
          total={total}
          showTotal={(total) => `总条数 ${total}`}
        />}

      </div>
      <EditModal type="add" modalRef={modalRef} aippInfo={aippInfo} addAippCallBack={addAippCallBack}/>
    </div>
  )}</>
};

const AppItem = (props) => {
  const { name, id, state, updateAt, createBy, attributes } = props.item;
  const { app_type, description, icon } = attributes;
  const navigate = useNavigate();
  const tenantId = '727d7157b3d24209aefd59eb7d1c49ff';

  function itemClick() {
    navigate(`/app/${tenantId}/detail/${id}`);
  }
  return <>{(
    <div className="app-item" onClick={itemClick}>
      <div className="app-header">
        <span className="item-avatar">{ icon ? <img src={icon}/> : <img src={robot}/> }</span>
        <div className="item-title">
          <div className="title">{ name }</div>
          <div className="tag">
           <div className="complete" >{ state === 'published' ? '已发布' : '未发布'}</div>
           <div className="green">{app_type}</div>
          </div>
        </div>
      </div>
      <div className="app-desc">{ description}</div>
      <div className="app-user">
        <UserIcon />
        <div className="create-msg">
          <span> { createBy } | 创建于 </span>
          <span>{ updateAt }</span>
        </div>
      </div>
    </div>
  )}</>
}

export default Home;
