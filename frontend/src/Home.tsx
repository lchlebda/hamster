import { FC, ReactElement, useEffect, useMemo, useState } from 'react';
import './App.css';
import { useAuth } from './authorization/AuthProvider';
import { ActivitiesService, StravaService } from './services';
import {
    Column,
    useTable,
    useFilters,
    usePagination,
    useSortBy,
    Row
} from 'react-table';
import { Activity } from './table/Types';
import { getFilter, getFilterType, validateData } from './table/Utils';
import { DeleteColumn } from './table/DeleteColumn';
import { EditableCell } from './table/EditableCell';
import { Button } from 'react-bootstrap';

const App: FC = (): ReactElement => {

    const columnNames = ['Date', 'Day', 'Sport', 'Title', 'Time', 'Rege', 'HR', 'HRm', 'Cad', 'Pow', 'EF', 'TSS',
                         'Effort', 'Elev', 'Speed', 'Dist', 'Notes'];
    const fields = ['date', 'dayOfWeek', 'type', 'title', 'time', 'regeTime', 'hr', 'hrMax', 'cadence', 'power', 'ef', 'tss',
                    'effort', 'elevation', 'speed', 'distance', 'notes'];
    const filterOnInit = new Map(fields.map(obj => [obj, false]));

    const [activities, setActivities] = useState<Activity[]>([]);
    const [exception, setException] = useState(false);
    const [stravaUpdateIdsException, setStravaUpdateIdsException] = useState(false);
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

    const editCell = (rowIndex: number, columnName: string, value: string): void => {
        setSkipPageReset(true)
        const activity = activities[rowIndex];
        if (activity[columnName as keyof typeof activity] === value) {
            return;
        }
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
        prepareRow,
        // @ts-ignore
        page, pageOptions, pageCount, state: { pageIndex, pageSize }, gotoPage, previousPage, nextPage, setPageSize, canPreviousPage, canNextPage,
        // @ts-ignore
    } = useTable({ columns, data, initialState: { pageIndex: 0 }, defaultColumn, autoResetPage: !skipPageReset, editCell },
                 useFilters,
                 useSortBy,
                 usePagination)

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
            <div className='main-header'>
                <Button variant="light" onClick={() =>
                  StravaService.updateStravaIds(auth.token).then((response) => {
                      if (response.status === 500) {
                          setStravaUpdateIdsException(true);
                      }
                  })}>
                    Update Strava ids
                </Button>
                {/*<Button variant="light" onClick={() =>*/}
                {/*  StravaService.updateActivitiesFromStrava(auth.token).then((response) => {*/}
                {/*      if (response.status === 500) {*/}
                {/*          setStravaUpdateIdsException(true);*/}
                {/*      }*/}
                {/*  })}>*/}
                {/*    Update Strava ids*/}
                {/*</Button>*/}
                <div className='main-title'>Welcome to Activities App!</div>
                <div className='activities-header-empty-div'></div>
            </div>
            <header className='App-header'>
                { exception && <div className='strava-exception'>Strava service is currently unavailable, cannot get the most recent data.</div> }
                { stravaUpdateIdsException && <div className='strava-exception'>Something is wrong with updating Strava ids or activities into your database file.</div> }
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
                        {page.map((row: Row<Activity>, i: number) => {
                            prepareRow(row)
                            return (
                                <tr {...row.getRowProps()} className={`table-row-${row.values.type}`}>
                                    {row.cells.map(cell => {
                                        return (
                                            <td {...cell.getCellProps()} className={`table-cell ${cell.column.id}`} id={`${cell.getCellProps().key}`}>
                                                {cell.render('Cell')}
                                            </td>
                                        )
                                    })}
                                </tr>
                            )
                        })}
                        </tbody>
                    </table>
                    <div className="pagination">
                        <Button variant="light" size="sm" onClick={() => gotoPage(0)} disabled={!canPreviousPage}>
                            {'<<'}
                        </Button>{' '}
                        <Button variant="light" size="sm" onClick={() => previousPage()} disabled={!canPreviousPage}>
                            {'<'}
                        </Button>{' '}
                        <Button variant="light" size="sm" onClick={() => nextPage()} disabled={!canNextPage}>
                            {'>'}
                        </Button>{' '}
                        <Button variant="light" size="sm" onClick={() => gotoPage(pageCount - 1)} disabled={!canNextPage}>
                            {'>>'}
                        </Button>{' '}
                        <span> Page{' '} <strong> {pageIndex + 1} of {pageOptions.length} </strong></span>
                        <span>| Go to page:{' '}
                            <input
                                type="number"
                                defaultValue={pageIndex + 1}
                                onChange={e => {
                                    const page = e.target.value ? Number(e.target.value) - 1 : 0
                                    gotoPage(page)
                                }}
                            />
                        </span>{' '}
                        <select
                            value={pageSize}
                            onChange={e => {
                                setPageSize(Number(e.target.value))
                            }}
                        >
                            {[10, 20, 30, 40, 50, 100].map(pageSize => (
                                <option key={pageSize} value={pageSize}>
                                    Show {pageSize}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>
            </header>
        </div>
    );
}
export default App;