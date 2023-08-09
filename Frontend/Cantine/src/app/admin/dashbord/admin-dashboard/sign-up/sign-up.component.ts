import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../../sharedmodule/functions/validation";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styles: [
  ]
})
export class SignUpComponent {
   submitted = false;
    image!: File ;
  isLoaded = false;


  adminForm: FormGroup = new FormGroup({
    firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    email: new FormControl('', [Validators.required ,  Validators.maxLength(1000),  Validators.pattern(Validation.EMAIL_REGEX)]),
    password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
    confirmPassword: new FormControl("" , [Validators.required]),
    birthDate: new FormControl('', [Validators.required]),
    address: new FormControl('', [Validators.required, Validators.maxLength(3000), Validators.minLength(10)]),
    phoneNumber: new FormControl('', [Validators.required, Validators.pattern(Validation.FRENCH_PHONE_REGEX)]),
    town: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(3)]),
    image: new FormControl(''),

  },
      {
        validators: [Validation.match('password', 'confirmPassword')]
  }
  );


  onSubmit() {
    this.submitted = true;
    this.isLoaded = true;
    if (this.adminForm.invalid) {
      return;
    }
    console.log("form is valid")
  }



  signUp() {
    const formData = new FormData();
    formData.append('firstName', this.adminForm.value.firstName);
    formData.append('lastName', this.adminForm.value.lastName);
    formData.append('email', this.adminForm.value.email);
    formData.append('password', this.adminForm.value.password);
    formData.append('birthdateAsString', this.adminForm.value.birthDate);
    formData.append('phoneNumber', this.adminForm.value.phoneNumber);
    formData.append('town', this.adminForm.value.town);
    if (this.image != null || this.image != undefined) // envoyer  une image  uniquement si  y'a eu  une image  !
      formData.append('image', this.image);

  }




  onChange = ($event: Event) => {
    const target = $event.target as HTMLInputElement;
    const file: File = (target.files as FileList)[0]
    this.image = file;
  }
  get f(): { [key: string]: AbstractControl } {
    return this.adminForm.controls;
  }

}
