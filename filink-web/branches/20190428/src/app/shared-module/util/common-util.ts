import {en_US, zh_CN} from 'ng-zorro-antd';
import _zh_CN from '../../../assets/i18n/zh_CN';
import _en_US from '../../../assets/i18n/en_US';
import {FACILITY_STATUS_NAME, FACILITY_TYPE_NAME} from '../const/facility';
import {TimeType} from '../entity/dateFormatString';

/**
 * Created by xiaoconghu on 2018/11/20.
 */
export class CommonUtil {
  /**
   * 获取uuid
   * param {number} len
   * returns {string}
   */
  public static getUUid(len: number = 36) {
    const uuid = [];
    const str = '0123456789abcdef';
    for (let i = 0; i < len; i++) {
      uuid[i] = str.substr(Math.floor(Math.random() * 0x10), 1);
    }
    if (len === 36) {
      uuid[14] = '4';
      uuid[19] = str.substr((uuid[19] & 0x3 | 0x8), 1);
      uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
    }
    return uuid.join('').replace(/-/g, '');
  }

  /**
   * 切换语言包
   * param localId
   * returns {any}
   */
  public static toggleNZi18n(localId?) {
    localId = localId || window.localStorage.getItem('localId') || 'zh_CN';
    let language;
    switch (localId) {
      case 'zh_CN':
        language = Object.assign(zh_CN, _zh_CN);
        break;
      case 'en_US':
        language = Object.assign(en_US, _en_US);
        break;
      default:
        language = Object.assign(zh_CN, _zh_CN);
        break;
    }
    return language;
  }

  /**
   * 时间格式化
   * param fmt
   * param date
   * returns {any}
   */
  public static dateFmt(fmt, date) {
    const o = {
      'M+': date.getMonth() + 1,                 // 月份
      'd+': date.getDate(),                    // 日
      'h+': date.getHours(),                   // 小时
      'm+': date.getMinutes(),                 // 分
      's+': date.getSeconds(),                 // 秒
      'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
      'S': date.getMilliseconds()             // 毫秒
    };
    if (/(y+)/.test(fmt)) {
      fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length));
    }
    for (const k in o) {
      if (new RegExp('(' + k + ')').test(fmt)) {

        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length)));
      }
    }
    return fmt;
  }

  /**
   * 时间转时间戳
   * param date
   * returns {any}
   */
  public static getTimeStamp(date) {
    if (date instanceof Date) {
      return date.getTime();
    } else {
      return null;
    }
  }

  /**
   * 深拷贝
   * param obj   拷贝对象
   * returns {any[] | {}}   返回拷贝对象
   */
  public static deepClone(obj): any {
    const objClone = Array.isArray(obj) ? [] : {};
    if (obj && typeof obj === 'object') {
      for (const key in obj) {
        // 判断ojb子元素是否为对象，如果是，递归复制
        if (obj[key] && typeof obj[key] === 'object') {
          objClone[key] = this.deepClone(obj[key]);
        } else {
          // 如果不是，简单复制
          objClone[key] = obj[key];
        }
      }
    }
    return objClone;
  }

  /**
   * 获取设施图标地址
   * param {string} size   设施图标大小  如：16-20
   * param {string} type   设施类型  如：16-20
   * param {string} status 设施状态  如：1   不传为选中状态的图标
   * returns {string}
   */
  public static getFacilityIconUrl(size: string, type: string, status: string = '0') {
    const iconName = `icon_${FACILITY_TYPE_NAME[type]}_${FACILITY_STATUS_NAME[status]}.png`;
    return `assets/facility-icon/${size}/${iconName}`;
  }

  /**
   * 获取设施图标样式
   * param {string} type
   * returns {string}
   */
  public static getFacilityIconClassName(type: string) {
    // const iconClass = `icon-${facilityTypeName[type]}`;
    const facilityType = FACILITY_TYPE_NAME[type] || 'all-facility';
    const iconClass = `iconfont facility-icon fiLink-${facilityType} ${facilityType}-color`;
    return iconClass;
  }

  /**
   * 判断用户是否有页面访问权限
   * param url
   */
  public static hasRole(roleList: Array<any>, url: string): boolean {
    if (roleList.length > 0) {
      const list = roleList.filter(item => url.startsWith(item));
      return list.length > 0;
    } else {
      return false;
    }
  }

  /**
   * 根据url查询指定的菜单信息
   * param menuList
   * param url
   */
  public static findMenuInfo(menuList, url) {
    for (let i = 0; i < menuList.length; i++) {
      if (menuList[i].menuHref === url) {
        return menuList[i];
      } else if (menuList[i].children && menuList[i].children.length > 0) {
        this.findMenuInfo(menuList[i].children, url);
      }
    }
  }

  /**
   * 比较ip大小
   * param ipBegin
   * param ipEnd
   */
  public static compareIP(ipBegin: string, ipEnd: string) {
    let temp1;
    let temp2;
    if (ipBegin.includes('.')) {
      temp1 = ipBegin.split('.');
      temp2 = ipEnd.split('.');
      for (let i = 0; i < temp2.length; i++) {
        if (parseInt(temp2[i], 0) > parseInt(temp1[i], 0)) {
          return true;
        } else if (parseInt(temp2[i], 0) < parseInt(temp1[i], 0)) {
          return false;
        }
      }
      return true;
    } else {
      temp1 = ipBegin.split(':');
      temp2 = ipEnd.split(':');
      for (let i = 0; i < temp2.length; i++) {
        if (temp2[i].padStart(4, '0') > temp1[i].padStart(4, '0')) {
          return true;
        } else if (temp2[i].padStart(4, '0') < temp1[i].padStart(4, '0')) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   * 返回几天前日期
   * param day
   */
  public static funDate(day: number) {
    const date1 = new Date();
    const time1 = date1.getFullYear() + '/' + (date1.getMonth() + 1) + '/' + date1.getDate();
    const date2 = new Date(time1);
    date2.setDate(date1.getDate() + day);
    return date2.getFullYear() + '/' + (date2.getMonth() + 1) + '/' + date2.getDate();
  }

  /**
   * web端导出html文件公共方法
   * param name 导出的文件名
   */
  public static exportHtml(name) {
    // 取html内容
    const test = document.getElementsByTagName('html')[0].outerHTML;
    const urlObject = window.URL || window['webkitURL'] || window;
    const export_blob = new Blob([test]);
    // 动态创建a标签
    const a = document.createElement('a');
    document.body.appendChild(a);
    // url地址为命名空间http://www.w3.org/1999/xhtml 或 http://www.w3.org/2000/
    const save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a');
    save_link['href'] = urlObject.createObjectURL(export_blob);
    save_link['download'] = name;
    // 创建事件对象
    const ev = document.createEvent('MouseEvents');
    ev.initMouseEvent(
      'click',
      true, false, window, 0, 0, 0, 0, 0,
      false,
      false,
      false,
      false,
      0,
      null
    );
    save_link.dispatchEvent(ev);
    document.body.removeChild(a);
  }

  /**
   *
   * param alarmBeginTime  传递过来的首次发生时间
   *       alarmCleanTime 告警清除时间
   * param language 对象 里面有 月 天 时
   */
  // 转化告警持续时间 (当前告警 历史告警使用)
  public static setAlarmContinousTime(alarmBeginTime: number, alarmCleanTime: number, language) {
    // 当有清除时间时 使用清除时间减去 首次发生时间
    let alarmContinousTimeHour: number;
    if ( alarmCleanTime ) {
      alarmContinousTimeHour = alarmCleanTime - alarmBeginTime;
    } else {
      alarmContinousTimeHour =  (+new Date()) - alarmBeginTime;
    }

    if ( alarmContinousTimeHour < 0 ) { return ''; }
    const time = Math.ceil(alarmContinousTimeHour / 1000 / 60 / 60);
    let name: string;
    if ( time === 0 ) {
      name = '1' + language.hour;
    } else if (time >= 8760) {
      // 年
      name = Math.floor(time / 8760) + language.year + Math.ceil(time % 8760 / 720) + language.month;
    } else if (time >= 720) {
      // 月
      name = Math.floor(time / 720) + language.month + Math.ceil(time % 720 / 24) + language.day;
    } else if (24 <= time) {
      // 天
      name = Math.floor(time / 24) + language.day + Math.ceil(time % 24) + language.hour;
    } else {
      // 时
      name = Math.ceil(time) + language.hour;
    }
    return name;
  }

  /**
   * 获取当前地区时区
   */
  public static getTimeone() {
    // 时区默认为本地时区
    let displaySettings = {
      timeType: 'local'
    };
    if (localStorage.getItem('displaySettings') && localStorage.getItem('displaySettings') !== 'undefined') {
      displaySettings = JSON.parse(localStorage.getItem('displaySettings'));
      if (displaySettings.timeType === TimeType.UTC) {
        return 'GMT+00:00';
      } else {
        const times = new Date();
        const arr = (times.toString()).split(' ');
        return arr[5];
      }
    } else {
      return 'GMT+00:00';
    }
  }

  /**
   * 获取秒数  （毫秒数转秒数）
   * param timestamps
   * returns {number}
   */
  public static getSeconds(timestamps) {
    const num = Number(timestamps) / 1000;
    return Math.round(num);
  }


    /**
     * 去前后空格
     */
  public static trim(str) {
    let str2;
    if (str === null) {
        str = '';
    } else {
        str2 = str.toString().replace(/(^\s*)|(\s*$)/g, '');
    }
    return str2;
  }
}
