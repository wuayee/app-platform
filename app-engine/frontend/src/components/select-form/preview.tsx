import React from 'react';
import './style.scoped.scss';

const Preview = () => {
  const data = new Array(10).fill('sderet.h');
  return (
    <div className='preview'>
      <div className='preview-title'>储存数据01</div>
      <div className='preview-data-wrapper'>
        {data.map((item) => (
          <div className='preview-data-item'>{item}</div>
        ))}
      </div>
    </div>
  );
};

export default Preview;
