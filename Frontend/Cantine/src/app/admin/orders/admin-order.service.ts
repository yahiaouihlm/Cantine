import {Injectable} from '@angular/core';
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import {Order} from "../../sharedmodule/models/order";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";
import {catchError, throwError} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";
import {Router} from "@angular/router";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";

@Injectable()
export class AdminOrderService {


    private BASE_ORDERS_URL = "http://localhost:8080/order/admin";
    private GET_ORDERS_BY_DAY = this.BASE_ORDERS_URL + "/getAllOrdersOfDay";
    private ADMIN_SUBMIT_ORDER = this.BASE_ORDERS_URL + "/submitOrder";
    private dialog = new DialogErrors(this.matDialog);

    constructor(private httpClient: HttpClient, private matDialog: MatDialog, private router: Router) {
    }


    submitOrder(orderUuid: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        // const params = new HttpParams().set('orderUuid', orderUuid);
        const params = new HttpParams().set('orderUuid', orderUuid)
        return this.httpClient.post <NormalResponse>(this.ADMIN_SUBMIT_ORDER, null, {
            headers: headers,
            params: params
        }).pipe(
            catchError((error) => this.handleError(error))
        )
    }


    getOrdersByDate() {
        let token = Malfunctions.getTokenFromLocalStorage();
        let date = Malfunctions.getCurrentDate();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('date', date);
        return this.httpClient.get<Order[]>(this.GET_ORDERS_BY_DAY, {headers: headers, params: params}).pipe(
            catchError((error) => this.handleError(error))

        );

    }

    private handleError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;
        if (errorMessage == null) {
            localStorage.clear();
            this.dialog.openDialog("Une  erreur  est  survenue  !");
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }
        if (error.status == HttpStatusCode.BadRequest) {
            this.dialog.openDialog("veuillez  verifier  les  informations  saisies  !");
        } else if (error.status == HttpStatusCode.Unauthorized) {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        } else {
            errorMessage = "Une  erreur    est  survenue  !"
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);

        }
        return throwError(() => new Error(error.error));

    }
}
