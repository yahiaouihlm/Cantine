import {IConstantsMessages} from "../constants/IConstantsMessages";
import {AuthObject} from "../models/authObject";

export class connection {


  public static checkStudentConnection(): boolean {
    let authObj = localStorage.getItem('authObject');

    if (authObj) {
      let authObject = JSON.parse(authObj);
      return authObject.role === IConstantsMessages.STUDENT_ROLE;
    } else {
      return false;
    }
  }


  public  static    getAuthObjectFromLocalStorage ()  :  AuthObject | null{
    const auth  = localStorage.getItem('authObject');
    if  (auth){
      return  JSON.parse(auth);
    }
    return null ;
  }
}
