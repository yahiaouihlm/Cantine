import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";
import {ActivatedRoute, Router} from "@angular/router";
import {SharedService} from "../../sharedmodule/shared.service";
import {SuccessfulDialogComponent} from "../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";


@Component({
    selector: 'app-reset-password',
    templateUrl: './reset-password.component.html',
    styleUrls: ['../../../assets/styles/global.scss']
})
export class ResetPasswordComponent implements OnInit {

    submitted = false;
    isLoadingPage = false;
    private  token  = '';
    constructor(private route: ActivatedRoute , private sharedService :  SharedService ,  private matDialog :  MatDialog , private  router :  Router) {}
    newPasswordForm: FormGroup = new FormGroup({
            password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
            confirmPassword: new FormControl("", [Validators.required]),
        },
        {
            validators: [Validation.match('password', 'confirmPassword')]
        }
    )

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            this.token = params['token'];

            if (!this.token) {  /*TODO redirect to ERROR page */
                this.router.navigate(['cantine/signIn']).then(r => console.log("redirected to login page"));
            }
        });

    }


    OnSubmit(): void {
        this.submitted = true;
        if (this.newPasswordForm.invalid) {
            return;
        }
        this.isLoadingPage = true;
        this.resetPassword();
    }

    resetPassword() {
        this.sharedService.resetPassword(this.token, this.newPasswordForm.value.password).subscribe((response) => {
            this.isLoadingPage = false;
            let dialogue = this.matDialog.open(SuccessfulDialogComponent, {
                data: {message: "Votre mot de passe a été réinitialisé avec succès"},
                width: '40%',
            });
            dialogue.afterClosed().subscribe(result => {
                this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(r => console.log("redirected to login page"));
            });
        });
    }


    get f(): { [key: string]: AbstractControl } {
        return this.newPasswordForm.controls;
    }


}