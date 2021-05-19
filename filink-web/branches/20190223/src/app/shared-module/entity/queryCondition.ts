/**
 * Created by xiaoconghu on 2019/1/15.
 */
/**
 * 分页条件
 */
export class PageCondition {
  /**
   * 当前页
   */
  pageNum: number;
  /**
   * 一页多少条
   */
  pageSize: number;

  constructor(pageNum: number, pageSize: number) {
    this.pageNum = pageNum;
    this.pageSize = pageSize;
  }
}

/**
 * 过滤条件
 */
export class FilterCondition {
  /**
   * 过滤字段
   */
  filterField: string;
  /**
   * 操作符
   */
  operator: string; // like
  /**
   * 过滤值
   */
  filterValue: any;
  /**
   * 初始值
   */
  initialValue?: any;
  constructor(filterField?: string) {
    this.filterField = filterField;
  }
}

/**
 * 排序条件
 */
export class SortCondition {
  sortField: string;
  sortRule: string;
}

/**
 * 查询条件
 */
export class QueryCondition {


  pageCondition: PageCondition;
  filterConditions: FilterCondition[] = [];
  sortCondition: SortCondition;
  bizCondition: any;

  constructor() {
    this.pageCondition = new PageCondition(1, 10);
    this.sortCondition = new SortCondition();
    this.bizCondition = {};
  }
}

