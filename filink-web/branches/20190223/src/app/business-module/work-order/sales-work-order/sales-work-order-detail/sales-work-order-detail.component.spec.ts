import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SalesWorkOrderDetailComponent } from './sales-work-order-detail.component';

describe('SalesWorkOrderDetailComponent', () => {
  let component: SalesWorkOrderDetailComponent;
  let fixture: ComponentFixture<SalesWorkOrderDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SalesWorkOrderDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SalesWorkOrderDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
