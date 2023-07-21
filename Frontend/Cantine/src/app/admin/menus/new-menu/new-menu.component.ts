import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {ListMealsComponent} from "../list-meals/list-meals.component";
import {MatDialog} from "@angular/material/dialog";
import {Meal} from "../../../sharedmodule/models/meal";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {MenusService} from "../menus.service";

@Component({
  selector: 'app-new-menu',
  templateUrl: './new-menu.component.html',
  styleUrls: ['../../../../assets/styles/new-meal.component.scss'] ,
  providers : [MenusService]
})

export class NewMenuComponent {
  private   ATTENTION_MEAL_PRICE = "Attention, Vous  avez  Saisie un  Priw de 80€  Pour un Menu  !"
  private   WOULD_YOU_LIKE_TO_SAVE_THIS_MENU = "Voulez-vous  enregistrer  ce  menu ?"

  submitted = false;
  image!: File
  mealsContainMenu:  Meal[] = []
  newMenu: FormGroup = new FormGroup({
    label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
    description: new FormControl('', [Validators.required, Validators.maxLength(1700), Validators.minLength(5)]),
    price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
    quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),
    image: new FormControl('', [Validators.required]),
    status: new FormControl('', [Validators.required]),
  });

  closedDialog   =  false;
  constructor( private matDialog: MatDialog , private  menusService   : MenusService  ) {}


  onSubmit() {
    /*this.submitted = true;
    if (this.newMenu.invalid || this.mealsContainMenu.length < 2) {
      return;
    }
    if (this.newMenu.controls["price"].value > 50) {
      alert(this.ATTENTION_MEAL_PRICE)
    }*/
    this.confirmAndSendNewMeal();
  }




  confirmAndSendNewMeal(): void {
    const result = this.matDialog.open(ValidatorDialogComponent, {
      data: {message: this.WOULD_YOU_LIKE_TO_SAVE_THIS_MENU},
      width: '40%',
    });

    result.afterClosed().subscribe((result) => {
      if (result != undefined && result == true) {
        this.sendNewMenu();
      } else {
        return;
      }
    });


  }


  sendNewMenu() :  void  {
/*
    const  formData = new FormData();
    formData.append("label", this.newMenu.controls["label"].value);
    formData.append("description", this.newMenu.controls["description"].value);
    formData.append("price", this.newMenu.controls["price"].value);
    formData.append("quantity", this.newMenu.controls["quantity"].value);
    formData.append("image", this.image);
    formData.append('status', this.newMenu.controls['status'].value === "available" ? "1" : "0");
    formData.append("meals", JSON.stringify(this.mealsContainMenu));
*/

    const  formData = new FormData();
    formData.append("label", "label");
    formData.append("description", "description");
    formData.append("price", "1.23");
    formData.append("quantity", "4");
    formData.append("image", this.image);
    formData.append('status', "1");
    formData.append("meals", JSON.stringify([1 , 2 , 3]));



    this.menusService.sendMenu(formData).subscribe((result) => {
      if (result != undefined && result.message === "MENU ADDED SUCCESSFULLY") {
        console.log("Bien  Ajouter")
      }
    })
  }








  // dialog to open  the  list  of  meals  and  select  the  meals  to  add  to  the  menu
  onOpenListMealDialog() {
    this.closedDialog = false;
    const result = this.matDialog.open(ListMealsComponent);
    result.afterClosed().subscribe((result) => {
      this.closedDialog = true;
      if (result === undefined)
        this.mealsContainMenu = []
      else {
        this.mealsContainMenu = result;
      }

    // this.clicked = true;
    })

  }


  onChange = ($event: Event) => {
    const target = $event.target as HTMLInputElement;
    const file: File = (target.files as FileList)[0]
    this.image = file;
  }


  get f(): { [key: string]: AbstractControl } {
    return this.newMenu.controls;
  }


}
