import {Component, OnInit} from '@angular/core';
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";
import {Router} from "@angular/router";
import {debounceTime, distinctUntilChanged, Observable, of, Subject, switchMap} from "rxjs";
import {CoreCantineService} from "../../core-cantine.service";

@Component({
  selector: 'app-menu-search',
  templateUrl: './menu-search.component.html',
  styles: [
  ]
})
export class MenuSearchComponent implements OnInit{

   searchTerms =  new  Subject<string>();
   menusLabelSearched$: Observable<string[]> = of([]);

  constructor(private router : Router,private coreCantineService : CoreCantineService) { }
    ngOnInit(): void {
      this.menusLabelSearched$ = this.searchTerms.pipe(
        debounceTime(300),
          distinctUntilChanged(),
          switchMap((term: string) => this.coreCantineService.searchMenusByTerm(term))
      );
  }
  searchMenusByTerm(term: string) {
   this.searchTerms.next(term);
  }

  goToDetailsMenu(mealLabel: string) {
    this.router.navigate([IConstantsURL.MENU_DETAILS_URL] ,{ queryParams: { mealLabel: mealLabel} }).then(window.location.reload);
  }

}
