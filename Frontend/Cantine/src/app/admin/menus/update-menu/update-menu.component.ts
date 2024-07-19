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
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";

@Component({
    selector: 'app-update-menu',
    templateUrl: './update-menu.component.html',
    styleUrls: ['../../../../assets/styles/new-meal.component.scss'],
    providers: [MenusService]
})
export class UpdateMenuComponent implements OnInit {

    private WOULD_YOU_LIKE_TO_UPDATE_THIS_MENU = "Voulez-vous  enregistrer  ce  menu ?"

    private MENU_ADDED_SUCCESSFULLY = "Le Menu a été ajouté avec succès !"
    private ATTENTION_MENU_PRICE = "Attention, Vous  avez  Saisie un  prix  supérieur à 80€  pour ce  menu  !"
    private  MENU_REMOVED_SUCCESSFULLY = "Le menu a été supprimé avec succès !"
    private  WOULD_YOU_LIKE_TO_DELETE_THIS_MENU = "Voulez-vous vraiment supprimer ce menu ?"
    submitted = false;
    image!: File;
    menu: Menu = new Menu()
    mealsContainMenu: Meal[] = []
    closedDialog = false;
    isLoading: boolean = false;
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

        if (!Malfunctions.checkAdminConnectivityAndMakeRedirection(this.router)) {
            return;
        }
        const param = this.route.snapshot.paramMap.get('id');
        if (param) {
            this.menuService.getMenuById(param).subscribe(data => {
                this.menu = data;
                this.matchFormsValue();
                this.mealsContainMenu = this.menu.meals;
            });

        }


    }

    onSubmit() {
        this.submitted = true
        if (this.updatedMenu.invalid || this.mealsContainMenu.length < 2) {
            return;
        }
        if (this.updatedMenu.controls["price"].value > 50) {
            alert(this.ATTENTION_MENU_PRICE)
        }

        if (this.menu.status == 2 && this.updatedMenu.controls["status"].value == "toRemove") {
            alert("Vous ne pouvez pas modifier un plat supprimé !");
            return;
        }
        this.isLoading = true;
        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_UPDATE_THIS_MENU},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.editMenu();
            } else {
                this.isLoading = false;
                return;
            }
        });


    }


    editMenu() {
        let mealsIds: string[] = []
        this.mealsContainMenu.forEach((meal) => {
            mealsIds.push(meal.id);
        });
        const menu = new FormData();
        menu.append("id", this.menu.id);
        menu.append('label', this.updatedMenu.controls["label"].value);
        menu.append('description', this.updatedMenu.controls["description"].value);
        menu.append('price', this.updatedMenu.controls["price"].value);
        menu.append('quantity', this.updatedMenu.controls["quantity"].value);
        menu.append("listOfMealsAsString", JSON.stringify(mealsIds));
        if (this.image != null || this.image != undefined) // envoyer  une image  uniquement si  y'a eu  une image  !
            menu.append('image', this.image);

        if (this.updatedMenu.controls['status'].value == "available") {
            menu.append('status', "1");
        } else {
            menu.append('status', "0");
        }
        console.log(menu.get("listOfMealsAsString"))

        this.menuService.updateMenu(menu).subscribe({
            next: (data) => {
                const result = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: this.MENU_ADDED_SUCCESSFULLY},
                    width: '40%',
                });
                result.afterClosed().subscribe((result) => {
                    this.goBack();
                });
                this.isLoading = false;
            },
            error: (error) => {
                this.isLoading = false;
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
            } else if (pstatus == 0) {
                return "unavailable"
            } else {
                return "toRemove"
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
        this.image = (target.files as FileList)[0];
    }


    goBack() {
        this.router.navigate([IConstantsURL.ADMIN_MENUS_URL]).then(r => window.location.reload());
    }

    deleteMenu () {
        this.menuService.removeMenu(this.menu.id).subscribe({
            next: (data) => {
                const result = this.matDialog.open(SuccessfulDialogComponent, {
                    data: {message: this.MENU_REMOVED_SUCCESSFULLY},
                    width: '40%',
                });
                result.afterClosed().subscribe((result) => {
                    this.goBack();
                });
                this.isLoading = false;
            },
            error: (error) => {
                this.isLoading = false;
            }
        });
    }

    removeMenu() {
        this.isLoading = true;
        const result = this.matDialog.open(ValidatorDialogComponent, {
            data: {message: this.WOULD_YOU_LIKE_TO_DELETE_THIS_MENU},
            width: '40%',
        });

        result.afterClosed().subscribe((result) => {
            if (result != undefined && result == true) {
                this.deleteMenu();
            } else {
                this.isLoading = false;
                return;
            }
        });

    }
}
