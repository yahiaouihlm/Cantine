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
    email: new FormControl('', [Validators.required ,  Validators.maxLength(1000),  Validators.pattern("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$")]),
    password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
    confirmPassword: new FormControl("" , [Validators.required]),
    /*  label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
    image: new FormControl('', [Validators.required]),
    status: new FormControl('', [Validators.required]),*/
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
  onChange = ($event: Event) => {
    const target = $event.target as HTMLInputElement;
    const file: File = (target.files as FileList)[0]
    this.image = file;
  }
  get f(): { [key: string]: AbstractControl } {
    return this.adminForm.controls;
  }

}
