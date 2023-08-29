import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../sharedmodule/functions/validation";
import {Observable, of} from "rxjs";
import {Adminfunction} from "../../../sharedmodule/models/adminfunction";
import {StudentClass} from "../../../sharedmodule/models/studentClass";
import {StudentDashboardService} from "../student-dashboard.service";
import {MatDialog} from "@angular/material/dialog";
import {SharedService} from "../../../sharedmodule/shared.service";
import {SuccessfulDialogComponent} from "../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-up.component.html',
  styles: [
  ] , providers: [StudentDashboardService]
})
export class SignUpComponent  implements   OnInit {

    image!: File;
    submitted = false;
    existEmail = false;

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


  constructor( private  studentService: StudentDashboardService, private matDialog: MatDialog , private sharedService: SharedService) {
  }

    ngOnInit(): void {
       this.studentClass$ =  this.studentService.getAllStudentClass();
    }



    onSubmit() {

        this.submitted = true;
        this.existEmail = false;
        if (this.studentForm.invalid) {
            return;
        }
        // check if email exist
        this.sharedService.checkExistenceOfEmail(this.studentForm.value.email).subscribe( {
                next: (data) => {
                    this.existEmail =  false;
                    this.signUp();
                } ,
                error: (error) => {
                    this.existEmail =  true;
                },
            }
        );

    }


    signUp() {
        const formData = new FormData();
        formData.append('firstname', this.studentForm.value.firstName);
        formData.append('lastname', this.studentForm.value.lastName);
        formData.append('email', this.studentForm.value.email);
        formData.append('password', this.studentForm.value.password);
        formData.append('birthDate', this.studentForm.value.birthDate);
        formData.append('studentClass', this.studentForm.value.studentClass);


        if (this.studentForm.value.phoneNumber != null || this.studentForm.value.phoneNumber != undefined)
            formData.append('phoneNumber', this.studentForm.value.phoneNumber);

        if (this.image != null || this.image != undefined) // envoyer  une image  uniquement si  y'a eu  une image  !
            formData.append('image', this.image);
        this.studentService.signUpStudent(formData).subscribe(data => {

            if (data != undefined && data.message === "STUDENT SAVED SUCCESSFULLY") {
                const result = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: " Votre  Inscription  est  prise en compte , un  Email  vous a éte  envoyer  pour vérifier  votre  Adresse "},
                    width: '40%',
                });
                result.afterClosed().subscribe((result) => {
                    // this.router.navigate(['/admin/meals'] ,  { queryParams: { reload: 'true' } })
                });

            }

        });


    }


    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        const file: File = (target.files as FileList)[0]
        this.image = file;
    }

    get f(): { [key: string]: AbstractControl } {
        return this.studentForm.controls;
    }


}

