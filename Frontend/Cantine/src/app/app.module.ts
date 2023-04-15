import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreCantineModule } from './core-cantine/core-cantine.module';
import { RouterModule } from '@angular/router';
import { PageNotFoundComponent } from './GlobalComponents/page-not-found/page-not-found.component';
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
    CoreCantineModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
