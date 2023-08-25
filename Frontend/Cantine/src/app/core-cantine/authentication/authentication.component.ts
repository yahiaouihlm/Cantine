import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";
import {HttpClient} from "@angular/common/http";
import {CoreCantineService} from "../core-cantine.service";

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
   styleUrls: ['../../../assets/styles/authentication.component.scss'],
    providers: [CoreCantineService]
})
export class AuthenticationComponent {
    submitted = false;
    signIn: FormGroup = new FormGroup({
        email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
        password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
    });

     constructor    ( private  coreCantineService :CoreCantineService) {}
    singIn() {
        this.submitted = true;
        if (this.signIn.invalid) {
            return;
        }

       this.coreCantineService.userAuthentication(this.signIn.value).subscribe(

    }





    get f(): { [key: string]: AbstractControl } {
        return this.signIn.controls;
    }




}
