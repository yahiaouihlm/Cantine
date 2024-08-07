import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AllMenusComponent } from './all-menus/all-menus.component';
import {RouterModule, Routes} from "@angular/router";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";
import { NewMenuComponent } from './new-menu/new-menu.component';
import { ListMealsComponent } from './list-meals/list-meals.component';
import { UpdateMenuComponent } from './update-menu/update-menu.component';
import {FormsModule} from "@angular/forms";




const  routes:  Routes  = [
  {path: '',
    children: [
      {path: '', component: AllMenusComponent},
      {path: 'new', component: NewMenuComponent},
        {path: 'update/:id', component: UpdateMenuComponent},
    ]
  },
];



@NgModule({
  declarations: [
    AllMenusComponent,
    NewMenuComponent,
    ListMealsComponent,
    UpdateMenuComponent
  ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        SharedmoduleModule,
        FormsModule
    ]
})
export class MenusModule { }
