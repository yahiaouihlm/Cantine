import {Component, OnInit} from '@angular/core';
import {Meal} from "../../../sharedmodule/models/meal";
import {MealServiceService} from "../meal-service.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Observable, of} from "rxjs";
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {ValidatorDialogComponent} from "../dialogs/validator-dialog/validator-dialog.component";

@Component({
  selector: 'app-update-meal',
  templateUrl: './update-meal.component.html',
  styleUrls: ["../../../../assets/styles/new-meal.component.scss"],
  providers: [MealServiceService]
})
export class UpdateMealComponent  implements OnInit{

    meal : Meal = new Meal();
    submitted!  :  boolean ;
    image! : File
    updatedMeal: FormGroup = new FormGroup({
        label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(1600), Validators.minLength(5)]),
        category: new FormControl('', [Validators.required, Validators.maxLength(44), Validators.minLength(3)]),
        price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
        quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),
        image: new FormControl(''),
        status: new FormControl('', [Validators.required])
    });

  constructor( private mealServiceService : MealServiceService , private  router  :  Router , private route: ActivatedRoute,private matDialog: MatDialog  ) { }

  ngOnInit(): void {
    const param = this.route.snapshot.paramMap.get('id');
    if (param) {
        const id = +param;
        this.mealServiceService.getMealById(id).subscribe(data => {
            this.meal = data;
            this.matchFormsValue()
            console.log(this.meal.category)
        });

    }

  }


    onSubmit() {
        this.submitted = true
      if (this.updatedMeal.invalid) {
          return;
      }
      if (this.updatedMeal.controls["price"].value > 50) {
          alert("Attention  vous  avez  saisi  un  prix  supérieur  à  50€  pour un  plat  !")
      }

        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: " Voulez-vous vraiment  Portez Les Modification  pour ce  plat ? "},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                console.log("ok")
            } else {
                return;
            }
        });


    }


    matchFormsValue() {
        function  getStatusFromNumber ( pstatus : number ) : string {
            if (pstatus == 1) { return "available" } else { return "unavailable" }
        }
        this.updatedMeal.patchValue({
            label: this.meal.label,
            description: this.meal.description,
            price: this.meal.price,
            quantity: this.meal.quantity,
            category :  this.meal.category,
            status :getStatusFromNumber (this.meal.status)
        });
    }
    get f(): { [key: string]: AbstractControl } {
        return this.updatedMeal.controls;
    }
    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        const file: File = (target.files as FileList)[0]
        this.image = file;
    }



  editMeal()  :  void {
      const formData = new FormData();
      formData.append('id', this.meal.id.toString());
      formData.append('label', this.updatedMeal.controls['label'].value);
      formData.append('description', this.updatedMeal.controls['description'].value);
      formData.append('category', this.updatedMeal.controls['category'].value);
      formData.append('price', this.updatedMeal.controls['price'].value);
      formData.append('quantity', this.updatedMeal.controls['quantity'].value);

      if (this.image != null || this.image != undefined) // envoyer  une image  uniquement si  y'a eu  une image  !
          formData.append('image', this.image);

      if  (this.updatedMeal.controls['status'].value == "available") {
          formData.append('status', "1");
      } else {
          formData.append('status', "0");
      }

      this.mealServiceService.editMeal(formData).subscribe((data) => {});

  }


}
