import {Component} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {ListMealsComponent} from "../list-meals/list-meals.component";
import {MatDialog} from "@angular/material/dialog";
import {Meal} from "../../../sharedmodule/models/meal";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {MenusService} from "../menus.service";
import {SuccessfulDialogComponent} from "../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {Router} from "@angular/router";

@Component({
    selector: 'app-new-menu',
    templateUrl: './new-menu.component.html',
    styleUrls: ['../../../../assets/styles/new-meal.component.scss'],
    providers: [MenusService]
})

export class NewMenuComponent {

    private MENU_ADDED_SUCCESSFULLY = "Le Menu a été ajouté avec succès !"
    private ATTENTION_MENU_PRICE = "Attention, Vous  avez  Saisie un  Priw de 80€  Pour un Menu  !"
    private WOULD_YOU_LIKE_TO_SAVE_THIS_MENU = "Voulez-vous  enregistrer  ce  menu ?"

    private   ERROR_OCCURRED_WHILE_UPDATING_MENU = "Une erreur s'est produite lors de la mise à jour du menu !"

    submitted = false;
    image!: File
    mealsContainMenu: Meal[] = []
    newMenu: FormGroup = new FormGroup({
        label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(1700), Validators.minLength(5)]),
        price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
        quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),
        image: new FormControl('', [Validators.required]),
        status: new FormControl('', [Validators.required]),
    });
     isLoaded = false;
    closedDialog = false;

    constructor(private matDialog: MatDialog, private menusService: MenusService, private router: Router) {
    }


    onSubmit() {
        this.submitted = true;
        this.isLoaded = true;
        if (this.newMenu.invalid || this.mealsContainMenu.length < 2) {
            this.isLoaded = false;
          return;
        }
        if (this.newMenu.controls["price"].value > 50) {
          alert(this.ATTENTION_MENU_PRICE)
        }
        this.confirmAndSendNewMeal();

    }


    confirmAndSendNewMeal(): void {
        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_SAVE_THIS_MENU},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.sendNewMenu();
            } else {
                return;
            }
        });


    }


    sendNewMenu(): void {
        let mealsIds: number[] = []

        this.mealsContainMenu.forEach((meal) => {
            mealsIds.push(meal.id);
        });

        const formData = new FormData();
        formData.append("label", this.newMenu.controls["label"].value);
        formData.append("description", this.newMenu.controls["description"].value);
        formData.append("price", this.newMenu.controls["price"].value);
        formData.append("quantity", this.newMenu.controls["quantity"].value);
        formData.append("image", this.image);
        formData.append('status', this.newMenu.controls['status'].value === "available" ? "1" : "0");
        formData.append("mealIDs", JSON.stringify(mealsIds));


        this.menusService.sendMenu(formData).subscribe((result) => {
            if (result != undefined && result.message === "MENU ADDED SUCCESSFULLY") {
                const result = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: this.MENU_ADDED_SUCCESSFULLY},
                    width: '40%',
                });
                result.afterClosed().subscribe((result) => {
                    this.router.navigate(['/admin/menus'], {queryParams: {reload: 'true'}})
                });

            }else{
                alert(this.ERROR_OCCURRED_WHILE_UPDATING_MENU);
                /*TODO    Remove  Token   */
            }
        })
    }


    // dialog to open  the  list  of  meals  and  select  the  meals  to  add  to  the  menu
    onOpenListMealDialog() {
        this.closedDialog = false;
        const result = this.matDialog.open(ListMealsComponent);
        result.afterClosed().subscribe((result) => {
            this.closedDialog = true;
            if (result === undefined)
                this.mealsContainMenu = []
            else {
                this.mealsContainMenu = result;
            }

            // this.clicked = true;
        })

    }


    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        const file: File = (target.files as FileList)[0]
        this.image = file;
    }


    get f(): { [key: string]: AbstractControl } {
        return this.newMenu.controls;
    }


}
