import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FacilitySliderComponent } from './facility-slider.component';

describe('FacilitySliderComponent', () => {
  let component: FacilitySliderComponent;
  let fixture: ComponentFixture<FacilitySliderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FacilitySliderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FacilitySliderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
