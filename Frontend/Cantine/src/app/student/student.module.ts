import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SignUpComponent } from './dashbord/sign-up/sign-up.component';
import {RouterModule, Routes} from "@angular/router";
import {SharedmoduleModule} from "../sharedmodule/sharedmodule.module";
import { MainStudentComponent } from './main-student/main-student.component';
import { ProfileComponent } from './dashbord/profile/profile.component';
import {MainCoreCantineComponent} from "../core-cantine/main-core-cantine/main-core-cantine.component";



const routes: Routes = [
  {
    path: '', component: MainCoreCantineComponent,
    children: [
      {path: 'sign-up', component: SignUpComponent},
        {path: 'profile', component: ProfileComponent},
    ]
  }
];

@NgModule({
  declarations: [
    SignUpComponent,
    MainStudentComponent,
    ProfileComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedmoduleModule,
  ]
})
export class StudentModule { }
