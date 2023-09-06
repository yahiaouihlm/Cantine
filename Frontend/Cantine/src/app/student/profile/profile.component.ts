import {Component, OnDestroy, OnInit} from '@angular/core';
import {AbstractControl, FormControl, FormGroup, Validators} from "@angular/forms";
import Validation from "../../sharedmodule/functions/validation";
import {ActivatedRoute} from "@angular/router";
import {User} from "../../sharedmodule/models/user";
import {SharedService} from "../../sharedmodule/shared.service";
import {Subscription} from "rxjs";

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    styles: []
})
export class ProfileComponent implements OnInit, OnDestroy {
    constructor(private route: ActivatedRoute, private sharedService: SharedService) {
    }

    user: User = new User();
    submitted!: boolean;
    private queryParamsSubscription: Subscription | undefined;
    private getStudentByIdSubscription: Subscription | undefined;
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
        this.queryParamsSubscription = this.route.queryParams.subscribe(params => {
            const id = params['id'];
            if (id) {
                this.getStudentByIdSubscription =this.sharedService.getStudentById(id).subscribe((response) => {
                    this.user = response;
                    this.matchFormsValue();
                });


            }

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

    get f(): { [key: string]: AbstractControl } {
        return this.studentUpdated.controls;
    }

    matchFormsValue() {
        this.studentUpdated.patchValue({
            firstName: this.user.firstname,
            lastName: this.user.lastname,
            email: this.user.email,
            birthDate: this.user.birthDate,
            phoneNumber: this.user.phoneNumber,
            town: this.user.town,
            studentClass: this.user.studentClass,
        });

    }

}
