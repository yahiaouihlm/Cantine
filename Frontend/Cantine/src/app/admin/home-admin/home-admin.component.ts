import { Component } from '@angular/core';
import {Router} from "@angular/router";
@Component({
  selector: 'app-home-admin',
  templateUrl: './home-admin.component.html',
  styles: [  ]
})
export class HomeAdminComponent {

  constructor(private  router: Router ) { }

  goto(): void {
     this.router.navigate(['/admin']);
  }
}
