import {Component, forwardRef, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormControl, FormGroup, NG_VALUE_ACCESSOR, Validators} from "@angular/forms";
import {IConstantsMessages} from "../../shared-module/constants/IConstantsMessages";


@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss'],
  providers: []
})
export class SignInComponent {

  wrongCredentials  = false ;

  submitted = false;
  constructor() {

  }


 public  signIn = new FormGroup({
   email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(IConstantsMessages.EMAIL_REGEX)]),
   password: new FormControl('', [Validators.required]),
  });


  onSubmit() {

    this.submitted = true;
      if (this.signIn.invalid) {
        return;
      }

  }

  get f(): { [key: string]: AbstractControl } {
    return this.signIn.controls;
  }



}
