
import React, { useEffect, useState, useRef } from 'react';
import { Popover, Button } from 'antd';
import { PlusSquareOutlined } from '@ant-design/icons';
import { Message } from '@/shared/utils/message';
import AddConditionForm from './add-condition-form';
import { useTranslation } from 'react-i18next';

const AddCondition = (props) => {
  const { t } = useTranslation();
  const { conditions, getFormData, save } = props;
  const [popoverShow, setPopoverShow] = useState(false);
  const [formData, setFormData] = useState('');
  const addRef = useRef();

  const onOpenChange = async (open) => {
    let data = JSON.stringify(getFormData());
    await setFormData(data);
    setPopoverShow(open);
  }
  const handleConfirm = () => {
    let filterItem = addRef.current.getFilterData();
    if (filterItem.value.length === 0) {
      Message({ type: 'warning', content: t('selectionEmpty') });
      return
    }
    save(filterItem);
    setPopoverShow(false);
  }
  return <>
    <Popover
      trigger='click'
      placement='bottomLeft'
      arrow={false}
      open={popoverShow}
      onOpenChange={onOpenChange}
      destroyTooltipOnHide={true}
      content={
        <div>
          <AddConditionForm
            formData={formData}
            popoverShow={popoverShow}
            conditions={conditions}
            addRef={addRef}
          />
          <div className='action-menu'>
            <Button size='small' onClick={() => setPopoverShow(false)}>{t('cancel')}</Button>
            <Button size='small' type='primary' onClick={handleConfirm}>{t('ok')}</Button>
          </div>
        </div>
      }>
      <PlusSquareOutlined style={{ cursor: 'pointer', fontSize: '20px', color: '#2673e5' }} />
    </Popover>
  </>
};


export default AddCondition;
