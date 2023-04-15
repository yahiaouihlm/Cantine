import {ChangeDetectorRef, Component, ViewChild} from '@angular/core';
import {MatSidenav} from "@angular/material/sidenav";
import {BreakpointObserver} from "@angular/cdk/layout";

@Component({
  selector: 'app-main-core-cantine',
  templateUrl: "./main-core-cantine.component.html",
  styleUrls
})
export class MainCoreCantineComponent {
    isconnected = true;
  @ViewChild('sidenav') sidenav!: MatSidenav;
  constructor (private observer: BreakpointObserver, private cd : ChangeDetectorRef) {}
  ngAfterViewInit() {
    if (this.isconnected) {

      this.observer.observe(['(max-width: 900px)']).subscribe((res) => {
        if (res.matches) {
          this.sidenav.mode = 'over';
          this.sidenav.close();
        } else {
          this.sidenav.mode = 'side';
          this.sidenav.open();
        }
      });

      this.cd.detectChanges();

    }
  }
}
