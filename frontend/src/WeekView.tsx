import { FC, ReactElement, useEffect, useMemo, useState } from 'react';
import './App.css';
import { useAuth } from './authorization/AuthProvider';
import { ActivitiesService } from './services';
import {
    Column,
    useTable
} from 'react-table';
import { ActivitiesPerWeek, Activity } from './table/Types';
import { validateData } from './table/Utils';
import { DeleteColumn } from './table/DeleteColumn';
import { EditableCell } from './table/EditableCell';

const WeekView: FC = (): ReactElement => {

    const columnNames = ['Date', 'Day', 'Sport', 'Title', 'Time', 'Rege', 'HR', 'HRm', 'Cad', 'Pow', 'EF', 'TSS',
        'Effort', 'Elev', 'Speed', 'Dist', 'Notes'];
    const fields = ['date', 'dayOfWeek', 'type', 'title', 'time', 'regeTime', 'hr', 'hrMax', 'cadence', 'power', 'ef', 'tss',
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
                        document.getElementById(`cell_${index}_${columnName}`).setAttribute('class', 'table-cell-not-valid ' + columnName);
                    } else {
                        // @ts-ignore
                        document.getElementById(`cell_${index}_${columnName}`).setAttribute('class', 'table-cell ' + columnName);
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

    let year = 2023;

    useEffect(() => {
        setSkipPageReset(false)
    }, [data])

    useEffect((): void => {
        function getActivitiesAndWeekSummaries(): Promise<ActivitiesPerWeek> {
            return ActivitiesService.getActivitiesPerWeek(auth.token, year).then((response) => {
                if (response.status === 206) {
                    setException(true);
                }

                return response.json();
            }).catch((reason => {
                return Promise.any([]);
            }));
        }

        getActivitiesAndWeekSummaries().then((body) => {
            let activitiesPerWeek = [];
            let k = 0;
            for (let i = 0; i < body.activities.length-1; i++) {
                if (body.activities[i+1].weekOfYear != body.activities[i].weekOfYear) {
                    activitiesPerWeek.push({...body.activities[i], weekSummary: body.weekSummaries[k++]});
                } else {
                    activitiesPerWeek.push(body.activities[i]);
                }
            }
            if (body.activities.length !== 0) {
                activitiesPerWeek.push({...body.activities[body.activities.length-1], weekSummary: body.weekSummaries[k]});
            }
            setActivities(activitiesPerWeek);
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
                                            <td {...cell.getCellProps()} className={`table-cell ${cell.column.id}`} id={`${cell.getCellProps().key}`}>
                                                {cell.render('Cell')}
                                            </td>
                                        )
                                    })}
                                </tr>
                                    { row.original.weekSummary &&
                                    <tr className='table-row-week'>
                                        <td>{ row.original.weekSummary.weekOfYear }</td>
                                        <td/><td/><td/><td/>
                                        <td>{ row.original.weekSummary.activityHours }</td>
                                        <td>{ row.original.weekSummary.regeHours }</td>
                                        <td/><td/><td/><td/><td/>
                                        <td>{ row.original.weekSummary.tss }</td>
                                        <td>{ row.original.weekSummary.effort }</td>
                                        <td>{ row.original.weekSummary.elevation }</td>
                                        <td/><td>{ row.original.weekSummary.distance }</td><td/>
                                    </tr> }
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