import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Meal} from "../shared-module/models/meals";

@Injectable()
export class CoreCantineService {
  private BASIC_URL = "http://localhost:8080/cantine/api/getAll/";
  private GET_ALL_MEALS_ENDPOINT = this.BASIC_URL + 'meals'

  constructor(private httpClient: HttpClient) {
  }


  getAllAvailableMeals() {
    return this.httpClient.get<Meal[]>(this.GET_ALL_MEALS_ENDPOINT)
  }
}
