import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../../../core-cantine/core-cantine.service";
import {Observable, of} from "rxjs";
import {Menu} from "../../../sharedmodule/models/menu";
import {Router} from "@angular/router";
import { IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";
import {MenusService} from "../menus.service";

@Component({
    selector: 'app-all-menus',
    templateUrl: './all-menus.component.html',
    styles: [],
    providers: [MenusService]
})
export class AllMenusComponent implements OnInit {

    constructor(private menusService: MenusService, private router: Router) {
    }

    menus$: Observable<Menu[]> = of([]);
    optionsOfMenus: string[] = ['Tous  les Menus ', 'Les  Menus Disponible', 'Les Menus Indisponible' ,'Les Menus  en  cours  de suppression'];
    selectedOption: string = 'Tous  les Menus '; // Pour stocker l'option sélectionnée
    ngOnInit(): void {
        if (Malfunctions.checkAdminConnectivityAndMakeRedirection(this.router)) {
            this.menus$ = this.menusService.getAllMenus();
        }
    }


    updateMenu(id: string) {
        this.router.navigate([IConstantsURL.ADMIN_UPDATE_MENU_URL, id]).then(r => window.location.reload());
    }


    addMenu(): void {
        this.router.navigate([IConstantsURL.ADMIN_NEW_MENU_URL]).then(r => window.location.reload());
    }

    menuAvailableToString(menuAvailable: number): string {
        return menuAvailable === 1 ? 'Available' : 'Unavailable';
    }
    validateSearch() {
        if  (this.selectedOption === this.optionsOfMenus[0]) {
            this.menus$ =this.menusService.getAllMenus();
        }
        else if  (this.selectedOption === this.optionsOfMenus[1]) {
            this.menus$ =this.menusService.getOnlyAvailableMenus();
        }
        else if (this.selectedOption === this.optionsOfMenus[2]) {
            this.menus$ =this.menusService.getOnlyUnavailableMenus();
        }
        else if (this.selectedOption === this.optionsOfMenus[3]) {
            this.menus$ =this.menusService.getOnlyMenusInDeletionProcess();
        }
        else {
            return; //  nothing  to  do
        }

    }
}
