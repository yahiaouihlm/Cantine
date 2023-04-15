import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
 {path:'cantine', loadChildren:() => import('./core-cantine/core-cantine.module').then(m => m.CoreCantineModule)},
 {path:'', redirectTo:'cantine/home', pathMatch:'full'}
];

@NgModule({
  imports: [
 
    RouterModule.forRoot(routes)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { 
  
}
