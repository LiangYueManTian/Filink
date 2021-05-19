import { Component, OnInit } from '@angular/core';
import {MapStoreService} from '../../core-module/store/map.store.service';
import {IndexMissionService} from '../../core-module/mission/index.mission.service';

@Component({
  selector: 'app-index',
  templateUrl: './index.component.html',
  styleUrls: ['./index.component.scss']
})
export class IndexComponent implements OnInit {
  mapType: string;
  left;
  constructor(private $mapStoreService: MapStoreService,
              private $mission: IndexMissionService) {
  }

  ngOnInit() {
    this.left = '50px';
    this.mapType = localStorage.getItem('mapType');
    this.$mapStoreService.mapType = this.mapType;
    // this.$mission.leftMenuExpandChangeHook.subscribe(bol => {
    //   console.log(bol);
    //   this.left = bol ? '50px' : '200px';
    // });
  }

}
