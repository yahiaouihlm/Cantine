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
    birthdate: new FormControl('', [Validators.required]),
  });








  get f(): { [key: string]: AbstractControl } {
    return this.studentSeeked.controls;
  }

}
