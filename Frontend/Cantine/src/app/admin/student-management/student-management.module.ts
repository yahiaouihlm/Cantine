import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentsHandlerComponent } from './students-handler/students-handler.component';
import {RouterModule, Routes} from "@angular/router";
import {OrderDashboardComponent} from "../orders/order-dashbord/order-dashboard.component";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";
import { ManageStudentWalletComponent } from './manage-student-wallet/manage-student-wallet.component';
import { EditStudentWalletDialogComponent } from './edit-student-wallet-dialog/edit-student-wallet-dialog.component';





const routes: Routes = [
  {path: '', component: StudentsHandlerComponent },
  {path: 'profile', component: ManageStudentWalletComponent }
];
@NgModule({
  declarations: [
    StudentsHandlerComponent,
    ManageStudentWalletComponent,
    EditStudentWalletDialogComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedmoduleModule,
  ]
})
export class StudentManagementModule { }
