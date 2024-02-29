import {Component, OnInit} from '@angular/core';
import {CoreCantineService} from "../../../core-cantine/core-cantine.service";
import {Observable, of} from "rxjs";
import {Menu} from "../../../sharedmodule/models/menu";
import {Router} from "@angular/router";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";
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

    ngOnInit(): void {
        if (Malfunctions.checkAdminConnectivity(this.router)) {
            this.menus$ = this.menusService.getAllMenus();
        }
    }


    updateMenu(id: string) {
        console.log(id)
        this.router.navigate(['/admin/menus/update', id]);
    }


    addMenu(): void {
        this.router.navigate([IConstantsURL.ADMIN_NEW_MENU_URL]).then(r => window.location.reload());
    }

    menuAvailableToString(menuAvailable: number): string {
        return menuAvailable === 1 ? 'Available' : 'Unavailable';
    }

}
