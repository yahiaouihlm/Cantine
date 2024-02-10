import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {MealsService} from "../meals.service";
import {Router} from "@angular/router";
import {SuccessfulDialogComponent} from "../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-new-meal',
    templateUrl: './new-meal.component.html',
    styleUrls: ['../../../../assets/styles/new-meal.component.scss'],
    providers: [MealsService]
})
export class NewMealComponent implements OnInit {
    private MEAL_ADDED_SUCCESSFULLY = "Le plat a été ajouté avec succès !"
    private WOULD_YOU_LIKE_TO_SAVE_THIS_MEAL = "Voulez-vous vraiment sauvegarder ce plat ?"
    private ATTENTION_MEAL_PRICE = "Attention  vous  avez  saisi  un  prix  supérieur  à  50€  pour un  plat  !"

    submitted = false;
    image!: File
    isLoading = false;
    newMeal: FormGroup = new FormGroup({
        label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(1600), Validators.minLength(5)]),
        category: new FormControl('', [Validators.required, Validators.maxLength(44), Validators.minLength(3)]),
        price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
        quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),
        mealType: new FormControl('', [Validators.required]),
        image: new FormControl('', [Validators.required]),
        status: new FormControl('', [Validators.required])
    });

    constructor(private mealServiceService: MealsService, private matDialog: MatDialog, private router: Router) {
    }

    /** TODO : make  loadingTemplate in  the   center of  the  page   in  html   in reponsive  mode  */

    ngOnInit(): void {
        if (!Malfunctions.checkAdminConnectivity(this.router)) {
            return;
        }
    }

    onSubmit() {


        this.submitted = true;
        if (this.newMeal.invalid) {
            return;
        }
        this.isLoading = true;
        if (this.newMeal.controls["price"].value > 50) {
            alert(this.ATTENTION_MEAL_PRICE)
        }

        this.confirmAndSendNewMeal();

    }


    goBack(): void {
        this.router.navigate([IConstantsURL.ADMIN_MEALS_URL]).then(r => window.location.reload());
    }

    confirmAndSendNewMeal(): void {
        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_SAVE_THIS_MEAL},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.sendNewMeal();
            } else {
                this.isLoading = false;
                return;
            }
        });


    }

    sendNewMeal(): void {
        const formData = new FormData();
        formData.append('label', this.newMeal.controls['label'].value);
        formData.append('description', this.newMeal.controls['description'].value);
        formData.append('category', this.newMeal.controls['category'].value);
        formData.append('mealType', this.newMeal.controls['mealType'].value);
        formData.append('price', this.newMeal.controls['price'].value);
        formData.append('quantity', this.newMeal.controls['quantity'].value);
        formData.append('image', this.image);
        formData.append('status', this.newMeal.controls['status'].value === "available" ? "1" : "0");

        this.mealServiceService.addMeal(formData).subscribe({
            next: response => {
                const result = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: this.MEAL_ADDED_SUCCESSFULLY},
                    width: '40%',
                });
                result.afterClosed().subscribe((result) => {
                    this.router.navigate([IConstantsURL.ADMIN_MEALS_URL]).then(r => window.location.reload);
                });

            },
            error: error => {
                this.isLoading = false;
            }

        });
    }

    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        this.image = (target.files as FileList)[0];
    }

    get f(): { [key: string]: AbstractControl } {
        return this.newMeal.controls;
    }


}
