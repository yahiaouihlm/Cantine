import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule} from "@angular/router";
import {SharedmoduleModule} from "../../../sharedmodule/sharedmodule.module";
import { AdminProfileComponent } from './admin-profile/admin-profile.component';


const  routes = [
  {path: 'profile', component: AdminProfileComponent},

];

@NgModule({
  declarations: [
    AdminProfileComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedmoduleModule
  ]
})
export class AdminDashboardModule { }
