import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentsHandlerComponent } from './students-handler/students-handler.component';
import {RouterModule, Routes} from "@angular/router";
import {OrderDashboardComponent} from "../orders/order-dashbord/order-dashboard.component";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";
import { ManageStudentComponent } from './manage-student-wallet/manage-student.component';
import { EditStudentWalletDialogComponent } from './edit-student-wallet-dialog/edit-student-wallet-dialog.component';
import { StudentTransactionHistoryComponent } from './student-transaction-history/student-transaction-history.component';






const routes: Routes = [
  {path: '', component: StudentsHandlerComponent },
  {path: 'profile', component: ManageStudentComponent },
    {path: 'transaction-history', component: StudentTransactionHistoryComponent }
];
@NgModule({
  declarations: [
    StudentsHandlerComponent,
    ManageStudentComponent,
    EditStudentWalletDialogComponent,
    StudentTransactionHistoryComponent
  ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedmoduleModule,
    ]
})
export class StudentManagementModule { }
