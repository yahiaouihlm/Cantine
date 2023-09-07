import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpStatusCode} from "@angular/common/http";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {Adminfunction} from "../../sharedmodule/models/adminfunction";
import {StudentClass} from "../../sharedmodule/models/studentClass";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {catchError, throwError} from "rxjs";
import {ExceptionDialogComponent} from "../../sharedmodule/dialogs/exception-dialog/exception-dialog.component";
import Malfunctions from "../../sharedmodule/functions/malfunctions";

@Injectable()
export class StudentDashboardService {

    private BASIC_ENDPOINT = "http://localhost:8080/cantine/student/";

    private STUDENT_SIGN_UP_URL = this.BASIC_ENDPOINT + 'signUp';
    private GET_ALL_STUDENT_CLASS = this.BASIC_ENDPOINT + 'getAllStudentClass';

    private UPDATE_STUDENT_INFO = this.BASIC_ENDPOINT + 'update/studentInfo'

    constructor(private httpClient: HttpClient, private matDialog: MatDialog, private router: Router) {
    }

    updateStudent(student: FormData) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
       return this.httpClient.put<NormalResponse>(this.UPDATE_STUDENT_INFO, student, {
            headers: headers,
        }).pipe(catchError((error) => this.handleError(error)));
    }

    signUpStudent(student: FormData) {
        return this.httpClient.post<NormalResponse>(this.STUDENT_SIGN_UP_URL, student).pipe(
            catchError((error) => this.handleError(error))
        );
    }

    getAllStudentClass() {
        return this.httpClient.get<StudentClass[]>(this.GET_ALL_STUDENT_CLASS);
    }

    private handleError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (error.status == HttpStatusCode.BadRequest || error.status == HttpStatusCode.NotAcceptable || error.status == HttpStatusCode.Conflict || error.status == HttpStatusCode.NotFound) {
            this.openDialog(errorMessage, error.status);
        } else {
            this.openDialog(" Une  Erreur  Inconnue  c'est produite ", error.status);
        }
        this.router.navigate(['cantine/home']).then(
            () => {
                localStorage.clear()
            }
        );

        return throwError(() => new Error(errorMessage));

    }


    private openDialog(message: string, httpError: HttpStatusCode): void {
        const result = this.matDialog.open(ExceptionDialogComponent, {
            data: {message: message},
            width: '40%',
        });

        result.afterClosed().subscribe((confirmed: boolean) => {

            /* TODO  remove THE  Token    and  redirect  to  the  Main  page */
            //this.router.navigate(['/cantine/home'] , { queryParams: { reload: 'true' } });

        });

    }
}
