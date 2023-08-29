import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {Adminfunction} from "../../sharedmodule/models/adminfunction";
import {StudentClass} from "../../sharedmodule/models/studentClass";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";

@Injectable()
export class StudentDashboardService {

  private  BASIC_ENDPOINT = "http://localhost:8080/cantine/student/";

  private  GET_ALL_STUDENT_CLASS = this.BASIC_ENDPOINT + 'getAllStudentClass';
  constructor(private httpClient: HttpClient, private matDialog: MatDialog, private router: Router) { }


  signUpStudent(student: FormData) {
    return this.httpClient.post<NormalResponse>(this.BASIC_ENDPOINT, student);
  }

  getAllStudentClass () {
    return this.httpClient.get<StudentClass[]>(this.GET_ALL_STUDENT_CLASS);
  }
}
