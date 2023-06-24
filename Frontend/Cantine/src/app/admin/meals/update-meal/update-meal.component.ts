import {Component, OnInit} from '@angular/core';
import {Meal} from "../../../sharedmodule/models/meal";
import {MealServiceService} from "../meal-service.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Observable, of} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-update-meal',
  templateUrl: './update-meal.component.html',
  styles: [],
  providers: [MealServiceService]
})
export class UpdateMealComponent  implements OnInit{

    meal : Meal = new Meal();
    submitted!  :  boolean ;
    updatedMeal: FormGroup = new FormGroup({
        label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(1600), Validators.minLength(5)]),
        category: new FormControl('', [Validators.required, Validators.maxLength(44), Validators.minLength(3)]),
        price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
        quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),
        image: new FormControl('', [Validators.required]),
        status: new FormControl('', [Validators.required])
    });

  constructor( private mealServiceService : MealServiceService , private  router  :  Router , private route: ActivatedRoute, ) { }

  ngOnInit(): void {
    const param = this.route.snapshot.paramMap.get('id');
    if (param) {
        const id = +param;
        this.mealServiceService.getMealById(id).subscribe(data => {
            this.meal = data;
            console.log(this.meal);
        });

    }

  }





}
