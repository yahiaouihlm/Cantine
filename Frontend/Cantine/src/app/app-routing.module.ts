import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {PageNotFoundComponent} from "./globalComponents/page-not-found/page-not-found.component";

const routes: Routes = [
    {path:  'cantine/admin', loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule)},
    {path:'cantine', loadChildren:() => import('./core-cantine/core-cantine.module').then(m => m.CoreCantineModule)},
    {path:'', redirectTo:'cantine/home', pathMatch:'full'},
    {path:'**', component: PageNotFoundComponent}
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { 
  
}
