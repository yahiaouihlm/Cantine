import {Component, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {MenusService} from "../menus.service";
import {Menu} from "../../../sharedmodule/models/menu";
import {ValidatorDialogComponent} from "../../../sharedmodule/dialogs/validator-dialog/validator-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {SuccessfulDialogComponent} from "../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {Meal} from "../../../sharedmodule/models/meal";
import {ListMealsComponent} from "../list-meals/list-meals.component";

@Component({
    selector: 'app-update-menu',
    templateUrl: './update-menu.component.html',
    styleUrls: ['../../../../assets/styles/new-meal.component.scss'],
    providers: [MenusService]
})
export class UpdateMenuComponent implements OnInit {

    private WOULD_YOU_LIKE_TO_UPDATE_THIS_MENU = "Voulez-vous  enregistrer  ce  menu ?"
    private MEAL_UPDATED_SUCCESSFULLY = "Le plat a été modifié avec succès !";
    private ERROR_OCCURRED_WHILE_UPDATING_MEAL = "Une erreur est survenue lors de la modification du plat !";
    private MENU_ADDED_SUCCESSFULLY = "Le Menu a été ajouté avec succès !"
    private ATTENTION_MENU_PRICE = "Attention, Vous  avez  Saisie un  prix  supérieur à 80€  pour ce  menu  !"
    private ERROR_OCCURRED_WHILE_UPDATING_MENU = "Voulez-vous  enregistrer  ce  menu ?"
    submitted = false;
    image!: File;
    menu: Menu = new Menu()
    mealsContainMenu: Meal[] = []
    closedDialog = false;
    updatedMenu: FormGroup = new FormGroup({
        label: new FormControl('', [Validators.required, Validators.maxLength(60), Validators.minLength(3)]),
        description: new FormControl('', [Validators.required, Validators.maxLength(1700), Validators.minLength(5)]),
        price: new FormControl('', [Validators.required, Validators.min(0.01), Validators.max(999.99)]),
        quantity: new FormControl('', [Validators.required, Validators.min(0), Validators.max(2147483501), Validators.pattern("^[0-9]+$")]),
        image: new FormControl('',),
        status: new FormControl('', [Validators.required]),
    });

    constructor(private route: ActivatedRoute, private menuService: MenusService, private matDialog: MatDialog, private router: Router) {
    }

    ngOnInit(): void {

        const param = this.route.snapshot.paramMap.get('id');
        if (param) {
            const id = +param;
            this.menuService.getMenuById(id).subscribe(data => {
                this.menu = data;
                this.matchFormsValue();
                this.mealsContainMenu = this.menu.meals;
            });

        }


    }

    onSubmit() {
        this.submitted = true
        if (this.updatedMenu.invalid ||  this.mealsContainMenu.length < 2) {
            return;
        }
        if (this.updatedMenu.controls["price"].value > 50) {
            alert(this.ATTENTION_MENU_PRICE)
        }

        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_UPDATE_THIS_MENU},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.editMenu();
            } else {
                return;
            }
        });


    }


    editMenu() {
        let mealsIds: string[] = []
        this.mealsContainMenu.forEach((meal) => {
            mealsIds.push(meal.uuid);
        });
        const menu = new FormData();
        menu.append("menuUuid", this.menu.uuid);
        menu.append('label', this.updatedMenu.controls["label"].value);
        menu.append('description', this.updatedMenu.controls["description"].value);
        menu.append('price', this.updatedMenu.controls["price"].value);
        menu.append('quantity', this.updatedMenu.controls["quantity"].value);
        menu.append("mealIDs", JSON.stringify(mealsIds));
        if (this.image != null || this.image != undefined) // envoyer  une image  uniquement si  y'a eu  une image  !
            menu.append('image', this.image);

        if (this.updatedMenu.controls['status'].value == "available") {
            menu.append('status', "1");
        } else {
            menu.append('status', "0");
        }

        this.menuService.updateMenu(menu).subscribe((data) => {
            if (data.message == "MENU UPDATED SUCCESSFULLY") {

                const result = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: this.MENU_ADDED_SUCCESSFULLY},
                    width: '40%',
                });
                result.afterClosed().subscribe((result) => {
                    this.router.navigate(['/admin/menus'], {queryParams: {reload: 'true'}})
                });

            } else {
                alert(this.ERROR_OCCURRED_WHILE_UPDATING_MENU);
                /*TODO    Remove  Token   */
            }

        });


    }


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
