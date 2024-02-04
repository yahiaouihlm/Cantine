import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";

@Component({
    selector: 'app-reset-password',
    templateUrl: './reset-password.component.html',
    styleUrls: ['../../../assets/styles/global.scss']
})
export class ResetPasswordComponent {

    submitted = false;
    isLoadingPage = false;
    newPasswordForm: FormGroup = new FormGroup({
            password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
            confirmPassword: new FormControl("", [Validators.required]),
        },
        {
            validators: [Validation.match('password', 'confirmPassword')]
        }
    )

    OnSubmit(): void {
        this.submitted = true;
        if (this.newPasswordForm.invalid) {
            return;
        }
    }

    get f(): { [key: string]: AbstractControl } {
        return this.newPasswordForm.controls;
    }
}
