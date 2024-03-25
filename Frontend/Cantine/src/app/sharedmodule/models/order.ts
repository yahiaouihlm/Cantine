import {Meal} from "./meal";
import {Menu} from "./menu";
import {User} from "./user";

export class Order {


    uuid!: string;
    studentId!: string;

    meals: Meal[] = [];

    menus: Menu[] = [];
    price!: number;
    creationDate!: Date;
    creationTime!: Date;
    isCancelled!: boolean;
    status!: number;
    mealsId: string[] = [];
    menusId: string[] = [];
    studentOrder!: User;
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
        let basket = localStorage.getItem('Order');
        if (!basket) {
            localStorage.setItem('Order', JSON.stringify(order));
            return;
        } else {
            let newBasket: Order = JSON.parse(basket);
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
        let basket = localStorage.getItem('Order');
        if (!basket) {
            localStorage.setItem('Order', JSON.stringify(order));
            return;
        } else {
            let newBasket: Order = JSON.parse(basket);
            if (newBasket.menus.length >= 10) {
                alert("you can't add more than 10 menus");
                return;
            }
            newBasket.menus.push(menu);
            localStorage.setItem('Order', JSON.stringify(newBasket));
        }
    }

    public static removeMealFromOrder(meal: Meal): Order {
        if (!meal) {
            return new Order();
        }
        let order = Order.getOrderFromLocalStorage();
        if (order) {
            let index = -1;
            for (let counter = 0; counter < order.meals.length; counter++)
                if (order.meals[counter].uuid === meal.uuid) {
                    index = counter;
                    break;
                }
            if (index != -1) {

                order.meals.splice(index, 1);
                localStorage.setItem('Order', JSON.stringify(order))
                return order;
            }
        }

        return new Order();


    }

    public static removeMenuFromOrder(menu: Menu): Order {
        if (!menu) {
            return new Order();
        }
        let order = Order.getOrderFromLocalStorage();
        if (order) {
            let index = -1;

            for (let counter = 0; counter < order.menus.length; counter++)
                if (order.menus[counter].uuid === menu.uuid) {
                    index = counter;
                    break;
                }
            if (index != -1) {
                order.menus.splice(index, 1);
                localStorage.setItem('Order', JSON.stringify(order))
                return order;
            }

        }

        return new Order();


    }


    public static clearOrder() {
        let  order =  localStorage.getItem('Order');
        if (order) {
            localStorage.removeItem('Order');
        }
    }


    getMealsIds(): string[] {
        return this.meals.map(meal => meal.uuid);
    }

    public getMenusIds(): string[] {
        return this.menus.map(menu => menu.uuid);
    }
}