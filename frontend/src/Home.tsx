import { ChangeEvent, FC, ReactElement, useEffect, useMemo, useState } from 'react';
import './App.css';
import { useAuth } from './authorization/AuthProvider';
import { ActivitiesService } from './services';
import { Column, useTable } from 'react-table';

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
    updateMyData: Function
}

const App: FC = (): ReactElement => {

    const [activities, setActivities] = useState<Activity[]>([]);
    const [exception, setException] = useState(false);
    const [skipPageReset, setSkipPageReset] = useState(false)
    const auth = useAuth();

    const columnNames = ['Date', 'Sport', 'Title', 'Time', 'Rege time', 'HR', 'HR max', 'Cadence', 'Power', 'EF', 'TSS',
        'Effort', 'Elevation', 'Speed', 'Distance', 'Notes'];
    const fields = ['date', 'type', 'title', 'time', 'regeTime', 'hr', 'hrMax', 'cadence', 'power', 'ef', 'tss',
        'effort', 'elevation', 'speed', 'distance', 'notes'];

    const data = useMemo<Activity[]>(() => activities, [activities]);
    const cols = columnNames.map((header, index) => {
        return {Header: header, accessor: fields[index].toString()};
    });
    const columns = useMemo<Column<Activity>[]>(() => cols as Column<Activity>[], []);

    const EditableCell = ({
                              value: initialValue,
                              row: { index, values },
                              column: { id },
                              updateMyData: updateMyData,
                          }: IEditableCell) => {
        const [value, setValue] = useState<string>(initialValue)
        const onChange = (e: ChangeEvent<HTMLInputElement>) => {
            setValue(e.target.value)
        }
        const onBlur = () => {
            updateMyData(index, id, value);
        }
        const showValueOrNothingWhenZero = () => {
            const arr = ['regeTime', 'hr', 'hrMax', 'cadence', 'power', 'ef', 'tss', 'effort'];
            if (!initialValue && arr.includes(id)) {
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

    const updateMyData = (rowIndex: number, columnId: string, value: string) => {
        setSkipPageReset(true)
        setActivities(old =>
            old.map((row, index) => {
                if (index === rowIndex) {
                    const prop = columnId as keyof typeof row;
                    if (row[prop] != value) {
                        ActivitiesService.updateActivity(row['id'], columnId, value);
                    }
                    return {
                        ...old[rowIndex],
                        [columnId]: value,
                    }
                }
                return row
            })
        )
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
    } = useTable({columns, data, defaultColumn, autoResetPage: !skipPageReset, updateMyData})

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
                                    <th
                                        {...column.getHeaderProps()}
                                        className='table-header'
                                    >
                                        {column.render('Header')}
                                    </th>
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
                                            <td {...cell.getCellProps()} className='table-cell'>
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