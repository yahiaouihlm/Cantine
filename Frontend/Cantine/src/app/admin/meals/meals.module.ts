import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewMealComponent } from './new-meal/new-meal.component';
import {RouterModule, Routes} from "@angular/router";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatRadioModule} from "@angular/material/radio";
import {ValidatorDialogComponent} from "./validator-dialog/validator-dialog.component";
import { AllMealsComponent } from './all-meals/all-meals.component';






const routes: Routes = [
  {path: '' , component:  AllMealsComponent,  /*TODO  make  this compenet  as the  main  shell  of our module    */
    children: [
      {path: 'new', component: NewMealComponent},
    ]
  },

  {path : 'meals',  redirectTo: 'all', pathMatch: 'full'}

];
@NgModule({
  declarations: [
    NewMealComponent,
      ValidatorDialogComponent,
      AllMealsComponent

  ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedmoduleModule,


    ]
})
export class MealsModule { }
