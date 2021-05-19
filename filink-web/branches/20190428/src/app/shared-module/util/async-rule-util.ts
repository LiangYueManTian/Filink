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
    const ipv6 = /^([\da-fA-F]{1,4}:){7}[\da-fA-F]{1,4}$/;
    const ipv4 = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/;
    return {
      asyncRule: (control: FormControl) => {
        return Observable.create(observer => {
          if (ipv4.test(control.value) || ipv6.test(control.value)) {
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

  static HttpIPV4Reg(msg?: string) {
    const ipv4 = /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/;
    return {
      asyncRule: (control: FormControl) => {
        return Observable.create(observer => {
          if (ipv4.test(control.value)) {
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

  static HttpReg(msg?: string) {
    const http = /^(http:\/\/).+$/;
    return {
      asyncRule: (control: FormControl) => {
        return Observable.create(observer => {
          if (http.test(control.value)) {
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

  static HttpsReg(msg?: string) {
    const https = /^(https:\/\/).+$/;
    return {
      asyncRule: (control: FormControl) => {
        return Observable.create(observer => {
          if (https.test(control.value)) {
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

  // 验证密码
  static passWordReg(msg?: string) {
    return {
      asyncRule: (control: FormControl) => {
        return Observable.create(observer => {
          if (/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[$@$!%*?&.])[A-Za-z\d$@$!%*?&.]{6, 255}/.test(control.value)) {
            observer.next(null);
            observer.complete();
          } else {
            observer.next({error: true, duplicated: true});
            observer.complete();
          }
        });
      },
      asyncCode: 'duplicated', msg: msg || '请输入正确的密码格式'
    };
  }

  // 邮箱秘钥校验
  static mailReg(msg?: string) {
    return {
      asyncRule: (control: FormControl) => {
        return Observable.create(observer => {
          if (/^([a-zA-Z0-9_]){1,32}$/.test(control.value)) {
            observer.next(null);
            observer.complete();
          } else {
            observer.next({error: true, duplicated: true});
            observer.complete();
          }
        });
      },
      asyncCode: 'duplicated', msg: msg || '请输入正确格式'
    };
  }

  static emailRegExp(msg?: string) {
    return {
      asyncRule: (control: FormControl) => {
        return Observable.create(observer => {
          if (/^[a-z0-9A-Z]+([._\\-]*[a-z0-9A-Z])*@([a-z0-9A-Z]+[-a-z0-9A-Z]*[a-z0-9A-Z]+.){1,63}[a-z0-9A-Z]+$/.test(control.value)) {
            observer.next(null);
            observer.complete();
          } else {
            observer.next({error: true, duplicated: true});
            observer.complete();
          }
        });
      },
      asyncCode: 'duplicated', msg: msg || '请输入正确的邮箱地址'
    };
  }

  ////////////////////////////////////

  /**
   * 获取数字检验规则
   */
  static getNumberRule(code) {
    if (code === '1') {
      return {
        asyncRule: (control: FormControl) => {
          return Observable.create(observer => {
            if (/^.*(?=.*\d)/.test(control.value)) {
              observer.next(null);
              observer.complete();
            } else {
              observer.next({error: true, duplicated: true});
              observer.complete();
            }
          });
        },
        asyncCode: 'duplicated', msg: '最少包含1个数字！'
      };
    } else if (code === '0') {
      return null;
    }
  }


  /**
   * 获取小写字母检验规则
   */
  static getLowerRule(code) {
    if (code === '1') {
      return {
        asyncRule: (control: FormControl) => {
          return Observable.create(observer => {
            if (/^.*(?=.*[a-z])/.test(control.value)) {
              observer.next(null);
              observer.complete();
            } else {
              observer.next({error: true, duplicated: true});
              observer.complete();
            }
          });
        },
        asyncCode: 'duplicated', msg: '最少包含1个小写字母！'
      };
    } else if (code === '0') {
      return null;
    }
  }


  /**
   * 获取大写字母检验规则
   */
  static getUpperRule(code) {
    if (code === '1') {
      return {
        asyncRule: (control: FormControl) => {
          return Observable.create(observer => {
            if (/^.*(?=.*[A-Z])/.test(control.value)) {
              observer.next(null);
              observer.complete();
            } else {
              observer.next({error: true, duplicated: true});
              observer.complete();
            }
          });
        },
        asyncCode: 'duplicated', msg: '最少包含1个大写字母！'
      };
    } else if (code === '0') {
      return null;
    }
  }

  /**
   * 获取特殊字符检验规则
   */

  static getSpecialCharacterRule(code) {
    if (code === '1') {
      return {
        asyncRule: (control: FormControl) => {
          return Observable.create(observer => {
            if (/^.*(?=.*[!@#$%.\-_&*<>?\(\)\[\]{}\\|;:])/.test(control.value)) {
              observer.next(null);
              observer.complete();
            } else {
              observer.next({error: true, duplicated: true});
              observer.complete();
            }
          });
        },
        asyncCode: 'duplicated', msg: '最少包含1个特殊字符！'
      };
    } else if (code === '0') {
      return null;
    }
  }
}

