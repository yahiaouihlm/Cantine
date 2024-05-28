import { NgModule } from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {SignInComponent} from "./sign-in/sign-in.component";
import {SharedModuleModule} from "../shared-module/shared-module.module";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgClass} from "@angular/common";



const  routes: Routes = [
  {path: 'sign-in',component:SignInComponent  },
];
@NgModule({
  declarations: [
    SignInComponent,
  ],
    imports: [
      ReactiveFormsModule,
      FormsModule,
        RouterModule.forChild(routes),
        SharedModuleModule,


    ]
})
export class StudentModule { }
