import { FC, ReactElement, useEffect, useMemo, useState } from 'react';
import './App.css';
import { useAuth } from './authorization/AuthProvider';
import { ActivitiesService } from './services';
import {
    Column,
    useTable
} from 'react-table';
import { Activity } from './table/Types';
import { validateData } from './table/Utils';
import { DeleteColumn } from './table/DeleteColumn';
import { EditableCell } from './table/EditableCell';

const WeekView: FC = (): ReactElement => {

    const columnNames = ['Date', 'Sport', 'Title', 'Time', 'Rege time', 'HR', 'HR max', 'Cadence', 'Power', 'EF', 'TSS',
        'Effort', 'Elevation', 'Speed', 'Distance', 'Notes'];
    const fields = ['date', 'type', 'title', 'time', 'regeTime', 'hr', 'hrMax', 'cadence', 'power', 'ef', 'tss',
        'effort', 'elevation', 'speed', 'distance', 'notes'];

    const [activities, setActivities] = useState<Activity[]>([]);
    const [exception, setException] = useState(false);
    const [skipPageReset, setSkipPageReset] = useState(false);
    const auth = useAuth();

    const data = useMemo<Activity[]>(() => activities, [activities]);

    const cols = columnNames.map((header, index) => {
        return {
            Header: header,
            accessor: fields[index].toString()
        };
    });

    cols.unshift(DeleteColumn(setActivities));
    const columns = useMemo<Column<Activity>[]>(() => cols as Column<Activity>[], []);

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
    } = useTable({ columns, data, defaultColumn, autoResetPage: !skipPageReset, editCell })

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
            <h1>Welcome to Activities App!</h1>
            <header className='App-header'>
                { exception && <div className='strava-exception'>Strava service is currently unavailable, cannot get the most recent data.</div> }
                <div>
                    <table {...getTableProps()} className='table'>
                        <thead>
                        {headerGroups.map(headerGroup => (
                            <tr {...headerGroup.getHeaderGroupProps()}>
                                {headerGroup.headers.map(column => (
                                    <th {...column.getHeaderProps()}>{column.render('Header')}</th>
                                ))}
                            </tr>
                        ))}
                        </thead>
                        <tbody {...getTableBodyProps()}>
                        {rows.map(row => {
                            prepareRow(row)
                            return (
                                <>
                                <tr {...row.getRowProps()} className={`table-row-${row.values.type}`}>
                                    {row.cells.map(cell => {
                                        return (
                                            <td {...cell.getCellProps()} className='table-cell' id={`${cell.getCellProps().key}`}>
                                                {cell.render('Cell')}
                                            </td>
                                        )
                                    })}
                                </tr>
                                {row.original.id % 7 === 0 && <tr className='table-row-week'><td/><td/></tr>}
                                </>
                            )
                        })}
                        </tbody>
                    </table>
                </div>
            </header>
        </div>
    );
}
export default WeekView;