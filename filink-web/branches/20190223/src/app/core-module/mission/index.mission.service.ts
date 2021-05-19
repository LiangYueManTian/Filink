import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/index';

@Injectable()
export class IndexMissionService {
  private leftMenuExpandStatus = new Subject<boolean>();
  leftMenuExpandChangeHook = this.leftMenuExpandStatus.asObservable();
  leftMenuExpandChange(bol: boolean) {
    this.leftMenuExpandStatus.next(bol);
  }
}
