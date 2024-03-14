import {Component, OnInit} from '@angular/core';
import {Meal} from "../../../sharedmodule/models/meal";
import {MealsService} from "../meals.service";
import {ActivatedRoute, Router} from "@angular/router";

import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {SuccessfulDialogComponent} from "../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";


@Component({
    selector: 'app-update-meal',
    templateUrl: './update-meal.component.html',
    styleUrls: ["../../../../assets/styles/new-meal.component.scss"],
    providers: [MealsService]
})
export class UpdateMealComponent implements OnInit {

    private MEAL_UPDATED_SUCCESSFULLY = "Le plat a été modifié avec succès !";
    private MEAL_DELETED_SUCCESSFULLY = "Le plat a été supprimé avec succès !";
    private WOULD_YOU_LIKE_TO_UPDATE_THIS_MEAL = "Voulez-vous vraiment Modifier  ce plat ?";

    private WOULD_YOU_LIKE_TO_DELETE_THIS_MEAL = "Voulez-vous vraiment supprimer ce plat ?";
    private ATTENTION_MEAL_PRICE = "Attention  vous  avez  saisi  un  prix  supérieur  à  50€  pour un  plat  !";


    meal: Meal = new Meal();
    submitted!: boolean;
    image!: File
    isLoading: boolean = false;
    updatedMeal: FormGroup = new FormGroup({
        label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(1600), Validators.minLength(5)]),
        category: new FormControl('', [Validators.required, Validators.maxLength(44), Validators.minLength(3)]),
        price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
        quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),
        mealType: new FormControl('', [Validators.required]),
        image: new FormControl(''),
        status: new FormControl('', [Validators.required])
    });

    constructor(private mealServiceService: MealsService, private router: Router, private route: ActivatedRoute, private matDialog: MatDialog) {
    }

    ngOnInit(): void {

        if (!Malfunctions.checkAdminConnectivityAndMakeRedirection(this.router)) {
            return;
        }
        const param = this.route.snapshot.paramMap.get('id');
        console.log(param)
        if (param) {
            this.mealServiceService.getMealByUuId(param).subscribe(data => {
                this.meal = data;
                this.matchFormsValue()
            });
        }
    }


    onSubmit() {
        this.submitted = true
        if (this.updatedMeal.invalid) {
            return;
        }
        if (this.meal.status == 2 && this.updatedMeal.controls["status"].value == "toRemove") {
            alert("Vous ne pouvez pas modifier un plat supprimé !");
            return;
        }

        this.isLoading = true;
        if (this.updatedMeal.controls["price"].value > 50) {
            alert(this.ATTENTION_MEAL_PRICE)
        }
        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_UPDATE_THIS_MEAL},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.editMeal();
            } else {
                this.isLoading = false;
                return;
            }
        });


    }


    delete(): void {
        this.isLoading = true;
        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_DELETE_THIS_MEAL},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.removeMealSendReq();
            } else {
                this.isLoading = false;
                return;
            }
        });


    }


    removeMealSendReq(): void {
        this.mealServiceService.deleteMeal(this.meal.uuid).subscribe({
                next: (data) => {
                    this.isLoading = false;
                    const result = this.matDialog.open(SuccessfulDialogComponent, {
                        data: {message: this.MEAL_DELETED_SUCCESSFULLY},
                        width: '40%',
                    });
                    result.afterClosed().subscribe((result) => {
                        this.router.navigate([IConstantsURL.ADMIN_MEALS_URL],).then(r => window.location.reload());
                    });
                },
                error: (error) => {
                    this.isLoading = false;
                    return;
                }


            }
        )

    }

    matchFormsValue() {
        function getStatusFromNumber(status: number): string {
            if (status == 1) {
                return "available"
            } else if (status == 0) {
                return "unavailable"
            } else {
                return "toRemove"
            }
        }

        this.updatedMeal.patchValue({
            label: this.meal.label,
            description: this.meal.description,
            price: this.meal.price,
            quantity: this.meal.quantity,
            category: this.meal.category,
            mealType: this.meal.mealType,
            status: getStatusFromNumber(this.meal.status)
        });
    }


    editMeal(): void {
        const formData = new FormData();
        formData.append('uuid', this.meal.uuid.toString());
        formData.append('label', this.updatedMeal.controls['label'].value);
        formData.append('description', this.updatedMeal.controls['description'].value);
        formData.append('category', this.updatedMeal.controls['category'].value);
        formData.append('price', this.updatedMeal.controls['price'].value);
        formData.append('quantity', this.updatedMeal.controls['quantity'].value);
        formData.append('mealType', this.updatedMeal.controls['mealType'].value);

        if (this.image != null || this.image != undefined) // envoyer  une image  uniquement si  y'a eu  une image  !
            formData.append('image', this.image);

        if (this.updatedMeal.controls['status'].value == "available") {
            formData.append('status', "1");
        } else {
            formData.append('status', "0");
        }

        this.mealServiceService.editMeal(formData).subscribe({

            next: (data) => {
                const result = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: this.MEAL_UPDATED_SUCCESSFULLY},
                    width: '40%',
                });
                result.afterClosed().subscribe((result) => {
                      this.goBack();
                });
            },
            error: (error) => {
                this.isLoading = false;
            }

        });

    }

    get f(): { [key: string]: AbstractControl } {
        return this.updatedMeal.controls;
    }

    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        this.image = (target.files as FileList)[0];
    }


    goBack(): void {
        this.router.navigate([IConstantsURL.ADMIN_MEALS_URL]).then(window.location.reload);
    }


}
