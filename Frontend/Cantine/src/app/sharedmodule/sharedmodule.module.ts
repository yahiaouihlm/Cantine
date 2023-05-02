import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {MainFooterComponent} from "./main-footer/main-footer.component";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatIconModule} from "@angular/material/icon";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule} from "@angular/material/list";
import {MatBadgeModule} from "@angular/material/badge";
import {ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatFormFieldModule} from "@angular/material/form-field";
import {MatDialogActions, MatDialogContent, MatDialogModule, MatDialogTitle} from "@angular/material/dialog";
import {MatCardModule} from "@angular/material/card";
import {HttpClientModule} from "@angular/common/http";
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatRadioModule} from "@angular/material/radio";
import {MatButton, MatButtonModule} from "@angular/material/button";



@NgModule({
  declarations: [
      MainFooterComponent,
  ],
  imports: [
    MatSidenavModule,
    MatIconModule,
    MatToolbarModule,
    MatListModule,
    MatBadgeModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatDialogModule,
    MatCardModule,
    HttpClientModule,
    MatFormFieldModule,
    MatOptionModule,
    MatSelectModule,

  ],
  exports : [
    MainFooterComponent,
    MatSidenavModule,
    MatIconModule,
    MatToolbarModule,
    MatListModule,
    MatBadgeModule,
    ReactiveFormsModule,
    MainFooterComponent,
    MatCheckboxModule,
    MatRadioModule,
    MatCardModule,
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatDialogModule,

  ]

})
export class SharedmoduleModule { }
