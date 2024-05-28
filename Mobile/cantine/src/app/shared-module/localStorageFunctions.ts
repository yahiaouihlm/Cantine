import {Router} from "@angular/router";
import {IConstantsURL} from "./constants/IConstantsURL";
import {IConstantsMessages} from "./constants/IConstantsMessages";
export class localStorageFunctions {


  checkUserConnection(router: Router) {
    let authObj = localStorage.getItem('authObject');
    if (authObj) {
      let authObject = JSON.parse(authObj);
     if (authObject.role === IConstantsMessages.STUDENT_ROLE) {
        router.navigate([IConstantsURL.HOME_URL]).then(() => {
          window.location.reload();
        });
      }
    }
  }



}
