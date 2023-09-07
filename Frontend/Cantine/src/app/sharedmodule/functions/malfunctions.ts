import {AuthObject} from "../models/authObject";
import {Router} from "@angular/router";

export  default class Malfunctions {


    public  static getTokenFromLocalStorage() :  string {
        let authObj = localStorage.getItem('authObject')
        if (!authObj) {
            return '';
        }
        let authObject = JSON.parse(authObj) as AuthObject;
        return authObject.Authorization;
    }

}