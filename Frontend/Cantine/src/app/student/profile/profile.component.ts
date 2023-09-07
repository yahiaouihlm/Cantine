import {Component, OnDestroy, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";
import {ActivatedRoute, Router} from "@angular/router";
import {User} from "../../sharedmodule/models/user";
import {SharedService} from "../../sharedmodule/shared.service";
import {Observable, of, Subscription} from "rxjs";
import {StudentDashboardService} from "../dashbord/student-dashboard.service";
import {StudentClass} from "../../sharedmodule/models/studentClass";

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    styles: [],
    providers : [StudentDashboardService]
})
export class ProfileComponent implements OnInit, OnDestroy {
    constructor(private route: ActivatedRoute, private sharedService: SharedService , private  router :  Router , private  studentService : StudentDashboardService ) {
    }

    user: User = new User();
    submitted!: boolean;
    private queryParamsSubscription: Subscription | undefined;
    private getStudentByIdSubscription: Subscription | undefined;

    studentClass$: Observable<StudentClass[]> = of([]);

    touched :  boolean  =  false ;

    image! :  File
    studentUpdated: FormGroup = new FormGroup({
        firstName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
        lastName: new FormControl('', [Validators.required, Validators.maxLength(90), Validators.minLength(3)]),
        email: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.pattern(Validation.EMAIL_REGEX)]),
        password: new FormControl('', [Validators.required, Validators.maxLength(20), Validators.minLength(6)]),
        birthDate: new FormControl('', [Validators.required]),
        phoneNumber: new FormControl('', [Validators.pattern(Validation.FRENCH_PHONE_REGEX)]),
        town: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(3)]),
        studentClass: new FormControl('', [Validators.required]),
        image: new FormControl(''),
    });

    ngOnInit(): void {
        this.studentUpdated.disable();
        this.studentClass$ =  this.studentService.getAllStudentClass();
        this.queryParamsSubscription = this.route.queryParams.subscribe(params => {
            const id = params['id'];
            if (id) {
                this.getStudentByIdSubscription =this.sharedService.getStudentById(id).subscribe((response) => {
                    this.user = response;
                    console.log(this.user.wallet);
                    this.matchFormsValue();
                });
            }
            else {
                localStorage.clear();
                this.router.navigate(["cantine/home"]).then(r => console.log(r));
            }

        });

    }




    onSubmit() : void {
       this.studentUpdated.enable();
    }


    get f(): { [key: string]: AbstractControl } {
        return this.studentUpdated.controls;
    }

    matchFormsValue() {
        this.studentUpdated.patchValue({
            firstName: this.user.firstname,
            lastName: this.user.lastname,
            email: this.user.email,
            birthDate: this.user.birthdate,
            phoneNumber: this.user.phone,
            town: this.user.town,
            studentClass: this.user.studentClass,
        });

    }
    ngOnDestroy() {
        if (this.queryParamsSubscription) {
            this.queryParamsSubscription.unsubscribe();
        }
        if (this.getStudentByIdSubscription) {
            this.getStudentByIdSubscription.unsubscribe();
        }
    }
    onChange = ($event: Event) => {
        const target = $event.target as HTMLInputElement;
        const file: File = (target.files as FileList)[0]
        this.image = file;
    }

    modify() {
        this.touched =  true ;
        this.studentUpdated.enable();
        this.studentUpdated.get('email')?.disable()
    }

    cancel() {
        this.studentUpdated.disable();
        this.touched =  false ;
    }
}
