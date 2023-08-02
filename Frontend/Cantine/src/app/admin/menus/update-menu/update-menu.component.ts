import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {MenusService} from "../menus.service";
import {Menu} from "../../../sharedmodule/models/menu";

@Component({
  selector: 'app-update-menu',
  templateUrl:'./update-menu.component.html',
  styleUrls: ['../../../../assets/styles/new-meal.component.scss'],
    providers: [MenusService]
})
export class UpdateMenuComponent   implements  OnInit{

  submitted = false;
  image!: File;
  menu: Menu =  new Menu()
  updatedMenu: FormGroup = new FormGroup({
    label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
    description: new FormControl('', [Validators.required, Validators.maxLength(1700), Validators.minLength(5)]),
    price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
    quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),
    image: new FormControl('', [Validators.required]),
    status: new FormControl('', [Validators.required]),
  });

  constructor(private  route :  ActivatedRoute, private  menuService :  MenusService) {}
  ngOnInit(): void {

    const param = this.route.snapshot.paramMap.get('id');
    if (param) {
      const id = +param;
      this.menuService.getMenuById(id).subscribe(data => {
        this.menu = data;
        this.matchFormsValue()
      });

    }


  }


  matchFormsValue() {
    function getStatusFromNumber(pstatus: number): string {
      if (pstatus == 1) {
        return "available"
      } else {
        return "unavailable"
      }
    }

    this.updatedMenu.patchValue({
      label: this.menu.label,
      description: this.menu.description,
      price: this.menu.price,
      quantity: this.menu.quantity,

      status: getStatusFromNumber(this.menu.status)
    });
  }



  get f(): { [key: string]: AbstractControl } {
    return this.updatedMenu.controls;
  }
  onChange = ($event: Event) => {
    const target = $event.target as HTMLInputElement;
    const file: File = (target.files as FileList)[0]
    this.image = file;
  }


}
