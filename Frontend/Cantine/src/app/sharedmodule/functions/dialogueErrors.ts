import {HttpStatusCode} from "@angular/common/http";
import {ExceptionDialogComponent} from "../dialogs/exception-dialog/exception-dialog.component";
import {MatDialog} from "@angular/material/dialog";


export class  DialogErrors  {


    constructor(private matDialog: MatDialog) {}
    public openDialog(message: string , httpError :  HttpStatusCode ): void {
        const result = this.matDialog.open(ExceptionDialogComponent, {
            data: {message: message},
            width: '40%',
        });

        result.afterClosed().subscribe((confirmed: boolean) => {
            if (httpError == HttpStatusCode.BadRequest || httpError == HttpStatusCode.NotAcceptable || httpError== HttpStatusCode.Conflict || httpError== HttpStatusCode.NotFound){
                //  this.router.navigate(['/admin/menus'] , { queryParams: { reload: 'true' } });
            }
            else {
                console.log("je suis  la ")
                /* TODO  remove THE  Token  */
                //this.router.navigate(['/cantine/home'] , { queryParams: { reload: 'true' } });
            }

        });

    }
}