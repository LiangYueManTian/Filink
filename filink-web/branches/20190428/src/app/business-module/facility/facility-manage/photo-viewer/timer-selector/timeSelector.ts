export enum DateType {
  DAY = 'day',
  WEEK = 'week',
  MONTH = 'month',
  YEAR = 'year',
}

export interface TimeItem {
  label: string;
  value: DateType.DAY | DateType.WEEK | DateType.MONTH | DateType.YEAR;
}
