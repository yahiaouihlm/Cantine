import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {catchError, of, throwError} from "rxjs";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";
import {MatDialog} from "@angular/material/dialog";
import {Order} from "../../sharedmodule/models/order";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";
import {Router} from "@angular/router";
import {error} from "@angular/compiler-cli/src/transformers/util";

@Injectable()
export class OrderService {
    private ORDER_BASIC_URL = "http://localhost:8080/order/student";
    private ADD_ORDER_URL = this.ORDER_BASIC_URL + "/add";
    private CANCEL_ORDER_URL = this.ORDER_BASIC_URL + "/cancel";
    private GET_ORDERS_OF_DAY_URL = this.ORDER_BASIC_URL + "/getByDateAndStudentId";
    private GET_ORDERS_HISTORY = this.ORDER_BASIC_URL + "/getHistory"

    private dialog = new DialogErrors(this.matDialog);

    constructor(private httpClient: HttpClient, private matDialog: MatDialog, private router: Router) {
    }

    addOrder(order: Order) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        return this.httpClient.post<NormalResponse>(this.ADD_ORDER_URL, order, {headers: headers}).pipe(
            catchError((error) => this.handleAddOrderError(error))
        )
    }

    getOrdersOfDay() {
        let studentId = Malfunctions.getUserIdFromLocalStorage();
        let date = Malfunctions.getCurrentDate();
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = {studentUuid: studentId, date: date};
        return this.httpClient.get<Order[]>(this.GET_ORDERS_OF_DAY_URL, {headers: headers, params: params});
    }

    cancelOrder(orderUuid: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = {orderUuid: orderUuid};
        return this.httpClient.post<NormalResponse>(this.CANCEL_ORDER_URL, null, {
            headers: headers,
            params: params
        }).pipe(
            catchError((error) => this.handleCancelOrderError(error))
        )
    }

    getAllOrders(studentUuid: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set("studentUuid", studentUuid);
        return this.httpClient.get <Order[]>(this.GET_ORDERS_HISTORY , {headers: headers, params : params}).pipe(
            catchError ((error) =>  of ([])
        ));
    }

    private handleCancelOrderError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        } else if (error.status == HttpStatusCode.BadRequest) {
            errorMessage = "Informations  invalides sur  la  commande"
            this.dialog.openDialog(errorMessage);
        } else if (error.status == HttpStatusCode.NotFound) {
            errorMessage = "Commande  Introuvable "
            this.dialog.openDialog(errorMessage);
        } else if (error.status == HttpStatusCode.Unauthorized) {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        } else if (error.status == HttpStatusCode.Forbidden) {
            errorMessage = "Commande  déja  annulée ou  validée  !"
            this.dialog.openDialog(errorMessage);
        } else if (error.status == HttpStatusCode.InternalServerError) {
            errorMessage = "Une erreur  s'est produite  pendant  l'annulation  de  la commande"
            this.dialog.openDialog(errorMessage);
            localStorage.clear();
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        } else {
            errorMessage = "Une erreur  inconnue  s'est produite"
            this.dialog.openDialog(errorMessage);
            localStorage.clear();
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }

        return throwError(() => new Error(errorMessage));

    }

    private handleAddOrderError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        } else if (error.status == HttpStatusCode.BadRequest) {
            errorMessage = "Informations  invalides sur  la  commande"
            this.dialog.openDialog(errorMessage);
        } else if (error.status == HttpStatusCode.NotFound) {
            errorMessage = "Utilisateur ,Plat ou Menu  Introuvable  !"
            this.dialog.openDialog(errorMessage);
        } else if (error.status == HttpStatusCode.NotAcceptable) {
            errorMessage = "Votre commande dépasse le nombre de repas autorisé  !";
            this.dialog.openDialog(errorMessage);
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        } else if (error.status == HttpStatusCode.Unauthorized) {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        } else if (error.status == HttpStatusCode.ServiceUnavailable) {
            errorMessage = "Certains plats ou  menus  ne sont pas  disponibles  !"
            this.dialog.openDialog(errorMessage);
        } else if (error.status == HttpStatusCode.InternalServerError) {
            errorMessage = "Une erreur  s'est produite  pendant  l'annulation  de  la commande"
            this.dialog.openDialog(errorMessage);
            localStorage.clear();
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        } else if (error.status == HttpStatusCode.PaymentRequired) {
            errorMessage = "Votre solde est insuffisant pour  effectuer  cette commande  !";
            this.dialog.openDialog(errorMessage);
        } else {
            errorMessage = "Une erreur  inconnue  s'est produite"
            this.dialog.openDialog(errorMessage);
            localStorage.clear();
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }

        return throwError(() => new Error(errorMessage));

    }
}
