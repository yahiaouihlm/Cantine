import {AuthObject} from "../models/authObject";
import {Router} from "@angular/router";
import {SharedService} from "../shared.service";

export  default class Malfunctions {


    public static checkStudentConnectivity(router: Router ,  sharedService : SharedService)  {
        let  interdiction  = () => {
            localStorage.clear();
            router.navigate(['cantine/signIn']).then(r =>
                window.location.reload());
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

}