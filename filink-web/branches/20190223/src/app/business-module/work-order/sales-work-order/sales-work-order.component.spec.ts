import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SalesWorkOrderComponent } from './sales-work-order.component';

describe('SalesWorkOrderComponent', () => {
  let component: SalesWorkOrderComponent;
  let fixture: ComponentFixture<SalesWorkOrderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SalesWorkOrderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SalesWorkOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
