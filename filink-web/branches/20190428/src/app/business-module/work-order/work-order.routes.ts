import {Routes} from '@angular/router';
import {WorkOrderComponent} from './work-order.component';
import {InspectionWorkOrderComponent} from './inspection/inspection-work-order.component';
import {InspectionTaskComponent} from './inspection/task/inspection-task.component';
import {InspectionTaskDetailComponent} from './inspection/task-detail/inspection-task-detail.component';
import {UnfinishedInspectionWorkOrderComponent} from './inspection/unfinished/unfinished-inspection-work-order.component';
import {InspectionWorkOrderDetailComponent} from './inspection/detail/inspection-work-order-detail.component';
import {FinishedInspectionWorkOrderComponent} from './inspection/finished/finished-inspection-work-order.component';
import {ClearBarrierWorkOrderComponent} from './clear-barrier/clear-barrier-work-order.component';
import {HistoryClearBarrierWorkOrderComponent} from './clear-barrier/history/history-clear-barrier-work-order.component';
import {UnfinishedClearBarrierWorkOrderComponent} from './clear-barrier/unfinished/unfinished-clear-barrier-work-order.component';
import {ClearBarrierWorkOrderDetailComponent} from './clear-barrier/detail/clear-barrier-work-order-detail.component';
import {SetAreaDeviceComponent} from './inspection/set-area-device/set-area-device.component';

export const ROUTER_CONFIG: Routes = [
  {
    path: '',
    component: WorkOrderComponent,
    children: [
      {
        path: 'clear-barrier',
        component: ClearBarrierWorkOrderComponent,
        children: [
          {
            path: 'unfinished',
            component: UnfinishedClearBarrierWorkOrderComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'clearBarrierWorkOrder'},
                {label: 'unfinishedClearBarrierWorkOrder'}
              ]
            },
          },
          {
            path: 'history',
            component: HistoryClearBarrierWorkOrderComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'clearBarrierWorkOrder'},
                {label: 'historyClearBarrierWorkOrder'}
              ]
            }
          },
          {
            path: 'unfinished/add',
            component: ClearBarrierWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'clearBarrierWorkOrder'},
                {label: 'unfinishedClearBarrierWorkOrder', url: '/business/work-order/clear-barrier/unfinished'},
                {label: 'addClearBarrierWorkOrder'}
              ]
            }
          },
          {
            path: 'unfinished/update',
            component: ClearBarrierWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'clearBarrierWorkOrder'},
                {label: 'unfinishedClearBarrierWorkOrder', url: '/business/work-order/clear-barrier/unfinished'},
                {label: 'modifyClearBarrierWorkOrder'}
              ]
            }
          },
        ]
      },
      {
        path: 'inspection',
        component: InspectionWorkOrderComponent,
        children: [
          {
            path: 'task',
            component: InspectionTaskComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'inspectionTask'},
              ]
            },
          },
          {
            path: 'task/inspectionDevice',
            component: SetAreaDeviceComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'inspectionTask'},
              ]
            }
          },
          {
            path: 'task/add',
            component: InspectionTaskDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'inspectionTask', url: '/business/work-order/inspection/task'},
                {label: 'addInspectionTask'}
              ]
            }
          },
          {
            path: 'task/update',
            component: InspectionTaskDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'inspectionTask', url: '/business/work-order/inspection/task'},
                {label: 'updateInspectionTask'}
              ]
            }
          },
          {
            path: 'unfinished',
            component: UnfinishedInspectionWorkOrderComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'unfinishedInspectionWorkOrder'},
              ]
            },
          },
          {
            path: 'unfinished/add',
            component: InspectionWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'unfinishedInspectionWorkOrder', url: '/business/work-order/inspection/unfinished'},
                {label: 'newInspectionWorkOrder'}
              ]
            }
          },
          {
            path: 'unfinished/update',
            component: InspectionWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'unfinishedInspectionWorkOrder', url: '/business/work-order/inspection/unfinished'},
                {label: 'updateInspectionWorkOrder'}
              ]
            }
          },
          {
            path: 'unfinished/updates',
            component: InspectionWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                { label: 'workOrderManagement', url: '/business/work-order' },
                { label: 'inspectionWorkOrder', url: '/business/work-order/inspection' },
                { label: 'unfinishedInspectionWorkOrder', url: '/business/work-order/inspection/unfinished' },
                { label: 'updateInspectionWorkOrder' }
              ]
            }
          },
          {
            path: 'finished',
            component: FinishedInspectionWorkOrderComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'finishedInspectionWorkOrder'},
              ]
            }
          }
        ]
      }
    ]
  }
];

