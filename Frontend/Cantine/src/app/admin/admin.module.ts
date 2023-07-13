import { NgModule } from '@angular/core';
import {APP_BASE_HREF, CommonModule} from '@angular/common';
import { MainAdminComponent } from './main-admin/main-admin.component';
import {RouterModule, Routes} from "@angular/router";
import { HomeAdminComponent } from './home-admin/home-admin.component';

import {SharedmoduleModule} from "../sharedmodule/sharedmodule.module";


const routes: Routes = [


  {path: '', component: MainAdminComponent,
    children: [
      {path: 'home', component: HomeAdminComponent},
      {path: 'meals', loadChildren:() => import('./meals/meals.module').then(m => m.MealsModule)},
      {path :'menus', loadChildren:() => import('./menus/menus.module').then(m => m.MenusModule)},
      {path: '', redirectTo: 'home', pathMatch: 'full'},
    ]
  },

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
  ],
})
export class AdminModule { }
