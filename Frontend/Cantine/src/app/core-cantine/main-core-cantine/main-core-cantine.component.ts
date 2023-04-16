import {ChangeDetectorRef, Component, ViewChild} from '@angular/core';
import {MatSidenav} from "@angular/material/sidenav";
import {BreakpointObserver} from "@angular/cdk/layout";

@Component({
  selector: 'app-main-core-cantine',
  templateUrl: "./main-core-cantine.component.html",
  styleUrls:['../../../assets/styles/main.component.scss']
})
export class MainCoreCantineComponent {
    isconnected = true;
  constructor () {}
}
