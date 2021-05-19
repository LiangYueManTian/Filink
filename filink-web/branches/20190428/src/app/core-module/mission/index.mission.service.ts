import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/index';

@Injectable()
export class IndexMissionService {
  private facilitySub = new Subject<any>();
  facilityChangeHook = this.facilitySub.asObservable();
  facilityChange(data: any) {
    this.facilitySub.next(data);
  }
}
