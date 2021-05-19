import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { NzNotificationService, NzI18nService } from 'ng-zorro-antd';
import { Router } from '@angular/router';
import { IndexMissionService } from '../core-module/mission/index.mission.service';
import { AlarmService } from '../core-module/api-service/alarm';
import { ThreeMenuComponent } from './menu/left-menu/three-menu/three-menu.component';
import { NativeWebsocketImplService } from '../core-module/websocket/native-websocket-impl.service';
import { UserService } from '../core-module/api-service/user/user-manage';
import { Result } from '../shared-module/entity/result';
import { AlarmStoreService } from '../core-module/store/alarm.store.service';
import { AlarmLevelSetConfig } from './alarm/alarm-manage/alarm-set/current-alarm-set/alarm-level-set/config';
import { QueryCondition } from '../shared-module/entity/queryCondition';
import { CurrAlarmServiceService } from './alarm/alarm-manage/current-alarm/curr-alarm-service.service';
import { FiLinkModalService } from '../shared-module/service/filink-modal/filink-modal.service';
import { CommonLanguageInterface } from 'src/assets/i18n/common/common.language.interface';


@Component({
  selector: 'app-business',
  templateUrl: './business.component.html',
  styleUrls: ['./business.component.scss']
})
export class BusinessComponent implements OnInit, OnDestroy {
  isCollapsed = true;
  // 用户信息
  userInfo = { userNickname: '', id: '' };
  // 主页面菜单配置
  menuList = [];
  // 三级菜单路由配置
  threeMenuInfo = { menuName: '' };
  // 修改密码弹出框控制
  isUpdatePassword = false;
  redAlarmCount: number = 0;     // 红色告警数量
  orangeAlarmCount: number = 0;  // 橙色告警数量
  yellowAlarmCount: number = 0;  // 黄色告警数量
  blueAlarmCount: number = 0;    // 蓝色告警数量
  alarmArray: Array<any> = [];
  queryCondition: QueryCondition = new QueryCondition();
  alarmColorObj = {};
  colorInfo = {};
  language: CommonLanguageInterface;
  @ViewChild('threeMenu') private threeMenu: ThreeMenuComponent;
  constructor(private $router: Router,
    public $alarmStoreService: AlarmStoreService,
    public $currentAalarmService: CurrAlarmServiceService,
    private $mission: IndexMissionService,
    private $alarmService: AlarmService,
    private $userService: UserService,
    private el: ElementRef,
    private $nzNotificationService: NzNotificationService,
    private $wsService: NativeWebsocketImplService,
    private $message: FiLinkModalService,
    private $i18n: NzI18nService
  ) {
    this.$currentAalarmService.getMessage().subscribe(res => {
      if (res === 2) {
        this.queryAlarmCount();
      }
      if (res === 1) {
        this.queryAlarmLevel();
      }
    });
  }

  ngOnInit() {
    this.$wsService.connect();
    this.initAlarmColor();
    this.language = this.$i18n.getLocaleData('common');
    // 获取菜单配置
    setTimeout(() => {
      let menuList = [];
      if (sessionStorage.getItem('menuList') !== 'undefined') {
        menuList = JSON.parse(sessionStorage.getItem('menuList'));
      }
      const newMenu = [{'children':[],'createTime':1548947982000,'createUser':'','imageUrl':'fiLink-index','isShow':'2','menuHref':'/business/index','menuId':'01','menuLevel':1,'menuName':'首页','menuSort':0,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','updateTime':1552374198000,'updateUser':''},{'children':[{'children':[],'createTime':1548152892000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/facility/facility-list','menuId':'02-1','menuLevel':2,'menuName':'设施列表','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'02','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548947908000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/facility/facility-log','menuId':'02-2','menuLevel':2,'menuName':'设施日志','menuSort':5,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'02','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548153102000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/facility/area-list','menuId':'02-6','menuLevel':2,'menuName':'区域管理','menuSort':6,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'02','updateTime':1550748936000,'updateUser':''}],'createTime':1548152753000,'createUser':'朱琦琦','imageUrl':'fiLink-facilities','isShow':'1','menuHref':'xx','menuId':'02','menuLevel':1,'menuName':'设施管理','menuSort':2,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','updateTime':1552374212000,'updateUser':''},{'children':[{'children':[],'createTime':1548945563000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/user/user-list','menuId':'4-1','menuLevel':2,'menuName':'用户管理','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'04','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548945599000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/user/unit-list','menuId':'4-2','menuLevel':2,'menuName':'单位列表','menuSort':2,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'04','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548945652000,'createUser':'','isShow':'1','menuHref':'/business/user/role-list','menuId':'4-3','menuLevel':2,'menuName':'角色管理','menuSort':3,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'04','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548945697000,'createUser':'','isShow':'1','menuHref':'/business/user/online-list','menuId':'4-4','menuLevel':2,'menuName':'在线用户列表','menuSort':4,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'04','updateTime':1550748936000,'updateUser':''}],'createTime':1548945494000,'createUser':'朱琦琦','imageUrl':'fiLink-user','isShow':'1','menuHref':'xx','menuId':'04','menuLevel':1,'menuName':'用户管理','menuSort':4,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','updateTime':1552374229000,'updateUser':''},{'children':[{'children':[],'createTime':1548153591000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/alarm/current-alarm','menuId':'5-1','menuLevel':2,'menuName':'当前告警','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'05','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548153630000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/alarm/history-alarm','menuId':'5-2','menuLevel':2,'menuName':'历史告警','menuSort':2,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'05','updateTime':1550748937000,'updateUser':''},{'children':[{'children':[],'createTime':1548945328000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/alarm/current-alarm-set','menuId':'5-3-1','menuLevel':3,'menuName':'当前告警设置','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'5-3','updateTime':1550748937000,'updateUser':''},{'children':[],'createTime':1548945407000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/alarm/history-alarm-set','menuId':'5-3-2','menuLevel':3,'menuName':'历史告警设置','menuSort':2,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'5-3','updateTime':1550748937000,'updateUser':''},{'menuId': '5-3-3','isShow': '1','menuName': '告警过滤','menuHref': '/business/alarm/alarm-filtration','parentMenuId': '5-3','menuLevel': 3,'menuSort': 3,'imageUrl': 'xx','createUser': '朱琦琦','createTime': '2019-01-31T06:36:47.000+0000','updateUser': '','updateTime': '2019-01-31T06:37:23.000+0000','menuTemplateId': '5c53397f18584546ae1d36645b6b3f85','children': []}],'createTime':1548153780000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'xx','menuId':'5-3','menuLevel':2,'menuName':'告警设置','menuSort':3,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'05','updateTime':1550748937000,'updateUser':''}],'createTime':1548153532000,'createUser':'朱琦琦','imageUrl':'fiLink-view-alarm','isShow':'1','menuHref':'xx','menuId':'05','menuLevel':1,'menuName':'告警管理 ','menuSort':5,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','updateTime':1552374246000,'updateUser':''},{'children':[{'children':[],'createTime':1548945812000,'createUser':'','isShow':'2','menuHref':'/business/system/menu','menuId':'6-1','menuLevel':2,'menuName':'菜单管理','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'06','updateTime':1551179569000,'updateUser':''},{'children':[{'children':[],'createTime':1548946037000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/system/agreement/facility','menuId':'6-3-1','menuLevel':3,'menuName':'设施协议','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'6-3','updateTime':1550748937000,'updateUser':''}],'createTime':1548946001000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'xx','menuId':'6-3','menuLevel':2,'menuName':'协议管理','menuSort':3,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'06','updateTime':1550748937000,'updateUser':''},{'children':[{'children':[],'createTime':1548945889000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/system/log','menuId':'6-2-1','menuLevel':3,'menuName':'操作日志','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'6-2','updateTime':1550748937000,'updateUser':''},{'children':[],'createTime':1548945927000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/system/log/system','menuId':'6-2-2','menuLevel':3,'menuName':'系统日志','menuSort':2,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'6-2','updateTime':1550748937000,'updateUser':''},{'children':[],'createTime':1548945955000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/system/log/security','menuId':'6-2-3','menuLevel':3,'menuName':'安全日志','menuSort':3,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'6-2','updateTime':1550748937000,'updateUser':''}],'createTime':1548945849000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'xx','menuId':'6-2','menuLevel':2,'menuName':'日志管理','menuSort':3,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'06','updateTime':1550748937000,'updateUser':''}],'createTime':1548945771000,'createUser':'','imageUrl':'fiLink-setup','isShow':'2','menuHref':'xx','menuId':'06','menuLevel':1,'menuName':'系统设置','menuSort':7,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','updateTime':1552374258000,'updateUser':''}]; // tslint:disable-line
      this.menuList = menuList || newMenu;
    }, 0);
    if (sessionStorage.getItem('userInfo')) {
      this.userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
    }
    this.queryAlarmLevel();
    this.queryAlarmCount();
    this.$wsService.subscibeMessage.subscribe(msg => {
      console.log(JSON.parse(msg.data));
      const data = JSON.parse(msg.data);
      const id_token = `${this.userInfo.id}_${sessionStorage.getItem('token')}`;
      if (data && data.channelId === 'forceOff') {  // 在线用户列表强制下线在线用户
        Object.keys(data.msg).forEach(item => {
          if (item === id_token) {
            this.$message.info(this.language.beOffLineMsg);
            sessionStorage.clear();
            this.$router.navigate(['']).then();
            this.$wsService.close();
            this.$currentAalarmService.clearMessage();
          }
        });
      } else if (data && data.channelId === 'deleteUser') { // 用户列表强制删除用户账号
        data.msg.forEach(item => {
          if (item === this.userInfo.id) {
            this.$message.info(this.language.beDeleteMsg);
            sessionStorage.clear();
            this.$router.navigate(['']).then();
            this.$wsService.close();
            this.$currentAalarmService.clearMessage();
          }
        });
      } else {
        this.$nzNotificationService.config({
          nzPlacement: 'bottomRight'
        });
        this.$nzNotificationService.blank('后台消息', JSON.parse(msg.data).msg);
      }

    });
  }

  /**
   * 修改密码
   */
  updatePassword() {
    this.isUpdatePassword = !this.isUpdatePassword;
  }

  /**
   * 显示三级菜单
   * param item
   */
  showThreeMenu(item) {
    if (this.threeMenu) {
      this.threeMenu.isShow = true;
    }
    this.threeMenuInfo = item;
  }

  /**
   * 退出登入
   */
  logout() {
    if (sessionStorage.getItem('userInfo')) {
      const userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
      const data = {
        userid: userInfo.id,
        token: sessionStorage.getItem('token')
      };
      this.$userService.logout(data).subscribe((result: Result) => {
      });
    }
    sessionStorage.clear();
    this.$wsService.close();
    this.$router.navigate(['']).then();
  }


  /**
   * 查询告警数量
   */
  queryAlarmCount() {
    this.$alarmService.queryEveryAlarmCount().subscribe(res => {
      // console.log(res);
      if (res['code'] === 0) {
        this.alarmArray = res['data'];
        if (this.alarmArray.length > 0) {
          for (let i = 0; i < this.alarmArray.length; i++) {
            const arr = this.alarmArray[i];
            if (arr.alarm_fixed_level === '1') {
              this.redAlarmCount = arr.count;
            }
            if (arr.alarm_fixed_level === '2') {
              this.orangeAlarmCount = arr.count;
            }
            if (arr.alarm_fixed_level === '3') {
              this.yellowAlarmCount = arr.count;
            }
            if (arr.alarm_fixed_level === '4') {
              this.blueAlarmCount = arr.count;
            }
          }
        }
      }
    });


  }

  /**
   * 查询所有告警级别
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

  ngOnDestroy() {
  }
}
