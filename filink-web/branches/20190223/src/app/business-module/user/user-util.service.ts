import { Result } from '../../shared-module/entity/result';
import { UserService } from '../../core-module/api-service/user/user-manage';
import { Injectable } from '@angular/core';

@Injectable()
export class UserUtilService {

    constructor(private $userService: UserService) {
    }

    /**
     * 获取部门列表信息
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
    public setAreaNodesStatus(data, parentId, currentId?) {
        data.forEach(areaNode => {
            // areaNode.checked = areaNode.id === parentId;
            // if (areaNode.childDepartmentList) {
            //     this.setAreaNodesStatus(areaNode.childDepartmentList, parentId);
            // }

            // 选中父亲
            areaNode.checked = areaNode.id === parentId;
            // 自己不让选
            areaNode.chkDisabled = areaNode.id === currentId;
            if (areaNode.childDepartmentList) {
                this.setAreaNodesStatus(areaNode.childDepartmentList, parentId, currentId);
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
            if (data.childDepartmentList) {
                this.setTreeNodesStatus(data.childDepartmentList, selectData);
            }
        });
    }
}

