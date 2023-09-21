import {Component, OnInit} from '@angular/core';
import Malfunctions from "../../sharedmodule/functions/malfunctions";

@Component({
  selector: 'app-main-admin',
  templateUrl: './main-admin.component.html',
  styleUrls: ["../../../assets/styles/main.component.scss"]
})
export class MainAdminComponent   implements  OnInit{
  isconnected = false;

    constructor() {}
  ngOnInit(): void {
    let  userid = Malfunctions.getUserIdFromLocalStorage();
    this.isconnected = userid !== '';
  }


  getAdminById () {

  }
}
