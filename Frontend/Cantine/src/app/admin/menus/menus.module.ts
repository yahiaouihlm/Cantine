import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AllMenusComponent } from './all-menus/all-menus.component';
import {RouterModule, Routes} from "@angular/router";
import {AllMealsComponent} from "../meals/all-meals/all-meals.component";
import {NewMealComponent} from "../meals/new-meal/new-meal.component";
import {UpdateMealComponent} from "../meals/update-meal/update-meal.component";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";




const  routes:  Routes  = [
  {path: '',
    children: [
      {path: '', component: AllMenusComponent},
    ]
  },
];



@NgModule({
  declarations: [
    AllMenusComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
      SharedmoduleModule
  ]
})
export class MenusModule { }
