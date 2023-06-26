import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-successful-dialog',
  templateUrl: './successful-dialog.component.html',
  styles: [
  ]
})
export class SuccessfulDialogComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { message: string},  private dialogRef: MatDialogRef<SuccessfulDialogComponent>) { }
  message: string = this.data.message;

  validate(): void {
    this.dialogRef.close(true);
  }

}



