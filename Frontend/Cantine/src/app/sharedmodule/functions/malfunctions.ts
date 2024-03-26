import {AuthObject} from "../models/authObject";
import {Router} from "@angular/router";
import {SharedService} from "../shared.service";
import {IConstantsURL} from "../constants/IConstantsURL";
import {IConstantsMessages} from "../constants/IConstantsMessages";


export  default class Malfunctions {


    /**
     * @doc this function is used to check if the user is connected by checking the authObject is  in local storage redirect to the  home page of the user
     * @param router
     */

    public  static  checkUserConnection(router  : Router) {
        let authObj = localStorage.getItem('authObject');
        if (authObj) {
            let authObject = JSON.parse(authObj);
            if (authObject.role === IConstantsMessages.ADMIN_ROLE) {
                 router.navigate([IConstantsURL.ADMIN_HOME_URL]).then(() => {
                    window.location.reload();
                });
            } else if (authObject.role === IConstantsMessages.STUDENT_ROLE) {
                 router.navigate([IConstantsURL.HOME_URL]).then(() => {
                    window.location.reload();
                });
            }
        }
        
    }

    public static checkAdminConnectivityAndMakeRedirection(router: Router)  : boolean  {
        let  interdiction  = () => {
            localStorage.clear();
            router.navigate([IConstantsURL.SIGN_IN_URL]).then(r =>
                window.location.reload()
            );
        }

        let authObj = localStorage.getItem('authObject');
        if (!authObj || JSON.parse(authObj).role !== IConstantsMessages.ADMIN_ROLE) {
            interdiction();
            return  false;
        }

        /**TODO : search  if  we need a request  to  the  server  to   getAdmin */
        return  true;
    }

    public  static getTokenFromLocalStorage() :  string {
        let authObj = localStorage.getItem('authObject')

        if (!authObj) {
            return '';
        }
        let authObject = JSON.parse(authObj) as AuthObject;
        return authObject.Authorization;
    }

    public  static  getUserIdFromLocalStorage() :  string {
        let authObj = localStorage.getItem('authObject')
        if (!authObj) {
            return '';
        }
        let authObject = JSON.parse(authObj) as AuthObject;
        if  (authObject== null  || authObject.id == null){
            return '';
        }
        return authObject.id;
    }

    /**
     *    @doc this function  is used to  get  the  current  date  in  the  format  yyyy-mm-dd
     */
    public static   getCurrentDate() : string {
        const  date = new Date();
        let   getMonth = date.toLocaleString("default", { month: "2-digit" });
        let   getDay = date.toLocaleString("default", { day: "2-digit" })
        let   getYear = date.toLocaleString("default", { year: "numeric" });
        return   getYear + "-" + getMonth + "-" + getDay;
    }
}