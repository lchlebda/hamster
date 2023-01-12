import { ChangeEvent, MouseEvent, FC, ReactElement, useEffect, useMemo, useRef, useState } from 'react';
import './App.css';
import { useAuth } from './authorization/AuthProvider';
import { ActivitiesService } from './services';
import {
    Column,
    useTable,
    useFilters,
    useSortBy,
    UseFiltersColumnProps
} from 'react-table';

export type Activity = {
    id: number;
    date: string;
    type: string;
    title: string;
    time: number;
    regeTime: number;
    hr: number;
    hrMax: number;
    cadence: number;
    power: number;
    ef: number;
    tss: number;
    effort: number;
    elevation: number;
    speed: number;
    distance: number;
    notes: string;
}

type IEditableCell = {
    value: string,
    row: { index: number, values: Activity },
    column: { id: string },
    editCell: Function
}

const App: FC = (): ReactElement => {

    const regexNumber = /\d+(\.\d+)?/
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

    const onFilterClick = (e: MouseEvent<HTMLDivElement>, setFilter: Function) => {
        // @ts-ignore
        if (e.target.localName !== 'input' && e.target.localName !== 'select') {
            setFilter(undefined)
        }
    }

    function DefaultColumnFilter({
                                     column: { filterValue, setFilter },
                                 }: {column: UseFiltersColumnProps<String> & { id: string }}) {
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

    const SelectColumnFilter = ({
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

    const NumberRangeColumnFilter = ({
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

    const SpeedRangeColumnFilter = ({
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

    const DistanceRangeColumnFilter = ({
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

    const getFilter = (header: string): Function | undefined => {
        if (header === 'Sport') {
            return SelectColumnFilter;
        }
        if (header === 'Title' || header === 'Notes') {
            return DefaultColumnFilter;
        }
        if (['Time', 'Rege time', 'HR', 'HR max', 'Cadence', 'Power', 'EF', 'TSS', 'Effort', 'Elevation'].includes(header)) {
            return NumberRangeColumnFilter;
        }
        if (header === 'Speed') {
            return SpeedRangeColumnFilter;
        }
        if (header === 'Distance') {
            return DistanceRangeColumnFilter;
        }
        return undefined;
    }

    const getFilterType = (header: string): string => {
        if (header === 'Sport') {
            return 'includes';
        }
        if (header === 'Title' || header === 'Notes') {
            return 'text';
        }
        if (['Time', 'Rege time', 'HR', 'HR max', 'Cadence', 'Power', 'EF', 'TSS', 'Effort', 'Elevation'].includes(header)) {
            return 'between';
        }
        if (header === 'Speed' || header === 'Distance') {
            return 'includesSome';
        }
        return '';
    }

    const cols = columnNames.map((header, index) => {
        return {
            Header: header,
            accessor: fields[index].toString(),
            Filter: getFilter(header),
            filter: getFilterType(header)
        };
    });
    const columns = useMemo<Column<Activity>[]>(() => cols as Column<Activity>[], []);

    const EditableCell = ({
                              value: initialValue,
                              row: { index, values },
                              column: { id },
                              editCell: editCell,
                          }: IEditableCell) => {
        const [value, setValue] = useState<string>(initialValue)
        const onChange = (e: ChangeEvent<HTMLInputElement>) => {
            setValue(e.target.value)
        }
        const onBlur = () => {
            editCell(index, id, value);
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

        return id === 'date' || id === 'type' ? value
            : <input value={ showValueOrNothingWhenZero() } onChange={ onChange } onBlur={ onBlur }/>
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