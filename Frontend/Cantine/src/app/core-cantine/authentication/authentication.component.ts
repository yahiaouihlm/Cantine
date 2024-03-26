import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";
import {HttpStatusCode} from "@angular/common/http";
import {CoreCantineService} from "../core-cantine.service";
import {SharedService} from "../../sharedmodule/shared.service";
import {SuccessfulDialogComponent} from "../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {IConstantsMessages} from "../../sharedmodule/constants/IConstantsMessages";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-authentication',
    templateUrl: './authentication.component.html',
    styleUrls: ['../../../assets/styles/authentication.component.scss', '../../../assets/styles/global.scss'],
    providers: [CoreCantineService, SharedService]
})
export class AuthenticationComponent  implements  OnInit{


    submitted = false;
    disabled_account = false;
    wrong_credentials = false;
    invalid_account = false;
    isLoading = false;
    signIn: FormGroup = new FormGroup({
        email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
        password: new FormControl('', [Validators.required]),
    });

    constructor(private coreCantineService: CoreCantineService, private sharedService: SharedService, private matDialog: MatDialog, private router: Router) {
    }

    ngOnInit(): void {
        Malfunctions.checkUserConnection(this.router);
    }

    singIn() {
        this.disabled_account = false;
        this.wrong_credentials = false;
        this.invalid_account = false;
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
                    console.log("response", response)
                    if (response.role === IConstantsMessages.ADMIN_ROLE) {
                        this.router.navigate([IConstantsURL.ADMIN_HOME_URL]).then(() => {
                            window.location.reload();
                        });
                    } else if (response.role === IConstantsMessages.STUDENT_ROLE) {
                        this.router.navigate([IConstantsURL.HOME_URL]).then(() => {
                            window.location.reload();
                        });

                    }else {
                        this.invalid_account = true;
                        /*TODO  redirection    to  error  page  */
                    }

                },
                error: (error) => {
                    if (error.status === HttpStatusCode.Forbidden && error.error.message === IConstantsMessages.USER_DISABLED_ACCOUNT) {
                        this.disabled_account = true;
                        this.wrong_credentials = false;
                    } else if (error.status === HttpStatusCode.Unauthorized && error.error.message === IConstantsMessages.USER_WRONG_CREDENTIALS) {
                        this.wrong_credentials = true;
                        this.disabled_account = false;
                    } else if (error.status === HttpStatusCode.Unauthorized && error.error.message === IConstantsMessages.ADMIN_INVALID_ACCOUNT) {
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
                let dialogue = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: "Un  Email  vous a éte  envoyer à   " + this.signIn.value.email + " pour  Activer    Votre  Compte  "},
                    width: '40%',
                });
                dialogue.afterClosed().subscribe(result => {
                    window.location.reload();
                });

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
    forgotPassword() {
        this.router.navigate([IConstantsURL.FORGOT_PASSWORD_URL]).then(r => window.location.reload());
    }


    goToSignUpStudent() {
        this.router.navigate([IConstantsURL.STUDENT_SIGN_UP_URL]).then(r => window.location.reload());
    }
}
