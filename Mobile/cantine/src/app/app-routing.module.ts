import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import {CoreCantineModule} from "./core-cantine/core-cantine.module";

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./core-cantine/core-cantine.module').then(m => m.CoreCantineModule)
  },
  {
     path : 'cantine/student',
      loadChildren: () => import('./student/student.module').then(m => m.StudentModule)
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
