import {Injectable} from '@angular/core';
//import {environment} from "../../../environments/environment";
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import {error} from "@angular/compiler-cli/src/transformers/util";
import {catchError, throwError} from "rxjs";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {ValidatorDialogComponent} from "../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {ExceptionDialogComponent} from "../../sharedmodule/dialogs/exception-dialog/exception-dialog.component";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse"
import {Meal} from "../../sharedmodule/models/meal";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {User} from "../../sharedmodule/models/user";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";

@Injectable()
export class MealsService {
    // private apiUrl = environment.apiUrl;

    private BASIC_ENDPOINT = "http://localhost:8080/cantine/" + 'admin/api/meals';


    private ADD_MEAL_URL = this.BASIC_ENDPOINT + '/add';
    private GET_MEAL_BY_ID_URL = this.BASIC_ENDPOINT + '/get';
    private UPDATE_MEAL_URL = this.BASIC_ENDPOINT + "/update";
    private DELETE_MEAL_URL = this.BASIC_ENDPOINT + "/delete";
    private GET_ALL_MEALS_URL = this.BASIC_ENDPOINT + "/getAll";

    constructor(private httpClient: HttpClient, private matDialog: MatDialog, private router: Router) {
    }

    private dialog = new DialogErrors(this.matDialog);

    deleteMeal(id: number) {
        const params = new HttpParams().set('idMeal', id);
        return this.httpClient.delete <NormalResponse>(this.DELETE_MEAL_URL, {params: params}).pipe(
            catchError((error) => this.handleAddMealErrors(error))
        );

    }

    editMeal(meal: FormData) {
        return this.httpClient.put <NormalResponse>(this.UPDATE_MEAL_URL, meal).pipe(
            catchError((error) => this.handleAddMealErrors(error))
        );
    }


    getMealById(id: number) {
        const params = new HttpParams().set('idMeal', id);
        return this.httpClient.get <Meal>(this.GET_MEAL_BY_ID_URL, {params: params}).pipe(
            catchError((error) => this.handleAddMealErrors(error))
        );
    }


    addMeal(meal: FormData) { //  we have  to  add  a  token  after
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        return this.httpClient.post <NormalResponse>(this.ADD_MEAL_URL, meal, {headers: headers}).pipe(
            catchError((error) => this.handleAddMealErrors(error))
        );
    }


    getAllMeals() {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        return this.httpClient.get <Meal[]>(this.GET_ALL_MEALS_URL, {
            headers: headers,
        });
    }


    private handleAddMealErrors(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;

        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }
        if (error.status == HttpStatusCode.BadRequest) {
            errorMessage = "Veuillez  vérifier  les  données  saisies  !"
            this.dialog.openDialog(errorMessage);
        } else if (error.status == HttpStatusCode.Unauthorized) { //  expired  token
            localStorage.clear()
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        } else if (error.status == HttpStatusCode.NotAcceptable) {
            errorMessage = "Veuillez  vérifier  l'image  envoyée !"
            this.dialog.openDialog(errorMessage);

        } else if (error.status == HttpStatusCode.Conflict) {
            errorMessage = "Ce  plat  existe  déjà  !"
            this.dialog.openDialog(errorMessage);
        } else if (error.status == HttpStatusCode.InternalServerError) {
            errorMessage = "Une  erreur  serveur  est  survenue  !"
            this.dialog.openDialog(errorMessage);
        }
        /*   else if  (error.status == HttpStatusCode.NotFound){
                errorMessage =  "Ce  plat  n'existe  pas  ! \n  il ce peut qu'il a été supprimé  !"
                this.openDialog(errorMessage, error.status);
            }*/
        else {
            errorMessage = "Une  erreur  est  survenue  !"
            this.dialog.openDialog(errorMessage);
            localStorage.clear();
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
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
                this.router.navigate(['/admin/meals'], {queryParams: {reload: 'true'}});
            } else {
                /* TODO  remove THE  Token  */
                //this.router.navigate(['/cantine/home'] , { queryParams: { reload: 'true' } });
            }

        });

    }


}
