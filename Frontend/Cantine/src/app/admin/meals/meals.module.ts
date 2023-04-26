import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NewMealComponent } from './new-meal/new-meal.component';
import {RouterModule, Routes} from "@angular/router";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatRadioModule} from "@angular/material/radio";




const routes: Routes = [
  {path: '' ,
    children: [
      {path: 'new', component: NewMealComponent},
    ]
  },

  {path : '',  redirectTo: 'cantine/admin/home', pathMatch: 'full'}

];
@NgModule({
  declarations: [
    NewMealComponent
  ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedmoduleModule,


    ]
})
export class MealsModule { }
