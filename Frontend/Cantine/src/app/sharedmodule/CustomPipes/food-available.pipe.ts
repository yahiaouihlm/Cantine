import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'foodAvailable'
})
export class FoodAvailablePipe implements PipeTransform {

  transform(type: string): string {
    if(type === "Unavailable"){
      return "mealCardUnavailable"
    }
    return "mealCardAvailable";
  }

}
