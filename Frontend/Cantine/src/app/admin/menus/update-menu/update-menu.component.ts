import { Component } from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-update-menu',
  templateUrl:'./update-menu.component.html',
  styleUrls: ['../../../../assets/styles/new-meal.component.scss'],
})
export class UpdateMenuComponent {

  submitted = false;

  image!: File;
  updatedMenu: FormGroup = new FormGroup({
    label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
    description: new FormControl('', [Validators.required, Validators.maxLength(1700), Validators.minLength(5)]),
    price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
    quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),

    status: new FormControl('', [Validators.required]),
  });


  get f(): { [key: string]: AbstractControl } {
    return this.updatedMenu.controls;
  }
  onChange = ($event: Event) => {
    const target = $event.target as HTMLInputElement;
    const file: File = (target.files as FileList)[0]
    this.image = file;
  }

}
