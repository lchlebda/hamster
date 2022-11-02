import { Activity } from '../Home';

const getActivities = async (token: string): Promise<Activity[]> => {
    const response = await fetch('/activities', {
        headers: {
            'ACCESS_TOKEN': token,
        }
    });

    return response.json();
}

const updateActivity = async (id: number, prop: string, value: string): Promise<boolean> => {
    const response = await fetch('/activities/update?' + new URLSearchParams({id: id.toString(), prop: prop, value: value}),
                                 { method: 'POST' });
    return response.json();
}

export const ActivitiesService = {
    getActivities,
    updateActivity
}