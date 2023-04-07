const getActivities = async (token: string): Promise<Response> => {
    return await fetch('/activities', {
        headers: {
            'ACCESS_TOKEN': token,
        }
    });
}

const updateActivity = async (id: number, type: string, prop: string, value: string): Promise<boolean> => {
    const response = await fetch(`/activities/update/${id}?` + new URLSearchParams({type, prop, value}),
                                 { method: 'POST' });
    return response.json();
}

const deleteActivity = async (id: number): Promise<void> => {
     await fetch(`/activities/delete/${id}`, { method: 'DELETE' });
}

export const ActivitiesService = {
    deleteActivity,
    getActivities,
    updateActivity
}