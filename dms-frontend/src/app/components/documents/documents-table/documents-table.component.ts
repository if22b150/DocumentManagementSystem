import {Component, computed, signal, Signal} from '@angular/core';
import {TableComponent} from "../../utils/table/table.component";
import {DocumentModel} from "../../../models/document.model";
import {TableColumnModel} from "../../../models/table-column.model";
import {FormBuilder, FormGroup} from "@angular/forms";
import {CustomNotificationService} from "../../../services/custom-notification.service";
import {DocumentService} from "../../../services/document.service";
import {finalize} from "rxjs";
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
    title: [null]
  });

  tableColumns: TableColumnModel[] = [
    {
      name: 'TITLE',
      selector: [{selector: 'title'}],
      filterFormControl: this.filterFormGroup.get('title'),
      filterFormControlLabel: 'TITLE',
      filterUrlParam: 'title',
    },
    {
      name: 'URL',
      selector: [
        {selector: 'url'}
      ]
    },
    {
      buttons: [
        {
          iconClass: 'fas fa-external-link-alt fa-lg',
          floating: true,
          outlined: true,
          isObjectLink: true
        },
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
    super(formBuilder, documentService, notificationService, '')
  }
}
