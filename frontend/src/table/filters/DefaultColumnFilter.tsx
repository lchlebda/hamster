import { UseFiltersColumnProps } from 'react-table';
import { onFilterClick } from '../Utils';

export const DefaultColumnFilter = ({
                                        column: { filterValue, setFilter },
                                    }: {column: UseFiltersColumnProps<String> & { id: string }}) => {
    return (
        <div onClick={e => {
            onFilterClick(e, setFilter)
        }}>
            <input
                value={filterValue || ''}
                onChange={e => {
                    setFilter(e.target.value || undefined)
                }}
                style={{
                    width: '90px',
                }}
            />
        </div>
    )
}