import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SignUpComponent } from './sign-up/sign-up.component';
import {RouterModule} from "@angular/router";
import {SharedmoduleModule} from "../../../sharedmodule/sharedmodule.module";
import { AdminProfileComponent } from './admin-profile/admin-profile.component';


const  routes = [
    {path: 'signUp', component: SignUpComponent},
    {path: 'profile', component: AdminProfileComponent},
];

@NgModule({
  declarations: [
    SignUpComponent,
    AdminProfileComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedmoduleModule
  ]
})
export class AdminDashboardModule { }
