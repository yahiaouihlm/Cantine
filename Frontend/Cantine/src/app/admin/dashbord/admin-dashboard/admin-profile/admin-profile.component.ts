import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../../sharedmodule/functions/validation";
import {Observable, of} from "rxjs";
import {Adminfunction} from "../../../../sharedmodule/models/adminfunction";
import {AdminService} from "../../admin.service";
import {User} from "../../../../sharedmodule/models/user";
import Malfunctions from "../../../../sharedmodule/functions/malfunctions";
import {Router} from "@angular/router";
import {IConstantsURL} from "../../../../sharedmodule/constants/IConstantsURL";
import {ValidatorDialogComponent} from "../../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {
    SuccessfulDialogComponent
} from "../../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";

@Component({
    selector: 'app-admin-profile',
    templateUrl: './admin-profile.component.html',
    styleUrls: ['../../../../../assets/styles/admin-profile.component.scss'],
    providers: [AdminService]
})
export class AdminProfileComponent implements OnInit {

    private WOULD_YOU_LIKE_TO_SAVE_THIS_MODIFICATIONS = "Voulez-vous vraiment sauvegarder ces modifications ?";
    private MODIFICATIONS_SAVED_SUCCESSFULLY = "Vos modifications ont été enregistrées avec succès";
    submitted = false;
    admin = new User;
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

    constructor(private adminService: AdminService, private router: Router, private matDialog: MatDialog) {
    }


    ngOnInit(): void {
        this.adminUpdated.controls['email'].disable();
        this.adminUpdated.disable();
        if (!Malfunctions.checkAdminConnectivityAndMakeRedirection(this.router)) {
            return;
        }
        let adminUuid = Malfunctions.getUserIdFromLocalStorage();
        this.adminfunction$ = this.adminService.getAdminFunctionS();
        this.adminService.getAdminById(adminUuid).subscribe((admin) => {
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
            address: this.admin.address,
            town: this.admin.town,
            adminFunction: this.admin.function,
        });
    }

    onSubmit() {
        this.submitted = true;
        if (this.adminUpdated.invalid || !this.adminUpdated.touched) {
            return;
        }
        this.isLoading = true;
        const formData = new FormData();
        formData.append('uuid', this.admin.uuid);
        formData.append('firstname', this.adminUpdated.value.firstName);
        formData.append('lastname', this.adminUpdated.value.lastName);
        formData.append('birthdateAsString', this.adminUpdated.value.birthDate);
        formData.append('phone', this.adminUpdated.value.phoneNumber);
        formData.append('town', this.adminUpdated.value.town);
        formData.append('address', this.adminUpdated.value.address);
        formData.append('function', this.adminUpdated.value.adminFunction);
        if (this.image) {
            formData.append('image', this.image);
        }

        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_SAVE_THIS_MODIFICATIONS},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.updateAdmin(formData);
            } else {
                this.isLoading = false;
                return;
            }
        });


    }

    updateAdmin(adminInfo: FormData) {
        this.adminService.updateAdmin(adminInfo).subscribe({
            next: (data) => {
                this.isLoading = false;
                const dialog = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: this.MODIFICATIONS_SAVED_SUCCESSFULLY},
                    width: '40%',
                });
                dialog.afterClosed().subscribe((result) => {
                    this.router.navigate([IConstantsURL.ADMIN_HOME_URL]).then(r => window.location.reload());
                });
            },
            error: (error) => {
                this.isLoading = false;
            }
        });
    }

    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        this.image = (target.files as FileList)[0];
    }

    get f(): { [key: string]: AbstractControl } {
        return this.adminUpdated.controls;
    }


    onUpdate() {
        this.adminUpdated.enable();
        this.adminUpdated.controls['email'].disable();
    }

    backToHome() {
        this.router.navigate(([IConstantsURL.ADMIN_HOME_URL])).then(r => window.location.reload());
    }
}
