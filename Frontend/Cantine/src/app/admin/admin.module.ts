import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MainAdminComponent } from './main-admin/main-admin.component';
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatIconModule} from "@angular/material/icon";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule} from "@angular/material/list";
import {MatBadgeModule} from "@angular/material/badge";
import {RouterModule, Routes} from "@angular/router";
import {CoreCantineModule} from "../core-cantine/core-cantine.module";


const routes: Routes = [
  {path: '', component: MainAdminComponent,
    children: [

    ]
  },

  {path:'', redirectTo:'cantine/home', pathMatch:'full'},
];


@NgModule({
  declarations: [
    MainAdminComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),

    MatSidenavModule,
    MatIconModule,
    MatToolbarModule,
    MatListModule,
    MatBadgeModule,
    CoreCantineModule,
  ]
})
export class AdminModule { }
