import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../sharedmodule/functions/validation";
import {Observable, of} from "rxjs";
import {Adminfunction} from "../../../sharedmodule/models/adminfunction";

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-up.component.html',
  styles: [
  ]
})
export class SignUpComponent {

    image :  File | undefined;
    submitted = false;
    existEmail = false;
    studentClass: Observable<Adminfunction[]> = of([]);
  studentForm: FormGroup = new FormGroup({
        firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
        lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
        email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
        password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
        confirmPassword: new FormControl("", [Validators.required]),
        birthDate: new FormControl('', [Validators.required]),
        phoneNumber: new FormControl('', [Validators.pattern(Validation.FRENCH_PHONE_REGEX)]),
        town: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(3)]),
        studentClass: new FormControl('', [Validators.required]),
        image: new FormControl(''),

      },
      {
        validators: [Validation.match('password', 'confirmPassword')]
      }
  );


    onSubmit()     {


  }




    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        const file: File = (target.files as FileList)[0]
        this.image = file;
    }

    get f(): { [key: string]: AbstractControl } {
        return this.studentForm.controls;
    }



}
