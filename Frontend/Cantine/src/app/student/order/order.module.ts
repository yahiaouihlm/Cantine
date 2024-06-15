import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {OrderDashboardComponent} from './order-dashbord/order-dashboard.component';
import {RouterModule, Routes} from "@angular/router";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";
import { ModifyOrderDialogueComponent } from './order-dashbord/modify-order-dialogue/modify-order-dialogue.component';
import { OrderHistoryComponent } from './order-history/order-history.component';


const routes: Routes = [
    {path: '', component: OrderDashboardComponent,},
    {path: 'history', component: OrderHistoryComponent},
];

@NgModule({
    declarations: [
        ModifyOrderDialogueComponent,
        OrderHistoryComponent,
        OrderDashboardComponent
    ],
    imports: [
        RouterModule.forChild(routes),
        CommonModule,
        SharedmoduleModule
    ]
})
export class OrderModule {
}
