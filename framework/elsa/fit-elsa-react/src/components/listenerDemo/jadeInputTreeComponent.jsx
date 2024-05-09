import {useDataContext, useDispatch} from "@/components/DefaultRoot.jsx";
import JadeCollapseInputTree from "../common/JadeCollapseInputTree.jsx";

export const jadeInputTreeComponent = (jadeConfig) => {
    const self = {};

    /**
     * 必须.
     */
    self.getJadeConfig = () => {
        return jadeConfig ? jadeConfig : {
            "input": [{
                id: "uuid1",
                name: "person",
                type: "Object",
                from: "Expand",
                value: [{
                    id: "uuid2",
                    name: "name",
                    type: "Object",
                    from: "Expand",
                    value: [{id: "uuid3", name: "surname", type: "String", value: null, from: "Input"}],
                    props: [{id: "uuid3", name: "surname", type: "String", value: null, from: "Input"}]
                }],
                props: [{
                    id: "uuid2",
                    name: "name",
                    type: "Object",
                    from: "Expand",
                    value: [{id: "uuid3", name: "surname", type: "String", value: null, from: "Input"}],
                    props: [{id: "uuid3", name: "surname", type: "String", value: null, from: "Input"}]
                }]
            }, {
                id: "uuid4",
                name: "school",
                type: "Object",
                from: "Reference",
                value: [],
                props: [{id: "uuid5", name: "schoolName", type: "String", value: "绵阳中学", from: "Input"}]
            }]
        };
    };

    /**
     * 必须.
     */
    self.getReactComponents = () => {
        return (<><Component/></>);
    };

    const update = (data, id, changes) => {
        return data.map(d => {
            const newD = {...d};
            if (d.id === id) {
                changes.forEach(change => {
                    newD[change.key] = change.value;
                });
                return newD;
            }
            if (newD.type === "Object" && Array.isArray(newD.value)) {
                newD.value = update(newD.value, id, changes);
            }

            return newD;
        });
    };

    /**
     * 必须.
     */
    self.reducers = (config, action) => {
        switch (action.type) {
            case "update":
                return {"input": update(config.input, action.id, action.changes)};
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    };

    return self;
};

const Component = () => {
    const data = useDataContext();
    const dispatch = useDispatch();

    const updateItem = (id, changes) => {
        dispatch({type: "update", id, changes});
    };

    return (<>
        <div>
            <JadeCollapseInputTree data={data.input} updateItem={updateItem}/>
        </div>
    </>);
};