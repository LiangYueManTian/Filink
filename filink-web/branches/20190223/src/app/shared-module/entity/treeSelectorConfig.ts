/**
 * Created by wh1709040 on 2018/12/27.
 */
export class TreeSelectorConfig {
  treeSetting?: any;
  treeNodes?: any[];
  width?: string;
  height?: string;
  title?: string;
  selectedColumn: any[];
  onlyLeaves?: boolean;

  constructor() {
    this.treeSetting = {data: {key: {name: 'name'}}};
    this.treeNodes = [];
    this.width = '500px';
    this.title = '选择';
  }
}
