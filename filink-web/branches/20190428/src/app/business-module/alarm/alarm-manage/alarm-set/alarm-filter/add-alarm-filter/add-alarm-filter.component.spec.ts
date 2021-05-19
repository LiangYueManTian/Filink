import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddAlarmFilterComponent } from './add-alarm-filter.component';

describe('AddAlarmFilterComponent', () => {
  let component: AddAlarmFilterComponent;
  let fixture: ComponentFixture<AddAlarmFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddAlarmFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddAlarmFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
