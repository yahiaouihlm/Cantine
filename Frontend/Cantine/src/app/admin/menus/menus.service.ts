import {Injectable} from '@angular/core';
import {Menu} from "../../sharedmodule/models/menu";
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpParams, HttpStatusCode} from "@angular/common/http";
import {catchError, throwError} from "rxjs";
import {ErrorResponse} from "../../sharedmodule/models/ErrorResponse";
import {MatDialog} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {ExceptionDialogComponent} from "../../sharedmodule/dialogs/exception-dialog/exception-dialog.component";
import {NormalResponse} from "../../sharedmodule/models/NormalResponse";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {IConstantsURL} from "../../sharedmodule/constants/IConstantsURL";
import {DialogErrors} from "../../sharedmodule/functions/dialogueErrors";
import {Meal} from "../../sharedmodule/models/meal";

@Injectable()
export class MenusService {

    private BASIC_ENDPOINT = "http://localhost:8080/cantine/" + 'admin/api/menus';
    private ADD_MENU_URL = this.BASIC_ENDPOINT + '/add';
    private GET_ONE_MENU_URL = this.BASIC_ENDPOINT + '/get';
    private UPDATE_MENU_URL = this.BASIC_ENDPOINT + '/update';
    private GET_ALL_MENUS_URL = this.BASIC_ENDPOINT + '/getAll';
    private dialog = new DialogErrors(this.matDialog);

    constructor(private httpClient: HttpClient, private matDialog: MatDialog, private router: Router) {
    }

    updateMenu(menu: FormData) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        return this.httpClient.post <NormalResponse>(this.UPDATE_MENU_URL, menu, {headers: headers}).pipe(
            catchError((error) => this.handleAddAndUpdateMenuErrors(error))
        );
    }

    addMenu(menu: FormData) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        return this.httpClient.post <NormalResponse>(this.ADD_MENU_URL, menu, {headers: headers}).pipe(
            catchError((error) => this.handleAddAndUpdateMenuErrors(error))
        );
    }

    getMenuById(id: string) {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        const params = new HttpParams().set('uuidMenu', id);
        return this.httpClient.get<Menu>(this.GET_ONE_MENU_URL, {params: params, headers: headers}).pipe(
            catchError((error) => this.handleGetMenuByUuidError(error))
        );
    }

    getAllMenus() {
        let token = Malfunctions.getTokenFromLocalStorage();
        const headers = new HttpHeaders().set('Authorization', token);
        return this.httpClient.get <Menu[]>(this.GET_ALL_MENUS_URL, {
            headers: headers,
        });
    }

    private handleAddAndUpdateMenuErrors(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;
        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        }

        if (error.status == HttpStatusCode.BadRequest) {
            errorMessage = "Certaines informations sont manquantes ou incorrectes";
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.ADMIN_HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        } else if (error.status == HttpStatusCode.Conflict) {
            errorMessage = "Ce menu existe déjà";
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.ADMIN_MENUS_URL]).then(window.location.reload);
        } else if (error.status == HttpStatusCode.Forbidden) {
            errorMessage = " Certains Plats ne  peuvent pas  être  ajouté  à  ce  menu  !";
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.ADMIN_HOME_URL]).then(window.location.reload);
        } else if (error.status == HttpStatusCode.Unauthorized) {
            localStorage.clear();
            this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
        } else if (error.status == HttpStatusCode.NotAcceptable) {
            errorMessage = "Certains informations sont  pas acceptables !";
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.ADMIN_HOME_URL]).then(window.location.reload);
        } else if (error.status == HttpStatusCode.InternalServerError) {
            errorMessage = "Une erreur interne est survenue";
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        } else if (error.status == HttpStatusCode.NotFound) {
            errorMessage = "Certains Plat(s) n'existe pas ! \n il ce peut qu'il a été supprimé !";
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.ADMIN_MENUS_URL]).then(window.location.reload);
        } else {
            errorMessage = "Une erreur est survenue";
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }
        return throwError(() => new Error(errorMessage));
    }


    private handleGetMenuByUuidError(error: HttpErrorResponse) {
        const errorObject = error.error as ErrorResponse;
        let errorMessage = errorObject.exceptionMessage;
        if (errorMessage == undefined) {
            localStorage.clear();
            this.dialog.openDialog("Une erreur s'est produite Veuillez réessayer plus tard");
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        }
        if (error.status == HttpStatusCode.BadRequest) {
            errorMessage = "Invalid  Menu Id";
            // localStorage.clear();
            this.dialog.openDialog(errorMessage);
            // this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
            return throwError(() => new Error(errorMessage));
        } else if (error.status == HttpStatusCode.NotFound) {
            errorMessage = "Ce menu n'existe pas ! ou il a été supprimé";
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.ADMIN_MENUS_URL]).then(window.location.reload);
        } else {
            errorMessage = "Une erreur est survenue";
            localStorage.clear();
            this.dialog.openDialog(errorMessage);
            this.router.navigate([IConstantsURL.HOME_URL]).then(window.location.reload);
        }
        return throwError(() => new Error(errorMessage));
    }

}
