import {Injectable} from '@angular/core';
import {DocumentModel} from "../models/document.model";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {BreadcrumbService} from "./breadcrumb.service";
import {ResourceService} from "./resource.service";
import {CustomNotificationService} from "./custom-notification.service";
import {environment} from "../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DocumentService extends ResourceService<DocumentModel> {
  constructor(http: HttpClient, breadcrumbService: BreadcrumbService, notificationService: CustomNotificationService) {
    super(http, breadcrumbService, '/documents', notificationService);
  }

  downloadDocument(documentId: number): Observable<any> {
    const url = `${environment.apiUrl}${this.resourceUrl}/${documentId}/download`; // Construct the download URL
    const headers = new HttpHeaders({
      'Accept': 'application/octet-stream', // Tell the server we expect binary data
    });

    return this.http.get(url, {headers, observe: 'response', responseType: 'blob'});
  }
}
