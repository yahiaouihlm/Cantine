import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {Observable, of} from "rxjs";
import {User} from "../../../sharedmodule/models/user";
import {StudentsManagementService} from "../students-management.service";
import {NavigationExtras, Router} from "@angular/router";
import Validation from "../../../sharedmodule/functions/validation";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";

@Component({
  selector: 'app-students-handler',
  templateUrl:'students-handler.component.html' ,
  styleUrls: ['../../../../assets/styles/students-handler.component.scss'],
  providers : [StudentsManagementService]
})
export class StudentsHandlerComponent {
   student  =   new User();
  submitted =  false ;

  isLoaded = false ;

  found =  false ;
  studentSeeked: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
  });

  constructor(private studentsManagementService :  StudentsManagementService,  private router : Router) {
  }


  validate () {
    this.submitted = true;
    if (this.studentSeeked.invalid) {
      return;
    }
    this.isLoaded =  true ;
    this.getStudent();
  }

  getStudent() {
    this.studentsManagementService.getStudentByEmail(this.studentSeeked.value.email).subscribe({
        next: (student) => {
            this.student = student;
            this.isLoaded =  false ;
            this.found =  true ;
            },
        error: (error) => {
            this.isLoaded =  false ;
        }
    })


  }

  goToStudentProfile() {
    const navigationExtras: NavigationExtras = {
        state: {
            student: this.student
        }
    }
    this.router.navigate([IConstantsURL.ADMIN_STUDENT_PROFILE_URL],  navigationExtras).then(r => window.location.reload());
  }

  get f(): { [key: string]: AbstractControl } {
    return this.studentSeeked.controls;
  }

}
