import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpStatusCode} from "@angular/common/http";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {catchError, throwError} from "rxjs";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";
import {MatDialog} from "@angular/material/dialog";
import {Order} from "../../sharedmodule/models/order";

@Injectable()
export class OrderService {
    private ORDER_BASIC_URL = "http://localhost:8080/cantine/student/order";
    private ADD_ORDER_URL = this.ORDER_BASIC_URL + "/add";

    constructor(private httpClient: HttpClient, private matDialog: MatDialog) {
    }

    addOrder(order: Order) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        return  this.httpClient.post<NormalResponse>(this.ADD_ORDER_URL, order, {headers: headers}).pipe(
            catchError((error) => this.handleError(error))
        )
    }


    private handleError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (error.status == HttpStatusCode.InternalServerError) {
            errorMessage = "Une  erreur    est  survenue  !"
            new DialogErrors(this.matDialog).openDialog(errorMessage, error.status);
        } else {
            console.log('error');
            console.log(error);
            new DialogErrors(this.matDialog).openDialog(errorMessage, error.status);
        }
        return throwError(() => new Error(errorMessage));

    }

}
