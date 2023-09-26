import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../../sharedmodule/functions/validation";

@Component({
    selector: 'app-admin-profile',
    template: `
        <p>
            admin-profile works!
        </p>
    `,
    styles: []
})
export class AdminProfileComponent {

    submitted = false;
    adminUpdated: FormGroup = new FormGroup({
        firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
        lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
        email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
        birthDate: new FormControl('', [Validators.required]),
        phoneNumber: new FormControl('', [Validators.pattern(Validation.FRENCH_PHONE_REGEX)]),
        town: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(3)]),
        address: new FormControl('', [Validators.required, Validators.maxLength(3000), Validators.minLength(10)]),
        adminFunction: new FormControl('', [Validators.required]),
        image: new FormControl(''),
    });
    image!: File;

    constructor() {
    }

    onSubmit() {
        this.submitted = true;
        if (this.adminUpdated.invalid || !this.adminUpdated.touched) {
            return;
        }
    }

    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        const file: File = (target.files as FileList)[0]
        this.image = file;
    }

    get f(): { [key: string]: AbstractControl } {
        return this.adminUpdated.controls;
    }

}
