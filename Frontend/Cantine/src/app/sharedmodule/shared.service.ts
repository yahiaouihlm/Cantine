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

  private BASIC_ENDPOINT = "http://localhost:8080/cantine/" + 'admin/adminDashboard';
  private SEND_CONFIRMATION_TOKEN = this.BASIC_ENDPOINT + '/sendToken';

  sendToken(email: string) {

    const params = new HttpParams().set('email', email);
    return this.httpClient.post<NormalResponse>(this.SEND_CONFIRMATION_TOKEN, params).pipe(
        catchError((error) => this.handleError(error))
    );
  }

  private handleError(error: HttpErrorResponse) {
    const errorObject = error.error as ErrorResponse;
    let errorMessage = errorObject.exceptionMessage;


    if (error.status == HttpStatusCode.InternalServerError) {
      this.openDialog("Unkwon Error   has  been occured  ", error.status);
    } else {
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
