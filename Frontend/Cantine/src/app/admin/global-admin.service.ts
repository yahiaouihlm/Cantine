import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import {User} from "../sharedmodule/models/user";
import Malfunctions from "../sharedmodule/functions/malfunctions";
import {ErrorResponse} from "../sharedmodule/models/ErrorResponse";
import {catchError, throwError} from "rxjs";
import {ExceptionDialogComponent} from "../sharedmodule/dialogs/exception-dialog/exception-dialog.component";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";

@Injectable()
export class GlobalAdminService {

    private BASIC_ADMIN_URL = "http://localhost:8080/cantine/admin/adminDashboard"

    private GET_ADMIN_BY_ID = this.BASIC_ADMIN_URL + "/getAdmin"

    constructor(private httpClient: HttpClient, private router: Router, private matDialog: MatDialog) {
    }


    getAdminById(idAdmin: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('idAdmin', idAdmin);
        return this.httpClient.get <User>(this.GET_ADMIN_BY_ID,   {
            headers: headers,
            params: params
        }).pipe(
            catchError((error) => this.handleError(error))
        );
    }


    private handleError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (error.status == HttpStatusCode.BadRequest || error.status == HttpStatusCode.NotAcceptable || error.status == HttpStatusCode.Conflict || error.status == HttpStatusCode.NotFound) {
            this.openDialog(errorMessage, error.status);
        } else {
            this.openDialog(" Une  Erreur  Inconnue  c'est produite ", error.status);
        }

        return throwError(() => new Error(errorMessage));

    }

    private openDialog(message: string, httpError: HttpStatusCode): void {
        const result = this.matDialog.open(ExceptionDialogComponent, {
            data: {message: message},
            width: '40%',
        });

        result.afterClosed().subscribe(() => {
           //  localStorage.clear();
               this.router.navigate(['cantine/home']);

        });

    }
}
