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
import { HomeAdminComponent } from './home-admin/home-admin.component';
import {PageNotFoundComponent} from "../sharedmodule/page-not-found/page-not-found.component";
import {MainFooterComponent} from "../sharedmodule/main-footer/main-footer.component";
import {SharedmoduleModule} from "../sharedmodule/sharedmodule.module";


const routes: Routes = [
  {path: '', component:MainAdminComponent ,
    children: [
        {path: 'home', component: HomeAdminComponent},
    ]
  },
  {path : '',  redirectTo: 'cantine/admin/home', pathMatch: 'full'},
];


@NgModule({
  declarations: [
    MainAdminComponent,
    HomeAdminComponent,

  ],

  imports: [
    CommonModule,
    RouterModule.forChild(routes),

    SharedmoduleModule,
  ]
})
export class AdminModule { }
