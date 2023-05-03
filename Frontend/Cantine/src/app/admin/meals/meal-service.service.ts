import { Injectable } from '@angular/core';
//import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class MealServiceService {
 // private apiUrl = environment.apiUrl;
  private BASIC_ENDPOINT = "http://localhost:8080/cantine" + 'admin/api/meals';


  private  ADD_MEAL_URL = this.BASIC_ENDPOINT  + '/add';
  constructor(private httpClient: HttpClient) { }
  addMeal(meal: Object ) { //  we have  to  add  a  token  after
    return this.httpClient.post <string>(this.ADD_MEAL_URL, meal);
  }
}
