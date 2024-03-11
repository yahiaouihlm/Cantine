import {Component, Inject} from '@angular/core';
import {NgOtpInputConfig} from "ng-otp-input";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {StudentsManagementService} from "../../students-management.service";
import Malfunctions from "../../../../sharedmodule/functions/malfunctions";
import {HttpStatusCode} from "@angular/common/http";
import {
    SuccessfulDialogComponent
} from "../../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {Router} from "@angular/router";
import {IConstantsURL} from "../../../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-ng-otp-input-dialog',
    template: `
        <app-loading-dialog *ngIf="isLoading"></app-loading-dialog>
        
        <div  class="amount-dialog">
          <div class="center" style="margin-top: 10px">  <h2> Veuillez Saisir le code de Confiramtion </h2></div>
            <div class="code-input">
                <ng-otp-input #ngOtpInput (onInputChange)="onOtpChange($event)" [config]="otpInputConfig"
                              class="center"></ng-otp-input>
            </div>
            <div style="margin-top: 15px" class="center"> <!--*ngIf="!isLoaded; else loadingTemplate"-->
                <button class="btn" (click)="validate()">Valider</button>
            </div>

            <div *ngIf="numberOfAttempts < 3" class="center" style="margin-top: 30px"> <!--*ngIf="!isLoaded; else loadingTemplate"-->
             <p style="color: red ;  font-weight: bolder">  il  vous  reste {{numberOfAttempts}} tentatives </p>
            </div>
        </div>

        <ng-template #loadingTemplate>
            <div class="spinner-container">
                <mat-progress-spinner mode="indeterminate"></mat-progress-spinner>
            </div>
        </ng-template>
    `,
    styles: [],
    providers: [StudentsManagementService]

})
export class NgOtpInputDialogComponent {
    otp!: string;
    numberOfAttempts = 3 ;
    wrongCode = false;
    isLoading =  true ;

    AMOUNT_ADDED_SUCCESSFULLY = "Le montant a été ajouté avec succès !"
    constructor(@Inject(MAT_DIALOG_DATA) public data: { studentUuid: string , amount : number},private dialogRef: MatDialogRef<NgOtpInputDialogComponent>, private studentsManagementService: StudentsManagementService , private matDialog: MatDialog, private router : Router) {
    }

    otpInputConfig: NgOtpInputConfig = {
        length: 7,
        placeholder: "*",
        inputStyles: {
            width: '4vw',
            height: '7vh',
            border: '2px solid ' + (this.wrongCode ? 'red' : 'black'),
            borderRadius: '10px',
        }
    }


    validate() {
        let validationCode = Number(this.otp);
        if (!validationCode){
            this.numberOfAttempts --;
            this.dialogRef.close();
        }
        this.sendStudentConfirmationCodeReq(this.data.studentUuid , this.data.amount ,  validationCode)
    }

    onOtpChange(otp: string) {
        this.otp = otp;
    }

    sendStudentConfirmationCodeReq( userUuid : string,amountToAdd: number, validationCode: number) {
        let myAdminUuid = Malfunctions.getUserIdFromLocalStorage();
        this.studentsManagementService.sendStudentCode(myAdminUuid, userUuid, amountToAdd, validationCode).subscribe({
            next: (response) => {
                this.showConfirmationDialog(this.AMOUNT_ADDED_SUCCESSFULLY);
                this.dialogRef.close();
            }
            , error: (error) => {
                if (error.status == HttpStatusCode.BadRequest){
                    this.wrongCode =  true ;
                    this.numberOfAttempts -- ;
                    if (this.numberOfAttempts  == 0){
                        this.dialogRef.close();
                    }
                }else {
                    localStorage.clear();
                    this.router.navigate([IConstantsURL.SIGN_IN_URL]).then(window.location.reload);
                }
            }
        });
    }



    showConfirmationDialog(message:  string ): void {
        const result = this.matDialog.open(SuccessfulDialogComponent, {
            data: {message: message},
            width: '40%',
        });
        result.afterClosed().subscribe((result) => {
             this.router.navigate([IConstantsURL.ADMIN_STUDENTS_URL]).then(window.location.reload)
        });
    }


}
