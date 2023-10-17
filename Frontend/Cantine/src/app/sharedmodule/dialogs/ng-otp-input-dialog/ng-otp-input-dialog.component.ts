import {Component} from '@angular/core';
import {NgOtpInputConfig} from "ng-otp-input";
import {MatDialogRef} from "@angular/material/dialog";
import {StudentsManagementService} from "../../../admin/student-management/students-management.service";

@Component({
  selector: 'app-ng-otp-input-dialog',
  template: `
    <div class="amount-dialog">
      <h2 class="center"> Veuillez Saisir le  code de Confiramtion  </h2>
      <div class="code-input">
        <ng-otp-input #ngOtpInput (onInputChange)="onOtpChange($event)" [config]="otpInputConfig"  class="center" > </ng-otp-input>
      </div>
      <div class="center"> <!--*ngIf="!isLoaded; else loadingTemplate"--> 
        <button class="btn" (click)="validate()">Valider</button>
      </div>
    </div>
  `,
  styles: [],
})
export class NgOtpInputDialogComponent {
    otp!: string;
   constructor(  private dialogRef: MatDialogRef<NgOtpInputDialogComponent>) {}
   otpInputConfig: NgOtpInputConfig = {
    length: 7,
       placeholder: "*",
    inputStyles: {
      width: '4vw',
      height: '7vh',
      border : "1px solid   black"
    }
  }



    validate(){
        this.dialogRef.close(this.otp);
    }
    onOtpChange(otp: string) {
        this.otp = otp;
    }


}
