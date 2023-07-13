import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AllMenusComponent } from './all-menus/all-menus.component';
import {Routes} from "@angular/router";
import {AllMealsComponent} from "../meals/all-meals/all-meals.component";
import {NewMealComponent} from "../meals/new-meal/new-meal.component";
import {UpdateMealComponent} from "../meals/update-meal/update-meal.component";




const  routes:  Routes  = [
  {path: '',
    children: [
      {path: '', component: AllMealsComponent},
    ]
  },
];



@NgModule({
  declarations: [
    AllMenusComponent
  ],
  imports: [
    CommonModule
  ]
})
export class MenusModule { }
