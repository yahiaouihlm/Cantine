import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {User} from "../../sharedmodule/models/user";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";
import {catchError, throwError} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";
import {Router} from "@angular/router";
import {Transaction} from "../../sharedmodule/models/Transaction";

@Injectable()
export class StudentsManagementService {

    private BASIC_ADMIN_URL = "http://localhost:8080/cantine/admin/adminDashboard/works";

    private GET_STUDENT_TRANSACTIONS_ENDPOINT = this.BASIC_ADMIN_URL + "/getStudentTransactions";
    private GET_STUDENT = this.BASIC_ADMIN_URL + "/getStudent";
    private GET_STUDENT_BY_UUID = this.BASIC_ADMIN_URL + "/getStudentByUuId";

    private  UPDATE_STUDENT_EMAIL = this.BASIC_ADMIN_URL + "/updateStudentEmail";
    private SEND_STUDENT_WALLET = this.BASIC_ADMIN_URL + "/addStudentAmount";

    private SEND_STUDENT_VALIDATION_CODE = this.BASIC_ADMIN_URL + "/validationAmount";
    private dialog = new DialogErrors(this.matDialog);
    constructor(private httpClient: HttpClient, private matDialog: MatDialog,  private router: Router) {
    }

    getStudentTransactions(studentUuid: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('studentUuid', studentUuid);
        return this.httpClient.get<Transaction[]>(this.GET_STUDENT_TRANSACTIONS_ENDPOINT, {headers: headers, params: params}).pipe(
            catchError((error) => this.handleGetStudentError(error))
        );
    }

    updateStudentEmail(studentUuid: string, newEmail: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('studentUuid', studentUuid).set('newEmail', newEmail);
        return this.httpClient.post<NormalResponse>(this.UPDATE_STUDENT_EMAIL, null, {headers: headers, params: params}).pipe(
            catchError((error) => this.handleUpdateStudentEmailError(error))
        );
    }
    sendStudentCode(adminUuid :  string ,  studentId: string, amount: number, validationCode: number) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('studentUuid', studentId.toString())
            .set('adminUuid' , adminUuid)
            .set('amount', amount)
            .set('validationCode', validationCode)
        return this.httpClient.post<NormalResponse>(this.SEND_STUDENT_VALIDATION_CODE, null, {headers: headers, params: params});
    }

    attemptAddAmountToStudentAccount(adminUuid :string, studentUuid: string, amount: number) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('adminUuid', adminUuid)
            .set('studentUuid', studentUuid)
            .set('amount', amount.toString());

        return this.httpClient.post<NormalResponse>(this.SEND_STUDENT_WALLET, null, {headers: headers, params: params}).pipe(
            catchError((error) => this.handleAttemptAddAmountToStudentAccountError(error))
        );
    }

    getStudentByUuId(studentUuid: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('studentUuid', studentUuid)
        return this.httpClient.get<User>(this.GET_STUDENT_BY_UUID, {headers: headers, params: params}).pipe(
            catchError((error) => this.handleGetStudentError(error))
        );
    }

    getStudentByEmail(email: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('email', email);
        return this.httpClient.get<User>(this.GET_STUDENT, {params: params, headers: headers}).pipe(
            catchError((error) => this.handleGetStudentError(error))
        );
    }

    private handleAttemptAddAmountToStudentAccountError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;
        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        }
        if (error.status == HttpStatusCode.BadRequest) {
            errorMessage = "Les informations de l'etudiant ou du  montant sont incorrectes";
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.ADMIN_HOME_URL]).then(window.location.reload);
        }else if (error.status == HttpStatusCode.Unauthorized) {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }
        else if (error.status == HttpStatusCode.NotFound) {
            errorMessage = "Etudiant non trouvé";
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }else  if  (error.status == HttpStatusCode.InternalServerError) {
            errorMessage = "Une  erreur serveur  est  survenue  !";
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }else if (error.status == HttpStatusCode.Forbidden) {
            errorMessage = "Une  erreur d'authentification  est  survenue  !";
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        } else {
            errorMessage = "Une erreur s'est produite Veuillez réessayer plus tard";
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }
        return  throwError(() => new Error(errorMessage))
    }

    private  handleUpdateStudentEmailError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;
        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        }
        if (error.status == HttpStatusCode.BadRequest) {
            errorMessage = " Email  ou identifiant  de  étudiant  sont incorrectes";
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.ADMIN_HOME_URL]).then(window.location.reload);
        }
        else  if ( error.status ==  HttpStatusCode.NotFound) {
            errorMessage = "Etudiant non trouvé";
            const  answer = this.dialog.openDialog(errorMessage);
            answer.afterClosed().subscribe((result) => {window.location.reload()});
        }
        else if (error.status == HttpStatusCode.Unauthorized) {
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }
        else if (error.status == HttpStatusCode.InternalServerError) {
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }
        else if (error.status == HttpStatusCode.Conflict) {
            errorMessage = "Email  existe déjà";
            const  answer = this.dialog.openDialog(errorMessage);
            answer.afterClosed().subscribe((result) => {window.location.reload()});
        }
        else {
            errorMessage = "Une erreur s'est produite Veuillez réessayer plus tard";
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }
        return  throwError(() => new Error(errorMessage))
    }
    private handleGetStudentError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;
        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        }
        if (error.status == HttpStatusCode.BadRequest) {
            errorMessage = "Les informations de l'etudiant sont incorrectes";
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.ADMIN_HOME_URL]).then(window.location.reload);
        }
        else  if ( error.status ==  HttpStatusCode.NotFound) {
            errorMessage = "Etudiant non trouvé";
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }
        else if (error.status == HttpStatusCode.Unauthorized) {
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        }
        else {
            errorMessage = "Une erreur s'est produite Veuillez réessayer plus tard";
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }
          return  throwError(() => new Error(errorMessage))
    }

}
