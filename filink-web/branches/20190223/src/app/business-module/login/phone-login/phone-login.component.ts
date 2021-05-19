import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-phone-login',
  templateUrl: './phone-login.component.html',
  styleUrls: ['./phone-login.component.scss']
})
export class PhoneLoginComponent implements OnInit {

  validateForm: FormGroup;
  areaCodeList = [{
    label: '+86',
    value: 1
  }];
  constructor(private fb: FormBuilder) { }

  ngOnInit() {
    this.validateForm = this.fb.group({
      phone: [ null, [ Validators.required ] ],
      authCode: [ null, [ Validators.required ] ],
      remember: [ true ]
    });
}
}
