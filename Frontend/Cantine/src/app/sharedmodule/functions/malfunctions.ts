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
        console.log("user  is connected");
    }

    public static checkStudentConnectivity(router: Router ,  sharedService : SharedService)  {
        let  interdiction  = () => {
            localStorage.clear();
            router.navigate(['cantine/signIn']).then(r =>
                window.location.reload()
            );
        }

        let authObj = localStorage.getItem('authObject');
        if (!authObj) {
            interdiction();
            return
        }
        else  {
            let authObject = JSON.parse(authObj) as AuthObject;
            let  studentId = authObject.id
           let result =   sharedService.getStudentById(studentId).subscribe({
                next: (response) => {
                    return true;
                },
                error: (error) => {
                    interdiction();
                }
            });

        }


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