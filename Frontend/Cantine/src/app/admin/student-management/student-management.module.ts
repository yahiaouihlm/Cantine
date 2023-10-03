import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentsHandlerComponent } from './students-handler/students-handler.component';
import {RouterModule, Routes} from "@angular/router";
import {OrderDashboardComponent} from "../orders/order-dashbord/order-dashboard.component";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";





const routes: Routes = [
  {path: '', component: StudentsHandlerComponent }
];
@NgModule({
  declarations: [
    StudentsHandlerComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedmoduleModule,
  ]
})
export class StudentManagementModule { }
