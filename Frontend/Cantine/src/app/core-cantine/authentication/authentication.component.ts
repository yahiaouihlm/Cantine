import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";
import {HttpClient, HttpStatusCode} from "@angular/common/http";
import {CoreCantineService} from "../core-cantine.service";
import {SharedService} from "../../sharedmodule/shared.service";
import {SuccessfulDialogComponent} from "../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";

@Component({
    selector: 'app-authentication',
    templateUrl: './authentication.component.html',
    styleUrls: ['../../../assets/styles/authentication.component.scss'],
    providers: [CoreCantineService, SharedService]
})
export class AuthenticationComponent {

    private USER_DISABLED_ACCOUNT = "DISABLED ACCOUNT";
    private USER_WRONG_CREDENTIALS = "WRONG CREDENTIALS";
    private ADMIN_INVALID_ACCOUNT = "INVALID ACCOUNT";
    submitted = false;
    disabled_account = false;
    wrong_credentials = false;
    invalid_account = false;
    isLoading = false;
    signIn: FormGroup = new FormGroup({
        email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
        password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
    });
   /*TODO
      make  a  behvour when  there  we  go  to  the  sign  while  have  the  token in  the  local  storage
    */
    constructor(private coreCantineService: CoreCantineService, private sharedService: SharedService, private matDialog: MatDialog ,  private  router: Router) {
    }

    singIn() {
        this.submitted = true;
        if (this.signIn.invalid) {
            return;
        }
        this.isLoading = true;

        this.coreCantineService.userAuthentication(this.signIn.value).subscribe({
                next: (response) => {
                    this.disabled_account = false;
                    this.wrong_credentials = false;
                    this.isLoading = false
                    const authObjectJSON = JSON.stringify(response);
                    localStorage.setItem('authObject', authObjectJSON);
                    if  (response.role === "ROLE_ADMIN") {
                        this.router.navigate(['cantine/admin']).then(() => {
                            window.location.reload();
                        });;
                    }else {
                        this.router.navigate(['cantine/home']).then(() => {
                            window.location.reload();
                        });;

                    }

                },
                error: (error) => {
                    if (error.status === HttpStatusCode.Forbidden && error.error.message === this.USER_DISABLED_ACCOUNT) {
                        this.disabled_account = true;
                        this.wrong_credentials = false;
                    } else if (error.status === HttpStatusCode.Unauthorized && error.error.message === this.USER_WRONG_CREDENTIALS) {
                        this.wrong_credentials = true;
                        this.disabled_account = false;
                    }
                    else if (error.status === HttpStatusCode.Unauthorized && error.error.message === this.ADMIN_INVALID_ACCOUNT) {
                        console.log("invalid account")
                        this.invalid_account = true;
                        this.disabled_account = false;
                        this.wrong_credentials = false;
                    }
                    this.isLoading = false

                }
            }
        );

    }


    sendTokenToActivateAccount() {

        this.isLoading = true;
        this.sharedService.sendToken(this.signIn.value.email).subscribe({
            next: (response) => {
               let  dialogue =  this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: "Un  Email  vous a éte  envoyer à   " + this.signIn.value.email + " pour  Activer    Votre  Compte  "},
                    width: '40%',
                });
               dialogue.afterClosed().subscribe(result => {window.location.reload();});

                this.isLoading = false;
            },
            error: (error) => {
                this.isLoading = false;
            }
        });

    }


    get f(): { [key: string]: AbstractControl } {
        return this.signIn.controls;
    }


}
