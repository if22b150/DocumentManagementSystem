import {Component} from '@angular/core';
import {TableComponent} from "../../utils/table/table.component";
import {DocumentModel} from "../../../models/document.model";
import {TableColumnModel} from "../../../models/table-column.model";
import {FormBuilder, FormGroup} from "@angular/forms";
import {CustomNotificationService} from "../../../services/custom-notification.service";
import {DocumentService} from "../../../services/document.service";
import {AResourceTableComponent} from "../../AResourceTableComponent";

@Component({
  selector: 'app-documents-table',
  standalone: true,
  imports: [
    TableComponent
  ],
  template: `
    <app-table
      [columns]="tableColumns"
      [data]="models()"
      [filterFormGroup]="filterFormGroup"
      [loading]="resourceService.loading()"
      (doFilterRequest)="resourceService.getAll($event)"
      (urlParamsChange)="this.urlParams = $event"
      [hasActiveFilters]="resourceService.apiResponse().filters != undefined"
      [deleteLoadingIds]="this.deleteLoadingIds()"
    ></app-table>
  `,
  styles: ''
})
export class DocumentsTableComponent extends AResourceTableComponent<DocumentModel> {
  filterFormGroup: FormGroup = this.formBuilder.group({
    content: [null]
  });

  tableColumns: TableColumnModel[] = [
    {
      name: 'TITLE',
      selector: [{selector: 'title'}],
      filterFormControl: this.filterFormGroup.get('content'),
      filterFormControlLabel: 'CONTENT',
      filterUrlParam: 'content',
    },
    {
      name: 'DESCRIPTION',
      selector: [
        {selector: 'description'}
      ]
    },
    {
      buttons: [
        // {
        //   iconClass: 'fas fa-pencil-alt fa-lg', // Update icon
        //   floating: true,
        //   outlined: true,
        //   color: 'primary',
        //   onClick: ($event) => this.update($event) // Call the update method
        // },
        {
          iconClass: 'fas fa-download fa-lg', // Update icon
          floating: true,
          outlined: true,
          onClick: ($event) => this.download($event) // Call the update method
        },
        // {
        //   iconClass: 'fas fa-external-link-alt fa-lg',
        //   floating: true,
        //   outlined: true,
        //   isObjectLink: true
        // },
        {
          iconClass: 'fas fa-trash fa-lg',
          floating: true,
          outlined: true,
          color: 'danger',
          isDelete: true,
          onClick: ($event) => this.delete($event)
        },
      ],
    },
  ];

  constructor(
    formBuilder: FormBuilder,
    public documentService: DocumentService,
    notificationService: CustomNotificationService
  ) {
    super(formBuilder, documentService, notificationService, 'DOCUMENTS')
  }

  update(event: number): void {
    // Implement the update logic here.
    // For example, navigate to an update form or modal.
    console.log('Update document:', event);
    // Implement your logic to handle the update here.
    // You might want to open a modal or navigate to an update page.
  }

  download(id: number): void {
    this.documentService.downloadDocument(id).subscribe({
      next: (response) => {
        const blob = response.body as Blob;

        // Extract the filename from the Content-Disposition header
        const contentDisposition = response.headers.get('Content-Disposition');
        let filename = `document-${id}.pdf`; // Fallback filename
        if (contentDisposition) {
          const filenameMatch = contentDisposition.match(/filename="([^"]+)"/);
          if (filenameMatch && filenameMatch[1]) {
            filename = filenameMatch[1];
          }
        }

        // Create a link element and trigger the download
        const downloadUrl = URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = downloadUrl;
        anchor.download = filename; // Use the filename from the header
        anchor.click();
        URL.revokeObjectURL(downloadUrl); // Clean up the URL object
      },
      error: (error) => {
        this.notificationService.error('Failed to download the document.');
        console.error('Download error:', error);
      },
    });
  }
}
