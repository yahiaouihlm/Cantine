import { NgModule } from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {SignInComponent} from "./sign-in/sign-in.component";
import {SharedModuleModule} from "../shared-module/shared-module.module";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgClass} from "@angular/common";
import {IonicModule} from "@ionic/angular";
import {ProfileComponent} from "./profile/profile.component";



const  routes: Routes = [
  {path: 'sign-in',component:SignInComponent  },
  {path: 'profile' , component:ProfileComponent}
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
        IonicModule,


    ]
})
export class StudentModule { }
