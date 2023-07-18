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
  submitted = false;
  image!: File
  mealsContainMenu: Meal[] = []
  newMenu: FormGroup = new FormGroup({
    label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
    description: new FormControl('', [Validators.required, Validators.maxLength(1700), Validators.minLength(5)]),
    price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
    quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),
    image: new FormControl('', [Validators.required]),
    status: new FormControl('', [Validators.required])
  });

  constructor( private matDialog: MatDialog) {}
  onOpenDialogClick() {
    const result = this.matDialog.open(ListMealsComponent);
    result.afterClosed().subscribe((result) => {
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
