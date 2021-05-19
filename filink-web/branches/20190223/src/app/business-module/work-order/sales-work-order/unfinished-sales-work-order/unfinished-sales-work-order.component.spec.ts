import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UnfinishedSalesWorkOrderComponent } from './unfinished-sales-work-order.component';

describe('UnfinishedSalesWorkOrderComponent', () => {
  let component: UnfinishedSalesWorkOrderComponent;
  let fixture: ComponentFixture<UnfinishedSalesWorkOrderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UnfinishedSalesWorkOrderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UnfinishedSalesWorkOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
