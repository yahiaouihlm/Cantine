import { NgModule } from '@angular/core';
import {MainCoreCantineComponent} from "../shared-module/main-core-cantine/main-core-cantine.component";
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./home/home.component";
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import {SharedModuleModule} from "../shared-module/shared-module.module";
import {NgForOf} from "@angular/common";
import {IonicModule} from "@ionic/angular";


const routes: Routes = [
  { path: 'cantine', component: MainCoreCantineComponent,
    children: [
      { path: 'home', component: HomeComponent },

      {path: '', redirectTo:'cantine/home', pathMatch:'full'}
    ]
  },
  { path: '', redirectTo: 'cantine/home', pathMatch: 'full' },
];
@NgModule({
  declarations: [
    HomeComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [
    RouterModule.forChild(routes),
    SharedModuleModule,
    NgForOf,
    IonicModule,
  ]
})
export class CoreCantineModule { }
