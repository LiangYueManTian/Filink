/**
 * Created by wh1709040 on 2018/9/17.
 * 翻页实体
 */
export class PageBean {
  constructor(pageSize?: number, pageIndex?: number, Total?: number) {
    this._pageSize = pageSize || 20;
    this._pageIndex = pageIndex || 1;
    this._Total = Total || 0;
  }

  private _pageSize: number;

  get pageSize(): number {
    return this._pageSize;
  }

  set pageSize(value: number) {
    this._pageSize = value;
  }

  private _pageIndex: number;

  get pageIndex(): number {
    return this._pageIndex;
  }

  set pageIndex(value: number) {
    if (value > this.lastIndex) {
      this._pageIndex = this.lastIndex || 1;
    } else if (value < 1) {
      this._pageIndex = 1;
    } else {
      this._pageIndex = value;
    }
  }

  private _Total: number;

  get Total(): number {
    return this._Total;
  }

  set Total(value: number) {
    this._Total = value;
    if (this._pageIndex > this.lastIndex) {
      this.pageIndex = this.lastIndex;
    }
  }

  get lastIndex(): number {
    return Math.ceil(this._Total / this._pageSize);
  }
}
