import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";
import {SharedService} from "../../sharedmodule/shared.service";
import {MatDialog} from "@angular/material/dialog";
import {SuccessfulDialogComponent} from "../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {Router} from "@angular/router";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";


@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls:['../../../assets/styles/forgot-password.component.scss', '../../../assets/styles/global.scss'] ,
    providers: [SharedService]
})
export class ForgotPasswordComponent {

  submitted = false;
  forgotPasswordForm  :  FormGroup = new  FormGroup({
    email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)])
  })
    isLoadingPage =  false;
  private EMAIL_SENT_SUCCESSFULLY_TO_RESET_PASSWORD = "un email a été envoyé à votre adresse email pour réinitialiser votre mot de passe";
 constructor( private sharedService: SharedService, private  matDialog : MatDialog, private router : Router) {
 }

  get f(): { [key: string]: AbstractControl } {
    return this.forgotPasswordForm.controls;
  }
  OnSubmit() : void {
      this.submitted = true;
    if (this.forgotPasswordForm.invalid) {
      return;
    }
      this.isLoadingPage = true;
      this.sendLinkForgotPassword();
  }

    sendLinkForgotPassword() : void {   //  we  have to change  the  loading field before  and  after  the  request
     this.sharedService.sendTokenForgotPassword(this.forgotPasswordForm.value.email).subscribe({
            next: (response) => {
                let result = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: this.EMAIL_SENT_SUCCESSFULLY_TO_RESET_PASSWORD},
                    width: '40%',
                });
                result.afterClosed().subscribe(() => {
                    this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
                });
                this.isLoadingPage = false;

            },
            error: (error) => {
                this.isLoadingPage = false;
            }
     });
  }
}
