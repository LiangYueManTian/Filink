/**
 * Created by xiaoconghu on 2018/12/13.
 */
import {Injectable} from '@angular/core';
import {TableConfig} from '../../entity/tableConfig';
import {FilterCondition} from '../../entity/queryCondition';

@Injectable()
export class TableService {
  /**
   * 表格统一提示配置
   * param handleOK
   * param handleCancel
   * returns {{nzTitle: string; nzContent: string; nzOkText: string; nzOkType:
    * string; nzMaskClosable: boolean; nzOnOk: any; nzCancelText: string; nzOnCancel: any}}
   */
  public tablePrompt(handleOK, handleCancel) {
    const obj = {
      nzTitle: '提示',
      nzContent: '<span>是否确认删除？</span>',
      nzOkText: '确认',
      nzOkType: 'danger',
      nzMaskClosable: false,
      nzOnOk: handleOK,
      nzCancelText: '取消',
      nzOnCancel: handleCancel
    };
    return obj;
  }

  /**
   * 初始化过滤字段
   * param {TableConfig} tableConfig
   * returns {Map<string, FilterCondition>}
   */
  initFilterParams(tableConfig: TableConfig): Map<string, FilterCondition> {
    const queryTerm = new Map();
    if (tableConfig && tableConfig.columnConfig) {
      tableConfig.columnConfig.forEach(item => {
        if (item.searchable && item.key) {
          const filterCondition = new FilterCondition(item.searchKey || item.key);
          if (item.searchConfig.initialValue) {
            filterCondition.filterValue = item.searchConfig.initialValue;
            filterCondition.initialValue = item.searchConfig.initialValue;
          }
          if (item.searchConfig.type === 'input') {
            filterCondition.operator = 'like';
          } else {
            filterCondition.operator = 'eq';
          }
          queryTerm.set(item.key, filterCondition);
        }
      });
    }
    return queryTerm;
  }

  /**
   * 创建数组过滤条件
   * param {Map<string, FilterCondition>} queryTerm
   * returns {FilterCondition[]}
   */
  createFilterConditions(queryTerm: Map<string, FilterCondition>): FilterCondition[] {
    const filterConditions = [];
    queryTerm.forEach(value => {
      if (value.filterValue && value.filterValue !== 0) {
        filterConditions.push(value);
      }
    });
    return filterConditions;
  }

  /**
   * 清空所有条件
   * param {Map<string, FilterCondition>} queryTerm
   */
  resetFilterConditions(queryTerm: Map<string, FilterCondition>) {
    queryTerm.forEach((value, key) => {
      if (value.initialValue && value.initialValue !== 0) {
        value.filterValue = value.initialValue;
      } else {
        value.filterValue = null;
      }
    });
  }

  /**
   * 创建一个map对象过滤条件
   * param {Map<string, FilterCondition>} queryTerm
   * returns {Map<string, any>}
   */
  createFilterConditionMap(queryTerm: Map<string, FilterCondition>): Object {
    const query =  Object.create(null);
    queryTerm.forEach((value, key) => {
      query[value.filterField] = value.filterValue;
    });
    return query;
  }

  /**
   * 判断所有子元素是否都没有选中
   * param data
   * param expendDataKey 子元素数组名字 string
   */
  checkStatus(data, expendDataKey) {
    // 全不选
    let allUnChecked = true;
    (function checkAllData(_data) {
      for (let i = 0; i < _data.length; i++) {
        if (_data[i].checked) {
          allUnChecked = false;
          break;
        }
        if (_data[i][expendDataKey] && _data[i][expendDataKey].length > 0) {
          checkAllData(_data[i][expendDataKey]);
        }
      }
    })(data);
    return allUnChecked;
  }
}
