import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Icons } from '../icons';

const GoBack: React.FC<{path,title}> = ({path,title}) => {
  const navigate=useNavigate();
  return(
  <div
    onClick={(): void => {
      navigate(path);
    }}
  >
    <Icons.Left />
    <span style={{marginLeft:8}}>{title}</span>
  </div>
)};

export default GoBack;