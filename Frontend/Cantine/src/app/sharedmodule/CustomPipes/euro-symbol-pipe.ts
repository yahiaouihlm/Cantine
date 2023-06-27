import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'euroSymbol'
})

export class EuroSymbolPipe implements PipeTransform {

  transform(value: number|undefined): string {
    return `${value} €`

  }
}
