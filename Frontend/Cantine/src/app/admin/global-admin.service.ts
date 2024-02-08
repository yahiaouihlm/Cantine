import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import {User} from "../sharedmodule/models/user";
import Malfunctions from "../sharedmodule/functions/malfunctions";
import {ErrorResponse} from "../sharedmodule/models/ErrorResponse";
import {catchError, throwError} from "rxjs";
import {ExceptionDialogComponent} from "../sharedmodule/dialogs/exception-dialog/exception-dialog.component";
import {Router} from "@angular/router";
import {MatDialog} from "@angular/material/dialog";
import {DialogErrors} from "../sharedmodule/functions/dialogueErrors";
import {IConstantsURL} from "../sharedmodule/constants/IConstantsURL";

@Injectable()
export class GlobalAdminService {

    private BASIC_ADMIN_URL = "http://localhost:8080/cantine/admin"

    private GET_ADMIN_BY_ID = this.BASIC_ADMIN_URL + "/getAdmin"

    constructor(private httpClient: HttpClient, private router: Router, private matDialog: MatDialog) {
    }

    private dialog = new DialogErrors(this.matDialog);

    getAdminById(adminUuid: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        console.log('token', token);
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('adminUuid', adminUuid);
        return this.httpClient.get <User>(this.GET_ADMIN_BY_ID, {
            headers: headers,
            params: params
        }).pipe(
            catchError((error) => this.handleErrorOfGetAdminById(error))
        );
    }


    private handleErrorOfGetAdminById(error: HttpErrorResponse) {
        const errorObject = error.error;
        let errorMessage = errorObject.exceptionMessage;

        if (error.status == HttpStatusCode.BadRequest || error.status == HttpStatusCode.NotFound || error.status == HttpStatusCode.Unauthorized) {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        } else {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }

        return throwError(() => new Error(errorMessage));
    }


}
