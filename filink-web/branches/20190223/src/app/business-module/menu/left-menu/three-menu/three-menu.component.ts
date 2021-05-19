import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-three-menu',
  templateUrl: './three-menu.component.html',
  styleUrls: ['./three-menu.component.scss']
})
export class ThreeMenuComponent implements OnInit, OnChanges {

  @Input() threeMenuList = [];
  @Input() title = '';

  // 判断菜单是否展开
  isShow = true;
  menuName = '';
  constructor(private $router: Router) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes.threeMenuList.currentValue) {
      this.menuName = changes.threeMenuList.currentValue[0].menuName;
    }
  }

  /**
   * 路由跳转
   * param item
   */
  itemClick(item) {
    this.menuName = item.menuName;
    this.$router.navigate([item.menuHref]).then();
  }

  /**
   * 展开事件
   * param item
   */
  expandItem(item) {
    const nodes = JSON.parse(JSON.stringify(this.threeMenuList));
    for (let i = 0 ; i < nodes.length; i++) {
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
