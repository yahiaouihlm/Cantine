import {Component, OnInit} from '@angular/core';
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {ActivatedRoute, Router} from "@angular/router";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";
import {StudentsManagementService} from "../students-management.service";
import {User} from "../../../sharedmodule/models/user";
import {Observable, of} from "rxjs";
import {Transaction} from "../../../sharedmodule/models/Transaction";

@Component({
  selector: 'app-student-transaction-history',
  templateUrl: './student-transaction-history.component.html',
  styles: [],
    providers: [StudentsManagementService]
})
export class StudentTransactionHistoryComponent  implements  OnInit{

  transactions$: Observable<Transaction[]> = of([]);

  constructor(private router: Router , private route : ActivatedRoute ,private studentsManagementService :StudentsManagementService ) {}

  ngOnInit(): void {
    if (!Malfunctions.checkAdminConnectivityAndMakeRedirection(this.router)) {
      return;
    }
    this.route.queryParams.subscribe(param => {
      const studentUuid = param['studentUuid'];
      if (studentUuid == undefined) {
        this.router.navigate([IConstantsURL.ADMIN_STUDENTS_URL]).then(r => window.location.reload());
      }
      this.transactions$ = this.studentsManagementService.getStudentTransactions(studentUuid);
      console.log(this.transactions$);
    });



  }

}
