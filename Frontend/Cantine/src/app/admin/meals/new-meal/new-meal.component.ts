import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-new-meal',
  templateUrl: './new-meal.component.html',
  styles: [
  ]
})
export class NewMealComponent {

  submitted = false;
  newMeal: FormGroup = new FormGroup({
    label : new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
    description: new FormControl('', [Validators.required, Validators.maxLength(400), Validators.minLength(5)]),
    price: new FormControl('' , [Validators.required]),
    quantity: new FormControl('' , [Validators.required]),
    category: new FormControl('', [Validators.required]),
    Image: new FormControl('' ,  [Validators.required]),
    status :  new FormControl('', [Validators.required])
  });

  onSubmit() {

  }

  get f(): { [key: string]: AbstractControl } {
    return this.newMeal.controls;
  }












}
