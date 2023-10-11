import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {StudentsManagementService} from "../students-management.service";
import {
    NgOtpInputDialogComponent
} from "../../../sharedmodule/dialogs/ng-otp-input-dialog/ng-otp-input-dialog.component";


@Component({
    selector: 'app-editer-salaire-etudiant-dialog',
    templateUrl: `./edit-student-wallet-dialog.component.html`,
    styles: [],
    providers: [StudentsManagementService]
})
export class EditStudentWalletDialogComponent {

    submitted: boolean = false;
    isLoaded: boolean = false;
    amountForm: FormGroup = new FormGroup({
        amount: new FormControl('', [Validators.required, Validators.max(200), Validators.min(10)]),
    });


    constructor(@Inject(MAT_DIALOG_DATA) public data: { message: string, userid: number }, private dialogRef: MatDialogRef<EditStudentWalletDialogComponent>, private studentsManagementService: StudentsManagementService, private matDialog: MatDialog) {
    }

    onSubmit(): void {

        this.submitted = true;
        if (this.amountForm.invalid) {
            return;
        }
        this.isLoaded = true;
        this.dialogRef.close(this.amountForm.value.amount);

    }


    // Ouvrir  un  dialogue  de  code   5  elements
    getValidationCode() {
        this.matDialog.open(NgOtpInputDialogComponent, {
            width: "50vw",
            height: "25vh"
        });
    }


    get f(): { [key: string]: AbstractControl } {
        return this.amountForm.controls;
    }


}
