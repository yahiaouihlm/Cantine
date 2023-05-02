import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, PatternValidator, Validators} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {ValidatorDialogComponent} from "../validator-dialog/validator-dialog.component";
import {MealServiceService} from "../meal-service.service";

@Component({
    selector: 'app-new-meal',
    templateUrl: './new-meal.component.html',
    styleUrls: ['../../../../assets/styles/new-meal.component.scss'],
    providers: [MealServiceService]
})
export class NewMealComponent {

    submitted = false;
    image!: File
    newMeal: FormGroup = new FormGroup({
        label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(1600), Validators.minLength(5)]),
        category: new FormControl('', [Validators.required, Validators.maxLength(44), Validators.minLength(3)]),
        price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
        quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),
        image: new FormControl('', [Validators.required]),
        status: new FormControl('', [Validators.required])
    });

    constructor(private mealServiceService: MealServiceService, private matDialog: MatDialog) {
    }

    onSubmit(): void {

        this.submitted = true;
        if (this.newMeal.invalid) {
            return;
        }
        if (this.newMeal.controls["price"].value > 50) {
            alert("Attention  vous  avez  saisi  un  prix  supérieur  à  50€  pour un  plat  !")
        }

        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: " Voulez-vous vraiment sauvegarder ce plat ? "},
            width: '40%',
        });


    }

    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        const file: File = (target.files as FileList)[0]
        this.image = file;
    }

    get f(): { [key: string]: AbstractControl } {
        return this.newMeal.controls;
    }


    goto(): void {
        console.log('goto');
    }


    sendNewMeal(): void {
        const formData = new FormData();
        formData.append('label', this.newMeal.controls['label'].value);
        formData.append('description', this.newMeal.controls['description'].value);
        formData.append('category', this.newMeal.controls['category'].value);
        formData.append('price', this.newMeal.controls['price'].value);
        formData.append('quantity', this.newMeal.controls['quantity'].value);
        formData.append('image', this.image);
    }
}
