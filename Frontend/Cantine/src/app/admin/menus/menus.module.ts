import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AllMenusComponent } from './all-menus/all-menus.component';
import {RouterModule, Routes} from "@angular/router";
import {SharedmoduleModule} from "../../sharedmodule/sharedmodule.module";
import { NewMenuComponent } from './new-menu/new-menu.component';




const  routes:  Routes  = [
  {path: '',
    children: [
      {path: '', component: AllMenusComponent},
      {path: '\'update/:id\'', component: NewMenuComponent},
    ]
  },
];



@NgModule({
  declarations: [
    AllMenusComponent,
    NewMenuComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
      SharedmoduleModule
  ]
})
export class MenusModule { }
