import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Meal} from "../sharedmodule/models/meal";

@Injectable()
export class CoreCantineService {
  private  BASIC_MEAL_URL = "http://localhost:8080/cantine/api/" ;
  private  GET_ALL_MEALS_ENDPOINT = this.BASIC_MEAL_URL +  'meals/getAll'

  private GET_ALL_MENUS_ENDPOINT =  this.BASIC_MEAL_URL + 'menus/getAll';
  constructor(private httpClient: HttpClient) { }

   getAllMeals () {
    return this.httpClient.get<Meal[]>(this.GET_ALL_MEALS_ENDPOINT)
  }

}
