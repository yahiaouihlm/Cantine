import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../../sharedmodule/functions/validation";
import {Adminfunction} from "../../../../sharedmodule/models/adminfunction";
import {AdminService} from "../../admin.service";
import {Observable, of} from "rxjs";
import {
    SuccessfulDialogComponent
} from "../../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {ValidatorDialogComponent} from "../../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {SharedService} from "../../../../sharedmodule/shared.service";
import {Router} from "@angular/router";
import Malfunctions from "../../../../sharedmodule/functions/malfunctions";
import {HttpStatusCode} from "@angular/common/http";
import {DialogErrors} from "../../../../sharedmodule/functions/dialogueErrors";

@Component({
    selector: 'app-sign-up',
    templateUrl: './sign-up.component.html',
    styles: [],
    providers: [AdminService, SharedService]
})
export class SignUpComponent implements OnInit {

    private ADMIN_ADDED_SUCCESSFULLY = "ADMIN ADDED SUCCESSFULLY";
    private WOULD_YOU_LIKE_TO_SIGN_UP = "Voulez-vous vous inscrire ?";
    submitted = false;
    image!: File;
    isLoading = false;
    adminfunction$: Observable<Adminfunction[]> = of([]);

    constructor(private adminService: AdminService, private matDialog: MatDialog, private sharedService: SharedService, private router: Router) {
    }

    adminForm: FormGroup = new FormGroup({
            firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
            lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
            email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
            password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
            confirmPassword: new FormControl("", [Validators.required]),
            birthDate: new FormControl('', [Validators.required]),
            address: new FormControl('', [Validators.required, Validators.maxLength(3000), Validators.minLength(10)]),
            phoneNumber: new FormControl('', [Validators.required, Validators.pattern(Validation.FRENCH_PHONE_REGEX)]),
            town: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(3)]),
            adminFunction: new FormControl('', [Validators.required]),
            image: new FormControl(''),

        },
        {
            validators: [Validation.match('password', 'confirmPassword')]
        }
    );

    ngOnInit(): void {

        Malfunctions.checkUserConnection(this.router);
        this.adminfunction$ = this.adminService.getAdminFunctionS();

    }

    onSubmit() {

        this.submitted = true;
        if (this.adminForm.invalid) {
            return;
        }
        this.isLoading = true;

        this.sharedService.checkExistenceOfEmail(this.adminForm.value.email).subscribe({
                next: (data) => {
                    //  inscrit  l'admin
                    this.isLoading = false;
                },
                error: (error) => {
                    if (error.status == HttpStatusCode.Conflict) {
                        this.adminForm.controls['email'].setErrors({existingEmail: true});
                        this.isLoading = false;
                        return;
                    } else {
                        const dialog = new DialogErrors(this.matDialog);
                        dialog.openDialog("Une  erreur inconnue  est survenu  pendant la  vérification de  email \n veuillez  rasseyez ultérieurement", error.status);
                        /*TODO  redirection    to  error  page  */
                    }
                },
            }
        );


    }

    adminRegistration() {
        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_SIGN_UP},
            width: '40%',
        });
        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.signUp();
            } else {
                return;
            }
        });
    }

    signUp() {
        const formData = new FormData();
        formData.append('firstname', this.adminForm.value.firstName);
        formData.append('lastname', this.adminForm.value.lastName);
        formData.append('email', this.adminForm.value.email);
        formData.append('password', this.adminForm.value.password);
        formData.append('birthdateAsString', this.adminForm.value.birthDate);
        formData.append('phone', this.adminForm.value.phoneNumber);
        formData.append('town', this.adminForm.value.town);
        formData.append('address', this.adminForm.value.address);
        formData.append('function', this.adminForm.value.adminFunction);


        if (this.image != null || this.image != undefined) // envoyer  une image  uniquement si  y'a eu  une image  !
            formData.append('image', this.image);


        this.adminService.signUpAdmin(formData).subscribe((data) => {
            if (data != undefined && data.message === "ADMIN ADDED SUCCESSFULLY") {
                this.isLoading = false;
                this.sendConfirmationToken();
            }
        });
    }


    sendConfirmationToken() {
        this.sharedService.sendToken(this.adminForm.value.email).subscribe((data) => {
            const result = this.matDialog.open(SuccessfulDialogComponent, {
                data: {message: " Votre  Inscription  est  prise en compte , un  Email  vous a éte  envoyer  pour vérifier  votre  Adresse "},
                width: '40%',
            });
            result.afterClosed().subscribe((result) => {
                this.router.navigate(['cantine/signIn']).then();
            });


        });
    }


    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        this.image = (target.files as FileList)[0];
    }

    get f(): { [key: string]: AbstractControl } {
        return this.adminForm.controls;
    }


}
