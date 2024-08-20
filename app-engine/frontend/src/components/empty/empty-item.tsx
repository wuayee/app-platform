import React from 'react';
import { Icons } from '../icons';
import i18n from '../../locale/i18n';

const EmptyItem = ({ text = i18n.t('noData') }) => {
  return (
    <>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Icons.emptyIcon />
        <div style={{ margin: '12px 0' }}>{text}</div>
      </div>
    </>
  );
}

export default EmptyItem;
