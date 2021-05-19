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
     * 获取顶级权限
     */
    getTopPermission() {
        return new Promise((resolve, reject) => {
            this.$userService.queryTopPermission().subscribe((result: Result) => {
                const data = result.data;
                resolve(data);
            });
        });
    }

    /**
      * 递归设置部门的被选状态
      */
    public setAreaNodesStatus(data, parentId, currentId?) {
        data.forEach(areaNode => {
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


    /**
     * 递归设置角色权限的节点的被选状态(部分)
     */
    public setRoleTreeNodesStatus(data, selectData: any[]) {
        data.forEach(node => {
            if (selectData && selectData.length > 0) {
                selectData.forEach(item => {
                    if (item === node.id) {
                        node.checked = true;
                    }
                });
                if (node.childPermissonList) {
                    this.setRoleTreeNodesStatus(node.childPermissonList, selectData);
                }
            } else {
                node.checked = false;
                if (node.childPermissonList) {
                    this.setRoleTreeNodesStatus(node.childPermissonList, selectData);
                }
            }
        });
    }


    /**
     * 递归设置设施被选状态(部分)
     */
    public setFacilityTreeNodesStastus(data, selectData: any[]) {
        data.forEach(node => {
            if (selectData && selectData.length > 0) {
                selectData.forEach(item => {
                    if (item === node.deviceTypeId) {
                        node.checked = true;
                    }
                });
                if (node.childDevieTypeList) {
                    this.setFacilityTreeNodesStastus(node.childDevieTypeList, selectData);
                }
            } else {
                node.checked = false;
                if (node.childDevieTypeList) {
                    this.setFacilityTreeNodesStastus(node.childDevieTypeList, selectData);
                }
            }
        });
    }


    /**
   * 递归设置所有角色操作权限的被选状态(全部)
   */
    queryTreeNodeListStatus(nodes: any[]) {
        if (nodes && nodes.length > 0) {
            for (let i = 0; i < nodes.length; i++) {
                nodes[i]['checked'] = true;
                nodes[i]['chkDisabled'] = true;
                nodes[i]['open'] = true;
                if (nodes[i].childPermissonList && nodes[i].childPermissonList.length > 0) {
                    this.queryTreeNodeListStatus(nodes[i].childPermissonList);
                }
            }
        }
    }


    /**
   * 递归设置所有设施权限的被选状态(全部)
   */
    queryTreeNodeFacilityStatus(nodes: any[]) {
        if (nodes && nodes.length > 0) {
            for (let i = 0; i < nodes.length; i++) {
                nodes[i]['checked'] = true;
                nodes[i]['chkDisabled'] = true;
                nodes[i]['open'] = true;
                if (nodes[i].childDevieTypeList && nodes[i].childDevieTypeList.length > 0) {
                    this.queryTreeNodeFacilityStatus(nodes[i].childDevieTypeList);
                }
            }
        }
    }


    /**
     * 递归获取角色权限所有节点的id
     */
    queryTreeNodeListId(nodes, ids: any[]) {
        if (nodes && nodes.length > 0) {
            for (let i = 0; i < nodes.length; i++) {
                ids.push(nodes[i].id);
                if (nodes[i].childPermissonList && nodes[i].childPermissonList.length > 0) {
                    this.queryTreeNodeListId(nodes[i].childPermissonList, ids);
                }
            }
        }
    }


    /**
     * 递归获取设施所有节点的id
     */
    queryTreeNodeFacilityId(nodes, ids: any[]) {
        if (nodes && nodes.length > 0) {
            for (let i = 0; i < nodes.length; i++) {
                ids.push(nodes[i].deviceTypeId);
                if (nodes[i].childDevieTypeList && nodes[i].childDevieTypeList.length > 0) {
                    this.queryTreeNodeFacilityId(nodes[i].childDevieTypeList, ids);
                }
            }
        }
    }


    /**
     * 重置左边树形节点勾选状态
     */
    resetLeftTreeNodeStatus(leftNodes) {
        leftNodes.forEach(item => {
            item['checked'] = false;
            if (item.childPermissonList) {
                this.resetLeftTreeNodeStatus(item.childPermissonList);
            }
        });
    }


    /**
     * 重置右边树形节点勾选状态
     */
    resetRightTreeNodeStatus(rightNodes) {
        rightNodes.forEach(item => {
            item['checked'] = false;
            if (item.childDevieTypeList) {
                item.childDevieTypeList.forEach(child => {
                    child['checked'] = false;
                });
            }
        });
    }


    /**
    * 递归设置区域的被选状态
    */
    public setDeptAreaNodesStatus(data, areaIds) {
        data.forEach(areaNode => {
            areaIds.forEach(item => {
                if (item === areaNode.areaId) {
                    areaNode.checked = true;
                    areaNode['open'] = true;
                }
            });
            if (areaNode.children) {
                this.setDeptAreaNodesStatus(areaNode.children, areaIds);
            }
        });
    }

    /**
     * 设施授权门编号排序
     */
    public sort(a, b) {
        return Number(a.value) - Number(b.value);
    }

}

