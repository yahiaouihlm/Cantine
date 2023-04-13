import { BreakpointObserver } from '@angular/cdk/layout';
import { ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./styles/app.component.scss']
})
export class AppComponent {
  isconnected = true;
  @ViewChild('sidenav') sidenav!: MatSidenav;
  constructor (private observer: BreakpointObserver, private cd : ChangeDetectorRef) {}
  title = 'Cantine';

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
