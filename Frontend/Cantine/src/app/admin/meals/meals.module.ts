import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewMealComponent } from './new-meal/new-meal.component';
import {RouterModule, Routes} from "@angular/router";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatRadioModule} from "@angular/material/radio";
import {ValidatorDialogComponent} from "./validator-dialog/validator-dialog.component";
import { AllMealsComponent } from './all-meals/all-meals.component';
import { MainMealsComponent } from './main-meals/main-meals.component';
import {MealServiceService} from "./meal-service.service";






const routes: Routes = [

  {path: '',
    children: [
      {path: '', component: AllMealsComponent},
      {path: 'new', component: NewMealComponent},
    ]
  },


  //
];
@NgModule({
  declarations: [
    NewMealComponent,
      ValidatorDialogComponent,
      AllMealsComponent,
      MainMealsComponent

  ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedmoduleModule,
    ],
  providers: [MealServiceService],
})
export class MealsModule { }
