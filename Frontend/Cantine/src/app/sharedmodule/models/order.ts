import {Meal} from "./meal";
import {Menu} from "./menu";

export class Order {

    studentId!: number;

    meals: Meal[] = [];

    menus: Menu[] = [];


    public static getOrderFromLocalStorage() {
        let order = localStorage.getItem('Order');
        if (!order) {
            return null;
        } else {
            return JSON.parse(order) as Order;
        }
    }

    public static addMealToOrder(meal: Meal) {
        if (!meal) {
            return
        }
        let order: Order = new Order();
        order.meals.push(meal);
        let bascket = localStorage.getItem('Order');
        if (!bascket) {
            localStorage.setItem('Order', JSON.stringify(order));
            return;
        } else {
            let newBasket: Order = JSON.parse(bascket);
            if (newBasket.meals.length >= 10) {
                alert("you can't add more than 10 meals");
                return;
            }
            newBasket.meals.push(meal);
            localStorage.setItem('Order', JSON.stringify(newBasket));
        }


    }

    public static addMenuToOrder(menu: Menu) {
        if (!menu) {
            return
        }
        let order: Order = new Order();
        order.menus.push(menu);
        let bascket = localStorage.getItem('Order');
        if (!bascket) {
            localStorage.setItem('Order', JSON.stringify(order));
            return;
        } else {
            let newBasket: Order = JSON.parse(bascket);
            if (newBasket.menus.length >= 10) {
                alert("you can't add more than 10 menus");
                return;
            }
            newBasket.menus.push(menu);
            localStorage.setItem('Order', JSON.stringify(newBasket));
        }
    }

    public static removeMealFromOrder(meal: Meal) :  Order {
        if (!meal) {
            return  new Order();
        }
        let order = Order.getOrderFromLocalStorage();
        if (order) {
            let index = -1;
            for( let counter = 0 ; counter < order.meals.length;  counter++ )
                if  (order.meals[counter].id === meal.id ){
                    index =  counter;
                    break;
                }
            if  (index != -1 ){
                order.meals.splice(index, 1);
                localStorage.setItem('Order', JSON.stringify (order) )
                return order;
            }
        }

        return new Order();


    }

    public static removeMenuFromOrder(menu: Menu) :  Order {
        if (!menu) {
            return  new Order();
        }
        let order = Order.getOrderFromLocalStorage();
        if (order) {
            let index = -1;

            for( let counter = 0 ; counter < order.menus.length;  counter++ )
                if  (order.menus[counter].id === menu.id ){
                    index =  counter;
                    break;
                }
            if  (index != -1 ){
                order.menus.splice(index, 1);
                localStorage.setItem('Order', JSON.stringify (order) )
                return order;
            }

        }

        return new Order();


    }



    public isEmpty() : boolean {
        if  (this.meals.length == 0 && this.menus.length == 0  && !this.studentId) {
            return true;
        }

        return false;
    }


}