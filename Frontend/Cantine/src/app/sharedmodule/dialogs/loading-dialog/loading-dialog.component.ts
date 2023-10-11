import { Component } from '@angular/core';

@Component({
  selector: 'app-loading-dialog',
  template: `
      <div class="d-flex justify-content-center">
            <div class="spinner-container">
              <mat-progress-spinner mode="indeterminate"></mat-progress-spinner>
            </div>
          </div>
  `,
  styles: [
  ]
})
export class LoadingDialogComponent {

}
