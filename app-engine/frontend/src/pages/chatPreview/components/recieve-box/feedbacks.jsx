
import React, { useEffect, useState } from 'react';
import { LikeIcon, UnlikeIcon } from '@/assets/icon';
import './styles/feedbacks.scss';

const Feedbacks = () => {
return <>{(
    <div className="feed-inner">
      <div className="feed-left"></div>
      <div className="feed-right">
        <LikeIcon />
        <UnlikeIcon />
      </div>
    </div>
  )}</>
};


export default Feedbacks;
