import { UseFiltersColumnProps } from 'react-table';
import { useMemo } from 'react';
import { onFilterClick } from '../Utils';

export const SpeedRangeColumnFilter = ({
                                           column: { filterValue = [], setFilter, preFilteredRows, id } ,
                                       }: {column: UseFiltersColumnProps<String> & { id: string }}) => {
    const [min, max] = useMemo(() => {
        let min = preFilteredRows.length ? preFilteredRows[0].values[id] : 0
        let max = preFilteredRows.length ? preFilteredRows[0].values[id] : 0
        preFilteredRows.forEach(row => {
            min = row.values[id] < min ? row.values[id] : min
            max = row.values[id] > max ? row.values[id] : max
        })
        return [min, max]
    }, [id, preFilteredRows])

    return (
        <div onClick={e => {
            onFilterClick(e, setFilter)
        }}>
            <input
                max={filterValue.sort()[filterValue.length-1]}
                onBlur={e => {
                    let filterValues: string[] = []
                    preFilteredRows.forEach(row => {
                        if ((e.target.value === "" || row.values[id] >= e.target.value)
                            && (e.target.max === "" || row.values[id] <= e.target.max)) {
                            filterValues.push(row.values[id])
                        }
                    })
                    setFilter(filterValues)
                }}
                placeholder={`${min}`}
            />
            to
            <input
                min={filterValue.sort()[0]}
                onBlur={e => {
                    let filterValues: string[] = []
                    preFilteredRows.forEach(row => {
                        if ((e.target.value === "" || row.values[id] < e.target.value)
                            && (e.target.min === "" || row.values[id] >= e.target.min)) {
                            filterValues.push(row.values[id])
                        }
                    })
                    setFilter(filterValues)
                }}
                placeholder={`${max}`}
            />
        </div>
    )
}
