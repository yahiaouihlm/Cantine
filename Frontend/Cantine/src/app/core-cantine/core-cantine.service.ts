import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams, HttpStatusCode} from "@angular/common/http";
import {Meal} from "../sharedmodule/models/meal";
import {Menu} from "../sharedmodule/models/menu";
import {Login} from "../sharedmodule/models/login";
import {AuthObject} from "../sharedmodule/models/authObject";


@Injectable()
export class CoreCantineService {
  private  BASIC_URL = "http://localhost:8080/cantine/" ;
  private  USER_AUTHENTICATION_ENDPOINT = "http://localhost:8080/"+ 'login';
  private  GET_ALL_MEALS_ENDPOINT = this.BASIC_URL+  'api/meals/getAll'
  private GET_ALL_MENUS_ENDPOINT =  this.BASIC_URL+ 'api/menus/getAll';


  constructor(private httpClient: HttpClient) { }




    userAuthentication(login:Login) {
        return this.httpClient.post<AuthObject>(this.USER_AUTHENTICATION_ENDPOINT, login );
    }

   getAllMeals () {
    return this.httpClient.get<Meal[]>(this.GET_ALL_MEALS_ENDPOINT)
  }

  getAllMenus (){
      return this.httpClient.get<Menu[]>(this.GET_ALL_MENUS_ENDPOINT)
  }





}

