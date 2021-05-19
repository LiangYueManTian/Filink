import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WorkOrderComponent } from './work-order.component';
import { SalesWorkOrderComponent } from './sales-work-order/sales-work-order.component';
import { InspectionWorkOrderComponent } from './inspection-work-order/inspection-work-order.component';
import {RouterModule} from '@angular/router';
import {ROUTER_CONFIG} from './work-order.routes';
import { UnfinishedSalesWorkOrderComponent } from './sales-work-order/unfinished-sales-work-order/unfinished-sales-work-order.component';
import { HistorySalesWorkOrderComponent } from './sales-work-order/history-sales-work-order/history-sales-work-order.component';
import {SharedModule} from '../../shared-module/shared-module.module';
import { SalesWorkOrderDetailComponent } from './sales-work-order/sales-work-order-detail/sales-work-order-detail.component';
import { InspectionTaskComponent } from './inspection-work-order/inspection-task/inspection-task.component';
import { InspectionWorkOrderDetailComponent } from './inspection-work-order/inspection-work-order-detail/inspection-work-order-detail.component';
import { UnfinishedInspectionWorkOrderComponent } from './inspection-work-order/unfinished-inspection-work-order/unfinished-inspection-work-order.component';
import { FinishedInspectionWorkOrderComponent } from './inspection-work-order/finished-inspection-work-order/finished-inspection-work-order.component';
import { InspectionTaskDetailComponent } from './inspection-work-order/inspection-task-detail/inspection-task-detail.component';

@NgModule({
  declarations: [
    WorkOrderComponent,
    SalesWorkOrderComponent,
    InspectionWorkOrderComponent,
    UnfinishedSalesWorkOrderComponent,
    HistorySalesWorkOrderComponent,
    SalesWorkOrderDetailComponent,
    InspectionTaskComponent,
    InspectionWorkOrderDetailComponent,
    UnfinishedInspectionWorkOrderComponent,
    FinishedInspectionWorkOrderComponent,
    InspectionTaskDetailComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(ROUTER_CONFIG),
    SharedModule,
  ]
})
export class WorkOrderModule { }
