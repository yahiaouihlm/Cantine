import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams, HttpStatusCode} from "@angular/common/http";
import {Meal} from "../sharedmodule/models/meal";
import {Menu} from "../sharedmodule/models/menu";
import {Login} from "../sharedmodule/models/login";
import {AuthObject} from "../sharedmodule/models/authObject";


@Injectable()
export class CoreCantineService {
    private BASIC_URL = "http://localhost:8080/cantine/api/getAll/";
    private USER_AUTHENTICATION_ENDPOINT = "http://localhost:8080/" + 'login';
    private GET_ALL_MEALS_ENDPOINT = this.BASIC_URL + 'meals'
    private GET_ALL_MENUS_ENDPOINT = this.BASIC_URL + 'menus';
    private GET_MEALS_BY_TYPE_ENDPOINT = this.BASIC_URL + 'getMealsByType';
    private  GET_MENUS_BY_TERM_ENDPOINT = this.BASIC_URL + '/menus/contains';


    constructor(private httpClient: HttpClient) {
    }


    userAuthentication(login: Login) {
        return this.httpClient.post<AuthObject>(this.USER_AUTHENTICATION_ENDPOINT, login);
    }

    getAllAvailableMeals() {
        return this.httpClient.get<Meal[]>(this.GET_ALL_MEALS_ENDPOINT)
    }

    getAllMenus() {
        return this.httpClient.get<Menu[]>(this.GET_ALL_MENUS_ENDPOINT)
    }

    getMealsByType(type: string) {
           let params = new HttpParams().set('mealType', type);
            return this.httpClient.get<Meal[]>(this.GET_MEALS_BY_TYPE_ENDPOINT, {params: params})
    }

    searchMenusByTerm(term: string) {
        let params = new HttpParams().set('term', term);
        return this.httpClient.get<Menu[]>(this.GET_MENUS_BY_TERM_ENDPOINT, {params: params})
    }
}

