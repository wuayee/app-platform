import React from 'react';
import './segment-preview.scoped.scss';
 
interface PreviewItem {
  title: string;
  content: string;
  chars: string;
}
 
const SegmentPreview: React.FC<{ data: PreviewItem[] }> = ({ data }) => {
  return (
    <div className='segment-preview'>
      <div className='segment-preview-header'>分段预览</div>
      <div className='preview-wrapper'>
        {data.map((item) => (
          <div className='preview-item'>
            <div className='preview-item-title'>{item.title}</div>
            <div className='preview-item-content'>{item.content}</div>
            <div className='preview-item-chars'>{item.chars}字符</div>
          </div>
        ))}
      </div>
    </div>
  );
};
 
export default SegmentPreview;
