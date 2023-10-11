import { NgModule } from '@angular/core';
import {MainFooterComponent} from "./main-footer/main-footer.component";
import {MatSidenavModule} from "@angular/material/sidenav";
import {MatIconModule} from "@angular/material/icon";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatListModule} from "@angular/material/list";
import {MatBadgeModule} from "@angular/material/badge";
import {ReactiveFormsModule} from "@angular/forms";
import { MatFormFieldModule} from "@angular/material/form-field";
import {MatDialogActions, MatDialogContent, MatDialogModule, MatDialogTitle} from "@angular/material/dialog";
import {MatCardModule} from "@angular/material/card";
import {HttpClientModule} from "@angular/common/http";
import {MatOptionModule} from "@angular/material/core";
import {MatSelectModule} from "@angular/material/select";
import {MatCheckboxModule} from "@angular/material/checkbox";
import {MatRadioModule} from "@angular/material/radio";
import { SuccessfulDialogComponent } from './dialogs/successful-dialog/successful-dialog.component';
import {EuroSymbolPipe} from "./CustomPipes/euro-symbol-pipe";
import { FoodAvailablePipe } from './CustomPipes/food-available.pipe';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {NgOtpInputConfig, NgOtpInputModule} from "ng-otp-input";
import { NgOtpInputDialogComponent } from './dialogs/ng-otp-input-dialog/ng-otp-input-dialog.component';
import { LoadingDialogComponent } from './dialogs/loading-dialog/loading-dialog.component';


@NgModule({
  declarations: [
      MainFooterComponent,
      SuccessfulDialogComponent,
        EuroSymbolPipe,
        FoodAvailablePipe,
        NgOtpInputDialogComponent,
        LoadingDialogComponent,
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
    NgOtpInputModule,
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
    EuroSymbolPipe,
    FoodAvailablePipe,
    MatProgressSpinnerModule,

  ]

})
export class SharedmoduleModule { }
