const updateStravaIds = async (token: string): Promise<Response> => {
  return await fetch('/updateStravaIds', {
    headers: {
      'ACCESS_TOKEN': token,
    },
    method: 'POST'
  });
}

const updateActivitiesFromStrava = async (token: string): Promise<Response> => {
  return await fetch('/updateActivitiesFromStrava', {
    headers: {
      'ACCESS_TOKEN': token,
    },
    method: 'POST'
  });
}

export const StravaService = {
  updateStravaIds,
  updateActivitiesFromStrava
}
