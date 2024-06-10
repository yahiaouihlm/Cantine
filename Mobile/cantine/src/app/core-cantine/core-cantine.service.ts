import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Meal} from "../shared-module/models/meal";
import {Menu} from "../shared-module/models/menu";

@Injectable()
export class CoreCantineService {
  private BASIC_URL = "http://localhost:8080/cantine/api/getAll/";
  private GET_ALL_MEALS_ENDPOINT = this.BASIC_URL + 'meals'
  private GET_ALL_MENUS_ENDPOINT = this.BASIC_URL + 'menus';
  constructor(private httpClient: HttpClient) {
  }


  getAllAvailableMeals() {
    return this.httpClient.get<Meal[]>(this.GET_ALL_MEALS_ENDPOINT)
  }


  getAllAvailableMenus() {
    return this.httpClient.get<Menu[]>(this.GET_ALL_MENUS_ENDPOINT)
  }
}
