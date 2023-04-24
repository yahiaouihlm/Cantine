import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MainFooterComponent} from "./main-footer/main-footer.component";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatIconModule} from "@angular/material/icon";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule} from "@angular/material/list";
import {MatBadgeModule} from "@angular/material/badge";



@NgModule({
  declarations: [
      MainFooterComponent,
  ],
  imports: [
    CommonModule,
    MatSidenavModule,
    MatIconModule,
    MatToolbarModule,
    MatListModule,
    MatBadgeModule,
  ],
  exports : [
    MainFooterComponent,
    MatSidenavModule,
    MatIconModule,
    MatToolbarModule,
    MatListModule,
    MatBadgeModule,
    ]

})
export class SharedmoduleModule { }
