import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {User} from "../../sharedmodule/models/user";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";
import {catchError, throwError} from "rxjs";
import {MatDialog} from "@angular/material/dialog";

@Injectable()
export class StudentsManagementService {

  private  BASIC_ADMIN_URL =  "http://localhost:8080/cantine/admin/adminDashboard/works";
  private  GET_STUDENTS =  this.BASIC_ADMIN_URL + "/getStudent"

  private GET_STUDENT_BY_ID = this.BASIC_ADMIN_URL + "/GetStudentById";
  constructor(private   httpClient:HttpClient , private matDialog :  MatDialog) { }



  getStudentById(id : string) {
    let token  =  Malfunctions.getTokenFromLocalStorage();
    const headers = new HttpHeaders().set('Authorization', token);
    const params =  new  HttpParams().set('studentId' , id.toString())
    return this.httpClient.get<User>( this.GET_STUDENT_BY_ID, { headers : headers , params : params }).pipe(
        catchError((error) => this.handleError(error))
    );
  }
  getStudents( user  :  User ) {
    let token  =  Malfunctions.getTokenFromLocalStorage();
    const headers = new HttpHeaders().set('Authorization', token);
    const params =  new  HttpParams().set('firstname' , user.firstname)
                                     .set('lastname', user.lastname)
                                     .set('birthdateAsString', user.birthdate.toString())
    return this.httpClient.get<User[]>(this.GET_STUDENTS , { params : params , headers : headers });
  }


  private handleError(error: HttpErrorResponse) {
    const errorObject = error.error as ErrorResponse;
    let errorMessage = errorObject.exceptionMessage;

    if (error.status == HttpStatusCode.InternalServerError) {
      errorMessage = "Une  erreur    est  survenue  !"
      new DialogErrors(this.matDialog).openDialog(errorMessage, error.status);
    } else {
      new DialogErrors(this.matDialog).openDialog(errorMessage, error.status);
    }
    return throwError(() => new Error(error.error));

  }

}
