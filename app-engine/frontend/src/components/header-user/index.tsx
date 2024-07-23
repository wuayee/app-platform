import React from 'react';
import { KnowledgeIcons } from '../icons';
import './style.scoped.scss';

const HeaderUser = () => {
  return (
    <div className='header-user-wrapper'>
      <KnowledgeIcons.dark />
      <KnowledgeIcons.alarm />
      <KnowledgeIcons.info />
    </div>
  );
};

export { HeaderUser };
