import {useDataContext} from "@/components/DefaultRoot.jsx";
import {JadeObservableTree} from "../common/JadeObservableTree.jsx";

/**
 * demo
 */
export const listener3Component = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            "output": [{
                id: "uuid1",
                name: "person",
                type: "Object",
                value: [{id: "uuid2", name: "name", type: "String"}, {id: "uuid3", name: "age", type: "Integer"}]
            }]
        };
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><Component/></>);
    };

    /**
     * 必须.
     */
    self.reducers = () => {
    };

    return self;
};

const Component = () => {
    const data = useDataContext();

    return (<>
        <div>
            <JadeObservableTree data={data.output}/>
        </div>
    </>);
};