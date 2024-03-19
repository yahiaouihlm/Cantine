import { Component } from '@angular/core';
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";
import {Router} from "@angular/router";
import {Observable, Subject} from "rxjs";

@Component({
  selector: 'app-menu-seach',
  templateUrl: './menu-search.component.html',
  styles: [
  ]
})
export class MenuSearchComponent {

   searchTerms =  new  Subject<string>();
   /*menusLabelSearched$: Observable<String []>;*/
  mealLabel: string = "";

  constructor(private router : Router) { }

  searchMenusByTerm(term: string) {
   this.searchTerms.next(term);
  }

  goToDetailsMenu() {
    this.router.navigate([IConstantsURL.MENU_DETAILS_URL] ,{ queryParams: { mealLabel: this.mealLabel} }).then(window.location.reload);
  }
}
