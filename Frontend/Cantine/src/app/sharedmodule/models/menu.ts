import {Meal} from "./meal";

export class  Menu {

    id! : number;
    label! : string ;

    description!:string;

    createdDate!: Date;

    price!: number;

    quantity! : number;

    status! : number;

    image!: string;

    meals: Meal [] = []

}