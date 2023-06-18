import { Injectable } from '@angular/core';
//import {environment} from "../../../environments/environment";
import {HttpClient, HttpErrorResponse, HttpStatusCode} from "@angular/common/http";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {catchError, throwError} from "rxjs";

@Injectable()
export class MealServiceService {
 // private apiUrl = environment.apiUrl;

  private BASIC_ENDPOINT = "http://localhost:8080/cantine/" + 'api/admin/meals';


  private  ADD_MEAL_URL = this.BASIC_ENDPOINT  + '/add';
  private  GET_MEAL_BY_ID_URL = this.BASIC_ENDPOINT  + '/get';
  constructor(private httpClient: HttpClient) { }
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
             console.log("error  400  or  406")
         }else if (error.status == HttpStatusCode.Conflict) {
             console.log(" The  Meal  Already  Exists  !");
         }
         else if  (error.status == HttpStatusCode.InternalServerError){
             console.log("error  500")
         }
        else if  (error.status == HttpStatusCode.NotFound){
             console.log("error  404")
         }
        else {
                console.log(error)
         }
        return throwError(() => new Error('Something bad happened; please try again later.'));

    }
}
