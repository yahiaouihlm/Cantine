import {HttpStatusCode} from "@angular/common/http";
import {ExceptionDialogComponent} from "../dialogs/exception-dialog/exception-dialog.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";


export class  DialogErrors  {


    constructor(private matDialog: MatDialog) {}
    public openDialog(message: string , httpError :  HttpStatusCode ): MatDialogRef<ExceptionDialogComponent, any> {
        return this.matDialog.open(ExceptionDialogComponent, {
            data: {message: message},
            width: '40%',
        });
    }



}