import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlarmFilterComponent } from './alarm-filter.component';

describe('AlarmFilterComponent', () => {
  let component: AlarmFilterComponent;
  let fixture: ComponentFixture<AlarmFilterComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AlarmFilterComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlarmFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
