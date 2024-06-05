import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {IConstantsURL} from "../constants/IConstantsURL";
import {log} from "@angular-devkit/build-angular/src/builders/ssr-dev-server";
import {connection} from "../functions/connection";

@Component({
  selector: 'app-main-core-cantine',
  templateUrl: './main-core-cantine.component.html',
  styleUrls: ['./main-core-cantine.component.scss'],
})
export class MainCoreCantineComponent implements OnInit {

  constructor(private router: Router) {
  }

  studentImage: string | undefined;

  ngOnInit(): void {

    if (connection.checkStudentConnection()) {
      let auth = connection.getAuthObjectFromLocalStorage();
      if (auth != null) {
        this.studentImage = auth.image;
      } else {
        this.router.navigate([IConstantsURL.STUDENT_SIGN_IN]).then()
        localStorage.clear();
      }
    } else {
      this.router.navigate([IConstantsURL.STUDENT_SIGN_IN]).then()
      localStorage.clear();
    }
  }


  goToMeals() {
    this.router.navigate([IConstantsURL.MEALS_URL]).then();
  }

  logout() {
    localStorage.clear();
    this.router.navigate([IConstantsURL.STUDENT_SIGN_IN]).then();
  }
}
