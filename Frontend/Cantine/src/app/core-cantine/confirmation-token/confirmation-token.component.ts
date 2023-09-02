import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AdminService} from "../../admin/dashbord/admin.service";
import {normalizeExtraEntryPoints} from "@angular-devkit/build-angular/src/webpack/utils/helpers";
import {HttpStatusCode} from "@angular/common/http";
import {SharedService} from "../../sharedmodule/shared.service";

@Component({
  selector: 'app-confirmation-token',
  templateUrl: './confirmation-token.component.html',
  styles: [] ,
  providers : [AdminService ]
})
export class ConfirmationTokenComponent  implements  OnInit {


  serverResponse: string = "";
  activated: boolean = false;

  constructor(private route: ActivatedRoute, private sharedService: SharedService) {
  }

  ngOnInit(): void {
    const token = this.route.snapshot.paramMap.get('token');
    if (token) {
      this.checkTokenValidityAdmin(token);
    }

  }


  checkTokenValidityAdmin(token: string) {
    this.sharedService.checkTokenValidityAdmin(token).subscribe({
      next: (response) => {
        if (response.message == "TOKEN VALID") {
          this.serverResponse = "Votre compte a été activé avec succès";
          this.activated = true;
        }

      },
      error: (error) => {
        if (error.status === HttpStatusCode.Unauthorized) {
          this.serverResponse = "Votre  Token est Expiré ";
        } else if ((error.status === HttpStatusCode.NotFound) || (error.status === HttpStatusCode.BadRequest)) {
          this.serverResponse = "Votre  Token est Invalide ";
        } else {
          console.log(error.status)
          this.serverResponse = "Une erreur s'est produite pendant la vérification du token";
        }

      }

    })


  }
}




