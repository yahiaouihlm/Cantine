import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../../sharedmodule/functions/validation";
import {Observable, of} from "rxjs";
import {Adminfunction} from "../../../../sharedmodule/models/adminfunction";
import {AdminService} from "../../admin.service";
import {GlobalAdminService} from "../../../global-admin.service";
import {User} from "../../../../sharedmodule/models/user";
import Malfunctions from "../../../../sharedmodule/functions/malfunctions";

@Component({
    selector: 'app-admin-profile',
    templateUrl: './admin-profile.component.html',
    styles: [],
    providers: [AdminService, GlobalAdminService]
})
export class AdminProfileComponent implements OnInit {

    submitted = false;
    admin!: User;
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
    isLoading = false;
    adminfunction$: Observable<Adminfunction[]> = of([]);

    constructor(private adminService: AdminService, private globalAdminService: GlobalAdminService) {
    }


    ngOnInit(): void {
        this.adminUpdated.disable();
        let idAdmin = Malfunctions.getUserIdFromLocalStorage();
        this.adminfunction$ = this.adminService.getAdminFunctionS();
        this.globalAdminService.getAdminById(idAdmin).subscribe((admin) => {
            this.admin = admin;
            this.matchFormsValue();
        });
    }
    matchFormsValue() {
        this.adminUpdated.patchValue({
            firstName: this.admin.firstname,
            lastName: this.admin.lastname,
            email: this.admin.email,
            birthDate: this.admin.birthdate,
            phoneNumber: this.admin.phone,
            town: this.admin.town,
            adminFunction: this.admin.function,
        });
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
