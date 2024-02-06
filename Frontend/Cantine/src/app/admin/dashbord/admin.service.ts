import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams, HttpStatusCode} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {catchError, throwError} from "rxjs";
import {ExceptionDialogComponent} from "../../sharedmodule/dialogs/exception-dialog/exception-dialog.component";
import {Adminfunction} from "../../sharedmodule/models/adminfunction";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";

@Injectable()
export class AdminService {


    private BASIC_ENDPOINT = "http://localhost:8080/cantine/" + 'admin';

    private ADMIN_SIGN_UP_URL = this.BASIC_ENDPOINT + '/signUp';
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
        return this.httpClient.get<Adminfunction[]>(this.GET_ADMIN_FUNCTION_S);
    }


    private handleSignUpAdminErrors(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;
        let dialog;
        if (error.status == HttpStatusCode.BadRequest) {
            dialog = this.dialog.openDialog("Certains champs sont invalides");
        } else if (error.status == HttpStatusCode.NotFound) {
            dialog = this.dialog.openDialog(" Votre  fonction au  sien de  Ecole  aston    est introuvable");
        } else if (error.status == HttpStatusCode.Conflict) {
            dialog = this.dialog.openDialog("L'adresse mail que vous avez saisie est déjà utilisée");
        } else if (error.status == HttpStatusCode.NotAcceptable) {
            dialog = this.dialog.openDialog(" Image  invalide Il ne  pas etre  pris en  charge");

        }
        if (error.status == HttpStatusCode.InternalServerError) {
            dialog = this.dialog.openDialog("Une erreur serveur est survenue lors de l'ajout de l'administrateur");
        } else {
            dialog = this.dialog.openDialog("Une erreur est inconnue survenue lors de l'ajout de l'administrateur");
        }

        dialog.afterClosed().subscribe((confirmed: boolean) => {
            this.router.navigate(['cantine/signIn']).then(error => console.log("redirected to login page"));
        });
        return throwError(() => new Error(errorMessage));

    }


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
                /* TODO  remove THE  Token  */
                //this.router.navigate(['/cantine/home'] , { queryParams: { reload: 'true' } });
            }

        });

    }

}
