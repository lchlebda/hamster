import { FC, ReactElement, useEffect, useState } from 'react';
import './App.css';
import { useAuth } from './authorization/AuthProvider';
import { ActivitiesService } from './services';
import { Table } from 'react-bootstrap';

export type Activity = {
  id: number;
  type: string;
  moving_time: number;
  name: string;
  distance: number;
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
              <Table striped bordered hover>
                  <thead>
                  <tr>
                      <th>Sport</th>
                      <th>Time</th>
                      <th>Title</th>
                      <th>Distance</th>
                  </tr>
                  </thead>
                  <tbody>
                  {activities?.map(activity =>
                      <tr key={activity.id}>
                          <td>
                              {activity.type}
                          </td>
                          <td>
                              {activity.moving_time}
                          </td>
                          <td>
                              {activity.name}
                          </td>
                          <td>
                              {activity.distance}
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