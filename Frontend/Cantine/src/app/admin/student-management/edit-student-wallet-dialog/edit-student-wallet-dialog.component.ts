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


    constructor(@Inject(MAT_DIALOG_DATA) public data: { message: string, userid: number }, private dialogRef: MatDialogRef<EditStudentWalletDialogComponent>) {
    }

    onSubmit(): void {

        this.submitted = true;
        if (this.amountForm.invalid) {
            return;
        }
        this.isLoaded = true;
        this.dialogRef.close(this.amountForm.value.amount);

    }





    get f(): { [key: string]: AbstractControl } {
        return this.amountForm.controls;
    }


}
