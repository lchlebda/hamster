import { UseFiltersColumnProps } from 'react-table';
import { useMemo } from 'react';
import { onFilterClick } from '../Utils';

export const NumberRangeColumnFilter = ({
                                            column: { filterValue = [], setFilter, preFilteredRows, id } ,
                                        }: {column: UseFiltersColumnProps<String> & { id: string }}) => {
    const [min, max] = useMemo(() => {
        let min = preFilteredRows.length ? preFilteredRows[0].values[id] : 0
        let max = preFilteredRows.length ? preFilteredRows[0].values[id] : 0
        preFilteredRows.forEach(row => {
            min = Math.min(row.values[id], min)
            max = Math.max(row.values[id], max)
        })
        return [min, max]
    }, [id, preFilteredRows])

    return (
        <div onClick={e => {
            onFilterClick(e, setFilter)
        }}>
            <input
                value={filterValue[0] || ''}
                type='number'
                onChange={e => {
                    const val = e.target.value
                    setFilter((old = []) => [val ? parseInt(val, 10) : undefined, old[1]])
                }}
                placeholder={`${min}`}
            />
            to
            <input
                value={filterValue[1] || ''}
                type='number'
                onChange={e => {
                    const val = e.target.value
                    setFilter((old = []) => [old[0], val ? parseInt(val, 10) : undefined])
                }}
                placeholder={`${max}`}
            />
        </div>
    )
}
