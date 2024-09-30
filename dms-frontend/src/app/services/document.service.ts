import {Injectable} from '@angular/core';
import {DocumentModel} from "../models/document.model";
import {HttpClient} from "@angular/common/http";
import {BreadcrumbService} from "./breadcrumb.service";
import {ResourceService} from "./resource.service";
import {CustomNotificationService} from "./custom-notification.service";

@Injectable({
  providedIn: 'root'
})
export class DocumentService extends ResourceService<DocumentModel> {
  constructor(http: HttpClient, breadcrumbService: BreadcrumbService, notificationService: CustomNotificationService) {
    super(http, breadcrumbService, '/documents', notificationService);
  }
}
