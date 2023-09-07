import {AuthObject} from "../models/authObject";
import {Router} from "@angular/router";

export  class  Gfunction {


    public  getTokenFromLocalStorage() {
        let authObj = localStorage.getItem('authObject')
        if (!authObj) {
            return '';
        }
        let authObject = JSON.parse(authObj) as AuthObject;
        return authObject.Authorization;
    }

}