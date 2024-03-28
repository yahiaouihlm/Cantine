import {Component, OnDestroy, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../sharedmodule/functions/validation";
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../../../sharedmodule/models/user";
import {SharedService} from "../../../sharedmodule/shared.service";
import {Observable, of, Subscription} from "rxjs";
import {StudentDashboardService} from "../student-dashboard.service";
import {StudentClass} from "../../../sharedmodule/models/studentClass";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {SuccessfulDialogComponent} from "../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    styles: [],
    providers: [StudentDashboardService]
})
export class ProfileComponent implements OnInit, OnDestroy {
    private WOULD_YOU_LIKE_TO_UPDATE = "Voulez-vous mettre à jour votre profile ?";

    constructor(private route: ActivatedRoute, private sharedService: SharedService, private router: Router, private studentService: StudentDashboardService, private matDialog: MatDialog) {
    }

    user: User = new User();
    submitted!: boolean;
    private queryParamsSubscription: Subscription | undefined;
    private getStudentByIdSubscription: Subscription | undefined;

    studentClass$: Observable<StudentClass[]> = of([]);
    isLoading = false;
    touched: boolean = false;

    image!: File
    studentUpdated: FormGroup = new FormGroup({
        firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
        lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
        email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
        birthDate: new FormControl('', [Validators.required]),
        phoneNumber: new FormControl('', [Validators.pattern(Validation.FRENCH_PHONE_REGEX)]),
        town: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(3)]),
        studentClass: new FormControl('', [Validators.required]),
        image: new FormControl(''),
    });

    ngOnInit(): void {

        this.studentUpdated.disable();
        this.studentClass$ = this.studentService.getAllStudentClass();
        this.queryParamsSubscription = this.route.queryParams.subscribe(params => {
            const id = params['id'];
            if (id) {
                this.getStudentByIdSubscription = this.sharedService.getStudentById(id).subscribe((response) => {
                    this.user = response;
                    this.matchFormsValue();
                });
            } else {
                localStorage.clear();
                this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            }

        });

    }


    onSubmit(): void {
        this.submitted = true;
        if (this.studentUpdated.invalid || !this.studentUpdated.touched) {
            return;
        }

        this.isLoading = true;

        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_UPDATE},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.updateStudent();
            } else {
                this.isLoading = false;
                return;
            }

        });
    }


    updateStudent() {
        const formDataStudent = new FormData();
        formDataStudent.append('id', this.user.uuid.toString());
        formDataStudent.append('firstname', this.studentUpdated.value.firstName);
        formDataStudent.append('lastname', this.studentUpdated.value.lastName);
        formDataStudent.append('birthdateAsString  ', this.studentUpdated.value.birthDate);
        formDataStudent.append('studentClass', this.studentUpdated.value.studentClass);
        formDataStudent.append('town', this.studentUpdated.value.town);

        if (this.studentUpdated.value.phoneNumber != null || this.studentUpdated.value.phoneNumber != undefined)
            formDataStudent.append('phone', this.studentUpdated.value.phoneNumber);

        if (this.image != null || this.image != undefined) // envoyer  une image  uniquement si  y'a eu  une image  !
            formDataStudent.append('image', this.image);


        this.studentService.updateStudent(formDataStudent).subscribe({
            next: (response) => {
                this.isLoading = false;
                const result = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: " Votre profile a été mis à jour avec succès ! "},
                    width: '40%',
                });
                result.afterClosed().subscribe((result) => {
                    window.location.reload();
                });
            },
            error: (error) => {
                this.isLoading = false;
            }
        });


    }


    matchFormsValue() {
        this.studentUpdated.patchValue({
            firstName: this.user.firstname,
            lastName: this.user.lastname,
            email: this.user.email,
            birthDate: this.user.birthdate,
            phoneNumber: this.user.phone,
            town: this.user.town,
            studentClass: this.user.studentClass,
        });

    }

    ngOnDestroy() {
        if (this.queryParamsSubscription) {
            this.queryParamsSubscription.unsubscribe();
        }
        if (this.getStudentByIdSubscription) {
            this.getStudentByIdSubscription.unsubscribe();
        }
    }

    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        this.image = (target.files as FileList)[0];
    }

    modify() {
        this.touched = true;
        this.studentUpdated.enable();
        this.studentUpdated.get('email')?.disable()
    }

    cancel() {
        this.studentUpdated.disable();
        this.touched = false;
    }

    get f(): { [key: string]: AbstractControl } {
        return this.studentUpdated.controls;
    }
}
