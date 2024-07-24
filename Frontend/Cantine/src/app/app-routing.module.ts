import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {PageNotFoundComponent} from "./sharedmodule/page-not-found/page-not-found.component";
import {APP_BASE_HREF} from '@angular/common';
const routes: Routes = [
    {path: '', loadChildren: () => import('./core-cantine/core-cantine.module').then(m => m.CoreCantineModule)},
    {path: 'cantine/admin', loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)},
    {path: 'cantine/student', loadChildren: () => import('./student/student.module').then(m => m.StudentModule)},
/*
    {path:'', redirectTo:'cantine/home', pathMatch:'full'},
*/

    {path:'**', component: PageNotFoundComponent}
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule],
  //  providers: [{provide: APP_BASE_HREF, useValue: '/cantine'}]
})
export class AppRoutingModule { 
  
}
