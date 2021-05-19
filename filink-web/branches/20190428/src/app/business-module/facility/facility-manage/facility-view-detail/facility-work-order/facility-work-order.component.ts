import {Component, Input, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-facility-work-order',
  templateUrl: './facility-work-order.component.html',
  styleUrls: ['./facility-work-order.component.scss']
})
export class FacilityWorkOrderComponent implements OnInit {
  @Input()
  deviceId;

  constructor(private $router: Router) {
  }

  ngOnInit() {
  }

  navigatorTo(url) {
    this.$router.navigate([url], {queryParams: {deviceId: this.deviceId}}).then();
  }
}
