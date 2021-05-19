import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UserInterface } from './user.interface';
import { Observable } from 'rxjs';
import {
  DELETE_DEPT,
  DELETE_USER,
  DELETE_ROLE,
  INSERT_DEPT,
  INSERT_ROLE,
  INSERT_USER,
  MODIFY_PASSWORD,
  QUERY_ALL_DEPARTMENT,
  QUERY_DEPT_INFO_BY_ID,
  QUERY_DEPT_LIST,
  QUERY_ROLE_INFO_BY_ID,
  QUERY_ROLE_LIST,
  QUERY_ROLES_LIST,
  QUERY_ALL_ROLES,
  QUERY_USER_INFO_BY_ID,
  QUERY_USER_LIST,
  QUERY_USER_LISTS,
  QUERY_TOTAL_DEPT,
  QUERY_TOTAL_DEPARTMENT,
  QUERY_ALL_DEPT,
  RESET_PASSWORD,
  QUERY_PASSWORD,
  UPDATE_DEPT,
  UPDATE_ROLE,
  UPDATE_USER,
  UPDATE_USER_STATUS,
  VERIFY_USER_INFO,
  VERIFY_ROLE_INFO,
  VERIFY_DEPT_INFO,
  GET_ONLINE_USER,
  OFFLINE,
  LOGOUT
} from '../user-request-url';

@Injectable()
export class UserService implements UserInterface {

  constructor(private $http: HttpClient) { }
  /**
   *用户管理...
   */
  queryUserList(body): Observable<Object> {
    return this.$http.post(`${QUERY_USER_LIST}`, body);
  }

  queryUserLists(body): Observable<Object> {
    return this.$http.post(`${QUERY_USER_LISTS}`, body);
  }

  addUser(body): Observable<Object> {
    return this.$http.post(`${INSERT_USER}`, body);
  }


  deleteUser(body): Observable<Object> {
    return this.$http.post(`${DELETE_USER}`, body);
  }


  modifyUser(body): Observable<Object> {
    return this.$http.post(`${UPDATE_USER}`, body);
  }


  queryUserInfoById(id: string): Observable<Object> {
    return this.$http.post(`${QUERY_USER_INFO_BY_ID}` + `${id}`, null);
  }

  updateUserStatus(status: number, idArray: any): Observable<Object> {
    return this.$http.get(`${UPDATE_USER_STATUS}?userStatus=${status}&userIdArray=${idArray}`);
  }

  verifyUserInfo(body): Observable<Object> {
    return this.$http.post(`${VERIFY_USER_INFO}`, body);
  }

  restPassword(body): Observable<Object> {
    return this.$http.post(`${RESET_PASSWORD}`, body);
  }

  modifyPassword(body): Observable<Object> {
    return this.$http.post(`${MODIFY_PASSWORD}`, body);
  }

  logout(body): Observable<Object> {
    return this.$http.get(`${LOGOUT}/${body.userid}/${body.token}`);
  }

  queryPassword(): Observable<Object> {
    return this.$http.post(`${QUERY_PASSWORD}`, {});
  }


  /**
   * 单位列表...
   */
  queryDeptList(body): Observable<Object> {
    return this.$http.post(`${QUERY_DEPT_LIST}`, body);
  }


  queryAllDepartment(): Observable<Object> {
    return this.$http.post(`${QUERY_ALL_DEPARTMENT}`, {});
  }


  queryDeptInfoById(id: string): Observable<Object> {
    return this.$http.post(`${QUERY_DEPT_INFO_BY_ID}` + `${id}`, null);
  }

  addDept(body): Observable<Object> {
    return this.$http.post(`${INSERT_DEPT}`, body);
  }

  deleteDept(body): Observable<Object> {
    return this.$http.post(`${DELETE_DEPT}`, body);
  }

  modifyDept(body): Observable<Object> {
    return this.$http.put(`${UPDATE_DEPT}`, body);
  }

  queryTotalDept(): Observable<Object> {
    return this.$http.post(`${QUERY_TOTAL_DEPT}`, {});
  }

  queryAllDept(body): Observable<Object> {
    return this.$http.post(`${QUERY_ALL_DEPT}`, body);
  }

  verifyDeptInfo(body): Observable<Object> {
    return this.$http.post(`${VERIFY_DEPT_INFO}`, body);
  }

  queryTotalDepartment(): Observable<Object> {
    return this.$http.post(`${QUERY_TOTAL_DEPARTMENT}`, {});
  }
  /**
   * 角色管理...
   */
  queryRoleList(body): Observable<Object> {
    return this.$http.post(`${QUERY_ROLE_LIST}`, body);
  }

  queryRoles(body): Observable<Object> {
    return this.$http.post(`${QUERY_ROLES_LIST}`, body);
  }

  queryAllRoles(): Observable<Object> {
    return this.$http.post(`${QUERY_ALL_ROLES}`, {});
  }

  queryRoleInfoById(id: string): Observable<Object> {
    return this.$http.post(`${QUERY_ROLE_INFO_BY_ID}` + `${id}`, null);
  }


  addRole(body): Observable<Object> {
    return this.$http.post(`${INSERT_ROLE}`, body);
  }

  modifyRole(body): Observable<Object> {
    return this.$http.post(`${UPDATE_ROLE}`, body);
  }


  deleteRole(body): Observable<Object> {
    return this.$http.post(`${DELETE_ROLE}`, body);
  }

  verifyRoleInfo(body): Observable<Object> {
    return this.$http.post(`${VERIFY_ROLE_INFO}`, body);
  }

  /**
   * 在线用户
   */
  getOnLineUser(body): Observable<Object> {
    return this.$http.post(`${GET_ONLINE_USER}`, body);
  }

  offline(body): Observable<Object> {
    return this.$http.post(`${OFFLINE}`, body);
  }
}
