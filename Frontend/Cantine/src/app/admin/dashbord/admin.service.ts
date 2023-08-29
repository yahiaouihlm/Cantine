import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams, HttpStatusCode} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {catchError, throwError} from "rxjs";
import {ExceptionDialogComponent} from "../../sharedmodule/dialogs/exception-dialog/exception-dialog.component";
import {Adminfunction} from "../../sharedmodule/models/adminfunction";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";

@Injectable()
export class AdminService {


    private BASIC_ENDPOINT = "http://localhost:8080/cantine/" + 'admin/adminDashboard';

    private ADMIN_SIGN_UP_URL = this.BASIC_ENDPOINT + '/signUp';
    private GET_ADMIN_FUNCTION_S = this.BASIC_ENDPOINT + '/getAllAdminFunctions';

    /*private SEND_CONFIRMATION_TOKEN = this.BASIC_ENDPOINT + '/sendToken';*/

    private CHECK_TOKEN_VALIDITY_ADMIN =  this.BASIC_ENDPOINT + '/checkTokenValidity';


    constructor(private httpClient: HttpClient, private matDialog: MatDialog, private router: Router) {
    }


    checkTokenValidityAdmin(token: string) {
        const params = new HttpParams().set('token', token);
        return this.httpClient.get<NormalResponse>(this.CHECK_TOKEN_VALIDITY_ADMIN, {params});
    }


    signUpAdmin(admin: FormData) {
        return this.httpClient.post<NormalResponse>(this.ADMIN_SIGN_UP_URL, admin).pipe(
            catchError((error) => this.handleError(error))
        );
    }

    getAdminFunctionS() {
        return this.httpClient.get<Adminfunction[]>(this.GET_ADMIN_FUNCTION_S);
    }




    private handleError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;


        if (error.status == HttpStatusCode.InternalServerError) {
            this.openDialog("Unkwon Error   has  been occured  ", error.status);
        } else {
            this.openDialog(errorMessage, error.status);
        }

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
