import { IEditableCell } from './Types';
import { ChangeEvent, useEffect, useState } from 'react';

export const EditableCell = ({
                          value: initialValue,
                          row: { index, values },
                          column: { id },
                          editCell: editCell,
                      }: IEditableCell) => {
    const [value, setValue] = useState<string>(initialValue)
    const onChange = (e: ChangeEvent<HTMLInputElement> | ChangeEvent<HTMLTextAreaElement>) => {
        setValue(e.target.value)
    }
    const onBlur = () => {
        editCell(index, id, value);
    }
    const onFocus = (e: ChangeEvent<HTMLInputElement> | ChangeEvent<HTMLTextAreaElement>) => {
        e.target.className = 'textarea-onfocus';
    }
    const showValueOrNothingWhenZero = () => {
        const arr = ['regeTime', 'hr', 'hrMax', 'cadence', 'power', 'ef', 'tss', 'effort'];
        // @ts-ignore
        if (value == false && arr.includes(id)) {
            return '';
        }
        if (id === 'elevation') {
            if (values.type !== 'Run' && values.type !== 'Ride' && !initialValue) {
                return '';
            }
        }

        return value;
    }

    useEffect(() => {
        setValue(initialValue)
    }, [initialValue])

    if (id === 'date' || id === 'dayOfWeek' || id === 'type') {
        return value;
    }
    if (id === 'title' || id === 'notes') {
        return <textarea value={ showValueOrNothingWhenZero() } onChange={ onChange } onBlur={ onBlur } onFocus={ onFocus }/>;
    } else {
        return <input value={ showValueOrNothingWhenZero() } onChange={ onChange } onBlur={ onBlur }/>;
    }
}
