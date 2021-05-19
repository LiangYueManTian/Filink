import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {XcI18nModule} from './service/i18N';
import {NgZorroAntdModule} from 'ng-zorro-antd';
import {PaginationComponent} from './component/pagination/pagination.component';
import {FormComponent} from './component/form/form.component';
import {TreeNodeComponent} from './component/tree/tree-node.component';
import {TreeComponent} from './component/tree/tree.component';
import {VideoComponent} from './component/video/video.component';
import {TableComponent} from './component/table/table.component';
import {TableSearchComponent} from './component/table/table-search/table-search.component';
import {TreeSelectorComponent} from './component/tree-selector/tree-selector.component';
import {SelectorComponent} from './component/selector/selector.component';
import {FilinkMenuComponent} from './component/filink-menu/filink-menu.component';
import {MapSelectorComponent} from './component/map-selector/map-selector.component';
import {BreadcrumbComponent} from './component/breadcrumb/breadcrumb.component';
import {RouterModule} from '@angular/router';
import {DetailTitleComponent} from './component/detail-title/detail-title.component';
import {DatePickerComponent} from './component/date-picker/date-picker.component';
import {MapComponentComponent} from './component/map-selector/map-component/map-component.component';
import {TreeAreaSelectorComponent} from './component/tree-area-selector/tree-area-selector.component';
import {FiLinkModalService} from './service/filink-modal/filink-modal.service';
import {MD5Service} from './util/md5.service';
import {MapComponent} from './component/map/map.component';
import { DynamicPipe } from './pipe/dynamic.pipe';
import {Download} from './util/download';

const COMPONENT = [
  PaginationComponent,
  FormComponent,
  TreeNodeComponent,
  TreeComponent,
  VideoComponent,
  TableComponent,
  TableSearchComponent,
  SelectorComponent,
  TreeSelectorComponent,
  FilinkMenuComponent,
  MapSelectorComponent,
  BreadcrumbComponent,
  DetailTitleComponent,
  DatePickerComponent,
  MapComponentComponent,
  TreeAreaSelectorComponent,
  MapComponent,
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    XcI18nModule,
    FormsModule,
    ReactiveFormsModule,
    NgZorroAntdModule,
    RouterModule
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgZorroAntdModule,
    ...COMPONENT
  ],
  providers: [FiLinkModalService, MD5Service, Download],
  declarations: [...COMPONENT, DynamicPipe],
  entryComponents: [...COMPONENT]
})
export class SharedModule {
}
