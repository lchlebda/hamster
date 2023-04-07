import { UseFiltersColumnProps } from 'react-table';
import { useMemo } from 'react';
import { onFilterClick } from '../Utils';

export const SelectColumnFilter = ({
                                       column: { filterValue, setFilter, preFilteredRows, id },
                                   }: {column: UseFiltersColumnProps<String> & { id: string }}) => {
    const options = useMemo(() => {
        const options = new Set()
        preFilteredRows.forEach(row => {
            options.add(row.values[id])
        })
        // @ts-ignore
        return [...options.values()]
    }, [id, preFilteredRows])

    return (
        <div onClick={e => {
            onFilterClick(e, setFilter)
        }}>
            <select
                value={filterValue}
                onChange={e => {
                    setFilter(e.target.value || undefined)
                }}
            >
                <option value=''>All</option>
                {options.map((option, i) => (
                    <option key={i} value={option}>
                        {option}
                    </option>
                ))}
            </select>
        </div>
    )
}
