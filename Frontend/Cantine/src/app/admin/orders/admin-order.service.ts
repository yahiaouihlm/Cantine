import { Injectable } from '@angular/core';
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpStatusCode} from "@angular/common/http";
import {Order} from "../../sharedmodule/models/order";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";
import {catchError, throwError} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";

@Injectable()
export class AdminOrderService {


  private   BASE_ORDERS_URL = "http://localhost:8080/cantine/order/admin";
  private GET_ORDERS_BY_DAY = this.BASE_ORDERS_URL+ "/getAllOrdersOfDay";
  private  ADMIN_SUBMIT_ORDER  =  this.BASE_ORDERS_URL +  "/submitOrder";
  constructor(private httpClient  : HttpClient , private  matDialog : MatDialog) { }



  submitOrder (orderId: number)  {
    let token  =  Malfunctions.getTokenFromLocalStorage();
    const headers = new HttpHeaders().set('Authorization', token);
    const params = {orderId: orderId};
    return  this.httpClient.post <NormalResponse>(this.ADMIN_SUBMIT_ORDER,  {headers: headers, params: params}).pipe(
        catchError(error => this.handleError(error))
    )
  }


  getOrdersByDate () {
    let token  =  Malfunctions.getTokenFromLocalStorage();
    const headers = new HttpHeaders().set('Authorization', token);
    let date = Malfunctions.getCurrentDate();
    const params = {date: date};
    return this.httpClient.get<Order[]>(this.GET_ORDERS_BY_DAY, {headers: headers, params: params});

  }

  private handleError(error: HttpErrorResponse) {
    const errorObject = error.error as ErrorResponse;
    let errorMessage = errorObject.exceptionMessage;

    if (error.status == HttpStatusCode.InternalServerError) {
      errorMessage = "Une  erreur    est  survenue  !"
      new DialogErrors(this.matDialog).openDialog(errorMessage, error.status);
    } else {
      new DialogErrors(this.matDialog).openDialog(errorMessage, error.status);
    }
    return throwError(() => new Error(errorMessage));

  }
}
