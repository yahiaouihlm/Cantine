import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../../sharedmodule/functions/validation";

@Component({
  selector: 'app-admin-profile',
  template: `
    <p>
      admin-profile works!
    </p>
  `,
  styles: [
  ]
})
export class AdminProfileComponent {

  adminUpdated: FormGroup = new FormGroup({
    firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
    birthDate: new FormControl('', [Validators.required]),
    phoneNumber: new FormControl('', [Validators.pattern(Validation.FRENCH_PHONE_REGEX)]),
    town: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(3)]),
    address: new FormControl('', [Validators.required, Validators.maxLength(3000), Validators.minLength(10)]),
    adminFunction: new FormControl('', [Validators.required]),
    image: new FormControl(''),
  });
}
