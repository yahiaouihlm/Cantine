import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-validator-dialog',
  templateUrl: './validator-dialog.component.html',
  styles: [
  ]
})
export class ValidatorDialogComponent {
    constructor(@Inject(MAT_DIALOG_DATA) public data: { message: string},  private dialogRef: MatDialogRef<ValidatorDialogComponent>) { }
      message: string = this.data.message;

    validate(): void {
    this.dialogRef.close(true);
    }
}
