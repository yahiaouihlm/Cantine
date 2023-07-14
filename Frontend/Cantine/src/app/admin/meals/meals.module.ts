import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewMealComponent } from './new-meal/new-meal.component';
import {RouterModule, Routes} from "@angular/router";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatRadioModule} from "@angular/material/radio";
import {ValidatorDialogComponent} from "../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import { AllMealsComponent } from './all-meals/all-meals.component';
import { MainMealsComponent } from './main-meals/main-meals.component';
import {MealsService} from "./meals.service";
import { UpdateMealComponent } from './update-meal/update-meal.component';
import { ExceptionDialogComponent } from '../../sharedmodule/dialogs/exception-dialog/exception-dialog.component';







const routes: Routes = [

  {path: '',
    children: [
      {path: '', component: AllMealsComponent},
      {path: 'new', component: NewMealComponent},
      {path: 'update/:id', component: UpdateMealComponent},
    ]
  },


  //
];
@NgModule({
  declarations: [
    NewMealComponent,
      ValidatorDialogComponent,
      AllMealsComponent,
      MainMealsComponent,
      UpdateMealComponent,
      ExceptionDialogComponent,


  ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedmoduleModule,
    ],
  providers: [MealsService],
})
export class MealsModule { }
