import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifyAlarmFilterComponent } from './modify-alarm-filter.component';

describe('ModifyAlarmFilterComponent', () => {
  let component: ModifyAlarmFilterComponent;
  let fixture: ComponentFixture<ModifyAlarmFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ModifyAlarmFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ModifyAlarmFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
