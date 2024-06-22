import JadeCollapseInputTree from "@/components/common/JadeCollapseInputTree.jsx";
import {useDispatch} from "@/components/DefaultRoot.jsx";

/**
 * fit接口入参展示和入参赋值
 *
 * @returns {JSX.Element}
 */
export default function InvokeInput({inputData, disabled}) {
    const dispatch = useDispatch();

    /**
     * 更新input
     *
     * @param id 需要更新的值的id
     * @param changes 需要改变的属性
     */
    const updateItem = (id, changes) => {
        dispatch({type: "update", id, changes});
    };

    return (<JadeCollapseInputTree data={inputData} updateItem={updateItem} disabled={disabled}/>);
}