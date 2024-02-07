import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AdminService} from "../../admin/dashbord/admin.service";
import {HttpStatusCode} from "@angular/common/http";
import {SharedService} from "../../sharedmodule/shared.service";

@Component({
    selector: 'app-confirmation-token',
    templateUrl: './confirmation-token.component.html',
    styles: [],
    providers: [AdminService]
})
export class ConfirmationTokenComponent implements OnInit {


    serverResponse: string = "";
    activated: boolean = false;

    token: string = '';
    isAdministrator: boolean = false;

    constructor(private route: ActivatedRoute, private sharedService: SharedService, private router: Router) {
    }

    ngOnInit(): void {
        this.route.queryParams.subscribe(params => {
            this.token = params['token'];

            if (!this.token) {  /*TODO redirect to ERROR page */
                this.router.navigate(['cantine/signIn']).then(r => console.log("redirected to login page"));
            } else {
                this.checkUserTokenValidity(this.token);
            }
        });

    }


    checkUserTokenValidity(token: string) {
        this.sharedService.checkUserTokenValidity(token).subscribe({
            next: (response) => {
                if (response.message == "ADMIN_TOKEN_CHECKED_SUCCESSFULLY") {
                    this.isAdministrator = true;
                }
                this.serverResponse = "Votre compte a été activé avec succès";
                this.activated = true;

            },
            error: (error) => {
                if (error.status === HttpStatusCode.Unauthorized) {
                    this.serverResponse = "Votre  Token est Expiré ";
                } else if ((error.status === HttpStatusCode.NotFound) || (error.status === HttpStatusCode.BadRequest)) {
                    this.serverResponse = "Votre  Token est Invalide ";
                } else if (error.status === HttpStatusCode.Conflict) {
                    this.serverResponse = "Votre compte a été déjà activé";
                } else {
                    this.serverResponse = "Une erreur s'est produite pendant la vérification du token";
                }

            }

        })


    }
}




