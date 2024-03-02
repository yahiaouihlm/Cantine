import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {catchError, throwError} from "rxjs";
import {ExceptionDialogComponent} from "../../sharedmodule/dialogs/exception-dialog/exception-dialog.component";
import {Adminfunction} from "../../sharedmodule/models/adminfunction";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {User} from "../../sharedmodule/models/user";

@Injectable()
export class AdminService {


    private BASIC_ENDPOINT = "http://localhost:8080/cantine/" + 'admin';

    private GET_ADMIN_BY_ID = this.BASIC_ENDPOINT + "/getAdmin"
    private ADMIN_SIGN_UP_URL = this.BASIC_ENDPOINT + '/register';
    private GET_ADMIN_FUNCTION_S = this.BASIC_ENDPOINT + '/getAllAdminFunctions';


    constructor(private httpClient: HttpClient, private matDialog: MatDialog, private router: Router) {
    }

    private dialog = new DialogErrors(this.matDialog);


    signUpAdmin(admin: FormData) {
        return this.httpClient.post<NormalResponse>(this.ADMIN_SIGN_UP_URL, admin).pipe(
            catchError((error) => this.handleSignUpAdminErrors(error))
        );
    }

    getAdminFunctionS() {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        return this.httpClient.get<Adminfunction[]>(this.GET_ADMIN_FUNCTION_S ,{headers : headers});
    }


    private handleSignUpAdminErrors(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;
        let dialog;
        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }

        if (error.status == HttpStatusCode.BadRequest) {
            dialog = this.dialog.openDialog("Certains champs sont invalides");
        } else if (error.status == HttpStatusCode.NotFound) {
            dialog = this.dialog.openDialog(" Votre  fonction au  sien de  Ecole  aston    est introuvable");
        } else if (error.status == HttpStatusCode.Conflict) {
            dialog = this.dialog.openDialog("L'adresse mail que vous avez saisie est déjà utilisée");
        } else if (error.status == HttpStatusCode.NotAcceptable) {
            dialog = this.dialog.openDialog(" Image  invalide Il ne  pas etre  pris en  charge");

        } else  if (error.status == HttpStatusCode.InternalServerError) {
            dialog = this.dialog.openDialog("Une erreur serveur est survenue lors de l'ajout de l'administrateur");
        } else {
            dialog = this.dialog.openDialog("Une erreur est inconnue survenue lors de l'ajout de l'administrateur");
        }

        /** TODO :    REVOIR  CECI */
        dialog.afterClosed().subscribe((confirmed: boolean) => {
           // this.router.navigate(['cantine/signIn']).then(error => console.log("redirected to login page"));
        });
        return throwError(() => new Error(errorMessage));

    }


    getAdminById(adminUuid: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
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
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if  (errorMessage == undefined){
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }

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
