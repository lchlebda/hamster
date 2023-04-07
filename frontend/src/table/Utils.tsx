import { MouseEvent } from 'react';
import { SelectColumnFilter } from './filters/SelectColumnFilter';
import { DefaultColumnFilter } from './filters/DefaultColumnFilter';
import { NumberRangeColumnFilter } from './filters/NumberRangeColumnFilter';
import { SpeedRangeColumnFilter } from './filters/SpeedRangeColumnFilter';
import { DistanceRangeColumnFilter } from './filters/DistanceRangeColumnFilter';

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