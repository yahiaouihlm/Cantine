import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-exception-dialog',
  templateUrl:"./exception-dialog.component.html",
  styles: []
})
export class ExceptionDialogComponent {


  constructor(@Inject(MAT_DIALOG_DATA) public data: { message: string},  private dialogRef: MatDialogRef<ExceptionDialogComponent>) { }
  message: string = this.data.message;

    close(): void {
    this.dialogRef.close(true);
    }


}
