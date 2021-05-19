import {Subject} from 'rxjs';

/**
 * Created by wh1709040 on 2019/2/13.
 */
export class NativeWebsocketImplService {
  private socket;
  // 心跳检测时间 默认一分钟发起一次心跳检测
  private heartCheckTime = 60 * 1000;
  private heartCheckTimeOut;
  // 通过订阅的方式拿到消息
  subscibeMessage;
  private messageTopic;
  private isConnect: boolean;
  private connectNum: number = 0;

  // 是否为连接状态
  constructor() {
    // this.connect();
  }

  /**
   * 连接
   */
  connect() {
    this.messageTopic = new Subject<any>();
    this.subscibeMessage = this.messageTopic.asObservable();
    this.socket = new WebSocket(`ws://${location.host}/websocket/${sessionStorage.getItem('token')}`);
    // this.socket = new WebSocket(`ws://10.5.33.51:9001/websocket/${sessionStorage.getItem('token')}`);

    // 链接成功
    this.socket.onopen = () => {
      console.log('链接成功！');
      this.isConnect = true;
    };
    // 开启心跳检测
    // this.heartCheckStart();
    this.socket.onmessage = (event) => {
      this.messageTopic.next(event);
    };
  }

  /**
   * 关闭
   */
  close() {
    if (this.socket) {
      this.socket.close();
      this.messageTopic.complete();
    }
  }

  /**
   * 获取数据（可能存在隐患）
   * 建议适用订阅的方式获取数据
   * param {(event) => {}} fn
   */
  getMessage(fn: (event) => {}) {
    if (this.socket) {
      this.socket.onmessage = (event) => {
        this.isConnect = true;
        if (fn) {
          fn(event);
        }
      };
    }
  }

  /**
   * 心跳开始
   */
  heartCheckStart() {
    this.connectNum++;
    this.isConnect = false;
    this.heartCheckTimeOut = setTimeout(() => {
      this.socket.send('ping');
      console.log('正在ping服务器。。。。。。。');
      setTimeout(() => {
        if (!this.isConnect && this.connectNum < 4) {
          // 重新连接
          this.heartCheckRest();
          this.connect();
        }
      }, 6000);
    }, this.heartCheckTime);

  }

  /**
   * 重置心跳
   */
  heartCheckRest() {
    clearInterval(this.heartCheckTimeOut);
    this.connectNum = 0;
  }
}
