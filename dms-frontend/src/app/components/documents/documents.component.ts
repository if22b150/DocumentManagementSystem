import { Component } from '@angular/core';
import {MdbButtonComponent} from "../utils/mdb-button/mdb-button.component";
import {TranslateModule} from "@ngx-translate/core";
import {MdbModalService} from "mdb-angular-ui-kit/modal";
import {DocumentUploadModalComponent} from "./document-upload-modal/document-upload-modal.component";
import {CustomNotificationService} from "../../services/custom-notification.service";
import {DocumentsTableComponent} from "./documents-table/documents-table.component";

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [
    MdbButtonComponent,
    TranslateModule,
    DocumentsTableComponent
  ],
  templateUrl: './documents.component.html',
  styleUrl: './documents.component.scss'
})
export class DocumentsComponent {
  constructor(private modalService: MdbModalService,
              private notificationService: CustomNotificationService) {
  }

  openImportModal() {
    let modalRef = this.modalService.open(DocumentUploadModalComponent)
    modalRef.onClose.subscribe((success) => {
      if(success)
        this.notificationService.success("UPLOAD_SUCCESS")

      // if(!value?.id)
      //   return;
      //
      // this.machines?.push(value)
      // this.customerService.getAll();
      // this.customNotificationService.notify("Machine was created.")
    })
  }
}
