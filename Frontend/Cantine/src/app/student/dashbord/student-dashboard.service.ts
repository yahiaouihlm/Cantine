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
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";

@Injectable()
export class StudentDashboardService {

    private BASIC_ENDPOINT = "http://localhost:8080/cantine/user/student/";

    private STUDENT_SIGN_UP_URL = this.BASIC_ENDPOINT + 'signUp';
    private GET_ALL_STUDENT_CLASS = this.BASIC_ENDPOINT + 'getAllStudentClass';

    private UPDATE_STUDENT_INFO = this.BASIC_ENDPOINT + 'update/studentInfo'

    private dialog = new DialogErrors(this.matDialog);

    constructor(private httpClient: HttpClient, private matDialog: MatDialog, private router: Router) {
    }

    updateStudent(student: FormData) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        return this.httpClient.put<NormalResponse>(this.UPDATE_STUDENT_INFO, student, {
            headers: headers,
        }).pipe(catchError((error) => this.handleSignUpErrors(error)));
    }

    signUpStudent(student: FormData) {
        return this.httpClient.post<NormalResponse>(this.STUDENT_SIGN_UP_URL, student).pipe(
            catchError((error) => this.handleSignUpErrors(error))
        );
    }

    getAllStudentClass() {
        let token = Malfunctions.getTokenFromLocalStorage();
        return this.httpClient.get<StudentClass[]>(this.GET_ALL_STUDENT_CLASS);
    }

    private handleSignUpErrors(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        }
        if (error.status == HttpStatusCode.BadRequest) {
            errorMessage = "Veuillez vérifier les informations saisies";
            this.dialog.openDialog(errorMessage);
        } else if (error.status == HttpStatusCode.Conflict) {
            errorMessage = "Cet utilisateur existe déjà";
            this.dialog.openDialog(errorMessage);
        } else if (error.status == HttpStatusCode.NotAcceptable) {
            errorMessage = "Format d'image non valide";
            this.dialog.openDialog(errorMessage);
        } else {
            errorMessage = "Une erreur s'est produite Veuillez réessayer plus tard";
            this.dialog.openDialog(errorMessage);
        }
        return throwError(() => new Error(errorMessage));

    }
}
