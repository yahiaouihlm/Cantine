import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {StudentsManagementService} from "../students-management.service";
import {User} from "../../../sharedmodule/models/user";
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../../sharedmodule/functions/validation";
import {Observable, of} from "rxjs";
import {StudentClass} from "../../../sharedmodule/models/studentClass";
import {StudentDashboardService} from "../../../student/dashbord/student-dashboard.service";
import {MatDialog} from "@angular/material/dialog";
import {EditStudentWalletDialogComponent} from "../edit-student-wallet-dialog/edit-student-wallet-dialog.component";
import {
    NgOtpInputDialogComponent
} from "../../../sharedmodule/dialogs/ng-otp-input-dialog/ng-otp-input-dialog.component";
import {SuccessfulDialogComponent} from "../../../sharedmodule/dialogs/successful-dialog/successful-dialog.component";
import {IConstantsURL} from "../../../sharedmodule/constants/IConstantsURL";
import Malfunctions from "../../../sharedmodule/functions/malfunctions";

@Component({
    selector: 'app-manage-student-wallet',
    templateUrl: './manage-student.component.html',
    styleUrls: ['../../../../assets/styles/manage-student.component.scss'],
    providers: [StudentsManagementService, StudentDashboardService]
})
export class ManageStudentComponent implements OnInit {
    constructor(private route:  ActivatedRoute, private  router : Router , private studentsManagementService: StudentsManagementService, private studentService: StudentDashboardService, private matDialog: MatDialog) {
    }

    AMOUNT_ADDED_SUCCESSFULLY = "Le montant a été ajouté avec succès !"
    STUDENT_EMAIL_UPDATED_SUCCESSFULLY = "L'email  a été modifié avec succès ! un email de confirmation a été envoyé à la nouvelle adresse email";
    user: User = new User();
    submitted = false
    enableEmailInput =  false ;
    isLoadingPage: boolean = false;

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
        if (!Malfunctions.checkAdminConnectivityAndMakeRedirection(this.router)) {
            return;
        }
        this.student.disable();
        this.route.queryParams.subscribe(params => {
            const studentUuid = params['studentUuid'];
            if (studentUuid == undefined) {
                this.router.navigate([IConstantsURL.ADMIN_STUDENTS_URL]).then(r => window.location.reload());
            }
           this.studentsManagementService.getStudentByUuId(studentUuid).subscribe((student) => {
                this.user = student;
                this.matchFormsValue();
           });


        });

    }


    addAmount() {
        let amountToAdd = 0;
        let dialogRef = this.matDialog.open(EditStudentWalletDialogComponent, {
            data: {message: "Le Montant à Ajouter", userid: this.user.uuid},
            width: '47%',
            height: '30%'
        });
        let result = dialogRef.afterClosed().subscribe((result: number) => {
            if (result != undefined && result != 0) {
                this.isLoadingPage = true;
                amountToAdd = result;
                this.sendStudentAmount(amountToAdd);
            }
        });


    }

    sendStudentAmount(amountToAdd: number) {
       let  myAdminUuid = Malfunctions.getUserIdFromLocalStorage()
        this.studentsManagementService.attemptAddAmountToStudentAccount(myAdminUuid,  this.user.uuid, amountToAdd).subscribe({
            next: (response) => {
                this.isLoadingPage = false
                console.log(response);
                //  this.sendStudentConfirmationCode(amountToAdd);//  ouvrir le formulaire pour avoir le code  de confirmation
            },
            error: (error) => {
                this.isLoadingPage = false
            },
        });
    }


    /************ Le formulaire   du  saisie  du  code  ************/

    sendStudentConfirmationCode(amountToAdd: number) {
        let dialogRef = this.matDialog.open(NgOtpInputDialogComponent, {width: "50vw", height: "25vh"});
        let result = dialogRef.afterClosed().subscribe((result: string) => {
            if (result != undefined && result != "") {
                this.isLoadingPage = true;
                this.sendStudentConfirmationCodeReq(amountToAdd, Number(result)); //  envoyer le  code  de  confirmation
            }
        });
    }

    sendStudentConfirmationCodeReq(amountToAdd: number, validationCode: number) {
        this.studentsManagementService.sendStudentCode(this.user.uuid, amountToAdd, validationCode).subscribe({
            next: (response) => {
                this.showConfirmationDialog(this.AMOUNT_ADDED_SUCCESSFULLY);
                this.isLoadingPage = false //  si  le code éte correcte
            }
            , error: (error) => {
                this.isLoadingPage = false  //  si le code est  incorrecte
            }
        });
    }


    showConfirmationDialog(message:  string ): void {
        const result = this.matDialog.open(SuccessfulDialogComponent, {
            data: {message: message},
            width: '40%',
        });
        result.afterClosed().subscribe((result) => {
            window.location.reload();
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


    get f(): { [key: string]: AbstractControl } {
        return this.student.controls;
    }


    changeStudentEmail() {
        this.submitted = true;
        if (this.student.controls['email'].invalid || this.student.controls['email'].value == this.user.email  || !this.student.controls['email'].touched) {
            return;
        }
        let  confirmation = confirm("Voulez-vous vraiment modifier l'email de cet étudiant ?");
        if (!confirmation)
              return;

        this.isLoadingPage = true;
        this.studentsManagementService.updateStudentEmail(this.user.uuid, this.student.controls['email'].value).subscribe({
            next: (response) => {
                this.showConfirmationDialog(this.STUDENT_EMAIL_UPDATED_SUCCESSFULLY);
                this.isLoadingPage = false;
                this.enableEmailInput = false;
            },
            error: (error) => {
                this.isLoadingPage = false;
                this.enableEmailInput = false;
            }

        });
    }

    enableEmailFormInput(){
        this.enableEmailInput = true;
        this.student.controls['email'].enable();
    }
}
