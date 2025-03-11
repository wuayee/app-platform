import React, { useEffect, useState, useRef, memo } from 'react';
import { UpOutlined } from '@ant-design/icons';
import ThinkBtn from './think-btn';
import './styles/think-block.scss';

const ThinkBlock = memo(({ content = '', thinkTime = '' }) => {
  let thinkEndIdx = content.indexOf('</think>');
  const thinkFinished = thinkEndIdx > -1;
  const [collapse, setcollapse] = useState(false);
  const thinkElRef = useRef<any>(null);

  const toggleFold = () => {
    if (!thinkFinished) {
      return;
    }
    if (!collapse) {
      thinkElRef.current.style.height = 0;
    } else {
      thinkElRef.current.style.height =
        thinkElRef.current.style.getPropertyValue('--height') + 'px';
    }
    setcollapse(!collapse);
  };

  useEffect(() => {
    if (thinkFinished && thinkElRef.current) {
      thinkElRef.current.style.setProperty('--height', thinkElRef.current.clientHeight);
      thinkElRef.current.style.height = thinkElRef.current.clientHeight + 'px';
    }
  }, [thinkFinished]);

  return (
    <>
      <div className='think-info-btn' onClick={toggleFold}>
        <ThinkBtn finished={thinkFinished} time={thinkTime} />
        {thinkFinished && <UpOutlined rotate={collapse ? 180 : 0} />}
      </div>
      <div
        className={[
          'think-info-html',
          thinkFinished ? 'think-info-html-finished' : '',
          collapse ? 'think-info-html-collapse' : '',
        ].join(' ')}
        ref={thinkElRef}
        dangerouslySetInnerHTML={{ __html: content }}
      ></div>
    </>
  );
});

export default ThinkBlock;
