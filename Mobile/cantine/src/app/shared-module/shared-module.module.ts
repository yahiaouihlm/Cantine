import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CommonModule, NgClass} from '@angular/common';
import {MainCoreCantineComponent} from "./main-core-cantine/main-core-cantine.component";
import {
  IonButton,
  IonButtons,
  IonContent,
  IonHeader,
  IonicModule, IonIcon,
  IonInput,
  IonItem, IonLabel,
  IonText, IonTitle,
  IonToolbar
} from "@ionic/angular";
import {RouterOutlet} from "@angular/router";
import {HttpClient, HttpClientModule} from "@angular/common/http";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";



@NgModule({
  declarations: [
    MainCoreCantineComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [
    CommonModule,
    IonicModule,
    RouterOutlet,
  ],
  exports: [CommonModule,ReactiveFormsModule , FormsModule,NgClass,HttpClientModule  , IonHeader ,  IonToolbar ,  IonButtons , IonButton ,  IonContent , IonInput, IonText, IonItem,IonLabel,IonIcon, IonTitle ]
})
export class SharedModuleModule { }
