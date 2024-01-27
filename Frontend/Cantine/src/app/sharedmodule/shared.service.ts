import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import {NormalResponse} from "./models/NormalResponse";
import {catchError, throwError} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {ErrorResponse} from "./models/ErrorResponse";
import {ExceptionDialogComponent} from "./dialogs/exception-dialog/exception-dialog.component";
import {User} from "./models/user";
import {AuthObject} from "./models/authObject";
import {Router} from "@angular/router";
import Malfunctions from "./functions/malfunctions";
import {DialogErrors} from "./functions/dialogueErrors";


@Injectable({
    providedIn: 'root'
})
export class SharedService {

    constructor(private httpClient: HttpClient, private matDialog: MatDialog , private router : Router) {
    }

    private BASIC_ENDPOINT = "http://localhost:8080/cantine/user/";
    private CHECK_EXISTENCE_OF_EMAIL = this.BASIC_ENDPOINT + "existing-email";

    private SEND_CONFIRMATION_TOKEN = this.BASIC_ENDPOINT + 'send-confirmation-link';

    private CHECK_TOKEN_VALIDITY = this.BASIC_ENDPOINT + 'check-confirmation-token';

    private GET_STUDENT_BY_ID = this.BASIC_ENDPOINT + 'student/getStudent';

    private  SEND_CONFIRMATION_TOKEN_FORGOT_PASSWORD_ENDPOINT = this.BASIC_ENDPOINT + 'send-reset-password-link';

    private  dialog =   new DialogErrors(this.matDialog);
    getStudentById(id: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
      const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('idStudent', id);
        return this.httpClient.get<User>(this.GET_STUDENT_BY_ID, {
                headers: headers,
                params: params
            }
        ).pipe(catchError((error) => this.handleErrors(error)));

    }

    sendTokenForgotPassword(email: string) {
        const params = new HttpParams().set('email', email);
        return this.httpClient.post<NormalResponse>(this.SEND_CONFIRMATION_TOKEN_FORGOT_PASSWORD_ENDPOINT, params).pipe(
            catchError((error) => this.handleError(error))
        );
    }

    checkExistenceOfEmail(email: string) {
        const params = new HttpParams().set('email', email);
        return this.httpClient.get<NormalResponse>(this.CHECK_EXISTENCE_OF_EMAIL, {params}).pipe(
            catchError((error) => this.handleErrors(error))
        );
    }

    checkTokenValidityAdmin(token: string) {
        const params = new HttpParams().set('token', token);
        return this.httpClient.get<NormalResponse>(this.CHECK_TOKEN_VALIDITY, {params});
    }


    private handleErrors(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (error.status == HttpStatusCode.Conflict) {
            return throwError(() => new Error(errorMessage));
        } else if (error.status == HttpStatusCode.Unauthorized) {
            localStorage.clear();
            this.router.navigate(['cantine/signIn']).then(error => console.log("redirected to login page"));

        } else {
            localStorage.clear();
            this.dialog.openDialog("Unkwon Error   has  been occured  ", error.status)
            this.router.navigate(['cantine/home']).then(error => console.log("redirected to login page"));
        }

        return throwError(() => new Error(error.message));

    }

    sendToken(email: string) {
        const params = new HttpParams().set('email', email);
        return this.httpClient.post<NormalResponse>(this.SEND_CONFIRMATION_TOKEN, params).pipe(
            catchError((error) => this.handleError(error))
        );
    }


    private handleError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (error.status == HttpStatusCode.NotFound || error.status == HttpStatusCode.Forbidden) {
            this.dialog.openDialog("Utilisateur  n'existe  pas", error.status);
        }
        else if (error.status == HttpStatusCode.Conflict ) {
            this.dialog.openDialog("Compte Utilisateur n'est  pas  activé", error.status);
        }
        else  if (error.status == HttpStatusCode.InternalServerError) {
            this.dialog.openDialog(" Une erreur s'est produite pendant l'envoi de l'email de confirmation", error.status)
        } else {
            console.log(error.message)
            this.dialog.openDialog(errorMessage, error.status)
        }

        return throwError(() => new Error(errorMessage));

    }


/*
    private openDialog(message: string, httpError: HttpStatusCode): void {
        const result = this.matDialog.open(ExceptionDialogComponent, {
            data: {message: message},
            width: '40%',
        });

        result.afterClosed().subscribe((confirmed: boolean) => {
            if (httpError == HttpStatusCode.BadRequest || httpError == HttpStatusCode.NotAcceptable || httpError == HttpStatusCode.Conflict || httpError == HttpStatusCode.NotFound) {
                //  this.router.navigate(['/admin/menus'] , { queryParams: { reload: 'true' } });
                console.log("je suis  la  dans  le  if  ")
            } else {
                console.log("je suis  la ")
                /!* TODO  remove THE  Token  *!/
                //this.router.navigate(['/cantine/home'] , { queryParams: { reload: 'true' } });
            }

        });

    }
*/





}
