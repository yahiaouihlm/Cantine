import {Meal} from "./meal";
import {Menu} from "./menu";

export  class  Order {

    studentId!: number;

    mealsId: number[] = [];

    menusId:  number[]  = [];






    public  static addMealToOrder(idMeal : number) {
        let  order : Order = new Order();
        console.log(idMeal)
        order.mealsId.push(idMeal);
        let bascket   = localStorage.getItem('Order');
        if  (!bascket) {
            localStorage.setItem('Order' ,   JSON.stringify( order) )  ;
            return;
        }
        else  {
            let newBasket :  Order =  JSON.parse(bascket);
            newBasket.mealsId.push(idMeal);
        }


    }
}