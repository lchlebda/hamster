import { Activity } from '../Home';

const getActivities = async (token: string): Promise<Activity[]> => {
    const response = await fetch('/activities', {
        headers: {
            'ACCESS_TOKEN': token,
        }
    });

    return response.json();
}

export const ActivitiesService = {
    getActivities
}