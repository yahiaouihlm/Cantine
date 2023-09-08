import {Meal} from "./meal";
import {Menu} from "./menu";

export  class  Order {

    studentId!: number;

    meals: Meal[] = [];

    menus:  Menu[]  = [];




    public  static  getOrderFromLocalStorage()   :     Order  {
         let order   =   localStorage.getItem('Order') ;
         if  (!order) {
             return new Order();
         }
            else  {
                return  JSON.parse(order) as Order;
         }
    }
    public  static addMealToOrder(meal: Meal) {
        let  order : Order = new Order();
        order.meals.push(meal);
        let bascket   = localStorage.getItem('Order');
        if  (!bascket) {
            localStorage.setItem('Order' ,   JSON.stringify( order) )  ;
            return;
        }
        else  {
            let newBasket :  Order =  JSON.parse(bascket);
            if  (newBasket.meals.length >=10) {
                alert("you can't add more than 10 meals");
                return;
            }
            newBasket.meals.push(meal);
            localStorage.setItem('Order' ,   JSON.stringify( newBasket) )  ;
        }


    }


    public  static addMenuToOrder(menu : Menu) {
        let  order : Order = new Order();
        order.menus.push(menu);
        let bascket   = localStorage.getItem('Order');
        if  (!bascket) {
            localStorage.setItem('Order' ,   JSON.stringify( order) )  ;
            return;
        }
        else  {
            let newBasket :  Order =  JSON.parse(bascket);
            if  (newBasket.menus.length >=10) {
                alert("you can't add more than 10 menus");
                return;
            }
            newBasket.menus.push(menu);
        }
    }

}