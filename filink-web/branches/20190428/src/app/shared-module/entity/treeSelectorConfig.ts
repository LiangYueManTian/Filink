/**
 * Created by wh1709040 on 2018/12/27.
 */
export class TreeSelectorConfig {
  treeSetting?: any;
  treeNodes?: any[];
  width?: string;
  height?: string;
  title?: string;
  selectedColumn?: any[];
  onlyLeaves?: boolean;
  leftTitle?: string;
  rightTitle?: string;
  leftNodes?: any[];
  rightNodes?: any[];
  treeLeftSetting?: any;
  treeRightSetting?: any;

  constructor() {
    this.treeSetting = {data: {key: {name: 'name'}, simpleData: {idKey: 'id'}}};
    this.treeLeftSetting = {data: {key: {name: 'name'}, simpleData: {idKey: 'id'}}};
    this.treeRightSetting = {data: {key: {name: 'name'}, simpleData: {idKey: 'id'}}};
    this.treeNodes = [];
    this.width = '500px';
    this.title = '选择';
  }
}
