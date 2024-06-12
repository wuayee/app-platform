
import React, { useEffect, useState, useRef, useContext } from 'react';
import { getKnowledges } from "@shared/http/appBuilder";
import { Form, Select, Button, TreeSelect  } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import { ConfigFormContext } from '../../../aippIndex/context';
import AddKnowledge from  './add-knowledge';

const Knowledge = (props) => {
  const { knowledge, updateData } = props;
  const [ showKnowControl, setShowKnowControl ] = useState(true);
  const [ knowledgeOptions, setKnowledgeOptions ] = useState(null);
  const [ knows, setKnows] = useState([]);
  const { tenantId } = useContext(ConfigFormContext);
  const searchName = useRef('');
  const list = useRef('');
  const modalRef = useRef();
  
  const handleGetKnowledgeOptions = () => {
    const params = {
      tenantId,
      pageNum: 1,
      pageSize: 10,
      name: searchName.current
    };
    getKnowledges(params).then((res) => {
      if (res.code === 0) {
        setKnowledgeOptions(res.data.items);
      }
    })
  }

  const handleChange = (value) => {
    list.current = value;
    setKnows(value)
    updateData(value, "knowledge");
  }
  // 删除
  const deleteItem = (item) => {
    list.current = list.current.filter(Litem => Litem.tableId !== item.tableId);
    setKnows([ ...list.current ]);
    updateData(list.current, "knowledge");
  }
  useEffect(() => {
    handleGetKnowledgeOptions();
  }, [])

  useEffect(() => {
    if (knowledge) {
      setKnows(knowledge);
      list.current = knowledge;
    }
    
  }, [knowledge])

  return (
    <>
      <div className="control-container">
        <div className="control">
          <div className="control-header">
            <div className="control-title">
              <Button onClick={ () => modalRef.current.showModal() }>添加</Button>
            </div>
          </div>
          <div className="control-inner">
            {
              knows.map((item, index) => {
                return (
                  <div className="item" key={index} >
                    <span>{item.name}</span>
                    <span>
                      <CloseOutlined style={{ cursor: 'pointer', fontSize: '14px', color: '#4D4D4D' }} onClick={() => { deleteItem(item) }} />
                    </span>
                  </div>
                )
              })
            }
          </div>
        </div>
      </div>
      <AddKnowledge 
        modalRef={modalRef} 
        tenantId={tenantId} 
        handleDataChange={handleChange}
        checkData={knows}
      />
    </>
  )
};


export default Knowledge;
