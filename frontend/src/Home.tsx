import { FC, ReactElement, useEffect, useState } from 'react';
import './App.css';
import { useAuth } from './authorization/AuthProvider';
import { ActivitiesService } from './services';

export type Activity = {
  id: number;
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
          <div className='App-intro'>
            <h2>Activities</h2>
            {activities?.map(activity =>
                <div key={activity.id}>
                  {activity.name} | {activity.distance}
                </div>
            )}
          </div>
        </header>
      </div>
  );
}
export default App;