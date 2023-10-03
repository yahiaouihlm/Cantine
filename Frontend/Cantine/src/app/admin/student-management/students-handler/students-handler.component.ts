import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-students-handler',
  templateUrl:'students-handler.component.html' ,
  styles: [
  ]
})
export class StudentsHandlerComponent {

  submitted =  false ;
  studentSeeked: FormGroup = new FormGroup({
    firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    birthDate: new FormControl('', [Validators.required]),
  });

  isLoaded = false ;

  validate () {
    this.submitted = true;
    if (this.studentSeeked.invalid) {
      return;
    }
    this.isLoaded =  true ;
  }





  get f(): { [key: string]: AbstractControl } {
    return this.studentSeeked.controls;
  }

}
