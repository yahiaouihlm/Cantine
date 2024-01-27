import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";

@Component({
    selector: 'app-change-password',
    templateUrl: './change-password.component.html',
    styleUrls: ['../../../assets/styles/global.scss']
})
export class ChangePasswordComponent {

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

    }

    get f(): { [key: string]: AbstractControl } {
        return this.newPasswordForm.controls;
    }
}
