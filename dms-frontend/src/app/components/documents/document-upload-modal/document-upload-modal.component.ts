import { Component } from '@angular/core';
import {ModalComponent} from "../../utils/modal/modal.component";
import {MdbFileUploadModule} from "mdb-angular-file-upload";
import {MdbModalRef} from "mdb-angular-ui-kit/modal";
import {TranslateModule} from "@ngx-translate/core";

@Component({
  selector: 'app-document-upload-modal',
  standalone: true,
  imports: [
    ModalComponent,
    MdbFileUploadModule,
    TranslateModule
  ],
  templateUrl: './document-upload-modal.component.html',
  styleUrl: './document-upload-modal.component.scss'
})
export class DocumentUploadModalComponent {
  loading: boolean = false
  file: File[] | null = null

  constructor(private modalRef: MdbModalRef<ModalComponent>) {}

  submit() {
    if(!this.file)
      return

    this.loading = true

    setTimeout(() => {
      this.modalRef.close(true)
    }, 2000)
  }
}
