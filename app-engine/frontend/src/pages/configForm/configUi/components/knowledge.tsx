
import React, { useEffect, useState, useRef } from 'react';
import { useParams } from 'react-router-dom';
import { Button } from 'antd';
import { CloseOutlined } from '@ant-design/icons';
import AddKnowledge from './add-knowledge';
import { useTranslation } from 'react-i18next';

const Knowledge = (props) => {
  const { t } = useTranslation();
  const { knowledge, updateData } = props;
  const [knows, setKnows] = useState([]);
  const { tenantId } = useParams();
  const list = useRef('');
  const modalRef = useRef();

  const handleChange = (value) => {
    list.current = value;
    setKnows(value)
    updateData(value, 'knowledge');
  }
  // 删除
  const deleteItem = (item) => {
    list.current = list.current.filter(Litem => Litem.tableId !== item.tableId);
    setKnows([...list.current]);
    updateData(list.current, 'knowledge');
  }

  useEffect(() => {
    if (knowledge) {
      setKnows(knowledge);
      list.current = knowledge;
    }
  }, [knowledge])

  return (
    <>
      <div className='control-container'>
        <div className='control'>
          <div className='control-header'>
            <div className='control-title'>
              <Button onClick={() => modalRef.current.showModal(knows)}>{t('additions')}</Button>
            </div>
          </div>
          <div className='control-inner'>
            {
              knows.map((item, index) => {
                return (
                  <div className='item' key={index} >
                    <span className='text'>{item.name}</span>
                    <span>
                      <CloseOutlined
                        style={{ cursor: 'pointer', fontSize: '14px', color: '#4D4D4D' }}
                        onClick={() => { deleteItem(item) }}
                      />
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
      />
    </>
  )
};


export default Knowledge;
