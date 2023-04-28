import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, PatternValidator, Validators} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {ValidatorDialogComponent} from "../validator-dialog/validator-dialog.component";

@Component({
    selector: 'app-new-meal',
    templateUrl: './new-meal.component.html',
    styleUrls: ['../../../../assets/styles/new-meal.component.scss']
})
export class NewMealComponent {

    submitted = false;
    newMeal: FormGroup = new FormGroup({
        label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(1600), Validators.minLength(5)]),
        category: new FormControl('', [Validators.required, Validators.maxLength(44), Validators.minLength(3)]),
        price: new FormControl('', [Validators.required , Validators.min(0.01), Validators.max(999.99) ]),
        quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501) , Validators.pattern("^[0-9]+$")  ]),
        image: new FormControl('', [Validators.required]),
        status: new FormControl('', [Validators.required])
    });

    constructor(private matDialog: MatDialog) {}
    onSubmit() :  void  {

        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: { message: " Voulez-vous vraiment sauvegarder ce plat ? " },
            width: '40%',
        });


        this.submitted = true;
        if (this.newMeal.invalid){
            return;
        }
       if  (this.newMeal.controls["price"].value > 50 ){
           alert("Attention  vous  avez  saisi  un  prix  supérieur  à  50€  pour un  plat  !")
       }


    }
    get f(): { [key: string]: AbstractControl } {
        return this.newMeal.controls;
    }


    goto(): void {
        console.log('goto');
    }
}
