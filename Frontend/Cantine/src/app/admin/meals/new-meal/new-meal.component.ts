import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, PatternValidator, Validators} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {ValidatorDialogComponent} from "../dialogs/validator-dialog/validator-dialog.component";
import {MealServiceService} from "../meal-service.service";
import {Router} from "@angular/router";

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

    constructor(private mealServiceService: MealServiceService, private matDialog: MatDialog , private  router : Router) {
    }

     onSubmit() {

         this.sendTest();

        /*this.submitted = true;
        if (this.newMeal.invalid) {
            return;
        }
        if (this.newMeal.controls["price"].value > 50) {
            alert("Attention  vous  avez  saisi  un  prix  supérieur  à  50€  pour un  plat  !")
        }

        this.confirmAndSendNewMeal();
*/
    }

    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        const file: File = (target.files as FileList)[0]
        this.image = file;
    }

    get f(): { [key: string]: AbstractControl } {
        return this.newMeal.controls;
    }


    goback(): void {
        this.router.navigate(['/admin/meals']);
    }

    confirmAndSendNewMeal(): void {
        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: " Voulez-vous vraiment sauvegarder ce plat ? "},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.sendNewMeal();
                console.log("ok")
            } else {
                return;
            }
        });


    }

    sendNewMeal(): void {
        const formData = new FormData();
        formData.append('label', this.newMeal.controls['label'].value);
        formData.append('description', this.newMeal.controls['description'].value);
        formData.append('category', this.newMeal.controls['category'].value);
        formData.append('price', this.newMeal.controls['price'].value);
        formData.append('quantity', this.newMeal.controls['quantity'].value);
        formData.append('image', this.image);
        if  (this.newMeal.controls['status'].value == "available") {
            formData.append('status', "1");
        } else {
            formData.append('status', "0");
        }
        this.mealServiceService.addMeal(formData).subscribe((data) => {
             if  (data != undefined  && data.message !=undefined   && data.message == "MEAL ADDED SUCCESSFULLY") {

             }
             else  {
                 alert("Une erreur est survenue lors de l'ajout du plat Veuillez réessayer ultérieurement") ;
             }
        });
    }



   sendTest(): void {
        const formData = new FormData();
        formData.append('label', "fritest");
        formData.append('description', "des pOMMES  de terRe");
        formData.append('category', " foo De");
        formData.append('price', "1");
        formData.append('quantity', "1");
        formData.append('image', this.image);
        formData.append('status', "1");
        this.mealServiceService.addMeal(formData).subscribe((result) => {
            console.log(result);
        });
   }
}
