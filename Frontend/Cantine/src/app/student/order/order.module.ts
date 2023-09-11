import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {OrderDashboardComponent} from './order-dashbord/order-dashboard.component';
import {RouterModule, Routes} from "@angular/router";
import {MainCoreCantineComponent} from "../../core-cantine/main-core-cantine/main-core-cantine.component";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";
import { ModifyOrderDialogueComponent } from './order-dashbord/modify-order-dialogue/modify-order-dialogue.component';


const routes: Routes = [
    {
        path: '', component: OrderDashboardComponent,
        children: []
    }
];

@NgModule({
    declarations: [
        OrderDashboardComponent,
        ModifyOrderDialogueComponent
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedmoduleModule
    ]
})
export class OrderModule {
}
