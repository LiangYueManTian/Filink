import {Component, EventEmitter, Input, OnChanges, OnInit, SimpleChanges, TemplateRef, ViewChild} from '@angular/core';
import {NzI18nInterface, NzI18nService, zh_TW} from 'ng-zorro-antd';

@Component({
  selector: 'xc-pagination',
  templateUrl: './pagination.component.html',
  styleUrls: ['./pagination.component.scss']
})
export class PaginationComponent implements OnInit, OnChanges {
  _xcTotal: number;
  _xcPageIndex: number;
  _xcPageSize: number;
  pages: number[] = [];
  locale = {items_per_page: '条/页'};
  xcPageSizeOptions = [10, 20, 30, 40, 50];
  firstIndex = 1;

  @Input()
  xcShowSizeChanger = false;

  get xcTotal(): number {
    return this._xcTotal;
  }

  @Input()
  set xcTotal(value: number) {
    this._xcTotal = value;
  }

  get xcPageIndex(): number {
    return this._xcPageIndex;
  }

  @Input()
  set xcPageIndex(value: number) {
    if (value < 0) {
      value = 1;
    }
    if (value > this.lastIndex) {
      value = this.lastIndex;
    }
    this._xcPageIndex = value;
    this.bingPageRange();

  }

  get xcPageSize(): number {
    return this._xcPageSize;
  }

  @Input()
  set xcPageSize(value: number) {
    if (this.xcPageIndex > this.lastIndex) {
      this.xcPageIndex = this.lastIndex;
      this.xcPageChange.emit({pageSize: this.xcPageSize, pageIndex: this.xcPageIndex});
    }
    this._xcPageSize = value;
    this.bingPageRange();

  }


  xcPageChange = new EventEmitter();
  @ViewChild('renderItemTemplate') private _xcItemRender: TemplateRef<{ $implicit: 'page' | 'prev' | 'next', page: number }>;
  @Input()
  get xcItemRender() {
    return this._xcItemRender;
  }

  set xcItemRender(value) {
    this._xcItemRender = value;
  }

  get lastIndex(): number {
    return Math.ceil(this.xcTotal / this.xcPageSize);
  }

  constructor(private i18n: NzI18nService ) {
  }

  ngOnInit() {
  }

  bingPageRange() {
    const temPages = [];
    if (this.lastIndex < 7) {
      for (let i = 2; i < this.lastIndex; i++) {
        temPages.push(i);
      }
    } else {
      const value = this.xcPageIndex;
      let left = Math.max(2, value - 2);
      let right = Math.min(value + 2, this.lastIndex - 1);
      if (value - 1 <= 2) {
        right = 5;
      }

      if (this.lastIndex - value <= 2) {
        left = this.lastIndex - 4;
      }

      for (let i = left; i <= right; i++) {
        temPages.push(i);
      }
    }
    this.pages = temPages;
  }

  previous() {
    this.jump(this.xcPageIndex - 1);
  }

  next() {
    this.jump(this.xcPageIndex + 1);
  }

  jump(currentIndex) {
    this.xcPageIndex = currentIndex;
    this.xcPageChange.emit({pageSize: this.xcPageSize, pageIndex: this.xcPageIndex});
  }

  onPageSizeChange(evt) {
    this.xcPageSize = evt;
    if (this.xcPageIndex > this.lastIndex) {
      this.xcPageIndex = this.lastIndex;
    }
    this.xcPageChange.emit({pageSize: this.xcPageSize, pageIndex: this.xcPageIndex});
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.xcPageSize && changes.xcPageIndex) {
      this.bingPageRange();
    }
  }
}
