import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import { OrderDashbordComponent } from './order-dashbord/order-dashbord.component';




const routes: Routes = [
    {path: '', component: OrderDashbordComponent }
];

@NgModule({
  declarations: [
    OrderDashbordComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ]
})
export class AdminOrdersModule { }
