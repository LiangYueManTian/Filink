import {Component, OnInit, ViewChild, ElementRef, TemplateRef, ViewContainerRef, OnDestroy} from '@angular/core';
import {NzNotificationService, NzI18nService, NzModalService, NzModalRef} from 'ng-zorro-antd';
import {Router} from '@angular/router';
import {IndexMissionService} from '../core-module/mission/index.mission.service';
import {AlarmService} from '../core-module/api-service/alarm';
import {ThreeMenuComponent} from './menu/left-menu/three-menu/three-menu.component';
import {NativeWebsocketImplService} from '../core-module/websocket/native-websocket-impl.service';
import {UserService} from '../core-module/api-service/user/user-manage';
import {Result} from '../shared-module/entity/result';
import {AlarmStoreService} from '../core-module/store/alarm.store.service';
import {AlarmLevelSetConfig} from './alarm/alarm-manage/alarm-set/current-alarm-set/alarm-level-set/config';
import {QueryCondition} from '../shared-module/entity/queryCondition';
import {CurrAlarmServiceService} from './alarm/alarm-manage/current-alarm/curr-alarm-service.service';
import {FiLinkModalService} from '../shared-module/service/filink-modal/filink-modal.service';
import {CommonLanguageInterface} from 'src/assets/i18n/common/common.language.interface';
import {SessionUtil} from '../shared-module/util/session-util';
import {OverlayRef, Overlay} from '@angular/cdk/overlay';
import {TemplatePortal} from '@angular/cdk/portal';
import {BusinessWebsocketMsgService} from './business-websocket-msg.service';
import {SecurityPolicyService} from '../core-module/api-service/system-setting/security-policy/security-policy.service';
import {UpdatePasswordService} from './menu/top-menu/update-password/update-password-service';
import {UpdateUserNameService} from '../business-module/user/user-manage/modify-user/update-username-service';
import { AudioMusicService } from 'src/app/shared-module/service/audio-music/audio-music.service';

@Component({
  selector: 'app-business',
  templateUrl: './business.component.html',
  styleUrls: ['./business.component.scss']
})
export class BusinessComponent implements OnInit, OnDestroy {
  isCollapsed = true;
  // ????????????
  userInfo = {userNickname: '', id: '', userName: ''};
  userName: string = '';
  // ?????????????????????
  menuList = [];
  // ????????????????????????
  threeMenuInfo = {menuName: '', children: []};
  // ???????????????????????????
  isUpdatePassword = false;
  alarmStyle = {
    urgency: { style: '', data: undefined },
    main: { style: '', data: undefined },
    secondly: { style: '', data: undefined },
    remind: { style: '', data: undefined }
  };
  alarmArray: Array<any> = [];
  queryCondition: QueryCondition = new QueryCondition();
  alarmColorObj = {};
  colorInfo = {};
  language: CommonLanguageInterface;

  // ????????????????????????
  indexSearchList = [];
  // ????????????
  searchValue: '';
  // ????????????
  displaySettings = {
    screenDisplay: '',
    screenScroll: '',
    screenScrollTime: 0,
    systemLogo: '',
    timeType: '',
  };
  overlayRef: OverlayRef;
  player1: any;
  // ??????????????????
  passwordCheckObj = {
    minLength: 6,
    containLower: '1',
    containUpper: '1',
    containNumber: '1',
    containSpecialCharacter: '1'
  };
  loginTimer = null; // ?????????????????????


  confirmModal: NzModalRef;
  @ViewChild('threeMenu') private threeMenu: ThreeMenuComponent;
  @ViewChild('searchTemplate') searchTemplate: TemplateRef<any>;
  @ViewChild('searchInput') searchInput: ElementRef;

  constructor(public $router: Router,
              public $alarmStoreService: AlarmStoreService,
              public $currentAalarmService: CurrAlarmServiceService,
              private $mission: IndexMissionService,
              private $alarmService: AlarmService,
              private $userService: UserService,
              private el: ElementRef,
              private $nzNotificationService: NzNotificationService,
              private $wsService: NativeWebsocketImplService,
              private $message: FiLinkModalService,
              private $i18n: NzI18nService,
              private overlay: Overlay,
              private viewContainerRef: ViewContainerRef,
              private $businessWebsocketMsgService: BusinessWebsocketMsgService,
              private $securityPolicyService: SecurityPolicyService,
              private $updatePasswordService: UpdatePasswordService,
              private $nzModalService: NzModalService,
              private $updateUserNameService: UpdateUserNameService,
              private $audioMusicService: AudioMusicService
              ) {
    this.$currentAalarmService.getMessage().subscribe(res => {
      if (res === 2) {
        this.queryAlarmCount();
      }
      if (res === 1) {
        this.queryAlarmLevel();
      }
    });
    this.$updatePasswordService.getMessage().subscribe(res => {
      if (res === 1) {
        this.queryPasswordSecurity();
      }
    });
    this.$updateUserNameService.getMessage().subscribe(res => {
      if (res) {
        localStorage.setItem('userName', res);
        this.userName = localStorage.getItem('userName');
      }
    });
  }

  ngOnInit() {
    this.userInfo = SessionUtil.getUserInfo();
    this.$wsService.connect();
    this.initAlarmColor();
    this.language = this.$i18n.getLocaleData('common');
    this.getMenuList();
    localStorage.setItem('userName', localStorage.getItem('userName') ? localStorage.getItem('userName') : this.userInfo.userName);
    this.userName = localStorage.getItem('userName');
    this.queryAlarmLevel();
    this.queryAlarmCount();
    this.$wsService.subscibeMessage.subscribe(msg => {
      // this.alarmMusic(JSON.parse(msg.data));
      this.$businessWebsocketMsgService.dealMsg(msg);
      this.alarmHint(msg);
    });
    this.createOverlayRef();
    this.initDisplaySettings();
    this.queryPasswordSecurity();
    // ???????????????????????? ???????????????
    this.loginTimer = setInterval(() => {
      if (!SessionUtil.getToken()) {
        this.$wsService.close();
        this.$router.navigate(['/login']).then();
        this.$message.warning('??????????????????????????????');
      }
    }, 1000);
  }

  // ???????????????
  alarmHint(msg) {
    const alarmMsg = JSON.parse(msg.data);
    if ( alarmMsg.channelKey === 'alarm' ) {
      const lecelCode = alarmMsg.msg.alarmLevelCode;
      switch (lecelCode) {
        case '1':
          this.alarmStyle.urgency.data = Number(this.alarmStyle.urgency.data) + 1;
          break;
        case '2':
            this.alarmStyle.main.data = Number(this.alarmStyle.main.data) + 1;
          break;
        case '3':
          this.alarmStyle.secondly.data = Number(this.alarmStyle.secondly.data) + 1;
        break;
        case '4':
          this.alarmStyle.remind.data = Number(this.alarmStyle.remind.data) + 1;
        break;
      }
      if ( alarmMsg.msg.isPrompt ===  '1') {
        // ??????????????????
        this.$audioMusicService.playAudio(alarmMsg);
      }
    }

  }

  /**
   * ??????????????????
   */
  getMenuList() {
    let menuList = [];
    if (localStorage.getItem('menuList') !== 'undefined') {
      menuList = JSON.parse(localStorage.getItem('menuList'));
    }
    const newMenu = [{
      'children': [],
      'createTime': 1548947982000,
      'createUser': '',
      'imageUrl': 'fiLink-index',
      'isShow': '2',
      'menuHref': '/business/index',
      'menuId': '01',
      'menuLevel': 1,
      'menuName': '??????',
      'menuSort': 0,
      'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
      'updateTime': 1552374198000,
      'updateUser': ''
    }, {
      'children': [{
        'children': [],
        'createTime': 1548152892000,
        'createUser': '?????????',
        'imageUrl': 'xx',
        'isShow': '1',
        'menuHref': '/business/facility/facility-list',
        'menuId': '02-1',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 1,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '02',
        'updateTime': 1550748936000,
        'updateUser': ''
      }, {
        'children': [],
        'createTime': 1548947908000,
        'createUser': '',
        'imageUrl': 'xx',
        'isShow': '1',
        'menuHref': '/business/facility/facility-log',
        'menuId': '02-2',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 5,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '02',
        'updateTime': 1550748936000,
        'updateUser': ''
      }, {
        'children': [],
        'createTime': 1548153102000,
        'createUser': '?????????',
        'imageUrl': 'xx',
        'isShow': '1',
        'menuHref': '/business/facility/area-list',
        'menuId': '02-6',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 6,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '02',
        'updateTime': 1550748936000,
        'updateUser': ''
      }],
      'createTime': 1548152753000,
      'createUser': '?????????',
      'imageUrl': 'fiLink-facilities',
      'isShow': '1',
      'menuHref': 'xx',
      'menuId': '02',
      'menuLevel': 1,
      'menuName': '????????????',
      'menuSort': 2,
      'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
      'updateTime': 1552374212000,
      'updateUser': ''
    }, {
      'children': [{
        'children': [],
        'createTime': 1548945563000,
        'createUser': '?????????',
        'imageUrl': 'xx',
        'isShow': '1',
        'menuHref': '/business/user/user-list',
        'menuId': '4-1',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 1,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '04',
        'updateTime': 1550748936000,
        'updateUser': ''
      }, {
        'children': [],
        'createTime': 1548945599000,
        'createUser': '',
        'imageUrl': 'xx',
        'isShow': '1',
        'menuHref': '/business/user/unit-list',
        'menuId': '4-2',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 2,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '04',
        'updateTime': 1550748936000,
        'updateUser': ''
      }, {
        'children': [],
        'createTime': 1548945652000,
        'createUser': '',
        'isShow': '1',
        'menuHref': '/business/user/role-list',
        'menuId': '4-3',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 3,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '04',
        'updateTime': 1550748936000,
        'updateUser': ''
      }, {
        'children': [],
        'createTime': 1548945697000,
        'createUser': '',
        'isShow': '1',
        'menuHref': '/business/user/online-list',
        'menuId': '4-4',
        'menuLevel': 2,
        'menuName': '??????????????????',
        'menuSort': 4,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '04',
        'updateTime': 1550748936000,
        'updateUser': ''
      }],
      'createTime': 1548945494000,
      'createUser': '?????????',
      'imageUrl': 'fiLink-user',
      'isShow': '1',
      'menuHref': 'xx',
      'menuId': '04',
      'menuLevel': 1,
      'menuName': '????????????',
      'menuSort': 4,
      'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
      'updateTime': 1552374229000,
      'updateUser': ''
    }, {
      'children': [{
        'children': [],
        'createTime': 1548153591000,
        'createUser': '?????????',
        'imageUrl': 'xx',
        'isShow': '1',
        'menuHref': '/business/alarm/current-alarm',
        'menuId': '5-1',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 1,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '05',
        'updateTime': 1550748936000,
        'updateUser': ''
      }, {
        'children': [],
        'createTime': 1548153630000,
        'createUser': '?????????',
        'imageUrl': 'xx',
        'isShow': '1',
        'menuHref': '/business/alarm/history-alarm',
        'menuId': '5-2',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 2,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '05',
        'updateTime': 1550748937000,
        'updateUser': ''
      }, {
        'children': [{
          'children': [],
          'createTime': 1548945328000,
          'createUser': '?????????',
          'imageUrl': 'xx',
          'isShow': '1',
          'menuHref': '/business/alarm/current-alarm-set',
          'menuId': '5-3-1',
          'menuLevel': 3,
          'menuName': '??????????????????',
          'menuSort': 1,
          'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
          'parentMenuId': '5-3',
          'updateTime': 1550748937000,
          'updateUser': ''
        }, {
          'children': [],
          'createTime': 1548945407000,
          'createUser': '?????????',
          'imageUrl': 'xx',
          'isShow': '1',
          'menuHref': '/business/alarm/history-alarm-set',
          'menuId': '5-3-2',
          'menuLevel': 3,
          'menuName': '??????????????????',
          'menuSort': 2,
          'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
          'parentMenuId': '5-3',
          'updateTime': 1550748937000,
          'updateUser': ''
        }, {
          'menuId': '5-3-3',
          'isShow': '1',
          'menuName': '????????????',
          'menuHref': '/business/alarm/alarm-filtration',
          'parentMenuId': '5-3',
          'menuLevel': 3,
          'menuSort': 3,
          'imageUrl': 'xx',
          'createUser': '?????????',
          'createTime': '2019-01-31T06:36:47.000+0000',
          'updateUser': '',
          'updateTime': '2019-01-31T06:37:23.000+0000',
          'menuTemplateId': '5c53397f18584546ae1d36645b6b3f85',
          'children': []
        }, {
          'menuId': '5-3-4',
          'isShow': '1',
          'menuName': '??????????????????',
          'menuHref': '/business/alarm/alarm-remote-notification',
          'parentMenuId': '5-4',
          'menuLevel': 3,
          'menuSort': 4,
          'imageUrl': 'xx',
          'createUser': '?????????',
          'createTime': '2019-01-31T06:36:47.000+0000',
          'updateUser': '',
          'updateTime': '2019-01-31T06:37:23.000+0000',
          'menuTemplateId': '5c53397f18584546ae1d36645b6b3f85',
          'children': []
        }, {
          'menuId': '5-3-5',
          'isShow': '1',
          'menuName': '???????????????',
          'menuHref': '/business/alarm/alarm-work-order',
          'parentMenuId': '5-5',
          'menuLevel': 3,
          'menuSort': 5,
          'imageUrl': 'xx',
          'createUser': '?????????',
          'createTime': '2019-01-31T06:36:47.000+0000',
          'updateUser': '',
          'updateTime': '2019-01-31T06:37:23.000+0000',
          'menuTemplateId': '5c53397f18584546ae1d36645b6b3f85',
          'children': []
        }],
        'createTime': 1548153780000,
        'createUser': '?????????',
        'imageUrl': 'xx',
        'isShow': '1',
        'menuHref': 'xx',
        'menuId': '5-3',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 3,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '05',
        'updateTime': 1550748937000,
        'updateUser': ''
      }],
      'createTime': 1548153532000,
      'createUser': '?????????',
      'imageUrl': 'fiLink-view-alarm',
      'isShow': '1',
      'menuHref': 'xx',
      'menuId': '05',
      'menuLevel': 1,
      'menuName': '???????????? ',
      'menuSort': 5,
      'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
      'updateTime': 1552374246000,
      'updateUser': ''
    }, {
      'children': [{
        'children': [],
        'createTime': 1548945812000,
        'createUser': '',
        'isShow': '2',
        'menuHref': '/business/system/menu',
        'menuId': '6-1',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 1,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '06',
        'updateTime': 1551179569000,
        'updateUser': ''
      }, {
        'children': [{
          'children': [],
          'createTime': 1548946037000,
          'createUser': '',
          'imageUrl': 'xx',
          'isShow': '1',
          'menuHref': '/business/system/agreement/facility',
          'menuId': '6-3-1',
          'menuLevel': 3,
          'menuName': '????????????',
          'menuSort': 1,
          'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
          'parentMenuId': '6-3',
          'updateTime': 1550748937000,
          'updateUser': ''
        }],
        'createTime': 1548946001000,
        'createUser': '',
        'imageUrl': 'xx',
        'isShow': '1',
        'menuHref': 'xx',
        'menuId': '6-3',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 3,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '06',
        'updateTime': 1550748937000,
        'updateUser': ''
      }, {
        'children': [{
          'children': [],
          'createTime': 1548945889000,
          'createUser': '',
          'imageUrl': 'xx',
          'isShow': '1',
          'menuHref': '/business/system/log',
          'menuId': '6-2-1',
          'menuLevel': 3,
          'menuName': '????????????',
          'menuSort': 1,
          'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
          'parentMenuId': '6-2',
          'updateTime': 1550748937000,
          'updateUser': ''
        }, {
          'children': [],
          'createTime': 1548945927000,
          'createUser': '',
          'imageUrl': 'xx',
          'isShow': '1',
          'menuHref': '/business/system/log/system',
          'menuId': '6-2-2',
          'menuLevel': 3,
          'menuName': '????????????',
          'menuSort': 2,
          'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
          'parentMenuId': '6-2',
          'updateTime': 1550748937000,
          'updateUser': ''
        }, {
          'children': [],
          'createTime': 1548945955000,
          'createUser': '',
          'imageUrl': 'xx',
          'isShow': '1',
          'menuHref': '/business/system/log/security',
          'menuId': '6-2-3',
          'menuLevel': 3,
          'menuName': '????????????',
          'menuSort': 3,
          'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
          'parentMenuId': '6-2',
          'updateTime': 1550748937000,
          'updateUser': ''
        }],
        'createTime': 1548945849000,
        'createUser': '',
        'imageUrl': 'xx',
        'isShow': '1',
        'menuHref': 'xx',
        'menuId': '6-2',
        'menuLevel': 2,
        'menuName': '????????????',
        'menuSort': 3,
        'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
        'parentMenuId': '06',
        'updateTime': 1550748937000,
        'updateUser': ''
      }],
      'createTime': 1548945771000,
      'createUser': '',
      'imageUrl': 'fiLink-setup',
      'isShow': '2',
      'menuHref': 'xx',
      'menuId': '06',
      'menuLevel': 1,
      'menuName': '????????????',
      'menuSort': 7,
      'menuTemplateId': 'b62ddf3ba2384ab8a536c334ff7cc946',
      'updateTime': 1552374258000,
      'updateUser': ''
    }]; // tslint:disable-line
    this.menuList = menuList || newMenu;
  }

  /**
   * ????????????
   */
  updatePassword() {
    this.isUpdatePassword = !this.isUpdatePassword;
  }

  /**
   * ??????????????????
   * param item
   */
  showThreeMenu(item) {
    if (this.threeMenu) {
      this.threeMenu.isShow = true;
    }
    this.threeMenuInfo = item;
  }

  /**
   * ????????????
   */
  logout() {
    this.confirmModal = this.$nzModalService.confirm({
      nzTitle: this.language.logOutMsg,
      nzOkText: this.language.cancelText,
      nzCancelText: this.language.okText,
      nzOkType: 'danger',
      nzOnCancel: () =>
        new Promise((resolve, reject) => {
          setTimeout(Math.random() > 0.5 ? resolve : reject, 500);
          const userInfo = SessionUtil.getUserInfo();
          const data = {
            userid: userInfo.id,
            token: SessionUtil.getToken()
          };
          this.$userService.logout(data).subscribe((result: Result) => {
            localStorage.clear();
            this.$wsService.close();
            this.$router.navigate(['']).then();
          }, () => {
            localStorage.clear();
            this.$wsService.close();
            this.$router.navigate(['']).then();
          });
        }).catch(() => console.log(''))
    });
  }

  /**
   * ??????????????????
   */
  queryAlarmCount() {
    this.$alarmService.queryEveryAlarmCount(1).subscribe(res => {
      if (res['code'] === 0) {
        // this.redAlarmCount = res['data'];
        // item.style = this.$alarmStoreService.getAlarmColorByLevel(1);
        this.alarmStyle.urgency = {
          style: this.$alarmStoreService.getAlarmColorByLevel(1),
          data: Number(res['data'])
        };
      }
    });
    this.$alarmService.queryEveryAlarmCount(2).subscribe(res => {
      if (res['code'] === 0) {
        // this.orangeAlarmCount = res['data'];
        this.alarmStyle.main = {
          style: this.$alarmStoreService.getAlarmColorByLevel(2),
          data: Number(res['data'])
        };
      }
    });
    this.$alarmService.queryEveryAlarmCount(3).subscribe(res => {
      if (res['code'] === 0) {
        // this.yellowAlarmCount = res['data'];
        this.alarmStyle.secondly = {
          style: this.$alarmStoreService.getAlarmColorByLevel(3),
          data: Number(res['data'])
        };
      }
    });
    this.$alarmService.queryEveryAlarmCount(4).subscribe(res => {
      if (res['code'] === 0) {
        // this.blueAlarmCount = res['data'];
        this.alarmStyle.remind = {
          style: this.$alarmStoreService.getAlarmColorByLevel(4),
          data: Number(res['data'])
        };
      }
    });
  }

  /**
   * ????????????????????????
   */
  queryAlarmLevel() {
    this.$alarmService.queryAlarmLevelList(this.queryCondition).subscribe((res: Result) => {
      const data = res['data'];
      if (res.code === 0 && data && data[0]) {
        this.$alarmStoreService.alarm = data.map(item => {
          item.color = this.alarmColorObj[item.alarmLevelColor];
          return item;
        });
      }
      this.colorInfo = this.$alarmStoreService.getAlarmColorStyleObj();
      // console.log(this.$alarmStoreService.alarm);
    });
  }

  initAlarmColor() {
    AlarmLevelSetConfig.forEach(item => {
      this.alarmColorObj[item.value] = item;
    });
  }

  /**
   * ??????????????????
   */
  showSearchList() {
    if (this.indexSearchList.length > 0) {
      if (!this.overlayRef.hasAttached()) {
        this.overlayRef.attach(new TemplatePortal(this.searchTemplate, this.viewContainerRef));
      }
    } else {
      this.overlayRef.detach();
    }
  }

  /**
   * ????????????
   */
  searchChange() {
    if (this.searchValue) {
      this.indexSearchList = this.findUrl(this.searchValue, this.menuList, []);
      this.showSearchList();
    } else {
      this.indexSearchList = [];
      this.overlayRef.detach();
    }
  }

  /**
   * ?????????????????????????????????
   */
  findUrl(name: string, menuList: Array<any>, arr: Array<any>) {
    for (let i = 0; i < menuList.length; i++) {
      if (menuList[i].menuName.indexOf(name) >= 0 && menuList[i].menuHref && menuList[i].menuHref.length > 6) {
        if (arr.length < 5) {
          arr.push(menuList[i]);
        } else {
          break;
        }
      }
      if (menuList[i].children && menuList[i].children.length > 0) {
        this.findUrl(name, menuList[i].children, arr);
      }
    }
    return arr;
  }

  /**
   * ??????????????????
   * param url
   */
  linkTo(url) {
    this.overlayRef.detach();
    this.$router.navigate([url]).then();
  }

  /**
   * ????????????????????????
   */
  createOverlayRef() {
    const strategy = this.overlay
      .position()
      .flexibleConnectedTo(this.searchInput).withPositions([{originX: 'start', originY: 'bottom', overlayX: 'start', overlayY: 'top'}]);
    this.overlayRef = this.overlay.create({
      hasBackdrop: true,
      positionStrategy: strategy
    });
    // ????????????????????????????????????
    this.overlayRef.backdropClick().subscribe(() => {
      this.overlayRef.detach();
    });
  }

  /**
   * ?????????????????????
   */
  initDisplaySettings() {
    if (localStorage.getItem('displaySettings') !== 'undefined') {
      this.displaySettings = JSON.parse(localStorage.getItem('displaySettings'));
    }
    if (this.displaySettings && this.displaySettings.systemLogo === 'local') {
      delete this.displaySettings.systemLogo;
    }
  }

  /**
   * ????????????????????????
   */
  queryPasswordSecurity() {
    this.$securityPolicyService.queryPasswordSecurity().subscribe((res: Result) => {
      this.passwordCheckObj = res.data;
    });
  }

  /**
   * ??????????????? ???????????????
   */
  ngOnDestroy() {
    clearInterval(this.loginTimer);
    // ????????????????????????
    this.$nzModalService.closeAll();
    this.loginTimer = null;
    localStorage.removeItem('userName');
  }
}
