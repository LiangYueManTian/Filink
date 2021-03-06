import {QueryCondition} from './queryCondition';

export class ExportParams {
  queryCondition: QueryCondition;
  columnInfoList: ColumnInfo[];
  excelType: number;
}

export class ColumnInfo {
  columnName: string;
  propertyName: string;
  isTranslation?: 1;   // 表示后台处理该字段的返回值
}
