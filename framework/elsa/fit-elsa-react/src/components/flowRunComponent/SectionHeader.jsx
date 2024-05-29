import {Button, message} from "antd";
import {CopyOutlined} from "@ant-design/icons";

/**
 * 章节头部
 *
 * @param section 章节对象
 * @param shape 图形
 * @return {JSX.Element}
 * @constructor
 */
export default function SectionHeader({section, shape}) {
    /**
     * 复制按钮的回调
     *
     * @param text 文本信息
     */
    const handleCopy = (text) => {
        navigator.clipboard.writeText(text)
                .then(() => {
                    message.success('复制成功');
                })
                .catch(err => {
                    console.error('Failed to copy: ', err);
                    message.error('复制失败!');
                });
    };
    return (
            <div className="section-header">
                <div className="section-title">{section.name}</div>
                <Button className={"copy-button"}  type="text"
                        icon={<CopyOutlined className="copy-button-icon"/>}
                        onClick={() => handleCopy(JSON.stringify(section.data))}/>
            </div>
    );
}