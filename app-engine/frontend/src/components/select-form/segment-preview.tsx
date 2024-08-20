import React from 'react';
import { useTranslation } from 'react-i18next';
import './segment-preview.scoped.scss';

interface PreviewItem {
  title: string;
  content: string;
  chars: string;
}
const SegmentPreview: React.FC<{ data: PreviewItem[] }> = ({ data }) => {
  const { t } = useTranslation();
  return (
    <div className='segment-preview'>
      <div className='segment-preview-header'>{t('segmentPreview')}</div>
      <div className='preview-wrapper'>
        {data.map((item) => (
          <div className='preview-item'>
            <div className='preview-item-title'>{item.title}</div>
            <div className='preview-item-content'>{item.content}</div>
            <div className='preview-item-chars'>{item.chars}{t('character')}</div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default SegmentPreview;
