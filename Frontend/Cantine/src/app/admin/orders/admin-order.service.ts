import { Injectable } from '@angular/core';
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Order} from "../../sharedmodule/models/order";

@Injectable()
export class AdminOrderService {


  private   BASE_ORDERS_URL = "http://localhost:8080/cantine/order/admin";
  private GET_ORDERS_BY_DAY = this.BASE_ORDERS_URL+ "/getAllOrdersOfDay";
  constructor(private httpClient  : HttpClient ) { }



  getOrdersByDate () {
    let token  =  Malfunctions.getTokenFromLocalStorage();
    const headers = new HttpHeaders().set('Authorization', token);
    let date = Malfunctions.getCurrentDate();
    const params = {date: date};
    return this.httpClient.get<Order[]>(this.GET_ORDERS_BY_DAY, {headers: headers, params: params});

  }

}
