import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
   styleUrls: ['../../../assets/styles/authentication.component.scss']
})
export class AuthenticationComponent {
    submitted = false;
    signIn: FormGroup = new FormGroup({
        email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
        password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
    });







    get f(): { [key: string]: AbstractControl } {
        return this.signIn.controls;
    }




}
