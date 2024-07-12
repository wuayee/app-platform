import {useState} from 'react';
import {useDispatch, useShapeContext} from "@/components/DefaultRoot.jsx";
import {JadeInput} from "@/components/common/JadeInput.jsx";

export default function AddTask() {
    const [text, setText] = useState('');
    const dispatch = useDispatch();
    const shape = useShapeContext();
    return (<>
                <JadeInput placeholder="Add task"
                           value={text}
                           onChange={e => setText(e.target.value)}
                />
                <button onClick={() => {
                    setText('');
                    console.log(shape.serialize());
                    dispatch({
                        type: 'added', id: nextId++, text: text
                    });
                }}>Add
                </button>
            </>);
}

let nextId = 3;
