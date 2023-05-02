import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './home/home.component';
import {RouterModule, Routes} from "@angular/router";
import { MainCoreCantineComponent } from './main-core-cantine/main-core-cantine.component';
import { MealsComponent } from './meals/meals.component';
import { MenuComponent } from './menu/menu.component';

import {SharedmoduleModule} from "../sharedmodule/sharedmodule.module";
import {MatListModule} from "@angular/material/list";
import {MatIconModule} from "@angular/material/icon";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatToolbarModule} from "@angular/material/toolbar";







const routes: Routes = [

  {path: '', component: MainCoreCantineComponent,
    children: [
      {path: '', redirectTo:'cantine/home', pathMatch:'full'},
      {path: 'home', component: HomeComponent},
      {path: 'meals', component: MealsComponent},
      {path: 'menu', component: MenuComponent},

    ]
  },
  {path: '', redirectTo:'cantine/home', pathMatch:'full'},

];
@NgModule({
  declarations: [
    HomeComponent,
    MainCoreCantineComponent,
    MealsComponent,
    MenuComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedmoduleModule,
  ]
})
export class CoreCantineModule { }