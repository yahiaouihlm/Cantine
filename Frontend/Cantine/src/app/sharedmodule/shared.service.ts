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
import {IConstantsURL} from "./constants/IConstantsURL";



@Injectable({
    providedIn: 'root'
})
export class SharedService {

    constructor(private httpClient: HttpClient, private matDialog: MatDialog, private router: Router) {
    }

    private BASIC_ENDPOINT =  "http://localhost:8080/cantine/user/";

    private RESET_PASSWORD_UER_ENDPOINT = this.BASIC_ENDPOINT + 'reset-password';
    private CHECK_EXISTENCE_OF_EMAIL = this.BASIC_ENDPOINT + "existing-email";

    private SEND_CONFIRMATION_TOKEN = this.BASIC_ENDPOINT + 'send-confirmation-link';

    private CHECK_TOKEN_VALIDITY = this.BASIC_ENDPOINT + 'check-confirmation-token';

    private GET_STUDENT_BY_ID = this.BASIC_ENDPOINT + 'student/getStudent';

    private SEND_CONFIRMATION_TOKEN_FORGOT_PASSWORD_ENDPOINT = this.BASIC_ENDPOINT + 'send-reset-password-link';

    private dialog = new DialogErrors(this.matDialog);


    resetPassword(token: string, password: string) {
        const params = new HttpParams().set('token', token).set('newPassword', password);
        return this.httpClient.post<NormalResponse>(this.RESET_PASSWORD_UER_ENDPOINT, params).pipe(
            catchError((error) => this.handleRestPasswordErrors(error))
        );
    }

    getStudentById(id: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('studentUuid', id);     
        return this.httpClient.get <User>(this.GET_STUDENT_BY_ID, {
            headers: headers,
            params: params
        }).pipe(
            catchError((error) => this.handleGetStudentByIdErrors(error))
        );

    }

    sendTokenForgotPassword(email: string) {
        const params = new HttpParams().set('email', email);
        return this.httpClient.post<NormalResponse>(this.SEND_CONFIRMATION_TOKEN_FORGOT_PASSWORD_ENDPOINT, params).pipe(
            catchError((error) => this.handleError(error))
        );
    }

    checkExistenceOfEmail(email: string) {
        const params = new HttpParams().set('email', email);
        return this.httpClient.get<NormalResponse>(this.CHECK_EXISTENCE_OF_EMAIL, {params});
    }

    checkUserTokenValidity(token: string) {
        const params = new HttpParams().set('token', token);
        return this.httpClient.post<NormalResponse>(this.CHECK_TOKEN_VALIDITY, params);
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
        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        }

        if (error.status == HttpStatusCode.NotFound || error.status == HttpStatusCode.Forbidden) {
            this.dialog.openDialog("Utilisateur  n'existe  pas");
        } else if (error.status == HttpStatusCode.Conflict) {
            this.dialog.openDialog("Compte Utilisateur n'est  pas  activé");
        } else if (error.status == HttpStatusCode.InternalServerError) {
            this.dialog.openDialog(" Une erreur s'est produite pendant l'envoi de l'email de confirmation")
        } else {
            console.log(error.message)
            this.dialog.openDialog(errorMessage)
        }

        return throwError(() => new Error(errorMessage));

    }

    private handleRestPasswordErrors(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        }
        let dialog;
        if (error.status == HttpStatusCode.BadRequest) {
            dialog = this.dialog.openDialog("Les  Informations  Transmises Sont  invalides");
        } else if (error.status == HttpStatusCode.NotFound) {
            dialog = this.dialog.openDialog("Impossible  de modifier  le  mot  de passe  Token ou  utilisateur est  Introuvable");
        } else if (error.status == HttpStatusCode.Unauthorized) {
            dialog = this.dialog.openDialog("Le Token est  Expiré");
        } else if (error.status == HttpStatusCode.InternalServerError) {
            dialog = this.dialog.openDialog(" Une erreur serveur s'est produite ")
        } else {
            dialog = this.dialog.openDialog(" Une erreur Inconnue s'est produite ")
        }

        dialog.afterClosed().subscribe(result => {
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(error => console.log("redirected to login page"));
        });
        return throwError(() => new Error(errorMessage));

    }


    private handleGetStudentByIdErrors(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        }
        if (error.status == HttpStatusCode.BadRequest) {
            errorMessage = "Les  Informations  Transmises Sont  invalides";
            this.dialog.openDialog(errorMessage);
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }else if (error.status == HttpStatusCode.Unauthorized) {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }
        else if (error.status == HttpStatusCode.NotFound) {
            errorMessage = "Utilisateur  n'existe  pas";
            this.dialog.openDialog(errorMessage);
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }
        else if (error.status == HttpStatusCode.InternalServerError) {
            errorMessage = " Une erreur serveur s'est produite ";
            this.dialog.openDialog(errorMessage);
            localStorage.clear();
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }
        else {
            errorMessage = " Une erreur Inconnue s'est produite ";
            this.dialog.openDialog(errorMessage);
            localStorage.clear();
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }
        return throwError(() => new Error(errorMessage));
    }

}
