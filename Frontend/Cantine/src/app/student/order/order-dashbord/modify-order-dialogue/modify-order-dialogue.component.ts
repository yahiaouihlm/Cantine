import {Component, Inject, OnInit} from '@angular/core';
import {Order} from "../../../../sharedmodule/models/order";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {
  SuccessfulDialogComponent
} from "../../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";

@Component({
  selector: 'app-modify-order-dialogue',
  templateUrl: './modify-order-dialogue.component.html',
  styles: [
  ]
})
export class ModifyOrderDialogueComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data :  Order,  private dialogRef: MatDialogRef<SuccessfulDialogComponent>) { }
  order: Order = this.data;


}
