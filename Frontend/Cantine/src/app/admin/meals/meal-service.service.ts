import { Injectable } from '@angular/core';
//import {environment} from "../../../environments/environment";
import {HttpClient, HttpErrorResponse, HttpStatusCode} from "@angular/common/http";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {catchError, throwError} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {ValidatorDialogComponent} from "./dialogs/validator-dialog/validator-dialog.component";
import {ExceptionDialogComponent} from "./dialogs/exception-dialog/exception-dialog.component";
import {ErrorResponse} from  "../../sharedmodule/models/ErrorResponse"
@Injectable()
export class MealServiceService {
 // private apiUrl = environment.apiUrl;

  private BASIC_ENDPOINT = "http://localhost:8080/cantine/" + 'api/admin/meals';


  private  ADD_MEAL_URL = this.BASIC_ENDPOINT  + '/add';
  private  GET_MEAL_BY_ID_URL = this.BASIC_ENDPOINT  + '/get';
  constructor(private httpClient: HttpClient , private matDialog: MatDialog , private  router : Router) { }

    getMealById(id: number) {
        return this.httpClient.get(this.GET_MEAL_BY_ID_URL + '/' + id).pipe(
            catchError( (error) => this.handleError(error))
        );
  }


    addMeal(meal: FormData ) { //  we have  to  add  a  token  after
    return this.httpClient.post <string>(this.ADD_MEAL_URL, meal).pipe(
         catchError( (error) => this.handleError(error))
    );
  }




    private handleError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let  errorMessage = errorObject.exceptionMessage;

         if  (error.status == HttpStatusCode.BadRequest || error.status == HttpStatusCode.NotAcceptable){
               errorMessage = "Veuillez  vérifier  les  données  saisies  !";
             this.openDialog(errorMessage,  error.status);

         }else if (error.status == HttpStatusCode.Conflict) {
                this.openDialog(errorMessage, error.status);
         }
         else if  (error.status == HttpStatusCode.InternalServerError){
             errorMessage =  "Une  erreur  interne  est  survenue  !"
              this.openDialog(errorMessage, error.status);
         }
        else if  (error.status == HttpStatusCode.NotFound){
             errorMessage =  "Ce  plat  n'existe  pas  !"
             this.openDialog(errorMessage, error.status);
         }
        else {
             errorMessage =  "Une  erreur  est  survenue  !"
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
                 this.router.navigate(['/admin/meals'] , { queryParams: { reload: 'true' } });
             }
             else {
                 /* TODO  remove THE  Token  */
                 //this.router.navigate(['/cantine/home'] , { queryParams: { reload: 'true' } });
             }

         });

   }


}
