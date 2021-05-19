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
import {DynamicPipe} from './pipe/dynamic.pipe';
import {CheckSelectInputComponent} from './component/check-select-input/check-select-input.component';
import {TelephoneInputComponent} from './component/telephone-input/telephone-input.component';
import {BsDropdownModule} from 'ngx-bootstrap';
import {ScrollingModule} from '@angular/cdk/scrolling';
import {Download} from './util/download';
import {AccessPermissionDirective} from './directive/access-permission.directive';
import {ImageViewComponent} from './component/image-view/image-view.component';
import {PhotoInfoComponent} from './component/image-view/photo-info/photo-info.component';
import {TableVirtualComponent} from './component/table/table-virtual/table-virtual.component';
import {ImageViewService} from './service/picture-view/image-view.service';
import {TreeRoleSelectorComponent} from './component/tree-role-selector/tree-role-selector.component';
import {SelectValuePipe} from './pipe/selectValue.pipe';
import {RuleUtil} from './util/rule-util';
import {MapSelectorInspectionComponent} from './component/map-selector/map-selector-inspection/map-selector-inspection.component';
import {BusinessTemplateComponent} from './component/business-template/business-template.component';
import {AddTemplateComponent} from './component/business-template/add-template/add-template.component';
import {BoxTemplateComponent} from './component/business-template/box-template/box-template.component';
import {TemplateSearchComponent} from './component/business-template/template-search/template-search.component';
import {FrameTemplateComponent} from './component/business-template/frame-template/frame-template.component';
import {BoardTemplateComponent} from './component/business-template/board-template/board-template.component';
import {ExportMessagePushComponent} from './component/export-message-push/export-message-push.component';
import {ExportMessagePushService} from './service/message-push/message-push.service';
import {BusinessPictureComponent} from './component/business-picture/business-picture.component';
import {CityPickerComponent} from './component/city-picker/city-picker.component';
import {DeviceTypePipe} from './pipe/device-type.pipe';
import {AlarmNameComponent} from './component/alarm/alarm-name/alarm-name.component';
import {AreaComponent} from './component/alarm/area/area.component';
import {AlarmObjectComponent} from './component/alarm/alarm-object/alarm-object.component';
import {SearchInputComponent} from './component/search-input/search-input.component';
import {UserComponent} from './component/alarm/user/user.component';
import {XcNzSelectModule} from './component/select';
import { AudioComponent } from './component/audio/audio.component';
import { AudioMusicService } from './service/audio-music/audio-music.service';
import {NoticeMusicService} from './util/notice-music.service';
import {UnitComponent} from './component/alarm/unit/unit.component';
import { CreateWorkOrderComponent } from './component/alarm/create-work-order/create-work-order.component';

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
  CheckSelectInputComponent,
  TelephoneInputComponent,
  ImageViewComponent,
  PhotoInfoComponent,
  TableVirtualComponent,
  TreeRoleSelectorComponent,
  BusinessTemplateComponent,
  AddTemplateComponent,
  MapSelectorInspectionComponent,
  BoxTemplateComponent,
  TemplateSearchComponent,
  FrameTemplateComponent,
  BoardTemplateComponent,
  ExportMessagePushComponent,
  BusinessPictureComponent,
  CityPickerComponent,
  AlarmNameComponent,
  AreaComponent,
  AlarmObjectComponent,
  SearchInputComponent,
  UserComponent,
  AudioComponent,
  UnitComponent,
  CreateWorkOrderComponent
];

@NgModule({
  imports: [
    BsDropdownModule.forRoot(),
    CommonModule,
    FormsModule,
    XcI18nModule,
    FormsModule,
    ReactiveFormsModule,
    NgZorroAntdModule,
    ScrollingModule,
    RouterModule,
    XcNzSelectModule
  ],
  exports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgZorroAntdModule,
    AccessPermissionDirective,
    ...COMPONENT,
    DynamicPipe,
    DeviceTypePipe,
    XcNzSelectModule
  ],
  providers: [FiLinkModalService, MD5Service, Download, ImageViewService,
    ExportMessagePushService, RuleUtil, AudioMusicService, NoticeMusicService],
  declarations: [...COMPONENT, DynamicPipe, SelectValuePipe
    , AccessPermissionDirective, DeviceTypePipe],
  entryComponents: [...COMPONENT]
})
export class SharedModule {
}
