import {Routes} from '@angular/router';
import {WorkOrderComponent} from './work-order.component';
import {SalesWorkOrderComponent} from './sales-work-order/sales-work-order.component';
import {InspectionWorkOrderComponent} from './inspection-work-order/inspection-work-order.component';
import {UnfinishedSalesWorkOrderComponent} from './sales-work-order/unfinished-sales-work-order/unfinished-sales-work-order.component';
import {HistorySalesWorkOrderComponent} from './sales-work-order/history-sales-work-order/history-sales-work-order.component';
import {SalesWorkOrderDetailComponent} from './sales-work-order/sales-work-order-detail/sales-work-order-detail.component';
import {InspectionTaskComponent} from './inspection-work-order/inspection-task/inspection-task.component';
import {InspectionTaskDetailComponent} from './inspection-work-order/inspection-task-detail/inspection-task-detail.component';
import {UnfinishedInspectionWorkOrderComponent} from './inspection-work-order/unfinished-inspection-work-order/unfinished-inspection-work-order.component';
import {InspectionWorkOrderDetailComponent} from './inspection-work-order/inspection-work-order-detail/inspection-work-order-detail.component';
import {FinishedInspectionWorkOrderComponent} from './inspection-work-order/finished-inspection-work-order/finished-inspection-work-order.component';

export const ROUTER_CONFIG: Routes = [
  {
    path: '',
    component: WorkOrderComponent,
    children: [
      {
        path: 'sales',
        component: SalesWorkOrderComponent,
        children: [
          {
            path: 'unfinished',
            component: UnfinishedSalesWorkOrderComponent,
            data: {
              breadcrumb: [
                { label: 'workOrderManagement', url: 'work-order' },
                { label: 'salesWorkOrder', url: 'sales' },
                { label: 'unfinishedSalesWorkOrder' }
              ]
            }
          },
          {
            path: 'history',
            component: HistorySalesWorkOrderComponent,
            data: {
              breadcrumb: [
                { label: 'workOrderManagement', url: 'work-order' },
                { label: 'salesWorkOrder', url: 'sales' },
                { label: 'historySalesWorkOrder' }
              ]
            }
          },
          {
            path: 'add',
            component: SalesWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                { label: 'workOrderManagement', url: 'work-order' },
                { label: 'salesWorkOrder', url: 'sales' },
                { label: 'unfinishedSalesWorkOrder', url: 'unfinished' },
                { label: 'addWorkOrder' }
              ]
            }
          },
          {
            path: 'view',
            component: SalesWorkOrderDetailComponent,
            data: {
              breadcrumb: [
                { label: 'workOrderManagement', url: 'work-order' },
                { label: 'salesWorkOrder', url: 'sales' },
                { label: 'unfinishedSalesWorkOrder', url: 'unfinished' },
                { label: 'viewWorkOrder' }
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
                { label: 'work-order', url: 'work-order' },
                { label: 'inspection-work-order', url: 'inspection' },
                { label: 'inspection-task'},
              ]
            },
            children: [
              {
                path: ':type',
                component: InspectionTaskDetailComponent,
                data: {
                  breadcrumb: [
                    { label: 'work-order', url: 'work-order' },
                    { label: 'inspection-work-order', url: 'inspection' },
                    { label: 'inspection-task', url: 'inspection-task' },
                    { label: 'inspection-task-detail' }
                  ]
                }
              },
            ]
          },
          {
            path: 'unfinished',
            component: UnfinishedInspectionWorkOrderComponent,
            data: {
              breadcrumb: [
                { label: 'work-order', url: 'work-order' },
                { label: 'inspection-work-order', url: 'inspection' },
                { label: 'unfinished-inspection-work-order'},
              ]
            },
            children: [
              {
                path: ':type',
                component: InspectionWorkOrderDetailComponent,
                data: {
                  breadcrumb: [
                    { label: 'work-order', url: 'work-order' },
                    { label: 'inspection-work-order', url: 'inspection' },
                    { label: 'unfinished-inspection-work-order', url: 'unfinished' },
                    { label: 'inspection-work-order-detail' }
                  ]
                }
              },
            ]
          },
          {
            path: 'finished',
            component: FinishedInspectionWorkOrderComponent,
            data: {
              breadcrumb: [
                { label: 'work-order', url: 'work-order' },
                { label: 'inspection-work-order', url: 'inspection' },
                { label: 'finished-inspection-work-order'},
              ]
            }
          }
        ]
      }
    ]
  }
];

