import { Component, OnInit } from '@angular/core';
import {CoreCantineService} from "../core-cantine.service";
import { register } from 'swiper/element/bundle';
register();

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
  providers: [CoreCantineService]
})
export class HomeComponent  implements OnInit {

  constructor(private   coreCantineService : CoreCantineService) { }

  categories =['ENTREE', 'PLAT', 'DESSERT', 'BOISSON', 'ACCOMPAGNEMENT', 'AUTRE'];


  ngOnInit() {

  }


}
