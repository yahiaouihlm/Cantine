import { Injectable } from '@angular/core';
//import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class MealServiceService {
 // private apiUrl = environment.apiUrl;
  //private BASIC_ENDPOINT = this.apiUrl + 'admin/meals';


  //private  ADD_MEAL_URL = this.BASIC_ENDPOINT  + '/add';
  constructor(private httpClient: HttpClient) { }


}
