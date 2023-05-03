import { Activity, IDeleteColumn, IEditableCell } from './Types';
import { ActivitiesService } from '../services';
import {BaseSyntheticEvent, Dispatch, MouseEventHandler, SetStateAction} from 'react';
import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Popover from 'react-bootstrap/Popover';
import Button from 'react-bootstrap/Button';

export const DeleteColumn = (setActivities: Dispatch<SetStateAction<Activity[]>>): IDeleteColumn => {

    return {
        Header: '',
        id: 'delete',
        accessor: 'delete',
        Filter: undefined,
        filter: '',

        Cell: (tableProps: IEditableCell) => (
            <OverlayTrigger trigger="click" placement="top" rootClose={ false } overlay={
                <Popover id="popover-basic">
                    <Popover.Body>
                        <Button variant="light" size='sm' onClick={(e: BaseSyntheticEvent) => {
                            setActivities(old =>
                                old.filter((row, i) => i !== tableProps.row.index)
                            )
                            ActivitiesService.deleteActivity(tableProps.row.original.id);
                            // @ts-ignore
                            document.getElementById(`row-menu-${tableProps.row.index.toString()}`).click()
                        }}>Delete</Button>{' '}
                    </Popover.Body>
                </Popover>
            }>
                <span id={ `row-menu-${tableProps.row.index.toString()}` } className='delete-cell' onClick={(e: BaseSyntheticEvent) => {
                    const classList = e.target.closest('tr').classList;
                        if (classList.length === 1) {
                            classList.add('table-row-clicked');
                        } else {
                            classList.remove('table-row-clicked');
                        }
                }}>0</span>
            </OverlayTrigger>

        ),}
};
