import SectionHeader from "@/components/flowRunComponent/SectionHeader.jsx";
import SectionContent from "@/components/flowRunComponent/SectionContent.jsx";
import {v4 as uuidv4} from "uuid";
import {Card} from "antd";
import {SECTION_TYPE} from "@/common/Consts.js";

/**
 * 构造运行报告的章节
 *
 * @param shape 图形
 * @return {*}
 * @constructor
 */
export default function SectionFactory({shape}) {

    /**
     * 构造默认章节
     *
     * @param section 章节信息
     * @return {JSX.Element}
     */
    const buildDefaultSection = section => (<div key={`section-${section.no}-${uuidv4()}`} className="section">
        <SectionHeader section={section} shape={shape}/>
        <div className="json-tree-section default-json-tree-section-width">
            <SectionContent data={section.data}/>
        </div>
    </div>);

    /**
     * 构造条件章节
     *
     * @param section 章节信息
     * @return {JSX.Element}
     */
    const buildConditionSection = section => (
            <div key={`section-${section.no}-${uuidv4()}`} className="section">
                <SectionHeader section={section} shape={shape}/>
                {section.data.conditions
                        .filter(c => c.condition !== "true" || c.left)
                        .map((condition, index) => {
                            const {key, value} = condition.left;
                            const separatorIndex = key.indexOf(".");
                            const referenceNodeId = key.substring(0, separatorIndex);
                            const text = shape.page.getShapeById(referenceNodeId).text;
                            const newKey = text + key.substring(separatorIndex);
                            let isShowLogic = section.data.conditions.length > 1 && index < section.data.conditions.length - 1;
                            return (<div key={`condition-container-${section.no}-${uuidv4()}`}
                                         className={"condition-container"}>
                                        <div key={`condition-card-${index}-${uuidv4()}`} className="condition-card">
                                            <div className="json-tree-section condition-json-tree-section-width">
                                                <SectionContent data={{[newKey]: value}}/>
                                            </div>
                                            <svg className="line-svg">
                                                <line x1="50%" y1="0" x2="50%" y2="100%" stroke="rgba(29,28,35,.16)"/>
                                            </svg>
                                            <Card className="center-card">
                                                <div className="center-text">{condition.condition}</div>
                                            </Card>
                                            <div className="json-tree-section condition-json-tree-section-width">
                                                <SectionContent data={condition.right ? condition.right.value : {}}/>
                                            </div>
                                        </div>
                                        {isShowLogic && <div className={"condition-logic"}>
                                            {section.data.conditionRelation}
                                        </div>}
                                    </div>);
                        })}
            </div>);

    /**
     * 根据图形类型获取测试报告
     *
     * @return {*}
     */
    const getReportSections = () => {
        return shape.getRunReportSections().map(section => {
            // 这里改为根据type来决定，如何绘制
            if (section.type === SECTION_TYPE.DEFAULT) {
                return buildDefaultSection(section);
            } else if (section.type === SECTION_TYPE.CONDITION) {
                return buildConditionSection(section);
            }
        });
    };

    return (getReportSections());
}