import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {ListMealsComponent} from "../list-meals/list-meals.component";
import {MatDialog} from "@angular/material/dialog";
import {Meal} from "../../../sharedmodule/models/meal";

@Component({
  selector: 'app-new-menu',
  templateUrl: './new-menu.component.html',
  styleUrls: ['../../../../assets/styles/new-meal.component.scss']
})

export class NewMenuComponent {
  private   ATTENTION_MEAL_PRICE = "Attention, Vous  avez  Saisie un  Priw de 80€  Pour un Menu  !"
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
  constructor( private matDialog: MatDialog) {}


  onSubmit() {
    this.submitted = true;
    if (this.newMenu.invalid || this.mealsContainMenu.length < 2) {
      return;
    }
    if (this.newMenu.controls["price"].value > 50) {
      alert(this.ATTENTION_MEAL_PRICE)
    }

    console.log("le  formulaire  est bien  valider  et  pret  a  etre  envoyer  au  serveur");

  }

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
