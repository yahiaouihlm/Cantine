import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";
import {SharedService} from "../../sharedmodule/shared.service";

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styles: [
  ], providers: [SharedService]
})
export class ForgotPasswordComponent {

  submitted = false;
  emailExist = false;
  forgotPasswordForm = new  FormGroup({
    email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)])
  })

 constructor( private sharedService: SharedService) {
 }

  get f(): { [key: string]: AbstractControl } {
    return this.forgotPasswordForm.controls;
  }
  OnSubmit() : void {
    this.submitted = true;
    if (this.forgotPasswordForm.invalid) {
      return;
    }
  }

  checkExistEmail() : void {

  }
}
