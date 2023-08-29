import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-confirmation-token',
  templateUrl: './confirmation-token.component.html',
  styles: [
  ]
})
export class ConfirmationTokenComponent  implements  OnInit{



  constructor(private   route :  ActivatedRoute) {}
  ngOnInit(): void {
    const token = this.route.snapshot.paramMap.get('token');
    if (token) {
     /* const token = +token;*/
        console.log(token)
    }

  }

}
