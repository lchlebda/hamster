export type Activity = {
    id: number;
    date: string;
    dayOfWeek: string;
    type: string;
    title: string;
    time: number;
    regeTime: number;
    hr: number;
    hrMax: number;
    cadence: number;
    power: number;
    ef: number;
    tss: number;
    effort: number;
    elevation: number;
    speed: number;
    distance: number;
    notes: string;
}

export type IEditableCell = {
    value: string,
    row: { index: number, original: { id: number }, values: Activity },
    column: { id: string },
    editCell: Function
}

export type IDeleteColumn = {
    Header: string,
    id: string,
    accessor: string,
    Filter: Function | undefined,
    filter: string,
    Cell: (tableProps: IEditableCell) => JSX.Element;
}