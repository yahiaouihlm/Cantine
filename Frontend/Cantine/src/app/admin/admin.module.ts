import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MainAdminComponent} from './main-admin/main-admin.component';
import {RouterModule, Routes} from "@angular/router";
import {HomeAdminComponent} from './home-admin/home-admin.component';
import {SharedmoduleModule} from "../sharedmodule/sharedmodule.module";
import {SignUpComponent} from "../student/dashbord/sign-up/sign-up.component";
import {MainCoreCantineComponent} from "../core-cantine/main-core-cantine/main-core-cantine.component";
import {AdminSignUpComponent} from "./dashbord/admin-dashboard/sign-up/admin-sign-up.component";

const routes: Routes = [
    {


        path: '', component: MainAdminComponent, children: [
            {path: 'home', component: HomeAdminComponent},
            {path: 'meals', loadChildren: () => import('./meals/meals.module').then(m => m.MealsModule)},
            {path: 'menus', loadChildren: () => import('./menus/menus.module').then(m => m.MenusModule)},
            {
                path: 'dashboard',
                loadChildren: () => import('./dashbord/admin-dashboard/admin-dashboard.module').then(m => m.AdminDashboardModule)
            },
            {path: 'orders', loadChildren: () => import('./orders/admin-orders.module').then(m => m.AdminOrdersModule)},
            {
                path: 'students',
                loadChildren: () => import('./student-management/student-management.module').then(m => m.StudentManagementModule)
            },
            {path: '', redirectTo: 'home', pathMatch: 'full'},
        ],
    },

    {
        path: 'registerOnlyAdmin', component:MainCoreCantineComponent, children: [
            {path: 'SignUp', component: AdminSignUpComponent},
            {path: '', redirectTo: 'SignUp', pathMatch: 'full'},
        ]
    }

];
@NgModule({
    declarations: [
        MainAdminComponent,
        HomeAdminComponent,
        AdminSignUpComponent
    ],
    imports: [
        CommonModule,
        SharedmoduleModule,
        RouterModule.forChild(routes),
    ],
})
export class AdminModule {}
