import {FormControl} from '@angular/forms';
import {Observable} from 'rxjs';

export class AsyncRuleUtil {

  /**
   * 必须为整数
   * param msg
   */
  static mustInt(msg?: string) {
    return {
      asyncRule: (control: FormControl) => {
        return Observable.create(observer => {
          if (/^\d+$/.test(control.value)) {
            observer.next(null);
            observer.complete();
          } else {
            observer.next({error: true, duplicated: true});
            observer.complete();
          }
        });
      },
      asyncCode: 'duplicated', msg: msg || '必须为整数！'
    };
  }

  // 名称效验
  static nameReg(msg?: string) {
    return {
      asyncRule: (control: FormControl) => {
        return Observable.create(observer => {
          if (/^(?!_)[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(control.value)) {
            observer.next(null);
            observer.complete();
          } else {
            observer.next({error: true, duplicated: true});
            observer.complete();
          }
        });
      },
      asyncCode: 'duplicated', msg: msg || '输入格式不正确'
    };
  }

  // IPV4效验
  static IPV4Reg(msg?: string) {
    return {
      asyncRule: (control: FormControl) => {
        return Observable.create(observer => {
          if (/^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/.test(control.value)) {
            observer.next(null);
            observer.complete();
          } else {
            observer.next({error: true, duplicated: true});
            observer.complete();
          }
        });
      },
      asyncCode: 'duplicated', msg: msg || '请输入正确的IP地址'
    };
  }
}
