import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../sharedmodule/functions/validation";
import {StudentsManagementService} from "../students-management.service";

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

    constructor(@Inject(MAT_DIALOG_DATA) public data: { message: string, userid: number }, private dialogRef: MatDialogRef<EditStudentWalletDialogComponent>, private studentsManagementService: StudentsManagementService) {
    }

    onSubmit(): void {
        this.submitted = true;
        if (this.amountForm.invalid) {
            return;
        }
        this.isLoaded = true;
        this.studentsManagementService.sendStudentWallet(15, this.amountForm.value.amount).subscribe(data => {
            this.isLoaded = false;
        }, error => {
            this.isLoaded = false;
        });
    }

    get f(): { [key: string]: AbstractControl } {
        return this.amountForm.controls;
    }


}
