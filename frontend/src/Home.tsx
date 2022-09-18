import { FC, ReactElement, useEffect, useState } from 'react';
import './App.css';
import { useAuth } from './authorization/AuthProvider';
import { ActivitiesService } from './services';
import { Table } from 'react-bootstrap';

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

  const [activities, setActivities] = useState<Activity[] | []>();
  const auth = useAuth();

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
              <Table striped bordered hover size='sm'>
                  <thead>
                  <tr>
                      <th>Date</th>
                      <th>Sport</th>
                      <th>Title</th>
                      <th>Time</th>
                      <th>Rege time</th>
                      <th>HR</th>
                      <th>HR max</th>
                      <th>Cadence</th>
                      <th>Power</th>
                      <th>EF</th>
                      <th>TSS</th>
                      <th>Effort</th>
                      <th>Elevation</th>
                      <th>Speed</th>
                      <th>Distance</th>
                      <th>Notes</th>
                  </tr>
                  </thead>
                  <tbody>
                  {activities?.map(activity =>
                      <tr key={activity.id}>
                          <td>
                              {activity.date}
                          </td>
                          <td>
                              {activity.type}
                          </td>
                          <td>
                              {activity.title}
                          </td>
                          <td>
                              {activity.time}
                          </td>
                          <td>
                              {activity.regeTime || ''}
                          </td>
                          <td>
                              {activity.hr || ''}
                          </td>
                          <td>
                              {activity.hrMax || ''}
                          </td>
                          <td>
                              {activity.cadence || ''}
                          </td>
                          <td>
                              {activity.power || ''}
                          </td>
                          <td>
                              {activity.ef || ''}
                          </td>
                          <td>
                              {activity.tss || ''}
                          </td>
                          <td>
                              {activity.effort || ''}
                          </td>
                          <td>
                              {activity.type === 'Run' || activity.type === 'Ride' ? activity.elevation
                                                                                   : activity.elevation || ''}
                          </td>
                          <td>
                              {activity.speed}
                          </td>
                          <td>
                              {activity.distance}
                          </td>
                          <td>
                              {activity.notes}
                          </td>
                      </tr>
                  )}
                  </tbody>
              </Table>
          </div>
        </header>
      </div>
  );
}
export default App;