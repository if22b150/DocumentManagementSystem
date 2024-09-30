import { FormBuilder } from '@angular/forms';
import { computed, signal, Signal } from '@angular/core';
import { TableColumnModel } from '../models/table-column.model';
import { finalize } from 'rxjs';
import {AModel} from "../models/a-model.model";
import {ResourceService} from "../services/resource.service";
import {CustomNotificationService} from "../services/custom-notification.service";

export abstract class AResourceTableComponent<M extends AModel> {
  models: Signal<M[]>
  deleteLoadingIds = signal<number[]>([])
  urlParams?: string

  abstract tableColumns: TableColumnModel[];

  protected constructor(
    protected formBuilder: FormBuilder,
    protected resourceService: ResourceService<M>,
    protected notificationService: CustomNotificationService,
    protected resourceTranslation: string,
  ) {
    this.models = computed(() => this.resourceService.models())
    this.resourceService.getAll(undefined)
  }

  delete(id: number) {
    // add id to loading ids array
    this.deleteLoadingIds.update((ids) => {
      return [...ids, +id]
    });

    this.resourceService
      .delete(id)
      .pipe(
        finalize(() => {
          // remove id from loading ids array
          this.deleteLoadingIds.update((ids) => {
            return ids.filter((i) => i != id);
          })
        }),
      )
      .subscribe({
        next: () => {
          this.resourceService.getAll({
            urlParams: this.urlParams,
            lazyLoad: true,
          })
          this.notificationService.success(`${this.resourceTranslation}.SUCCESSFULLY_DELETED`)
        },
        error: (e) => {
          console.error(e)
          this.notificationService.error('ERROR_OCCURRED')
        },
      });
  }
}
