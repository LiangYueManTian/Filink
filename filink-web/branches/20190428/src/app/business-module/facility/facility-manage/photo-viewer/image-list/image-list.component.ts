import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  OnInit,
  Output,
  SimpleChanges,
  TemplateRef,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import Viewer from 'viewerjs';
import {Overlay, OverlayRef} from '@angular/cdk/overlay';
import {TemplatePortal} from '@angular/cdk/portal';
import {PictureInfo} from '../photo-viewer-entity';
import {Download} from '../../../../../shared-module/util/download';
import {NzI18nService} from 'ng-zorro-antd';

@Component({
  selector: 'app-image-list',
  templateUrl: './image-list.component.html',
  styleUrls: ['./image-list.component.scss']
})
export class ImageListComponent implements OnInit, OnChanges {

  @Input() photoList: Array<PictureInfo> = [];
  @Input() timeStr = '';
  @Output() selectChange = new EventEmitter();
  @Output() viewItem = new EventEmitter();
  @Output() delImg = new EventEmitter();
  @Output() viewLargerImage = new EventEmitter();

  // 当前图片信息
  curPhoto = {
    picName: ''
  };
  viewer: Viewer;
  // 全部勾选
  allChecked = false;
  // 已勾选列表
  selectedList = [];
  hidden = true;
  // 国际化
  language: any;
  overlayRef: OverlayRef;
  @ViewChild('picInfo') picInfoTemplate: TemplateRef<any>;
  constructor( private overlay: Overlay,
               private $download: Download,
               private $nzI18n: NzI18nService,
               private viewContainerRef: ViewContainerRef) { }

  ngOnInit() {
    this.language = this.$nzI18n.getLocaleData('facility');
  }

  /**
   * 监听数据变化
   * param changes
   */
  ngOnChanges(changes: SimpleChanges) {
    // 监听到数据变化时取消全选
    this.allChecked = false;
  }

  /**
   * 勾选
   */
  checkItem(item) {
    if (!this.selectedList.some((el) => item.devicePicId === el.devicePicId)) {
      this.selectedList.push(item);
    } else {
      this.selectedList = this.selectedList.filter(el => el.devicePicId !== item.devicePicId);
      item.checked = false;
    }
    this.updateAllChecked();
    const obj = {};
    obj[this.timeStr] = this.selectedList;
    this.selectChange.emit(obj);
  }

  /**
   * 全部勾选/取消
   */
  selectAll() {
    if (this.allChecked) {
      this.photoList.forEach(item => item.checked = true);
      this.selectedList = this.photoList;
    } else {
      this.photoList.forEach(item => item.checked = false);
      this.selectedList = [];
    }
    const obj = {};
    obj[this.timeStr] = this.selectedList;
    this.selectChange.emit(obj);
  }

  /**
   * 图片删除
   * param item
   */
  delete(item) {
    // 如果该图片已勾选则先去掉勾选
    if (item.checked) {
      this.checkItem(item);
    }
    this.delImg.emit([item]);
  }
  /**
   * 大图退出
   */
  close() {
    this.curPhoto = {picName: ''};
    this.viewer.hide();
  }

  /**
   * 查看详情
   * param item
   */
  clickItem(event, item) {
    event.stopPropagation();
    this.viewItem.emit(item);
  }

  /**
   * 鼠标移动到眼睛显示图片信息
   * param item
   * param picInfo
   */
  showPicInfo(item, picInfo) {
    const strategy = this.overlay
      .position()
      .flexibleConnectedTo(item.target).withPositions([{ originX: 'start',
        originY: 'bottom',
        overlayX: 'start',
        overlayY: 'top',
        offsetY: 10
        }]);
    this.overlayRef = this.overlay.create({
      hasBackdrop: false,
      positionStrategy: strategy
    });
    this.overlayRef.attach(new TemplatePortal(this.picInfoTemplate, this.viewContainerRef, {picInfo}));
  }

  /**
   * 移开隐藏图片信息
   */
  hidePicInfo() {
    this.overlayRef.detach();
  }

  /**
   * 点击图片查看大图
   * param item
   */
  viewBigPic(item) {
    this.viewLargerImage.emit({
      curPicInfo: item,
      bigPicList: this.photoList
    });
  }

  /**
   * 图片下载
   * param url
   */
  download(url) {
    this.$download.downloadFile(url);
  }
  /**
   * 更新全选
   */
  private updateAllChecked() {
    if (this.selectedList.length === this.photoList.length) {
      this.allChecked = true;
    } else {
      this.allChecked = false;
    }
  }
}
