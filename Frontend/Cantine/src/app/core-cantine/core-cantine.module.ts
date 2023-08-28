import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './home/home.component';
import {RouterModule, Routes} from "@angular/router";
import { MainCoreCantineComponent } from './main-core-cantine/main-core-cantine.component';
import { MealsComponent } from './meals/meals.component';
import { MenuComponent } from './menu/menu.component';

import {SharedmoduleModule} from "../sharedmodule/sharedmodule.module";

import { AuthenticationComponent } from './authentication/authentication.component';
import { ConfirmationTokenComponent } from './confirmation-token/confirmation-token.component';







const routes: Routes = [

  {path: '', component: MainCoreCantineComponent,
    children: [
      {path: 'signIn', component: AuthenticationComponent},
      {path: 'home', component: HomeComponent},
      {path: 'meals', component: MealsComponent},
      {path: 'menus', component: MenuComponent},
      {path: '', redirectTo:'cantine/home', pathMatch:'full'},

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
    AuthenticationComponent,
    ConfirmationTokenComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedmoduleModule,
  ]
})
export class CoreCantineModule { }