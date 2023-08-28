import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SignUpComponent } from './dashbord/sign-up/sign-up.component';
import {RouterModule, Routes} from "@angular/router";
import {SharedmoduleModule} from "../sharedmodule/sharedmodule.module";
import { MainStudentComponent } from './main-student/main-student.component';



const routes: Routes = [
  {
    path: '', component: MainStudentComponent,
    children: [
      {path: 'sign-up', component: SignUpComponent},
    ]
  }
];

@NgModule({
  declarations: [
    SignUpComponent,
    MainStudentComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    SharedmoduleModule,
  ]
})
export class StudentModule { }
