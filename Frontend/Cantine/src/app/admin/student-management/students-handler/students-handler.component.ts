import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {Observable, of} from "rxjs";
import {User} from "../../../sharedmodule/models/user";
import {StudentsManagementService} from "../students-management.service";
import {NavigationExtras, Router} from "@angular/router";

@Component({
  selector: 'app-students-handler',
  templateUrl:'students-handler.component.html' ,
  styles: [],
  providers : [StudentsManagementService]
})
export class StudentsHandlerComponent {
  students$  :  Observable <User[]>  =  of([]);
  submitted =  false ;

  isLoaded = false ;

  studentSeeked: FormGroup = new FormGroup({
    firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    birthDate: new FormControl('', [Validators.required]),
  });

  constructor(private studentsManagementService :  StudentsManagementService,  private router : Router) {
  }


  validate () {
    this.submitted = true;
    if (this.studentSeeked.invalid) {
      return;
    }
    this.isLoaded =  true ;
    this.getStudents();
  }

  getStudents() {
    let user  =   new User();
    user.firstname = this.studentSeeked.value.firstName ;
    user.lastname=this.studentSeeked.value.lastName ;
    user.birthdate =  this.studentSeeked.value.birthDate;
    this.students$ = this.studentsManagementService.getStudents(user)
    this.isLoaded =  false ;

  }

  goToStudentProfile(studentId :  number) {
    const navigationExtras: NavigationExtras = {
      queryParams: { studentId: studentId }
    };

    this.router.navigate(["cantine/admin/students/profile"],  navigationExtras);
  }

  get f(): { [key: string]: AbstractControl } {
    return this.studentSeeked.controls;
  }

}
