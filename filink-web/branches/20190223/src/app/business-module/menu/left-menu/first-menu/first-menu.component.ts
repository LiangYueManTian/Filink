import {AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {fadeIn} from '../../../../shared-module/animations/fadeIn';

@Component({
  selector: 'app-first-menu',
  templateUrl: './first-menu.component.html',
  styleUrls: ['./first-menu.component.scss'],
  animations: [
    fadeIn
  ]
})
export class FirstMenuComponent implements OnInit, AfterViewInit {

  @Input() menuList = [];
  @Input() isCollapsed: boolean;

  // 推送取消事件
  @Output() changeThreeMenu = new EventEmitter();
  // 当前点击菜单
  menuName = '';
  // 一级菜单名称
  firstMenuName = '';

  menuId = null;
  // 延时处理
  timer = null;
  constructor(private $router: Router) { }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    let menuList = [];
    if (sessionStorage.getItem('menuList') !== 'undefined') {
      menuList = JSON.parse(sessionStorage.getItem('menuList'));
    }
    const newMenu = [{'children':[],'createTime':1548947982000,'createUser':'','imageUrl':'fiLink-index','isShow':'2','menuHref':'/business/index','menuId':'01','menuLevel':1,'menuName':'首页','menuSort':0,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','updateTime':1552374198000,'updateUser':''},{'children':[{'children':[],'createTime':1548152892000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/facility/facility-list','menuId':'02-1','menuLevel':2,'menuName':'设施列表','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'02','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548947908000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/facility/facility-log','menuId':'02-2','menuLevel':2,'menuName':'设施日志','menuSort':5,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'02','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548153102000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/facility/area-list','menuId':'02-6','menuLevel':2,'menuName':'区域管理','menuSort':6,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'02','updateTime':1550748936000,'updateUser':''}],'createTime':1548152753000,'createUser':'朱琦琦','imageUrl':'fiLink-facilities','isShow':'1','menuHref':'xx','menuId':'02','menuLevel':1,'menuName':'设施管理','menuSort':2,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','updateTime':1552374212000,'updateUser':''},{'children':[{'children':[],'createTime':1548945563000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/user/user-list','menuId':'4-1','menuLevel':2,'menuName':'用户管理','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'04','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548945599000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/user/unit-list','menuId':'4-2','menuLevel':2,'menuName':'单位列表','menuSort':2,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'04','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548945652000,'createUser':'','isShow':'1','menuHref':'/business/user/role-list','menuId':'4-3','menuLevel':2,'menuName':'角色管理','menuSort':3,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'04','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548945697000,'createUser':'','isShow':'1','menuHref':'/business/user/online-list','menuId':'4-4','menuLevel':2,'menuName':'在线用户列表','menuSort':4,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'04','updateTime':1550748936000,'updateUser':''}],'createTime':1548945494000,'createUser':'朱琦琦','imageUrl':'fiLink-user','isShow':'1','menuHref':'xx','menuId':'04','menuLevel':1,'menuName':'用户管理','menuSort':4,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','updateTime':1552374229000,'updateUser':''},{'children':[{'children':[],'createTime':1548153591000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/alarm/current-alarm','menuId':'5-1','menuLevel':2,'menuName':'当前告警','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'05','updateTime':1550748936000,'updateUser':''},{'children':[],'createTime':1548153630000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/alarm/history-alarm','menuId':'5-2','menuLevel':2,'menuName':'历史告警','menuSort':2,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'05','updateTime':1550748937000,'updateUser':''},{'children':[{'children':[],'createTime':1548945328000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/alarm/current-alarm-set','menuId':'5-3-1','menuLevel':3,'menuName':'当前告警设置','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'5-3','updateTime':1550748937000,'updateUser':''},{'children':[],'createTime':1548945407000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'/business/alarm/history-alarm-set','menuId':'5-3-2','menuLevel':3,'menuName':'历史告警设置','menuSort':2,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'5-3','updateTime':1550748937000,'updateUser':''},{'menuId': '5-3-3','isShow': '1','menuName': '告警过滤','menuHref': '/business/alarm/alarm-filtration','parentMenuId': '5-3','menuLevel': 3,'menuSort': 3,'imageUrl': 'xx','createUser': '朱琦琦','createTime': '2019-01-31T06:36:47.000+0000','updateUser': '','updateTime': '2019-01-31T06:37:23.000+0000','menuTemplateId': '5c53397f18584546ae1d36645b6b3f85','children': []}],'createTime':1548153780000,'createUser':'朱琦琦','imageUrl':'xx','isShow':'1','menuHref':'xx','menuId':'5-3','menuLevel':2,'menuName':'告警设置','menuSort':3,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'05','updateTime':1550748937000,'updateUser':''}],'createTime':1548153532000,'createUser':'朱琦琦','imageUrl':'fiLink-view-alarm','isShow':'1','menuHref':'xx','menuId':'05','menuLevel':1,'menuName':'告警管理 ','menuSort':5,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','updateTime':1552374246000,'updateUser':''},{'children':[{'children':[],'createTime':1548945812000,'createUser':'','isShow':'2','menuHref':'/business/system/menu','menuId':'6-1','menuLevel':2,'menuName':'菜单管理','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'06','updateTime':1551179569000,'updateUser':''},{'children':[{'children':[],'createTime':1548946037000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/system/agreement/facility','menuId':'6-3-1','menuLevel':3,'menuName':'设施协议','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'6-3','updateTime':1550748937000,'updateUser':''}],'createTime':1548946001000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'xx','menuId':'6-3','menuLevel':2,'menuName':'协议管理','menuSort':3,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'06','updateTime':1550748937000,'updateUser':''},{'children':[{'children':[],'createTime':1548945889000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/system/log','menuId':'6-2-1','menuLevel':3,'menuName':'操作日志','menuSort':1,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'6-2','updateTime':1550748937000,'updateUser':''},{'children':[],'createTime':1548945927000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/system/log/system','menuId':'6-2-2','menuLevel':3,'menuName':'系统日志','menuSort':2,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'6-2','updateTime':1550748937000,'updateUser':''},{'children':[],'createTime':1548945955000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'/business/system/log/security','menuId':'6-2-3','menuLevel':3,'menuName':'安全日志','menuSort':3,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'6-2','updateTime':1550748937000,'updateUser':''}],'createTime':1548945849000,'createUser':'','imageUrl':'xx','isShow':'1','menuHref':'xx','menuId':'6-2','menuLevel':2,'menuName':'日志管理','menuSort':3,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','parentMenuId':'06','updateTime':1550748937000,'updateUser':''}],'createTime':1548945771000,'createUser':'','imageUrl':'fiLink-setup','isShow':'2','menuHref':'xx','menuId':'06','menuLevel':1,'menuName':'系统设置','menuSort':7,'menuTemplateId':'b62ddf3ba2384ab8a536c334ff7cc946','updateTime':1552374258000,'updateUser':''}]; // tslint:disable-line
    this.menuList = menuList || newMenu;
    this.dealHighlight(this.menuList, `/business${window.location.href.split('business')[1]}`);
    this.$router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.dealHighlight(this.menuList, event.url);
      }
    });
  }

  /**
   * 路由跳转
   * param item
   */
  itemClick(item, menuName) {
    this.menuName = item.menuName;
    if (item.children && item.children.length > 0) {
      this.firstMenuName = menuName;
      // 默认跳转三级菜单
      this.$router.navigate([item.children[0].menuHref]).then();
      this.changeThreeMenu.emit(item);
    } else {
      this.changeThreeMenu.emit({});
      // 判断是不是一级菜单  主要用于菜单收起是背景颜色控制
      if (!item.parentMenuId) {
        this.firstMenuName = item.menuName;
      } else {
        this.firstMenuName = menuName;
      }
      this.$router.navigate([item.menuHref]).then();
    }
  }

  /**
   * 展开事件
   * param item
   */
  expandItem(item) {
    clearTimeout(this.timer);
    this.timer = setTimeout(() => {
      if (this.menuId !== item.menuId) {
        this.menuId = item.menuId;
        const nodes = JSON.parse(JSON.stringify(this.menuList));
        for (let i = 0 ; i < nodes.length; i++) {
          if (nodes[i].menuId === item.menuId) {
            nodes[i].expand = true;
          } else {
            nodes[i].expand = false;
          }
        }
        this.menuList = nodes;
      }
    }, 300);

  }

  /**
   * 根据菜单处理高亮
   * param url
   */
  dealHighlight(menuList, url) {
    for (let i = 0; i < menuList.length; i++) {
      if (menuList[i].menuHref === url) {
        if (menuList[i].menuLevel === 1) {
          this.firstMenuName = menuList[i].menuName;
          this.menuName = menuList[i].menuName;
        } else if (menuList[i].menuLevel === 2) {
          this.firstMenuName = (this.findMenuInfoByID(this.menuList, menuList[i].parentMenuId)).menuName;
          this.menuName = menuList[i].menuName;
        } else {
          const menu = this.findMenuInfoByID(this.menuList, menuList[i].parentMenuId);
          this.firstMenuName = (this.findMenuInfoByID(this.menuList, menu.parentMenuId)).menuName;
          this.menuName = menu.menuName;
        }
      } else if (menuList[i].children && menuList[i].children.length > 0) {
        this.dealHighlight(menuList[i].children, url);
      }
    }
  }

  /**
   * 根据id查找菜单
   * param menuList
   * param id
   */
  findMenuInfoByID(menuList, id) {
    let menuInfo = null;
    for (let i = 0; i < menuList.length; i++) {
      if (menuList[i].menuId === id) {
        menuInfo = menuList[i];
        break;
      } else if (menuList[i].children && menuList[i].children.length > 0) {
        if (!menuInfo) {
          menuInfo = this.findMenuInfoByID(menuList[i].children, id);
        }
      }
    }
    return menuInfo;
  }
}
