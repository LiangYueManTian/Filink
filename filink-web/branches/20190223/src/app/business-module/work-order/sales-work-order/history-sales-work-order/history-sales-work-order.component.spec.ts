import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HistorySalesWorkOrderComponent } from './history-sales-work-order.component';

describe('HistorySalesWorkOrderComponent', () => {
  let component: HistorySalesWorkOrderComponent;
  let fixture: ComponentFixture<HistorySalesWorkOrderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HistorySalesWorkOrderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HistorySalesWorkOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
