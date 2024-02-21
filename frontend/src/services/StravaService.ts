const updateStravaIds = async (token: string): Promise<Response> => {
  return await fetch('/updateStravaIds', {
    headers: {
      'ACCESS_TOKEN': token,
    },
    method: 'POST'
  });
}

export const StravaService = {
  updateStravaIds
}
