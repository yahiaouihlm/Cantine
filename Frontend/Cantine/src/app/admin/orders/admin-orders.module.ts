import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import { OrderDashboardComponent } from './order-dashbord/order-dashboard.component';





const routes: Routes = [
    {path: '', component: OrderDashboardComponent }
];

@NgModule({
  declarations: [
    OrderDashboardComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ]
})
export class AdminOrdersModule { }
