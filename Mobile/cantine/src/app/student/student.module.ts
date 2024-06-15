import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {SignInComponent} from "./sign-in/sign-in.component";
import {SharedModuleModule} from "../shared-module/shared-module.module";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {IonHeader, IonicModule, IonToolbar} from "@ionic/angular";
import {ProfileComponent} from "./profile/profile.component";




const  routes: Routes = [
  {path: 'sign-in',component:SignInComponent  },
  {path: 'profile' , component:ProfileComponent}
];
@NgModule({
  declarations: [
    SignInComponent,
    ProfileComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
    imports: [
        ReactiveFormsModule,
        FormsModule,
        RouterModule.forChild(routes),
        SharedModuleModule,
        IonicModule,
    ]
})
export class StudentModule { }
