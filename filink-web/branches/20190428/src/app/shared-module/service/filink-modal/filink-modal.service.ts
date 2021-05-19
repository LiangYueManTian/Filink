/**
 * Created by xiaoconghu on 2019/1/28.
 */
import {Injectable} from '@angular/core';
import {NzMessageService, NzModalService} from 'ng-zorro-antd';

@Injectable()
export class FiLinkModalService {

  constructor(private $modalService: NzModalService,
              private $messageService: NzMessageService) {
  }

  /**
   * 正常信息
   * param msg
   */
  info(msg: string): void {
    this.$modalService.info({
      nzTitle: '提示',
      nzContent: msg,
    });
  }

  /**
   * 成功信息
   * param msg
   */
  success(msg: string): void {
    this.$modalService.success({
      nzTitle: '提示',
      nzContent: msg
    });
  }

  /**
   * 错误信息
   * param msg
   */
  error(msg: string): void {
    this.$modalService.error({
      nzTitle: '提示',
      nzContent: msg
    });
  }

  /**
   * 警告信息
   * param msg
   */
  warning(msg: string): void {
    this.$modalService.warning({
      nzTitle: '提示',
      nzContent: msg
    });
  }

  /**
   * 加载
   * param msg
   */
  loading(msg: string, duration = 3000): void {
    this.$messageService.remove();
    this.$messageService.loading(msg, { nzDuration: duration});
  }

  /**
   * 加载
   * param msg
   */
  remove(): void {
    this.$messageService.remove();
  }
}
