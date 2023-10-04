import {Component, Inject, OnInit} from '@angular/core';
import {Order} from "../../../../sharedmodule/models/order";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {
  SuccessfulDialogComponent
} from "../../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {Meal} from "../../../../sharedmodule/models/meal";
import {Menu} from "../../../../sharedmodule/models/menu";

@Component({
  selector: 'app-modify-order-dialogue',
  templateUrl: './modify-order-dialogue.component.html',
  styles: [
  ]
})
export class ModifyOrderDialogueComponent {

  /*

     regler le  probléme  de  supprission  de commande quans  il ya  toutes  erreur

     regler le  problem  de exception  back  quand je fais une  erreur  de  un plat invalid
   */

  constructor(@Inject(MAT_DIALOG_DATA) public data :  Order,  private dialogRef: MatDialogRef<ModifyOrderDialogueComponent>) { }
  order: Order = this.data;


  removeMealFromOrder(meal: Meal ) {
    this.order =  Order.removeMealFromOrder(meal);
  }

  removeMenuFromOrder(menu: Menu ) {
    this.order =  Order.removeMenuFromOrder(menu);
  }

  removeOrder() {
    Order.clearOrder();
    this.dialogRef.close();
  }
}
