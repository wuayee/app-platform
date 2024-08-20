import React from 'react';
import { Flex } from 'antd';
import { Icons } from '../icons';
import i18n from '../../locale/i18n';

const EmptyItem = ({ text = i18n.t('noData') }) => {
  return (
    <>
      <Flex vertical align={'center'}>
        <Icons.emptyIcon />
        <div style={{ margin: '12px 0' }}>{text}</div>
      </Flex>
    </>
  );
}

export default EmptyItem;
