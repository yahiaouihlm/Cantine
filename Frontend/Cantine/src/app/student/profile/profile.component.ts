import { Component } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styles: [
  ]
})
export class ProfileComponent {
  constructor() { }

  studentForm: FormGroup = new FormGroup({
    firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
    password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
    birthDate: new FormControl('', [Validators.required]),
    phoneNumber: new FormControl('', [Validators.pattern(Validation.FRENCH_PHONE_REGEX)]),
    town: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(3)]),
    studentClass: new FormControl('', [Validators.required]),
    image: new FormControl(''),

  });
}
