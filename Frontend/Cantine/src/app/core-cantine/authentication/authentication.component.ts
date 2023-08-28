import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";
import {HttpClient, HttpStatusCode} from "@angular/common/http";
import {CoreCantineService} from "../core-cantine.service";

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
   styleUrls: ['../../../assets/styles/authentication.component.scss'],
    providers: [CoreCantineService]
})
export class AuthenticationComponent {

    private  USER_DISABLED_ACCOUNT = "DISABLED ACCOUNT";
    private  USER_WRONG_CREDENTIALS = "WRONG CREDENTIALS";
    submitted = false;
    disabled_account = false;
    wrong_credentials = false;
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

       this.coreCantineService.userAuthentication(this.signIn.value).subscribe({
              next: (response) => {
                  this.disabled_account = false;
                  this.wrong_credentials = false;
                  console.log(response);
                  console.log("vous  etes  connecter ");
              },
              error : (error) => {
                if  (error.status ===  HttpStatusCode.Forbidden  &&  error.error.message === this.USER_DISABLED_ACCOUNT) {
                    this.disabled_account = true;
                    this.wrong_credentials = false;
                }
                else if  (error.status ===  HttpStatusCode.Unauthorized  &&  error.error.message === this.USER_WRONG_CREDENTIALS) {
                    this.wrong_credentials = true;
                    this.disabled_account = false;
                }
              }
           }
       );

    }



    sendTokenToActivateAccount(email:string) {

    }

    get f(): { [key: string]: AbstractControl } {
        return this.signIn.controls;
    }




}
