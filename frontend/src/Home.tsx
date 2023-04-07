import { ChangeEvent, FC, ReactElement, useEffect, useMemo, useState } from 'react';
import './App.css';
import { useAuth } from './authorization/AuthProvider';
import { ActivitiesService } from './services';
import {
    Column,
    useTable,
    useFilters,
    useSortBy
} from 'react-table';
import { Activity, IEditableCell } from './table/Types';
import { getFilter, getFilterType } from './table/Utils';
import { DeleteColumn } from './table/DeleteColumn';

const App: FC = (): ReactElement => {

    const columnNames = ['Date', 'Sport', 'Title', 'Time', 'Rege time', 'HR', 'HR max', 'Cadence', 'Power', 'EF', 'TSS',
                         'Effort', 'Elevation', 'Speed', 'Distance', 'Notes'];
    const fields = ['date', 'type', 'title', 'time', 'regeTime', 'hr', 'hrMax', 'cadence', 'power', 'ef', 'tss',
                    'effort', 'elevation', 'speed', 'distance', 'notes'];
    const integerFields =['time', 'regeTime', 'hr', 'hrMax', 'cadence', 'power', 'effort', 'elevation']
    const filterOnInit = new Map(fields.map(obj => [obj, false]));

    const [activities, setActivities] = useState<Activity[]>([]);
    const [exception, setException] = useState(false);
    const [skipPageReset, setSkipPageReset] = useState(false);
    const [filterOn, setFilterOn] = useState<Map<string, boolean>>(filterOnInit);
    const auth = useAuth();

    const data = useMemo<Activity[]>(() => activities, [activities]);

    const cols = columnNames.map((header, index) => {
        return {
            Header: header,
            accessor: fields[index].toString(),
            Filter: getFilter(header),
            filter: getFilterType(header)
        };
    });

    cols.unshift(DeleteColumn(setActivities));
    const columns = useMemo<Column<Activity>[]>(() => cols as Column<Activity>[], []);

    const EditableCell = ({
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

        if (id === 'date' || id === 'type') {
            return value;
        }
        if (id === 'title' || id === 'notes') {
            return <textarea value={ showValueOrNothingWhenZero() } onChange={ onChange } onBlur={ onBlur } onFocus={ onFocus }/>;
        } else {
            return <input value={ showValueOrNothingWhenZero() } onChange={ onChange } onBlur={ onBlur }/>;
        }
    }

    const editCell = (rowIndex: number, columnName: string, value: string): void => {
        setSkipPageReset(true)
        setActivities(old =>
            old.map((row, index) => {
                if (index === rowIndex) {
                    const prop = columnName as keyof typeof row;
                    const isValid = validateData(columnName, value, row)
                    if (row[prop] != value && value != '' && isValid) {
                        ActivitiesService.updateActivity(row['id'], row['type'], columnName, value);
                    }
                    if (!isValid) {
                        // @ts-ignore
                        document.getElementById(`cell_${index}_${columnName}`).setAttribute('class', 'table-cell-not-valid');
                    } else {
                        // @ts-ignore
                        document.getElementById(`cell_${index}_${columnName}`).setAttribute('class', 'table-cell');
                    }
                    if (isValid) {
                        return {
                            ...old[rowIndex],
                            [columnName]: value,
                        }
                    }
                }
                return row
            })
        )
    }

    const validateData = (columnName: string, value: any, row: Activity) => {
        if (value === '') {
            return true; // I don't want to validate empty values
        }
        if (integerFields.includes(columnName)) {
            return Number.isInteger(parseFloat(value));
        }
        if (['ef', 'tss'].includes(columnName)) {
            return !isNaN(value);
        }
        if (columnName === 'speed') {
            if (!['Run', 'Ride', 'Swim'].includes(row.type)) {
                return false; // I don't want to set speed for any other activities besides run, ride and swim
            } else if (row.type === 'Run') {
                return /^[1-7]:[0-5][0-9]\s*(\/km)?\s*$/.exec(value) != null;
            } else if (row.type === 'Ride') {
                return /^\d+(\.\d+)?\s*(km\/h)?\s*$/.exec(value) != null;
            } else if (row.type === 'Swim') {
                return /^[1-7]:[0-5][0-9]\s*(\/100m)?\s*$/.exec(value) != null;
            }
        }
        if (columnName === 'distance') {
            if (['Run', 'Ride', 'Hike', 'Walk', 'BackcountrySki'].includes(row.type)) {
                return /^\d+(\.\d+)?\s*(km)?\s*$/.exec(value) != null;
            } else if (row.type === 'Swim') {
                return /^\d+\s*(m)?\s*$/.exec(value) != null;
            } else {
                return false; // I don't want to set distance for any other activities
            }
        }
        return true;
    }

    const defaultColumn = {
        Cell: EditableCell,
    }

    const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        rows,
        prepareRow,
        // @ts-ignore
    } = useTable({ columns, data, defaultColumn, autoResetPage: !skipPageReset, editCell },
                 useFilters,
                 useSortBy)

    useEffect(() => {
        setSkipPageReset(false)
    }, [data])

    useEffect((): void => {
        function getActivities(): Promise<Activity[]> {
            return ActivitiesService.getActivities(auth.token).then((response) => {
                if (response.status === 206) {
                    setException(true);
                }

                return response.json();
            }).catch((reason => {
                return Promise.any([]);
            }));
        }

        getActivities().then((body) => {
            setActivities(body);
        })
    }, []);

    return (
        <div className='App'>
            <h1>Welcome to Hamster App!</h1>
            <header className='App-header'>
                { exception && <div className='strava-exception'>Strava service is currently unavailable, cannot get the most recent data.</div> }
                <div>
                    <table {...getTableProps()} className='table'>
                        <thead>
                        {headerGroups.map(headerGroup => (
                            <tr {...headerGroup.getHeaderGroupProps()}>
                                {headerGroup.headers.map(column => (
                                    <span className='table-header-div'>
                                    <th
                                        // @ts-ignore
                                        {...column.getHeaderProps(column.getSortByToggleProps())}
                                        className='table-header'
                                    >
                                        {column.render('Header')}
                                        <span>
                                            {
                                                // @ts-ignore
                                                column.isSorted ? column.isSortedDesc
                                                    ? ' 🔽'
                                                    : ' 🔼'
                                                : ''}
                                        </span>

                                    </th>
                                        <div className='table-filter' onClick={e => {
                                            // @ts-ignore
                                            if (e.target.localName !== 'input' && e.target.localName !== 'select') {
                                                // @ts-ignore
                                                setFilterOn(new Map(fields.map(obj => {
                                                    return [obj, (obj === column.id && !filterOn.get(column.id))
                                                    || (obj !== column.id && filterOn.get(obj))]
                                                })))
                                            }
                                        }}>
                                            {
                                                // @ts-ignore
                                                column.canFilter && column.Filter && filterOn.get(column.id) ? column.render('Filter') : null }
                                        </div>
                                    </span>
                                ))}
                            </tr>
                        ))}
                        </thead>
                        <tbody {...getTableBodyProps()}>
                        {rows.map(row => {
                            prepareRow(row)
                            return (
                                <tr {...row.getRowProps()} className={`table-row-${row.values.type}`}>
                                    {row.cells.map(cell => {
                                        return (
                                            <td {...cell.getCellProps()} className='table-cell' id={`${cell.getCellProps().key}`}>
                                                {cell.render('Cell')}
                                            </td>
                                        )
                                    })}
                                </tr>
                            )
                        })}
                        </tbody>
                    </table>
                </div>
            </header>
        </div>
    );
}
export default App;