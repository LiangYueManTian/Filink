import {FormItem} from '../component/form/form-config';
import {TemplateRef} from '@angular/core';

/**
 * Created by wh1709040 on 2018/9/17.
 */
export class ColumnConfig {
  public type?: string; // 类型
  public title?: string; // 每列的标题
  public key?: string;  // 每列的key
  public searchKey?: string; // 用于查询
  public width?: number; // 每列的宽度
  public minWidth?: number; // 最小宽度
  public isShowSort?: boolean; // 是否显示排序
  public fixedStyle?: FixedStyle; // 固定列属性
  public configurable?: boolean; // 列可配置
  public isShowFilter?: boolean; // 是否显示筛选
  public filterList?: { text: string, value: any }[]; // 筛选类别
  public handleFilter?: ($event) => void; // 筛选处理函数
  public isCustomFilter?: boolean; // 自定义筛选菜单
  public template?: any; // 自定义筛选模板
  public renderTemplate?: any; // 当为 类型为render的模板
  public expendDataKey?: string; // 展开列的数据主键配置
  public expandConfig?: TableConfig | FormItem[]; // 展开列的配置
  public searchable?: boolean; // 是否可搜索
  public searchConfig?: SearchConfig;
  public pipe?: string;
  public pipeParam?: any;
  public remarkPipe?: string;
}

export interface Scroll {
  x?: string;
  y?: string;
}

export class SearchConfig {
  type: string; // 搜索类型 'input' |'select'|'date'|'render'
  selectType?: string; // 当搜索类型为下拉选择时 多选或者单选 'multiple' 丨 'tags' 丨 'default'
  selectInfo?: any;
  label?: string;
  value?: string;
  initialValue?: any; // 初始值
  notAllowClear?: boolean; // 当为select 时候 不显示清除按钮
  renderTemplate?: TemplateRef<any>;
}

export interface FixedStyle {

  fixedLeft?: boolean;
  fixedRight?: boolean;
  style: { left?: string, right?: string };
}

export class TableConfig {
  constructor() {
    this.topButtons = [];
    this.columnConfig = [];
    this.operation = [];
    this.leftBottomButtons = [];
    this.expendDataKey = 'expandData';
  }

  public isLoading?: boolean; // 表格是否显示加载状态
  public scroll?: Scroll; // 表格滚动配置
  public noIndex?: boolean;
  public bordered?: boolean; // 是否带边框
  public isDraggable?: boolean; // 是否拖拽
  public showPagination?: boolean; // 是否显示分页
  public columnConfig: ColumnConfig[]; // 表格列配置
  public operation?: Operation[];  // 操作栏
  public topButtons?: Operation[]; // 表格头部按钮
  public sort?: (e) => any; // 排序事件
  public expandTemplate?: any; // 展开行 模板
  public showSearchSwitch?: boolean; // 是否显示搜索控制按钮
  public showSearch?: any; // 是否显示搜索栏
  public searchTemplate?: any; // 搜索栏 模板
  public handleSearch?: any; // 搜索事件
  public handleSelect?: any; // 点击选择事件
  public expendDataKey?: string; // 展开列的数据主键配置
  public expandConfig?: TableConfig | FormItem[]; // 展开列的配置
  public showRowSelection?: boolean; // 是否显示checkbox下拉选择器
  public showSizeChanger?: boolean; // 是否显示分页器的 pageSize 改变框
  public pageSizeOptions?: number[]; // 指定每页可以显示多少条 默认值[10, 20, 30, 40]
  public leftBottomButtons?: Operation[]; // 表格左下角的按钮
  public topCustomButton?: TemplateRef<any>; // 表格左上角自定义按钮
  public searchReturnType?: string; // 搜索返回值类型 'object' |'array' 默认为array
  public hideOnSinglePage?: boolean; // 只有一页时隐藏分页
  public simplePage?: boolean; // 显示简单分页
  public rightTopButtons?: Operation[]; // 表格右上角的按钮
  public expandHandle?: () => any; // 处理展开表格的事件

}

export class Operation {
  public btnType?: string; // 按钮类型
  public text: string; // 操作名称
  public className?: string; // 样式的类名
  public iconClassName?: string; // 有图标样式的类名
  public canDisabled?: boolean; // 当为头部按钮时 按钮是否可以被禁用
  public needConfirm?: boolean; // 操作需要确认
  public confirmTitle?: string; // 确认框的title
  public confirmContent?: string; // 确认框的内容信息
  public handle: any; // 操作处理程序
  public key?: string;
}
