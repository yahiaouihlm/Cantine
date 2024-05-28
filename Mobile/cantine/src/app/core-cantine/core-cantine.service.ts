import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'

})
export class CoreCantineService {


  constructor(private httpClient: HttpClient) {
  }


  getCategories() {
    return this.httpClient.get('https://devdactic.fra1.digitaloceanspaces.com/foodui/home.json');
  }
}
