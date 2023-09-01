import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams, HttpStatusCode} from "@angular/common/http";
import {NormalResponse} from "./models/NormalResponse";
import {catchError, throwError} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {ErrorResponse} from "./models/ErrorResponse";
import {ExceptionDialogComponent} from "./dialogs/exception-dialog/exception-dialog.component";

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  constructor(private httpClient: HttpClient, private matDialog: MatDialog) { }

  private CHECK_EXISTENCE_OF_EMAIL = "http://localhost:8080/cantine/superAdmin" + '/ExistingEmail';
  private BASIC_ENDPOINT = "http://localhost:8080/cantine/";

  private SEND_CONFIRMATION_TOKEN = this.BASIC_ENDPOINT  + 'admin/adminDashboard' + '/sendToken';
  private SEND_STUDENT_CONFIRMATION_TOKEN = this.BASIC_ENDPOINT  +  "student/sendToken" ;
  checkExistenceOfEmail(email: string) {
    const params = new HttpParams().set('email', email);
    return this.httpClient.get<NormalResponse>(this.CHECK_EXISTENCE_OF_EMAIL, {params}).pipe(
        catchError((error) => this.handleErrors(error))
    );
  }

  private handleErrors(error: HttpErrorResponse) {
    const errorObject = error.error as ErrorResponse;
    let errorMessage = errorObject.exceptionMessage;

    if (error.status == HttpStatusCode.Conflict) {
      return throwError(() => new Error(errorMessage));

    } else {

      this.openDialog("Unkwon Error   has  been occured  ", error.status);
      return throwError(() => new Error(errorMessage));
    }

  }
  sendToken(email: string) {

    const params = new HttpParams().set('email', email);
    return this.httpClient.post<NormalResponse>(this.SEND_CONFIRMATION_TOKEN, params).pipe(
        catchError((error) => this.handleError(error))
    );
  }


  sendTokenStudent(email: string) {
    const params = new HttpParams().set('email', email);
    return this.httpClient.post<NormalResponse>(this.SEND_STUDENT_CONFIRMATION_TOKEN, params).pipe(
        catchError((error) => this.handleError(error))
    );
  }

  private handleError(error: HttpErrorResponse) {
    const errorObject = error.error as ErrorResponse;
    let errorMessage = errorObject.exceptionMessage;


    if (error.status == HttpStatusCode.InternalServerError) {
      this.openDialog(" Une erreur s'est produite pendant l'envoi de l'email de confirmation", error.status);
    } else {
      console.log(error.message)
      this.openDialog(errorMessage, error.status);
    }

    return throwError(() => new Error(errorMessage));

  }


  private openDialog(message: string, httpError: HttpStatusCode): void {
    const result = this.matDialog.open(ExceptionDialogComponent, {
      data: {message: message},
      width: '40%',
    });

    result.afterClosed().subscribe((confirmed: boolean) => {
      if (httpError == HttpStatusCode.BadRequest || httpError == HttpStatusCode.NotAcceptable || httpError == HttpStatusCode.Conflict || httpError == HttpStatusCode.NotFound) {
        //  this.router.navigate(['/admin/menus'] , { queryParams: { reload: 'true' } });
        console.log("je suis  la  dans  le  if  ")
      } else {
        console.log("je suis  la ")
        /* TODO  remove THE  Token  */
        //this.router.navigate(['/cantine/home'] , { queryParams: { reload: 'true' } });
      }

    });

  }



}
