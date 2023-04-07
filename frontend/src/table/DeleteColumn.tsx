import { Activity, IDeleteColumn, IEditableCell } from './Types';
import { ActivitiesService } from '../services';
import { Dispatch, SetStateAction } from 'react';

export const DeleteColumn = (setActivities: Dispatch<SetStateAction<Activity[]>>): IDeleteColumn => {
    return {
        Header: '',
        id: 'delete',
        accessor: 'delete',
        Filter: undefined,
        filter: '',

        Cell: (tableProps: IEditableCell) => (
            <span className='delete-cell'
                  onClick={() => {
                      setActivities(old =>
                          old.filter((row, index) => index !== tableProps.row.index)
                      )
                      ActivitiesService.deleteActivity(tableProps.row.original.id);
                  }}>X</span>
        ),}
};
