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
            path: 'unfinished-list',
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
            path: 'history-list',
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
            path: 'unfinished-detail/add',
            component: ClearBarrierWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'clearBarrierWorkOrder'},
                {label: 'unfinishedClearBarrierWorkOrder', url: '/business/work-order/clear-barrier/unfinished-list'},
                {label: 'addClearBarrierWorkOrder'}
              ]
            }
          },
          {
            path: 'unfinished-detail/update',
            component: ClearBarrierWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'clearBarrierWorkOrder'},
                {label: 'unfinishedClearBarrierWorkOrder', url: '/business/work-order/clear-barrier/unfinished-list'},
                {label: 'modifyClearBarrierWorkOrder'}
              ]
            }
          },
          {
            path: 'unfinished-detail/rebuild',
            component: ClearBarrierWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'clearBarrierWorkOrder'},
                {label: 'unfinishedClearBarrierWorkOrder', url: '/business/work-order/clear-barrier/unfinished-list'},
                {label: 'rebuild'}
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
            path: 'task-list',
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
            path: 'task_device',
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
            path: 'task-detail/add',
            component: InspectionTaskDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'inspectionTask', url: '/business/work-order/inspection/task-list'},
                {label: 'addInspectionTask'}
              ]
            }
          },
          {
            path: 'task-detail/update',
            component: InspectionTaskDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'inspectionTask', url: '/business/work-order/inspection/task-list'},
                {label: 'updateInspectionTask'}
              ]
            }
          },
          {
            path: 'unfinished-list',
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
            path: 'unfinished-detail/add',
            component: InspectionWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'unfinishedInspectionWorkOrder', url: '/business/work-order/inspection/unfinished-list'},
                {label: 'newInspectionWorkOrder'}
              ]
            }
          },
          {
            path: 'unfinished-detail/update',
            component: InspectionWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                {label: 'workOrderManagement'},
                {label: 'inspectionWorkOrder'},
                {label: 'unfinishedInspectionWorkOrder', url: '/business/work-order/inspection/unfinished-list'},
                {label: 'updateInspectionWorkOrder'}
              ]
            }
          },
          {
            path: 'unfinished-detail/updates',
            component: InspectionWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                { label: 'workOrderManagement', url: '/business/work-order' },
                { label: 'inspectionWorkOrder', url: '/business/work-order/inspection' },
                { label: 'unfinishedInspectionWorkOrder', url: '/business/work-order/inspection/unfinished-list' },
                { label: 'updateInspectionWorkOrder' }
              ]
            }
          },
          {
            path: 'finished-list',
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

