import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpStatusCode} from "@angular/common/http";
import {Meal} from "../sharedmodule/models/meal";
import {Menu} from "../sharedmodule/models/menu";
import {Login} from "../sharedmodule/models/login";
import {ErrorResponse} from "../sharedmodule/models/ErrorResponse";
import {throwError} from "rxjs";

@Injectable()
export class CoreCantineService {
  private  BASIC_MEAL_URL = "http://localhost:8080/cantine/api/" ;
  private  USER_AUTHENTICATION_ENDPOINT =  "http://localhost:8080/"+ 'login';
  private  GET_ALL_MEALS_ENDPOINT = this.BASIC_MEAL_URL +  'meals/getAll'
  private GET_ALL_MENUS_ENDPOINT =  this.BASIC_MEAL_URL + 'menus/getAll';
  constructor(private httpClient: HttpClient) { }


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
