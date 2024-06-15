import {Injectable} from '@angular/core';
import {Login} from "../shared-module/models/login";
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import {AuthObject} from "../shared-module/models/authObject";
import {connection} from "../shared-module/functions/connection";
import {ErrorResponse} from "../shared-module/models/ErrorResponse";
import {catchError, throwError} from "rxjs";
import {Router} from "@angular/router";
import {IConstantsURL} from "../shared-module/constants/IConstantsURL";
import {AlertController} from "@ionic/angular";
import {Student} from "../shared-module/models/student";



@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private BASIC_URL = "http://localhost:8080/user"
  private USER_AUTHENTICATION_ENDPOINT = this.BASIC_URL + '/login';
  private GET_STUDENT_BY_ID = "http://localhost:8080/cantine/user/"+ 'student/getStudent';

  constructor(private httpClient: HttpClient,private router : Router , private alertController: AlertController) {
  }


  signIn(login: Login) {
    return this.httpClient.post <AuthObject>(this.USER_AUTHENTICATION_ENDPOINT, login);
  }

  getStudentByID(id: string) {
    let token = connection.getTokenFromLocalStorage();
    const headers = new HttpHeaders().set('Authorization', token);
    const params = new HttpParams().set('studentUuid', id);
    return this.httpClient.get<Student>(this.GET_STUDENT_BY_ID, {
        headers: headers,
        params: params
      }
    ).pipe(catchError((error) => this.handleErrors(error)));
  }


  private handleErrors(error: HttpErrorResponse) {

    const errorObject = error.error as ErrorResponse;
    let errorMessage = errorObject.exceptionMessage;
    if (errorMessage == null)

    if (error.status == HttpStatusCode.Conflict) {
      return throwError(() => new Error(errorMessage));
    } else if (error.status == HttpStatusCode.Unauthorized) {
      localStorage.clear();
      this.router.navigate([IConstantsURL.STUDENT_SIGN_IN]).then();

    } else {
      localStorage.clear();
      this.presentAlert().then();
      this.router.navigate([IConstantsURL.STUDENT_SIGN_IN]).then();
    }

    return throwError(() => new Error(error.message));

  }



  async presentAlert() {
    const alert = await this.alertController.create({
      header: 'Unkwon Error  has  been occured',
   /*   subHeader: 'A Sub Header Is Optional',
   */   message: 'Une erreur serveur produite , veuillez réessayer ultérieurement',
      buttons: ['OK'],
    });

    await alert.present();
  }

}
