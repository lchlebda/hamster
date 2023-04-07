import { MouseEvent } from 'react';
import { SelectColumnFilter } from './filters/SelectColumnFilter';
import { DefaultColumnFilter } from './filters/DefaultColumnFilter';
import { NumberRangeColumnFilter } from './filters/NumberRangeColumnFilter';
import { SpeedRangeColumnFilter } from './filters/SpeedRangeColumnFilter';
import { DistanceRangeColumnFilter } from './filters/DistanceRangeColumnFilter';
import { Activity } from './Types';

const integerFields =['time', 'regeTime', 'hr', 'hrMax', 'cadence', 'power', 'effort', 'elevation']

export const regexNumber = /\d+(\.\d+)?/

export const onFilterClick = (e: MouseEvent<HTMLDivElement>, setFilter: Function) => {
    // @ts-ignore
    if (e.target.localName !== 'input' && e.target.localName !== 'select') {
        setFilter(undefined)
    }
}

export const getFilter = (header: string): Function | undefined => {
    if (header === 'Sport') {
        return SelectColumnFilter;
    }
    if (header === 'Title' || header === 'Notes') {
        return DefaultColumnFilter;
    }
    if (['Time', 'Rege time', 'HR', 'HR max', 'Cadence', 'Power', 'EF', 'TSS', 'Effort', 'Elevation'].includes(header)) {
        return NumberRangeColumnFilter;
    }
    if (header === 'Speed') {
        return SpeedRangeColumnFilter;
    }
    if (header === 'Distance') {
        return DistanceRangeColumnFilter;
    }
    return undefined;
}

export const getFilterType = (header: string): string => {
    if (header === 'Sport') {
        return 'includes';
    }
    if (header === 'Title' || header === 'Notes') {
        return 'text';
    }
    if (['Time', 'Rege time', 'HR', 'HR max', 'Cadence', 'Power', 'EF', 'TSS', 'Effort', 'Elevation'].includes(header)) {
        return 'between';
    }
    if (header === 'Speed' || header === 'Distance') {
        return 'includesSome';
    }
    return '';
}

export const validateData = (columnName: string, value: any, row: Activity) => {
    if (value === '') {
        return true; // I don't want to validate empty values
    }
    if (integerFields.includes(columnName)) {
        return Number.isInteger(parseFloat(value));
    }
    if (['ef', 'tss'].includes(columnName)) {
        return !isNaN(value);
    }
    if (columnName === 'speed') {
        if (!['Run', 'Ride', 'Swim'].includes(row.type)) {
            return false; // I don't want to set speed for any other activities besides run, ride and swim
        } else if (row.type === 'Run') {
            return /^[1-7]:[0-5][0-9]\s*(\/km)?\s*$/.exec(value) != null;
        } else if (row.type === 'Ride') {
            return /^\d+(\.\d+)?\s*(km\/h)?\s*$/.exec(value) != null;
        } else if (row.type === 'Swim') {
            return /^[1-7]:[0-5][0-9]\s*(\/100m)?\s*$/.exec(value) != null;
        }
    }
    if (columnName === 'distance') {
        if (['Run', 'Ride', 'Hike', 'Walk', 'BackcountrySki'].includes(row.type)) {
            return /^\d+(\.\d+)?\s*(km)?\s*$/.exec(value) != null;
        } else if (row.type === 'Swim') {
            return /^\d+\s*(m)?\s*$/.exec(value) != null;
        } else {
            return false; // I don't want to set distance for any other activities
        }
    }
    return true;
}
