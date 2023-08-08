import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SignUpComponent } from './sign-up/sign-up.component';
import {RouterModule} from "@angular/router";
import {SharedmoduleModule} from "../../../sharedmodule/sharedmodule.module";


const  routes = [
    {path: 'signUp', component: SignUpComponent},
];

@NgModule({
  declarations: [
    SignUpComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedmoduleModule
  ]
})
export class AdminDashboardModule { }
