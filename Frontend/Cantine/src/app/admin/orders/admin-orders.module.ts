import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule, Routes} from "@angular/router";
import { OrderDashboardComponent } from './order-dashbord/order-dashboard.component';
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";





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
        SharedmoduleModule,
    ]
})
export class AdminOrdersModule { }
