import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreCantineModule } from './core-cantine/core-cantine.module';
import { RouterModule } from '@angular/router';
import { PageNotFoundComponent } from './sharedmodule/page-not-found/page-not-found.component';
import {AdminModule} from "./admin/admin.module";
@NgModule({
  declarations: [
    AppComponent,
    PageNotFoundComponent,
  ],
  imports: [
    BrowserModule,
    RouterModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    AdminModule,

  ],

  providers: [],
  exports: [
    PageNotFoundComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
