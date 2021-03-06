/**
 * Created by xiaoconghu on 2019/1/9.
 */

import {Component, OnInit} from '@angular/core';
import {Router, ActivatedRoute, NavigationEnd, Params, PRIMARY_OUTLET} from '@angular/router';

import {filter} from 'rxjs/operators';
import {NzI18nService} from 'ng-zorro-antd';

interface IBreadcrumb {
  label: string;
  params: Params;
  url: string;
}

const ADD = 'add';
const UPDATE = 'update';
const VIEW = 'view';
const ROUTE_DATA_BREADCRUMB: string = 'breadcrumb';

@Component({
  selector: 'xc-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss']
})
export class BreadcrumbComponent implements OnInit {

  public breadcrumbs: IBreadcrumb[] = [];
  language;
  constructor(private activatedRoute: ActivatedRoute,
              private i18n: NzI18nService,
              private router: Router) {
    this.language = i18n.getLocaleData('breadcrumb');
    this.breadcrumbs = [];
    this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe(event => {
      const root: ActivatedRoute = this.activatedRoute.root;
      // 智能获取 在路由配置比较理想的情况下可以用
      // this.breadcrumbs = this.getBreadcrumbs(root);
      this.breadcrumbs = this.getLastBreadcrumbs(root);

    });
  }

  ngOnInit() {
  }

  /**
   * 返回表示面包屑的IBreadcrumb对象数组
   * method getBreadcrumbs
   * param {ActivateRoute} route
   * param {string} url
   * param {IBreadcrumb[]} breadcrumbs
   */
  private getBreadcrumbs(route: ActivatedRoute, url: string = '', breadcrumbs: IBreadcrumb[] = []): IBreadcrumb[] {

    // get the child routes
    const children: ActivatedRoute[] = route.children;

    // return if there are no more children
    if (children.length === 0) {
      return breadcrumbs;
    }

    // iterate over each children
    for (const child of children) {
      // verify primary route
      if (child.outlet !== PRIMARY_OUTLET) {
        continue;
      }

      // verify the custom data property "breadcrumb" is specified on the route
      if (!child.snapshot.data.hasOwnProperty(ROUTE_DATA_BREADCRUMB)) {
        return this.getBreadcrumbs(child, url, breadcrumbs);
      }

      // get the route's URL segment
      const routeURL: string = child.snapshot.url.map(segment => segment.path).join('/');

      // append route URL to URL
      url += `/${routeURL}`;

      // add breadcrumb
      const breadcrumb: IBreadcrumb = {
        label: child.snapshot.data[ROUTE_DATA_BREADCRUMB],
        params: child.snapshot.params,
        url: url
      };
      breadcrumbs.push(breadcrumb);

      // recursive
      return this.getBreadcrumbs(child, url, breadcrumbs);
    }
  }

  /**
   * 获取最后一个路由的数据
   * param {ActivatedRoute} route
   * returns {Data}
   */
  getLastBreadcrumbs(route: ActivatedRoute): IBreadcrumb[] {
    // 找到最后一个路由
    while (route.firstChild) {
      route = route.firstChild;
    }
    // 把路由里面的数据拷贝一份 防止改变源数据
    if (route.snapshot.data && route.snapshot.data.breadcrumb) {
      const breadcrumb = JSON.parse(JSON.stringify(route.snapshot.data.breadcrumb));
      this.translate(breadcrumb);
      if (route.snapshot.params.type) {
        const label = this.getTypeLabel(route.snapshot.params.type);
        breadcrumb[breadcrumb.length - 1].label = label + breadcrumb[breadcrumb.length - 1].label;
        return breadcrumb;
      } else {
        return breadcrumb as IBreadcrumb[];
      }
    } else {
      return [];
    }

  }

  private getTypeLabel(type): string {
    let title;
    switch (type) {
      case ADD:
        title = this.language.add;
        break;
      case UPDATE:
        title = this.language.update;
        break;
      case VIEW:
        title = this.language.view;
        break;
    }
    return title;
  }

  /**
   * 统一翻译面包屑
   * param breadcrumb
   */
  private translate(breadcrumb) {
    breadcrumb.forEach(item => {
      item.label = this.i18n.translate(`${ROUTE_DATA_BREADCRUMB}.${item.label}`);
    });
  }
}
