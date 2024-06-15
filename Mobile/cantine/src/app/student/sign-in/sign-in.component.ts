import {Component, forwardRef, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormControl, FormGroup, NG_VALUE_ACCESSOR, Validators} from "@angular/forms";
import {IConstantsMessages} from "../../shared-module/constants/IConstantsMessages";
import {connection} from "../../shared-module/functions/connection";
import {Router} from "@angular/router";
import {IConstantsURL} from "../../shared-module/constants/IConstantsURL";
import {StudentService} from "../student.service";
import {Login} from "../../shared-module/models/login";
import {HttpStatusCode} from "@angular/common/http";

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss'],
  providers: [StudentService]
})
export class SignInComponent implements OnInit {

  wrongCredentials = false;
  disabled_account = true;
  isLoading = false;
  submitted = false;

  constructor(private router: Router, private studentService: StudentService) {
  }

  ngOnInit(): void {
    if (connection.checkStudentConnection()) {
      this.router.navigate([IConstantsURL.HOME_URL]).then()
    }
  }


  public signIn = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(IConstantsMessages.EMAIL_REGEX)]),
    password: new FormControl('', [Validators.required]),
  });


  onSubmit() {

    this.submitted = true;
    if (this.signIn.invalid) {
      return;
    }
    this.isLoading = true;
    this.studentAuthentication();
  }


  studentAuthentication() {
    let login: Login = new Login();
    login.email = this.signIn.controls['email'].value;
    login.password = this.signIn.controls['password'].value;

    this.studentService.signIn(login).subscribe({
      next: (response) => {
        this.disabled_account = false;
        this.wrongCredentials = false;
        this.isLoading = false
        if (response.role === IConstantsMessages.STUDENT_ROLE) {
          const authObjectJSON = JSON.stringify(response);
          localStorage.setItem('authObject', authObjectJSON);
          this.router.navigate([IConstantsURL.HOME_URL]).then(() => {
          });
        }
      },
      error: (error) => {
        if (error.status === HttpStatusCode.Forbidden && error.error.message === IConstantsMessages.STUDENT_DISABLED_ACCOUNT) {
          this.disabled_account = true;
          this.wrongCredentials = false;
        } else if (error.status === HttpStatusCode.Unauthorized && error.error.message === IConstantsMessages.STUDENT_WRONG_CREDENTIALS) {
          this.wrongCredentials = true;
          this.disabled_account = false;
        } else {
          this.wrongCredentials = true
        }
        this.isLoading = false
      }
    })
  }


  get f(): { [key: string]: AbstractControl } {
    return this.signIn.controls;
  }


}
