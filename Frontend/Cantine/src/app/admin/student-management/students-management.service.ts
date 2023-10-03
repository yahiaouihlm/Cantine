import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams} from "@angular/common/http";
import Malfunctions from "../../sharedmodule/functions/malfunctions";
import {User} from "../../sharedmodule/models/user";

@Injectable()
export class StudentsManagementService {

  private  BASIC_ADMIN_URL =  "http://localhost:8080/cantine/admin/adminDashboard/works";
  private  GET_STUDENTS =  this.BASIC_ADMIN_URL + "/GetStudents"
  constructor(private   httpClient:HttpClient) { }



  getStudents( user  :  User ) {
    let token  =  Malfunctions.getTokenFromLocalStorage();
    const headers = new HttpHeaders().set('Authorization', token);
    const params =  new  HttpParams().set('firstname' , user.firstname)
                                     .set('lastname', user.lastname)
                                     .set('birthdateAsString', user.birthdate.toString())
    return this.httpClient.get<User[]>(this.GET_STUDENTS , { params : params , headers : headers });
  }
}
