import React, { memo } from 'react';
import { LoadingOutlined } from '@ant-design/icons';
import BrainImg from '@/assets/images/ai/brain.svg';

const ThinkBtn = memo(({ finished, time }) => {
  const transTime = (milliseconds: number | '') => {
    if (!milliseconds) {
      return '';
    }
    if (milliseconds < 1000) return `（用时${(milliseconds / 1000).toFixed(2)}秒）`;
    let seconds = Math.floor(milliseconds / 1000);
    let minutes = Math.floor(seconds / 60);
    let hours = Math.floor(minutes / 60);

    seconds %= 60;
    minutes %= 60;
    let text = '（用时';
    if (hours > 0) {
      text += hours + '时';
    }
    if (minutes > 0) {
      text += minutes + '分';
    }
    if (seconds > 0) {
      text += seconds + '秒';
    }
    text += '）';
    return text;
  };

  return (
    <>
      <img src={BrainImg} alt='' />
      <span>深度思考{finished ? transTime(time) : '中'}</span>
      {!finished && <LoadingOutlined />}
    </>
  );
});

export default ThinkBtn;
