import { Component } from '@angular/core';
import { ModalComponent } from "../../utils/modal/modal.component";
import { MdbFileUploadModule } from "mdb-angular-file-upload";
import { MdbModalRef } from "mdb-angular-ui-kit/modal";
import { TranslateModule } from "@ngx-translate/core";
import { DocumentService } from "../../../services/document.service";
import { finalize } from "rxjs";
import { DocumentModel } from "../../../models/document.model";
import { CustomNotificationService } from "../../../services/custom-notification.service";
import {FormInputComponent} from "../../utils/form-input/form-input.component";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {TextAreaInputComponent} from "../../utils/text-area-input/text-area-input.component";

@Component({
  selector: 'app-document-upload-modal',
  standalone: true,
  imports: [
    ModalComponent,
    MdbFileUploadModule,
    TranslateModule,
    FormInputComponent,
    ReactiveFormsModule,
    TextAreaInputComponent
  ],
  templateUrl: './document-upload-modal.component.html',
  styleUrl: './document-upload-modal.component.scss'
})
export class DocumentUploadModalComponent {
  loading: boolean = false;
  file: File | null = null;
  fileDataBase64: string | null = null; // Store the base64 encoded file data
  formGroup: FormGroup

  constructor(
    private modalRef: MdbModalRef<ModalComponent>,
    private notificationService: CustomNotificationService,
    private documentService: DocumentService,
    private formBuilder: FormBuilder
  ) {
    this.formGroup = this.formBuilder.group({
      title: [null, Validators.required],
      description: [null, Validators.required],
    })
  }

  onFileUploaded(event: any) {
    const file = event[0] as File;

    if (file) {
      this.file = file;

      // Convert file to base64 string
      const reader = new FileReader();
      reader.onload = () => {
        this.fileDataBase64 = reader.result ? reader.result.toString().split(",")[1] : null;
      };
      reader.readAsDataURL(this.file);
    } else {
      this.file = null;
      this.fileDataBase64 = null;
    }
  }

  submit() {
    this.formGroup.markAllAsTouched()
    if (!this.file || !this.fileDataBase64 || this.formGroup.invalid) return

    this.loading = true;

    const resource = {
      title: this.formGroup.get('title')?.value,           // replace with actual title
      description: this.formGroup.get('description')?.value, // replace with actual description
      type: "pdf",                    // replace with actual type if dynamic
      size: this.file.size,
      uploadDate: new Date().toISOString().split("T")[0],  // Format to "YYYY-MM-DD",
      fileData: this.fileDataBase64 // Send the base64-encoded file data
    };

    this.documentService.create(resource)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (document: DocumentModel) => {
          this.modalRef.close(document);
        },
        error: (e) => {
          this.notificationService.error("Fehler beim Upload");
          console.error(e);
        },
      });
  }
}
