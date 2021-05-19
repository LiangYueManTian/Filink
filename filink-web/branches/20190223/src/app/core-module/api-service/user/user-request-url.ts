import { USER_SERVER } from '../api-common.config';

const USER = `${USER_SERVER}/user`;
// const USER = 'user';
// 查询用户信息列表(按框架组件要求的)
export const QUERY_USER_LIST = `${USER}/queryUserList`;
// 查询用户列表(后端重写自定义的)
export const QUERY_USER_LISTS = `${USER}/queryUserByField`;
// 查询单个用户信息
export const QUERY_USER_INFO_BY_ID = `${USER}/queryUserInfoById/`;
// 新增单个用户
export const INSERT_USER = `${USER}/insert`;
// 修改用户
export const UPDATE_USER = `${USER}/update`;
// 删除用户
export const DELETE_USER = `${USER}/deleteByIds`;
// 用户状态修改
export const UPDATE_USER_STATUS = `${USER}/updateUserStatus`;
// 用户校验
export const VERIFY_USER_INFO = `${USER}/verifyUserInfo`;
// 重置密码
export const RESET_PASSWORD = `${USER}/resetPWD`;
// 修改密码
export const MODIFY_PASSWORD = `${USER}/modifyPWD`;
// 查询用户默认密码
export const QUERY_PASSWORD = `${USER}/queryUserDefaultPWD`;

export const LOGOUT = `${USER}/logout`;
// 在线用户信息列表
export const GET_ONLINE_USER = `${USER}/getOnLineUser`;
// 强制下线在线用户
export const OFFLINE = `${USER}/forceOffline`;


const DEPARTMENT = `${USER_SERVER}/department`;
// const DEPARTMENT = 'department';
// 查询部门列表信息
export const QUERY_DEPT_LIST = `${DEPARTMENT}/queryDeptList`;
// 不分页查询部门信息
export const QUERY_ALL_DEPARTMENT = `${DEPARTMENT}/queryTotalDepartment`;
// 查询单个部门信息
export const QUERY_DEPT_INFO_BY_ID = `${DEPARTMENT}/queryDeptInfoById/`;
// 新增单个部门
export const INSERT_DEPT = `${DEPARTMENT}/insert`;
// 删除部门
export const DELETE_DEPT = `${DEPARTMENT}/deleteByIds`;
// 修改部门信息
export const UPDATE_DEPT = `${DEPARTMENT}/update`;
// 查询所有单位/部门(无分页)
export const QUERY_TOTAL_DEPT = `${DEPARTMENT}/queryTotalDepartment`;
// 查询所有部门(有分页)
export const QUERY_ALL_DEPT = `${DEPARTMENT}/queryDepartmentList`;
// 单位部门校验
export const VERIFY_DEPT_INFO = `${DEPARTMENT}/verifyDeptInfo`;
// 用户列表所有部门(平级)
export const QUERY_TOTAL_DEPARTMENT = `${DEPARTMENT}/conditionDepartment`;

const ROLE = `${USER_SERVER}/role`;
// const ROLE = 'role';
// 查询角色列表信息
export const QUERY_ROLE_LIST = `${ROLE}/queryRoleList`;
// 查询角色新接口
export const QUERY_ROLES_LIST = `${ROLE}/queryRoleByField`;
// 查询单个角色信息
export const QUERY_ROLE_INFO_BY_ID = `${ROLE}/queryRoleInfoById/`;
// 查询所有角色(无分页)
export const QUERY_ALL_ROLES = `${ROLE}/queryAllRoles`;
// 新增单个角色
export const INSERT_ROLE = `${ROLE}/insert`;
// 修改角色信息
export const UPDATE_ROLE = `${ROLE}/update`;
// 删除角色
export const DELETE_ROLE = `${ROLE}/deleteByIds`;
// 角色校验
export const VERIFY_ROLE_INFO = `${ROLE}/verifyRoleInfo`;
