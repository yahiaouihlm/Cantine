import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../sharedmodule/functions/validation";
import {Observable, of} from "rxjs";
import {StudentClass} from "../../../sharedmodule/models/studentClass";
import {StudentDashboardService} from "../student-dashboard.service";
import {MatDialog} from "@angular/material/dialog";
import {SharedService} from "../../../sharedmodule/shared.service";
import {SuccessfulDialogComponent} from "../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {Router} from "@angular/router";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-sign-in',
    templateUrl: './sign-up.component.html',
    styles: [], providers: [StudentDashboardService]
})
export class SignUpComponent implements OnInit {


    private WOULD_YOU_LIKE_TO_SIGN_UP = "Voulez-vous vous inscrire ?";
    private STUDENT_SIGN_UP_SUCCESSFULLY = "Votre inscription a été effectuée avec succès, un email vous a été envoyé pour vérifier votre adresse";
    image!: File;
    submitted = false;
    isLoading = false;
    studentClass$: Observable<StudentClass[]> = of([]);
    studentForm: FormGroup = new FormGroup({
            firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
            lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
            email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
            password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
            confirmPassword: new FormControl("", [Validators.required]),
            birthDate: new FormControl('', [Validators.required]),
            phoneNumber: new FormControl('', [Validators.pattern(Validation.FRENCH_PHONE_REGEX)]),
            town: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(3)]),
            studentClass: new FormControl('', [Validators.required]),
            image: new FormControl(''),

        },
        {
            validators: [Validation.match('password', 'confirmPassword')]
        }
    );


    constructor(private studentService: StudentDashboardService, private matDialog: MatDialog, private sharedService: SharedService, private router: Router) {
    }

    ngOnInit(): void {
        Malfunctions.checkUserConnection(this.router);
        this.studentClass$ = this.studentService.getAllStudentClass();
    }


    onSubmit() {
        this.submitted = true;

        if (this.studentForm.invalid) {
            return;
        }
        this.isLoading = true;
        // check if email exist
        this.sharedService.checkExistenceOfEmail(this.studentForm.value.email).subscribe({
                next: () => {
                    this.studentForm.setErrors({existingEmail: false});
                    this.inscription();
                },
                error: (error) => {
                    this.studentForm.controls['email'].setErrors({existingEmail: true});
                    this.isLoading = false;
                },
            }
        );

    }

    inscription() {
        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_SIGN_UP},
            width: '40%',
        });
        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.signUp();
            } else {
                this.isLoading = false;
                return;
            }
        });
    }

    signUp() {
        const formData = new FormData();
        formData.append('firstname', this.studentForm.value.firstName);
        formData.append('lastname', this.studentForm.value.lastName);
        formData.append('email', this.studentForm.value.email);
        formData.append('password', this.studentForm.value.password);
        formData.append('birthdateAsString  ', this.studentForm.value.birthDate);
        formData.append('studentClass', this.studentForm.value.studentClass);
        formData.append('town', this.studentForm.value.town);

        if (this.studentForm.value.phoneNumber != null || this.studentForm.value.phoneNumber != undefined)
            formData.append('phone', this.studentForm.value.phoneNumber);

        if (this.image != null || this.image != undefined) // envoyer  une image  uniquement si  y'a eu  une image  !
            formData.append('image', this.image);

        this.studentService.signUpStudent(formData).subscribe({
            next: (data) => {
                const result = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: this.STUDENT_SIGN_UP_SUCCESSFULLY},
                    width: '40%',
                });
                result.afterClosed().subscribe((result) => {
                    this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(() => window.location.reload());
                });
                this.isLoading = false;
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
        return this.studentForm.controls;
    }

}

