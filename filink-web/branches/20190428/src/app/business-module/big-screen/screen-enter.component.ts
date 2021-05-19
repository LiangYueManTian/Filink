import {Component, OnInit, TemplateRef, ViewChild, Input, Output, ElementRef} from '@angular/core';
import {NzI18nService} from 'ng-zorro-antd';
import {ShowScreenComponent} from './show-screen/show-screen.component';
import {Result} from '../../shared-module/entity/result';
import {FiLinkModalService} from '../../shared-module/service/filink-modal/filink-modal.service';


@Component({
  selector: 'app-enterScreen',
  templateUrl: './screen-enter.component.html',
  styleUrls: ['./screen-enter.component.scss']
})
export class ScreenEnterComponent implements OnInit {
  // 是否显示大屏
  isVisible = false;
  // 显示修改名称
  isEditName = true;
  @ViewChild('showViewScreen') showViewScreen: ShowScreenComponent;
  @ViewChild('showViewScreen') viewScreen: ElementRef;

  constructor(private $I18n: NzI18nService,
              private $message: FiLinkModalService
  ) {
  }

  ngOnInit() {
    console.log('大屏入口');
  }

  /**
   * 显示大屏
   */
  showScreen() {
    this.isVisible = true;
    setTimeout(() => {
      this.showViewScreen.shoeViewScreen();
    }, 0);
    const fullscreenDiv = window.document.body;
    let fullscreenFunc = fullscreenDiv.requestFullscreen;
    if (!fullscreenFunc) {
      // 判断浏览器内核
      ['mozRequestFullScreen', 'msRequestFullscreen', 'webkitRequestFullScreen'].forEach(function (req) {
        fullscreenFunc = fullscreenFunc || fullscreenDiv[req];
      });
    }
    // 把全屏展示的内容 通过call 改变this指向
    fullscreenFunc.call(fullscreenDiv);

  }

  /**
   * 修改大屏名称
   */
  editName() {
    this.isEditName = false;
  }

  changeName() {
    // todo 失去焦点请求接口
    this.isEditName = true;

  }

}
