import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {OrderDashbordComponent} from './order-dashbord/order-dashbord.component';
import {RouterModule, Routes} from "@angular/router";
import {MainCoreCantineComponent} from "../../core-cantine/main-core-cantine/main-core-cantine.component";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";


const routes: Routes = [
    {
        path: '', component: OrderDashbordComponent,
        children: []
    }
];

@NgModule({
    declarations: [
        OrderDashbordComponent
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedmoduleModule
    ]
})
export class OrderModule {
}
