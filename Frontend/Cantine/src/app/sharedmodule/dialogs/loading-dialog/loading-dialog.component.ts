import { Component } from '@angular/core';

@Component({
  selector: 'app-loading-dialog',
  template: `
    <div class="loading-dialog">
      <div class="d-flex justify-content-center">
            <div class="spinner-container">
              <mat-progress-spinner mode="indeterminate"></mat-progress-spinner>
            </div>
          </div>
    </div>
      <style>
        .loading-dialog {
          position: fixed;
          top: 0;
          left: 0;
          z-index: 1000;
          width: 100%;
          height: 100%;
          background-color: rgba(0, 0, 0, 0.5);
          display: flex;
          justify-content: center;
          align-items: center;
        }
      </style>
  `,
  styles: [
  ]
})
export class LoadingDialogComponent {

}
