import { Injectable } from '@angular/core';
//import {environment} from "../../../environments/environment";
import {HttpClient, HttpErrorResponse, HttpStatusCode} from "@angular/common/http";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {catchError, throwError} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {ValidatorDialogComponent} from "./dialogs/validator-dialog/validator-dialog.component";
import {ExceptionDialogComponent} from "./dialogs/exception-dialog/exception-dialog.component";

@Injectable()
export class MealServiceService {
 // private apiUrl = environment.apiUrl;

  private BASIC_ENDPOINT = "http://localhost:8080/cantine/" + 'api/admin/meals';


  private  ADD_MEAL_URL = this.BASIC_ENDPOINT  + '/add';
  private  GET_MEAL_BY_ID_URL = this.BASIC_ENDPOINT  + '/get';
  constructor(private httpClient: HttpClient , private matDialog: MatDialog , private  router : Router) { }
  addMeal(meal: FormData ) { //  we have  to  add  a  token  after
    return this.httpClient.post <string>(this.ADD_MEAL_URL, meal).pipe(
         catchError( (error) => this.handleError(error))
    );
  }

/*
  getMealById(id: number) {
       return this.httpClient.get(this.GET_MEAL_BY_ID_URL + '/' + id).pipe(
           catchError( (error) => this.handleError(error))
  }
*/

    private handleError(error: HttpErrorResponse) {
         if  (error.status == HttpStatusCode.BadRequest || error.status == HttpStatusCode.NotAcceptable){
             this.openDialog("Veuillez  vérifier  les  données  saisies  !", error.status);
         }else if (error.status == HttpStatusCode.Conflict) {
             console.log("je suis dans le  conflict  error  handler");
                this.openDialog(error.message, error.status);
         }
         else if  (error.status == HttpStatusCode.InternalServerError){
              this.openDialog("Une  erreur  interne  est  survenue  !", error.status);
         }
        else if  (error.status == HttpStatusCode.NotFound){
                this.openDialog("Ce  plat  n'existe  pas  !", error.status);
         }
        else {
            this.openDialog("Une  erreur  est  survenue  !", error.status);
         }
        return throwError(() => new Error('Something bad happened; please try again later.'));

    }




   private openDialog(message: string , httpError :  HttpStatusCode ): void {
       const result = this.matDialog.open(ExceptionDialogComponent, {
           data: {message: message},
           width: '40%',
       });

         result.afterClosed().subscribe((confirmed: boolean) => {
             if (httpError == HttpStatusCode.BadRequest || httpError == HttpStatusCode.NotAcceptable || httpError== HttpStatusCode.Conflict || httpError== HttpStatusCode.NotFound){

                 //this.router.navigate(['/admin/meals'] , { queryParams: { reload: 'true' } });
             }
             else {
                 /* TODO  remove THE  Token  */
                 //this.router.navigate(['/cantine/home'] , { queryParams: { reload: 'true' } });
             }

         });

   }


}
