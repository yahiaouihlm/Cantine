import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams, HttpStatusCode} from "@angular/common/http";
import {Meal} from "../sharedmodule/models/meal";
import {Menu} from "../sharedmodule/models/menu";
import {Login} from "../sharedmodule/models/login";
import {ErrorResponse} from "../sharedmodule/models/ErrorResponse";
import {catchError, throwError} from "rxjs";
import {NormalResponse} from "../sharedmodule/models/NormalResponse";

@Injectable()
export class CoreCantineService {
  private  BASIC_MEAL_URL = "http://localhost:8080/cantine/api/" ;
  private  USER_AUTHENTICATION_ENDPOINT =  "http://localhost:8080/"+ 'login';
  private  GET_ALL_MEALS_ENDPOINT = this.BASIC_MEAL_URL +  'meals/getAll'
  private GET_ALL_MENUS_ENDPOINT =  this.BASIC_MEAL_URL + 'menus/getAll';

    private SEND_CONFIRMATION_TOKEN = "http://localhost:8080/cantine/"+ '/sendToken';

  constructor(private httpClient: HttpClient) { }



    sendToken(email: string) {

        const params = new HttpParams().set('email', email);
        return this.httpClient.post<NormalResponse>(this.SEND_CONFIRMATION_TOKEN, params);
    }
    userAuthentication(login:Login) {
        return this.httpClient.post(this.USER_AUTHENTICATION_ENDPOINT, login );
    }

   getAllMeals () {
    return this.httpClient.get<Meal[]>(this.GET_ALL_MEALS_ENDPOINT)
  }

  getAllMenus (){
      return this.httpClient.get<Menu[]>(this.GET_ALL_MENUS_ENDPOINT)
  }





}
