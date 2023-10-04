import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {StudentsManagementService} from "../students-management.service";
import {User} from "../../../sharedmodule/models/user";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../sharedmodule/functions/validation";

@Component({
  selector: 'app-manage-student-wallet',
  templateUrl: './manage-student-wallet.component.html',
  styles: [],
  providers: [StudentsManagementService]
})
export class ManageStudentWalletComponent implements OnInit{
  constructor(private route: ActivatedRoute ,   private studentsManagementService :  StudentsManagementService){}


  user :  User = new User();
  student: FormGroup = new FormGroup({
    firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
    email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
    birthDate: new FormControl('', [Validators.required]),
    phoneNumber: new FormControl('', [Validators.pattern(Validation.FRENCH_PHONE_REGEX)]),
    town: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(3)]),
    studentClass: new FormControl('', [Validators.required]),
  });


  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const id = params['id'];
      if (!isNaN(id) && Number.isInteger(Number(id))) {
          this.studentsManagementService.getStudentById(id).subscribe(data => {
          this.user = data;
          this.matchFormsValue();
        });
      } else {
        /*TODO:*/
        // Le paramètre 'id' n'est pas un nombre entier, signalez une erreur
        console.error('Le paramètre "id" n\'est pas un nombre entier valide.');
        // Vous pouvez également rediriger l'utilisateur vers une page d'erreur ici
      }
    });

  }

  matchFormsValue() {
    this.student.patchValue({
      firstName: this.user.firstname,
      lastName: this.user.lastname,
      email: this.user.email,
      birthDate: this.user.birthdate,
      phoneNumber: this.user.phone,
      town: this.user.town,
      studentClass: this.user.studentClass,
    });

  }
}
