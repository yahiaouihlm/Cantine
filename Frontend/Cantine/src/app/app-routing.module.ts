import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {PageNotFoundComponent} from "./sharedmodule/page-not-found/page-not-found.component";

const routes: Routes = [
    {path: '', loadChildren: () => import('./core-cantine/core-cantine.module').then(m => m.CoreCantineModule)},
    {path: 'cantine/admin', loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)},
    {path: 'cantine/student', loadChildren: () => import('./student/student.module').then(m => m.StudentModule)},
    {path:'**', component: PageNotFoundComponent}
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule],
})

export class AppRoutingModule {
  
}
