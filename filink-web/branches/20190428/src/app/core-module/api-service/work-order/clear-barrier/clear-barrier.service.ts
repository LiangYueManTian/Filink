import {Injectable} from '@angular/core';
import {ClearBarrierInterface} from './clear-barrier.interface';
import {Observable} from 'rxjs/index';
import {HttpClient} from '@angular/common/http';
import {WORK_ORDER_URL} from '../work-order-request-url';

@Injectable()
export class ClearBarrierService implements ClearBarrierInterface {
  constructor(
    private $http: HttpClient
  ) {
  }

  getUnfinishedWorkOrderList(body): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_UNFINISHED_CLEAR_BARRIER_WORK_ORDER_LIST_ALL}`, body);
  }

  getHistoryWorkOrderList(body): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_HISTORY_CLEAR_BARRIER_WORK_ORDER_LIST_ALL}`, body);
  }

  checkName(name, id): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.CHECK_CLEAR_BARRIER_WORK_ORDER_NAME_EXIST}`, {title: name, procId: id});
  }

  getAssignedStatistics(): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_ASSIGNED_CLEAR_BARRIER_WORK_ORDER_STATISTICS}`, {});
  }

  getProcessingStatistics(): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_PROCESSING_CLEAR_BARRIER_WORK_ORDER_STATISTICS}`, {});
  }

  getPendingStatistics(): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_PENDING_CLEAR_BARRIER_WORK_ORDER_STATISTICS}`, {});
  }

  getIncreaseStatistics(): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_INCREASE_CLEAR_BARRIER_WORK_ORDER_STATISTICS}`, {});
  }

  getUnfinishedStatistics(): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_UNFINISHED_CLEAR_BARRIER_WORK_ORDER_STATISTICS}`, {});
  }

  getHistoryStatistics(): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_HISTORY_CLEAR_BARRIER_WORK_ORDER_STATISTICS}`, {});
  }

  getStatisticsByErrorReason(): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_CLEAR_BARRIER_WORK_ORDER_STATISTICS_BY_ERROR_REASON}`, {});
  }

  getStatisticsByProcessingScheme(): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_CLEAR_BARRIER_WORK_ORDER_STATISTICS_BY_PROCESSING_SCHEME}`, {});
  }

  getStatisticsByDeviceType(): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_CLEAR_BARRIER_WORK_ORDER_STATISTICS_BY_DEVICE_TYPE}`, {});
  }

  getStatisticsByStatus(): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.GET_CLEAR_BARRIER_WORK_ORDER_STATISTICS_BY_STATUS}`, {});
  }

  sendBackWorkOrder(id): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.SEND_BACK_CLEAR_BARRIER_WORK_ORDER}`, {procId: id});
  }

  revokeWorkOrder(id): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.REVOKE_CLEAR_BARRIER_WORK_ORDER}`, {procId: id});
  }

  assignWorkOrder(id, departmentList): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.ASSIGN_CLEAR_BARRIER_WORK_ORDER}`, {procId: id, departmentList});
  }

  singleBackConfirm(id): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.SINGLE_BACK_CONFIRM}`, {procId: id});
  }

  getWorkOrderDetailById(id): Observable<Object> {
    return this.$http.get(`${WORK_ORDER_URL.GET_CLEAR_BARRIER_WORK_ORDER_BY_ID}/${id}`);
  }

  addWorkOrder(body): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.ADD_CLEAR_BARRIER_WORK_ORDER}`, body);
  }

  updateWorkOrder(body): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.UPDATE_CLEAR_BARRIER_WORK_ORDER}`, body);
  }

  deleteWorkOrder(ids): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.DELETE_CLEAR_BARRIER_WORK_ORDER}`, ids);
  }

  exportUnfinishedWorkOrder(body): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.EXPORT_UNFINISHED_CLEAR_BARRIER_WORK_ORDER}`, body);
  }

  exportHistoryWorkOrder(body): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.EXPORT_HISTORY_CLEAR_BARRIER_WORK_ORDER}`, body);
  }

  RefundGeneratedAgain(body): Observable<Object> {
    return this.$http.post(`${WORK_ORDER_URL.REFUND_GENERATED_AGAIN}`, body);
  }

  getFiveWorkOrder(id): Observable<Object> {
    return this.$http.get(`${WORK_ORDER_URL.GET_FIVE_CLEAR_BARRIER}/${id}`);
  }
}
