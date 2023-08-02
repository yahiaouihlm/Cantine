import { Injectable } from '@angular/core';
import {Menu} from "../../sharedmodule/models/menu";
import {HttpClient, HttpErrorResponse, HttpStatusCode} from "@angular/common/http";
import {catchError, throwError} from "rxjs";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {ExceptionDialogComponent} from "../../sharedmodule/dialogs/exception-dialog/exception-dialog.component";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";

@Injectable()
export class MenusService {

  private BASIC_ENDPOINT = "http://localhost:8080/cantine/" + 'api/admin/menus';
  private  ADD_MENU_URL = this.BASIC_ENDPOINT  + '/add';
  constructor(private httpClient: HttpClient , private matDialog: MatDialog , private  router : Router ) { }



  sendMenu (menu :  FormData ){
    return this.httpClient.post <NormalResponse>(this.ADD_MENU_URL, menu).pipe(
        catchError( (error) => this.handleError(error))
    );
  }

  private handleError(error: HttpErrorResponse) {
    const errorObject = error.error as ErrorResponse;
    let  errorMessage = errorObject.exceptionMessage;

    if  (error.status == HttpStatusCode.BadRequest || error.status == HttpStatusCode.NotAcceptable){
      this.openDialog(errorMessage,  error.status);
      console.log("je   suis dans  le  BadRequest ou    dans le  not  acceptable");
    }
    else if  (error.status == HttpStatusCode.Conflict){
      this.openDialog(errorMessage,  error.status);
    }

    else if  (error.status == HttpStatusCode.Forbidden){
      this.openDialog(errorMessage,  error.status);
    }

    else if  (error.status == HttpStatusCode.InternalServerError){
      errorMessage =  "Une  erreur  interne  est  survenue  !"
      this.openDialog(errorMessage, error.status);
    }
   /* else if  (error.status == HttpStatusCode.NotFound){
      errorMessage =  "Ce  plat  n'existe  pas  ! \n  il ce peut qu'il a été supprimé  !"
      this.openDialog(errorMessage, error.status);
    }*/
    else {
      errorMessage =  "Une  erreur  est  survenue  !"
      console.log(error.status)
      this.openDialog(errorMessage, error.status);
    }
    return throwError(() => new Error(errorMessage));

  }




  private openDialog(message: string , httpError :  HttpStatusCode ): void {
    const result = this.matDialog.open(ExceptionDialogComponent, {
      data: {message: message},
      width: '40%',
    });

    result.afterClosed().subscribe((confirmed: boolean) => {
      if (httpError == HttpStatusCode.BadRequest || httpError == HttpStatusCode.NotAcceptable || httpError== HttpStatusCode.Conflict || httpError== HttpStatusCode.NotFound){
      //  this.router.navigate(['/admin/menus'] , { queryParams: { reload: 'true' } });
      }
      else {
        console.log("je suis  la ")
        /* TODO  remove THE  Token  */
        //this.router.navigate(['/cantine/home'] , { queryParams: { reload: 'true' } });
      }

    });

  }

}
