import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './home/home.component';
import {RouterModule, Routes} from "@angular/router";
import { MainCoreCantineComponent } from './main-core-cantine/main-core-cantine.component';
import { MealsComponent } from './meals/meals.component';
import { MenuComponent } from './menu/menu.component';

const routes: Routes = [
  {path: '', component: MainCoreCantineComponent, 
      children: [
        {path: 'home', component: HomeComponent},
        {path: 'meals', component: MealsComponent},
        {path: 'menu', component: MenuComponent},
      ]
}
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
  ]
})
export class CoreCantineModule { }
