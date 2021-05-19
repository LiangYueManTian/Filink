import {Component, Input, OnInit} from '@angular/core';


@Component({
  selector: 'app-view-three-menu',
  templateUrl: './three-view-menu.component.html',
  styleUrls: ['./three-view-menu.component.scss']
})
export class ThreeViewMenuComponent implements OnInit {

  @Input() threeMenuList = [];
  @Input() title = '';

  // 判断菜单是否展开
  isShow = true;
  menuName = '';

  constructor() {
  }

  ngOnInit() {
  }


  /**
   * 展开事件
   * param item
   */
  expandItem(item) {
    const nodes = JSON.parse(JSON.stringify(this.threeMenuList));
    for (let i = 0; i < nodes.length; i++) {
      if (nodes[i].menuId === item.menuId) {
        if (nodes[i].expand) {
          nodes[i].expand = false;
        } else {
          nodes[i].expand = true;
        }
      }
    }
    this.threeMenuList = nodes;
  }
}
