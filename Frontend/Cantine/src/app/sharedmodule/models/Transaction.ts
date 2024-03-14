import {User} from "./user";
import {Time} from "@angular/common";

export class  Transaction  {
    admin!: User;
    student!: User;
    amount!: number;
    paymentDate!: Date;

    paymentTime!: Time;

}