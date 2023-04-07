import { UseFiltersColumnProps } from 'react-table';
import { useMemo } from 'react';
import { onFilterClick, regexNumber } from '../Utils';

export const DistanceRangeColumnFilter = ({
                                              column: { filterValue = [], setFilter, preFilteredRows, id } ,
                                          }: {column: UseFiltersColumnProps<String> & { id: string }}) => {
    const [min, max] = useMemo(() => {
        const firstNumber = preFilteredRows[0].values[id].match(regexNumber);
        let min = preFilteredRows.length && firstNumber !== null ? firstNumber[0] : 0
        let max = preFilteredRows.length && firstNumber !== null ? firstNumber[0] : 0
        preFilteredRows.forEach(row => {
            const match = row.values[id].match(regexNumber)
            const num = match !== null ? Number(match[0]) : 0;
            min = num < min ? num : min
            max = num > max ? num : max
        })
        return [min, max]
    }, [id, preFilteredRows])

    return (
        <div onClick={e => {
            onFilterClick(e, setFilter)
        }}>
            <input
                max={filterValue.sort((a: number, b: number) => a - b)[filterValue.length-1]}
                onBlur={e => {
                    let filterValues: number[] = []
                    preFilteredRows.forEach(row => {
                        const match = row.values[id].match(regexNumber)
                        const num = match !== null ? Number(match[0]) : 0;
                        if ((e.target.value === "" || num >= Number(e.target.value))
                            && (e.target.max === "" || num <= Number(e.target.max))) {
                            filterValues.push(num)
                        }
                    })
                    setFilter(filterValues)
                }}
                placeholder={`${min}`}
            />
            to
            <input
                min={filterValue.sort((a: number, b: number) => a - b)[0]}
                onBlur={e => {
                    let filterValues: number[] = []
                    preFilteredRows.forEach(row => {
                        const match = row.values[id].match(regexNumber)
                        const num = match !== null ? Number(match[0]) : 0;
                        if ((e.target.value === "" || num < Number(e.target.value))
                            && (e.target.min === "" || num >= Number(e.target.min))) {
                            filterValues.push(num)
                        }
                    })
                    setFilter(filterValues)
                }}
                placeholder={`${max}`}
            />
        </div>
    )
}