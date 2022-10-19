import { FC, ReactElement, useEffect, useMemo, useState } from 'react';
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

const App: FC = (): ReactElement => {

  const [activities, setActivities] = useState<Activity[]>([]);
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

  const {
        getTableProps,
        getTableBodyProps,
        headerGroups,
        rows,
        prepareRow,
  } = useTable({ columns, data })

  useEffect((): void => {
      function getActivities(): Promise<Activity[]> {
          try {
              return ActivitiesService.getActivities(auth.token);
          } catch (e) {
              return Promise.any([]);
          }
      }

      getActivities().then((body) =>  {
          setActivities(body);
      })
  }, []);

  return (
      <div className='App'>
          <h1>Welcome to Hamster App!</h1>
          <header className='App-header'>
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
                          <tr {...row.getRowProps()}>
                              {row.cells.map(cell => {
                                  return (
                                      <td
                                          {...cell.getCellProps()}
                                          style={{
                                              padding: '10px',
                                              border: 'solid 1px gray',
                                              background: 'papayawhip',
                                          }}
                                      >
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