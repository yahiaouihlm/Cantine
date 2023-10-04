import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-manage-student-wallet',
  templateUrl: './manage-student-wallet.component.html',
  styles: [
  ]
})
export class ManageStudentWalletComponent implements OnInit{
  constructor(private route: ActivatedRoute){}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const id = params['id'];
      if (!isNaN(id) && Number.isInteger(Number(id))) {

      } else {
        /*TODO:*/
        // Le paramètre 'id' n'est pas un nombre entier, signalez une erreur
        console.error('Le paramètre "id" n\'est pas un nombre entier valide.');
        // Vous pouvez également rediriger l'utilisateur vers une page d'erreur ici
      }
    });

  }

}
