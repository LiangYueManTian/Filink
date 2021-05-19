export class SessionUtil {
  /**
   * 获取用户信息
   */
  static getUserInfo() {
    if (sessionStorage.getItem('userInfo')) {
      return JSON.parse(sessionStorage.getItem('userInfo'));
    } else {
      return {};
    }
  }

  /**
   * 获取用户ID
   */
  static getUserId() {
    const userInfo = this.getUserInfo();
    return userInfo.id;
  }
}
