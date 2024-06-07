import {Component, OnInit} from '@angular/core';
import {connection} from "../../shared-module/functions/connection";
import {Router} from "@angular/router";
import {IConstantsURL} from "../../shared-module/constants/IConstantsURL";
import {Student} from "../../shared-module/models/student";
import {StudentService} from "../student.service";

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  providers: [StudentService]
})
export class ProfileComponent implements OnInit {

  constructor(private router: Router, private studentService: StudentService) {
  }

  student: Student = new Student();

  ngOnInit() {
    let authObject = connection.getAuthObjectFromLocalStorage();
    if (authObject == null) {
      localStorage.clear();
      this.router.navigate([IConstantsURL.STUDENT_SIGN_IN]).then();
      return;
    }

    this.studentService.getStudentByID(authObject.id).subscribe((result) => {
      this.student = result;
    })


  }

  updateProfile() {
     /*TODO  :  to  do  after */
  }

  goBack() {
    this.router.navigate([IConstantsURL.HOME_URL]).then();
  }
}
