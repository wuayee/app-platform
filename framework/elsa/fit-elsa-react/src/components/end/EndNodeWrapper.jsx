import OutputVariable from "@/components/end/OutputVariable.jsx";

/**
 * 用来封装结束节点子组件的最顶层组件
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function EndNodeWrapper() {
    return (<div style={{backgroundColor: 'white'}}>
        <OutputVariable/>
    </div>)
}