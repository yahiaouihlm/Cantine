import { Component } from '@angular/core';

@Component({
  selector: 'app-main-footer',
  templateUrl: './main-footer.component.html',
  styles: [
    `
       footer{
         font-size: 1rem;
         background-color: #7b7b78;
         i{
           margin-right: 8px;
         }
    }
    .footer-container{
      width: 100%;


    }
      
      `
  ]
})
export class MainFooterComponent {

}
