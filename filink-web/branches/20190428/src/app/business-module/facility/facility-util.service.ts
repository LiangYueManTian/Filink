/**
 * Created by xiaoconghu on 2019/1/15.
 */
import {Result} from '../../shared-module/entity/result';
import {AreaService} from '../../core-module/api-service/facility/area-manage';
import {UserService} from '../../core-module/api-service/user/user-manage';
import {Injectable} from '@angular/core';

@Injectable()
export class FacilityUtilService {

  constructor(private $areaService: AreaService,
              private $userService: UserService) {
  }

  /**
   * 获取区域列表信息
   * returns {Promise<any>}
   */
  getArea() {
    return new Promise((resolve, reject) => {
      this.$areaService.selectForeignAreaInfoForPageCollection([]).subscribe((result: Result) => {
        const data = result.data || [];
        resolve(data);
      });
    });

  }

  /**
   * 获取单位列表信息
   * returns {Promise<any>}
   */
  getDept() {
    return new Promise((resolve, reject) => {
      this.$userService.queryTotalDept().subscribe((result: Result) => {
        const data = result.data;
        resolve(data);
      });
    });

  }

  /**
   * 递归设置区域的被选状态
   * param data
   * param parentId
   */
  public setAreaNodesStatus(data, parentId, areaId?) {
    data.forEach(areaNode => {
      // 选中父亲
      areaNode.checked = areaNode.areaId === parentId;
      // 自己不让选 没权限不让选
      areaNode.chkDisabled = (areaNode.areaId === areaId) || (areaNode.hasPermissions === false);
      // 如果是已经选中可以选
      if (areaNode.checked) {
        areaNode.chkDisabled = false;
      }
      if (areaNode.children) {
        this.setAreaNodesStatus(areaNode.children, parentId, areaId);
      }
    });
  }

  /**
   * 递归设置责任单位的被选状态
   * param data
   * param selectData
   */
  public setTreeNodesStatus(data, selectData: string[]) {
    data.forEach(treeNode => {
      // 从被选择的数组中找到当前项
      const index = selectData.findIndex(item => treeNode.id === item);
      // 如果找到了 checked 为true
      treeNode.checked = !(index === -1);
      if (treeNode.childDepartmentList) {
        this.setTreeNodesStatus(treeNode.childDepartmentList, selectData);
      }
    });
  }

  /**
   * 告警转工单 和 告警远程通知 使用到了
   * 递归勾选传入的数据
   * param data
   * param parentId
   * param areaId
   */
  public setAlarmAreaNodesStatus(data, parentId, areaId?) {
    data.forEach(areaNode => {
      areaId.forEach(item => {
        // 选中父亲
        areaNode.checked = areaNode.areaId === parentId;
        // 自己不让选
        areaNode.chkDisabled = (areaNode.areaId === item) || (areaNode.hasPermissions === false);
        if (areaNode.children) {
          this.setAreaNodesStatus(areaNode.children, parentId, item);
        }
      });
    });
  }
}
