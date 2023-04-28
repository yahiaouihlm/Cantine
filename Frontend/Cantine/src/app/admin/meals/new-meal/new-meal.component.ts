import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, PatternValidator, Validators} from "@angular/forms";

@Component({
    selector: 'app-new-meal',
    templateUrl: './new-meal.component.html',
    styleUrls: ['../../../../assets/styles/new-meal.component.scss']
})
export class NewMealComponent {

    submitted = false;
    newMeal: FormGroup = new FormGroup({
        label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(400), Validators.minLength(5)]),
        category: new FormControl('', [Validators.required, Validators.maxLength(44), Validators.minLength(3)]),
        price: new FormControl('', [Validators.required]),
        quantity: new FormControl('', [Validators.required,  Validators.pattern("^[0-9]+$")  ]),
        image: new FormControl('', [Validators.required]),
        status: new FormControl('', [Validators.required])
    });

    onSubmit() :  void  {
        this.submitted = true;
        if (this.newMeal.invalid){
            return;
        }

        console.log(this.newMeal.value)
    }
    get f(): { [key: string]: AbstractControl } {
        return this.newMeal.controls;
    }


    goto(): void {
        console.log('goto');
    }
}
